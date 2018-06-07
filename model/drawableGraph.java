package model;

import model.HelperTypes.ProtoNode;

import java.util.*;

//@formatter:off
public class drawableGraph {
    private LinkedHashSet<GraphNode>        nodeSet            = new LinkedHashSet<>();
    private LinkedHashSet<Edge>             edgeSet            = new LinkedHashSet<>();
    private LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn  = new LinkedHashMap<>();

    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeOut(){
        return nodeToEdgesOut;
    }
    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeIn(){
        return nodeToEdgesIn;
    }

    public drawableGraph(GraphMLGraph graph, String edgeType) throws Exception { //sollte einziger Konstruktor bleiben
        Set<Edge> relevantEdges = new LinkedHashSet<>();
        Set<ProtoNode> relevantNodes = new LinkedHashSet<>();
        //relavante Nodes Erzeugen und Adjazenzinformationen aus Edges extrahieren.
        if (!graph.getEdgeTypes().contains(new HelperTypes.EdgeType(edgeType))) {
            throw new Exception("Chosen EdgeType: " + edgeType + " not in Graphs EdgeTypeList");
        }

        relevantEdges.addAll(graph.getEdgesOfType(edgeType));


        for (Edge e : relevantEdges) {
            relevantNodes.add((ProtoNode) e.start);
            relevantNodes.add ((ProtoNode) e.target);
        }

        Map<String, GraphNode> nodesMap = new HashMap<>();
        for (ProtoNode relevantNode : relevantNodes) {
            GraphNode newGraphNode = relevantNode.toGraphNode();
            nodeSet.add(newGraphNode);
            nodesMap.put(newGraphNode.label, newGraphNode);
        }
        for (Edge relevantEdge : relevantEdges) {
            ProtoNode startObject =(ProtoNode) relevantEdge.start;
            String startString = startObject.getLabel();

            ProtoNode targetObject = (ProtoNode) relevantEdge.target;
            String targetString = targetObject.getLabel();

            GraphNode start = nodesMap.get(startString);
            GraphNode target = nodesMap.get(targetString);
            start.addChild(target);
            target.addParent(start);

            Edge edgeToAdd = new Edge(start, target);
            edgeSet.add(edgeToAdd);

        } //alle Knoten initialisiert und Adjazenzinformationen in den Knoten;
    }
    
    public LinkedHashSet<GraphNode> copyNodeSet(){
        return new LinkedHashSet<>(nodeSet); //returns a copy of the nodeSet
    }
    public LinkedHashSet<Edge>      copyEdgeSet(){
        return new LinkedHashSet<>(edgeSet); //returns a copy of the edgeSet
    }
    LinkedHashSet<Edge>             getEdgeSet() {
        return edgeSet;
    }
    LinkedHashSet<GraphNode>        getNodeSet() {
        return nodeSet;
    }
    public GraphNode                getNode(String id) {
        for (GraphNode node : nodeSet) {
            if (node.label.equals(id)) return node;
        }
        return null;
    }
    public LinkedList<GraphNode>    getRoots(){
        LinkedList<GraphNode> returnlist = new LinkedList<>();
        for (GraphNode graphNode : nodeSet) {
            if (graphNode.isRoot()) {
                returnlist.add(graphNode);
            }
        }
        return returnlist;
    }
    public LinkedList<GraphNode>    getLeaves(){
        LinkedList<GraphNode> returnlist = new LinkedList<>();
        for (GraphNode graphNode : nodeSet) {
            if (graphNode.isLeaf()) {
                returnlist.add(graphNode);
            }
        }
        return returnlist;
    }



//    added by dustyn
    private drawableGraph(LinkedHashSet<GraphNode> nodeSet, LinkedHashSet<Edge> edgeSet, LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn, LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut  ){
        this.edgeSet = new LinkedHashSet<>(edgeSet);
        this.nodeSet = new LinkedHashSet<>(nodeSet);
        this.nodeToEdgesIn = new LinkedHashMap<>(nodeToEdgesIn);
        this.nodeToEdgesOut = new LinkedHashMap<>(nodeToEdgesOut);
}
    drawableGraph           copy(drawableGraph graph) {
        LinkedHashSet<GraphNode>    copyNodes   = new LinkedHashSet<>();
        LinkedHashSet<Edge>         copyEdges   = new LinkedHashSet<>(graph.copyEdgeSet());

        LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn = new LinkedHashMap<>();
        LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();

        for (Edge e : copyEdges) {
            copyNodes.add( (GraphNode) e.start);
            copyNodes.add( (GraphNode) e.target);
        }


        // set Children/Parent structure
        for (Edge edge : copyEdges) {
            GraphNode u = (GraphNode) edge.start;
            GraphNode v = (GraphNode) edge.target;
            u.addChild(v);
            v.addParent(u);
        }

//        generate HashMaps
        for (Edge e: edgeSet) {
            GraphNode u = (GraphNode) e.start;
            GraphNode v = (GraphNode) e.target;

            LinkedList<Edge> edgeSetIn = new LinkedList<>();
            if (nodeToEdgesIn.containsKey(v)){
                edgeSetIn.addAll(nodeToEdgesIn.get(v));}
            edgeSetIn.add(e);
            nodeToEdgesIn.put(v, edgeSetIn);

            LinkedList<Edge> edgeSetOut = new LinkedList<>();
            if (nodeToEdgesOut.containsKey(u)){
                edgeSetOut.addAll(nodeToEdgesOut.get(u));}
            edgeSetOut.add(e);
            nodeToEdgesOut.put(u, edgeSetOut);
        }

        return new drawableGraph(copyNodes,copyEdges, nodeToEdgesIn,nodeToEdgesOut);
    }

    LinkedList<GraphNode>   getIsolatedNodes(){
    LinkedList<GraphNode> isolatedNodes = new LinkedList<>();
        for (GraphNode v: nodeSet) {
            if (getIndegree(v) == 0 && getOutdegree(v) == 0){
                isolatedNodes.add(v);
            }
        }
        return isolatedNodes;
    }

    void                    deleteIsolatedNodes(){
        LinkedList<GraphNode> n = getIsolatedNodes();
        System.out.printf("Found %d isolated nodes: %s\n",n.size(), n.toString());
        for (GraphNode graphNode : n) {
            justRemoveNode(graphNode);
        }
    }

    GraphNode               getSink () {
            for (GraphNode node : this.nodeSet)
                if (getOutdegree(node) == 0 && getIndegree(node) > 0) return node;
            return null;}
    GraphNode               getSource () {
            for (GraphNode node : this.nodeSet)
                if (getIndegree(node) == 0) return node;
            return null;}

    void        justRemoveNode  (GraphNode removeMe){
        /* will only remove this node, and children/parents references, nothin else */
        removeMe.getChildren().forEach(child -> child.removeParent(removeMe));
        removeMe.getParents().forEach(parent -> parent.removeChild(removeMe));
        this.nodeSet.remove(removeMe); }
    private int getOutdegree    (GraphNode node) {
        int size = 0;
        if (nodeToEdgesOut.containsKey(node))
        {
            size = nodeToEdgesOut.get(node).size();
        }
        if (size!=node.outdegree()) {
            System.out.printf("Warning! Outdegree of node.parents %d is not equal to the number of outgoing edges %d!\n",size,node.outdegree());
        }
        return size;
    }
    private int getIndegree     (GraphNode node) {
        int size = 0;
        if (nodeToEdgesIn.containsKey(node))
        {
            size = nodeToEdgesIn.get(node).size();
        }
        if (size!=node.indegree()) {
            System.out.printf("Warning! Indegree of node.parents %d is not equal to the number of incoming edges %d!\n",size,node.indegree());
        }
        return size;
    }


//    void removeOutgoingEdges(GraphNode node) { }
//@formatter:on


    void removeIngoingEdges(GraphNode node) {
        edgeSet.removeAll(nodeToEdgesIn.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesIn.get(node));

        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode startNode = (GraphNode) edgeToBeRemoved.start;
            LinkedList<Edge> modifiedListOut = nodeToEdgesOut.get(startNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error to remove edge in modifiedList");
            nodeToEdgesOut.put(startNode, modifiedListOut);
        }
        nodeToEdgesIn.keySet().removeIf(entry -> entry == node);
    }
    void removeOutgoingEdges(GraphNode node) {
        edgeSet.removeAll(nodeToEdgesOut.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesOut.get(node));
        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode targetNode = (GraphNode) edgeToBeRemoved.target;
            LinkedList<Edge> modifiedListOut = nodeToEdgesIn.get(targetNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error");
            nodeToEdgesIn.put(targetNode, modifiedListOut);
        }
        nodeToEdgesOut.keySet().removeIf(entry -> entry == node);
    }

    GraphNode getNodeWithMaxDiffDegree() {
        int max = Integer.MIN_VALUE;
        GraphNode winnerNode = null;
        for (GraphNode node : nodeSet) {
            if (getOutdegree(node) - getIndegree(node) > max) {
                max = getOutdegree(node) - getIndegree(node);
                winnerNode = node;
            }
        }
//        System.out.println("max = " + max);
        return winnerNode;
    }
    public GraphNode selectRandomNode() {
        int size = nodeSet.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (GraphNode obj : nodeSet) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }
    LinkedList<GraphNode> getAllSinks() {
        LinkedList<GraphNode> allSinks = new LinkedList<>();
        for (GraphNode node : this.nodeSet) {
            if (getOutdegree(node) == 0 && getIndegree(node) >= 1) {
                allSinks.add(node);
            }
        }
        return allSinks;
    }
    boolean reverseEdge(Edge edge) {
        GraphNode u = (GraphNode) edge.start;
        GraphNode v = (GraphNode) edge.target;

        boolean status1 = u.removeChild(v);
        boolean status2 = u.addParent(v);
        boolean status3 = v.removeParent(u);
        boolean status4 = v.addChild(v);

        return status1 && status2 && status3 && status4;
    }


    void addDummies(){
        LinkedList<Edge> edgesToDelete = new LinkedList<>();
        for (Edge edge : edgeSet) {
            GraphNode start = (GraphNode) edge.start;
            GraphNode target = (GraphNode) edge.target;
            int spanningLevels = Math.abs(start.getLayer() - target.getLayer());
//            spanningLevels = Math.abs(spanningLevels * spanningLevels -1); // keine ahnung wie man sonst das vorzeichen entfernt
            if (spanningLevels > 1) { // precondition for adding dummies
                edgesToDelete.add(edge);
//                if (!deleteEdge(edge)) System.out.println("couldnt delete edge in addDummies");
                List<GraphNode> block = new LinkedList<>();
                block.add(start);
                for (int i = spanningLevels; i > 0; i--) { // for every additional spanned level
                    String label = "Dummy-" + i + "; from: " + start.label + " to: " + target.label+"|";
                    GraphNode node = new GraphNode(label, "Dummy", true); // create a dummy with a proper label
                    nodeSet.add(node); // add to NodeSet
                    block.add(node); // add to block to find parent/children
                }
                block.add(target);

                for (int i = 0; i < block.size(); i++) { // hoffe das wirft keinen outOfBounds
                    if (!block.get(i).equals(target)) block.get(i).addChild(block.get(i+1)); // except for at start Node, add children
                    if (!block.get(i).equals(start)) block.get(i).addParent(block.get(i-1)); // except for at target Node, add parents
                }
            }
        }
    }

    private boolean deleteEdge(Edge e){
        if (!edgeSet.contains(e)) return false;
        try {
            GraphNode start = (GraphNode) e.start;
            GraphNode target = (GraphNode) e.target;
            start.removeChild(target);
            target.removeParent(start);
            edgeSet.remove(e);
            //does not update the hashsets nodeToIn/OutgoingEdges
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return (nodeSet.size() + " Nodes " + "and " + edgeSet.size()  + " Edges" );
    }

}


//    LinkedHashSet<Edge> getOutgoingEdges(GraphNode node) {
//        LinkedHashSet<Edge> outgoingEdges = new LinkedHashSet<>();
//        for (GraphNode v : node.getParents()) {
//            for (Edge e: edgeSet){
//                if (e.start == v){
//                    outgoingEdges.add(e);
//                }
//            }
//        }
//        return outgoingEdges;
//    }

//    LinkedList<GraphNode>   getSources(){
//    LinkedList<GraphNode> sources = new LinkedList<>();
//        for (GraphNode node: nodeSet){
//            if (node.inDegree() == 0){
//                sources.add(node);
//            }
//        }
//        return sources;
//    }

//    LinkedList<GraphNode>   getSinks(){
//        LinkedList<GraphNode> sinks = new LinkedList<>();
//        for (GraphNode node: nodeSet){
//            if (node.outDegree() == 0){
//                sinks.add(node);
//            }
//        }
//        return sinks;
//    }

//    ArrayList<Edge> getIngoingEdges(GraphNode node) {
//        ArrayList<Edge> ingoingEdges = new ArrayList<>();
//        for (Edge e : edgeSet)  // ingoing, if node is target from edge
//            if (e.target == node) ingoingEdges.add(e);
//        return ingoingEdges;
//    }

//        boolean containsSink () {
//            for (GraphNode node : nodeSet) {
//                if (node.outDegree() == 0) return true;
//            }
//            return false;
//        }
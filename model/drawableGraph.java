package model;

import model.HelperTypes.ProtoNode;

import java.util.*;

//@formatter:off
public class drawableGraph {
    private LinkedHashSet<GraphNode>        nodeList            = new LinkedHashSet<>();
    private LinkedHashSet<Edge>             edgeList            = new LinkedHashSet<>();
    private LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn  = new LinkedHashMap<>();

    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToOutgoingEdges(){
        return nodeToEdgesOut;
    }
    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToIngoingEdges(){
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
            nodeList.add(newGraphNode);
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
            edgeList.add(edgeToAdd);

        } //alle Knoten initialisiert und Adjazenzinformationen in den Knoten;
    }
    
    public LinkedHashSet<GraphNode> getNodeSet(){
        return new LinkedHashSet<>(nodeList); //returns a copy of the nodeList
    }
    public LinkedHashSet<Edge> getEdgeSet(){
        return new LinkedHashSet<>(edgeList);
    }
    public GraphNode                getNode(String id) {
        for (GraphNode node : nodeList) {
            if (node.label.equals(id)) return node;
        }
        return null;
    }
    public LinkedList<GraphNode>    getRoots(){
        LinkedList<GraphNode> returnlist = new LinkedList<>();
        for (GraphNode graphNode : nodeList) {
            if (graphNode.isRoot()) {
                returnlist.add(graphNode);
            }
        }
        return returnlist;
    }
    public LinkedList<GraphNode>    getLeaves(){
        LinkedList<GraphNode> returnlist = new LinkedList<>();
        for (GraphNode graphNode : nodeList) {
            if (graphNode.isLeaf()) {
                returnlist.add(graphNode);
            }
        }
        return returnlist;
    }



//    added by dustyn
    private drawableGraph(LinkedHashSet<GraphNode> nodeSet, LinkedHashSet<Edge> edgeSet, LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn, LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut  ){
        this.edgeList = new LinkedHashSet<>(edgeSet);
        this.nodeList = new LinkedHashSet<>(nodeSet);
        this.nodeToEdgesIn = new LinkedHashMap<>(nodeToEdgesIn);
        this.nodeToEdgesOut = new LinkedHashMap<>(nodeToEdgesOut);
}
    drawableGraph           copy(drawableGraph graph) {
        LinkedHashSet<GraphNode>    copyNodes   = new LinkedHashSet<>();
        LinkedHashSet<Edge>         copyEdges   = new LinkedHashSet<>(graph.edgeList);

        for (Edge e : copyEdges) {
            copyNodes.add( (GraphNode) e.start);
            copyNodes.add( (GraphNode) e.target);
        }

        Map<String, GraphNode> nodesMap = new HashMap<>();
        for (GraphNode node : copyNodes) {
            GraphNode newNode        = new GraphNode(node.label, node.GraphMLType, false);
            nodeList.add(newNode);
            nodesMap.put(newNode.label, newNode);
        }
        for (Edge edge : copyEdges) {

            GraphNode startObject = (GraphNode) edge.start;
            String startString = startObject.getLabel();
            GraphNode start = nodesMap.get(startString);

            GraphNode targetObject = (GraphNode) edge.target;
            String targetString = targetObject.getLabel();
            GraphNode target = nodesMap.get(targetString);

            start.addChild(target);
            target.addParent(start);
        } //alle Knoten initialisiert und Adjazenzinformationen in den Knoten;

//        generate HashMaps
        for (Edge e: edgeList) {

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
    LinkedHashSet<Edge>     getEdgeList() {
        return edgeList;
    }
    LinkedList<GraphNode>   getIsolatedNodes(){
    LinkedList<GraphNode> isolatedNodes = new LinkedList<>();
        for (GraphNode v: nodeList) {
            if (getIndegree(v) == 0 && getOutdegree(v) == 0){
                isolatedNodes.add(v);
            }
        }
        return isolatedNodes;
    }
    GraphNode               getSink () {
            for (GraphNode node : this.nodeList) {
                if (getOutdegree(node) == 0 && getIndegree(node) >= 1) {
                    return node;
                }
            }
            return null;
        }
    GraphNode               getSource () {
            for (GraphNode node : this.nodeList)
                if (getIndegree(node) == 0 && getOutdegree(node) >= 1) return node;
            return null; }


    private int getOutdegree(GraphNode node) {
        int size = 0;
        if (nodeToEdgesOut.containsKey(node))
        {
            size = nodeToEdgesOut.get(node).size();
        }
        return size;
    }
    private int getIndegree(GraphNode node) {
        int size = 0;
        if (nodeToEdgesIn.containsKey(node))
        {
            size = nodeToEdgesIn.get(node).size();
        }
        return size;
    }
    void justRemoveNode(GraphNode node){
        /* will only remove this node, nothin else */
        this.nodeList.remove(node);
        }

//    void removeOutgoingEdges(GraphNode node) { }
//@formatter:on


    void removeIngoingEdges(GraphNode node) {
        System.out.println("node = " + node);
        edgeList.removeAll(nodeToEdgesIn.get(node));
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
        edgeList.removeAll(nodeToEdgesOut.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesOut.get(node));
        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode targetNode = (GraphNode) edgeToBeRemoved.target;
            LinkedList<Edge> modifiedListOut = nodeToEdgesIn.get(targetNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error");
            nodeToEdgesIn.put(targetNode, modifiedListOut);
        }
        nodeToEdgesOut.keySet().removeIf(entry -> entry == node);
    }

    public GraphNode getNodeWithMaxDiffDegree() {
        int max = Integer.MIN_VALUE;
        GraphNode winnerNode = null;
        for (GraphNode node : nodeList) {
            if (getOutdegree(node) - getIndegree(node) > max) {
                max = getOutdegree(node) - getIndegree(node);
                winnerNode = node;
            }
        }
//        System.out.println("max = " + max);
        return winnerNode;
    }

    public GraphNode selectRandomNode() {
        int size = nodeList.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (GraphNode obj : nodeList) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    LinkedList<GraphNode> getAllSinks() {
        LinkedList<GraphNode> allSinks = new LinkedList<>();
        for (GraphNode node : this.nodeList) {
            if (getOutdegree(node) == 0 && getIndegree(node) >= 1) {
                allSinks.add(node);
            }
        }
        return allSinks;
    }

    @Override
    public String toString() {
        return (nodeList.size() + " Nodes " + "and " + edgeList.size()  + " Edges" );
    }

}


//    LinkedHashSet<Edge> getOutgoingEdges(GraphNode node) {
//        LinkedHashSet<Edge> outgoingEdges = new LinkedHashSet<>();
//        for (GraphNode v : node.getParents()) {
//            for (Edge e: edgeList){
//                if (e.start == v){
//                    outgoingEdges.add(e);
//                }
//            }
//        }
//        return outgoingEdges;
//    }

//    LinkedList<GraphNode>   getSources(){
//    LinkedList<GraphNode> sources = new LinkedList<>();
//        for (GraphNode node: nodeList){
//            if (node.inDegree() == 0){
//                sources.add(node);
//            }
//        }
//        return sources;
//    }

//    LinkedList<GraphNode>   getSinks(){
//        LinkedList<GraphNode> sinks = new LinkedList<>();
//        for (GraphNode node: nodeList){
//            if (node.outDegree() == 0){
//                sinks.add(node);
//            }
//        }
//        return sinks;
//    }

//    ArrayList<Edge> getIngoingEdges(GraphNode node) {
//        ArrayList<Edge> ingoingEdges = new ArrayList<>();
//        for (Edge e : edgeList)  // ingoing, if node is target from edge
//            if (e.target == node) ingoingEdges.add(e);
//        return ingoingEdges;
//    }

//        boolean containsSink () {
//            for (GraphNode node : nodeList) {
//                if (node.outDegree() == 0) return true;
//            }
//            return false;
//        }
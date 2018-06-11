//package model;
//
//import java.util.*;
//
////@formatter:off
//public class drawableGraph {
//    private final boolean verbose = false;
//
//    private LinkedHashSet<model.GraphNode>        nodeSet            = new LinkedHashSet<>();
//    private LinkedHashSet<Edge>             edgeSet            = new LinkedHashSet<>();
//    private LinkedHashMap<model.GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();
//    private LinkedHashMap<model.GraphNode,LinkedList<Edge>> nodeToEdgesIn  = new LinkedHashMap<>();
//
//    LinkedHashMap<model.GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeOut(){
//        return nodeToEdgesOut;
//    }
//    LinkedHashMap<model.GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeIn(){
//        return nodeToEdgesIn;
//    }
//
//    public drawableGraph(Graph graph, String edgeType) throws Exception { //sollte einziger Konstruktor bleiben
//        Set<Edge> relevantEdges = new LinkedHashSet<>();
//        Set<GraphNode> relevantNodes = new LinkedHashSet<>();
//        //relavante Nodes Erzeugen und Adjazenzinformationen aus Edges extrahieren.
//        if (!graph.getEdgeTypes().contains(new HelperTypes.EdgeType(edgeType))) {
//            throw new Exception("Chosen EdgeType: " + edgeType + " not in Graphs EdgeTypeList");
//        }
//
//        relevantEdges.addAll(graph.getEdgesOfType(edgeType));
//
//
//        for (Edge e : relevantEdges) {
//            relevantNodes.add((GraphNode) e.start);
//            relevantNodes.add ((GraphNode) e.target);
//        }
//
//        Map<String, model.GraphNode> nodesMap = new HashMap<>();
//        for (GraphNode relevantNode : relevantNodes) {
//            model.GraphNode newGraphNode = relevantNode.toGraphNode();
//            nodeSet.add(newGraphNode);
//            nodesMap.put(newGraphNode.label, newGraphNode);
//        }
//        for (Edge relevantEdge : relevantEdges) {
//            GraphNode startObject =(GraphNode) relevantEdge.start;
//            String startString = startObject.getLabel();
//
//            GraphNode targetObject = (GraphNode) relevantEdge.target;
//            String targetString = targetObject.getLabel();
//
//            model.GraphNode start = nodesMap.get(startString);
//            model.GraphNode target = nodesMap.get(targetString);
//            start.addChild(target);
//            target.addParent(start);
//
//            Edge edgeToAdd = new Edge(start, target);
//            edgeSet.add(edgeToAdd);
//
//        }
//    }
//
//    public LinkedHashSet<model.GraphNode> getNodes(){
//        return new LinkedHashSet<>(nodeSet); //returns a copy of the nodeSet
//    }
//    public LinkedHashSet<Edge>      getEdges(){
//        return new LinkedHashSet<>(edgeSet); //returns a copy of the edgeSet
//    }
//    LinkedHashSet<Edge>             getEdgeSet() {
//        return edgeSet;
//    }
//    LinkedHashSet<model.GraphNode>        getNodeSet() {
//        return nodeSet;
//    }
//    public model.GraphNode                getNode(String id) {
//        for (model.GraphNode node : nodeSet) {
//            if (node.label.equals(id)) return node;
//        }
//        return null;
//    }
//    public LinkedList<model.GraphNode>    getRoots(){
//        LinkedList<model.GraphNode> returnlist = new LinkedList<>();
//        for (model.GraphNode graphNode : nodeSet) {
//            if (graphNode.isRoot()) {
//                returnlist.add(graphNode);
//            }
//        }
//        return returnlist;
//    }
//    public LinkedList<model.GraphNode>    getLeaves(){
//        LinkedList<model.GraphNode> returnlist = new LinkedList<>();
//        for (model.GraphNode graphNode : nodeSet) {
//            if (graphNode.isLeaf()) {
//                returnlist.add(graphNode);
//            }
//        }
//        return returnlist;
//    }
//
//
//
////    added by dustyn
//    private drawableGraph(LinkedHashSet<model.GraphNode> nodeSet, LinkedHashSet<Edge> edgeSet, LinkedHashMap<model.GraphNode,LinkedList<Edge>> nodeToEdgesIn, LinkedHashMap<model.GraphNode,LinkedList<Edge>> nodeToEdgesOut  ){
//        this.edgeSet = new LinkedHashSet<>(edgeSet);
//        this.nodeSet = new LinkedHashSet<>(nodeSet);
//        this.nodeToEdgesIn = new LinkedHashMap<>(nodeToEdgesIn);
//        this.nodeToEdgesOut = new LinkedHashMap<>(nodeToEdgesOut);
//}
//    drawableGraph           copy(drawableGraph graph) {
//        LinkedHashSet<model.GraphNode>    copyNodes   = new LinkedHashSet<>();
//        LinkedHashSet<Edge>         copyEdges   = new LinkedHashSet<>(graph.getEdges());
//
//        LinkedHashMap<model.GraphNode,LinkedList<Edge>> nodeToEdgesIn = new LinkedHashMap<>();
//        LinkedHashMap<model.GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();
//
//        for (Edge e : copyEdges) {
//            copyNodes.add( (model.GraphNode) e.start);
//            copyNodes.add( (model.GraphNode) e.target);
//        }
//
//
//        // set Children/Parent structure
//        for (Edge edge : copyEdges) {
//            model.GraphNode u = (model.GraphNode) edge.start;
//            model.GraphNode v = (model.GraphNode) edge.target;
//            u.addChild(v);
//            v.addParent(u);
//        }
//
////        generate HashMaps
//        for (Edge e: edgeSet) {
//            model.GraphNode u = (model.GraphNode) e.start;
//            model.GraphNode v = (model.GraphNode) e.target;
//
//            LinkedList<Edge> edgeSetIn = new LinkedList<>();
//            if (nodeToEdgesIn.containsKey(v)){
//                edgeSetIn.addAll(nodeToEdgesIn.get(v));}
//            edgeSetIn.add(e);
//            nodeToEdgesIn.put(v, edgeSetIn);
//
//            LinkedList<Edge> edgeSetOut = new LinkedList<>();
//            if (nodeToEdgesOut.containsKey(u)){
//                edgeSetOut.addAll(nodeToEdgesOut.get(u));}
//            edgeSetOut.add(e);
//            nodeToEdgesOut.put(u, edgeSetOut);
//        }
//
//        return new drawableGraph(copyNodes,copyEdges, nodeToEdgesIn,nodeToEdgesOut);
//    }
//
//    LinkedList<model.GraphNode>   getIsolatedNodes(){
//    LinkedList<model.GraphNode> isolatedNodes = new LinkedList<>();
//        for (model.GraphNode v: nodeSet) {
//            if (getIndegree(v) == 0 && getOutdegree(v) == 0){
//                isolatedNodes.add(v);
//            }
//        }
//        return isolatedNodes;
//    }
//
//    void                    deleteIsolatedNodes(){
//        LinkedList<model.GraphNode> n = getIsolatedNodes();
//        System.out.printf("Found %d isolated nodes: %s\n",n.size(), n.toString());
//        for (model.GraphNode graphNode : n) {
//            justRemoveNode(graphNode);
//        }
//    }
//
//    model.GraphNode               getSink () {
//            for (model.GraphNode node : this.nodeSet)
//                if (getOutdegree(node) == 0 && getIndegree(node) > 0) return node;
//            return null;}
//    model.GraphNode               getSource () {
//            for (model.GraphNode node : this.nodeSet)
//                if (getIndegree(node) == 0) return node;
//            return null;}
//
//    void        justRemoveNode  (model.GraphNode removeMe){
//        /* will only remove this node, and children/parents references, nothin else */
//        removeMe.getChildren().forEach(child -> child.removeParent(removeMe));
//        removeMe.getParents().forEach(parent -> parent.removeChild(removeMe));
//        this.nodeSet.remove(removeMe); }
//    private int getOutdegree    (model.GraphNode node) {
//        int size = 0;
//        if (nodeToEdgesOut.containsKey(node))
//        {
//            size = nodeToEdgesOut.get(node).size();
//        }
//        if (size!=node.outdegree()) {
//            System.out.printf("Warning! Outdegree of node.children %d is not equal to the number of outgoing edges %d!\n",node.outdegree(),size);
//            System.out.println("Node: " + node.getLabel());
//            System.out.println("Edges by Hash: " + nodeToEdgesOut.get(node));
//            System.out.println("Edges by Node.Children: " + node.childrenLabels());
//        }
//        return size;
//    }
//    private int getIndegree     (model.GraphNode node) {
//        int size = 0;
//        if (nodeToEdgesIn.containsKey(node))
//        {
//            size = nodeToEdgesIn.get(node).size();
//        }
//        if (size!=node.indegree()) {
//            System.out.printf("Warning! Indegree of node.parents %d is not equal to the number of incoming edges %d!\n",node.indegree(),size);
//            System.out.println("Node: " + node.getLabel());
//            System.out.println("Edges by Hash: " + nodeToEdgesIn.get(node));
//            System.out.println("Edges by Node.Parent: " + node.parentLabels());
//        }
//        return size;
//    }
//
//
////    void removeOutgoingEdges(GraphNode node) { }
////@formatter:on
//
//
//    void removeIngoingEdges(model.GraphNode node) {
//        edgeSet.removeAll(nodeToEdgesIn.get(node));
//        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesIn.get(node));
//
//        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
//            model.GraphNode startNode = (model.GraphNode) edgeToBeRemoved.start;
//            LinkedList<Edge> modifiedListOut = nodeToEdgesOut.get(startNode);
//            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error to remove edge in modifiedList");
//            nodeToEdgesOut.put(startNode, modifiedListOut);
//        }
//        nodeToEdgesIn.keySet().removeIf(entry -> entry == node);
//    }
//    void removeOutgoingEdges(model.GraphNode node) {
//        edgeSet.removeAll(nodeToEdgesOut.get(node));
//        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesOut.get(node));
//        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
//            model.GraphNode targetNode = (model.GraphNode) edgeToBeRemoved.target;
//            LinkedList<Edge> modifiedListOut = nodeToEdgesIn.get(targetNode);
//            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error");
//            nodeToEdgesIn.put(targetNode, modifiedListOut);
//        }
//        nodeToEdgesOut.keySet().removeIf(entry -> entry == node);
//    }
//
//    model.GraphNode getNodeWithMaxDiffDegree() {
//        int max = Integer.MIN_VALUE;
//        model.GraphNode winnerNode = null;
//        for (model.GraphNode node : nodeSet) {
//            if (getOutdegree(node) - getIndegree(node) > max) {
//                max = getOutdegree(node) - getIndegree(node);
//                winnerNode = node;
//            }
//        }
////        System.out.println("max = " + max);
//        return winnerNode;
//    }
//    public model.GraphNode selectRandomNode() {
//        int size = nodeSet.size();
//        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
//        int i = 0;
//        for (model.GraphNode obj : nodeSet) {
//            if (i == item)
//                return obj;
//            i++;
//        }
//        return null;
//    }
//    LinkedList<model.GraphNode> getAllSinks() {
//        LinkedList<model.GraphNode> allSinks = new LinkedList<>();
//        for (model.GraphNode node : this.nodeSet) {
//            if (getOutdegree(node) == 0 && getIndegree(node) >= 1) {
//                allSinks.add(node);
//            }
//        }
//        return allSinks;
//    }
//    boolean reverseEdge(Edge edge) {
//        model.GraphNode u = (model.GraphNode) edge.start;
//        model.GraphNode v = (model.GraphNode) edge.target;
//        if (verbose) {
//            System.out.printf("Turning: %s to %s \n",u.label,v.label);
//            System.out.println("Old Nodes:");
//            System.out.printf("Node: %s Children: %s Parents: %s\n",u.label, u.childrenLabels(), u.parentLabels());
//            System.out.printf("Node: %s Children: %s Parents: %s\n",v.label, v.childrenLabels(), v.parentLabels());
//        }
//        boolean status1 = u.removeChild(v);
//        boolean status2 = u.addParent(v);
//        boolean status3 = v.removeParent(u);
//        boolean status4 = v.addChild(u);
//        if (verbose) {
//            System.out.println(status1);
//            System.out.println(status2);
//            System.out.println(status3);
//            System.out.println(status4);
//
//            System.out.println("New Nodes:");
//            System.out.printf("Node: %s Children: %s Parents: %s\n",u.label, u.childrenLabels(), u.parentLabels());
//            System.out.printf("Node: %s Children: %s Parents: %s\n",v.label, v.childrenLabels(), v.parentLabels());
//        }
//        // get equivalent edge in edgeSet, delete it and insert the turned variant
//        if (edgeSet.contains(edge)) {
//            if (verbose) {
//                System.out.println("Edge in EdgeSet going to be turned");
//            }
//            edgeSet.remove(edge);
//            edgeSet.add(new Edge(v, u, edge.edgeType, edge.weight));
//        }
//
//        return status1 && status2 && status3 && status4;
//    }
//
//
//    void addDummies(){
//        LinkedList<Edge> edgesToDelete = new LinkedList<>();
//        LinkedList<Edge> edgesNew = new LinkedList<>();
//        for (Edge edge : edgeSet) {
//            model.GraphNode start = (model.GraphNode) edge.start;
//            model.GraphNode target = (model.GraphNode) edge.target;
//            int spanningLevels = Math.abs(start.getLayer() - target.getLayer())-1;
////            spanningLevels = Math.abs(spanningLevels * spanningLevels -1); // keine ahnung wie man sonst das vorzeichen entfernt
//            if (spanningLevels > 0) { // precondition for adding dummies
//                edgesToDelete.add(edge);
//                List<model.GraphNode> block = new LinkedList<>();
//                block.add(start);
//                for (int i = spanningLevels; i > 0; i--) { // for every additional spanned level
//                    String label = "Dummy-" + i + "; from: " + start.label + " to: " + target.label+"|";
//                    model.GraphNode node = new model.GraphNode(label, "Dummy", true); // create a dummy with a proper label
//                    node.setLayer(target.getLayer() + i);
//                    nodeSet.add(node); // add to NodeSet
//                    block.add(node); // add to block to find parent/children
//                }
//                block.add(target);
//
//                for (int i = 0; i < block.size(); i++) { // hoffe das wirft keinen outOfBounds
//                    if (!block.get(i).equals(target)) {
//                        block.get(i).addChild(block.get(i+1)); // except for at target Node, add child i+1
//                        edgesNew.add(new Edge(block.get(i), block.get(i+1))); // likewise, add Edge to edgeList
//                    }
//                    if (!block.get(i).equals(start)) block.get(i).addParent(block.get(i-1)); // except for at start Node, add parent i-1
//                }
//            }
//        }
//        for (Edge edge : edgesToDelete) {
//            deleteEdge(edge);
//        }
//        for (Edge edge : edgesNew) {
//            edgeSet.add(edge);
//        }
//    }
//
//    private boolean deleteEdge(Edge e){
//        if (!edgeSet.contains(e)) return false;
//        try {
//            model.GraphNode start = (model.GraphNode) e.start;
//            model.GraphNode target = (model.GraphNode) e.target;
//            start.removeChild(target);
//            target.removeParent(start);
//            edgeSet.remove(e);
//            //does not update the hashsets nodeToIn/OutgoingEdges
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return (nodeSet.size() + " Nodes " + "and " + edgeSet.size()  + " Edges" );
//    }
//
//}
//
//
////    LinkedHashSet<Edge> getOutgoingEdges(GraphNode node) {
////        LinkedHashSet<Edge> outgoingEdges = new LinkedHashSet<>();
////        for (GraphNode v : node.getParents()) {
////            for (Edge e: edgeSet){
////                if (e.start == v){
////                    outgoingEdges.add(e);
////                }
////            }
////        }
////        return outgoingEdges;
////    }
//
////    LinkedList<GraphNode>   getSources(){
////    LinkedList<GraphNode> sources = new LinkedList<>();
////        for (GraphNode node: nodeSet){
////            if (node.inDegree() == 0){
////                sources.add(node);
////            }
////        }
////        return sources;
////    }
//
////    LinkedList<GraphNode>   getSinks(){
////        LinkedList<GraphNode> sinks = new LinkedList<>();
////        for (GraphNode node: nodeSet){
////            if (node.outDegree() == 0){
////                sinks.add(node);
////            }
////        }
////        return sinks;
////    }
//
////    ArrayList<Edge> getIngoingEdges(GraphNode node) {
////        ArrayList<Edge> ingoingEdges = new ArrayList<>();
////        for (Edge e : edgeSet)  // ingoing, if node is target from edge
////            if (e.target == node) ingoingEdges.add(e);
////        return ingoingEdges;
////    }
//
////        boolean containsSink () {
////            for (GraphNode node : nodeSet) {
////                if (node.outDegree() == 0) return true;
////            }
////            return false;
////        }
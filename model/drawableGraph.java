package model;

import model.HelperTypes.ProtoNode;

import java.util.*;

//@formatter:off
public class drawableGraph {
    private LinkedHashSet<GraphNode>        GraphNodeSet             = new LinkedHashSet<>();
    private LinkedHashSet<Edge>             GraphNodeEdgeSet         = new LinkedHashSet<>();
    private LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn  = new LinkedHashMap<>();

    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeOut(){
        return nodeToEdgesOut;
    }
    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeIn(){
        return nodeToEdgesIn;
    }

    public  drawableGraph(GraphMLGraph graph, String edgeType) throws Exception {

        if (!graph.getEdgeTypes().contains(new HelperTypes.EdgeType(edgeType)))
            throw new Exception("Chosen EdgeType: " + edgeType + " not in Graphs EdgeTypeList");

        Set<Edge> relevantEdges = new LinkedHashSet<>(graph.getEdgesOfType(edgeType));
        for (Edge e : relevantEdges) {

            // generate ProtoNodes from the Edge
            ProtoNode ProtoNodeStart = (ProtoNode) e.start;
            ProtoNode ProtoNodeTarget = (ProtoNode) e.target;

            // migrate to ProtoNode to GraphNode
            GraphNode GraphNodeStart = ProtoNodeStart.toGraphNode();
            GraphNode GraphNodeTarget = ProtoNodeTarget.toGraphNode();

            // update for EdgeOut
            GraphNodeStart.addChild(GraphNodeTarget);
            addEdgeOutToHashmap(GraphNodeStart, e);

            // update for EdgeIn
            GraphNodeTarget.addParent(GraphNodeStart);
            addEdgeInToHashmap(GraphNodeTarget, e);

            // Create new GraphNodes containing Edge and add to GraphNodeSet
            Edge GraphNodeEdge = new Edge(GraphNodeStart, GraphNodeTarget);
            GraphNodeEdgeSet.add(GraphNodeEdge);
        }
    }
    private drawableGraph(LinkedHashSet<GraphNode> nodeSet, LinkedHashSet<Edge> edgeSet, LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn, LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut  ){
        this.GraphNodeEdgeSet = new LinkedHashSet<>(edgeSet);
        this.GraphNodeSet = new LinkedHashSet<>(nodeSet);
        this.nodeToEdgesIn = new LinkedHashMap<>(nodeToEdgesIn);
        this.nodeToEdgesOut = new LinkedHashMap<>(nodeToEdgesOut);
}

    private void addEdgeInToHashmap(GraphNode graphNodeTarget, Edge e) {
        LinkedList<Edge> edgeSetIn = new LinkedList<>();
        if (nodeToEdgesIn.containsKey(graphNodeTarget)){
            edgeSetIn.addAll(nodeToEdgesIn.get(graphNodeTarget));}
            edgeSetIn.add(e);
            nodeToEdgesIn.put(graphNodeTarget, edgeSetIn);
    }
    private void addEdgeOutToHashmap(GraphNode graphNodeStart, Edge e) {
        LinkedList<Edge> edgeSetOut = new LinkedList<>();
        if (nodeToEdgesOut.containsKey(graphNodeStart)){
                edgeSetOut.addAll(nodeToEdgesOut.get(graphNodeStart));}
            edgeSetOut.add(e);
            nodeToEdgesOut.put(graphNodeStart, edgeSetOut);
    }
    drawableGraph copy(drawableGraph graph) {
        LinkedHashSet<GraphNode>    copyNodes   = new LinkedHashSet<>();
        LinkedHashSet<Edge>         copyEdges   = new LinkedHashSet<>();
        LinkedList<Edge>            origEdges   = new LinkedList<>(graph.getGraphNodeEdges());

        LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesIn = new LinkedHashMap<>();
        LinkedHashMap<GraphNode,LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();

        for (Edge e_orig : origEdges) {
            GraphNode u_orig = (GraphNode) e_orig.start;
            String u_label = u_orig.getLabel();
            String u_type = u_orig.getType();
            Boolean u_dummy = u_orig.isDummyNode();
            Integer u_layer = u_orig.getLayer();
            int u_ID = u_orig.getID();

            GraphNode u_copy = new GraphNode(u_ID, u_label,u_type,u_dummy,u_layer);
            copyNodes.add(u_copy);

            GraphNode v_orig = (GraphNode) e_orig.target;
            String v_label = v_orig.getLabel();
            String v_type = v_orig.getType();
            Boolean v_dummy = v_orig.isDummyNode();
            Integer v_layer = v_orig.getLayer();
            int v_ID = v_orig.getID();

            GraphNode v_copy = new GraphNode(v_ID, v_label,v_type,v_dummy,v_layer);
            copyNodes.add(v_copy);

            Edge e_copy = new Edge(u_copy,v_copy);
            copyEdges.add(e_copy);

            // update Hashmap for EdgeOut
            u_copy.addChild(v_copy);
            addEdgeOutToHashmap(u_copy, e_copy);

            // update Hashmap for EdgeIn
            v_copy.addParent(u_copy);
            addEdgeInToHashmap(v_copy, e_copy);
        }
        return new drawableGraph(copyNodes,copyEdges, nodeToEdgesIn,nodeToEdgesOut);
    }

    LinkedHashSet<GraphNode>    copyNodeSet(){
        return new LinkedHashSet<>(GraphNodeSet); //returns a copy of the nodeSet
    }
    LinkedHashSet<Edge>         copyEdgeSet(){
        return new LinkedHashSet<>(GraphNodeEdgeSet); //returns a copy of the edgeSet
    }
    private LinkedHashSet<Edge> getGraphNodeEdges() {
        return GraphNodeEdgeSet;
    }

    LinkedList<GraphNode>       getIsolatedNodes(){
    LinkedList<GraphNode> isolatedNodes = new LinkedList<>();
        for (GraphNode v: GraphNodeSet) {
            if (getIndegree(v) == 0 && getOutdegree(v) == 0){
                isolatedNodes.add(v);
            }
        }
        return isolatedNodes;
    }
    GraphNode                   getSink () {
            for (GraphNode node : GraphNodeSet)
                if (getOutdegree(node) == 0 && getIndegree(node) > 0) return node;
            return null;}
    GraphNode                   getSource () {
            for (GraphNode node : GraphNodeSet)
                if (getIndegree(node) == 0) return node;
            return null;}

    void        justRemoveNode  (GraphNode removeMe){
        /* will only remove this node, and children/parents references, nothin else */
        removeMe.getChildren().forEach(child -> child.removeParent(removeMe));
        removeMe.getParents().forEach(parent -> parent.removeChild(removeMe));
        this.GraphNodeSet.remove(removeMe); }
    int         getOutdegree    (GraphNode node) {
        int size = 0;
        if (nodeToEdgesOut.containsKey(node))
        {
            size = nodeToEdgesOut.get(node).size();
        }
        if (size!=node.outdegree()) {
//            System.out.printf("Warning! Outdegree of node.parents %d is not equal to the number of outgoing edges %d!\n",size,node.outdegree());
        }
        return size;
    }
    int         getIndegree     (GraphNode node) {
        int size = 0;
        if (nodeToEdgesIn.containsKey(node))
        {
            size = nodeToEdgesIn.get(node).size();
        }
        if (size!=node.indegree()) {
//            System.out.printf("Warning! Indegree of node.parents %d is not equal to the number of incoming edges %d!\n",size,node.indegree());
        }
        return size;
    }


    void removeIngoingEdges(GraphNode node) {

        // remove from the GraphNodeEdgeSet
        GraphNodeEdgeSet.removeAll(nodeToEdgesIn.get(node));

        // update Hashmap
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
        // remove from GraphNodeEdgeSet
        GraphNodeEdgeSet.removeAll(nodeToEdgesOut.get(node));

        // update hashmap
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesOut.get(node));
        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode targetNode = (GraphNode) edgeToBeRemoved.target;
            LinkedList<Edge> modifiedListOut = nodeToEdgesIn.get(targetNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error");
            nodeToEdgesIn.put(targetNode, modifiedListOut);
        }
        nodeToEdgesOut.keySet().removeIf(entry -> entry == node);
    }

    GraphNode               getNodeWithMaxDiffDegree() {
        int max = Integer.MIN_VALUE;
        GraphNode winnerNode = null;
        for (GraphNode node : GraphNodeSet) {
            if (getOutdegree(node) - getIndegree(node) > max) {
                max = getOutdegree(node) - getIndegree(node);
                winnerNode = node;
            }
        }
        return winnerNode;
    }
    LinkedList<GraphNode>   getAllSinks() {
        LinkedList<GraphNode> allSinks = new LinkedList<>();
        for (GraphNode node : GraphNodeSet) {
            if (getOutdegree(node) == 0 && getIndegree(node) >= 1) {
                allSinks.add(node);
            }
        }
        return allSinks;
    }

    void    addEdge     (Edge edge) {
        this.GraphNodeEdgeSet.add(edge);
    }
    boolean deleteEdge  (Edge e) {
        if (!GraphNodeEdgeSet.contains(e)) return false;
        try {
            GraphNode start = (GraphNode) e.start;
            GraphNode target = (GraphNode) e.target;
            start.removeChild(target);
            target.removeParent(start);
            GraphNodeEdgeSet.remove(e);
            //does not update the hashsets nodeToIn/OutgoingEdges
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    boolean reverseEdge (Edge edge) {
        GraphNode u = (GraphNode) edge.start;
        GraphNode v = (GraphNode) edge.target;

        boolean status1 = u.removeChild(v);
        boolean status2 = u.addParent(v);
        boolean status3 = v.removeParent(u);
        boolean status4 = v.addChild(v);

        return status1 && status2 && status3 && status4;
    }


    @Override
    public String toString() {
        return (GraphNodeSet.size() + " Nodes " + "and " + GraphNodeEdgeSet.size() + " Edges");
    }


//////////////////////////
//  not really needed area
/////////////////////////

        void                    deleteIsolatedNodes(){
        LinkedList<GraphNode> n = getIsolatedNodes();
        System.out.printf("Found %d isolated nodes: %s\n",n.size(), n.toString());
        for (GraphNode graphNode : n) {
            justRemoveNode(graphNode);
        }
    }


    public GraphNode selectRandomNode() {
        int size = GraphNodeSet.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (GraphNode obj : GraphNodeSet) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    public GraphNode getNode(String id) {
        for (GraphNode node : GraphNodeSet) {
            if (node.label.equals(id)) return node;
        }
        return null;
    }


}
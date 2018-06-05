package model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import model.HelperTypes.ProtoNode;

public class drawableGraph {

    private final LinkedHashSet<GraphNode> nodeList = new LinkedHashSet<>();

    public drawableGraph(GraphMLGraph graph, String edgeType) throws Exception { //sollte einziger Konstruktor bleiben
        Set<Edge> relevantEdges = new LinkedHashSet<>();
        Set<ProtoNode> relevantNodes = new LinkedHashSet<>();
        //relavante Nodes Erzeugen und Adjazenzinformationen aus Edges extrahieren.
        if (!graph.getEdgeTypes().contains(new HelperTypes.EdgeType(edgeType))) {
            throw new Exception("Chosen EdgeType: " + edgeType + " not in Graphs EdgeTypeList");
        }
        relevantEdges.addAll(graph.getEdgesOfType(edgeType));
        for (Edge e : relevantEdges) {
            if (!relevantNodes.contains(e.start)) {
                relevantNodes.add(e.start);
            }
            if (!relevantNodes.contains(e.target)) {
                relevantNodes.add(e.target);
            }
        }
        Map<String, GraphNode> nodesMap = new HashMap<>();
        for (ProtoNode relevantNode : relevantNodes) {
            GraphNode newNode = relevantNode.toGraphNode();
            nodeList.add(newNode);
            nodesMap.put(newNode.label, newNode);
        }
        for (Edge relevantEdge : relevantEdges) {
            GraphNode start = nodesMap.get(relevantEdge.start.getLabel());
            GraphNode target = nodesMap.get(relevantEdge.target.getLabel());
            start.addChild(target);
            target.addParent(start);
        } //alle Knoten initialisiert und Adjazenzinformationen in den Knoten;
    }
    
    public LinkedHashSet<GraphNode> getNodes(){
        LinkedHashSet<GraphNode> returnset = new LinkedHashSet<>(nodeList);
        return returnset; //returns a copy of the nodeList
    }
    
    public LinkedList<GraphNode> getRoots(){
        LinkedList<GraphNode> returnlist = new LinkedList<>();
        for (GraphNode graphNode : nodeList) {
            if (graphNode.isRoot()) {
                returnlist.add(graphNode);
            }
        }
        return returnlist;
    }
    
    public LinkedList<GraphNode> getLeaves(){
        LinkedList<GraphNode> returnlist = new LinkedList<>();
        for (GraphNode graphNode : nodeList) {
            if (graphNode.isLeaf()) {
                returnlist.add(graphNode);
            }
        }
        return returnlist;
    }

    public GraphNode getNode(String id) {
        for (GraphNode node : nodeList) {
            if (node.label.equals(id)) return node;
        }
        return null;
    }

}

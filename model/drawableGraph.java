package model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import model.HelperTypes.protoNode;

public class drawableGraph {
    
    public final LinkedHashSet<GraphNode> nodeList;

    public drawableGraph(GraphMLGraph graph, String edgeType) throws Exception { //sollte einziger Konstruktor bleiben
        Set<Edge> relevantEdges = new LinkedHashSet<>();
        Set<protoNode> relevantNodes = new LinkedHashSet<>();
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
        nodeList = new LinkedHashSet<>();
        Map<String, GraphNode> nodesMap = new HashMap<>();
        for (protoNode relevantNode : relevantNodes) {
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
        
        //Azyklisch machen
    }
    
}

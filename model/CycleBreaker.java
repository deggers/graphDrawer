//@formatter:off
package model;
import model.HelperTypes.ProtoNode;

import java.util.HashMap;import java.util.HashSet;
import java.util.LinkedHashSet;

public class CycleBreaker {
    /*    Returns the edges of a cycle found via a directed, depth-first traversal. */
    private static final boolean verbose = true;
    private static HashSet<Edge> turnedEdges = new LinkedHashSet<>(); //in their turned form, hence start and target must be interchanged before drawing
    private static HashMap<String, GraphNode> namesMap;

    public static void dfsCycleBreaker(drawableGraph g) {
        //Tiefensuche um Zyklen zu entfernen
        namesMap = new HashMap<>();
        for (GraphNode graphNode : g.getNodes()) {
            namesMap.put(graphNode.label,graphNode);
        }
        for(GraphNode startNode: g.getNodes()){
            if (startNode.getDfsStatus()=='u') {
                dfsRec(startNode);
            }
        }
//        add the change


        // turn each edge from turnedEdges
        for (Edge edge: turnedEdges) {
            // turn me on!! no ,reverse me!
            GraphNode start = namesMap.get(edge.start.getLabel());
            GraphNode target = namesMap.get(edge.target.getLabel());
            start.removeChild(target);
            start.addParent(target);
            target.removeParent(start);
            target.addChild(start);
        }



    }
    private static void dfsRec(GraphNode node){
        if (node.isLeaf()) {
            node.setDfsStatus('f');
        } else {
            node.setDfsStatus('v');
            for (GraphNode graphNode : node.getChildren()) {
                if (graphNode.getDfsStatus()=='v') {
                    if (verbose==true) {
                        System.out.printf("Cycle found, turning edge from %s to %s \n", node.label, graphNode.label);
                    }
                    turnedEdges.add(new Edge(new ProtoNode(node.label), new ProtoNode(graphNode.label)));
                } else {
                    if (graphNode.getDfsStatus()=='u') {
                        dfsRec(graphNode);
                    } else {
                        //Node has been finalized, nothing to do
                    }
                }

            }
            node.setDfsStatus('f');
        }

    }


}



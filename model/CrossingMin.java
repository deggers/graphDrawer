package model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CrossingMin {

    public static Graph naiveAlgo(Graph graph) {
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();
        int bestCrossings = Integer.MAX_VALUE;
        for (int layer = 1; layer <= numOfLayer-1; layer++) {
            System.out.println("layer = " + layer);
            LinkedList<GraphNode> thisLayer = layerMap.get(layer);
            LinkedList<GraphNode> nextLayer = layerMap.get((layer + 1));
            // count crossings, save them as best

            for (int i = 0; i <= 100000; i++) {
                Collections.shuffle(nextLayer);
                int shuffleCrosses = CountCrossing_Naive(graph, thisLayer, nextLayer);
                if (shuffleCrosses < bestCrossings) {
                    layerMap.put(layer + 1, nextLayer);
//                    System.out.println("neuer bestwert!: " + shuffleCrosses + " Kreuzungen");
                }
                // count corssing, less than best? save as best
                // is zero? done. tried 10.000 times? done and take best.

            }

        }


        return graph;
    }

    private static int CountCrossing_Naive(Graph g, LinkedList<GraphNode> topLayer, LinkedList<GraphNode> bottomLayer) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesOut = g.getHashmap_nodeToEdgeOut();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesIn = g.getHashmap_nodeToEdgeIn();
        int crossings = 0;
        // look for each node from top to which it connects in the order below
        for (int i = 0; i <= topLayer.size()-1; i++) {
            GraphNode topNode = topLayer.get(i);
            if (nodeToEdgesIn.containsKey(topNode)) {
                LinkedList<Edge> edgesTo = nodeToEdgesIn.get(topNode);
                LinkedList<GraphNode> targets = new LinkedList<>();
                for (Edge edge : edgesTo)
                    targets.add(edge.start);   // e.g. Node1 and Node3
                    for (int k = 0; k <= bottomLayer.size() - 1; k++) {
                        GraphNode bottomNode = bottomLayer.get(k);
                        if (targets.contains(bottomNode))
                            if (k != i && nodeToEdgesOut.containsKey(bottomNode))
                                crossings++;
                    }
            }
        }
        return crossings;
    }

}

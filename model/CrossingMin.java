package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CrossingMin {

    public static Graph naiveAlgo(Graph graph) {
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();

        for (int layer = 1; layer <= numOfLayer-1; layer++) {
            int bestCrossings = Integer.MAX_VALUE;

            System.out.println("fixedLayer = " + layer);
            System.out.println("freeLayer = " + (layer+1));
            LinkedList<GraphNode> fixedLayer = layerMap.get(layer);
            LinkedList<GraphNode> freeLayer = layerMap.get((layer + 1));
            // count crossings, save them as best

            int SHUFFLING = 100000;
            for (int i = 0; i <= SHUFFLING; i++) {
                Collections.shuffle(freeLayer);
                int shuffleCrosses = BLCC_naive(graph, fixedLayer, freeLayer);

                if (shuffleCrosses < bestCrossings) {
                    bestCrossings = shuffleCrosses;
                    layerMap.put(layer + 1, freeLayer);
                    System.out.println("neuer bestwert!: " + shuffleCrosses + " Kreuzungen");
                    if (bestCrossings == 0)
                        break;
                }
                // count corssing, less than best? save as best
                // is zero? done. tried 10.000 times? done and take best.

            }

        }


        return graph;
    }

    /*
    fixed= 1 bis size-1
    free= 1 bis size
    if i not k && any node before has connection to any after i = crossing
     */

    private static int BLCC_naive(Graph g, LinkedList<GraphNode> fixedLayer, LinkedList<GraphNode> freeLayer) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesOut = g.getHashmap_nodeToEdgeOut();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesIn = g.getHashmap_nodeToEdgeIn();
        int crossings = 0;
        // look for each node from top to which it connects in the order below
        for (int i = 0; i <= fixedLayer.size() - 2; i++) { // -2 because last node cannot account for more crossings
            GraphNode fixedNode = fixedLayer.get(i);
            if (nodeToEdgesIn.containsKey(fixedNode)) {
                LinkedList<Edge> edgesTo = nodeToEdgesIn.get(fixedNode);
                LinkedList<GraphNode> targets = new LinkedList<>();
                for (Edge edge : edgesTo)
                    targets.add(edge.start);   // e.g. Node1 and Node3
                    for (int k = 0; k <= freeLayer.size() - 1; k++) {
                        GraphNode freeNode = freeLayer.get(k);
                        if (targets.contains(freeNode))
                            if (k != i )
                                // there might be some crossings from nodes before
                                for (int subIndex = 0; subIndex <= k - i; subIndex++) {
                                    GraphNode possibleCrossingCausingNode = freeLayer.get(subIndex);
                                    if (nodeToEdgesOut.containsKey(possibleCrossingCausingNode)) {
                                        LinkedList<Edge> possibleCrossingEdges = nodeToEdgesOut.get(possibleCrossingCausingNode);
                                        for (Edge maybeCrossingEdge : possibleCrossingEdges) {
                                            GraphNode pointedNode = maybeCrossingEdge.target;
                                            int IndexOfPointedNode = fixedLayer.indexOf(pointedNode);
                                            if (IndexOfPointedNode > i) {
                                                crossings++;
                                            }
                                        }
                                    }
                                }
                    }
            }
        }
        return crossings;
    }

}

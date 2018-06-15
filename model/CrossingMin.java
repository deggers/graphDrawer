package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CrossingMin {
    private static boolean VERBOSE = true;

    public static Graph naiveAlgo(Graph graph) {
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();

        for (int layer = 1; layer <= numOfLayer-1; layer++) {

            if (VERBOSE) System.out.println("fixedLayer = " + layer);
            if (VERBOSE) System.out.println("freeLayer = " + (layer+1));
            LinkedList<GraphNode> fixedLayer = layerMap.get(layer);
            LinkedList<GraphNode> freeLayer = layerMap.get((layer + 1));

            int bestCrossings = Integer.MAX_VALUE;
            int SHUFFLING = 100;
            for (int i = 0; i <= SHUFFLING; i++) {
                Collections.shuffle(freeLayer);
                int shuffleCrosses = BLCC_naive(graph, fixedLayer, freeLayer);
                if (shuffleCrosses < bestCrossings) {
                    bestCrossings = shuffleCrosses;
                    layerMap.put(layer + 1, freeLayer);
                    if (VERBOSE) System.out.println("neuer Bestwert!: " + shuffleCrosses + " Kreuzungen");
                    if (bestCrossings == 0)
                            break;
                }
            }
        }
        return graph;
    }

    private static int BLCC_naive(Graph g, LinkedList<GraphNode> fixedLayer, LinkedList<GraphNode> freeLayer) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesOut = g.getHashmap_nodeToEdgeOut();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesIn = g.getHashmap_nodeToEdgeIn();
        int crossings = 0;
        for (int i = 0; i <= fixedLayer.size() - 2; i++) { // -2 because last node cannot account for more crossings
            GraphNode fixedNode = fixedLayer.get(i);
            if (nodeToEdgesOut.containsKey(fixedNode)) {
                LinkedList<Edge> edgesTo = nodeToEdgesOut.get(fixedNode);
                LinkedList<GraphNode> targets = new LinkedList<>();
                for (Edge edge : edgesTo)
                    targets.add(edge.target);   // e.g. Node1 and Node3
                    for (int k = 0; k <= freeLayer.size() - 1; k++) {
                        GraphNode freeNode = freeLayer.get(k);
                        if (targets.contains(freeNode))
                            if (k > i )
                                for (int subIndex = 0; subIndex <= k - i; subIndex++) {
                                    GraphNode possibleCrossingCausingNode = freeLayer.get(subIndex);
                                    if (nodeToEdgesIn.containsKey(possibleCrossingCausingNode)) {
                                        LinkedList<Edge> possibleCrossingEdges = nodeToEdgesIn.get(possibleCrossingCausingNode);
                                        for (Edge maybeCrossingEdge : possibleCrossingEdges) {
                                            GraphNode pointedNode = maybeCrossingEdge.start;
                                            int IndexOfPointedNode = fixedLayer.indexOf(pointedNode);
                                            if (IndexOfPointedNode > i)
                                                crossings++;
                                        }
                                    }
                                }
                    }
            }
        }
        return crossings;
    }

}

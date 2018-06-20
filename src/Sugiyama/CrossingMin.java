package Sugiyama;

import structure.*;

import java.util.*;

public class CrossingMin {
    private static boolean VERBOSE = false;
    private static boolean DEBUG = false;

    public static Graph naiveAlgo(Graph graph) {
        LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();

        for (int layer = 1; layer <= numOfLayer - 1; layer++) {

            if (VERBOSE) System.out.println("fixedLayer = " + layer);
            if (VERBOSE) System.out.println("freeLayer = " + (layer + 1));
            LinkedList<GraphNode> fixedLayer = layerMap.get(layer);
            LinkedList<GraphNode> freeLayer = layerMap.get((layer + 1));

            int bestCrossings = Integer.MAX_VALUE;
            ArrayList<LinkedList<GraphNode>> ListOfAllPermutation;
            ListOfAllPermutation = graph.heapGenerate(freeLayer.size(), freeLayer, new ArrayList<>());
            if (VERBOSE) System.out.println("Checking " + ListOfAllPermutation.size() + " Permutations :)");
            for (LinkedList<GraphNode> permutationOfFreeLayer : ListOfAllPermutation) {
                int shuffleCrosses = BLCC_naive(graph, fixedLayer, permutationOfFreeLayer);
                if (VERBOSE && DEBUG) System.out.printf("for layer %d with order %s found %s crossings%n",(layer+1),permutationOfFreeLayer,shuffleCrosses);
                if (shuffleCrosses < bestCrossings) {
                    bestCrossings = shuffleCrosses;
                    graph.setCrossings("L" + layer + "-L" + (layer+1),bestCrossings );
                    layerMap.put(layer + 1, permutationOfFreeLayer);
                    if (VERBOSE) System.out.println("neuer Bestwert!: " + shuffleCrosses + " Kreuzungen");
                    if (bestCrossings == 0) break;
                }
            }
        }
        return graph;
    }

    private static int BiLayerCrossingCounter_naive(Graph g, LinkedList<Graph> fixedLayer, LinkedList<Graph> freeLayer) {
        return 1;
    }

    private static int BLCC_naive(Graph g, LinkedList<GraphNode> fixedLayer, LinkedList<GraphNode> freeLayer) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = g.getEdgesInMap();
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = g.getEdgesOutMap();

        int crossings = 0;
        for (int i = 0; i <= fixedLayer.size() - 1; i++) { // -2 because last node cannot account for more crossings
            GraphNode fixedNode = fixedLayer.get(i);
            int fixedLayerInt = fixedNode.getLayer();
            int freeLayerInt = fixedNode.getLayer() + 1;
            if (VERBOSE && DEBUG) System.out.println("\nfixedNode = " + fixedNode);
            LinkedList<GraphNode> adjacentToFixedNodes = new LinkedList<>();
            if (edgesOut.containsKey(fixedNode)) adjacentToFixedNodes.addAll(g.getChildren(fixedNode));
            if (edgesIn.containsKey(fixedNode)) adjacentToFixedNodes.addAll(g.getParentsOf(fixedNode));
            adjacentToFixedNodes.removeIf(node -> node.getLayer() != freeLayerInt);
            if (VERBOSE && DEBUG) System.out.println("adjacentNodes = " + adjacentToFixedNodes);
                for (GraphNode adjToFixedNode : adjacentToFixedNodes) {
                    if (VERBOSE && DEBUG) System.out.println("looking left for all nodes from " + adjToFixedNode);
                    for (int k = 0; k <= freeLayer.indexOf(adjToFixedNode)-1; k++) {
                        GraphNode checkNode = freeLayer.get(k);
                        if (VERBOSE && DEBUG) System.out.println("checkNode = " + checkNode);
                        LinkedList<GraphNode> adjToCheckNode = new LinkedList<>();
                        if (edgesOut.containsKey(checkNode)) adjToCheckNode.addAll(g.getChildren(checkNode));
                        if (edgesIn.containsKey(checkNode)) adjToCheckNode.addAll(g.getParentsOf(checkNode));
                        adjToCheckNode.removeIf(node -> node.getLayer() != fixedLayerInt);
                        if (VERBOSE && DEBUG) System.out.println("adjToCheckNode = " + adjToCheckNode);
                            for (GraphNode particularNode : adjToCheckNode){
                                if (VERBOSE && DEBUG) System.out.println("particularNode = " + particularNode);
                                int indexParticularNode = fixedLayer.indexOf(particularNode);
                                int indexFixedNode = fixedLayer.indexOf(fixedNode);
                                if (indexParticularNode > indexFixedNode) {
                                    crossings++;
                                }
                            }
                    }
                }
        }
        return crossings;
    }

}

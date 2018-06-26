package Sugiyama;

import structure.*;

import java.util.*;

public class CrossingMin {
    private static boolean VERBOSE = false;
    private static boolean DEBUG = false ;
    private static LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap;

    public static void baryCenter_naive(Graph graph, boolean bidirectional, int sweeps) {
        layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();

        for (int i = 0; i < sweeps; i++) {
            if (bidirectional) {
                for (int layer = 1; layer <= numOfLayer - 1; layer++)
                    processBaryCenter_naive(graph, layer, (layer + 1), "top-down");
                for (int layer = numOfLayer; layer > 1; layer--)
                    processBaryCenter_naive(graph, layer, (layer - 1),"down-top"); }
            else for (int layer = 1; layer <= numOfLayer -1; layer++)
                processBaryCenter_naive(graph, layer, (layer + 1),"top-down");
        }
    }
    private static void processBaryCenter_naive(Graph g, int indexFixed, int indexFree, String direction) {
        layerMap = g.getLayerMap();
        if (VERBOSE) System.out.println("fixedLayer = " + indexFixed);
        if (VERBOSE) System.out.println("freeLayer = " + indexFree);
        LinkedList<GraphNode> fixedLayer = layerMap.get(indexFixed);
        LinkedList<GraphNode> freeLayer = layerMap.get(indexFree);
        int shuffleCrosses = 0; // NNEED TO BE CHANGED!!V :)
        int bestCrossings = Integer.MAX_VALUE;

        for (GraphNode freeNode : freeLayer) {
            ArrayList<GraphNode> adjacentNodes = g.getAdjacentNodes(freeNode,indexFixed);
            double inDegree = adjacentNodes.size();
            double sumOfIndices = sumUpIndices(fixedLayer, adjacentNodes);
            freeNode.x_Bary = ((1/inDegree) * sumOfIndices);}
        freeLayer.sort(Comparator.comparing(GraphNode::getX_Bary));
        // still open, check if ambigous position swap improve that situation
    }
    private static double sumUpIndices(LinkedList<GraphNode> layer, ArrayList<GraphNode> adjacentNodes) {
        double sum = 0.0;
        for (GraphNode graphNode : layer) {
            if (adjacentNodes.contains(graphNode)) {
                double indexOf = layer.indexOf(graphNode) +1;
                sum += indexOf;
            }
        }
        return sum;
    }

    public static void allPermutation(Graph graph, boolean bidirectional, int sweeps) {
        layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();

        for (int i = 0; i < sweeps; i++) {
            if (bidirectional) {
                for (int layer = 1; layer <= numOfLayer - 1; layer++)
                    processLayers(graph,"Permutation", layer, (layer + 1), "top-down");
                for (int layer = numOfLayer; layer > 1; layer--)
                    processLayers(graph,"Permutation", layer, (layer - 1),"down-top"); }
            else for (int layer = 1; layer <= numOfLayer - 1; layer++)
                processLayers(graph, "Permutation", layer, (layer + 1),"top-down");
        }
    }
    private static void processLayers(Graph graph, String algo, int indexFixed, int indexFree, String direction) {
        layerMap = graph.getLayerMap();
        if (VERBOSE) System.out.println("fixedLayer = " + indexFixed);
        if (VERBOSE) System.out.println("freeLayer = " + indexFree);
        LinkedList<GraphNode> fixedLayer = layerMap.get(indexFixed);
        LinkedList<GraphNode> freeLayer = layerMap.get(indexFree);

        int bestCrossings = Integer.MAX_VALUE;
        ArrayList<LinkedList<GraphNode>> ListOfAllPermutation;
        ListOfAllPermutation = graph.heapGenerate(freeLayer.size(), freeLayer, new ArrayList<>());
        if (VERBOSE) System.out.println("Checking " + ListOfAllPermutation.size() + " Permutations :)");
        for (LinkedList<GraphNode> permutationOfFreeLayer : ListOfAllPermutation) {
            int newCrossings = BLCC_naive(graph, fixedLayer, permutationOfFreeLayer, direction);
            if (VERBOSE && DEBUG) System.out.printf("for layer %d with order %s found %s crossings%n",indexFree,permutationOfFreeLayer,newCrossings);
            if (newCrossings < bestCrossings) {
                bestCrossings = newCrossings;
//                System.out.println(graph.getCrossings());
                if (direction.equals("top-down"))   graph.setCrossings("L" + indexFixed + "-L" + indexFree, bestCrossings );
                else graph.setCrossings("L" + indexFree + "-L" + indexFixed, bestCrossings );
                layerMap.put(indexFree, permutationOfFreeLayer);
                if (VERBOSE) System.out.println("neuer Bestwert!: " + bestCrossings + " Kreuzungen");
                if (bestCrossings == 0) break;
            }
        }
    }
    private static int BLCC_naive(Graph g, LinkedList<GraphNode> fixedLayer, LinkedList<GraphNode> freeLayer, String direction) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = g.getEdgesInMap();
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = g.getEdgesOutMap();

        int crossings = 0;
        for (int i = 0; i <= fixedLayer.size() - 1; i++) { // -2 because last node cannot account for more crossings
            GraphNode fixedNode = fixedLayer.get(i);
            int fixedLayerInt = fixedNode.getLayer();
            int freeLayerInt;
            if (direction.equals("top-down")) freeLayerInt = fixedNode.getLayer() + 1;
            else freeLayerInt = fixedNode.getLayer() - 1;
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

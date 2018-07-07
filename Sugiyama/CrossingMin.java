package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CrossingMin {
    private static boolean VERBOSE = false;
    private static boolean DEBUG = false;
    private static LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap;
    private static int iterations = 0;
    private static int minCrossings = 10000;
    private static int graphDepth = 1;

    public static void baryCenter_naive(Graph graph, boolean bidirectional, int sweeps) {
        layerMap = graph.getLayerMap();
        int numOfLayer = layerMap.keySet().size();

        for (int i = 0; i < sweeps; i++) {
            if (bidirectional) {
                for (int layer = 1; layer <= numOfLayer - 1; layer++)
                    processBaryCenter_naive(graph, layer, (layer + 1), "top-down");
                for (int layer = numOfLayer; layer > 1; layer--)
                    processBaryCenter_naive(graph, layer, (layer - 1), "down-top");
            } else for (int layer = 1; layer <= numOfLayer - 1; layer++)
                processBaryCenter_naive(graph, layer, (layer + 1), "top-down");
        }
    }

    private static void processBaryCenter_naive(Graph g, int indexFixed, int indexFree, String direction) {
        layerMap = g.getLayerMap();
        if (VERBOSE) System.out.println("fixedLayer = " + indexFixed);
        if (VERBOSE) System.out.println("freeLayer = " + indexFree);
        LinkedList<GraphNode> fixedLayer = layerMap.get(indexFixed);
        LinkedList<GraphNode> freeLayer = layerMap.get(indexFree);
        int bestCrossings = Integer.MAX_VALUE;

        for (GraphNode freeNode : freeLayer) {
            ArrayList<GraphNode> adjacentNodes = g.getAdjacentNodes(freeNode, indexFixed);
            double inDegree = adjacentNodes.size();
            double sumOfIndices = sumUpIndices(fixedLayer, adjacentNodes);
            freeNode.x_Bary = ((1 / inDegree) * sumOfIndices);
        }
        freeLayer.sort(Comparator.comparing(GraphNode::getX_Bary));
        // still open, check if ambigous position swap improve that situation
    }

    private static double sumUpIndices(LinkedList<GraphNode> layer, ArrayList<GraphNode> adjacentNodes) {
        double sum = 0.0;
        for (GraphNode graphNode : layer) {
            if (adjacentNodes.contains(graphNode)) {
                double indexOf = layer.indexOf(graphNode) + 1;
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
                    processLayers(graph, "Permutation", layer, (layer + 1), "top-down");
                for (int layer = numOfLayer; layer > 1; layer--)
                    processLayers(graph, "Permutation", layer, (layer - 1), "down-top");
            } else for (int layer = 1; layer <= numOfLayer - 1; layer++)
                processLayers(graph, "Permutation", layer, (layer + 1), "top-down");
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
            if (VERBOSE && DEBUG)
                System.out.printf("for layer %d with order %s found %s crossings%n", indexFree, permutationOfFreeLayer, newCrossings);
            if (newCrossings < bestCrossings) {
                bestCrossings = newCrossings;
//                System.out.println(graph.getCrossings());
                if (direction.equals("top-down"))
                    graph.setCrossings("L" + indexFixed + "-L" + indexFree, bestCrossings);
                else graph.setCrossings("L" + indexFree + "-L" + indexFixed, bestCrossings);
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
                for (int k = 0; k <= freeLayer.indexOf(adjToFixedNode) - 1; k++) {
                    GraphNode checkNode = freeLayer.get(k);
                    if (VERBOSE && DEBUG) System.out.println("checkNode = " + checkNode);
                    LinkedList<GraphNode> adjToCheckNode = new LinkedList<>();
                    if (edgesOut.containsKey(checkNode)) adjToCheckNode.addAll(g.getChildren(checkNode));
                    if (edgesIn.containsKey(checkNode)) adjToCheckNode.addAll(g.getParentsOf(checkNode));
                    adjToCheckNode.removeIf(node -> node.getLayer() != fixedLayerInt);
                    if (VERBOSE && DEBUG) System.out.println("adjToCheckNode = " + adjToCheckNode);
                    for (GraphNode particularNode : adjToCheckNode) {
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


/*    public class Bary {
        private BaryHelperGraph g0 = new BaryHelperGraph(), gTemp;
        private Map<GraphNode, LinkedList<GraphNode>> nodesAreConnected = new HashMap<>();
        private int count = 0;

        public  void barycenterAlgo(Graph graph) {
            Bary b = new Bary(graph);
        }

        public Bary(Graph graph) {
            nodesConnected(graph);
            g0.setLayerMap(graph.getLayerMap());

            List<Integer> keys = new ArrayList<>();
            keys.addAll(graph.getLayerMap().keySet());
            // key is level, value is bary matrix
            for (int i = 1; i < keys.size(); i++) {                                 // alle außer für letzten
                g0.setBaryMat(i, new BaryMatrix(i));
            }
            g0.calcCrossingsTotal();
            gTemp = g0.copy();

            SugiyamaPhase1Sweep(1, g0.getLayerMap().size(), 1);         //layer beginnen bei 1

            int graphDepth = gTemp.getLayerMap().size();
            for (int level = 1; level < graphDepth; level++) {    // zb lm size 6, geht von 1-5
                graph.getLayerMap().put(level, gTemp.getBaryMatOnLevel(level).rows);
            }
            graph.getLayerMap().put(graphDepth, gTemp.getBaryMatOnLevel(graphDepth - 1).columns);

            for (Integer integer : graph.getLayerMap().keySet()) {
                for (int i = 0; i < graph.getLayerMap().get(integer).size(); i++) {
                    graph.getLayerMap().get(integer).get(i).x = i;
                }
            }
            System.out.println("number of crossings total = " + gTemp.getCrossingsTotal());
        }


        // Down col : 1 bis n-1, Up row: n-1 bis 1
        private void SugiyamaPhase1Sweep(int start, int end, int step) {
            boolean stillProcessing = false;
            count++;

            for (int i = start; i != end; i += step) {
                if (i < gTemp.getLayerMap().size()) {
                    if (gTemp.getBaryMatOnLevel(i).orderByRow(i)) {
                        stillProcessing = true;
                    }
                }
            }
            for (int i = start; i != end; i += step) {                          // von 1 bis 6 bzw bis 5
                if (i < gTemp.getLayerMap().size()) {                            //layermap  1-6   barymat 1-5 = 1&2 2&3 3&4 4&5 5&6
                    if (gTemp.getBaryMatOnLevel(i).orderByColumn(i)) {
                        stillProcessing = true;
                    }
                }
            }
            if (stillProcessing && count < 101) {
                if (start < end) {                             // i am in p1 down
                    SugiyamaPhase2Sweep(start, end, step);  // goto p2 down
                }
                if (start > end) {                            // i am in p1 up
                    SugiyamaPhase1Sweep(end, start, step * -1);   // goto p1 down
                }
            }
        }


        private void SugiyamaPhase2Sweep(int start, int end, int step) {
            for (int i = start; i != end; i += step) {
                if (i < gTemp.getLayerMap().size()) {
                    gTemp.getBaryMatOnLevel(i).reverseRows(i);
                }
            }
            for (int i = start; i != end; i += step) {
                if (i < gTemp.getLayerMap().size()) {
                    gTemp.getBaryMatOnLevel(i).reverseColumns(i);
                }
            }

            if (count < 101) {
                SugiyamaPhase1Sweep(end, start, step * -1);
            }
        }

        private void nodesConnected(Graph graph) {
            for (Edge edge : graph.getEdges()) {
                GraphNode u = edge.getStart();
                GraphNode v = edge.getTarget();
                if (nodesAreConnected.get(u) != null) {
                    LinkedList<GraphNode> temp = (nodesAreConnected.get(u));
                    temp.add(v);
                    nodesAreConnected.put(u, temp);
                } else {
                    LinkedList<GraphNode> n = new LinkedList<>();
                    n.add(v);
                    nodesAreConnected.put(u, n);
                }
                if (nodesAreConnected.get(v) != null) {
                    LinkedList<GraphNode> temp = (nodesAreConnected.get(v));
                    temp.add(u);
                    nodesAreConnected.put(v, temp);
                } else {
                    LinkedList<GraphNode> n = new LinkedList<>();
                    n.add(u);
                    nodesAreConnected.put(v, n);
                }
            }
        }


        public class BaryMatrix {
            public LinkedList<GraphNode> rows = new LinkedList<>();
            public LinkedList<GraphNode> columns = new LinkedList<>();
            private int[][] matrix;
            private List<Double> rowBary = new ArrayList<>();
            private List<Double> columnBary = new ArrayList<>();
            private int matCrossings;

            BaryMatrix(int level) {
                rows.addAll(g0.getLayerMap().get(level));
                columns.addAll(g0.getLayerMap().get(level + 1));
                matrix = new int[rows.size()][columns.size()];
                calcMatBaryAndCross();
            }

            void calcMatBaryAndCross() {
                for (int i = 0; i < rows.size(); i++) { // calc mat
                    for (int j = 0; j < columns.size(); j++) {
                        if (nodesAreConnected.get(rows.get(i)).contains((columns.get(j)))
                                || nodesAreConnected.get(columns.get(j)).contains((rows.get(i)))) {
                            matrix[i][j] = 1;
                        } else {
                            matrix[i][j] = 0;
                        }
                    }
                }
                rowBary.clear();
                columnBary.clear();
                for (int k = 0; k < rows.size(); k++) {     // calcRowBarycenter
                    int sumcount = 0;
                    double tempBary = 0;
                    for (int l = 0; l < columns.size(); l++) {
                        int temp = matrix[k][l];
                        if (temp != 0) {
                            tempBary += (((l + 1.0) * matrix[k][l]));
                            sumcount++;
                        }
                    }
                    tempBary = tempBary / (double) sumcount;
                    rowBary.add(tempBary);
                }
                for (int l = 0; l < columns.size(); l++) {      // calcColumnBary
                    int sumcount = 0;
                    double tempBary = 0;
                    for (int k = 0; k < rows.size(); k++) {
                        int temp = matrix[k][l];
                        if (temp != 0) {
                            tempBary += (((k + 1.0) * matrix[k][l]));
                            sumcount++;
                        }
                    }
                    tempBary = tempBary / (double) sumcount;
                    columnBary.add(tempBary);
                }
                int crossings = 0;
                for (int j = 0; j < rows.size() - 1; j++) { // von 1 bis p-1
                    for (int k = j + 1; k < rows.size(); k++) { // von j+1 bis p
                        for (int a = 0; a < columns.size() - 1; a++) { // von 1 bis q-1
                            for (int b = a + 1; b < columns.size(); b++) { // von a+1 bis q
                                crossings += matrix[j][b] * matrix[k][a];
                            }
                        }
                    }
                }
                matCrossings = crossings;
            }

            public int getMatCrossings() {
                return this.matCrossings;
            }

            boolean orderByColumn(Integer level) {
                boolean hasChanged = true;      //  hier wird er immer wieder rein springen ? rekursiv aufrufen oder bool mitgeben?
                boolean neededOrdering = false;
                if (columnBary.size() > 1) {
                    while (hasChanged) {
                        hasChanged = false;
                        for (int i = 1; i < columnBary.size(); i++) {   // level für graph1 von 1 bis < 6
                     *//*   if (improvementCols(level, i)) {
                        }*//*
                            int prev = i - 1;
                            if (Double.compare(columnBary.get(i), columnBary.get(prev)) < 0) {
                                Collections.swap(columnBary, prev, i);
                                Collections.swap(columns, prev, i); //zugriff auf 0 und 1
                                hasChanged = true;
                                neededOrdering = true;
                                calcMatBaryAndCross();
                                gTemp.setBaryMat(level, this);
                                gTemp.setLayerInMap(level + 1, columns);

                                if (level < gTemp.getLayerMap().size() - 1) {  // es gibt level 1-6 in layermap
                                    // wenn auf 1-4 dann muss in nächster mat rows gesetzt werden
                                    // wenn schon in 5 dann gibt es keine mat mehr , da 5&6 in mat(5)
                                    gTemp.getBaryMatOnLevel(level + 1).rows = this.columns;
                                    gTemp.getBaryMatOnLevel(level + 1).calcMatBaryAndCross();
                                }
                                gTemp.calcCrossingsTotal();
                            }
                        }
                    }
                }
                return neededOrdering;
            }

            public boolean orderByRow(Integer level) {
                boolean hasChanged = true;
                boolean neededOrdering = false;
                if (rowBary.size() > 1) {
                    while (hasChanged) {
                        hasChanged = false;
                        for (int i = 1; i < rowBary.size(); i++) {
                            int prev = i - 1;
                            if (improvementRows(level, i)) {
                            }
                            if (Double.compare(rowBary.get(i), rowBary.get(prev)) < 0) {
                                Collections.swap(rowBary, prev, i);
                                Collections.swap(rows, prev, i);
                                hasChanged = true;
                                neededOrdering = true;
                                calcMatBaryAndCross();
                                gTemp.setBaryMat(level, this);
                                gTemp.setLayerInMap(level, rows);
                                if (level > 1) {
                                    gTemp.getBaryMatOnLevel(level - 1).columns = this.rows;
                                    gTemp.getBaryMatOnLevel(level - 1).calcMatBaryAndCross();
                                }
                                gTemp.calcCrossingsTotal();
                            }
                        }
                    }
                }
                return neededOrdering;
            }


            void reverseRows(int level) {
                if (rows.size() > 1) {
                    for (int i = 1; i < rowBary.size(); i++) {
                        int prev = i - 1;
                        if (improvementRows(level, i)) {
                            if (Double.compare(rowBary.get(i), rowBary.get(prev)) == 0) {
                                Collections.swap(rowBary, i, prev);
                                Collections.swap(rows, i, prev);
                                calcMatBaryAndCross();
                                gTemp.setBaryMat(level, this);
                                gTemp.setLayerInMap(level, rows);
                                if (level > 1) {
                                    gTemp.getBaryMatOnLevel(level - 1).columns = this.rows;
                                    gTemp.getBaryMatOnLevel(level - 1).calcMatBaryAndCross();
                                }
                                gTemp.calcCrossingsTotal();
                            }
                        }
                    }
                }
            }

            public void reverseColumns(int level) {
                if (columns.size() > 1) {
                    for (int i = 1; i < columnBary.size(); i++) {
                        int prev = i - 1;
                        if (improvementCols(level, i)) {


                            if (Double.compare(columnBary.get(i), columnBary.get(prev)) == 0) {
                                Collections.swap(columnBary, i, prev);
                                Collections.swap(columns, i, prev);

                                calcMatBaryAndCross();
                                gTemp.setBaryMat(level, this);
                                gTemp.setLayerInMap(level + 1, columns);

                                if (level < gTemp.getLayerMap().size() - 1) {
                                    gTemp.getBaryMatOnLevel(level + 1).rows = this.columns;
                                    gTemp.getBaryMatOnLevel(level + 1).calcMatBaryAndCross();
                                }
                                gTemp.calcCrossingsTotal();
                            }
                        }
                    }
                }
            }

            boolean improvementRows(int level, int i) {
                BaryHelperGraph gHelp = gTemp.copy();
                BaryMatrix mHelp = gHelp.getBaryMatOnLevel(level);

                if (Double.compare(mHelp.rowBary.get(i), mHelp.rowBary.get(i - 1)) == 0) {
                    Collections.swap(mHelp.rowBary, i, i - 1);
                    Collections.swap(mHelp.rows, i, i - 1);
                }
                mHelp.calcMatBaryAndCross();
                gHelp.setBaryMat(level, mHelp);
                gHelp.setLayerInMap(level, mHelp.rows);
                if (level > 1) {
                    gHelp.getBaryMatOnLevel(level - 1).columns = mHelp.rows;
                    gHelp.getBaryMatOnLevel(level - 1).calcMatBaryAndCross();
                }
                gHelp.calcCrossingsTotal();
                return (gHelp.getCrossingsTotal() < gTemp.getCrossingsTotal());
            }

            boolean improvementCols(int level, int i) {
                BaryHelperGraph gHelper = gTemp.copy();
                BaryMatrix mHelper = gHelper.getBaryMatOnLevel(level);

                if (Double.compare(mHelper.columnBary.get(i), mHelper.columnBary.get(i - 1)) == 0) {
                    Collections.swap(mHelper.columnBary, i, i - 1);
                    Collections.swap(mHelper.columns, i, i - 1);
                }
                mHelper.calcMatBaryAndCross();
                gHelper.setBaryMat(level, mHelper);
                gHelper.setLayerInMap(level, mHelper.columns);
                if (level < gHelper.getLayerMap().size() - 1) {
                    gHelper.getBaryMatOnLevel(level + 1).rows = mHelper.columns;
                    gHelper.getBaryMatOnLevel(level + 1).calcMatBaryAndCross();
                }
                gHelper.calcCrossingsTotal();
                return (gHelper.getCrossingsTotal() < gTemp.getCrossingsTotal());
            }


        }


    }*/

}
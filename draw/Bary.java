package draw;

import model.Edge;
import model.Graph;
import model.GraphNode;

import java.util.*;

public class Bary {
    private BaryHelperGraph g0 = new BaryHelperGraph(), gTemp;
    private Map<GraphNode, LinkedList<GraphNode>> nodesAreConnected = new HashMap<>();
    private int count = 0;

    public static void barycenterAlgo(Graph graph) {
        Bary b = new Bary(graph);
    }

    public Bary(Graph graph) {
        nodesConnected(graph);
        g0.setLayerMap(graph.getLayerMap());

        List<Integer> keys = new ArrayList<>();
        for (Integer integer : graph.getLayerMap().keySet()) {
            keys.add(integer);
        }
        // key is level, value is bary matrix
        for (int i = 1; i < keys.size(); i++) {                                 // alle außer für letzten
            g0.setBaryMat(i, new BaryMatrix(i));
        }
        g0.calcCrossingsTotal();
        gTemp = g0.copy();

        SugiyamaPhase1Sweep(1, g0.getLayerMap().size(), 1);         //layer beginnen bei 1


        for (Integer integer : gTemp.getLayerMap().keySet()) {
            graph.getLayerMap().put(integer, gTemp.getLayerMap().get(integer));
            for (int i = 0; i < graph.getLayerMap().get(integer).size(); i++) {
                graph.getLayerMap().get(integer).get(i).x = i;
            }

            /*for (int i = 1; i < gTemp.getLayerMap().size(); i++) {
                System.out.println("i = " + i);
                for (Double aDouble : gTemp.getBaryMatOnLevel(i).rowBary) {
                    System.out.println("rowbary = " + aDouble);
                }
                for (Double aDouble : gTemp.getBaryMatOnLevel(i).columnBary) {
                    System.out.println("colbary = " + aDouble);
                }
            }*/
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
        if (stillProcessing & count < 101) {
            SugiyamaPhase1Sweep(end, start, step * -1); // wenn von 1up kommend --> 1 down
        }
      /*  else{
            SugiyamaPhase2Sweep(start, end, step);
        }*/
    }


    private void SugiyamaPhase2Sweep(int start, int end, int step) {

        for (int i = start; i != end; i += step) {
            if (i < gTemp.getLayerMap().size()) {
                gTemp.getBaryMatOnLevel(i).reverseRows(i, gTemp);
            }
        }

        if (count < 100) {
            SugiyamaPhase1Sweep(end, start, step * -1);
        }
    }

    private void nodesConnected(Graph graph) {
        for (Iterator<Edge> iterator = graph.getEdges().iterator(); iterator.hasNext(); ) {
            Edge edge = iterator.next();
            GraphNode u = (GraphNode) edge.getStart();
            GraphNode v = (GraphNode) edge.getTarget();
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


    class BaryMatrix {
        private LinkedList<GraphNode> rows = new LinkedList<>();
        private LinkedList<GraphNode> columns = new LinkedList<>();
        private int[][] matrix;
        private List<Double> rowBary = new ArrayList<>();
        private List<Double> columnBary = new ArrayList<>();
        private int matCrossings;

        public BaryMatrix(int level) {
            for (GraphNode g : g0.getLayerMap().get(level)) {
                rows.add(g);
            }
            for (GraphNode g : g0.getLayerMap().get(level + 1)) {
                columns.add(g);
            }
            matrix = new int[rows.size()][columns.size()];
            calcMatBaryAndCross();
        }

        public void calcMatBaryAndCross() {
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

        public boolean orderByColumn(Integer level) {
            boolean hasChanged = true;      //  hier wird er immer wieder rein springen ? rekursiv aufrufen oder bool mitgeben?
            boolean neededOrdering = false;
            if (columnBary.size() > 1) {
                while (hasChanged) {
                    hasChanged = false;
                    for (int i = 1; i < columnBary.size(); i++) {   // level für graph1 von 1 bis < 6
                        int prev = i - 1;
                        if (Double.compare(columnBary.get(i), columnBary.get(prev)) < 0) {
                            Collections.swap(columnBary, prev, i);
                            Collections.swap(columns, prev, i); //zugriff auf 0 und 1
                            hasChanged = true;
                            neededOrdering = true;
                        }
                    }
                }
                if (neededOrdering) {   // irgendwelche neuberechnungen vergessen?
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
            return neededOrdering;
        }

        public boolean orderByRow(Integer level) {
            int crossBefore = gTemp.getCrossingsTotal();
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
                            Collections.swap(rowBary, i, prev);
                            Collections.swap(rows, i, prev);
                            hasChanged = true;
                            neededOrdering = true;
                        }
                    }
                }
                if (neededOrdering) {
                    calcMatBaryAndCross();
                    gTemp.setBaryMat(level, this);
                    gTemp.setLayerInMap(level, rows);
                    if (level > 1) {
                        gTemp.getBaryMatOnLevel(level - 1).columns = this.rows;
                        gTemp.getBaryMatOnLevel(level - 1).calcMatBaryAndCross();
                    }
                    gTemp.calcCrossingsTotal();

                    if (!(gTemp.getCrossingsTotal() < crossBefore)) {
                        // alles rückgängig machen
                        // wie??? neue funktion mit hilfsgraphen? weil sonst sind i und prev unbek


                        neededOrdering = false;
                    }
                }
            }

            // vielleicht einfach schauen ob es besser wird und wenn nicht alles rückgängig machen?


            return neededOrdering;
        }


        /*public boolean orderRowImproves(int i, int level) {
            boolean changed = false;
            BaryHelperGraph gHelp = gTemp.copy();
            if (Double.compare(gHelp.getBaryMatOnLevel(i).rowBary.get(i), gHelp.getBaryMatOnLevel(i).rowBary.get(i - 1)) < 0) {
                Collections.swap(gHelp.getBaryMatOnLevel(i).rowBary, i, i - 1);
                Collections.swap(gHelp.getBaryMatOnLevel(i).rows, i, i - 1);
                changed = true;
            }
            if (changed) {
                gHelp.getBaryMatOnLevel(i).calcMatBaryAndCross();
                gHelp.setBaryMat(level, gHelp.getBaryMatOnLevel(i));
                gHelp.setLayerInMap(level, gHelp.getBaryMatOnLevel(i).rows);
                if (level > 1) {
                    gHelp.getBaryMatOnLevel(level - 1).columns = gHelp.getBaryMatOnLevel(i).rows;
                    gHelp.getBaryMatOnLevel(level - 1).calcMatBaryAndCross();
                }
                gHelp.calcCrossingsTotal();
            }
            return (gHelp.getCrossingsTotal() < gTemp.getCrossingsTotal());
        }*/

        public void reverseRows(int level, BaryHelperGraph gTemp) {
            if (rows.size() > 1) {
                for (int i = 1; i < rowBary.size(); i++) {
                    int prev = i - 1;
                    if (improvementRows(level, i)) {
                        if (Double.compare(rowBary.get(i), rowBary.get(prev)) == 0) {
                            Collections.swap(rowBary, i, prev);
                            Collections.swap(rows, i, prev);
                        }
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

        public boolean improvementRows(int level, int i) {
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


        public void reverseColumns() {

        }


    }


}




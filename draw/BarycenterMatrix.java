package draw;

import model.Graph;
import model.Edge;
import model.GraphNode;

import java.util.*;

public class BarycenterMatrix {
    private Graph graph;
    private Map<GraphNode, LinkedList<GraphNode>> nodesAreConnected = new HashMap<>();
    private List<GraphNode> rows = new ArrayList<>();
    private List<GraphNode> columns = new ArrayList<>();
    private int[][] matrix;
    private List<Double> rowBary = new ArrayList<>();
    private List<Double> columnBary = new ArrayList<>();
    private String upDown;


    public BarycenterMatrix(Graph graphInput, int level, String updown) {
        this.graph= graphInput;
        this.upDown= updown;
        int order = 0;

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

        for(GraphNode g: graph.getLayerMap().get(level)){
            g.x = order;
            rows.add(g);
            order++;
        }
        order=0;

        switch (upDown){
            case("down"):
                for(GraphNode g: graph.getLayerMap().get(level+1)){
                    g.x = order;
                    columns.add(g);
                    order++;
                }
                break;

            case("up"):
                for(GraphNode g: graph.getLayerMap().get(level-1)){
                    g.x = order;
                    columns.add(g);
                    order++;
                }
                break;
        }
        System.out.println("level = " + level+ " , direction: = " + updown
                + " ,rows.size() = " + rows.size()+" ,columns.size() = " + columns.size());

        matrix = new int[rows.size()][columns.size()];
        calcMatrixAndBarys(upDown);
    }


    // level beachten!! down: row i , col i+1     // up: row i, col i-1
    private void calcMatrixAndBarys(String updown) {
        rowBary.clear();
        columnBary.clear();
        switch (updown){
            case("down"):
                for (int i = 0; i < rows.size(); i++) {
                    for (int j = 0; j < columns.size(); j++) {
                        if (nodesAreConnected.get(rows.get(i)).contains((columns.get(j)))
                                || nodesAreConnected.get(columns.get(j)).contains((rows.get(i)))) {
                            matrix[i][j] = 1;
                        } else {
                            matrix[i][j] = 0;
                        }
                    }
                } break;
            case("up"):
                for (int i = 0; i < rows.size(); i++) {
                    for (int j = 0; j < columns.size(); j++) {
                        if (nodesAreConnected.get(rows.get(i)).contains((columns.get(j)))
                                || nodesAreConnected.get(columns.get(j)).contains((rows.get(i)))) {
                            matrix[i][j] = 1;
                            //System.out.println("set 1");
                        } else {
                            matrix[i][j] = 0;
                            //System.out.println("set 0");
                        }
                    }
                }break;
        }

        for (int k = 0; k < rows.size(); k++) {     // calcRowBarycenter
            int sumcount=0;
            double tempBary = 0;
            for (int l = 0; l < columns.size(); l++) {
                int temp = matrix[k][l];
                if (temp != 0) {
                    tempBary += (((l + 1.0) * matrix[k][l]));
                    sumcount ++;
                }
            }
            tempBary= tempBary/ (double) sumcount;
            rowBary.add(tempBary);
            System.out.println("row no: " + k + " rowbary = " + tempBary);
        }
        for (int l = 0; l < columns.size(); l++) {      // calcColumnBary
            int sumcount=0;
            double tempBary = 0;
            for (int k = 0; k < rows.size(); k++) {
                int temp = matrix[k][l];
                if (temp != 0) {
                    tempBary += (((k + 1.0) * matrix[k][l]));
                    sumcount++;
                }
            }
            tempBary= tempBary / (double)sumcount;
            columnBary.add(tempBary);
            System.out.println(" col no: " + l + " colbary = " + tempBary);
        }
    }


    // p= anzahl knoten auf level i, q= anz knoten auf level i+1
    public int getCrossings() {
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
        return crossings;
    }


    public void orderByRow() {              // order bary ascending, for nodes only change x coord
        if(rowBary.size() >1){
            boolean change = true;
            while (change) {
                change = false;
                for (int i = 1; i < rowBary.size(); i++) {// bis < size oder <= ?
                    int prev = i - 1;
                    System.out.println("rowBary size= " + rowBary.size());
                    System.out.println("row = "+ i +" rowB = " + rowBary.get(i)+ " rowb prev   "+ rowBary.get(prev));

                        if(Double.compare(rowBary.get(i), rowBary.get(prev)) < 0 ) {
                        Collections.swap(rowBary, i, prev);
                        rows.get(i).x = prev;
                        rows.get(prev).x = i;
                        Collections.swap(rows, i, prev);
                        change = true;
                        System.out.println("swapped");
                    }
                }
            }
            calcMatrixAndBarys(upDown);                   // alles neu berech
        }   System.out.println("orderByrow done");
    }

    public void orderByColumn() {
        if (columnBary.size()>1){
            boolean change = true;
            while (change) {
                change = false;
                for (int i = 1; i < columns.size(); i++) {
                    int prev = i - 1;
                    System.out.println("colb = " + columnBary.get(i)+ " colb prev   "+ columnBary.get(prev));

                    if (Double.compare(columnBary.get(i),columnBary.get(prev)) <0 ) {
                        Collections.swap(columnBary, i, prev);
                        columns.get(i).x = prev;
                        columns.get(prev).x = i;
                        Collections.swap(columns, i, prev);
                        change = true;
                        System.out.println("swapped");
                    }
                }
            }
            calcMatrixAndBarys(upDown);
        }   System.out.println("order by col done");
    }


    // REVERSION: reorder rows/ cols with equal barycenters
    // noch nicht sicher ob das so stimmt
    public void reverseRows() {
        if(rows.size()>1){
            for (int i = 1; i < rowBary.size(); i++) {
                int prev = i - 1;
                if (Double.compare(rowBary.get(i) , rowBary.get(prev)) == 0) {
                    Collections.swap(rowBary, i, prev);
                    rows.get(i).x = prev;
                    rows.get(prev).x = i;
                    Collections.swap(rows, i, prev);
                }
            }
        } System.out.println("reverse row done");
        calcMatrixAndBarys(upDown);
    }

    public void reverseColumns() {
        if(columns.size()>1){
            for (int i = 1; i < columns.size(); i++) {
                int prev = i - 1;
                if (Double.compare(columnBary.get(i), columnBary.get(prev)) == 0) {
                    Collections.swap(columnBary, i, prev);
                    columns.get(i).x = prev;
                    columns.get(prev).x = i;
                    Collections.swap(columns, i, prev);
                }
            }
        }  System.out.println("reverse col done");
        calcMatrixAndBarys(upDown);
    }


    public boolean rowsAreIncreasing() {
        for (int i = 1; i < rowBary.size(); i++) {
            int prev = i - 1;
            if (rowBary.get(i) < rowBary.get(prev)) {
                return false;
            }
        }
        return true;
    }

    public boolean columnsAreIncreasing() {
        for (int i = 1; i < columnBary.size(); i++) {
            int prev = i - 1;
            if (columnBary.get(i) < columnBary.get(prev)) {
                return false;
            }
        }
        return true;
    }

  //equals funktion schreiben um matrizen auf gleichheit zu prüfen?
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BarycenterMatrix)) return false;

        BarycenterMatrix mat = (BarycenterMatrix) obj;

        // vergleichen ob die x coord der knoten gleich

     if(rows.size() != ((BarycenterMatrix) obj).rows.size()
                || columns.size() != ((BarycenterMatrix) obj).columns.size()){
            return false;
        }
        for(int i=0; i<rows.size();i++){
            if(! (rows.get(i).x == (((BarycenterMatrix) obj).rows.get(i)).x)) return false;
            if (!rowBary.get(i).equals(((BarycenterMatrix) obj).rowBary.get(i) )) return false;
        }
        for(int i=0; i<columns.size();i++) {
            if (!(columns.get(i).x == (((BarycenterMatrix) obj).columns.get(i)).x)) return false;
            if (!columnBary.get(i).equals(((BarycenterMatrix) obj).columnBary.get(i))) return false;
        }
        return true;
    }



/*    public List<Double> getRowBarys() {
        return rowBary;
    }
    public List<Double> getColumnBarys() {
        return columnBary;
    }*/
}
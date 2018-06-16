package draw;

import model.DrawableGraph;
import model.GraphNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BarycenterMatrix {
    private List<GraphNode> rows = new ArrayList<>();
    private List<GraphNode> columns = new ArrayList<>();
    private int[][] matrix;
    private List<Double> rowBary = new ArrayList<>();
    private List<Double> columnBary = new ArrayList<>();


    public BarycenterMatrix(DrawableGraph graph, int level) {
        int order1 = 0, order2 = 0;
        for (GraphNode g : graph.copyNodeSet()) {
            if (g.level == level) {
                g.setOrder(order1);
                rows.add(g);
                order1++;
            } else if (g.level == (level + 1)) {
                g.setOrder(order2);
                columns.add(g);
                order2++;
            }
        }

        matrix = new int[rows.size()][columns.size()];
        calcMatrixAndBarys();
    }

    private void calcMatrixAndBarys() {
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                if (rows.get(i).getChildren().contains(columns.get(j))) {
                    matrix[i][j] = 1;
                } // else matrix entry= 0
            }
        }
        for (int k = 0; k < rows.size(); k++) {     // calcRowBarycenter
            double tempBary = 0;
            for (int l = 0; l < columns.size(); l++) {
                tempBary += (((l + 1) * matrix[k][l]) / matrix[k][l]);
            }
            rowBary.add(tempBary);
        }
        for (int l = 0; l < columns.size(); l++) {      // calcColumnBary
            double tempBary = 0;
            for (int k = 0; k < rows.size(); k++) {
                tempBary += (((k + 1) * matrix[k][l]) / matrix[k][l]);
            }
            columnBary.add(tempBary);
        }
    }


    // Checken: stimmt das so? 1 bis p-1 ....
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


    public void orderByRow() {              // order bary ascending, for nodes only change "order"
        boolean change = true;
        while (change) {
            change = false;
            for (int i = 1; i < rowBary.size(); i++) {// bis < size oder <= ?
                int prev = i - 1;
                if (rowBary.get(i) < rowBary.get(prev)) {
                    Collections.swap(rowBary, i, prev);
                    rows.get(i).setOrder(prev);
                    rows.get(prev).setOrder(i);
                    Collections.swap(rows, i, prev);
                    change = true;
                }
            }
        }
        calcMatrixAndBarys();                   // alles neu berechnen
    }

    public void orderByColumn() {
        boolean change=true;
        while (change){
            change=false;
            for(int i=1; i<columns.size(); i++){
                int prev= i-1;
                if(columnBary.get(i)< columnBary.get(prev)){
                    Collections.swap(columnBary, i, prev);
                    columns.get(i).setOrder(prev);
                    columns.get(prev).setOrder(i);
                    Collections.swap(columns, i, prev);
                    change= true;
                }
            }
        }
        calcMatrixAndBarys();
    }


    // REVERSION: reorder rows/ cols with equal barycenters
    // noch nicht sicher ob das so stimmt
    public void reverseRows() {
        for (int i = 1; i < rowBary.size(); i++) {// bis < size oder <= ?
            int prev = i - 1;
            if (rowBary.get(i) == rowBary.get(prev)) {
                Collections.swap(rowBary, i, prev);
                rows.get(i).setOrder(prev);
                rows.get(prev).setOrder(i);
                Collections.swap(rows, i, prev);
            }
        }
        calcMatrixAndBarys();
    }

    public void reverseColumns() {
        for(int i=1; i<columns.size(); i++){
            int prev= i-1;
            if(columnBary.get(i) == columnBary.get(prev)){      // klappt == bei double?
                Collections.swap(columnBary, i, prev);
                columns.get(i).setOrder(prev);
                columns.get(prev).setOrder(i);
                Collections.swap(columns, i, prev);
            }
        }
        calcMatrixAndBarys();
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
/*    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }*/

/*    public List<Double> getRowBarys() {
        return rowBary;
    }

    public List<Double> getColumnBarys() {
        return columnBary;
    }*/
}

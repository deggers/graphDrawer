package draw;

import model.Graph;
import model.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class BarycenterMatrix {
    private List<GraphNode> rows = new ArrayList<>();
    private List<GraphNode> columns = new ArrayList<>();
    private int[][] matrix;
    private List<Double> rowBary = new ArrayList<>();
    private List<Double> columnBary = new ArrayList<>();


    public BarycenterMatrix(Graph graph, int level) {
        for (GraphNode g : graph.copyNodeSet()) {
            if (g.level == level) {
                rows.add(g);
            } else if (g.level == level + 1) {
                columns.add(g);
            }
        }

        matrix = new int[rows.size()][columns.size()];
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
//                if (rows.get(i).isChild(columns.get(j))) {
//                    matrix[i][j] = 1;
//                }
            }
        }
    }

    // p= anzahl knoten auf level i, q= anz knoten auf level i+1
    public int getCrossings() {
        int crossings=0;
        for (int j = 1; j < rows.size(); j++) { // von 1 bis p-1
            for (int k = j + 1; k <= rows.size(); k++) { // von j+1 bis p
                for( int a= 1; a < columns.size(); a++){ // von 1 bis q-1
                    for( int b= a+1; b<= columns.size(); b++){ // von a+1 bis q
                        crossings += matrix[j][b]*matrix[k][a];
                    }
                }
            }
        }
        return crossings; // klappt das? wird das hochgezählt?
    }

    // Methoden noch schreiben!
    // vielleicht sowas wie bubble sort ? if bary kleiner dann tausche order
    public BarycenterMatrix orderByRow(){
        // for each row calc bary, compare, smallest to front
        return this;
    }

    public BarycenterMatrix orderByColumn(){
        return this;
    }

    public BarycenterMatrix reverseRows(){
        return this;

    }

    public BarycenterMatrix reverseColumns(){
        return this;

    }


    public double calcRowBarycenter(){
        return 0.0;
    }

    public double calcColumnBarycenter(){
        return 0.0;
    }

    public boolean rowsAreIncreasing(){
        return false;
    }

    public boolean columnsAreIncreasing(){
        return false;
    }

    //equals funktion schreiben um matrizen auf gleichheit zu prüfen?
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

/*    public List<Double> getRowBarys() {
        return rowBary;
    }

    public List<Double> getColumnBarys() {
        return columnBary;
    }*/
}

package structure;
import java.util.*;

public class BarycenterMatrix {
    private Map<GraphNode, LinkedList<GraphNode>> nodesAreConnected = new HashMap<>();
    private LinkedList<GraphNode> rows = new LinkedList<>();
    private LinkedList<GraphNode> columns = new LinkedList<>();
    private int[][] matrix;
    private List<Double> rowBary = new ArrayList<>();
    private List<Double> columnBary = new ArrayList<>();
    private String upDown;

    private BarycenterMatrix() {
    }

    public BarycenterMatrix(Graph graphInput, int level, String updown) {
        this.upDown = updown;

        for (Iterator<Edge> iterator = graphInput.getEdges().iterator(); iterator.hasNext(); ) {
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

        int order = 0;
        for (GraphNode g : graphInput.getLayerMap().get(level)) {
            g.x = order;
            rows.add(g);
            order++;
        }
        order = 0;

        switch (upDown) {
            case ("down"):
                for (GraphNode g : graphInput.getLayerMap().get(level + 1)) {
                    g.x = order;
                    columns.add(g);
                    order++;
                }
                break;

            case ("up"):
                for (GraphNode g : graphInput.getLayerMap().get(level - 1)) {
                    g.x = order;
                    columns.add(g);
                    order++;
                }
                break;
        }
        //System.out.println("level = " + level+ " , direction: = " + updown
        //       + " ,rows.size() = " + rows.size()+" ,columns.size() = " + columns.size());

        matrix = new int[rows.size()][columns.size()];
        calcMatrixAndBarys(upDown);
    }


    // level beachten!! down: row i , col i+1     // up: row i, col i-1
    private void calcMatrixAndBarys(String updown) {
        rowBary.clear();
        columnBary.clear();
        switch (updown) {
            case ("down"):
                for (int i = 0; i < rows.size(); i++) {
                    for (int j = 0; j < columns.size(); j++) {
                        if (nodesAreConnected.get(rows.get(i)).contains((columns.get(j)))
                                || nodesAreConnected.get(columns.get(j)).contains((rows.get(i)))) {
                            matrix[i][j] = 1;
                        } else {
                            matrix[i][j] = 0;
                        }
                    }
                }
                break;
            case ("up"):
                for (int i = 0; i < rows.size(); i++) {
                    for (int j = 0; j < columns.size(); j++) {
                        if (nodesAreConnected.get(rows.get(i)).contains((columns.get(j)))
                                || nodesAreConnected.get(columns.get(j)).contains((rows.get(i)))) {
                            matrix[i][j] = 1;              //System.out.println("set 1");
                        } else {
                            matrix[i][j] = 0;              //System.out.println("set 0");
                        }
                    }
                }
                break;
        }

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
            //System.out.println("row no: " + k + " rowbary = " + tempBary);
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
        String matrixent = "";
        for (int row = 0; row < rows.size(); row++) {
            matrixent += "\n";
            for (int col = 0; col < columns.size(); col++) {
                matrixent += matrix[row][col];
                matrixent += "\t";
            }
        }
        System.out.println("matrixent = \n" + matrixent);

        String barys = "";
        for (Double aDouble : rowBary) {
            barys += aDouble;
            barys += " ";
        }
        for (Double aDouble : columnBary) {
            barys += aDouble;
            barys += " ";
        }
        System.out.println("barys = " + barys);
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
        if (rowBary.size() > 1) {
            // boolean change = true;
            for(int iter=0; iter<100; iter++){
                // change = false;
                for (int i = 1; i < rowBary.size(); i++) {
                    int prev = i - 1;

                    if (Double.compare(rowBary.get(i), rowBary.get(prev)) < 0) {
                        Collections.swap(rowBary, i, prev);
                        //System.out.println("rows.get(i).x =" + rows.get(i) + " gets x coor= " + prev);
                        // rows.get(i).x = prev;
                        //System.out.println("rows.get(prev).x =" + rows.get(prev) + " gets x coor= " + i);
                        // rows.get(prev).x = i;
                        Collections.swap(rows, i, prev);
                        // change = true;
                        //System.out.println("swapped");
                    }
                }
            }
            calcMatrixAndBarys(upDown);                   // alles neu berech
        }   //System.out.println("orderByrow done");
    }

    public void orderByColumn() {
        if (columnBary.size() > 1) {
           /* boolean change = true;
            while (change)*/ for(int iter=0; iter<100; iter++) {
                //change = false;
                for (int i = 1; i < columns.size(); i++) {
                    int prev = i - 1;
                    //System.out.println("colb = " + columnBary.get(i)+ " colb prev   "+ columnBary.get(prev));

                    if (Double.compare(columnBary.get(i), columnBary.get(prev)) < 0) {
                        Collections.swap(columnBary, i, prev);
                        // System.out.println("rcol.get(i).x =" + columns.get(i) + " gets x coor= " + prev);
                        // columns.get(i).x = prev;
                        //System.out.println("cols.get(prev).x =" + columns.get(prev) + " gets x coor= " + i);
                        //columns.get(prev).x = i;
                        Collections.swap(columns, i, prev);
                        //  change = true;
                        //System.out.println("swapped");
                    }
                }
            }
            calcMatrixAndBarys(upDown);
        }   //System.out.println("order by col done");
    }


    // REVERSION: reorder rows/ cols with equal barycenters
    // noch nicht sicher ob das so stimmt
    public void reverseRows() {
        if (rows.size() > 1) {
            for (int i = 1; i < rowBary.size(); i++) {
                int prev = i - 1;
                if (Double.compare(rowBary.get(i), rowBary.get(prev)) == 0) {
                    Collections.swap(rowBary, i, prev);
                  /*  rows.get(i).x = prev;
                    rows.get(prev).x = i;*/
                    Collections.swap(rows, i, prev);
                }
            }
        } //System.out.println("reverse row done");
        calcMatrixAndBarys(upDown);
    }

    public void reverseColumns() {
        if (columns.size() > 1) {
            for (int i = 1; i < columns.size(); i++) {
                int prev = i - 1;
                if (Double.compare(columnBary.get(i), columnBary.get(prev)) == 0) {
                    Collections.swap(columnBary, i, prev);
                    /*columns.get(i).x = prev;
                    columns.get(prev).x = i;*/
                    Collections.swap(columns, i, prev);
                }
            }
        }  //System.out.println("reverse col done");
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


    public BarycenterMatrix copy() {
        BarycenterMatrix other = new BarycenterMatrix();
        other.columns = new LinkedList<>(this.columns);
        other.rows = new LinkedList<>(this.rows);
        other.nodesAreConnected = new HashMap<>(this.nodesAreConnected);
        other.upDown = this.upDown;
        other.rowBary = new ArrayList<>(this.rowBary);
        other.columnBary = new ArrayList<>(this.columnBary);
        other.matrix = new int[rows.size()][columns.size()];
        for (int row = 0; row < rows.size(); row++) {
            for (int col = 0; col < columns.size(); col++) {
                other.matrix[row][col] = matrix[row][col];
            }
        }
        return other;
    }

    //equals funktion schreiben um matrizen auf gleichheit zu prüfen?
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BarycenterMatrix)) return false;

        BarycenterMatrix mat = (BarycenterMatrix) obj;

        if (rows.size() != mat.rows.size()
                || columns.size() != mat.columns.size()) {
            return false;
        }

        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i) != mat.getRows().get(i)) {
                return false;
            }
        }

        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i) != mat.getColumns().get(i)) {
                return false;
            }
        }
        return true;
    }

    public LinkedList<GraphNode> getRows() {
        return rows;
    }

    public LinkedList<GraphNode> getColumns() {
        return columns;
    }
    /*    public List<Double> getRowBarys() {
        return rowBary;
    }
    public List<Double> getColumnBarys() {
        return columnBary;
    }*/
}
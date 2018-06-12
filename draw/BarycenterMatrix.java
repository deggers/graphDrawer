package draw;

import model.DrawableGraph;
import model.GraphNode;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BarycenterMatrix {
    private List<GraphNode> row = new ArrayList<>();
    private List<GraphNode> columns = new ArrayList<>();
    private int[][] matrix;

    public BarycenterMatrix(DrawableGraph graph, int level) {
        for (GraphNode g : graph.nodeList) {
            if (g.level == level) {
                row.add(g);
            } else if (g.level == level + 1) {
                columns.add(g);
            }
        }

        matrix = new int[row.size()][columns.size()];
        for (int i = 0; i < row.size(); i++) {
            for(int j=0; j<columns.size();j++){
                if(row.get(i).isChild(columns.get(j))){
                    matrix[i][j] =1;
                }
            }
        }
    }
}

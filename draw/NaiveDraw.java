package draw;

import controller.GUIController;
import model.*;

public class NaiveDraw {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) throws Exception {
        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        drawableGraph drawGraph = new drawableGraph(theGraph,selectedEdgeType);
        System.out.println("drawGraph got " + drawGraph);

//      1. Remove all Cycles
        CycleBreaker.DFS_Florian(drawGraph);
        System.out.println("drawGraph got " + drawGraph);

//      2.  Layer Assignment: Vertices are assigned to layers.
//        AssignLayer.topologicalPath(drawGraph);


      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

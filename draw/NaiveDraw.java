package draw;

import controller.GUIController;
import model.*;

public class NaiveDraw {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) throws Exception {
        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        System.out.println("selectedEdgeType = " + selectedEdgeType);
        drawableGraph drawGraph = new drawableGraph(theGraph,selectedEdgeType);
//        System.out.println("drawGraph got " + drawGraph);

//      1. Remove all Cycles
//        System.out.println("remove all cycles");
        System.out.println("GreedyCycleRemoval");
        CycleBreaker.GreedyCycleRemoval(drawGraph);
        System.out.println("BergerShor");
        CycleBreaker.Berger_Shor(drawGraph);
//        System.out.println("DFS");
//        CycleBreaker.DFS_Florian(drawGraph);




//      2.  Layer Assignment: Vertices are assigned to layers.
//        System.out.println("layer assignment");
//        System.out.println("drawGraph before " + drawGraph);
//        AssignLayer.topologicalPath(drawGraph);
//        System.out.println("drawGraph after  " + drawGraph);

      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

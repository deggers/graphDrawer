package draw;

import controller.GUIController;
import model.*;

public class NaiveDraw {
    private static final boolean verbose = false;

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) throws Exception {
        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        drawableGraph drawGraph = new drawableGraph(theGraph,selectedEdgeType);
        System.out.println("drawGraph got " + drawGraph);

//      1. Remove all Cycles
        System.out.println("remove all cycles");
        CycleBreaker.DFS_Florian(drawGraph);
        System.out.println("cyclefree got " + drawGraph);
        if (verbose) {
            System.out.println(drawGraph);
            System.out.println(drawGraph.copyEdgeSet());
            System.out.println("g.getNodeSet() = " + drawGraph.copyNodeSet());
        }
//      2.  Layer Assignment: Vertices are assigned to layers.
        System.out.println("layer assignment");
        AssignLayer.longestPath(drawGraph);
        System.out.println("drawGraph got " + drawGraph);
        if (verbose) {
            System.out.println(drawGraph);
            System.out.println(drawGraph.copyEdgeSet());
            System.out.println("g.getNodeSet() = " + drawGraph.copyNodeSet());
        }

      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

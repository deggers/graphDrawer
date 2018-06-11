package draw;

import controller.GUIController;
import model.*;

public class NaiveDraw {
    private static final boolean VERBOSE = false;

    public static Graph processGraph(Graph theGraph) {
        System.out.println("Nodes: " + theGraph.getNodes().size() + ", Edges: " + theGraph.getEdges().size());
        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        Graph partialGraph = theGraph.copyWithRestrains(selectedEdgeType);
        System.out.println("Nodes: " + partialGraph.getNodes().size() + ", Edges: " + partialGraph.getEdges().size());


////      1. Remove all Cycles
        System.out.println("remove all cycles");
        CycleBreaker.Berger_Shor(partialGraph);
//        System.out.println("cyclefree got " + drawGraph);
//        if (verbose) {
//            System.out.println(drawGraph);
//            System.out.println(drawGraph.copyEdgeSet());
//            System.out.println("g.getNodeSet() = " + drawGraph.copyNodeSet());
//        }
////      2.  Layer Assignment: Vertices are assigned to layers.
//        System.out.println("Layer assignment");
//        AssignLayer.topologicalPath(partialGraph);
//        System.out.println("partialGraph = " + partialGraph);
//        System.out.println("drawGraph got " + drawGraph);
//        if (verbose) {
//            System.out.println(drawGraph);
//            System.out.println(drawGraph.copyEdgeSet());
//            System.out.println("g.getNodeSet() = " + drawGraph.copyNodeSet());
//        }

      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

package draw;

import controller.GUIController;
import model.*;

public class NaiveDraw {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) throws Exception {
        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        drawableGraph drawableGraph = new drawableGraph(theGraph,selectedEdgeType);

//      1. Remove all Cycles
        CycleBreaker.GreedyCycleRemoval(drawableGraph);

//      2.  Layer Assignment: Vertices are assigned to layers.
        AssignLayer.topologicalPath(drawableGraph);


      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

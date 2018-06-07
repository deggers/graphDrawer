package draw;

import controller.GUIController;
import model.*;

public class NaiveDraw {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) throws Exception {
        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        drawableGraph drawableGraph = new drawableGraph(theGraph,selectedEdgeType);

        CycleBreaker.Eades_Dustyn(drawableGraph);
        CycleBreaker.DFS_Florian(drawableGraph);


//   2.  Layer Assignment: Vertices are assigned to layers.
//       drawableGraph = LayerAssignment(theGraphtoBedrawn, "LayerAlgo");
      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

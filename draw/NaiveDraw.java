package draw;

import controller.GUIController;
import model.*;
import model.HelperTypes.ProtoNode;

public class NaiveDraw {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) throws Exception {

        String selectedEdgeType = GUIController.getInstance().getSelectedEdgeType();
        drawableGraph drawableGraph = new drawableGraph(theGraph,selectedEdgeType);

        System.out.println("before");
//        System.out.println(drawableGraph.getNode("1"));
//        System.out.println(drawableGraph.getNode("2"));
//        System.out.println(drawableGraph.getNode("3"));

        CycleBreaker.dfsCycleBreaker(drawableGraph);

        System.out.println("after");
//        System.out.println(drawableGraph.getNode("1"));
//        System.out.println(drawableGraph.getNode("2"));
//        System.out.println(drawableGraph.getNode("3"));


      return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

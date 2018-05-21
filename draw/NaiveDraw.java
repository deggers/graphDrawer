package draw;

import controller.PaneController;
import javafx.scene.layout.Pane;
import model.GraphMLGraph;
import model.Node;
import model.Tree;

import java.util.List;

public class NaiveDraw {

    public static GraphMLGraph processGraph(GraphMLGraph theGraph) {
        PaneController paneInstance = PaneController.getInstance();
        if (paneInstance != null) {
            int paneHeight = PaneController.getInstance().getScrollPaneHeight();
            int paneWidth = PaneController.getInstance().getScrollPaneWidth();

            int minHeight = 0;
            int minWidth = 0;
            for (Node node : theGraph.listAllNodes()) {
                node.x = minWidth + (int) (Math.random() * ((paneWidth - minWidth) + 1 ));
                node.y = minHeight + (int)(Math.random() * ((paneHeight - minHeight) + 1));
            }
        }
        return theGraph;
    }


    public static Tree processTree(Tree theTree) {

        return null;
    }
}

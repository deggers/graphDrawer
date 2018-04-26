import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class PaneController {
    private double nodeSize = 16;
    private MutableTree tree;

    @FXML
    private AnchorPane rootPane;


    public static PaneController getInstance() {
        FXMLLoader loader = new FXMLLoader();
        try {  // Load root layout from fxml file.
            loader.setLocation(PaneController.class.getResource("Pane.fxml"));
            AnchorPane rootPane = loader.load();
            PaneController paneController;
            paneController = loader.<PaneController>getController();
            paneController.setPane(rootPane);
            return paneController;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void draw(MutableTree tree) {
        rootPane.getChildren().clear();

        // should get a tree - with the levels (y,x)

        double x = 10.0;
        double y = 10.0;
        double radius = nodeSize;

        Circle leftNode = createNode(x+radius, y+radius, radius);
        rootPane.getChildren().add(leftNode);
    }

    private Circle createNode(double x, double y, double radius) {
        Circle node = new Circle(x, y, radius);
        node.setFill(Color.WHITE);
        node.setStroke(Color.BLACK);
        return node;
    }


    public void setNodeSize(double nodeSize) {
        // should first get the  tree we are working on?
        // getTree from FrameController?
        this.nodeSize = nodeSize;
        this.draw(tree);
    }

    public double getNodeSize() {
        return nodeSize;
    }

    public void setPane(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    public Parent getRoot() {
        return rootPane;
    }


}

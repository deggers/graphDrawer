import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.IOException;

public class PaneController {
    private MutableTree tree;
    private FrameController.TreeAlgorithm algorithm;
    private double nodeSize = 16;
    @FXML
    private AnchorPane rootPane;

    public static PaneController instance;


    public static PaneController getInstance() {
        if (instance == null){
            FXMLLoader loader = new FXMLLoader();
            try {  // Load root layout from fxml file.
                loader.setLocation(PaneController.class.getResource("Pane.fxml"));
                AnchorPane rootPane = loader.load();
                PaneController paneController = loader.getController();
                paneController.setPane(rootPane);
                PaneController.instance = paneController;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }


    public void draw() {
        System.out.println("Selected Tree Algo = " + FrameController.getInstance().getSelectedTreeAlgorithm());
        System.out.println("selected nodeSize = " + PaneController.getInstance().getNodeSize());
        System.out.println("Tree = " + ParseController.getInstance().getTree());
        rootPane.getChildren().clear();

        // should get a tree - with the levels (y,x)

        double x = 10.0;
        double y = 10.0;
        double radius = nodeSize;

        Circle leftNode = createNode(x + radius, y + radius, radius);
        rootPane.getChildren().add(leftNode);
    }

    private Circle createNode(double x, double y, double radius) {
        Circle node = new Circle(x, y, radius);
        node.setFill(Color.WHITE);
        node.setStroke(Color.BLACK);
        return node;
    }


    public void setNodeSize(double nodeSize) {
        this.nodeSize = nodeSize;
        this.draw();
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

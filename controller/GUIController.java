package controller;

import draw.NaiveDraw;
import draw.RadialTree;
import draw.WalkerImprovedDraw;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.MappedTreeStructure;
import model.Node;

import java.io.File;
import java.util.List;

public class GUIController {
    private int nodeSize = 8;
    private String selectedTreeAlgorithm;
    private MappedTreeStructure treeWalker = null;
    private Node treeRadial = null;
    public static final double OFFSET = 20;

    @FXML
    VBox layout;
    @FXML
    ScrollPane scollPane;
    @FXML
    Pane pane;
    @FXML
    Button exitBtn;
    @FXML
    ToggleButton fullscreenToggle;
    @FXML
    Slider nodeSizeSlider;
    @FXML
    ChoiceBox choiceBoxAlgorithm;
    @FXML
    Button loadFile;

    public void initialize() {
        nodeSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNodeSize(newValue.intValue());
        });

//        layout.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//                System.out.println("Width: " + newSceneWidth);
//            }
//        });
//        layout.heightProperty().addListener(new ChangeListener<Number>() {
//            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
//                System.out.println("Height: " + newSceneHeight);
//            }
//        });

    } // setup observer for nodeSizeSlider

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);
        }
        drawInit();
    }

    @FXML
    private void drawInit() {
        if (ParseController.getInstance().getTree() != null && getSelectedTreeAlgorithm() != null) {
            pane.getChildren().clear();
            switch (getSelectedTreeAlgorithm()) {
                case "Naive":
                    System.out.println("Selected Naive");
                    nodeSizeSlider.setDisable(false);
                    NaiveDraw.processTree(ParseController.getInstance().getTree());
                    break;
                case "Walker": // ugly much code..
                    if (treeWalker == null) {
                        this.treeWalker = WalkerImprovedDraw.processTreeNodes(ParseController.getInstance().getTree());
                    }
                    nodeSizeSlider.setDisable(false);
                    drawTreeStructure(this.treeWalker);
                    break;
                case "Radial":
                    System.out.println("Selected Radial");
                    if (treeRadial == null) {
                        this.treeRadial = RadialTree.processTree(ParseController.getInstance().getTree());
                    }
                    nodeSizeSlider.setDisable(true);
                    drawRadialTreeStructure(this.treeRadial);
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
    }

    private void drawRadialTreeStructure(Node root) {
        int halfHeight = (int) scollPane.getHeight()/2;
        int halfWidth = (int) scollPane.getWidth()/2;
        // draw levels
        int level = Node.treeDepth(root);
        int decreasingRadius = (Math.min(halfHeight,halfWidth)) - ( 2 * nodeSize);
        int spaceBetweenLevels = decreasingRadius / level;

        for (int i = 0; i <= level+1; i++) {
            pane.getChildren().add(createGuideline(halfWidth,halfHeight,decreasingRadius));
            decreasingRadius -= spaceBetweenLevels;
        }
        // set appropriate nodeSize
        this.nodeSize = spaceBetweenLevels >> 2;

        // draw root
        pane.getChildren().add(createNode(halfWidth, halfHeight, getNodeSize()));
    }

    private Circle createGuideline(int halfWidth, int halfHeight, int decreasingRadius) {
        Circle node = new Circle(halfWidth, halfHeight, decreasingRadius);
        node.setFill(Color.TRANSPARENT);
        node.setStroke(Color.BLACK);
        return node;
    }

    private boolean drawTreeNodes(MappedTreeStructure<Node> tree) {
        try {
            tree.listAllNodes().forEach((Node node) -> {
                String label = node.label;
                double x = scaleCoordinate(node.x);
                double y = scaleCoordinate(node.y);
                pane.getChildren().add(createNode((int) x, (int) y, getNodeSize()));
            });
        } catch (Exception e) {
            System.out.println("Fehler in drawTreeNodes");
            System.out.println(e);
            return false;
        }
        return true;
    }

    private boolean drawTreeEdges(MappedTreeStructure<Node> tree) {
        try {
//            System.out.println("print now for drawTreeEdges");
            tree.listAllNodes().forEach((Node child) -> {
                //
                if (child.parent != null) {
                    Node parent = child.parent;
                    String parentLabel = parent.label;
                    double parentX = scaleCoordinate(parent.x);
                    double parentY = scaleCoordinate(parent.y);

                    String childLabel = child.label;
                    double childX = scaleCoordinate(child.x);
                    double childY = scaleCoordinate(child.y);
                    pane.getChildren().add(new Line(parentX,parentY, childX, childY));

//                    System.out.println("parent: " + parentLabel + "(" + String.valueOf(parentX) + "," + String.valueOf(parentY) + ") child: " + childLabel + "(" + String.valueOf(childX) + "," + String.valueOf(childY) + ") ");
                }
            });
        } catch (Exception e) {
            System.out.println("Exception in drawTreeEdges");
            System.out.println(e);
            return false;
        }
        return true;
    }

//    private void drawTreeArrows(double node1X, double node1Y, double node2X, double node2Y) {
//        double arrowAngle = Math.toRadians(45.0);
//        double arrowLength = 10.0;
//        double dx = node1X - node2X;
//        double dy = node1Y - node2Y;
//        double angle = Math.atan2(dy, dx);
//        double x1 = Math.cos(angle + arrowAngle) * arrowLength + node2X;
//        double y1 = Math.sin(angle + arrowAngle) * arrowLength + node2Y;
//
//        double x2 = Math.cos(angle - arrowAngle) * arrowLength + node2X;
//        double y2 = Math.sin(angle - arrowAngle) * arrowLength + node2Y;
//
//        pane.getChildren().add(new Line(scaleCoordinate(node2X), scaleCoordinate(node2Y), scaleCoordinate(x1), scaleCoordinate(y1)));
//        pane.getChildren().add(new Line(scaleCoordinate(node2X), scaleCoordinate(node2Y), scaleCoordinate(x2), scaleCoordinate(y2)));
//        pane.getChildren().add(new Line(scaleCoordinate(node1X), scaleCoordinate(node1Y), scaleCoordinate(node2X), scaleCoordinate(node2Y)));
//    }

    private void drawTreeStructure(MappedTreeStructure tree) {
        drawTreeEdges(tree);
        drawTreeNodes(tree);
    }

    private double scaleCoordinate(double number) {
        double coordinate = (number * 2 * getNodeSize()) + OFFSET;
        return coordinate;
    }

    @FXML
    private void choiceBoxAlgorithmOnAction(ActionEvent event) {
        String selectedAlgo = String.valueOf(choiceBoxAlgorithm.getSelectionModel().getSelectedItem());
        switch (selectedAlgo) {
            case "Naive":
                this.selectedTreeAlgorithm = "Naive";
                break;
            case "Radial":
                this.selectedTreeAlgorithm = "Radial";
                break;
            case "Walker":
                this.selectedTreeAlgorithm = "Walker";
                break;
            default:
                throw new IllegalArgumentException("The algo: " + selectedAlgo + " is not yet implemented");
        }
        drawInit();
    }

    @FXML
    private void loadFileOnAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Newick files (*.nh)", "*.nh"),
                new FileChooser.ExtensionFilter("GraphML files (*.graphml)", "*.graphml")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            if (ParseController.getInstance().initializeParsing(file)) {
                drawInit();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsing(file)");
            }
        }
    }

    private Circle createNode(int x, int y, int radius) {
        Circle node = new Circle(x, y, radius);
        node.setFill(Color.FORESTGREEN);
        node.setStroke(Color.BLACK);
        return node;
    }

    // SETTER & GETTER AREA
    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
        this.drawInit();
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public String getSelectedTreeAlgorithm() {
        return this.selectedTreeAlgorithm;
    }
}
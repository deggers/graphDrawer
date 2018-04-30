package controller;

import draw.NaiveDraw;
import draw.WalkerImprovedDraw;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.MappedTreeStructure;
import model.Node;

import java.io.File;

public class GUIController {
    private int nodeSize = 8;
    private String selectedTreeAlgorithm;
    private MappedTreeStructure treeWalker = null;
    public static final double OFFSET = 20;

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
    }

    @FXML
    private void drawInit() {
        if (ParseController.getInstance().getTree() != null && getSelectedTreeAlgorithm() != null) {
            pane.getChildren().clear();
            switch (getSelectedTreeAlgorithm()) {
                case "Naive":
                    System.out.println("Selected Naive");
                    NaiveDraw.processTree(ParseController.getInstance().getTree());
                    break;
                case "Walker": // ugly much code..
                    if (treeWalker == null) {
                        this.treeWalker = WalkerImprovedDraw.processTreeNodes(ParseController.getInstance().getTree());
                    }
                    drawTreeStructure(this.treeWalker);
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
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

    // toDo for Dustyn
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
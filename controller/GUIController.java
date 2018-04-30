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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.MutableTree;

import java.io.File;

public class GUIController {
    private int nodeSize = 8;
    private String selectedTreeAlgorithm;
    private MutableTree treeWalker = null;
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
                    drawTreeStructure(treeWalker);
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
    }

    private boolean drawTreeNodes(MutableTree tree) {
        String toProcess = tree.getRoots().toString();
        String[] splittedTree = toProcess.substring(1, toProcess.length() - 1).split(";");
        for (String node : splittedTree) {
            try {
                String[] nodeString = node.split(",");

                String[] label_stringArray = nodeString[0].split(":");
                String label = label_stringArray[1];

                String[] x_stringArray = nodeString[1].split(":");
                double x = Double.parseDouble(x_stringArray[1]);
                double startX = (x * 2 * nodeSize) + OFFSET;

                String[] y_stringArray = nodeString[2].split(":");
                double y = Double.parseDouble(y_stringArray[1]);
                double startY = (y * 2 * nodeSize) + OFFSET;
//            System.out.println("x: " + Double.toString(x) + ", y: " + Double.toString(y) + ", id: " + label);

                pane.getChildren().add(createNode((int) startX, (int) startY, (int) getNodeSize() / 2));

            }
            catch (Exception e) {
                System.out.println("Fehler in drawTreeNodes");
                System.out.println(e);
                return false;
            }
        }
         return true;
    }

    // toDo for Dustyn
    private void drawTreeStructure(MutableTree tree) {
            drawTreeNodes(tree);

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
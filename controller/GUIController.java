package controller;

import draw.WalkerImprovedDraw;
import draw.naiveDraw;
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
import model.MappedTreeStructure;
import model.Node;

import java.io.File;
import java.util.List;

public class GUIController {
    private int             nodeSize = 16;
    private String          selectedTreeAlgorithm;

    @FXML Pane          pane;
    @FXML Button        exitBtn;
    @FXML ToggleButton  fullscreenToggle;
    @FXML Slider        nodeSizeSlider;
    @FXML ChoiceBox     choiceBoxAlgorithm;
    @FXML Button loadFile;

    public void initialize() {
        nodeSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNodeSize(newValue.intValue());
        });
    } // setup observer for nodeSizeSlider

    @FXML   private void closeButtonAction() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
    @FXML   private void toggleFullscreen() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);
        }
    }
    @FXML   private void drawCircles() {
        if (ParseController.getInstance().getTree() != null && getSelectedTreeAlgorithm() != null) {
            pane.getChildren().clear();
            System.out.println(getSelectedTreeAlgorithm());
            switch (getSelectedTreeAlgorithm()) {
                case "Naive":
                    System.out.println("Selected Naive");
                    naiveDraw.processTree(ParseController.getInstance().getTree());
                    break;
                case "Walker": // ugly much code..
                    System.out.println("selected Walker");
                    Node root = ParseController.getInstance().getTree();
                    MappedTreeStructure<Node> tree = new MappedTreeStructure<Node>(root);
                    try {
                        WalkerImprovedDraw w = new WalkerImprovedDraw();
                        tree = w.treeLayout(tree);
                        //System.out.println("\n CONTENT: \n" + tree.echoContent());
//                        System.out.println("Root(s): " + tree.getRoots().toString());
                        List<Node> toWorkOn = tree.getRoots();

                        for (Node node : toWorkOn) {
                            String[] split = node.toString().split(",");
                            System.out.println("label: " + split[0]);
                        }


                    } catch(Exception e){
                        System.out.println("Error while running Walker Algorithm");
                        System.out.println(e);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
    }
    @FXML   private void choiceBoxAlgorithmOnAction(ActionEvent event) {
        String selectedAlgo = String.valueOf(choiceBoxAlgorithm.getSelectionModel().getSelectedItem());
        switch (selectedAlgo) {
            case "Naive":   this.selectedTreeAlgorithm = "Naive";   break;
            case "Radial":  this.selectedTreeAlgorithm = "Radial";  break;
            case "Walker":  this.selectedTreeAlgorithm = "Walker";  break;
            default: throw new IllegalArgumentException("The algo: " + selectedAlgo + " is not yet implemented");
        }
        System.out.println("set algo to: " +this.selectedTreeAlgorithm);
        drawCircles();
    }
    @FXML   private void loadFileOnAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Newick files (*.nh)", "*.nh"),
                new FileChooser.ExtensionFilter("GraphML files (*.graphml)", "*.graphml")
                );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            if (ParseController.getInstance().initializeParsing(file)) {
                drawCircles();
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
        this.drawCircles();
    }
    public int getNodeSize() {
        return nodeSize;
    }

    public String getSelectedTreeAlgorithm() {
        return this.selectedTreeAlgorithm;
    }
}

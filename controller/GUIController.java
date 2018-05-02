package controller;

import draw.NaiveDraw;
import draw.RadialTree;
import draw.WalkerImprovedDraw;
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

import javax.xml.bind.annotation.XmlAnyAttribute;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class GUIController {
    private int nodeSize = 8;
    private String selectedTreeAlgorithm;
    private MappedTreeStructure<Node> treeWalker = null;
    private MappedTreeStructure<Node> treeRadial = null;
    public static GUIController instance;

    private ListIterator<File> filesIter;

    List<File> filesInFolder = null;
    private String fileName;

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
    @FXML
    Button nextFile;
    @FXML
    Label fileNameLabel;

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
//        System.out.println("start drawInit()");
//        System.out.println("Tree: " + ParseController.getInstance().getTree());
//        System.out.println("Algorithm: " + getSelectedTreeAlgorithm());
        if (ParseController.getInstance().getTree() != null && getSelectedTreeAlgorithm() != null) {
            System.out.println("i am in the process of doing the draw..");
            pane.getChildren().clear();
            fileNameLabel.setText(this.fileName);
            switch (getSelectedTreeAlgorithm()) {
                case "Naive":
                    System.out.println("Selected Naive");
                    nodeSizeSlider.setDisable(false);
                    NaiveDraw.processTree(ParseController.getInstance().getTree());
                    break;
                case "Walker":
                    this.treeWalker = WalkerImprovedDraw.processTreeNodes(ParseController.getInstance().getTree());
                    nodeSizeSlider.setDisable(false);
                    drawTreeStructure(this.treeWalker);
                    break;
                case "Radial":
                    System.out.println("Selected Radial");
                    this.treeRadial = RadialTree.processTree(ParseController.getInstance().getTree());
                    nodeSizeSlider.setDisable(true);
                    drawRadialTreeStructure(this.treeRadial);
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
    }

    private void drawRadialTreeStructure(MappedTreeStructure<Node> root) {
        int halfHeight = (int) scollPane.getHeight() / 2;
        int halfWidth = (int) scollPane.getWidth() / 2;
        int level = 0;
        // draw levels
        for (Node node : root.nodeList) {
            level = Node.getTreeDepth(node);
        }
        int decreasingRadius = (Math.min(halfHeight, halfWidth)) - (2 * nodeSize);
        int spaceBetweenLevels = decreasingRadius / level;

        for (int i = 0; i <= level + 1; i++) {
            pane.getChildren().add(createGuideline(halfWidth, halfHeight, decreasingRadius));
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
                    pane.getChildren().add(new Line(parentX, parentY, childX, childY));

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

    private void drawTreeStructure(MappedTreeStructure tree) {
        System.out.println("called drawTreeStructure");
        drawTreeEdges(tree);
        drawTreeNodes(tree);
    }

    private double scaleCoordinate(double number) {
        double coordinate = (number * 2 * getNodeSize()) + OFFSET;
        return coordinate;
    }

    @FXML
    private void choiceBoxAlgorithmOnAction() {
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
    public void setNextFileAsTree() {
//        System.out.println("next called!");
        if (filesInFolder != null) { // we selected a file so we have the folder from here on
            if (getFilesIter() == null || !getFilesIter().hasNext()) {
                List<File> files = getFilesInFolder();
                ListIterator<File> filesIter = files.listIterator();
                setFilesIter(filesIter);
            }
            ParseController.getInstance().setFile(filesIter.next());
            File file = ParseController.getInstance().getFile();
            if (ParseController.getInstance().initializeParsing(file)) {
                this.fileName = file.getName();
                drawInit();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsing(file)");
            }
        }

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
            this.fileName = file.getName();
            try {
                filesInFolder = Files.walk(Paths.get(file.getParent()))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                setFilesInFolder(filesInFolder);
//                System.out.println("filesInFolder = " + filesInFolder);
            } catch (IOException e) {
                System.out.println("Error in loadFileAction");
                e.printStackTrace();
            }
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

    public void setSelectedTreeAlgorithm(String selectedTreeAlgorithm) {
        this.selectedTreeAlgorithm = selectedTreeAlgorithm;
    }

    public List<File> getFilesInFolder() {
        return filesInFolder;
    }

    public void setFilesInFolder(List<File> filesInFolder) {
        this.filesInFolder = filesInFolder;
    }

    public ListIterator<File> getFilesIter() {
        return filesIter;
    }

    public void setFilesIter(ListIterator<File> filesIter) {
        this.filesIter = filesIter;
    }

    public static GUIController getInstance() {
        if (instance == null) {
            GUIController.instance = new GUIController();
        }
        return GUIController.instance;
    }

}
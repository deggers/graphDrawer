package controller;

import draw.NaiveDraw;
import draw.RadialTree;
import draw.Reinhold;
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
import model.Node;
import model.Tree;

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
    private Tree treeWalker = null;
    private Tree treeRadial = null;
    private Tree reinholdTree = null;
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
    private int spaceBetweenRadii;

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
                case "RT":
                    System.out.println("Selected Reinhold");
                    nodeSizeSlider.setDisable(false);
                    this.reinholdTree = Reinhold.processTree(ParseController.getInstance().getTree());
                    drawTreeStructure(this.reinholdTree);
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
    }

    private void drawRadialTreeStructure(Tree tree) {
        int halfHeight = (int) scollPane.getHeight() / 2;
        int halfWidth = (int) scollPane.getWidth() / 2;
        int level = 0;
        // draw levels
        level = tree.getTreeDepth();
        System.out.println("Number of levels = " + level);
        System.out.println("");

        int maxRadius = (Math.min(halfHeight, halfWidth)) - (2 * nodeSize);
        int spaceBetweenRadii = maxRadius / level;
        this.spaceBetweenRadii = spaceBetweenRadii;
        System.out.println("spaceBetweenRadii = " + spaceBetweenRadii);
        System.out.println("");

        for (int i = 0; i <= level + 1; i++) {
            pane.getChildren().add(createGuideline(halfWidth, halfHeight, maxRadius));
            maxRadius -= spaceBetweenRadii;
        }

        this.nodeSize = spaceBetweenRadii >> 2;         // set appropriate nodeSize

        // draw root
//        pane.getChildren().add(createNode(halfWidth, halfHeight, getNodeSize(), "root"));

        Node root = tree.getRoot();
        System.out.println("root = " + root);

        this.treeRadial = radialPositions(tree, root, Math.toRadians(60), Math.toRadians(360));
        drawRadialTreeEdges(this.treeRadial);
    }

    private Tree radialPositions(Tree radialTree, Node node, double alpha, double beta) {
        int centerY = (int) scollPane.getHeight() / 2;
        int centerX = (int) scollPane.getWidth() / 2;

        if (node.parent == null) { // if node is root
            radialTree.setNodeCoords(node, centerX, centerY);
            pane.getChildren().add(createNode(centerX, centerY, getNodeSize(), node.label));
        }

        //Depth of vertex starting from 0 and adding one
        double depthOfVertex = node.level + 1;
        double theta = alpha;
        double radius = (this.spaceBetweenRadii * depthOfVertex) + nodeSize;

        // number of leaves for the subtree rooted in v
        int k = treeRadial.getLeavesOfNode(node);
        System.out.println("numberOfLeafs = " + k);

//        System.out.println("numberOfLeafs = " + numberOfLeafs + " from: " + node);
//        int runner = 0;
        for (Node child : node.getChildren()) {
//            runner++;
            int lambda = treeRadial.getLeavesOfNode(child);
            System.out.println("lambda = " + lambda);
            double betaAlphaTemp = beta - alpha;
            System.out.println("beta-alpha: " + betaAlphaTemp);
            System.out.println("lambda/k = " + (lambda/k));
            System.out.println("k = " + k);
            System.out.println("lambda = " + lambda);
            double mi = theta + (((double)lambda / k) * (beta - alpha));
            System.out.println("mi = " + mi);

            double term = (theta + mi)/2;
            double cosTerm = Math.cos(term);
            double sinTerm = Math.sin(term);
            System.out.println("cosTerm = " + cosTerm);
            System.out.println("sinTerm = " + sinTerm);
            System.out.println("");
            // x = center x + radius * cos(angle)
            int x = (int) (centerX + (radius * cosTerm));
            int y = (int) (centerY + (radius * sinTerm));
            radialTree.setNodeCoords(child, x, y);
            pane.getChildren().add(createNode(x, y, getNodeSize(), child.label));

            if (!child.isLeaf()) {
                radialPositions(radialTree, child, theta, mi);
            }
            theta = mi;
//            System.out.println("theta = " + theta);
        }
        return radialTree;
    }

    private boolean drawRadialTreeEdges(Tree tree) {
        try {
            for (Node child : tree.listAllNodes()) {
                System.out.println(child);
                if (child.parent != null) {
                    Node parent = child.parent;
                    double parentX = parent.x;
                    double parentY = parent.y;

                    double childX = child.x;
                    double childY = child.y;

                    pane.getChildren().add(new Line(parentX, parentY, childX, childY));
//                    System.out.println("childY = " + childY);
//                    System.out.println("childX = " + childX);
//                    System.out.println("parentY = " + parentY);
//                    System.out.println("parentX = " + parentX);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in drawRadialTreeEdges");
            System.out.println(e);
            return false;
        }
        return true;
    }

    private Circle createGuideline(int halfWidth, int halfHeight, int decreasingRadius) {
        Circle node = new Circle(halfWidth, halfHeight, decreasingRadius);
        node.setFill(Color.TRANSPARENT);
        node.setStroke(Color.LIGHTGRAY);
        return node;
    }

    private boolean drawTreeNodes(Tree tree) {
        try {
            tree.listAllNodes().forEach((Node node) -> {
                String label = node.label;
                double x = scaleCoordinate(node.x);
                double y = scaleCoordinate(node.y);
                pane.getChildren().add(createNode((int) x, (int) y, getNodeSize(), label));
            });
        } catch (Exception e) {
            System.out.println("Fehler in drawTreeNodes");
            System.out.println(e);
            return false;
        }
        return true;
    }

    private boolean drawTreeEdges(Tree tree) {
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

    private void drawTreeStructure(Tree tree) {
        System.out.println("called drawTreeStructure");
        System.out.println("The tree = " + tree);
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
            case "RT":
                this.selectedTreeAlgorithm = "RT";
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

    private Circle createNode(int x, int y, int radius, String id) {
        Circle node = new Circle(x, y, radius);
        try {
            int test = Integer.parseInt(id);
            node.setFill(Color.GRAY);

        } catch (NumberFormatException e) {
            node.setFill(Color.FORESTGREEN);
        }

        node.setStroke(Color.BLACK);
        Tooltip tip = new Tooltip(id);
        Tooltip.install(node, tip);
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
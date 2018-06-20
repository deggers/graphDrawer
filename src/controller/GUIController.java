package controller;

import structure.*;

import draw.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class GUIController {
    //@formatter:off
    private static final String PARSE_WHOLE_GRAPH = "WHOLE GRAPH";

    private boolean         choiceBoxEdgeTypeIsSet  = false;
    private boolean         choiceBoxRootIsSet      = false;
    private File            fileHandle              = null;
    private ParseController parseInstance           = ParseController.INSTANCE;

    public void setChoiceBoxAlgorithmIsSet(boolean choiceBoxAlgorithmIsSet) {
        this.choiceBoxAlgorithmIsSet = choiceBoxAlgorithmIsSet;
    }

    private boolean choiceBoxAlgorithmIsSet = false;

    public void initialize() {
        nodeSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNodeSize(newValue.intValue());
        });
        choiceBoxEdgeType.setDisable(true); // disable at beginning
        choiceBoxRoot.setDisable(true); // disable at beginning
        choiceBoxAlgorithm.setDisable((true)); // disable at beginning :)
    } // setup observer for nodeSizeSlider


    @FXML    private    VBox            vBox;
    @FXML    Button                     exitBtn;
    @FXML    ToggleButton               fullscreenToggle;
    @FXML    Slider                     nodeSizeSlider;
    @FXML    ChoiceBox                  choiceBoxAlgorithm;
    @FXML    Button                     loadFile;
    @FXML    Button                     nextFile;
    @FXML    Label                      fileNameLabel;
    @FXML    ChoiceBox                  choiceBoxEdgeType;
    @FXML    ChoiceBox                  choiceBoxRoot;


    @FXML    private void               closeButtonAction() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
    @FXML    private void               toggleFullscreen()  {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);
        }
        drawInit();
    }
    @FXML    private void               setNextFileAsTree() {
        if (filesInFolder != null) { // we selected a file so we have the folder from here on
            if (filesIter == null || !filesIter.hasNext()) {
                List<File> files = filesInFolder;
                this.filesIter = files.listIterator();
            }
            parseInstance.setFile(filesIter.next());
            File file = parseInstance.getFile();
            if (parseInstance.initParsing(file)) {
                this.fileName = file.getName();
                drawInit();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsing(file)");
            }
        }

    }
    @FXML    private void               loadFileOnAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Newick files (*.nh)", "*.nh"),
                new FileChooser.ExtensionFilter("GraphML files (*.graphml)", "*.graphml")
        );

        File file = fileChooser.showOpenDialog(null);
        this.fileHandle = file;

        if (file != null) {
            this.fileName = file.getName();
            filesInFolder = getFilesFromFolder(file);
            if (parseInstance.initParsing(file)) {
                drawInit();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsing(file)");
            }
        }
    }
    @FXML    private void               choiceBoxAlgorithmOnAction() {
        this.selectedAlgorithm = String.valueOf(choiceBoxAlgorithm.getSelectionModel().getSelectedItem());
        drawInit();
    }

    private PaneController              paneController              = null;
    private String                      selectedAlgorithm           = null;
    private String                      fileName                    = null;
    private ListIterator<File>          filesIter                   = null;
    private List<File>                  filesInFolder               = null;

    public String getFileName(){return this.fileName;}

    public static   GUIController       getInstance() {
        FXMLLoader loader = new FXMLLoader();
        if (guiInstance == null) {
            try {
                loader.setLocation(GUIController.class.getResource("/fxml/GUI.fxml"));
                VBox vBox = loader.load();
                GUIController guiController = loader.getController();
                guiController.setPane(vBox);
                guiInstance = guiController;
                return guiController;
            } catch (IOException e) {
                System.out.println("that's an IOException here!");
                e.printStackTrace();
                return null;
            }
        } else {
            return guiInstance;
        }
    }
    private static  GUIController       guiInstance                 = null;


    private int                      nodeSize                    = 8;
    private int                         spaceBetweenRadii           = 0;
    private String                      selectedRoot                = null;
    private String                      selectedEdgeType            = null;

    public  void                        init()      {
        paneController = PaneController.getInstance();// set ScrollPane
        vBox.getChildren().add(paneController.getScrollPane());
    }
    void                                cleanPane()   {
        if (paneController != null) paneController.cleanPane();
    }
    private void                        setFileLabel() {
        fileNameLabel.setText(this.fileName);
    }
    public List<File>                  getFilesFromFolder(File file) {
        try {
            return Files.walk(Paths.get(file.getParent()))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error in loadFileAction");
            e.printStackTrace();
        }
        return null;
    }

    public void cb_EdgeTypeOnAction(ActionEvent event) {
        this.selectedEdgeType = String.valueOf(choiceBoxEdgeType.getSelectionModel().getSelectedItem());

        if (selectedEdgeType != null) {
            this.selectedRoot = null;
            choiceBoxRoot.getItems().clear();
            choiceBoxRoot.setDisable(false);

            Graph theGraph = parseInstance.getGraph();
            if (theGraph != null) {
//                List<String> rootList = parseInstance.getGraph().getPossibleRootLabels(selectedEdgeType);
//                choiceBoxRoot.getItems().setAll(rootList);
//                choiceBoxRoot.getItems().add(0,PARSE_WHOLE_GRAPH);
            }
            drawInit();
        }
    }
    public void cb_SelectRootOnAction(ActionEvent event) {
        this.selectedRoot     = String.valueOf(choiceBoxRoot.getSelectionModel().getSelectedItem());
        Graph    theGraph        = parseInstance.getGraph();

        boolean willBeTree = (selectedRoot != null) && (theGraph != null) && !selectedRoot.equals(PARSE_WHOLE_GRAPH);
        boolean isWholeGraph = (selectedRoot != null) && (theGraph != null) && selectedRoot.equals(PARSE_WHOLE_GRAPH);

        if (willBeTree) {
//            Tree extractedTreeFromNode = theGraph.extractSubtreeFromProtoNode(theGraph.labelToProtoNode(selectedRoot), selectedEdgeType);
//            parseInstance.setTree(extractedTreeFromNode);
        }
        else if (isWholeGraph) {
            parseInstance.setGraph(theGraph);
            parseInstance.setTree(null);
        }
        cleanPane();
        drawInit();
    }

    private void processAlgo() {
        cleanPane();
        nodeSizeSlider.setDisable(false);
        switch (selectedAlgorithm) {
            case "Walker":
                Tree treeWalker = WalkerImproved.processTreeNodes(parseInstance.getTree());
                paneController.drawTreeStructure(treeWalker);
                break;
            case "Radial":
                Tree radialTree = RadialTree.processTree(parseInstance.getTree());
                paneController.drawRadialTreeStructure(radialTree);
                break;
            case "RT":
                Tree reinholdTree = Reinhold.processTree(parseInstance.getTree());
                paneController.drawTreeStructure(reinholdTree);
                break;
            case "BPlus":
                Tree BPlusTree = B_Plus.processTree(parseInstance.getTree());
                paneController.drawTreeOrthogonally(BPlusTree);
                break;
            case "Random":
                if (parseInstance.getGraph() != null) {
                    Graph naiveDraw = Naive.processGraph(parseInstance.getGraph());
                    paneController.drawDAG(naiveDraw);
                }
                break;
            default:
                throw new IllegalArgumentException("The algo: " + selectedAlgorithm + " is not yet implemented");
        }

    }


    public void drawInit() {
        setFileLabel();
        Tree theTree = parseInstance.getTree();
        Graph theGraph = parseInstance.getGraph();
        boolean treeOrGraph = (theTree != null || theGraph != null);

        setupChoiceBoxAlgorithms(theTree, theGraph, treeOrGraph);
        setupChoiceBoxEdgeType(theGraph);

        if (treeOrGraph && selectedAlgorithm != null && !selectedAlgorithm.equals("null")) {
            if (theGraph == null) {
                choiceBoxEdgeType.setDisable(true);
                choiceBoxRoot.setDisable(true);
                choiceBoxEdgeType.getItems().clear();
                choiceBoxRoot.getItems().clear();
                processAlgo();
            } else {
                if (selectedEdgeType != null) processAlgo();
            }
        }
    }
    private void setupChoiceBoxAlgorithms(Tree theTree, Graph theGraph, boolean treeOrGraph) {
        List<String> allAlgos = Arrays.asList("BPlus", "RT", "Walker", "Radial", "Random");
        List<String> reducedAlgos = Arrays.asList("BPlus", "Walker", "Radial", "Random");
        List<String> graphMLAlgos = Arrays.asList("Random");

        if (treeOrGraph && !choiceBoxAlgorithmIsSet) {
            choiceBoxAlgorithmIsSet = true;
            choiceBoxAlgorithm.setDisable(false);
            choiceBoxAlgorithm.getItems().clear();
            choiceBoxAlgorithm.getItems().setAll(theTree != null ? theTree.isBinary() ? allAlgos : reducedAlgos : graphMLAlgos);
        }
    }
    private void setupChoiceBoxEdgeType(Graph theGraph) {
        if (theGraph != null && !choiceBoxEdgeTypeIsSet) {
            choiceBoxEdgeType.setDisable(false);
            choiceBoxEdgeType.getItems().setAll(theGraph.getEdgeTypes());
            choiceBoxEdgeTypeIsSet = true;
        }
    }


    // SETTER & GETTER AREA
    public Parent getRoot() {
        return this.vBox;
    }
    private void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
        drawInit();
    }
    public int getNodeSize() {
        return this.nodeSize;
    }
    private void setPane(VBox vBox) {
        this.vBox = vBox;
    }
    void setChoiceBoxAlgorithm(String algo) {
        this.selectedAlgorithm = algo;
    }
    public void setFilesInFolder(List<File> filesInFolder) {
        this.filesInFolder = filesInFolder;
    }
    public File getFileHandle() {
        return fileHandle;
    }
    public String getSelectedAlgo() {
        return selectedAlgorithm;
    }
    public String getSelectedEdgeType() {
        return selectedEdgeType;
    }
}

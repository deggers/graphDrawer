
package controller;

import draw.B_Plus;
import draw.RadialTree;
import draw.Reinhold;
import draw.WalkerImprovedDraw;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.GraphMLGraph;
import model.Tree;

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

    private boolean choiceBoxEdgeTypeIsSet = false;
    private boolean choiceBoxRootIsSet = false;

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

    //@formatter:off
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
    @FXML    private void               loadFileOnAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Newick files (*.nh)", "*.nh"),
                new FileChooser.ExtensionFilter("GraphML files (*.graphml)", "*.graphml")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            this.fileName = file.getName();
            filesInFolder = getFilesFromFolder(file);
            if (ParseController.getInstance().initializeParsing(file)) {
                drawInit();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsing(file)");
            }
        }
    }
    @FXML    private void               choiceBoxAlgorithmOnAction() {
        String selectedAlgo = String.valueOf(choiceBoxAlgorithm.getSelectionModel().getSelectedItem());
        switch (selectedAlgo) {
            case "Radial":
                this.selectedAlgorithm = "Radial";
                break;
            case "Walker":
                this.selectedAlgorithm = "Walker";
                break;
            case "BPlus":
                this.selectedAlgorithm = "BPlus";
                break;
            case "RT":
                this.selectedAlgorithm = "RT";
                break;
            default:
                break;
        }
        drawInit();
    }

    private PaneController              paneController              = null;
    private String                      selectedAlgorithm           = null;
    private String                      fileName                    = null;
    private ListIterator<File>          filesIter                   = null;
    private List<File>                  filesInFolder               = null;


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


    private int                         nodeSize                    = 8;
    private int                         spaceBetweenRadii           = 0;
    private String                      selectedRoot                = null;
    private String                      selectedEdgeType            = null;

    public  void                        init()      {
        paneController = PaneController.getInstance();// set ScrollPane
        vBox.getChildren().add(paneController.getScrollPane());
    }
    private void                        cleanPane()   {
        paneController.cleanPane();
    }
    private void                        setFileLabel() {
        fileNameLabel.setText(this.fileName);
    }
    public List<File>                   getFilesFromFolder(File file) {
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

    public void choiceBoxSelectRoot(ActionEvent event) {
        selectedRoot =  String.valueOf(choiceBoxRoot.getSelectionModel().getSelectedItem());
        if (selectedRoot != null) {
            GraphMLGraph theGraph = ParseController.getInstance().getGraph();
            if (theGraph != null) {
                ParseController.getInstance().setTree(theGraph.extractSubtreeFromNode(theGraph.labelToNode(selectedRoot), selectedEdgeType));
            }
            cleanPane();
            drawInit(); 
        }
    }
    
    public void choiceBoxEdgeTypeOnAction(ActionEvent event) {
        try {
            selectedEdgeType = String.valueOf(choiceBoxEdgeType.getSelectionModel().getSelectedItem());
        } catch (NullPointerException e) {
            selectedEdgeType = null;
        }

        if (selectedEdgeType != null) {
            this.selectedEdgeType = selectedEdgeType;
            this.selectedRoot = null;
            choiceBoxRoot.getItems().clear();
            choiceBoxRoot.setDisable(false);

            GraphMLGraph theGraph = ParseController.getInstance().getGraph();
            if (theGraph != null) {
                List<String> rootList = ParseController.getInstance().getGraph().getPossibleRootLabels(selectedEdgeType);
                choiceBoxRoot.getItems().setAll(rootList);
            }            
            drawInit(); 
        }      
    }
    public void setChoiceBoxEdgeTypeIsSet(boolean choiceBoxEdgeTypeIsSet) {
        this.choiceBoxEdgeTypeIsSet = choiceBoxEdgeTypeIsSet;
    }

    //@formatter:on

    private void drawInit() {
        Tree theTree = ParseController.getInstance().getTree();
        GraphMLGraph theGraph = ParseController.getInstance().getGraph();

        if ((theTree != null || theGraph != null) && !choiceBoxAlgorithmIsSet) {
            choiceBoxAlgorithm.setDisable(false);
            choiceBoxAlgorithm.getItems().clear();
            if (theTree != null && theTree.isBinary()) {
                List<String> list = Arrays.asList("BPlus", "RT", "Walker", "Radial");
                choiceBoxAlgorithm.getItems().setAll(list);
            } else {
                List<String> list = Arrays.asList("BPlus", "Walker", "Radial");
                choiceBoxAlgorithm.getItems().setAll(list);
            }
            choiceBoxAlgorithmIsSet = true;
        }

        if (theGraph != null && !choiceBoxEdgeTypeIsSet) {
            choiceBoxEdgeType.setDisable(false);
            choiceBoxEdgeType.getItems().setAll(theGraph.getRelevantEdgeTypeLabels());
            choiceBoxEdgeTypeIsSet = true;
        }


        if (theTree != null && selectedAlgorithm != null && theGraph == null) {
            choiceBoxEdgeType.setDisable(true);
            choiceBoxRoot.setDisable(true);
            choiceBoxEdgeType.getItems().clear();
            choiceBoxRoot.getItems().clear();
            processTreeAndAlgo();

        } else if (theGraph != null && selectedAlgorithm != null && theTree != null && selectedRoot != null && selectedEdgeType != null) {
            processTreeAndAlgo();
            choiceBoxEdgeType.setDisable(false);
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

    private void processTreeAndAlgo() {
        cleanPane();
        setFileLabel();
        switch (selectedAlgorithm) {  // what about a tree.resizeToScreen() ?
            case "Walker":
                Tree treeWalker = WalkerImprovedDraw.processTreeNodes(ParseController.getInstance().getTree());
                nodeSizeSlider.setDisable(false);
                paneController.drawTreeStructure(treeWalker);
                break;
            case "Radial":
                Tree radialTree = RadialTree.processTree(ParseController.getInstance().getTree());
                nodeSizeSlider.setDisable(true);
                paneController.drawRadialTreeStructure(radialTree);
                break;
            case "RT":
                nodeSizeSlider.setDisable(false);
                Tree reinholdTree = Reinhold.processTree(ParseController.getInstance().getTree());
                paneController.drawTreeStructure(reinholdTree);
                break;
            case "BPlus":
                Tree BPlusTree = B_Plus.processTree(ParseController.getInstance().getTree());
                nodeSizeSlider.setDisable(false);
                paneController.drawTreeOrthogonally(BPlusTree);
                break;
            default:
                throw new IllegalArgumentException("The algo: " + selectedAlgorithm + " is not yet implemented");
        }
        selectedAlgorithm = null;
    }

    public void setFilesInFolder(List<File> filesInFolder) {
        this.filesInFolder = filesInFolder;
    }

}

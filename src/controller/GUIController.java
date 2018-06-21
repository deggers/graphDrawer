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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class GUIController {
    //@formatter:off
    private static final LinkedList<String> CYCLEREMOVAL = new LinkedList<>(Arrays.asList("Greedy_Eades'90","BergerShor'87","DFS_Florian"));
    private static final LinkedList<String> LAYERASSIGNER = new LinkedList<>(Arrays.asList("TopoSort", "Longest Path"));
    private static final LinkedList<String> HORIZONTALLAYOUT = new LinkedList<>(Arrays.asList("theOneAndOnly"));

    private PaneController              paneController              = null;
    private String                      selectedAlgorithm           = null;
    private String                      fileName                    = null;
    private ListIterator<File>          filesIter                   = null;
    private List<File>                  filesInFolder               = null;
    private String                      selectedLayerAssigner       = null;

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


    public  void                        init()      {
        paneController = PaneController.getInstance();// set ScrollPane
        vBox.getChildren().add(paneController.getScrollPane());
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
    private int                         nodeSize                    = 8;
    private int                         spaceBetweenRadii           = 0;
    private String                      selectedRoot                = null;
    private String                      selectedEdgeType            = null;
    private String                      selectedCycleRemovalAlgo    = null;
    private String                      selectedHorizontalAlgo      = null;
    private File                        fileHandle                  = null;
private ParseController             parseInstance                   = ParseController.INSTANCE;
    
    @FXML    private    VBox            vBox;
    @FXML    Button                     exitBtn;
    @FXML    ToggleButton               fullscreenToggle;
    @FXML    Button                     loadFile;

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
        loadedFile();
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
                loadedFile();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsing(file)");
            }
        }
    }

    @FXML    ChoiceBox choiceBox_1;     private boolean choiceBox_1_Set = false;
    @FXML    ChoiceBox choiceBox_2;     private boolean choiceBox_2_Set = false;
    @FXML    ChoiceBox choiceBox_3;     private boolean choiceBox_3_Set = false;
    @FXML    ChoiceBox choiceBox_4;     private boolean choiceBox_4_Set = false;

    public void initialize() { // things which should happend imediately
        }

    private void loadedFile() {
        Graph theGraph = ParseController.INSTANCE.getGraph();
        setupChoiceBox_1(theGraph);
        setupChoiceBox_2(theGraph);
        setupChoiceBox_3(theGraph);
        setupChoiceBox_4(theGraph);
    }

    private void setupChoiceBox_1(Graph theGraph) {
        if (theGraph != null && !choiceBox_1_Set) {
            choiceBox_1.getItems().setAll(theGraph.getEdgeTypes());
            choiceBox_1_Set = true;}}

    private void setupChoiceBox_2(Graph theGraph) {
        if (theGraph != null && !choiceBox_2_Set) {
            choiceBox_2.getItems().setAll(CYCLEREMOVAL);
            choiceBox_2_Set = true;}}

    private void setupChoiceBox_3(Graph theGraph) {
        if (theGraph != null && !choiceBox_3_Set) {
            choiceBox_3.getItems().setAll(LAYERASSIGNER);
            choiceBox_3_Set = true;}}

    private void setupChoiceBox_4(Graph theGraph) {
        if (theGraph != null && !choiceBox_4_Set) {
            choiceBox_4.getItems().setAll(HORIZONTALLAYOUT);
            choiceBox_4_Set = true;}}

    public void cb_choiceBox_1(ActionEvent event) {
        this.selectedEdgeType = String.valueOf(choiceBox_1.getSelectionModel().getSelectedItem());
    }

    public void cb_choiceBox_2(ActionEvent event) {
        this.selectedCycleRemovalAlgo = String.valueOf(choiceBox_2.getSelectionModel().getSelectedItem());
    }

    public void cb_choiceBox_3(ActionEvent event) {
        this.selectedLayerAssigner = String.valueOf(choiceBox_3.getSelectionModel().getSelectedItem());
    }

    public void cb_choiceBox_4(ActionEvent event) {
        this.selectedHorizontalAlgo = String.valueOf(choiceBox_4.getSelectionModel().getSelectedItem());
    }

    public void drawOnAction(ActionEvent event) {
        if (parseInstance.getGraph() != null) {
            paneController.cleanPane();
            choiceBox_1.setDisable(true);
            choiceBox_2.setDisable(true);
            choiceBox_3.setDisable(true);
            choiceBox_4.setDisable(true);
            Graph naiveDraw = Sugiyama.processGraph(parseInstance.getGraph());
            paneController.drawDAG(naiveDraw);
            choiceBox_1.setDisable(false);
            choiceBox_2.setDisable(false);
            choiceBox_3.setDisable(false);
            choiceBox_4.setDisable(false);
        }
    }



    private void processAlgo() {
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
            case "Sugiyama":
                if (parseInstance.getGraph() != null) {
                    Graph naiveDraw = Sugiyama.processGraph(parseInstance.getGraph());
                    paneController.drawDAG(naiveDraw);
                }
                break;
            default:
                throw new IllegalArgumentException("The algo: " + selectedAlgorithm + " is not yet implemented");
        }

    }




//    public void cb_choiceBox_2(ActionEvent event) {
//        this.selected = String.valueOf(choiceBoxTwo.getSelectionModel().getSelectedItem());
//    }


    // SETTER & GETTER AREA
    public Parent getRoot() {
        return this.vBox;
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

    public String getSelectedEdgeType() {
        return selectedEdgeType;
    }

    public String getSelectedCycleRemovalAlgo() {
        return this.selectedCycleRemovalAlgo;
    }

    public String getSelectedLayerAssigner() {
        return this.selectedLayerAssigner;
    }

    public String getSelectedHorizontalAlgo() {
        return this.selectedHorizontalAlgo;
    }

}

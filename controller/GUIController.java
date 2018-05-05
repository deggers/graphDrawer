package controller;

import draw.RadialTree;
import draw.Reinhold;
import draw.WalkerImprovedDraw;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

    //@formatter:off
    @FXML    VBox                       vBox;
    @FXML    Button                     exitBtn;
    @FXML    ToggleButton               fullscreenToggle;
    @FXML    Slider                     nodeSizeSlider;
    @FXML    ChoiceBox                  choiceBoxAlgorithm;
    @FXML    Button                     loadFile;
    @FXML    Button                     nextFile;
    @FXML    Label                      fileNameLabel;

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
    @FXML    private void               loadFileOnAction() {
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

    private PaneController              paneController              = null;
    private String                      selectedTreeAlgorithm       = null;
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

    @FXML   private void drawInit() {
        if (ParseController.getInstance().getTree() != null && getSelectedTreeAlgorithm() != null) {
            cleanPane();
            setFileLabel();
            switch (getSelectedTreeAlgorithm()) {
                case "Walker":
                    Tree treeWalker = WalkerImprovedDraw.processTreeNodes(ParseController.getInstance().getTree());
                    nodeSizeSlider.setDisable(false);
                    paneController.drawTreeStructure(treeWalker);
                    break;
                case "Radial":
                    System.out.println("Selected Radial");
                    Tree radialTree = RadialTree.processTree(ParseController.getInstance().getTree());
                    nodeSizeSlider.setDisable(true);
                    paneController.drawRadialTreeStructure(radialTree);
                    break;
                case "RT":
                    System.out.println("Selected Reinhold");
                    nodeSizeSlider.setDisable(false);
                    Tree reinholdTree = Reinhold.processTree(ParseController.getInstance().getTree());
                    paneController.drawTreeStructure(reinholdTree);
                    break;
                default:
                    throw new IllegalArgumentException("The algo: " + selectedTreeAlgorithm + " is not yet implemented");
            }
        }
    }
    @FXML   private void choiceBoxAlgorithmOnAction() {
        String selectedAlgo = String.valueOf(choiceBoxAlgorithm.getSelectionModel().getSelectedItem());
        switch (selectedAlgo) {
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

    // SETTER & GETTER AREA
    public String               getSelectedTreeAlgorithm() {
        return this.selectedTreeAlgorithm;
    }
    public List<File>           getFilesInFolder() {
        return filesInFolder;
    }
    public ListIterator<File>   getFilesIter() {
        return filesIter;
    }
    public Parent               getRoot() {
        return this.vBox;
    }

    private void                setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
        drawInit();
    }
    public int                  getNodeSize() {
        return this.nodeSize;
    }

    public void                 setFilesInFolder(List<File> filesInFolder) {
        this.filesInFolder = filesInFolder;
    }
    public void                 setFilesIter(ListIterator<File> filesIter) {
        this.filesIter = filesIter;
    }
    public void                 initialize() {
        nodeSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNodeSize(newValue.intValue());
        });
    } // setup observer for nodeSizeSlider
    private void                setPane(VBox vBox) {
        this.vBox = vBox;
    }


    //    public void                 setSelectedTreeAlgorithm(String selectedTreeAlgorithm) {
//        this.selectedTreeAlgorithm = selectedTreeAlgorithm;
//    }
}
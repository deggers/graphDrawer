import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * FXML Controller class
 *
 * @author zeckzer
 */
public class FrameController {

    private PaneController paneController;
    private ParseController parseController;
//    private FrameController frameController;
    private SplitPane rootPane;
    private Main main;
    private MutableTree tree;
    private File lastParsedGraphFile;
    private String selectedTreeAlgorithm;

    private static FrameController instance;

    // does not work ?!
    public enum TreeAlgorithm {
        Naive, RT, Walker, Radial, None
    }

    @FXML
    private Slider sizeSlider;

    @FXML
    private ChoiceBox choiceAlgo;

    @FXML
    private Button loadBtn;


    public void setPane(SplitPane rootPane) {
        this.rootPane = rootPane;
    }

    public Parent getRoot() {
        return rootPane;
    }

    public File getLastParsedGraphFile() {
        return lastParsedGraphFile;
    }

    public static FrameController getInstance() {
        if (instance == null) {
            FXMLLoader loader = new FXMLLoader();
            try {            // Load root layout from fxml file.
                loader.setLocation(FrameController.class.getResource("Frame.fxml"));
                SplitPane rootPane = loader.load();
                FrameController frameController;
                frameController = loader.getController();
                frameController.setPane(rootPane);
                instance = frameController;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }

    public void init() {
        paneController = PaneController.getInstance();

        rootPane.getItems().set(1, paneController.getRoot());
        sizeSlider.setValue(paneController.getNodeSize());

        // Handle Slider value change events.
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            paneController.setNodeSize(newValue.doubleValue());
        });
    }

    @FXML
    private void locateFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GraphML files (*.graphml)", "*.graphml"),
                new FileChooser.ExtensionFilter("Newick files (*.nh)", "*.nh"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            lastParsedGraphFile = file;
            if (ParseController.getInstance().initializeParsing(file)) {
                PaneController.getInstance().draw();
            } else {
                System.out.println("was not able to ParseController.getInstance().initializeParsind(file)");
            }
        }
    }

//    private void setTree(MutableTree tree) {
//        this.tree = tree;
//    }
//
//    public MutableTree getTree() {
//        return this.tree;
//    }

    @FXML
    private void choiceAlgoChanged(ActionEvent event) {
        String selectedAlgo = String.valueOf(choiceAlgo.getSelectionModel().getSelectedItem());
        switch (selectedAlgo) {
            case "Naive":
                this.selectedTreeAlgorithm = "Naive";
                break;
            case "Radial":
                this.selectedTreeAlgorithm = "Radial";
               break;
            default:
                throw new IllegalArgumentException("The algo: " + selectedAlgo + " is not yet implemented");
        }
        paneController.draw();
    }

    String getSelectedTreeAlgorithm() {
        return this.selectedTreeAlgorithm;
    }

}

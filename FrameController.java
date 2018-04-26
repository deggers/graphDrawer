import com.sun.xml.internal.ws.assembler.jaxws.MustUnderstandTubeFactory;
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

    SplitPane rootPane;
    MutableTree tree;

    @FXML
    private Slider sizeSlider;

    @FXML
    private ChoiceBox choiceAlgo;

    @FXML
    private Button loadBtn;


    PaneController paneController;

    public void setPane(SplitPane rootPane) {
        this.rootPane = rootPane;
    }

    public Parent getRoot() {
        return rootPane;
    }

    public static FrameController getInstance() {
        FXMLLoader loader = new FXMLLoader();
        try {            // Load root layout from fxml file.
            loader.setLocation(FrameController.class.getResource("Frame.fxml"));
            SplitPane rootPane = loader.load();
            FrameController frameController;
            frameController = loader.<FrameController>getController();
            frameController.setPane(rootPane);
            return frameController;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void init() {
        paneController = PaneController.getInstance();
        rootPane.getItems().set(1, paneController.getRoot());
        sizeSlider.setValue(paneController.getNodeSize());

        // Handle Slider value change events.
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            paneController.setNodeSize(newValue.doubleValue());
        });

//        loadBtn.setOnAction((event) -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Open File");
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Newick", "*.nh"));
//            File selectedFile = fileChooser.showOpenDialog(null);
//            if (selectedFile != null) {
//                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                // IMPORTANT: Make that not-static !!!!!!!!!!!!!!!!!!!!
//                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                System.out.println("Print static input_hsg20way hardcoded ");
//                String input_hsg20way = "((((((((((((hg38:0.00988692,panTro4:0.00346722):0.00188147,panPan1:0.00264326):0.00531373,gorGor3:0.00893149):0.00925601,ponAbe2:0.0188981):0.0034213,nomLeu3:0.0230926):0.011557,((((rheMac3:0.00340845,macFas5:0.00235962):0.00535291,papAnu2:0.00850951):0.0040808,chlSab2:0.0129507):0.00594416,(nasLar1:0.00691583,rhiRox1:0.00647931):0.0119022):0.0216071):0.0217539,(calJac3:0.0352577,saiBol1:0.0327629):0.0373501):0.0612174,tarSyr2:0.142005):0.0118853,(micMur1:0.092909,otoGar3:0.128695):0.0342627):0.0166216,tupBel1:0.184214):0.0152928,mm10:0.330771):0.0876596,canFam3:0.0876596);";
//                tree = finalParserNewick.parseStringToTree(input_hsg20way);
//            }
//        });

        // Zeichne rechte Seite
//        javaFXDemoPaneController.draw();
    }

    @FXML
    protected MutableTree locateFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Newick", "*.nh"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // IMPORTANT: Make that not-static !!!!!!!!!!!!!!!!!!!!
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            System.out.println("Print static input_hsg20way hardcoded ");
            String input_hsg20way = "((((((((((((hg38:0.00988692,panTro4:0.00346722):0.00188147,panPan1:0.00264326):0.00531373,gorGor3:0.00893149):0.00925601,ponAbe2:0.0188981):0.0034213,nomLeu3:0.0230926):0.011557,((((rheMac3:0.00340845,macFas5:0.00235962):0.00535291,papAnu2:0.00850951):0.0040808,chlSab2:0.0129507):0.00594416,(nasLar1:0.00691583,rhiRox1:0.00647931):0.0119022):0.0216071):0.0217539,(calJac3:0.0352577,saiBol1:0.0327629):0.0373501):0.0612174,tarSyr2:0.142005):0.0118853,(micMur1:0.092909,otoGar3:0.128695):0.0342627):0.0166216,tupBel1:0.184214):0.0152928,mm10:0.330771):0.0876596,canFam3:0.0876596);";
            System.out.println("okay, i will return something, promised!");
            tree = finalParserNewick.parseStringToTree(input_hsg20way);
            return tree;
        }
        return null;
    }

    public MutableTree getTree() {
        return this.tree;
    }

    @FXML
    private void choiceAlgoChanged(ActionEvent event) {
        String selectedAlgo = String.valueOf(choiceAlgo.getSelectionModel().getSelectedItem());
        System.out.println("ComboBox Action (selected: " + selectedAlgo + ")");
        switch (selectedAlgo) {
            case "Naive":
                tree = getTree();
                System.out.println("tree = " + tree);
//                NaiveAlgorithm naiveAlgorithm = new NaiveAlgorithm();
//                tree = naiveAlgorithm.calculateCoordinates(tree);
//                paneController.draw(tree);
                break;
            case "TreeMap":
                TreeMapAlgorithm treeMapAlgorithm = new TreeMapAlgorithm();
                tree = treeMapAlgorithm.calculateCoordinates(tree);
                paneController.draw(tree);
                break;
            default:
                throw new IllegalArgumentException("The algo: " + selectedAlgo + " is not implemented");
        }
    }

}

import draw.Reinhold;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Node;
import model.Observe;
import model.TreeParserNewick;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        Observe.moveAction(stage, scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
//
//        String newickString = "((((((((((((hg38:0.00988692,panTro4:0.00346722):0.00188147,panPan1:0.00264326):0.00531373,"
//                + "gorGor3:0.00893149):0.00925601,ponAbe2:0.0188981):0.0034213,nomLeu3:0.0230926):0.011557," +
//                " ((((rheMac3:0.00340845,  macFas5:0.00235962):0.00535291, papAnu2:0.00850951):0.0040808," +
//                " chlSab2:0.0129507):0.00594416, (nasLar1:0.00691583,rhiRox1:0.00647931):0.0119022):0.0216071):0.0217539," +
//                " (calJac3:0.0352577,saiBol1:0.0327629):0.0373501):0.0612174,tarSyr2:0.142005):0.0118853," +
//                " (micMur1:0.092909,otoGar3:0.128695):0.0342627):0.0166216,zupBel1:0.184214):0.0152928," +
//                " mm10:0.330771):0.0876596,canFam3:0.0876596);";
//        Node root = TreeParserNewick.parseStringToTree(newickString);
//        Reinhold r= new Reinhold();
//        r.layout(root);
//        System.out.println("root = " + root);
//        TreeParserNewick tree= new TreeParserNewick();


    }
}
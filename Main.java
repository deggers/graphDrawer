import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    private static final boolean GUI = false;

    public static void main(String[] args) {
        if (GUI) {
            launch(args);
        }

        String input = "((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700, seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201, weasel:18.87953):2.09460):3.87382,dog:25.46154);";
        String input_hsg20way = "((((((((((((hg38:0.00988692,panTro4:0.00346722):0.00188147,panPan1:0.00264326):0.00531373,gorGor3:0.00893149):0.00925601,ponAbe2:0.0188981):0.0034213,nomLeu3:0.0230926):0.011557,((((rheMac3:0.00340845,macFas5:0.00235962):0.00535291,papAnu2:0.00850951):0.0040808,chlSab2:0.0129507):0.00594416,(nasLar1:0.00691583,rhiRox1:0.00647931):0.0119022):0.0216071):0.0217539,(calJac3:0.0352577,saiBol1:0.0327629):0.0373501):0.0612174,tarSyr2:0.142005):0.0118853,(micMur1:0.092909,otoGar3:0.128695):0.0342627):0.0166216,tupBel1:0.184214):0.0152928,mm10:0.330771):0.0876596,canFam3:0.0876596);";
        //MutableTree tree = finalParserNewick.parseStringToTree(input);
        //System.out.println(tree);
        Node root = TreeParserNewick.parseStringToTree("((raccoon:19.19959,bear:6.80041):0.84600,Test:1);");
        System.out.println(root);
        
        MappedTreeStructure<Node> tree = new MappedTreeStructure<>(root);
        try {
            WalkerImproved w = new WalkerImproved();
            tree = w.treeLayout(tree);
            //System.out.println("\n CONTENT: \n" + tree.echoContent());
            System.out.println("Root(s): " + tree.getRoots().toString());
            System.out.println(tree.getRoots().isEmpty());
        } catch(Exception e){
            System.out.println("Error while running Walker Algorithm");
            System.out.println(e);
        }
        
        if (!GUI) {
            System.exit(1);
        }

    }

    @Override
    public void start(Stage stage) {
        FrameController frameController = FrameController.getInstance();
        Parent root = frameController.getRoot();
//        root.setStyle("-fx-background-color:transparent;");
        Scene scene = new Scene(root);
        stage.setTitle("Awesome project for BioVis 2018");
        stage.setScene(scene);
        stage.show();
        frameController.init();
    }
}

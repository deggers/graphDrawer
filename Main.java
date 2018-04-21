import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;


public class Main extends Application {
    private static final boolean GUI = false;

    public static void main(String[] args) {
        if (GUI) {
            launch(args);
        }
        Graph graph = new Graph();
        System.out.println(graph.toString());

//        String input = "(((One:0.2,Two:0.3):0.3,(Three:0.5,Four:0.3):0.2):0.3,Five:0.7):0.0;";
//        NewickParserTry output = NewickParserTry.readNewickFormat(input);
//        System.out.println("output.toString() = " + output.toString());
//        System.out.println("input = " + input);


        String input = "((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700, seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201, weasel:18.87953):2.09460):3.87382,dog:25.46154);";
//        String input = "(Bovine:0.69395,(Gibbon:0.36079,(Orang:0.33636,(Gorilla:0.17147,(Chimp:0.19268, Human:0.11927):0.08386):0.06124):0.15057):0.54939,Mouse:1.21460);";
        NewickParserTry output = NewickParserTry.readNewickFormat(input);
//        System.out.println("output.toString() = " + output.toString());
        System.out.println("output = " + output);
        System.out.println(NewickParserTry.split(output.toString()));

//        String input_hsg20way = "((((((((((((hg38:0.00988692," +
//                "           panTro4:0.00346722):0.00188147," +
//                "          panPan1:0.00264326):0.00531373," +
//                "         gorGor3:0.00893149):0.00925601," +
//                "        ponAbe2:0.0188981):0.0034213," +
//                "       nomLeu3:0.0230926):0.011557," +
//                "      ((((rheMac3:0.00340845," +
//                "         macFas5:0.00235962):0.00535291," +
//                "        papAnu2:0.00850951):0.0040808," +
//                "       chlSab2:0.0129507):0.00594416," +
//                "      (nasLar1:0.00691583," +
//                "      rhiRox1:0.00647931):0.0119022):0.0216071):0.0217539," +
//                "     (calJac3:0.0352577," +
//                "     saiBol1:0.0327629):0.0373501):0.0612174," +
//                "    tarSyr2:0.142005):0.0118853," +
//                "   (micMur1:0.092909," +
//                "   otoGar3:0.128695):0.0342627):0.0166216," +
//                "  tupBel1:0.184214):0.0152928," +
//                " mm10:0.330771):0.0876596," +
//                "canFam3:0.0876596);";
//
//        NewickParserTry output3 = NewickParserTry.readNewickFormat(input_hsg20way);
//        System.out.println("output = " + input_hsg20way.toString());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("FunFunFun Group Graphs");
        primaryStage.setScene(new Scene(root, 1024, 760));
        primaryStage.show();
    }
}

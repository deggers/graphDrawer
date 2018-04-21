import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;


public class Main extends Application {
    private static final boolean GUI = true;

    public static void main(String[] args) {
        if (GUI == true) { launch(args); }
        Graph graph = new Graph();
        System.out.println(graph.toString());

        String input = "(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);";
//      should result in: -> Node A, B, E ssame height, children from E with C and D
        String[] output = NewickParserTry.split(input);
        System.out.println("output = " + Arrays.toString(output));

        String input2 = "(Bovine:0.69395,(Gibbon:0.36079,(Orang:0.33636,(Gorilla:0.17147,(Chimp:0.19268, Human:0.11927):0.08386):0.06124):0.15057):0.54939,Mouse:1.21460);";
        String[] output2 = NewickParserTry.split(input2);
        System.out.println("output = " + Arrays.toString(output2));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("FunFunFun Group Graphs");
        primaryStage.setScene(new Scene(root, 1024, 760));
        primaryStage.show();
    }
}

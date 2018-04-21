import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("FunFunFun Group Graphs");
        primaryStage.setScene(new Scene(root, 1024, 760));
        primaryStage.show();
    }

    private static Graph getGraph() {

        return new Graph();
    }
    public static void main( String[] args ) {
        launch(args);
        Graph graph = getGraph();
//        System.out.println(graph.toString());
//        System.out.println("blabla\tblabla");
//       // String input = "(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);";
//        String input = " ";
//        NewickParser.isValidFormat(input);
//        System.out.println(" = " +  NewickParser.isValidFormat(input));
    }
}

import java.io.File;

import Sugiyama.AssignLayer;
import Sugiyama.CrossingMin;
import Sugiyama.CycleBreaker;
import controller.GUIController;
import controller.ParseController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import structure.Graph;

//@formatter:off
public class Main extends Application {



    private static File file = new File("C:\\Users\\dusty\\Desktop\\Zeckzer\\Vorlesung\\Baeume_1\\Baeume\\Data\\Software-Engineering\\test.graphml");

    public static void main(String[] args) {

//        ParseController.INSTANCE.initParsing(file);
//        Graph theWholeGraph = ParseController.INSTANCE.getGraph();
//        Graph partialGraph = theWholeGraph.copyWithRestrains("Graph1");
//        System.out.println("partialGraph = " + partialGraph);
//
//        System.out.println("Beginn with Sugiyama.CycleBreaker\n");
//        CycleBreaker.GreedyCycleRemoval(partialGraph);
//
//        System.out.println("Beginn with LayerAssignment\n");
//        AssignLayer.topologicalPath(partialGraph);
//
//
//        System.out.println("Beginn with Sugiyama.CrossingMin\n");
//        CrossingMin.allPermutation(partialGraph,true);
//        System.out.println("Crossings = " + partialGraph.getCrossings());
//        System.out.println(partialGraph.getLayerMap());
//        System.out.println("partialGraph.getEdges() = " + partialGraph.getEdges());

            launch(args);

    }

    @Override public void start(Stage stage) throws Exception {
        GUIController guiController = GUIController.getInstance();
        Parent root = guiController.getRoot();
        Scene scene = new Scene(root);


        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        Observe.moveAction(stage, scene);
        guiController.init();
    }


}

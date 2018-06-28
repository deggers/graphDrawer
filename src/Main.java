import java.io.File;

import Sugiyama.AssignHorizontalPosition;
import Sugiyama.AssignLayer;
import Sugiyama.CrossingMin;
import Sugiyama.CycleBreaker;
import controller.GUIController;
import controller.PaneController;
import controller.ParseController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import structure.Edge;
import structure.Graph;
import structure.GraphNode;

//@formatter:off
public class Main extends Application {



    private static File file = new File("C:\\Users\\dusty\\Desktop\\Zeckzer\\Vorlesung\\Baeume_1\\Baeume\\Data\\Software-Engineering\\test.graphml");

    public static void main(String[] args) {

        Graph myGraph =  new Graph();
        GraphNode node1 = new GraphNode("1",false);
        GraphNode d1 = new GraphNode("D1",true);
        GraphNode d2 = new GraphNode("D2",true);
        GraphNode d3 = new GraphNode("D3",true);
        GraphNode d4 = new GraphNode("D4",true);
        GraphNode node2 = new GraphNode("2",false);

        Edge e1 = new Edge(node1,d1);
        Edge e2 = new Edge(d1,d2);
        Edge e3 = new Edge(d2,node2);

        Edge e4 = new Edge(node1,d3);
        Edge e5 = new Edge(d3,d4);
        Edge e6 = new Edge(d4,node2);

        myGraph.addEdge(e1);
        myGraph.addEdge(e2);
        myGraph.addEdge(e3);
        myGraph.addEdge(e4);
        myGraph.addEdge(e5);
        myGraph.addEdge(e6);

        CycleBreaker.GreedyCycleRemoval(myGraph);
        AssignLayer.topologicalPath(myGraph);
        CrossingMin.baryCenter_naive(myGraph,true,3);
        AssignHorizontalPosition.processNaive(myGraph);

        System.out.println("myGraph = " + myGraph.getNodes());
        System.out.println("myGraph = " + myGraph.getEdges());

        PaneController.getInstance().drawDAG(myGraph);

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

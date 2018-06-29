import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

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

//        Graph myGraph =  new Graph();
//        GraphNode node1 = new GraphNode("1",false);
//        GraphNode node2 = new GraphNode("2",false);
//        GraphNode node3 = new GraphNode("3",false);
//        GraphNode node4 = new GraphNode("4",false);
//        GraphNode node5 = new GraphNode("5",false);
//        GraphNode node6 = new GraphNode("6",false);
//        GraphNode node7 = new GraphNode("7",false);
//
//        GraphNode d1 = new GraphNode("D1",true);
//        GraphNode d2 = new GraphNode("D2",true);
//        GraphNode d3 = new GraphNode("D3",true);
//        GraphNode d4 = new GraphNode("D4",true);
//        GraphNode d5 = new GraphNode("D5",true);
//        GraphNode d6 = new GraphNode("D6",true);
//        GraphNode d7 = new GraphNode("D7",true);
//
//        Edge e1 = new Edge(node1,node5);    myGraph.addEdge(e1);
//        Edge e2 = new Edge(node1,node6);    myGraph.addEdge(e2);
//        Edge e7 = new Edge(node4,d4);       myGraph.addEdge(e7);
//        Edge e4 = new Edge(d1,d5);          myGraph.addEdge(e4);
//        Edge e5 = new Edge(d2,d6);          myGraph.addEdge(e5);
//        Edge e3 = new Edge(node1,node7);    myGraph.addEdge(e3);
//        Edge e6 = new Edge(d3,node7);       myGraph.addEdge(e6);
//        Edge e8 = new Edge(node4,d7);       myGraph.addEdge(e8);
//
//        LinkedHashMap<Integer,LinkedList<GraphNode>> myModifiedLayerMap = new LinkedHashMap<>();
//        LinkedList<GraphNode> layer1 = new LinkedList<>(Arrays.asList(node1,node2,d1,d2,d3,node4));
//        LinkedList<GraphNode> layer2 = new LinkedList<>(Arrays.asList(node5,node6,d4,d5,d6,node7,d7));
//        myModifiedLayerMap.put(1,layer1);
//        myModifiedLayerMap.put(2,layer2);
//
//        AssignLayer.topologicalPath(myGraph);
//        myGraph.setLayerMap(myModifiedLayerMap);
//        AssignHorizontalPosition.processBK(myGraph);
//        System.out.println("Going to give u all the conflict edges :-)");
//        for (Edge e: myGraph.getEdges())
//            if (e.isMarkedType1Conflict()) System.out.println("e = " + e);
//        System.out.println("that have been them all..\n");

//        PaneController.getInstance().drawDAG(myGraph);

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

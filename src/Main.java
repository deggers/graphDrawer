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
        boolean VERBOSE = true;

//        Graph myGraph =  new Graph();
//        GraphNode node1 = new GraphNode("1",false,1);
//        GraphNode node2 = new GraphNode("2",false,1);
//        GraphNode node3 = new GraphNode("3",false,1);
//        GraphNode node4 = new GraphNode("4",false,2);
//        GraphNode node5 = new GraphNode("5",false,2);
//        GraphNode node6 = new GraphNode("6",false,2);
//
//        GraphNode d1 = new GraphNode("D1",true,1);
//        GraphNode d2 = new GraphNode("D2",true,1);
//        GraphNode d3 = new GraphNode("D3",true,1);
//        GraphNode d4 = new GraphNode("D4",true,2);
//        GraphNode d5 = new GraphNode("D5",true,2);
//        GraphNode d6 = new GraphNode("D6",true,2);
//        GraphNode d7 = new GraphNode("D7",true,2);
//
//        Edge e1 = new Edge(node4,node1);    myGraph.addEdge(e1);
//        Edge e2 = new Edge(node5,node1);    myGraph.addEdge(e2);
//        Edge e3 = new Edge(node6,node1);    myGraph.addEdge(e3);
//        Edge e4 = new Edge(d5,d1);          myGraph.addEdge(e4);
//        Edge e5 = new Edge(d6,d2);          myGraph.addEdge(e5);
//        Edge e6 = new Edge(node6,d3);       myGraph.addEdge(e6);
//        Edge e7 = new Edge(d7,node3);       myGraph.addEdge(e7);
//        Edge e8 = new Edge(d4,node3);       myGraph.addEdge(e8);

//        LinkedHashMap<Integer,LinkedList<GraphNode>> myModifiedLayerMap = new LinkedHashMap<>();
//        LinkedList<GraphNode> layer1 = new LinkedList<>(Arrays.asList(node1,node2,d1,d2,d3,node3));
//        LinkedList<GraphNode> layer2 = new LinkedList<>(Arrays.asList(node4,node5,d4,d5,d6,node6,d7));
//        myModifiedLayerMap.put(1,layer1);
//        myModifiedLayerMap.put(2,layer2);
//        myGraph.setLayerMap(myModifiedLayerMap);

        Graph myGraph =  new Graph();
//------Layer1-----------------------------------------------------------
        GraphNode node11 = new GraphNode("11",false,1);
        GraphNode node12 = new GraphNode("12",false,1);
//------Layer2-----------------------------------------------------------
        GraphNode node21 = new GraphNode("21",false,2);
        GraphNode node22 = new GraphNode("22",false,2);
        GraphNode node23 = new GraphNode("23",true,2);
        GraphNode node24 = new GraphNode("24",false,2);
        GraphNode node25 = new GraphNode("25",true,2);
        GraphNode node26 = new GraphNode("26",true,2);
        GraphNode node27 = new GraphNode("27",false,2);
        GraphNode node28 = new GraphNode("28",false,2);
//------Layer3-----------------------------------------------------------
        GraphNode node31 = new GraphNode("31",false,3);
        GraphNode node32 = new GraphNode("32",false,3);
        GraphNode node33 = new GraphNode("33",true,3);
        GraphNode node34 = new GraphNode("34",true,3);
        GraphNode node35 = new GraphNode("35",true,3);
        GraphNode node36 = new GraphNode("36",false,3);
//------Layer4-----------------------------------------------------------
        GraphNode node41 = new GraphNode("41",false,4);
        GraphNode node42 = new GraphNode("42",false,4);
        GraphNode node43 = new GraphNode("43",true,4);
        GraphNode node44 = new GraphNode("44",true,4);
        GraphNode node45 = new GraphNode("45",true,4);
        GraphNode node46 = new GraphNode("46",false,4);
        GraphNode node47 = new GraphNode("47",true,4);
//------Layer5-----------------------------------------------------------
        GraphNode node51 = new GraphNode("51",false,5);
        GraphNode node52 = new GraphNode("52",false,5);
        GraphNode node53 = new GraphNode("53",false,5);

        Edge e1 = new Edge(node11,node21);    myGraph.addEdge(e1);
        Edge e2 = new Edge(node11,node26);    myGraph.addEdge(e2);
        Edge e3 = new Edge(node11,node28);    myGraph.addEdge(e3);

        Edge e4 = new Edge(node12,node23);    myGraph.addEdge(e4);
        Edge e5 = new Edge(node12,node25);    myGraph.addEdge(e5);

        Edge e6 = new Edge(node22,node32);    myGraph.addEdge(e6);
        Edge e7 = new Edge(node23,node32);    myGraph.addEdge(e7);
        Edge e8 = new Edge(node24,node32);    myGraph.addEdge(e8);
        Edge e9 = new Edge(node25,node33);    myGraph.addEdge(e9);
        Edge e10 = new Edge(node26,node34);    myGraph.addEdge(e10);
        Edge e11 = new Edge(node27,node32);    myGraph.addEdge(e11);
        Edge e12 = new Edge(node27,node36);    myGraph.addEdge(e12);
        Edge e13 = new Edge(node28,node32);    myGraph.addEdge(e13);
        Edge e14 = new Edge(node28,node35);    myGraph.addEdge(e14);

        Edge e15 = new Edge(node31,node41);    myGraph.addEdge(e15);
        Edge e16 = new Edge(node31,node42);    myGraph.addEdge(e16);
        Edge e17 = new Edge(node31,node46);    myGraph.addEdge(e17);
        Edge e18 = new Edge(node33,node44);    myGraph.addEdge(e18);
        Edge e19 = new Edge(node34,node45);    myGraph.addEdge(e19);
        Edge e20 = new Edge(node35,node46);    myGraph.addEdge(e20);
        Edge e21 = new Edge(node36,node47);    myGraph.addEdge(e21);
        Edge e22 = new Edge(node36,node43);    myGraph.addEdge(e22);

        Edge e23 = new Edge(node41,node51);    myGraph.addEdge(e23);
        Edge e24 = new Edge(node41,node52);    myGraph.addEdge(e24);
        Edge e25 = new Edge(node42,node52);    myGraph.addEdge(e25);
        Edge e26 = new Edge(node43,node51);    myGraph.addEdge(e26);
        Edge e27 = new Edge(node44,node53);    myGraph.addEdge(e27);
        Edge e28 = new Edge(node45,node53);    myGraph.addEdge(e28);
        Edge e29 = new Edge(node46,node53);    myGraph.addEdge(e29);
        Edge e30 = new Edge(node47,node53);    myGraph.addEdge(e30);

        LinkedHashMap<Integer,LinkedList<GraphNode>> myModifiedLayerMap = new LinkedHashMap<>();
        LinkedList<GraphNode> layer1 = new LinkedList<>(Arrays.asList(node11,node12));
        LinkedList<GraphNode> layer2 = new LinkedList<>(Arrays.asList(node21,node22,node23,node24,node25,node26,node27,node28));
        LinkedList<GraphNode> layer3 = new LinkedList<>(Arrays.asList(node31,node32,node33,node34,node35,node36));
        LinkedList<GraphNode> layer4 = new LinkedList<>(Arrays.asList(node41,node42,node43,node44,node45,node46,node47));
        LinkedList<GraphNode> layer5 = new LinkedList<>(Arrays.asList(node51,node52,node53));
        myModifiedLayerMap.put(1,layer1);
        myModifiedLayerMap.put(2,layer2);
        myModifiedLayerMap.put(3,layer3);
        myModifiedLayerMap.put(4,layer4);
        myModifiedLayerMap.put(5,layer5);
        myGraph.setLayerMap(myModifiedLayerMap);


        AssignHorizontalPosition.processBK(myGraph);
        if (VERBOSE) {
        System.out.println("Going to give u all the conflict edges :-)");
        for (Edge e: myGraph.getEdges())
            if (e.isMarkedType1Conflict()) System.out.println("e = " + e);
        System.out.println("that have been them all..\n");
        }

//        System.out.println("myGraph = " + myGraph.getAlignBlock());
//        System.out.println("my = " + myGraph.getRootBlock());
//        PaneController.getInstance().drawDAG(myGraph);
//        launch(args);


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

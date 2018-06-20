import java.io.File;
import java.util.*;

import Sugiyama.AssignLayer;
import Sugiyama.CrossingMin;
import Sugiyama.CycleBreaker;
import controller.ParseController;
import structure.Graph;

//@formatter:off
public class Main {
    private static File file = new File("C:\\Users\\dusty\\Desktop\\Zeckzer\\Vorlesung\\Baeume_1\\Baeume\\Data\\Software-Engineering\\test.graphml");

    public static void main(String[] args) {

        ParseController.INSTANCE.initParsing(file);
        Graph theWholeGraph = ParseController.INSTANCE.getGraph();
        Graph partialGraph = theWholeGraph.copyWithRestrains("Graph1");
        System.out.println("partialGraph = " + partialGraph);

        System.out.println("Beginn with Sugiyama.CycleBreaker\n");
        CycleBreaker.GreedyCycleRemoval(partialGraph);


        System.out.println("Beginn with LayerAssignment\n");
        AssignLayer.topologicalPath(partialGraph);


        System.out.println("Beginn with Sugiyama.CrossingMin\n");
        CrossingMin.naiveAlgo(partialGraph);
        System.out.println("partialGraph = " + partialGraph.getLayerMap());
        System.out.println("partialGraph.getEdges() = " + partialGraph.getEdges());
        System.out.println("Crossings = " + partialGraph.getCrossings());
    }
}

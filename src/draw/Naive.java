package draw;

import controller.GUIController;
import structure.*;
import Sugiyama.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Naive {
    private static final boolean VERBOSE = false;
    private static Graph partialGraph = null;

    public static Graph processGraph(Graph theGraph) {
        System.out.println("Graph: \n Nodes: " + theGraph.getNodes().size() + ", Edges: " + theGraph.getEdges().size());
        String selectedEdgeType = Objects.requireNonNull(GUIController.getInstance()).getSelectedEdgeType();
//        if (partialGraph!=null){
//            if (partialGraph.edgeType == selectedEdgeType && partialGraph.graphname == GUIController.getInstance().getFileName()){
//                partialGraph.resetAllPorts();
//                return partialGraph;
//            }
//        }
        partialGraph = theGraph.copyWithRestrains(selectedEdgeType);
        System.out.println("Partial: \n Nodes: " + partialGraph.getNodes().size() + ", Edges: " + partialGraph.getEdges().size());

////      1. Remove all Cycles
        System.out.println("remove all cycles");
        CycleBreaker.DFS_Florian(partialGraph);

        Graph partialGraph2 = theGraph.copyWithRestrains(selectedEdgeType);
        CycleBreaker.Berger_Shor(partialGraph2);

        System.exit(1);

////      2.  Layer Assignment: Vertices are assigned to layers.
        System.out.println("Layer assignment");
        AssignLayer.topologicalPath(partialGraph);


        System.out.println(partialGraph.getEdgesInMap());
        System.out.println("out");
        System.out.println(partialGraph.getEdgesOutMap());

        System.out.println("Crossing Minimization");
        CrossingMin.allPermutation(partialGraph,true);
        System.out.println("from x crossing to y crossing :)");


        // simple algo to give nodes an coordinate to draw something :)
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = partialGraph.getLayerMap();
        System.out.println(layerMap);
        for (int layer : layerMap.keySet()) {
            int x = 1;
            System.out.println("layer = " + layerMap.get(layer));
            for (GraphNode node : layerMap.get(layer)) {
                node.y = layer;
                node.x = x;
                x++;
            }
        }

        return partialGraph;
    }
}

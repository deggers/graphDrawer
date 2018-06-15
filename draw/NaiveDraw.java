package draw;

import controller.GUIController;
import model.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class NaiveDraw {
    private static final boolean VERBOSE = false;
    private static Graph partialGraph = null;

    public static Graph processGraph(Graph theGraph) {
        System.out.println("Graph: \n Nodes: " + theGraph.getNodes().size() + ", Edges: " + theGraph.getEdges().size());
        String selectedEdgeType = Objects.requireNonNull(GUIController.getInstance()).getSelectedEdgeType();
        if (partialGraph!=null){
            if (partialGraph.edgeType == selectedEdgeType && partialGraph.graphname == GUIController.getInstance().getFileName()){
                return partialGraph;
            }
        }
        partialGraph = theGraph.copyWithRestrains(selectedEdgeType);
        System.out.println("Partial: \n Nodes: " + partialGraph.getNodes().size() + ", Edges: " + partialGraph.getEdges().size());


////      1. Remove all Cycles
        System.out.println("remove all cycles");
        CycleBreaker.DFS_Florian(partialGraph);
//        System.out.println("cyclefree got " + drawGraph);
//        if (verbose) {
//            System.out.println(drawGraph);
//            System.out.println(drawGraph.copyEdgeSet());
//            System.out.println("g.getNodeSet() = " + drawGraph.copyNodeSet());
//        }

////      2.  Layer Assignment: Vertices are assigned to layers.
        System.out.println("Layer assignment");
        AssignLayer.topologicalPath(partialGraph);
//        System.out.println("partialGraph = " + partialGraph);
//        System.out.println("Partial: \n Nodes: " + partialGraph.getNodes().size() + ", Edges: " + partialGraph.getEdges().size());
//        System.out.println(partialGraph.getEdges());

//        if (verbose) {
//            System.out.println(drawGraph);
//            System.out.println(drawGraph.copyEdgeSet());
//            System.out.println("g.getNodeSet() = " + drawGraph.copyNodeSet());
//        }

        System.out.println("Crossing Minimization");
        CrossingMin.naiveAlgo(partialGraph);
        System.out.println("from x crossing to y crossing :)");


        // simple algo to give nodes an coordinate to draw something :)
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = partialGraph.getLayerMap();
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

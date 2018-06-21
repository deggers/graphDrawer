package draw;

import controller.GUIController;
import structure.*;
import Sugiyama.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Sugiyama {
    private static final boolean VERBOSE = false;
    private static Graph partialGraph = null;

    public static Graph processGraph(Graph theGraph) {
        GUIController GUIinstance = GUIController.getInstance();


        if (VERBOSE) System.out.println("Graph: \n Nodes: " + theGraph.getNodes().size() + ", Edges: " + theGraph.getEdges().size());
        String selectedEdgeType = Objects.requireNonNull(GUIController.getInstance()).getSelectedEdgeType();
        partialGraph = theGraph.copyWithRestrains(selectedEdgeType);
        partialGraph.resetAllPorts();
        System.out.println("Working on Graph with EdgeType: " + selectedEdgeType);
        if (VERBOSE) System.out.println("Partial: \n Nodes: " + partialGraph.getNodes().size() + ", Edges: " + partialGraph.getEdges().size());

        // make all buttons grey so you need, it's working...


//        if (partialGraph!=null){
//            if (partialGraph.edgeType == selectedEdgeType && partialGraph.graphname == GUIController.getInstance().getFileName()){
//                partialGraph.resetAllPorts();
//                return partialGraph;
//            }
//        }

        if (VERBOSE) System.out.println("remove all cycles");
        String selectedCycleRemovalAlgo = GUIController.getInstance().getSelectedCycleRemovalAlgo();
        System.out.println("selectedCycleRemovalAlgo = " + selectedCycleRemovalAlgo);
        if (selectedCycleRemovalAlgo == null) throw new RuntimeException ("no CycleRemovalAlgo selected!");


        switch (selectedCycleRemovalAlgo) {
            case "Greedy_Eades'90"  : CycleBreaker.GreedyCycleRemoval(partialGraph);  break;
            case "BergerShor'87"    : CycleBreaker.Berger_Shor(partialGraph); break;
            case "DFS_Florian"      : CycleBreaker.DFS_Florian(partialGraph); break;
            default                 : System.out.println("unknown algo selected, wtf!"); break;
        }


////      2.  Layer Assignment: Vertices are assigned to layers.
        System.out.println("Layer assignment");

        switch (GUIController.getInstance().getSelectedLayerAssigner()) {
            case "TopoSort"             : AssignLayer.topologicalPath(partialGraph); break;
            case "Longest Path"         : AssignLayer.longestPath(partialGraph); break;
            default                     : System.out.println(" mistake in AssignlayerSugyi"); break;
        }




        System.out.println("Crossing Minimization");
        CrossingMin.allPermutation(partialGraph,true);
        System.out.println("from x crossing to y crossing :)");


        // simple algo to give nodes an coordinate to draw something :)
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = partialGraph.getLayerMap();
        for (int layer : layerMap.keySet()) {
            int x = 1;
//            System.out.println("layer = " + layerMap.get(layer));
            for (GraphNode node : layerMap.get(layer)) {
                node.y = layer;
                node.x = x;
                x++;
            }
        }

        return partialGraph;
    }
}

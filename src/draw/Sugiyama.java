package draw;

import controller.GUIController;
import structure.*;
import Sugiyama.*;

import java.util.Objects;

public class Sugiyama {
    private static final boolean VERBOSE = false;
    private static Graph partialGraph = null;

    public static Graph processGraph(Graph theGraph) {
        boolean bidirectional;
        int sweeps;

        if (VERBOSE) System.out.println("Graph: \n Nodes: " + theGraph.getNodes().size() + ", Edges: " + theGraph.getEdges().size());
        String selectedEdgeType = Objects.requireNonNull(GUIController.getInstance()).getSelectedEdgeType();
        partialGraph = theGraph.copyWithRestrains(selectedEdgeType);
        System.out.println("Working on Graph with EdgeType: " + selectedEdgeType);
        if (VERBOSE) System.out.println("Partial: \n Nodes: " + partialGraph.getNodes().size() + ", Edges: " + partialGraph.getEdges().size());

        GUIController.getInstance().setGraphInfo(String.format("Graph: %s Nodes, %s Edges || ", partialGraph.getNodes().size(), partialGraph.getEdges().size()));

//        if (partialGraph!=null){
//            if (partialGraph.edgeType == selectedEdgeType && partialGraph.graphname == GUIController.getInstance().getFileName()){
//                partialGraph.resetAllPorts();
//                return partialGraph;
//            }
//        }

        if (VERBOSE) System.out.println("remove all cycles");
        String selectedCycleRemovalAlgo = GUIController.getInstance().getSelectedCycleRemovalAlgo();
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
        String  inputParams1    = GUIController.getInstance().getInputParams_1();
        if (inputParams1.length() == 2) {
            bidirectional = inputParams1.contains("B");
            sweeps = Character.getNumericValue(inputParams1.charAt(1));
        } else {
            bidirectional = false;
            sweeps = 1;
        }
        System.out.println(GUIController.getInstance().getSelectedCrossingMinAlgo());
        switch (GUIController.getInstance().getSelectedCrossingMinAlgo()) {
            case "Permutation"  :   CrossingMin.allPermutation(partialGraph, bidirectional,sweeps); break;
            case "BaryCenter_naive" : CrossingMin.baryCenter_naive(partialGraph, bidirectional,sweeps); break;
            case "Bary_Viola" : CrossingMin.baryCenterViola(partialGraph); break;
            default             :   System.out.println(" mhm, missin something here! Sugiyama"); break;
        }
        GUIController.getInstance().setCrossingLabel("Crossings: " + partialGraph.getCrossings().toString());

        String HorizontalAlignment = GUIController.getInstance().getSelectedHorizontalAlgo();
        switch (GUIController.getInstance().getSelectedHorizontalAlgo()) {
            case "BK"   : AssignHorizontalPosition.processBK(partialGraph); break;
            case "Naive": AssignHorizontalPosition.processNaive(partialGraph); break;
            case "None" : break;
        }

        // simple algo to give nodes an coordinate to draw something :)



        partialGraph.resetAllPorts();
        return partialGraph;
    }
}

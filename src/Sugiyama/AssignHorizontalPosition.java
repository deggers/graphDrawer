package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.*;

//@formatter:off

// aka Brandes Köpf
public class AssignHorizontalPosition {

    public static Graph processBK(Graph inputGraph) {
        // step 1 -> VerticalAlignment(direction)
        /* In the first step, referred to as vertical alignment, we try to align each vertex with either
        its median upper or its median lower neighbor, and we resolve alignment conflicts (of type 0) either
        in a leftmost or a rightmost fashion. We thus obtain one vertical alignment for each combination of
        upward and downward alignment with leftmost and rightmost conflict resolution. */
        Graph graphSW = VerticalAlignment(inputGraph,"SW");
//        Graph graphNW = VerticalAlignment(inputGraph,"NW");
//        Graph graphNE = VerticalAlignment(inputGraph,"NE");
//        Graph graphSE = VerticalAlignment(inputGraph,"SE");

        // step 2 -> Compaction(direction)
            graphSW = Compaction(graphSW, "SW");
//        graphNW = Compaction(graphNW, "NW");
//        graphNE = Compaction(graphNE, "NE");
//        graphSE = Compaction(graphSE, "SE");

        // step 3
//        Balancing(graphNW,graphNE,graphSW,graphSE);
        return graphSW;
    }

    private static Graph VerticalAlignment(Graph graph, String direction) {
        int numberOfLayer = graph.getLayerMap().keySet().size();
        ArrayList<Edge> blockEdges = new ArrayList<>();

        if (direction.contains("S")){  // top-to-bottom
            for (int intLayer = 1; intLayer < numberOfLayer ; intLayer++) {
                MarkTypeOneConflicts(graph, intLayer, intLayer+1);
                LinkedList<GraphNode> nodesOnLayer = graph.getLayerMap().get(intLayer);
                for (GraphNode nodeOnLayer : nodesOnLayer) {
                    ArrayList<GraphNode> neighbours = graph.getAdjacentNodes(nodeOnLayer, intLayer + 1);
                    blockEdges.add(new Edge(nodeOnLayer, getMedianNeighbour(neighbours, direction)));
                }
            }
        } else if (direction.contains("N")) {// bottom-to-top
            for (int intLayer = numberOfLayer; intLayer > 1; intLayer--) {
                MarkTypeOneConflicts(graph, intLayer, intLayer-1);
                LinkedList<GraphNode> nodesOnLayer = graph.getLayerMap().get(intLayer);
                for (GraphNode nodeOnLayer : nodesOnLayer) {
                    ArrayList<GraphNode> neighbours = graph.getAdjacentNodes(nodeOnLayer, intLayer - 1);
                    blockEdges.add(new Edge(nodeOnLayer, getMedianNeighbour(neighbours, direction)));
                }
            }
        } else System.out.println(" what da fuck? Greetings from BK - VerticalAlignment");
        return new Graph(graph, blockEdges);
    }

    private static void MarkTypeOneConflicts(Graph graph, int layer1, int layer2) {
        int k0 = 0, k1 = 0, l = 0;
        LinkedList<GraphNode> nodesOnLayer1 = graph.getLayerMap().get(layer1);
        LinkedList<GraphNode> nodesOnLayer2 = graph.getLayerMap().get(layer2);
        for (GraphNode nodeOnLayer : nodesOnLayer2) {
            if (nodeOnLayer.isDummy() || nodesOnLayer2.indexOf(nodeOnLayer) == nodesOnLayer2.size()-1){
                ArrayList<GraphNode> neighbours = graph.getAdjacentNodes(nodeOnLayer, layer1);
                if (neighbours.size()==1 && neighbours.get(0).isDummy()){
                    //Kante ist inneres Segment
                    k1 = nodesOnLayer1.size()-1;
                    if (nodeOnLayer.isDummy()){
                        k1 = nodesOnLayer1.indexOf(neighbours.get(0)); //position des oberen nachbarn im inneren segment
                    }
                    while (l <= nodesOnLayer2.indexOf(nodeOnLayer)){
                        GraphNode bottomNodeForConflictFinding = nodesOnLayer2.get(l);
                        for (GraphNode upperneighbor : graph.getAdjacentNodes(bottomNodeForConflictFinding, layer1)) {
                            if (nodesOnLayer1.indexOf(upperneighbor) < k0 || nodesOnLayer1.indexOf(upperneighbor) > k1){
                                graph.getEdgeBetween(upperneighbor, bottomNodeForConflictFinding).setMarkedType1Conflict(true);
                            }
                        }
                        l++;
                    }
                    k0 = k1;
                }
            }
        }
    }

    private static Graph Compaction(Graph graph, String direction) {

        return graph;
    }

    private static void Balancing(Graph graphNW, Graph graphNE, Graph graphSW, Graph graphSE) {
        // Balance for all Graphs
    }


        private static GraphNode getMedianNeighbour(ArrayList<GraphNode> neighbours, String direction) {
        int neighbourSize = neighbours.size();
        int median = neighbourSize % 2 != 0 ? neighbourSize / 2 : -1;

        switch (direction) {
            case "SW": // fallthrough
            case "NW": // the both cases the left one if ambiguous
                System.out.println("Case West");
                if (median >= 0) { // we really have one median, easy
                    return neighbours.get(median);
                }
                else { // we have not a clear median, take left one
                    return neighbours.get((neighbourSize / 2) - 1);
                }
            case "SE": // fallthrough
            case "NE": // both cases, take the right one if ambiguous
                System.out.println("Case East");
                if (median >= 0) { // we really have one median, easy
                    return neighbours.get(median);
                }
                else { // we have not a clear median, take right one
                    return neighbours.get( (neighbourSize / 2) );
                }
            default: System.out.println(" what happend here? BK greets"); break;
        }
        return null;
    }

}

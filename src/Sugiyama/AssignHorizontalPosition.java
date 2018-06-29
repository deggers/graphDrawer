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
//                LinkedList<GraphNode> nodesOnLayer = graph.getLayerMap().get(intLayer);
//                for (GraphNode nodeOnLayer : nodesOnLayer) {
//                    ArrayList<GraphNode> neighbours = graph.getAdjacentNodes(nodeOnLayer, intLayer + 1);
//                    blockEdges.add(new Edge(nodeOnLayer, getMedianNeighbour(neighbours, direction)));
//                }
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

    private static void MarkTypeOneConflicts(Graph graph, int layer1_int, int layer2_int) {
        LinkedList<GraphNode> layer_1 = graph.getLayerMap().get(layer1_int);
        LinkedList<GraphNode> layer_2 = graph.getLayerMap().get(layer2_int);
        int k0 = 0, k1 = 0, l = 0;                                                                           // Zeile 2
        for (GraphNode L_1 : layer_2) {                                                                      // Zeile 3
            if (isLastNode(layer_2, L_1) || incidentToInnerSegment(graph, L_1, layer1_int) ){                // Zeile 4
                k1 = layer_1.size()-1;                                                                       // Zeile 5
                if (incidentToInnerSegment(graph,L_1,layer1_int))                                            // Zeile 6
                        k1 = layer_1.indexOf(getUpperNodeFromInnerSegment(graph,L_1,layer1_int));            // Zeile 7
                while (l <= layer_2.indexOf(L_1)){                                                           // Zeile 8
                        GraphNode bottomNode = layer_2.get(l);
                        for (GraphNode upperneighbor : graph.getAdjacentNodes(bottomNode, layer1_int))       // Zeile 9
                            if (layer_1.indexOf(upperneighbor) < k0 || layer_1.indexOf(upperneighbor) > k1)  // Zeile 10
                                graph.getEdgeBetween(upperneighbor, bottomNode).setMarkedType1Conflict(true);
                        l++;
                    }
                    k0 = k1;
            }
        }
    }
    private static boolean isLastNode(LinkedList<GraphNode> nodesLayer, GraphNode node){
        return node == nodesLayer.getLast();
    }


    private static boolean incidentToInnerSegment(Graph g, GraphNode u, int layer_int){
        ArrayList<GraphNode> neighbors = g.getAdjacentNodes(u, layer_int);
        for (GraphNode node : neighbors)
            if (node.isDummy()) return true;
        return false;
    }

    private static GraphNode getUpperNodeFromInnerSegment(Graph g, GraphNode u, int layer_int){
        ArrayList<GraphNode> neighbors = g.getAdjacentNodes(u, layer_int);
        for (GraphNode node : neighbors)
            if (node.isDummy()) {
                Edge e =  g.getEdgeBetween(u,node);
                return e.tail.equals(u) ? e.head : e.tail;
            }
        return null;
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

    public static void processNaive(Graph g) {
        int layerdepth = g.getLayerMap().keySet().size();
        for (int layer = 1; layer <= layerdepth; layer++){
            int x = 1;
            for (GraphNode node: g.getLayerMap().get(layer)){
                node.x = x;
                node.y = layer;
                x++;
            }
        }

    }

}

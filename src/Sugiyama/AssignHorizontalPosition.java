package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.*;

//@formatter:off

// aka Brandes Köpf
public class AssignHorizontalPosition {
    private LinkedHashMap<GraphNode, ArrayList<GraphNode>> medianNeighboursMap;

    public static Graph processBK(Graph inputGraph) {
        // step 1 -> BlockBuilding(direction)
//        Graph graphNW = BlockBuilding(inputGraph,"NW");
//        Graph graphNE = BlockBuilding(inputGraph,"NE");
        Graph graphSW = BlockBuilding(inputGraph,"SW");
//        Graph graphSE = BlockBuilding(inputGraph,"SE");

        // step 2 -> Compaction(direction)
//        graphNW = Compaction(graphNW, "NW");
//        graphNE = Compaction(graphNE, "NE");
        graphSW = Compaction(graphSW, "SW");
//        graphSE = Compaction(graphSE, "SE");

        // step 3
//        Balancing(graphNW,graphNE,graphSW,graphSE);
        return graphSW;
    }

    private static Graph BlockBuilding(Graph graph, String direction) {
        int numberOfLayer = graph.getLayerMap().keySet().size();
        ArrayList<Edge> blockEdges = new ArrayList<>();

        if (direction.contains("S")){  // top-to-bottom
            for (int intLayer = 1; intLayer < numberOfLayer ; intLayer++) {
                LinkedList<GraphNode> nodesOnLayer = graph.getLayerMap().get(intLayer);
                for (GraphNode nodeOnLayer : nodesOnLayer) {
                    System.out.println("nodeOnLayer = " + nodeOnLayer);
                    ArrayList<GraphNode> neighbours = graph.getAdjacentNodes(nodeOnLayer, intLayer + 1);
                    System.out.println("neighbours = " + neighbours);
                    System.out.println("getMedianNeighbour() = " + getMedianNeighbour(neighbours,direction));
                    blockEdges.add(new Edge(nodeOnLayer, getMedianNeighbour(neighbours, direction)));
                }
            }
        } else if (direction.contains("N")) {// bottom-to-top
            for (int intLayer = numberOfLayer; intLayer > 1; intLayer--) {
                LinkedList<GraphNode> nodesOnLayer = graph.getLayerMap().get(intLayer);
                for (GraphNode nodeOnLayer : nodesOnLayer) {
                    ArrayList<GraphNode> neighbours = graph.getAdjacentNodes(nodeOnLayer, intLayer - 1);
                    blockEdges.add(new Edge(nodeOnLayer, getMedianNeighbour(neighbours, direction)));
                }
            }
        } else System.out.println(" what da fuck? Greetings from BK - BlockBuilding");
        return new Graph(blockEdges);
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

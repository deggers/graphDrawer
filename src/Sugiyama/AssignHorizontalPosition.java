package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

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

    private static Graph VerticalAlignment(Graph graph, String direction){
        int graphDepth = graph.getLayerMap().keySet().size();
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap = graph.getLayerMap();
        int r = direction.contains("W") ? -1 : Integer.MAX_VALUE;                                          // Zeile 5,6
        LinkedHashMap<GraphNode,GraphNode> rootMap = new LinkedHashMap<>();
        LinkedHashMap<GraphNode,GraphNode> alignMap = new LinkedHashMap<>();
        for(GraphNode node: graph.getNodes().values())rootMap.put(node,node);                              // Zeile 1
        for(GraphNode node: graph.getNodes().values())alignMap.put(node,node);                             // Zeile 2
        if (direction.contains("S")) {
            for (int layer = 1; layer < graphDepth; layer++){                                                  // Zeile 3
                int                     layerSize = layerMap.get(layer).size();
                LinkedList<GraphNode>   layerList = layerMap.get(layer);
                if (direction.contains("W")){                               // left
                for (int k = 0; k < layerSize; k++){                        // from the mostleft position to the rightmost
                        GraphNode                   vk_i            = layerList.get(k);
                        ArrayList<GraphNode>        neighbors       = getNeighbors(graph, vk_i, direction);
                        if (neighbors != null){
                            ArrayList<GraphNode>    medianNeighbor  = new ArrayList<>();
                            if (neighbors.size() % 2 == 0){                 // even case --> Return 2

                            } else {                                        // odd case --> Return 1
                                int d = (neighbors.size()+1)/2;             // 5 + 1 = 6; 6/2 = 3; 1,2|3|4,5
                                medianNeighbor.add(layerList.get(median));
                            }

                        }
                    }

                } else {                                    // right
                    for (int k = layerSize; k > 0; k--){    // from the mostright position to the mostleft

                    }

                }

            }
        }
        else {
            for (int layer = graphDepth; layer > 0; layer-- ){                                             // Zeile 4
                // left or right?

                System.out.println("going up!");
            }
        }
        return graph;
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

    // coild probably much more easier, if inner segment, incident node must be unique ..
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
    // has direction bias - layers need to be vertical
    private static ArrayList<GraphNode> getNeighbors(Graph g, GraphNode node, String direction){
        ArrayList<GraphNode> neighbors = new ArrayList<>();
        int layer;
        switch(direction){
            case "predecessor":     layer = node.getLayer() - 1;     break;
            case "successor":       layer = node.getLayer() + 1;     break;
            default: return null;}
        if (g.getAdjacentNodes(node, layer).size() > 0) // has neigbhors in upper layer
            return g.getAdjacentNodes(node,layer);
        return null;
    }
}

package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.*;

//@formatter:off

// aka Brandes Köpf
public class AssignHorizontalPosition {
    private static boolean VERBOSE = false;

    public static Graph processBK(Graph inputGraph) {
        // step 1 -> verticalAlignment(direction)
        /* In the first step, referred to as vertical alignment, we try to align each vertex with either
        its median upper or its median lower neighbor, and we resolve alignment conflicts (of type 0) either
        in a leftmost or a rightmost fashion. We thus obtain one vertical alignment for each combination of
        upward and downward alignment with leftmost and rightmost conflict resolution. */

        MarkTypeOneConflicts(inputGraph);
        verticalAlignment(inputGraph,"NW");
//        verticalAlignment(inputGraph,"NE");
//        verticalAlignment(inputGraph,"SW");
//        verticalAlignment(inputGraph,"SE");

        // step 2 -> Compaction(direction)
//            graphSW = Compaction(graphSW, "SW");
//        graphNW = Compaction(graphNW, "NW");
//        graphNE = Compaction(graphNE, "NE");
//        graphSE = Compaction(graphSE, "SE");

        // step 3
//        Balancing(graphNW,graphNE,graphSW,graphSE);
        return inputGraph;
    }

    private static void MarkTypeOneConflicts(Graph graph) {
        int graphDepth = graph.getLayerMap().keySet().size();
        if (VERBOSE) System.out.println("graphDepth = " + graphDepth);
        for (int layer1_int = 2; layer1_int < graphDepth; layer1_int++) {
            int layer2_int = layer1_int + 1;
            LinkedList<GraphNode> layer_1 = graph.getLayerMap().get(layer1_int);
            LinkedList<GraphNode> layer_2 = graph.getLayerMap().get(layer2_int);
            int k0 = 0, k1 = 0, l = 0;                                                                           // Zeile 2
            for (GraphNode L_1 : layer_2) {                                                                      // Zeile 3
                if (isLastNode(layer_2, L_1) || incidentToInnerSegment(graph, L_1, layer1_int) ){                // Zeile 4
                    k1 = layer_1.size();                                                                       // Zeile 5
                    if (incidentToInnerSegment(graph,L_1,layer1_int))                                            // Zeile 6
                            k1 = layer_1.indexOf(getUpperNodeFromInnerSegment(graph,L_1,layer1_int));            // Zeile 7
                    while (l <= layer_2.indexOf(L_1)){                                                           // Zeile 8
                            GraphNode bottomNode = layer_2.get(l);
                            for (GraphNode upperneighbor : graph.getAdjacentNodes(bottomNode, layer1_int))       // Zeile 9
                                if (layer_1.indexOf(upperneighbor) < k0 || layer_1.indexOf(upperneighbor) > k1){  // Zeile 10
                                    graph.getEdgeBetween(upperneighbor, bottomNode).setMarkedType1Conflict(true);
                                    }
                            l++;
                        }
                        k0 = k1;
                }
            }
            }
    }

    private static void     verticalAlignment(Graph graph, String direction){
        LinkedHashMap<Integer,LinkedList<GraphNode>> layerMap   = graph.getLayerMap();
        LinkedHashMap<GraphNode,GraphNode> rootMap              = new LinkedHashMap<>();
        LinkedHashMap<GraphNode,GraphNode> alignMap             = new LinkedHashMap<>();
        Map<GraphNode, Runnable> commands = new HashMap<>();

        System.out.println("direction = " + direction);
        for(GraphNode node: graph.getNodes().values())rootMap.put(node,node);                               // Zeile 1
        for(GraphNode node: graph.getNodes().values())alignMap.put(node,node);                              // Zeile 2

        int graphDepth  = graph.getLayerMap().keySet().size();
        if (direction.contains("S")) {
            System.out.println("south");
// TRAVERSING LAYERS FROM TOP TO BOTTOM-v-v-v-v-v-v-v-v-v-v-v-v-TOP TO BOTTOM-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-v-
            for (int layer = 1; layer < graphDepth; layer++){                                                 // Zeile 3
                int layerSize   = layerMap.get(layer).size();
                int r           = direction.contains("W") ? -1 : Integer.MAX_VALUE;                         // Zeile 5,6
                LinkedList<GraphNode>   layerList = layerMap.get(layer);
                System.out.println("layerSize = " + layerSize);
                System.out.println(r);
                System.out.println("layerList = " + layerList);
//>>>>>>>>>>>>>>>--------------------------------------FOR LEFT TO RIGHT-------------------------------------->>>>>>>>>>
                if (direction.contains("W")){ for (int k = 0; k < layerSize; k++){                            // Zeile 7
                        System.out.println("");
                        System.out.println("west");
                        GraphNode               vk_i        = layerList.get(k);
                        System.out.println("vk_i = " + vk_i);
                        ArrayList<GraphNode>    neighbors   = getNeighbors(graph, vk_i, "predecessor");          // Zeile  9
                        System.out.println("neighbors = " + neighbors);
                        LinkedList<GraphNode> layerAbove = graph.getLayerMap().get(layer + 1);
                        if (neighbors != null) {
                            for (GraphNode neighbor_m : getUpperAndLowerMedian(neighbors,  false)) {
                                if (alignMap.get(vk_i) == vk_i){                                             // Zeile 14
                                    System.out.println("neighbor_m = " + neighbor_m);
                                    boolean isMarked = graph.getEdgeBetween(neighbor_m, vk_i).isMarkedType1Conflict();
                                    if (!isMarked && r < layerAbove.indexOf(neighbor_m)){     // ATTENTION, ">" ||"<"        // Zeile 15
                                        alignMap.put(neighbor_m, vk_i);                                      // Zeile 17
                                        rootMap.put(vk_i,rootMap.get(neighbor_m));                           // Zeile 18
                                        alignMap.put(vk_i, rootMap.get(vk_i));                               // Zeile 19
                                        r = layerAbove.indexOf(neighbor_m);                                                  // Zeile 20
                                    }
                                }}}}} else {
//<<<<<<<<<<<<<<<<<<-----------------------------------FOR RIGHT TO LEFT--------------------------------------<<<<<<<<<<
                    for (int k = layerSize - 1; k > 0; k--){                                                      // Zeile 8
                        GraphNode            vk_i       = layerList.get(k);
                        ArrayList<GraphNode> neighbors  = getNeighbors(graph, vk_i,"predecessor");
                        LinkedList<GraphNode> layerAbove = graph.getLayerMap().get(layer + 1);
                        if (neighbors != null)
                            for (GraphNode neighbor_m : getUpperAndLowerMedian(neighbors, true))
                            if (alignMap.get(vk_i) == vk_i){                                             // Zeile 14
                                    boolean isMarked = graph.getEdgeBetween(neighbor_m, vk_i).isMarkedType1Conflict();
                                        if (!isMarked && r > layerAbove.indexOf(neighbor_m)){ // ATTENTION, ">" ||"<"        // Zeile 15
                                        alignMap.put(neighbor_m, vk_i);                                      // Zeile 17
                                        rootMap.put(vk_i,rootMap.get(neighbor_m));                           // Zeile 18
                                        alignMap.put(vk_i, rootMap.get(vk_i));                               // Zeile 19
                                        r = layerAbove.indexOf(neighbor_m);                                                  // Zeile 20
                                    }
                                }
                    }
                }
            } // END-FOR-LOOP TOP-TO-BOTTOM -----------------------------------------------------------------------------
        } else {
            System.out.println("North");
// TRAVERSING LAYERS FROM BOTTOM TO TOP------------------BOTTOM TO TOP--------------------------------------------------
            for (int layer = graphDepth; layer > 0; layer-- ){                                             // Zeile 4
                int layerSize   = layerMap.get(layer).size();
                int r           = direction.contains("W") ? -1 : Integer.MAX_VALUE;                         // Zeile 5,6
                LinkedList<GraphNode>   layerList = layerMap.get(layer);
//>>>>>>>>>>>>>>>--------------------------------------FOR LEFT TO RIGHT-------------------------------------->>>>>>>>>>
                if (direction.contains("W")){ for (int k = 0; k < layerSize; k++){                            // Zeile 7
                        GraphNode               vk_i        = layerList.get(k);
                        System.out.println("");
                        System.out.println("vk_i = " + vk_i);
                        ArrayList<GraphNode>    neighbors   = getNeighbors(graph, vk_i, "successors");          // Zeile  9
                        LinkedList<GraphNode> layerAbove = graph.getLayerMap().get(layer - 1);
                        System.out.println("layerAbove = " + layerAbove);
                        System.out.println("neighbors = " + neighbors);
                        if (neighbors != null)
                            for (GraphNode neighbor_m : getUpperAndLowerMedian(neighbors, false)){
                                if (alignMap.get(vk_i).equals(vk_i)){                                             // Zeile 14
                                    int pos_neighbor_m = layerAbove.indexOf((neighbor_m));
                                    boolean isMarked = graph.getEdgeBetween(neighbor_m, vk_i).isMarkedType1Conflict();
                                        if (!isMarked && r < pos_neighbor_m){     // ATTENTION, ">" ||"<"        // Zeile 15

                                            // Populate commands map

                                            System.out.println("neighbor_m = " + neighbor_m);
                                            System.out.println("update it because not marked and r < pos_neighbor_m && without block");
                                            alignMap.put(neighbor_m,vk_i);
                                            rootMap.put(vk_i,rootMap.get(neighbor_m));
//                                            commands.put(vk_i, () -> System.out.print(rootMap.get(neighbor_m)));
                                            alignMap.put(vk_i,rootMap.get(vk_i));
                                            System.out.println("rootMap = " + rootMap);
                                            System.out.println("alignMap = " + alignMap);
                                            r = pos_neighbor_m;                                                  // Zeile 20
                                        }
                                }

                            }}
                } else {
//<<<<<<<<<<<<<<<<<<-----------------------------------FOR RIGHT TO LEFT--------------------------------------<<<<<<<<<<
                    for (int k = layerSize - 1; k > 0; k--){                                                      // Zeile 8
                        GraphNode            vk_i       = layerList.get(k);
                        ArrayList<GraphNode> neighbors  = getNeighbors(graph, vk_i,"successors");
                        LinkedList<GraphNode> layerAbove = graph.getLayerMap().get(layer - 1);
                        if (neighbors != null)
                            for (GraphNode neighbor_m : getUpperAndLowerMedian(neighbors, true)) {
                                if (alignMap.get(vk_i) == vk_i){                                             // Zeile 14
                                    int pos_neighbor_m = layerAbove.indexOf((neighbor_m));
                                        boolean isMarked = graph.getEdgeBetween(neighbor_m, vk_i).isMarkedType1Conflict();
                                            if (!isMarked && r > pos_neighbor_m){ // ATTENTION, ">" ||"<"        // Zeile 15
                                            alignMap.put(neighbor_m, vk_i);                                      // Zeile 17
                                            rootMap.put(vk_i,rootMap.get(neighbor_m));                           // Zeile 18
                                            alignMap.put(vk_i, rootMap.get(vk_i));                               // Zeile 19
                                            r = pos_neighbor_m;                                                  // Zeile 20
                                        }
                                    }
                    }}
                }
            }
        }
        System.out.println(""); System.out.println("");
        System.out.println("rootMap = " + rootMap);
        System.out.println("");
        System.out.println("alignMap = " + alignMap);

//        System.out.println("");
//        for (GraphNode node: rootMap.values()){
//            System.out.println("node = " + node);
//            commands.get(node);
//        }

        graph.setRootBlock(direction,rootMap);
        graph.setAlignBlock(direction,rootMap);
    }
    private static boolean  isLastNode(LinkedList<GraphNode> nodesLayer, GraphNode node){
        return node == nodesLayer.getLast();
    }
    private static ArrayList<GraphNode>    getUpperAndLowerMedian(ArrayList<GraphNode> neighbors, boolean rightToLeft){
        if (neighbors.size() == 1) return new ArrayList<>(Arrays.asList(neighbors.get(0),neighbors.get(0)));
        double d = neighbors.size();
        int upper = (int) Math.ceil(  (d+1)/2) -1;
        int lower = (int)  Math.floor(( d+1)/2) -1;

        if (rightToLeft) return new ArrayList<>(Arrays.asList(neighbors.get(upper), neighbors.get(lower)));
        else return new ArrayList<>(Arrays.asList(neighbors.get(lower),neighbors.get(upper)));
    }


    private static boolean incidentToInnerSegment(Graph g, GraphNode u, int onLayer){
        if (u.isDummy()) {
            ArrayList<GraphNode> neighbors = g.getAdjacentNodes(u, onLayer);
            for (GraphNode node : neighbors) if (node.isDummy()) return true;}
        return false;
    }

    // coild probably much more easier, if inner segment, incident node must be unique ..
    private static GraphNode    getUpperNodeFromInnerSegment(Graph g, GraphNode u, int layer_int){
        ArrayList<GraphNode> neighbors = g.getAdjacentNodes(u, layer_int);
        for (GraphNode node : neighbors)
            if (node.isDummy()) {
                Edge e =  g.getEdgeBetween(u,node);
                return e.tail.equals(u) ? e.head : e.tail;
            }
        return null;
    }
    private static Graph        Compaction(Graph graph, String direction) {

        return graph;
    }
    private static void         Balancing(Graph graphNW, Graph graphNE, Graph graphSW, Graph graphSE) {
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
    private static ArrayList<GraphNode> getNeighbors(Graph g, GraphNode node, String predecessorOrSucessor){
        ArrayList<GraphNode> neighbors = new ArrayList<>();
        int layer;
        switch(predecessorOrSucessor){
            case "predecessor":     layer = node.getLayer() + 1;     break;
            case "successors":       layer = node.getLayer() - 1;     break;
            default: return null;}
        if (g.getAdjacentNodes(node, layer).size() > 0) // has neigbhors in upper layer
            return g.getAdjacentNodes(node,layer);
        return null;
    }
}

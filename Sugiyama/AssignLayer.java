package Sugiyama;/*
Consider a DAG G = (V, E) with a set of vertices V and a set of edges E. Let L =
{L0, L1, . . . , Lh} be a partition of the vertex set of G into h ≥ 1 subsets such that if
(u, v) ∈ E with u ∈ Lj and v ∈ Li then i < j. L is called a layering of G and the sets L0,
L1, . . ., Lh are called layers. A DAG with a layering is called a layered DAG. The problem
of partitioning the vertex set of a graph into layers is known as the layering problem or the
layer assignment problem
 */

import structure.Graph;
import structure.GraphNode;

import java.util.*;

public class AssignLayer {
    private static final int DEFAULT_LAYER_UNSET = -1;
    private static final int DEFAULT_LAYER_STARTING = 1;
    private static final boolean VERBOSE = false;

    public static void longestPath(Graph g) {
        final boolean OPTIONAL_CHECK = true;

        LinkedHashSet<GraphNode> U; // set of nodes that will get a layer in this step
        LinkedHashSet<GraphNode> Z = new LinkedHashSet<>(); // set of all parents of nodes in U
        LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap = new LinkedHashMap<>();

        int currentLayer = DEFAULT_LAYER_STARTING;
        if (VERBOSE) System.out.println("Deleting isolated nodes, if any.");
        g.deleteAll_IsolatedNodes();
        if (VERBOSE) System.out.println("Fetching set of sinks");
        U = new LinkedHashSet<>(g.getAllSinks()); // start U with all the sinks in the graph
        if (VERBOSE) System.out.print("Starting while loop\n");
        while (!U.isEmpty()) {
            if (VERBOSE) System.out.printf("Set U: %s\n", U.toString());
            for (GraphNode node : U) {
                node.setLayer(currentLayer);
                if (layerMap.containsKey(currentLayer)) layerMap.get(currentLayer).add(node);
                else layerMap.put(currentLayer, new LinkedList<>(Collections.singletonList(node)));
                if (g.getParentsOf(node) != null) Z.addAll(g.getParentsOf(node)); // compute the union of all parents
            }
            U.clear(); // clear U to compute all nodes that can get a layer in the next step
            for (GraphNode graphNode : Z) {
                boolean hasNoUnlayeredChildren = true;
                for (GraphNode gn : g.getChildren(graphNode))
                    if (gn.getLayer() < DEFAULT_LAYER_STARTING) hasNoUnlayeredChildren = false;
                if (hasNoUnlayeredChildren)
                    U.add(graphNode); // add all nodes to U, for which all children have a proper layer assigned//
            }
            Z.clear();
            currentLayer++; // repeat with next layer, as long as there are nodes that can be assigned a layer
        }
        //all nodes should be layered now, optional check
        if (OPTIONAL_CHECK) {
            for (GraphNode graphNode : g.getNodes().values()) {
                if (graphNode.getLayer() == DEFAULT_LAYER_UNSET) {
                    System.out.println("graphNode unlayered! = " + graphNode);
                    throw new Error("LongestPath Layering produced an error, as some nodes in the graph remained unlayered. Check for correctness, solitary nodes or disable this check!");
                }
            }
        }
        g.insertLayer(layerMap);
        g.reverseLayerOrder();
        g.addDummies();
    }
    public static void topologicalPath(Graph g) {
        int level = DEFAULT_LAYER_STARTING;
        Graph copyG = new Graph(g);

        LinkedHashMap<Integer, LinkedList<GraphNode>> sorted = new LinkedHashMap<>();
        LinkedList<GraphNode> sinks;

        while (!copyG.getAllSinks().isEmpty()) {
            sinks = copyG.getAllSinks();
            sorted.put(level, sinks);
            sinks.forEach(copyG::removeIngoingEdges);
            copyG.getNodes().values().removeAll(sinks);
            copyG.getNodes().keySet().removeAll(sinks);
            level += 1;
            if (VERBOSE && level == g.getNodes().size()) System.out.println("\n#Nodes = #Layer\n");
        } sorted.put(level, copyG.getIsolatedNodes());

        if (sorted.size() == 0) System.out.println("nothin in the layer ?!");
        g.insertLayer(sorted);
        g.reverseLayerOrder();
        g.addDummies();

        if (VERBOSE) { System.out.println(g);
            System.out.println(g.getEdges());
            System.out.println("g.getNodeSet() = " + g.getNodes());}
    }
}

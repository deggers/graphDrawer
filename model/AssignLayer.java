package model;

/*
Consider a DAG G = (V, E) with a set of vertices V and a set of edges E. Let L =
{L0, L1, . . . , Lh} be a partition of the vertex set of G into h ≥ 1 subsets such that if
(u, v) ∈ E with u ∈ Lj and v ∈ Li then i < j. L is called a layering of G and the sets L0,
L1, . . ., Lh are called layers. A DAG with a layering is called a layered DAG. The problem
of partitioning the vertex set of a graph into layers is known as the layering problem or the
layer assignment problem
 */

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class AssignLayer {
    private static final boolean verbose = false;
    
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layering = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode, Integer> nodeToRank = new LinkedHashMap<>();

//    right now not working
    public static Graph longestPath(Graph g) {
        final boolean optionalCheck = true;

        LinkedHashSet<GraphNode> U = new LinkedHashSet<>(); // set of nodes that will get a layer in this step
        LinkedHashSet<GraphNode> Z = new LinkedHashSet<>(); // set of all parents of nodes in U
        int currentLayer = 1;
        Graph layeredGraph = g.copy(g);
        if (verbose) System.out.println("Deleting isolated nodes, if any.");
        //layeredGraph.deleteIsolatedNodes(); //hope this works
        //layeredGraph.getIsolatedNodes().forEach(layeredGraph::justRemoveNode);
        // nodes without a layer have layer set to -1 by default now (any number that can't be a proper layer is ok)
        if (verbose) System.out.println("Fetching set of sinks");
        U = new LinkedHashSet<>(layeredGraph.getAllSinks()); // start U with all the sinks in the graph
        if (verbose) System.out.printf("Starting while loop\n");
        while (!U.isEmpty()) {
            if (verbose) System.out.printf("Set U: %s\n", U.toString());
            for (GraphNode node : U) {
                node.setLayer(currentLayer); // set all nodes in U to currentLayer
                Z.addAll(layeredGraph.getParentsOf(node)); // compute the union of all parents of nodes in U
            }
            U.clear(); // clear U to compute all nodes that can get a layer in the next step
            for (GraphNode graphNode : Z) {
                boolean hasNoUnlayeredChildren = true;
                for (GraphNode gn : layeredGraph.getChildrenFrom(graphNode)) {
                    if (gn.getLayer()<0/*depends on default value for unlayered nodes*/) {
                        hasNoUnlayeredChildren = false;
                    }
                }
                if (hasNoUnlayeredChildren) {
                    U.add(graphNode); // add all nodes to U, for which all children have a proper layer assigned
                }
            }
            Z.clear();
            currentLayer++; // repeat with next layer, as long as there are nodes that can be assigned a layer
        }
        //all nodes should be layered now, optional check
        if (optionalCheck) {
            for (GraphNode graphNode : layeredGraph.getNodes()) {
                if (graphNode.getLayer()<0) {
                    //if any node is not properly labeled, throw something
                    throw new Error("LongestPath Layering produced an error, as some nodes in the graph remained unlayered. Check for correctness, solitary nodes or disable this check!");
                }
            }
        }
        //one should potentially turn the layering around now, so that the upmost node is at level 0 or 1 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //time to add dummy nodes
        layeredGraph.addDummies();
        return layeredGraph;
    }
    public static void topologicalPath(Graph g) {
        int level = 1;
        Graph copyG = g.copy(g);
        LinkedHashMap<Integer, LinkedList<GraphNode>> sorted = new LinkedHashMap<>();
        LinkedList<GraphNode> sinks;

        while (!copyG.getAllSinks().isEmpty()) {
            sinks = copyG.getAllSinks();
            sorted.put(level, sinks);
            sinks.forEach(copyG::removeIngoingEdges);
            sinks.forEach(copyG::justRemoveNode);
            level += 1;
            if (level == g.getNodes().size()) System.out.println("Way to many levels..");
        }
        sorted.put(level, copyG.getIsolatedNodes());
        System.out.println("sorted = " + sorted);
//        sorted.forEach((key, value) -> {value.forEach(node -> node.setLayer(key));});
        System.out.println("g.getNodes befor () = " + g.getNodes());
        System.out.println("g.getEdges before() = " + g.getEdges());
        System.out.println("");
        for (int layer : sorted.keySet()) {
           g.insertLayer(layer, sorted.get(layer));
        }

        System.out.println("g.getNodes after () = " + g.getNodes());
        System.out.println("g.getEdges after() = " + g.getEdges());

//        g.addDummies();
        if (verbose) {
            System.out.println(g);
            System.out.println(g.getEdges());
            System.out.println("g.getNodeSet() = " + g.getNodes());
        }
    }

//  All the HELPER-Functions

    private LinkedList<GraphNode> getNodesFromLevel(Integer level) {
        return layering.get(level);
    }
    private Integer getRank(GraphNode node) {
        return nodeToRank.get(node);
    }
    private Integer getSpanOf(Edge edge) {
        GraphNode u = edge.start;
        GraphNode v = edge.target;
        return getRank(u) - getRank(v);
    }
    private Edge getLongEdge(Graph g) {
        for (Edge edge : g.getEdges()) {
            if (getSpanOf(edge) > 1) {
                return edge;
            }
        }
        return null;
    }
    private Boolean layeringIsProper(Graph g) {
       /*   The layering found by a layering algorithm might not
            be proper because only a small fraction of DAGs can be layered properly and also because
            a proper layering may not satisfy other layering requirements.
        */
        return getLongEdge(g) == null;
//        if return null, evaluates to true -> isProper
    }

}

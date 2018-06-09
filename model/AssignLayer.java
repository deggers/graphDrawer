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
import java.util.List;

import static model.drawableGraph.setLayering;

public class AssignLayer {
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layering = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode, Integer> nodeToRank = new LinkedHashMap<>();
    static int dummyCounter = 0;

    //    right now not working
    public static void longestPath(drawableGraph g) {
        final boolean optionalCheck = true;

        LinkedHashSet<GraphNode> U = new LinkedHashSet<>(); // set of nodes that will get a layer in this step
        LinkedHashSet<GraphNode> Z = new LinkedHashSet<>(); // set of all parents of nodes in U
        int currentLayer = 1;
        drawableGraph layeredGraph = g.copy(g);
//        layeredGraph.deleteIsolatedNodes(); //hope this works
        layeredGraph.getIsolatedNodes().forEach(layeredGraph::justRemoveNode);
        // nodes without a layer have layer set to -1 by default now (any number that can't be a proper layer is ok)
        U = new LinkedHashSet<>(layeredGraph.getAllSinks()); // start U with all the sinks in the graph
        while (!U.isEmpty()) {
            for (GraphNode node : U) {
                node.setLayer(currentLayer); // set all nodes in U to currentLayer
                Z.addAll(node.getParents()); // compute the union of all parents of nodes in U
            }
            U.clear(); // clear U to compute all nodes that can get a layer in the next step
            for (GraphNode graphNode : Z) {
                boolean hasNoUnlayeredChildren = true;
                for (GraphNode gn : graphNode.getChildren()) {
                    if (gn.getLayer() < 0/*depends on default value for unlayered nodes*/) {
                        hasNoUnlayeredChildren = false;
                    }
                }
                if (hasNoUnlayeredChildren) {
                    U.add(graphNode); // add all nodes to U, for which all children have a proper layer assigned
                }
            }
            currentLayer++; // repeat with next layer, as long as there are nodes that can be assigned a layer
        }
        //all nodes should be layered now, optional check
        if (optionalCheck) {
            for (GraphNode graphNode : layeredGraph.copyNodeSet()) {
                if (graphNode.getLayer() < 0) {
                    //if any node is not properly labeled, throw something
                    throw new Error("LongestPath Layering produced an error, as some nodes in the graph remained unlayered. Check for correctness, solitary nodes or disable this check!");
                }
            }
        }
        //one should potentially turn the layering around now, so that the upmost node is at level 0 or 1 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //time to add dummy nodes
        addDummies(g, true);
        g = layeredGraph; // copy layered graph to g (necessary)
    }

    public static void topologicalPath(drawableGraph g) {
        int level = 1;
        drawableGraph copyG = g.copy(g);
        LinkedHashMap<Integer, LinkedList<GraphNode>> processed = new LinkedHashMap<>();
        LinkedList<GraphNode> sinks;

        while (!copyG.getAllSinks().isEmpty()) {
            sinks = copyG.getAllSinks();
//            processed.put(level, sinks);
//            System.out.println("processed = " + processed);
            sinks.forEach(copyG::removeIngoingEdges);
            sinks.forEach(copyG::justRemoveNode);
            setLayering(g,level,sinks);
            level += 1;
            if (level == g.getNodeSet().size()) System.out.println("Way to many levels..");
        }
        setLayering(g, level+1, copyG.getIsolatedNodes());

//        sorted.forEach((key, value) -> {
//            value.forEach(node -> node.setLayer(key));
//        });

//        System.out.println("sorted = " + processed);

        System.out.println(g.copyNodeSet());
        // key,value in sorted -> set layer for nodes in g
        // with key = level and value = node to set


        addDummies(copyG, true);
        System.out.println("After adding Dummies");
    }

//  All the HELPER-Functions


    private static void addDummies(drawableGraph g, boolean verbose) {
        LinkedList<Edge> edgesToDelete = new LinkedList<>();
        LinkedList<Edge> edgeToAdd = new LinkedList<>();
        LinkedHashSet<Edge> edges = g.getEdgeSet();
        LinkedList<List<GraphNode>> processBlocks = new LinkedList<>();


        for (Edge edge : edges) {
            if (verbose) System.out.printf("Working on edge: %s \n", edge);
            GraphNode start = (GraphNode) edge.start;
            GraphNode target = (GraphNode) edge.target;
            int spanningLevels = Math.abs(start.getLayer() - target.getLayer());

            if (spanningLevels > 1) {
                int startLevel = Math.min(start.getLayer(), target.getLayer()) + 1;
                int endLevel = Math.max(start.getLayer(), target.getLayer()) - 1;

                LinkedList<GraphNode> block = new LinkedList<>();
                for (int i = startLevel; i <= endLevel; i++) {

                    GraphNode dummyNode = new GraphNode(dummyCounter++,"Dummy", "Dummy", true, i);
                    block.add(dummyNode);
                    g.getNodeSet().add(dummyNode);
                }
                if (verbose) System.out.println("block = " + block);
                processBlocks.add(block);

                edgeToAdd.add(new Edge(start, block.getFirst()));  // connect start with first dummy
                for (int i = 0; i < block.size() - 1; i++) {       // connect each dummy with next dummy
                    GraphNode left = block.get(i);
                    GraphNode right = block.get(i + 1);
                    edgeToAdd.add(new Edge(left, right));
                }
                edgeToAdd.add(new Edge(block.getLast(), target));  // connect lastDummy with end
                edgesToDelete.add(edge);                           // remove later that long edge
            }
        }
        edgesToDelete.forEach(g::deleteEdge);
        edgeToAdd.forEach(g::addEdge);
    }


    private LinkedList<GraphNode> getNodesFromLevel(Integer level) {
        return layering.get(level);
    }

    private Integer getRank(GraphNode node) {
        return nodeToRank.get(node);
    }

    private Integer getSpanOf(Edge edge) {
        GraphNode u = (GraphNode) edge.start;
        GraphNode v = (GraphNode) edge.target;
        return getRank(u) - getRank(v);
    }

    private Edge getLongEdge(drawableGraph g) {
        for (Edge edge : g.copyEdgeSet()) {
            if (getSpanOf(edge) > 1) {
                return edge;
            }
        }
        return null;
    }

    private Boolean layeringIsProper(drawableGraph g) {
       /*   The layering found by a layering algorithm might not
            be proper because only a small fraction of DAGs can be layered properly and also because
            a proper layering may not satisfy other layering requirements.
        */
        return getLongEdge(g) == null;
//        if return null, evaluates to true -> isProper
    }

}

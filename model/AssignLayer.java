package model;

/*
Consider a DAG G = (V, E) with a set of vertices V and a set of edges E. Let L =
{L0, L1, . . . , Lh} be a partition of the vertex set of G into h ≥ 1 subsets such that if
(u, v) ∈ E with u ∈ Lj and v ∈ Li then i < j. L is called a layering of G and the sets L0,
L1, . . ., Lh are called layers. A DAG with a layering is called a layered DAG. The problem
of partitioning the vertex set of a graph into layers is known as the layering problem or the
layer assignment problem
 */

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class AssignLayer {
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layering = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode, Integer> nodeToRank = new LinkedHashMap<>();

//    right now not working
    public static void longestPath(drawableGraph g) {
        HashSet<GraphNode> U = new HashSet<>(); // set of already assigned to a layer
        HashSet<GraphNode> Z = new HashSet<>(); // set of all vertices assigned to a layer below the currnent
        int currentLayer = 1;

        drawableGraph copyG = g.copy(g);

        while (U != g.getNodeSet()) {
            /* A new vertex v to be assigned to the current layer is picked among the vertices which have not been
               i.e. v ∈ V \ U, and which have all their immediate successors already assigned to the layers below the
               current one, i.e. N+G (v) ⊆ Z          */

            copyG.getNodeSet().removeAll(U);

            GraphNode selectedNode = null;
            for (GraphNode v: copyG.getNodeSet()) {

            }

            if (selectedNode == null) {
                currentLayer += 1;
                Z.addAll(U);
            }




            System.exit(1);
        }
    }

//    right now not working
    public static void topologicalPath(drawableGraph g) {
        int level = 1;
        System.out.println("got following edges: " + g.getEdgeSet());
        drawableGraph copyG = g.copy(g);
        System.out.println("got copied    edges: " + copyG.getEdgeSet());
        LinkedHashMap<Integer, LinkedList<GraphNode>> sorted = new LinkedHashMap<>();
        LinkedList<GraphNode> sinks = copyG.getAllSinks();

        while (!sinks.isEmpty()) {
            System.out.println("will remove sinks!");
            System.out.println("sinks = " + sinks);
            sorted.put(level, sinks);
            sinks.forEach(copyG::removeIngoingEdges);
            sinks = copyG.getAllSinks();
            level += 1;
            if (level == g.getNodeSet().size()) System.out.println("Way to many levels..");
        }
        System.out.println("sorted = " + sorted);
    }

//  All the HELPER-Functions

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
        for (Edge edge : g.getEdgeList()) {
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

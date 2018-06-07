package model;

/*
Consider a DAG G = (V, E) with a set of vertices V and a set of edges E. Let L =
{L0, L1, . . . , Lh} be a partition of the vertex set of G into h ≥ 1 subsets such that if
(u, v) ∈ E with u ∈ Lj and v ∈ Li then i < j. L is called a layering of G and the sets L0,
L1, . . ., Lh are called layers. A DAG with a layering is called a layered DAG. The problem
of partitioning the vertex set of a graph into layers is known as the layering problem or the
layer assignment problem
 */

import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class AssignLayer {
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layering = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode, Integer> NodeToRank = new LinkedHashMap<>();

//    right now not working
    public static void longestPath(drawableGraph g) {
        HashSet<GraphNode> U = new HashSet<>(); // set of already assigned to a layer
        HashSet<GraphNode> Z = new HashSet<>(); // set of all vertices assigned to a layer below the currnent
        int currentLayer = 1;

        drawableGraph copyG = g.copy(g);

        while (U != g.getNodes()) {
            /* A new vertex v to be assigned to the current layer is picked among the vertices which have not been
               i.e. v ∈ V \ U, and which have all their immediate successors already assigned to the layers below the
               current one, i.e. N+G (v) ⊆ Z          */

            copyG.getNodes().removeAll(U);

            GraphNode selectedNode = null;
            for (GraphNode v: copyG.getNodes()) {

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
        drawableGraph copyG = g.copy(g);
//        LinkedList<GraphNode> sorted = new LinkedList<>();
        LinkedHashMap<Integer, LinkedList<GraphNode>> sorted = new LinkedHashMap<>();
        LinkedHashSet<Edge> origGraphEdges = g.getEdgeList();  // const edges = this._graph.edges
        LinkedHashSet<GraphNode> origGraphNodes = g.getNodes();  //  const vertices = this._graph.vertices.toArray()
        LinkedList<GraphNode> sinks = copyG.getAllSinks();
        System.out.println("sinks before first whileLoop = " + sinks);
        while (sinks != null) {
            sorted.put(level, sinks);
            sinks.forEach(copyG::removeIngoingEdges);
            sinks = copyG.getAllSinks();
            System.out.println("sinks remaining = " + sinks);
            level += 1;
            System.exit(1);
        }
        System.out.println("sorted = " + sorted);
    }

//  All the HELPER-Functions

    private LinkedList<GraphNode> getNodesFromLevel(Integer level) {
        return layering.get(level);
    }
    private Integer getRank(GraphNode node) {
        return NodeToRank.get(node);
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

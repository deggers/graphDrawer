package Sugiyama;
/*A set of edges whose removal makes the digraph acyclic is commonly known as a feedback
arc set (FAS). Following the terminology used by Di Battista et al. [DETT99] we call a set
of edges whose reversal makes the digraph acyclic a feedback set (FS). Each FS is also a
FAS. However, not each FAS is a FS. For example, if a digraph has only one cycle, then the
set of all edges in the cycle is a FAS but not a FS.*/

import structure.Edge;
import structure.Graph;
import structure.GraphNode;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class CycleBreaker {
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    private static LinkedHashSet<Edge> edgesToBeReversed = new LinkedHashSet<>(); //in their original form

    public static void Berger_Shor(Graph g) {
        Graph copyG = new Graph(g);
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = copyG.getEdgesInMap();
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = copyG.getEdgesOutMap();
        LinkedHashSet<Edge> acyclicEdges = new LinkedHashSet<>();

        g.getNodes().values().forEach(node -> {
            if (copyG.outDegree(node) >= copyG.inDegree(node) && copyG.outDegree(node) > 0)
                acyclicEdges.addAll(edgesOut.get(node));
            else if (copyG.inDegree(node) > 0)
                acyclicEdges.addAll(edgesIn.get(node));
            copyG.removeNode(node);
        });
        reverseTheEdges(g,acyclicEdges);
    }
    public static void GreedyCycleRemoval(Graph g) {
/*        The first polynomial-time algorithm for solving the minimum FAS problem with an approximation
          ratio less than 2 in the worst case */
        Graph copyG = new Graph(g);
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = copyG.getEdgesInMap();
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = copyG.getEdgesOutMap();
        LinkedHashSet<Edge> acyclicEdges = new LinkedHashSet<>();

        while (!copyG.getNodes().isEmpty()) {
            while (copyG.getSink() != null) {
                GraphNode sink = copyG.getSink();
                acyclicEdges.addAll(edgesIn.get(sink));
                copyG.getNodes().remove(sink);
                copyG.removeIngoingEdges(sink);
            }
            copyG.deleteAll_IsolatedNodes();

            while (copyG.getSource() != null) {
                GraphNode source = copyG.getSource();
                LinkedList<Edge> edgeSetOfV = edgesOut.get(source);
                acyclicEdges.addAll(edgeSetOfV);
                copyG.getNodes().remove(source);
                copyG.removeOutgoingEdges(source);
            }

            if (!copyG.getNodes().isEmpty()) {
                GraphNode v = copyG.getNodeWithMaxDiffDegree();
                LinkedList<Edge> edgeSetOfV = edgesOut.get(v);
                acyclicEdges.addAll(edgeSetOfV);
                copyG.removeNode(v);
            }
        }
        if (copyG.getNodes().size() > 0 || copyG.getEdges().size() > 0)
            System.out.println("something wrong in Greedy Cycle Removal - got left Nodes or Edges");
        reverseTheEdges(g,acyclicEdges);
    }
    public static void DFS_Florian(Graph g) {
        /*    Returns the edges of a cycle found via a directed, depth-first traversal. */
        for (GraphNode startNode : g.getNodes().values())
            if (startNode.getDfsStatus() == GraphNode.STATUS.unvisited)
                dfsRec(g, startNode);
        if (VERBOSE) edgesToBeReversed.forEach(System.out::println);
        if (VERBOSE && edgesToBeReversed.isEmpty()) System.out.println("Nothin' to reverse");
        edgesToBeReversed.forEach(g::reverseEdge);
        edgesToBeReversed.clear();

    }

    private static void dfsRec(Graph g, GraphNode node) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = g.getEdgesInMap();
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = g.getEdgesOutMap();
        if (g.isSink(node))  node.setDfsStatus(GraphNode.STATUS.done);
        else {               node.setDfsStatus(GraphNode.STATUS.visited);
            if (VERBOSE && DEBUG) System.out.printf("Current structure.GraphNode: %s \n", node.getLabel());
            if (edgesOut.containsKey(node) && edgesOut.get(node).size() > 0 ){
                for (Edge edge : edgesOut.get(node)) {
                    if (VERBOSE && DEBUG) edgesOut.get(node).forEach(System.out::println);
                    GraphNode child = edge.head;
                    if (VERBOSE && DEBUG) System.out.println("child = " + child);
                    if (child.getDfsStatus() == GraphNode.STATUS.visited) {
                        edgesToBeReversed.add(edge);
                        if (VERBOSE) System.out.printf("Cycle found, turning edge from %s to %s \n", node.getLabel(), child.getLabel()); }
                    else if (child.getDfsStatus() == GraphNode.STATUS.unvisited) {
                        dfsRec(g, child);
                    }
                }
            }
            node.setDfsStatus(GraphNode.STATUS.done);
        }
    }

    private static void reverseTheEdges(Graph g, LinkedHashSet<Edge> acyclicEdges) {
        if (acyclicEdges.size() < 1) throw new Error("No acyclic edges found");
        edgesToBeReversed = new LinkedHashSet<>(g.getEdges());
        edgesToBeReversed.removeAll(acyclicEdges);
        if (VERBOSE) System.out.println("Going to reverse following edges:");
        if (VERBOSE) edgesToBeReversed.forEach(System.out::println);
        edgesToBeReversed.forEach(g::reverseEdge);
        edgesToBeReversed.clear();
    }
}
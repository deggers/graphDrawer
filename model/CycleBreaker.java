//@formatter:off
package model;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/*A set of edges whose removal makes the digraph acyclic is commonly known as a feedback
arc set (FAS). Following the terminology used by Di Battista et al. [DETT99] we call a set
of edges whose reversal makes the digraph acyclic a feedback set (FS). Each FS is also a
FAS. However, not each FAS is a FS. For example, if a digraph has only one cycle, then the
set of all edges in the cycle is a FAS but not a FS.*/


public class CycleBreaker {
    /*    Returns the edges of a cycle found via a directed, depth-first traversal. */
    private static final boolean VERBOSE    = true;
    private static final boolean DEBUG      = false;
    private static HashSet<Edge> edgesToBeReversed = new LinkedHashSet<>(); //in their original form

    public static void      Berger_Shor(Graph g){
        Graph copyG = g.copy(g);
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeIn = copyG.getHashmap_nodeToEdgeIn();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeOut = copyG.getHashmap_nodeToEdgeOut();
        LinkedHashSet<Edge> acyclicEdges = new LinkedHashSet<>();
        for (GraphNode node: g.getNodes()) {
            if (copyG.getOutdegree(node) >= copyG.getIndegree(node) && copyG.getOutdegree(node) > 0) {
                acyclicEdges.addAll(nodeToEdgeOut.get(node));
                }
            else if(copyG.getIndegree(node) > 0)
                acyclicEdges.addAll(nodeToEdgeIn.get(node));

            if (copyG.getHashmap_nodeToEdgeIn().containsKey(node))
                copyG.removeIngoingEdges(node);
            if (copyG.getHashmap_nodeToEdgeOut().containsKey(node))
                copyG.removeOutgoingEdges(node);
        }

        edgesToBeReversed = new LinkedHashSet<>(g.getEdges());
        edgesToBeReversed.removeAll(acyclicEdges);
        if (VERBOSE) System.out.println("Going to reverse following edges:");
        if (VERBOSE) edgesToBeReversed.forEach(System.out::println);
        edgesToBeReversed.forEach(g::reverseEdge);
        edgesToBeReversed.clear();
    }
    public static void      GreedyCycleRemoval(Graph g){
/*        The first polynomial-time algorithm for solving the minimum FAS problem with an approximation
          ratio less than 2 in the worst case */
        Graph copyG = g.copy(g);
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeIn = copyG.getHashmap_nodeToEdgeIn();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeOut = copyG.getHashmap_nodeToEdgeOut();
        LinkedHashSet<Edge> acyclicEdges = new LinkedHashSet<>();

        while (!copyG.getNodes().isEmpty()){
            while (copyG.getSink() != null) {
                GraphNode sink = copyG.getSink();
                acyclicEdges.addAll(nodeToEdgeIn.get(sink));
                copyG.justRemoveNode(sink);
                copyG.removeIngoingEdges(sink);}

            copyG.getIsolatedNodes().forEach(copyG::justRemoveNode);

            while (copyG.getSource() != null){
                GraphNode source = copyG.getSource();
                LinkedList<Edge> edgeSetOfV = nodeToEdgeOut.get(source);
                acyclicEdges.addAll(edgeSetOfV);
                copyG.justRemoveNode(source);
                copyG.removeOutgoingEdges(source); }

            if (!copyG.getNodes().isEmpty()){
                GraphNode v = copyG.getNodeWithMaxDiffDegree();
                LinkedList<Edge> edgeSetOfV = nodeToEdgeOut.get(v);
                acyclicEdges.addAll(edgeSetOfV);
                copyG.justRemoveNode(v);
                copyG.removeOutgoingEdges(v);
                copyG.removeIngoingEdges(v); }
        }
        if  (copyG.getNodes().size() >0 || copyG.getEdges().size() > 0) System.out.println("something wrong in Greedy Cycle Removal - got left Nodes or Edges");

        edgesToBeReversed = g.getEdges();
        edgesToBeReversed.removeAll(acyclicEdges);
        if (VERBOSE) System.out.println("Going to reverse following edges:");
        if (VERBOSE) edgesToBeReversed.forEach(System.out::println);
        edgesToBeReversed.forEach(g::reverseEdge);
        edgesToBeReversed.clear();
    }
    public static void      DFS_Florian(Graph g) {
        for (GraphNode startNode : g.getNodes())
            if (startNode.getDfsStatus() == 'u')
                dfsRec(g, startNode);
        if (VERBOSE) edgesToBeReversed.forEach(System.out::println);
        edgesToBeReversed.forEach(g::reverseEdge);
        edgesToBeReversed.clear();
    }
    private static void     dfsRec(Graph g, GraphNode node) {
        if (g.isSink(node)) {
            node.setDfsStatus('f');
        } else {
            node.setDfsStatus('v');
            if (VERBOSE && DEBUG) System.out.printf("Current Node: %s \n", node.getLabel());
            if (g.getChildrenFrom(node) != null) {
                for (GraphNode graphNode : g.getChildrenFrom(node)) {
                    if (graphNode.getDfsStatus() == 'v') {
                        if (VERBOSE) System.out.printf("Cycle found, turning edge from %s to %s \n", node.getLabel(), graphNode.getLabel());
                        Edge edge =  g.getEdgeBetween(node, graphNode);
                        edgesToBeReversed.add(edge);
//                        String edgeType = g.getEdgeType();
//                        edgesToBeReversed.add(new Edge(node, graphNode, edgeType));
                    } else {
                        if (graphNode.getDfsStatus() == 'u')
                            dfsRec(g, graphNode);
                    }
                }
            }
            node.setDfsStatus('f');
        }
    }
}



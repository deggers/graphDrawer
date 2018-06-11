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
    private static final boolean VERBOSE = false;
        private static final boolean DEBUG = false;
    private static HashSet<Edge> edgesToBeTurned = new LinkedHashSet<>(); //in their original form


    public static void GreedyCycleRemoval(Graph g){
/*        The first polynomial-time algorithm for solving the minimum FAS problem with an approximation
          ratio less than 2 in the worst case */
        Graph copyG = g.copy(g);
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeIn = copyG.getHashmap_nodeToEdgeIn();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeOut = copyG.getHashmap_nodeToEdgeOut();
        LinkedHashSet<Edge> safeEdges = new LinkedHashSet<>();

        while (!copyG.getNodes().isEmpty()){
            while (copyG.getSink() != null) {
                GraphNode sink = copyG.getSink();
                safeEdges.addAll(nodeToEdgeIn.get(sink));
                copyG.justRemoveNode(sink);
                copyG.removeIngoingEdges(sink);}

            copyG.getIsolatedNodes().forEach(copyG::justRemoveNode);

            while (copyG.getSource() != null){
                GraphNode source = copyG.getSource();
                LinkedList<Edge> edgeSetOfV = nodeToEdgeOut.get(source);
                safeEdges.addAll(edgeSetOfV);
                copyG.justRemoveNode(source);
                copyG.removeOutgoingEdges(source); }

            if (!copyG.getNodes().isEmpty()){
                GraphNode v = copyG.getNodeWithMaxDiffDegree();
                LinkedList<Edge> edgeSetOfV = nodeToEdgeOut.get(v);
                safeEdges.addAll(edgeSetOfV);
                copyG.justRemoveNode(v);
                copyG.removeOutgoingEdges(v);
                copyG.removeIngoingEdges(v); }
        }
        if  (copyG.getNodes().size() >0 || copyG.getEdges().size() > 0) System.out.println("something wrong in Greedy Cycle Removal - got left Nodes or Edges");

        safeEdges.forEach(System.out::println);
        edgesToBeTurned.clear();
    }
    public static void DFS_Florian(Graph g) {
        for (GraphNode startNode : g.getNodes())
            if (startNode.getDfsStatus() == 'u')
                dfsRec(g, startNode);
        if (VERBOSE) edgesToBeTurned.forEach(System.out::println);
        edgesToBeTurned.forEach(g::reverseEdge);
        edgesToBeTurned.clear();
    }
    private static void dfsRec(Graph g, GraphNode node) {
        if (g.isSink(node)) {
            node.setDfsStatus('f');
        } else {
            node.setDfsStatus('v');
            if (VERBOSE && DEBUG) System.out.printf("Current Node: %s \n", node.getLabel());
            if (g.getChildrenOf(node) != null) {
                for (GraphNode graphNode : g.getChildrenOf(node)) {
                    if (graphNode.getDfsStatus() == 'v') {
                        if (VERBOSE) System.out.printf("Cycle found, turning edge from %s to %s \n", node.getLabel(), graphNode.getLabel());
                        Edge edge =  g.getEdgeBetween(node, graphNode);
                        edgesToBeTurned.add(edge);
//                        String edgeType = g.getEdgeType();
//                        edgesToBeTurned.add(new Edge(node, graphNode, edgeType));
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



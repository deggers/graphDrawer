//@formatter:off
package model;

import java.util.*;

/*A set of edges whose removal makes the digraph acyclic is commonly known as a feedback
arc set (FAS). Following the terminology used by Di Battista et al. [DETT99] we call a set
of edges whose reversal makes the digraph acyclic a feedback set (FS). Each FS is also a
FAS. However, not each FAS is a FS. For example, if a digraph has only one cycle, then the
set of all edges in the cycle is a FAS but not a FS.*/


public class CycleBreaker {
    /*    Returns the edges of a cycle found via a directed, depth-first traversal. */
    private static final boolean verbose = true;
    private static HashSet<Edge> turnedEdges = new LinkedHashSet<>(); //in their original form


    // TODO: 07.06.2018 : Add Constructor which build drawableGraph from listOfEdges

    public static drawableGraph GreedyCycleRemoval(drawableGraph g){
/*        The first polynomial-time algorithm for solving the minimum FAS problem with an approximation
          ratio less than 2 in the worst case */
        drawableGraph copyG = g.copy(g);
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeIn = copyG.getHashmap_nodeToEdgeIn();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgeOut = copyG.getHashmap_nodeToEdgeOut();
        LinkedHashSet<Edge> safeEdges = new LinkedHashSet<>();

        while (!copyG.getNodeSet().isEmpty()){
            while (copyG.getSink() != null) {
                GraphNode sink = copyG.getSink();
                safeEdges.addAll(nodeToEdgeIn.get(sink));
                copyG.justRemoveNode(sink);
                copyG.removeIngoingEdges(sink); }

            copyG.getIsolatedNodes().forEach(copyG::justRemoveNode);

            while (copyG.getSource() != null){
                GraphNode source = copyG.getSource();
//                System.out.println("source = " + source);
                LinkedList<Edge> edgeSetOfV = nodeToEdgeOut.get(source);
                safeEdges.addAll(edgeSetOfV);
                copyG.justRemoveNode(source);
                copyG.removeOutgoingEdges(source); }

            if (!copyG.getNodeSet().isEmpty()){
                GraphNode v = copyG.getNodeWithMaxDiffDegree();
                LinkedList<Edge> edgeSetOfV = nodeToEdgeOut.get(v);
                safeEdges.addAll(edgeSetOfV);
                copyG.justRemoveNode(v);
                copyG.removeOutgoingEdges(v);
                copyG.removeIngoingEdges(v); }
        }
        if  (copyG.getNodeSet().size() >0 || copyG.getEdgeList().size() > 0) System.out.println("something wrong in Greedy Cycle Removal - got left Nodes or Edges");
        return g;
    }


    public static void DFS_Florian(drawableGraph g) {
        //Tiefensuche um Zyklen zu entfernen
        HashMap<String, GraphNode> namesMap = new HashMap<>();
        for (GraphNode graphNode : g.getNodeSet()) {
            namesMap.put(graphNode.label, graphNode);
        }
        for (GraphNode startNode : g.getNodeSet()) {
            if (startNode.getDfsStatus() == 'u') {
                dfsRec(startNode);
            }
        }

        for (Edge edge : turnedEdges) {
            GraphNode startNode = (GraphNode) edge.start;
            GraphNode start = namesMap.get(startNode.getLabel());

            GraphNode targetNode = (GraphNode) edge.target;
            GraphNode target = namesMap.get(targetNode.getLabel());

            start.removeChild(target);
            start.addParent(target);
            target.removeParent(start);
            target.addChild(start);
        }


    }

    private static void dfsRec(GraphNode node) {
        if (node.isLeaf()) {
            node.setDfsStatus('f');
        } else {
            node.setDfsStatus('v');
            if (verbose == true) {
                System.out.printf("Current Node: %s \n\t\twith children: %s \n",node.getLabel() ,node.childrenLabels().toString());
            }
            for (Iterator<GraphNode> it = node.getChildren().iterator(); it.hasNext();) {
                GraphNode graphNode = it.next();
                if (graphNode.getDfsStatus() == 'v') {
                    if (verbose == true) {
                        System.out.printf("Cycle found, turning edge from %s to %s \n", node.label, graphNode.label);
                    }

                    GraphNode startNode = new GraphNode(node.label);
                    GraphNode targetNode = new GraphNode(graphNode.label);

                    turnedEdges.add(new Edge(startNode, targetNode));
                } else {
                    if (graphNode.getDfsStatus() == 'u') {
                        dfsRec(graphNode);
                    } else {
                        //Node has been finalized, nothing to do
                    }
                }
            }
            node.setDfsStatus('f');
        }

    }


}



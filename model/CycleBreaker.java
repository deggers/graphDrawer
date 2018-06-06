//@formatter:off
package model;

import java.util.*;

public class CycleBreaker {
    /*    Returns the edges of a cycle found via a directed, depth-first traversal. */
    private static final boolean verbose = true;
    private static HashSet<Edge> turnedEdges = new LinkedHashSet<>(); //in their turned form, hence start and target must be interchanged before drawing


    // TODO: 07.06.2018 : Add Constructor which build drawableGraph from listOfEdges

    public static void Eades_Dustyn(drawableGraph g){
        drawableGraph copyG = g.copy(g);

        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToIngoingEdges = copyG.getHashmap_nodeToIngoingEdges();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToOutgoingEdges = copyG.getHashmap_nodeToOutgoingEdges();

        LinkedHashSet<Edge> Heuristik_FAS = new LinkedHashSet<>();

        while (!copyG.getNodes().isEmpty()){
            while (copyG.getSink() != null) {
                GraphNode sink = copyG.getSink();
                System.out.println("sink = " + sink);
                LinkedList<Edge> edgeSetOfV = nodeToIngoingEdges.get(sink);
                Heuristik_FAS.addAll(edgeSetOfV);
                copyG.justRemoveNode(sink);
                copyG.removeIngoingEdges(sink); }

            copyG.getIsolatedNodes().forEach(copyG::justRemoveNode);

            while (copyG.getSource() != null){
                GraphNode source = copyG.getSource();
                System.out.println("source = " + source);
                LinkedList<Edge> edgeSetOfV = nodeToOutgoingEdges.get(source);
                Heuristik_FAS.addAll(edgeSetOfV);
                copyG.justRemoveNode(source);
                copyG.removeOutgoingEdges(source); }

            if (!copyG.getNodes().isEmpty()){
                GraphNode v = copyG.getNodeWithMaxDiffDegree();
                LinkedList<Edge> edgeSetOfV = nodeToOutgoingEdges.get(v);
                Heuristik_FAS.addAll(edgeSetOfV);
                copyG.justRemoveNode(v);
                copyG.removeOutgoingEdges(v);
                copyG.removeIngoingEdges(v); }
        }
        for (GraphNode node : copyG.getNodes()) {
            System.out.println("node = " + node);
        }

        for (Edge edge : copyG.getEdgeList()) {
            System.out.println("edge = " + edge);
        }

        System.out.println("Heuristik FAS = " + Heuristik_FAS);
    }


    public static void DFS_Florian(drawableGraph g) {
        //Tiefensuche um Zyklen zu entfernen
        HashMap<String, GraphNode> namesMap = new HashMap<>();
        for (GraphNode graphNode : g.getNodes()) {
            namesMap.put(graphNode.label, graphNode);
        }
        for (GraphNode startNode : g.getNodes()) {
            if (startNode.getDfsStatus() == 'u') {
                dfsRec(startNode);
            }
        }

        for (Edge edge : turnedEdges) {
            // turn me on!! no ,reverse me!
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
            for (GraphNode graphNode : node.getChildren()) {
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



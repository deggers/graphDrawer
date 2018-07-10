package Sugiyama;

import structure.Edge;
import structure.Graph;
import structure.GraphNode;
import structure.GraphNode.STATUS;

import java.util.*;

public class WCC {
    public static List<Graph> findWCC(Graph g){
        List<Graph> graphList = new ArrayList<>();
        LinkedList<GraphNode> nodes = new LinkedList<>(g.getNodes().values());
        while(nodes.size()>0){
            graphList.add(new Graph(g,rec(nodes,g)));
        }
        return graphList;
    }
    private static ArrayList<Edge> rec(LinkedList<GraphNode> nodes, Graph g){
        Set<GraphNode> allNodes;
        GraphNode start = nodes.getFirst();
        start.setDfsStatus(STATUS.done);
        allNodes = subrec(start,g);
        LinkedHashSet<Edge> edges = new LinkedHashSet<>(g.getEdges());
        Iterator<Edge> iter= edges.iterator();
        ArrayList<Edge> returnArrayList = new ArrayList<>();
        while (iter.hasNext()){
            Edge e = iter.next();
            if (allNodes.contains(e.head)||allNodes.contains(e.tail)){
                returnArrayList.add(e);
                iter.remove();
            }
        }
        for (GraphNode n : allNodes){
            nodes.remove(n);
        }
        return returnArrayList;
    }
    private static HashSet<GraphNode> subrec(GraphNode node,Graph g){
        HashSet<GraphNode> returnSet = new HashSet<>();
        for (GraphNode n : g.getParentsOf(node)){
            if (n.getDfsStatus().equals(STATUS.unvisited)){
                n.setDfsStatus(STATUS.done);
                returnSet.addAll(subrec(n,g));
            }
        }
        for (GraphNode n : g.getChildren(node)){
            if (n.getDfsStatus().equals(STATUS.unvisited)){
                n.setDfsStatus(STATUS.done);
                returnSet.addAll(subrec(n,g));
            }
        }
        return returnSet;
    }
}

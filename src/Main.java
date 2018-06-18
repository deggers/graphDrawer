import java.util.*;

//@formatter:off
public class Main {

    public static void main(String[] args) {

//        if (new Edge(new GraphNode("a"), new GraphNode("b")).equals(new Edge(new GraphNode("a"), new GraphNode("b")))){
//            System.out.println("new Edge( new Node, new Node)!");}

        LinkedHashSet<Edge> edges = new LinkedHashSet<>();
        edges.add(new Edge(new GraphNode("o_one"),new GraphNode("o_two")));
        edges.add(new Edge(new GraphNode("o_two"),new GraphNode("o_three")));
        edges.add(new Edge(new GraphNode("o_three"), new GraphNode("o_one")));
//        edges.add(new Edge(new GraphNode("o_three"), new GraphNode("o_four")));

        LinkedList<GraphNode> testNodes = new LinkedList<>();
        for (Edge edge : edges){
            testNodes.add(edge.tail);
            testNodes.add(edge.head);
        }
        LinkedHashSet<GraphNode> testHashNodes = new LinkedHashSet<>(testNodes);
        System.out.println(testHashNodes.contains(new GraphNode("o_one"))? "contains" : "not contained!");



        Graph graph = new Graph(edges);
        System.out.println(String.format("Actual Graph: \n%s\n",graph));
        CycleBreaker.DFS_Florian(graph);
        System.out.println(String.format("Actual Graph: \n%s\n",graph));

        AssignLayer.longestPath(graph);

        System.out.println("graph = " + graph);

//        Graph clonedG = new Graph(graph);
//        System.out.println(String.format("Actual clonedG: \n%s\n",clonedG));

    }

}

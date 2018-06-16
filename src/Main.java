import java.util.*;

//@formatter:off
public class Main {

    public static void main(String[] args) {

        LinkedHashSet<Edge> edges = new LinkedHashSet<>();
        edges.add(new Edge(new Node("o_one"),new Node("o_two")));
        edges.add(new Edge(new Node("o_two"),new Node("o_three")));
        edges.add(new Edge(new Node("o_three"), new Node("o_one")));
        edges.add(new Edge(new Node("o_lonely"), new Node("o_one")));

        Graph graph = new Graph(edges);
        System.out.println(String.format("Actual Graph: \n%s",graph));

    }

}

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


    public static class Graph {
        private LinkedHashMap<String,Node> nodesMap = new LinkedHashMap<>();
        private LinkedHashSet<Edge> edges;
        private LinkedHashSet<Node> nodes = new LinkedHashSet<>();
        private LinkedHashMap<Node,LinkedList<Edge>> edgesIn = new LinkedHashMap<>();
        private LinkedHashMap<Node,LinkedList<Edge>> edgesOut = new LinkedHashMap<>();

        Graph(LinkedHashSet<Edge> edges) {
            this.edges = edges;
            for(Edge e: edges) {
                if(!nodesMap.containsKey(e.tail.label))    {nodesMap.put(e.tail.label, e.tail);}
                if(!nodesMap.containsKey(e.head.label))    {nodesMap.put(e.head.label, e.head);}
                updateHashmaps(e);}

            this.nodes.addAll(nodesMap.values());
        }
        Graph(Graph graph){ // copyconstructor!
            LinkedHashSet<Edge> deepClonedEdges = new LinkedHashSet<>();
            LinkedHashMap<String,Node> deepClonedNodesMap = new LinkedHashMap<>();
            for (Edge edge : graph.edges){
                String tail = edge.tail.label;
                String head = edge.head.label;
                if (!deepClonedNodesMap.containsKey(tail)) deepClonedNodesMap.put(tail, new Node(tail));
                if (!deepClonedNodesMap.containsKey(head)) deepClonedNodesMap.put(head, new Node(head));
                Edge deepClonedEdge = new Edge(deepClonedNodesMap.get(tail),deepClonedNodesMap.get(head));
                deepClonedEdges.add(deepClonedEdge);
                updateHashmaps(deepClonedEdge);}

            this.edges = deepClonedEdges;
            this.nodesMap = deepClonedNodesMap;
            this.nodes.addAll(deepClonedNodesMap.values());
        }

        private void updateHashmaps(Edge e) {
            if (!edgesIn.containsKey(e.tail))       edgesIn.put(e.tail, new LinkedList<>());
            if (!edgesIn.containsKey(e.head))       edgesIn.put(e.head, new LinkedList<>());
            if (!edgesOut.containsKey(e.tail))      edgesOut.put(e.tail,new LinkedList<>());
            if (!edgesOut.containsKey(e.head))      edgesOut.put(e.head, new LinkedList<>());

            edgesIn.get(e.head).add(e);
            edgesOut.get(e.tail).add(e);
        }

        void     removeEdge(Edge edge){
            if (edge != null) {
            edges.remove(edge);
            Node tail = edge.tail;
            Node head = edge.head;
            edgesOut.get(tail).remove(edge);
            edgesIn.get(head).remove(edge);
            deleteAll_IsolatedNodes();

            }
            else System.out.println("Could not delete Edge");
        }

        void     deleteAll_IsolatedNodes(){
            LinkedList<Node> nodesToBeRemoved = new LinkedList<>();
            nodes.stream().filter(node -> inDegree(node) == 0 && outDegree(node) == 0).forEach(nodesToBeRemoved::add);
            nodesToBeRemoved.forEach(this::removeNode);
            nodesToBeRemoved.forEach(node -> {
                edgesOut.remove(node);
                edgesIn.remove(node);});
            }

        private void removeNode(Node node) {
            LinkedList<Edge> looseEdges = edgesOut.get(node);
            LinkedList<Edge> edgesToBeRemoved = new LinkedList<>();

            looseEdges.addAll(edgesIn.get(node));
            looseEdges.forEach(edge -> edgesIn.values().forEach(edgesIn -> edgesIn.remove(edge)));

            edgesOut.values().forEach(edgeList -> {
                edgeList.stream().filter(edge -> edge.contains(node)).forEach(edgesToBeRemoved::add);
                edgesToBeRemoved.forEach(edge -> edgesOut.values().forEach(edgesOut -> edgesOut.remove(edge)));});

            edgesOut.remove(node);
            looseEdges.addAll(edgesToBeRemoved);
            edges.removeAll(looseEdges);
            nodes.remove(node);
            nodesMap.remove(node.label);
            deleteAll_IsolatedNodes();}

        int             outDegree(Node node){ return edgesOut.containsKey(node) ? edgesOut.get(node).size() : 0;}
        int             inDegree(Node node) { return  edgesIn.containsKey(node) ? edgesIn.get(node).size()  : 0;}
        void            addEdge(Edge edge)  {
            this.edges.add(edge);
            updateNodesAndNodesMap(edge.tail);
            updateNodesAndNodesMap(edge.head);

            Node tail = nodesMap.get(edge.tail.label);
            Node head = nodesMap.get(edge.head.label);

            if (edgesOut.containsKey(tail))              edgesOut.get(tail).add(edge);
            else edgesOut.put(tail,new LinkedList<>(Collections.singletonList(edge)));
            if (edgesIn.containsKey(head))              edgesIn.get(head).add(edge);
            else edgesIn.put(head,new LinkedList<>(Collections.singletonList(edge)));
        }

        private void updateNodesAndNodesMap(Node node){
            if (!nodesMap.containsKey(node.label))
                nodesMap.put(node.label,node);
                nodes.add(node);
        }

        // getter and Setter Area :)
        LinkedHashMap<Node,LinkedList<Edge>>    getEdgesInMap(){ return this.edgesIn; }
        LinkedHashMap<Node,LinkedList<Edge>>    getEdgesOutMap(){ return this.edgesOut; }
        Node                                    getNode(String nodelabel) { return nodesMap.get(nodelabel); }
        Edge                                    getEdge(Node tail, Node head) {
            for (Edge edge: edges)
                if (edge.tail.equals(tail) && edge.head.equals(head))
                    return edge;
            System.out.println(String.format("No Edge found for tail: '%s' to head: '%s'",tail,head));
            return null;
        }



        @Override   public String   toString() { return "Nodes: " + nodes + " and \nEdges: " + edges; }
    }



    public static class Node {
        private String label;
        private int x, y;

        Node(String label){ this.label = label; }

        @Override public String     toString(){ return this.label; }
        @Override public boolean    equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return label != null?label.equals(node.label):node.label==null; }
        @Override public int        hashCode() { return label != null ? label.hashCode() : 0; }
    }
    public static class Edge {
        private Node tail, head;

        Edge(Node tail, Node head){
            this.tail = tail;
            this.head = head;}

        boolean contains(Node node) { return this.head.equals(node) || this.tail.equals(node); }

        @Override   public String   toString() { return String.format("'%s' to '%s'",tail,head); }
        @Override   public boolean  equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            if (tail != null ? !tail.equals(edge.tail) : edge.tail != null) return false;
            return head != null?head.equals(edge.head):edge.head==null; }
        @Override   public int      hashCode() {
            int result = tail != null ? tail.hashCode() : 0;
            result = 31 * result + (head != null ? head.hashCode() : 0);
            return result; }
    }

}

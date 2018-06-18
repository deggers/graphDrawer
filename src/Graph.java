import java.util.*;

//@formatter:off
public class    Graph {
    private static boolean VERBOSE = true;
    private LinkedHashMap<String, GraphNode> nodesMap = new LinkedHashMap<>();
    private LinkedHashSet<Edge> edges;
    private LinkedHashSet<GraphNode> graphNodes = new LinkedHashSet<>();
    private LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = new LinkedHashMap<>();
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap = new LinkedHashMap<>();


    Graph(LinkedHashSet<Edge> edges) {
        LinkedHashSet<Edge> newEdges = new LinkedHashSet<>();
        for (Edge e : edges) {
            if (!nodesMap.containsKey(e.tail.label))    nodesMap.put(e.tail.label, e.tail);
            if (!nodesMap.containsKey(e.head.label))    nodesMap.put(e.head.label, e.head);
            Edge newEdge = new Edge(nodesMap.get(e.tail.label), nodesMap.get(e.head.label));
            newEdges.add(newEdge);
            generateHashmaps(newEdge);
        }
        this.graphNodes.addAll(nodesMap.values());
        this.edges = newEdges;
    }

    Graph(Graph graph) { // copyconstructor!
        LinkedHashSet<Edge> deepClonedEdges = new LinkedHashSet<>();
        LinkedHashMap<String, GraphNode> deepClonedNodesMap = new LinkedHashMap<>();
        for (Edge edge : graph.edges) {
            String tail = edge.tail.label;
            String head = edge.head.label;
            if (!deepClonedNodesMap.containsKey(tail)) deepClonedNodesMap.put(tail, new GraphNode(tail));
            if (!deepClonedNodesMap.containsKey(head)) deepClonedNodesMap.put(head, new GraphNode(head));
            Edge deepClonedEdge = new Edge(deepClonedNodesMap.get(tail), deepClonedNodesMap.get(head));
            deepClonedEdges.add(deepClonedEdge);
            generateHashmaps(deepClonedEdge);
        }
        this.edges = deepClonedEdges;
        this.nodesMap = deepClonedNodesMap;
        this.graphNodes.addAll(deepClonedNodesMap.values());
        this.insertLayer(graph.layerMap);

    }

    void generateHashmaps(Edge e) {
        GraphNode tail = getNode(e.tail.label);
        GraphNode head = getNode(e.head.label);

        if (!edgesIn.containsKey(e.tail)) edgesIn.put(tail, new LinkedList<>());
        if (!edgesIn.containsKey(e.head)) edgesIn.put(head, new LinkedList<>());
        if (!edgesOut.containsKey(e.tail)) edgesOut.put(tail, new LinkedList<>());
        if (!edgesOut.containsKey(e.head)) edgesOut.put(head, new LinkedList<>());

        edgesIn.get(head).add(e);
        edgesOut.get(tail).add(e);
    }
    void removeNode(GraphNode graphNode) {
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>();
        LinkedList<Edge> looseEdges = new LinkedList<>();
        if (edgesOut.containsKey(graphNode)) looseEdges.addAll(edgesOut.get(graphNode));

        if (edgesIn.containsKey(graphNode)) {
            looseEdges.addAll(edgesIn.get(graphNode));
            looseEdges.forEach(edge -> edgesIn.values().forEach(edgesIn -> edgesIn.remove(edge)));
        }
        edgesOut.values().forEach(edgeList -> {
            edgeList.stream().filter(edge -> edge.contains(graphNode)).forEach(edgesToBeRemoved::add);
            edgesToBeRemoved.forEach(edge -> edgesOut.values().forEach(edgesOut -> edgesOut.remove(edge)));
        });

        edgesOut.remove(graphNode);
        looseEdges.addAll(edgesToBeRemoved);
        edges.removeAll(looseEdges);
        graphNodes.remove(graphNode);
        nodesMap.remove(graphNode.label);
        deleteAll_IsolatedNodes();
    }
    private void updateNodesAndNodesMap(GraphNode graphNode) {
        if (!nodesMap.containsKey(graphNode.label)){
            nodesMap.put(graphNode.label, graphNode);
            graphNodes.add(graphNode);
        }

    }
    private void addEdge(Edge edge) {
        this.edges.add(edge);
        updateNodesAndNodesMap(edge.tail);
        updateNodesAndNodesMap(edge.head);

        GraphNode tail = nodesMap.get(edge.tail.label);
        GraphNode head = nodesMap.get(edge.head.label);

        if (edgesOut.containsKey(tail)) edgesOut.get(tail).add(edge);
        else edgesOut.put(tail, new LinkedList<>(Collections.singletonList(edge)));
        if (edgesIn.containsKey(head)) edgesIn.get(head).add(edge);
        else edgesIn.put(head, new LinkedList<>(Collections.singletonList(edge)));
    }
    private void removeEdge(Edge edge) {
        if (edge != null) {
            edges.remove(edge);
            GraphNode tail = getNode(edge.tail.label);
            GraphNode head = getNode(edge.head.label);
            edgesOut.get(tail).remove(edge);
            edgesIn.get(head).remove(edge);
            deleteAll_IsolatedNodes();
        } else System.out.println("Could not delete Edge");
    }
    void deleteAll_IsolatedNodes() {
        LinkedList<GraphNode> nodesToBeRemoved = new LinkedList<>();
        for(GraphNode node: graphNodes) {if(inDegree(node) == 0 && outDegree(node) == 0)
            nodesToBeRemoved.add(node); }
        nodesToBeRemoved.forEach(this::removeNode);
        nodesToBeRemoved.forEach(graphNode -> {
            edgesOut.remove(graphNode);
            edgesIn.remove(graphNode);
        });
    }
    LinkedList<GraphNode> getIsolatedNodes(){
        LinkedList<GraphNode> isolatedNodes = new LinkedList<>();
        for(GraphNode node: graphNodes) {if(inDegree(node) == 0 && outDegree(node) == 0)
            isolatedNodes.add(node); }
        return isolatedNodes;
        }
    void insertLayer(LinkedHashMap<Integer, LinkedList<GraphNode>> otherLayerMap) {
        this.layerMap.clear();
        LinkedHashMap<Integer,LinkedList<GraphNode>> ownLayerMap = new LinkedHashMap<>();
        for (Map.Entry<Integer,LinkedList<GraphNode>> entry : otherLayerMap.entrySet()){
                Integer layer = entry.getKey();
                ownLayerMap.put(layer, new LinkedList<>());
                LinkedList<GraphNode> nodes = entry.getValue();
                for (GraphNode node : nodes){
                    GraphNode myNode = this.getNode(node.label);
                    myNode.setLayer(layer);
                    ownLayerMap.get(layer).add(myNode);
                }
        }
        this.layerMap = ownLayerMap;
    }
    void reverseLayerOrder(){
        // no good idea how to reverse - so naive approach.. --dustyn
        LinkedHashMap<Integer, LinkedList<GraphNode>> sortedReversed = new LinkedHashMap<>();
        int reverseCounter = this.layerMap.size() + 1;
        for (int layer : this.layerMap.keySet())
            sortedReversed.put(reverseCounter - layer, this.layerMap.get(layer));
        this.layerMap = sortedReversed;

        for (int layer : layerMap.keySet())
            for (GraphNode node : layerMap.get(layer))
                node.setLayer(layer);
    }
    int outDegree(GraphNode graphNode) {
        return edgesOut.containsKey(graphNode) ? edgesOut.get(graphNode).size() : 0;
    }
    int inDegree(GraphNode graphNode) {
        return edgesIn.containsKey(graphNode) ? edgesIn.get(graphNode).size() : 0;
    }
    void removeEdgesIn(GraphNode node) {

    }
    void reverseEdge(Edge edge) {
        if (edges.contains(edge)) {
            GraphNode start = this.getNode(edge.tail.label);
            GraphNode target = this.getNode(edge.head.label);
            String edgeType = edge.edgeType;
            Edge newEdge = new Edge(target,start,edgeType,true);
            addEdge(newEdge);
            removeEdge(edge);
//            if (VERBOSE) System.out.printf("turned: %s to %s \n", start.label, target.label);
        } else System.out.println("EDGE COULDN'T BE TURNED, WTF!");
    }
    GraphNode getSink() {
        for (GraphNode node : graphNodes)
            if (outDegree(node) == 0 && inDegree(node) > 0) return node;
        return null;
    }
    GraphNode getSource() {
        for (GraphNode node : graphNodes)
            if (inDegree(node) == 0) return node;
        return null;
    }
    void removeIngoingEdges(GraphNode node) {
        edges.removeAll(edgesIn.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(edgesIn.get(node));

        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode startNode = edgeToBeRemoved.tail;
            LinkedList<Edge> modifiedListOut = edgesOut.get(startNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error to remove edge in modifiedList");
            edgesOut.put(startNode, modifiedListOut);
        }
        edgesIn.keySet().removeIf(entry -> entry == node);
    }
    void removeOutgoingEdges(GraphNode node) {
        edges.removeAll(edgesOut.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(edgesOut.get(node));
        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode targetNode = edgeToBeRemoved.head;
            LinkedList<Edge> modifiedListOut = edgesIn.get(targetNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error");
            edgesIn.put(targetNode, modifiedListOut);
        }
        edgesOut.keySet().removeIf(entry -> entry == node);
    }
    GraphNode getNodeWithMaxDiffDegree() {
        int max = Integer.MIN_VALUE;
        GraphNode winnerNode = null;
        for (GraphNode node : graphNodes) {
            if (outDegree(node) - inDegree(node) > max) {
                max = outDegree(node) - inDegree(node);
                winnerNode = node;
            }
        }
        return winnerNode;
    }
    ArrayList<LinkedList<GraphNode>> allPermutationsOf(LinkedList<GraphNode> input) {
        ArrayList<LinkedList<GraphNode>> output = new ArrayList<>();

        return null;
    }
    ArrayList<LinkedList<GraphNode>> heapGenerate(int n, LinkedList<GraphNode> list, ArrayList<LinkedList<GraphNode>> result) {
        if (n == 1) result.add(new LinkedList<>(list));
        else for (int i = 0; i < n; i++) {
            heapGenerate(n - 1, list, result);
            if (n % 2 == 0) Collections.swap(list, i, n - 1);
            else Collections.swap(list, 0, n - 1);
        }
        return result;
    }
    boolean isSink(GraphNode node) { return !edgesOut.containsKey(node);}


    // getter and Setter Area :)
    LinkedHashMap<GraphNode, LinkedList<Edge>> getEdgesInMap() {
        return this.edgesIn;
    }
    LinkedHashMap<GraphNode, LinkedList<Edge>> getEdgesOutMap() {
        return this.edgesOut;
    }
    GraphNode                   getNode(String nodelabel) {
        return this.nodesMap.get(nodelabel);
    }
    LinkedHashSet<GraphNode>    getNodes() {
        return this.graphNodes;
    }
    LinkedHashSet<Edge>         getEdges() {
        return this.edges;
    }
    Edge                        getEdge(GraphNode tail, GraphNode head) {
        for (Edge edge : edges)
            if (edge.tail.equals(tail) && edge.head.equals(head))
                return edge;
        System.out.println(String.format("No Edge found for tail: '%s' to head: '%s'", tail, head));
        return null;
    }
    LinkedList<GraphNode>       getAllSinks() {
        LinkedList<GraphNode> allSinks = new LinkedList<>();
        for (GraphNode node : getNodes()){
            if (outDegree(node) == 0 && inDegree(node) >= 1){
                allSinks.add(node); }
        }
        return allSinks; }
    LinkedList<GraphNode>       getParentsOf(GraphNode node) {
        LinkedList<GraphNode> parents = new LinkedList<>();
        if (edgesIn.containsKey(node)){
            for (Edge edge : edgesIn.get(node))
                parents.add(edge.tail);
            return parents;
        }
        return null;}
    LinkedList<GraphNode>       getChildren(GraphNode parent){
        LinkedList<GraphNode> returnList = new LinkedList<>();
        if (edgesOut.containsKey(parent) && edgesOut.get(parent).size() >= 1) {
            for (Edge edge : edgesOut.get(parent))
                returnList.add(edge.head);
        }
        return returnList;}

    void addDummies() {
        LinkedList<Edge> edgesToDelete = new LinkedList<>();
        LinkedList<Edge> edgesNew = new LinkedList<>();

        System.out.println("layerMap = " + layerMap);

        for (Edge edge : edges) {
            GraphNode start = this.getNode(edge.tail.label);
            GraphNode target = this.getNode(edge.head.label);

            int spanningLevels = Math.abs(start.getLayer() - target.getLayer()) - 1;
            if (spanningLevels > 0) {
                edgesToDelete.add(edge);
                List<GraphNode> block = new LinkedList<>();
                block.add(start);

                for (int i = spanningLevels; i > 0; i--) { // for every additional spanned level
                    String label = "Dummy-" + i;
                    GraphNode dummyNode = new GraphNode(label, "Dummy", true); // create a dummy with a proper label
                    int layer = target.getLayer() + i;  // loooks wrong... --dustyn
                    dummyNode.setLayer(layer);

                    layerMap.get(layer).add(dummyNode);
                    graphNodes.add(dummyNode); // add to NodeSet
                    block.add(dummyNode); // add to block to find parent/children
                }
                block.add(target);

                for (int i = 0; i < block.size(); i++) { // hoffe das wirft keinen outOfBounds
                    if (!block.get(i).equals(target)) {
                        edgesNew.add(new Edge(block.get(i), block.get(i+1),edge.edgeType, edge.reversed)); // likewise, add Edge to edgeList
                    }
                }
            }
        } // end-for edge

        edgesToDelete.forEach(this::removeEdge);
        edgesNew.forEach(this::addEdge);
    }



    @Override    public String toString() {
        return "Nodes: " + graphNodes + " and \nEdges: " + edges;
    }

}
class           GraphNode {
    String label, nodeType;
    int x, y;
    private int layer = -1;
    boolean isDummy = false;

    enum STATUS {done, unvisited, visited}

    private STATUS status = STATUS.unvisited;

    GraphNode(String label) {
        this.label = label;
    }

    GraphNode(String label, boolean isDummy) {
        this(label);
        this.isDummy = isDummy;
    }

    GraphNode(String label, String nodeType, boolean isDummy) {
        this(label,isDummy);
        this.isDummy = isDummy;

    }

    public STATUS getDfsStatus() {
        return this.status;
    }

    public void setDfsStatus(STATUS status) {
        this.status = status;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public String toString() {
        return this.label+":"+layer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;
        GraphNode graphNode = (GraphNode) o;
        return label != null ? label.equals(graphNode.label) : graphNode.label == null;
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }
}
class           Edge {
    GraphNode tail, head;
    boolean reversed = false;
    public String edgeType;


    Edge(GraphNode tail, GraphNode head) {
        this.tail = tail;
        this.head = head;
    }

    Edge(GraphNode tail, GraphNode head, String edgeType, boolean reversed) {
        this(tail, head);
        this.edgeType = edgeType;
        this.reversed = reversed;
    }

    boolean contains(GraphNode graphNode) {
        return this.head.equals(graphNode) || this.tail.equals(graphNode);
    }

    @Override
    public String toString() {
        return String.format("'%s' to '%s'", tail, head);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        if (tail != null ? !tail.equals(edge.tail) : edge.tail != null) return false;
        return head != null ? head.equals(edge.head) : edge.head == null;
    }

    @Override
    public int hashCode() {
        int result = tail != null ? tail.hashCode() : 0;
        result = 31 * result + (head != null ? head.hashCode() : 0);
        return result;
    }
}
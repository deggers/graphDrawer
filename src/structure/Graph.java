package src.structure;

import structure.GraphNode;

import java.util.*;


public class Graph {
    private static boolean VERBOSE = true;
    private LinkedHashMap<String, Integer> crossings = new LinkedHashMap<>();
    private LinkedHashSet<Edge> edges = new LinkedHashSet<>();
    private LinkedHashMap<GraphNode, GraphNode> nodes = new LinkedHashMap<>();
    private ArrayList<String> edgeTypes = new ArrayList<>();
    private LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = new LinkedHashMap<>();
    private LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = new LinkedHashMap<>();
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap = new LinkedHashMap<>();

    // BK stuff
    private LinkedHashMap<String, LinkedHashMap<GraphNode, GraphNode>> alignBlock = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<GraphNode, GraphNode>> rootBlock  = new LinkedHashMap<>();

    public Graph() {
        this.crossings = new LinkedHashMap<>();
        this.edges = new LinkedHashSet<>();
        this.nodes = new LinkedHashMap<>();
        this.edgeTypes = new ArrayList<>();
        this.edgesIn = new LinkedHashMap<>();
        this.edgesOut = new LinkedHashMap<>();
        this.layerMap = new LinkedHashMap<>();
    }

    public Graph(Graph graph) { // copyconstructor!
        for (Edge e : graph.edges) {
            GraphNode deepTail = new GraphNode(e.tail);
            GraphNode deepHead = new GraphNode(e.head);
            // Process Tail
            if (!nodes.containsKey(deepTail)) nodes.put(deepTail,deepTail);
            if (!layerMap.containsKey(deepTail.getLayer())) layerMap.put(deepTail.getLayer(), new LinkedList<>());
            if (!layerMap.get(deepTail.getLayer()).contains(deepTail)) layerMap.get(deepTail.getLayer()).add(deepTail);
            // Process Head
            if (!nodes.containsKey(deepHead)) nodes.put(deepHead,deepHead);
            if (!layerMap.containsKey(deepHead.getLayer())) layerMap.put(deepHead.getLayer(), new LinkedList<>());
            if (!layerMap.get(deepHead.getLayer()).contains(deepHead)) layerMap.get(deepHead.getLayer()).add(deepHead);
            // Process new Edges
            Edge deepClonedEdge = new Edge(nodes.get(e.tail), nodes.get(e.head), e.getEdgeType(),e.isReversed());
            this.edges.add(deepClonedEdge);
            addToHashmap(deepClonedEdge);}
    }

    public Graph(Graph origGraph, ArrayList<Edge> restrictedEdgeSet) {
        Graph graph = new Graph();
        for (Edge e : restrictedEdgeSet) {
            this.edges.add(e);
            addToHashmap(e);
        }
        this.nodes = origGraph.getNodes();
        this.layerMap = origGraph.getLayerMap();
    }

    private void addToHashmap(Edge e) {
        GraphNode tail = e.tail;
        GraphNode head = e.head;

        if (!edgesIn.containsKey(e.tail)) edgesIn.put(tail, new LinkedList<>());
        if (!edgesIn.containsKey(e.head)) edgesIn.put(head, new LinkedList<>());
        if (!edgesOut.containsKey(e.tail)) edgesOut.put(tail, new LinkedList<>());
        if (!edgesOut.containsKey(e.head)) edgesOut.put(head, new LinkedList<>());

        edgesIn.get(head).add(e);
        edgesOut.get(tail).add(e);
    }
    public void removeNode(GraphNode graphNode) {
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
        nodes.remove(graphNode);
        deleteAll_IsolatedNodes();
    }
    public void addEdge(String tail,String head){
        GraphNode tailNode = new GraphNode(tail);
        GraphNode headNode = new GraphNode(head);
        if (!nodes.containsKey(tailNode)) nodes.put(tailNode,tailNode);
        if (!nodes.containsKey(headNode)) nodes.put(headNode,headNode);
        Edge edge = new Edge(nodes.get(tailNode), nodes.get(headNode));
        edges.add(edge);

        if (edgesOut.containsKey(tailNode)) edgesOut.get(tailNode).add(edge);
        else edgesOut.put(tailNode, new LinkedList<>(Collections.singletonList(edge)));
        if (edgesIn.containsKey(headNode)) edgesIn.get(headNode).add(edge);
        else edgesIn.put(headNode, new LinkedList<>(Collections.singletonList(edge)));
    }
    public void addEdge(Edge edge) {
        this.edges.add(edge);
        if (!nodes.containsKey(edge.tail)) nodes.put(edge.tail, edge.tail);
        if (!nodes.containsKey(edge.head)) nodes.put(edge.head, edge.head);

        GraphNode tail = edge.tail;
        GraphNode head = edge.head;

        if (edgesOut.containsKey(tail)) edgesOut.get(tail).add(edge);
        else edgesOut.put(tail, new LinkedList<>(Collections.singletonList(edge)));
        if (edgesIn.containsKey(head)) edgesIn.get(head).add(edge);
        else edgesIn.put(head, new LinkedList<>(Collections.singletonList(edge)));
    }
    private void removeEdge(Edge edge) {
        if (edge != null) {
            edges.remove(edge);
            GraphNode tail = edge.tail;
            GraphNode head = edge.head;
            edgesOut.get(tail).remove(edge);
            edgesIn.get(head).remove(edge);
            deleteAll_IsolatedNodes();
        } else System.out.println("Could not delete structure.Edge");
    }
    public void deleteAll_IsolatedNodes() {
        LinkedList<GraphNode> nodesToBeRemoved = new LinkedList<>();
        for(GraphNode node: nodes.values()) {if(inDegree(node) == 0 && outDegree(node) == 0)
            nodesToBeRemoved.add(node); }
        nodesToBeRemoved.forEach(this::removeNode);
        nodesToBeRemoved.forEach(graphNode -> {
            edgesOut.remove(graphNode);
            edgesIn.remove(graphNode);
        });
    }
    public LinkedList<GraphNode> getIsolatedNodes(){
        LinkedList<GraphNode> isolatedNodes = new LinkedList<>();
        for(GraphNode node: nodes.values()) {if(inDegree(node) == 0 && outDegree(node) == 0)
            isolatedNodes.add(node); }
        return isolatedNodes; }
    public void insertLayer(LinkedHashMap<Integer, LinkedList<GraphNode>> otherLayerMap) {
        this.layerMap.clear();
        LinkedHashMap<Integer,LinkedList<GraphNode>> ownLayerMap = new LinkedHashMap<>();
        for (Map.Entry<Integer,LinkedList<GraphNode>> entry : otherLayerMap.entrySet()){
            int layer = entry.getKey();
            ownLayerMap.put(layer, new LinkedList<>());
            for (GraphNode copyNode : entry.getValue()){
                GraphNode myNode = nodes.get(copyNode);
                myNode.setLayer(layer);
                ownLayerMap.get(layer).add(myNode);
            }
        }
        this.layerMap = ownLayerMap;
    }

    public void setLayerMap(LinkedHashMap<Integer, LinkedList<GraphNode>> myLayerMap) {
        this.layerMap = myLayerMap;
    }

    public void reverseLayerOrder(){
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
    public int outDegree(GraphNode graphNode) {
        return edgesOut.containsKey(graphNode) ? edgesOut.get(graphNode).size() : 0;
    }
    public int inDegree(GraphNode graphNode) {
        return edgesIn.containsKey(graphNode) ? edgesIn.get(graphNode).size() : 0;
    }
    void removeEdgesIn(GraphNode node) {

    }
    public void reverseEdge(Edge edge) {
        if (edges.contains(edge)) {
            GraphNode start = edge.tail;
            GraphNode target = edge.head;
            String edgeType = edge.getEdgeType();
            Edge newEdge = new Edge(target,start,edgeType,true);
            addEdge(newEdge);
            removeEdge(edge);
            if (VERBOSE) System.out.printf("turned: %s to %s %n", start.getLabel(), target.getLabel());
        } else System.out.println("EDGE COULDN'T BE TURNED, WTF!");
    }
    public GraphNode getSink() {
        for (GraphNode node : nodes.values())
            if (outDegree(node) == 0 && inDegree(node) > 0) return node;
        return null;
    }
    public GraphNode getSource() {
        for (GraphNode node : nodes.values())
            if (inDegree(node) == 0) return node;
        return null;
    }
    public void removeIngoingEdges(GraphNode node) {
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
    public void removeOutgoingEdges(GraphNode node) {
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
    public GraphNode getNodeWithMaxDiffDegree() {
        int max = Integer.MIN_VALUE;
        GraphNode winnerNode = null;
        for (GraphNode node : nodes.values()) {
            if (outDegree(node) - inDegree(node) > max) {
                max = outDegree(node) - inDegree(node);
                winnerNode = node;
            }
        }
        return winnerNode;
    }
    public ArrayList<LinkedList<GraphNode>> heapGenerate(int n, LinkedList<GraphNode> list, ArrayList<LinkedList<GraphNode>> result) {
        if (n == 1) result.add(new LinkedList<>(list));
        else for (int i = 0; i < n; i++) {
            heapGenerate(n - 1, list, result);
            if (n % 2 == 0) Collections.swap(list, i, n - 1);
            else Collections.swap(list, 0, n - 1);
        }
        return result;
    }
    public boolean isSink(GraphNode node) { return !edgesOut.containsKey(node);}
    public void addDummies() {
        LinkedList<Edge> edgesToDelete = new LinkedList<>();
        LinkedList<Edge> edgesNew = new LinkedList<>();

        for (Edge edge : edges) {
            GraphNode start = edge.tail;
            GraphNode target = edge.head;

            int spanningLevels = Math.abs(start.getLayer() - target.getLayer()) - 1;
            if (spanningLevels > 0) {
                edgesToDelete.add(edge);
                List<GraphNode> block = new LinkedList<>();
                block.add(start);

                for (int i = spanningLevels; i > 0; i--) { // for every additional spanned level
                    String label = String.format("D%s-%s-%s",i,target.getLabel(),start.getLabel());
                    GraphNode dummyNode = new GraphNode(label, "Dummy", true); // create a dummy with a proper label
                    int layer = target.getLayer() - i;  // loooks wrong... --dustyn
                    dummyNode.setLayer(layer);

                    layerMap.get(layer).add(dummyNode);
                    nodes.put(dummyNode,dummyNode); // add to NodeSet
                    block.add(dummyNode); // add to block to find parent/children
                }
                block.add(target);

                for (int i = 0; i < block.size(); i++) { // hoffe das wirft keinen outOfBounds
                    if (!block.get(i).equals(target)) {
                        edgesNew.add(new Edge(block.get(i), block.get(i+1),edge.getEdgeType(), edge.getReversed())); // likewise, add structure.Edge to edgeList
                    }
                }
            }
        } // end-for edge

        edgesToDelete.forEach(this::removeEdge);
        edgesNew.forEach(this::addEdge);
    }

    public Edge getEdgeBetween(GraphNode start, GraphNode target) {
        for (Edge e : getEdges()) {
            if ( (e.tail.equals(start) && e.head.equals(target)) || (e.tail.equals(target) && e.head.equals(start) ) ) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<GraphNode> getAdjacentNodes(GraphNode node, int onLayer) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesIn = this.getEdgesInMap();
        LinkedHashMap<GraphNode, LinkedList<Edge>> edgesOut = this.getEdgesOutMap();

        ArrayList<GraphNode> adjToNodeWithLayerRestriction = new ArrayList<>();
        if (edgesOut.containsKey(node)) adjToNodeWithLayerRestriction.addAll(this.getChildren(node));
        if (edgesIn.containsKey(node)) adjToNodeWithLayerRestriction.addAll(this.getParentsOf(node));
        adjToNodeWithLayerRestriction.removeIf(particularNode -> particularNode.getLayer() != onLayer);
        return adjToNodeWithLayerRestriction;
    }



    // getter and Setter Area :)
    public LinkedHashMap<GraphNode, LinkedList<Edge>>      getEdgesInMap() {
        return this.edgesIn;
    }
    public LinkedHashMap<Integer, LinkedList<GraphNode>>   getLayerMap() { return layerMap; }
    public LinkedHashMap<GraphNode, LinkedList<Edge>>      getEdgesOutMap() {
        return this.edgesOut;
    }
    public LinkedHashMap<GraphNode,GraphNode>              getNodes() {
        return this.nodes;
    }
    public HashSet<Edge>                                   getEdges() {
        return this.edges;
    }
    public Edge                                            getEdge(GraphNode tail, GraphNode head) {
        for (Edge edge : edges)
            if (edge.tail.equals(tail) && edge.head.equals(head))
                return edge;
        System.out.println(String.format("No structure.Edge found for tail: '%s' to head: '%s'", tail, head));
        return null;
    }
    public LinkedList<GraphNode>                           getAllSinks() {
        LinkedList<GraphNode> allSinks = new LinkedList<>();
        for (GraphNode node : nodes.values()){
            if (outDegree(node) == 0 && inDegree(node) >= 1){
                allSinks.add(node); }
        }
        return allSinks; }
    public LinkedList<GraphNode>                           getParentsOf(GraphNode node) {
        LinkedList<GraphNode> parents = new LinkedList<>();
        if (edgesIn.containsKey(node)){
            for (Edge edge : edgesIn.get(node))
                parents.add(edge.tail);
            return parents;
        }
        return null;}
    public LinkedList<GraphNode>                           getChildren(GraphNode parent){
        LinkedList<GraphNode> returnList = new LinkedList<>();
        if (edgesOut.containsKey(parent) && edgesOut.get(parent).size() >= 1) {
            for (Edge edge : edgesOut.get(parent))
                returnList.add(edge.head);
        }
        return returnList;}
    public LinkedHashMap<String, Integer>                                             getCrossings() {
        return crossings;
    }
    public void                                            setCrossings(String layer, int crossings) {
        this.crossings.put(layer, crossings);
    }

    public void resetAllPorts(){
        this.nodes.values().forEach(GraphNode::resetPortMap);
    }
    public void addEdgeType(String edgeType) {
        if (!edgeTypes.contains(edgeType))
            edgeTypes.add(edgeType);
    }

    public ArrayList<String> getEdgeTypes() { return edgeTypes; }


    @Override    public String toString() {
        return "Nodes: " + nodes + " and \nEdges: " + edges;
    }

    public void addNode(GraphNode graphNode) {
        if (!nodes.containsKey(graphNode)) nodes.put(graphNode, graphNode);
    }

    private List<Edge> getEdgesOfType(String edgeType) {
        List<Edge> relevantEdges = new LinkedList<>();
        for (Edge e : edges)
            if (e.getEdgeType().equals(edgeType))
                relevantEdges.add(e);
        return relevantEdges;
    }

    public Graph copyWithRestrains(String selectedEdgeType) {
        LinkedHashSet<Edge> relevantEdges = new LinkedHashSet<>(getEdgesOfType(selectedEdgeType));
        Graph returnGraph = new Graph();
        relevantEdges.forEach(returnGraph::addEdge);
        return returnGraph;
    }

    public LinkedHashMap<String, LinkedHashMap<GraphNode, GraphNode>> getAlignBlock() {
        return alignBlock;
    }
    public void setAlignBlock(String direction, LinkedHashMap<GraphNode,GraphNode> alignBlock) {
        this.alignBlock.put(direction, alignBlock);
    }

    public void setRootBlock(String direction, LinkedHashMap<GraphNode,GraphNode> rootBlock) {
        this.rootBlock.put(direction, rootBlock);
    }
    public LinkedHashMap<String, LinkedHashMap<GraphNode, GraphNode>> getRootBlock() {
        return rootBlock;
    }

}
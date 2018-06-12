package model;
//@formatter:off
import java.util.*;

public class Graph {
    private static final boolean VERBOSE = false;
    private LinkedHashSet<GraphNode> nodeSet;
    private LinkedHashSet<Edge> edgeSet;
    private LinkedList<HelperTypes.EdgeType> edgeTypeList = new LinkedList<>();
    private LinkedHashMap<Integer, GraphNode> nodeToLayer;
    private LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap;

    private LinkedHashSet<String> visitedNodesForExtractSubtreeSet = null;
    private HashSet<Edge> temporaryEdgeSubset = null;

    private LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesOut;
    private LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesIn;

    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeOut() {
        return nodeToEdgesOut;
    }

    LinkedHashMap<GraphNode, LinkedList<Edge>> getHashmap_nodeToEdgeIn() {
        return nodeToEdgesIn;
    }


    // Constructor
    Graph(LinkedHashSet<Edge> inputEdges) {
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesOut = new LinkedHashMap<>();
        LinkedHashMap<GraphNode, LinkedList<Edge>> nodeToEdgesIn = new LinkedHashMap<>();
        LinkedHashMap<Integer, GraphNode> nodeToLayer = new LinkedHashMap<>();
        LinkedHashMap<Integer, LinkedList<GraphNode>> layerMap = new LinkedHashMap<>();

        LinkedHashSet<GraphNode> clonedNodes = new LinkedHashSet<>();
        LinkedHashMap<String, GraphNode> nodeMap = new LinkedHashMap<>();
        LinkedHashSet<Edge> clonedEdges = new LinkedHashSet<>();


        for (Edge originalEdge : inputEdges) {

            GraphNode start = originalEdge.start;
            int startLayer = start.getLayer();
            GraphNode clonedStart = new GraphNode(start.getLabel(), start.getNodeType(), startLayer);
//            nodeToLayer.put(startLayer, clonedStart);

            GraphNode target = originalEdge.target;
            int targetLayer = target.getLayer();
            GraphNode clonedTarget = new GraphNode((target.getLabel()), target.getNodeType(), targetLayer);
//            nodeToLayer.put(targetLayer, clonedTarget);

            if (!nodeMap.containsKey(clonedStart.getLabel()))
                nodeMap.put(clonedStart.getLabel(), clonedStart);
            if (!nodeMap.containsKey(clonedTarget.getLabel()))
                nodeMap.put(clonedTarget.getLabel(), clonedTarget);

            GraphNode referencedStartClone = nodeMap.get(clonedStart.getLabel());
            GraphNode referencedTargetClone = nodeMap.get(clonedTarget.getLabel());

            Edge clonedEdge = new Edge(referencedStartClone, referencedTargetClone, originalEdge.edgeType);
            clonedEdges.add(clonedEdge);

            // update Hashmaps :)
            LinkedList<Edge> edgeSetOut = new LinkedList<>();
            if (nodeToEdgesOut.containsKey(clonedStart))
                edgeSetOut.addAll(nodeToEdgesOut.get(clonedStart));
            edgeSetOut.add(clonedEdge);
            nodeToEdgesOut.put(clonedStart, edgeSetOut);

            LinkedList<Edge> edgeSetIn = new LinkedList<>();
            if (nodeToEdgesIn.containsKey(clonedTarget))
                edgeSetIn.addAll(nodeToEdgesIn.get(clonedTarget));
            edgeSetIn.add(clonedEdge);
            nodeToEdgesIn.put(clonedTarget, edgeSetIn);
        }

        this.nodeToEdgesOut = nodeToEdgesOut;
        this.nodeToEdgesIn = nodeToEdgesIn;

        for (String str : nodeMap.keySet())
            clonedNodes.add(nodeMap.get(str));

        this.nodeSet = clonedNodes;
        this.edgeSet = clonedEdges;
        this.nodeToLayer = nodeToLayer;

        for (Map.Entry<Integer, GraphNode> entry : nodeToLayer.entrySet()) {
            int layer = entry.getKey();
            LinkedList<GraphNode> modifyList = new LinkedList<>();
            if (layerMap.containsKey(layer))
                modifyList.addAll(layerMap.get(layer));
            modifyList.add(entry.getValue());
            layerMap.put(layer, modifyList);
        }
        this.layerMap = layerMap;
    }

    public Graph copyWithRestrains(String edgeType) {
        LinkedHashSet<Edge> relevantEdges = new LinkedHashSet<>(getEdgesOfType(edgeType));
        return new Graph(relevantEdges);
    }


    public ArrayList<HelperTypes.EdgeType> getEdgeTypes() {
        return new ArrayList<>(edgeTypeList);
    }

    public List<String> getPossibleRootLabels(String edgeType) {
        List<String> possibleRoots = new LinkedList<>();
        boolean bTest;
        for (GraphNode node : nodeSet) {
            if (edgeType.equals("package")) {
                bTest = (!getEdgesOutWithType(node, edgeType).isEmpty() && node.getNodeType().equals("package"));
            } else {
                bTest = (getEdgesInWithType(node, edgeType).isEmpty() && !getEdgesOutWithType(node, edgeType).isEmpty());
            }
            if (bTest) {
                Tree tree = extractSubtreeFromProtoNode(node, edgeType);
                int depth = tree.getTreeDepth();
                int numNodes = tree.nodeList.size() - 1;
                possibleRoots.add(node.getLabel() + " (" + depth + "|" + numNodes + ")");
            }
        }
        possibleRoots.sort(null);
        return possibleRoots;//.size() > 0 ? possibleRoots : null;
    }

    public Tree extractSubtreeFromProtoNode(GraphNode root, String edgeType) { //setzt mit hilfe der Edge Liste und des gewählten Edge Types -> Parent und children für alle Noten, überschreibt bestehende Infos, damit konsekutive Auswahlen nicht interferieren
        if (root == null) {
            return null;
        }
        boolean bEdgeTypeValid = false;
        for (HelperTypes.EdgeType et : edgeTypeList) {
            if (et.id.equals(edgeType)) {
                bEdgeTypeValid = true;
                break;
            }
        }
        if (!bEdgeTypeValid) {
            System.out.println("Error: chosen edgeType: " + edgeType + " not in edgeTypeList");
            return null;
        }
        TreeNode rootnode = root.toTreeNode();
        rootnode.parent = null;
        temporaryEdgeSubset = new HashSet<>(); //startlabel, edge
        for (Edge edge : edgeSet) { //zum schnelleren finden der Kanten des Teilbaumes
            if (edge.edgeType.equals(edgeType)) {
                temporaryEdgeSubset.add(edge);
            }
        }
        // reset the temporary sets for computing the subtree
        visitedNodesForExtractSubtreeSet = new LinkedHashSet<>();
        extractSubtreeFromRootRecursion(rootnode);
        temporaryEdgeSubset = null;
        visitedNodesForExtractSubtreeSet = null;
        return new Tree(rootnode);
    }

    private void extractSubtreeFromRootRecursion(TreeNode node) {
//        System.out.println("recursion at node:" + node);
        node.resetChildren();
        visitedNodesForExtractSubtreeSet.add(node.label);
        for (Edge edge : temporaryEdgeSubset) {
            GraphNode startNode = (GraphNode) edge.start;
            if (startNode.getLabel().equals(node.label)) {
                GraphNode targetNode = (GraphNode) edge.target;
                if (!visitedNodesForExtractSubtreeSet.contains(targetNode.getLabel())) {
                    TreeNode child = targetNode.toTreeNode();
                    node.addChild(child);
                    extractSubtreeFromRootRecursion(child);
                }
            }
        }
    }

    public List<Edge> getEdgesOut(GraphNode node) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeSet) {
            if (e.start.equals(node))
                outgoingEdges.add(e);
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesOutWithType(GraphNode node, String edgeType) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeSet) {
            if (e.start.equals(node) && e.edgeType.equals(edgeType))
                outgoingEdges.add(e);
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesIn(GraphNode node) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeSet) {
            if (e.target.equals(node))
                incomingEdges.add(e);
        }
        return incomingEdges;
    }

    private List<Edge> getEdgesInWithType(GraphNode node, String edgeType) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeSet) {
            if (e.target.equals(node) && e.edgeType.equals(edgeType))
                incomingEdges.add(e);
        }
        return incomingEdges;
    }

    private List<Edge> getEdgesOfType(String edgeType) {
        List<Edge> relevantEdges = new LinkedList<>();
        for (Edge e : edgeSet)
            if (e.edgeType.equals(edgeType))
                relevantEdges.add(e);
        return relevantEdges;
    }

    @Override
    public String toString() {
        return "\n nodes: " + getNodes().toString() + "\n" + "edges: " + getEdges().toString();
    }

    /// needs to do something !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    Graph copy(Graph g) {
        return new Graph(g.getEdges());
    }

    LinkedList<GraphNode> getIsolatedNodes() {
        LinkedList<GraphNode> isolatedNodes = new LinkedList<>();
        for (GraphNode v : nodeSet) {
            if (getIndegree(v) == 0 && getOutdegree(v) == 0) {
                isolatedNodes.add(v);
            }
        }
        return isolatedNodes;
    }

    void deleteIsolatedNodes() {
        LinkedList<model.GraphNode> n = getIsolatedNodes();
        System.out.printf("Found %d isolated nodes: %s\n", n.size(), n.toString());
        for (model.GraphNode graphNode : n) {
            justRemoveNode(graphNode);
        }
    }

    model.GraphNode getSink() {
        for (model.GraphNode node : this.nodeSet)
            if (getOutdegree(node) == 0 && getIndegree(node) > 0) return node;
        return null;
    }
    model.GraphNode getSource() {
        for (model.GraphNode node : this.nodeSet)
            if (getIndegree(node) == 0) return node;
        return null;
    }

    void justRemoveNode(model.GraphNode removeMe) {
        /* will only remove this node */
        this.nodeSet.remove(removeMe);
    }
    int getOutdegree(GraphNode node) {
        int size = 0;
        if (nodeToEdgesOut.containsKey(node)) {
            size = nodeToEdgesOut.get(node).size();
        }
        return size;
    }
    int getIndegree(GraphNode node) {
        int size = 0;
        if (nodeToEdgesIn.containsKey(node)) {
            size = nodeToEdgesIn.get(node).size();
        }
        return size;
    }
    void removeIngoingEdges(GraphNode node) {
        edgeSet.removeAll(nodeToEdgesIn.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesIn.get(node));

        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode startNode = edgeToBeRemoved.start;
            LinkedList<Edge> modifiedListOut = nodeToEdgesOut.get(startNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error to remove edge in modifiedList");
            nodeToEdgesOut.put(startNode, modifiedListOut);
        }
        nodeToEdgesIn.keySet().removeIf(entry -> entry == node);
    }
    void removeOutgoingEdges(GraphNode node) {
        edgeSet.removeAll(nodeToEdgesOut.get(node));
        LinkedList<Edge> edgesToBeRemoved = new LinkedList<>(nodeToEdgesOut.get(node));
        for (Edge edgeToBeRemoved : edgesToBeRemoved) {
            GraphNode targetNode = edgeToBeRemoved.target;
            LinkedList<Edge> modifiedListOut = nodeToEdgesIn.get(targetNode);
            if (!modifiedListOut.remove(edgeToBeRemoved)) System.out.println("Error");
            nodeToEdgesIn.put(targetNode, modifiedListOut);
        }
        nodeToEdgesOut.keySet().removeIf(entry -> entry == node);
    }

    GraphNode getNodeWithMaxDiffDegree() {
        int max = Integer.MIN_VALUE;
        GraphNode winnerNode = null;
        for (GraphNode node : nodeSet) {
            if (getOutdegree(node) - getIndegree(node) > max) {
                max = getOutdegree(node) - getIndegree(node);
                winnerNode = node;
            }
        }
//        System.out.println("max = " + max);
        return winnerNode;
    }
    public GraphNode selectRandomNode() {
        int size = nodeSet.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (GraphNode obj : nodeSet) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }
    LinkedList<GraphNode> getAllSinks() {
        LinkedList<GraphNode> allSinks = new LinkedList<>();
        for (GraphNode node : this.nodeSet) {
            if (getOutdegree(node) == 0 && getIndegree(node) >= 1) {
                allSinks.add(node);
            }
        }
        return allSinks;
    }
    void reverseEdge(Edge edge) {
        GraphNode u = edge.start;
        GraphNode v = edge.target;

        if (VERBOSE) System.out.printf("Turning: %s to %s \n", u.getLabel(), v.getLabel());
        if (edgeSet.contains(edge)) {
            String edgeType = edge.edgeType;
            System.out.println("edgeType = " + edgeType);
            if (VERBOSE) System.out.println("Edge in EdgeSet going to be turned");
            deleteEdge(edge);
            addEdge(new Edge(v, u, edgeType));
        } else System.out.println("EDGE COULDN'T BE TURNED, WTF!");
    }

    public LinkedHashMap<Integer,LinkedList<GraphNode>> getLayerMap(){
        return this.layerMap;
    }

    void insertLayer(int layer, LinkedList<GraphNode> nodes) {
        for (GraphNode node : nodes) {
            for (GraphNode graphNode : nodeSet) {
                if (node.equals(graphNode)) {
                    graphNode.setLayer(layer);
                    nodeToLayer.put(layer, graphNode);

                    LinkedList<GraphNode> tempList = new LinkedList<>();
                    if (layerMap.containsKey(layer))
                        tempList.addAll(layerMap.get(layer));
                    tempList.add(graphNode);
                    layerMap.put(layer,tempList);
                    }
                }
            }
        }

    void addDummies() {
        LinkedList<Edge> edgesToDelete = new LinkedList<>();
        LinkedList<Edge> edgesNew = new LinkedList<>();

        for (Edge edge : edgeSet) {
            GraphNode start = edge.start;
            GraphNode target = edge.target;
            int spanningLevels = Math.abs(start.getLayer() - target.getLayer()) - 1;
            if (spanningLevels > 0) {
                edgesToDelete.add(edge);
                List<GraphNode> block = new LinkedList<>();
                block.add(start);

                // not sure if correct... --dustyn
                for (int i = spanningLevels; i > 0; i--) { // for every additional spanned level
                    String label = "Dummy-" + i + "; from: " + start.getLabel() + " to: " + target.getLabel() + "|";
                    GraphNode node = new GraphNode(label, "Dummy", true); // create a dummy with a proper label
                    node.setLayer(target.getLayer() + i);
                    nodeSet.add(node); // add to NodeSet
                    block.add(node); // add to block to find parent/children
                }
                block.add(target);

                // missing adding new edges !! --dustyn
            }
        } // end-for edge


        edgesToDelete.forEach(this::deleteEdge);
        edgesNew.forEach(this::addEdge);

    }

    private void addEdge(Edge e) {
        update_HashmapForInsertion(e);
        edgeSet.add(e);
    }

    private void deleteEdge(Edge e) {
        update_HashmapForDeletion(e);
        edgeSet.remove(e);
    }

    private void update_HashmapForInsertion(Edge edge) {
        GraphNode start = edge.start;
        GraphNode target = edge.target;

        LinkedList<Edge> edgeSetOut = new LinkedList<>();
        if (nodeToEdgesOut.containsKey(start))
            edgeSetOut.addAll(nodeToEdgesOut.get(start));
        edgeSetOut.add(edge);
        this.nodeToEdgesOut.put(start, edgeSetOut);

        LinkedList<Edge> edgeSetIn = new LinkedList<>();
        if (nodeToEdgesIn.containsKey(target))
            edgeSetIn.addAll(nodeToEdgesIn.get(target));
        edgeSetIn.add(edge);
        this.nodeToEdgesIn.put(target, edgeSetIn);
    }

    private void update_HashmapForDeletion(Edge edge) {
        nodeToEdgesIn.remove(edge.start);
        nodeToEdgesOut.remove(edge.start);

        for (GraphNode node_key : nodeToEdgesIn.keySet()) {
            LinkedList<Edge> edgeSetIn = new LinkedList<>();
            edgeSetIn.addAll(nodeToEdgesIn.get(node_key));
            edgeSetIn.remove(edge);
            nodeToEdgesIn.put(node_key, edgeSetIn);
        }
        // logik korrekt ?
        for (GraphNode node_key : nodeToEdgesOut.keySet()) {
            LinkedList<Edge> edgeSetOut = new LinkedList<>();
            edgeSetOut.addAll(nodeToEdgesOut.get(node_key));
            edgeSetOut.remove(edge);
            nodeToEdgesOut.put(node_key, edgeSetOut);
        }

    }

    public LinkedHashSet<GraphNode> getNodes() {
        return new LinkedHashSet<>(this.nodeSet);
    }

    public LinkedHashSet<Edge> getEdges() {
        return new LinkedHashSet<>(this.edgeSet);
    }

    boolean isSink(GraphNode node) {
        return !nodeToEdgesOut.containsKey(node);
    }

    LinkedList<GraphNode> getChildrenFrom(GraphNode node) {
        LinkedList<GraphNode> children = new LinkedList<>();
        if (nodeToEdgesOut.containsKey(node)) {
            for (Edge edge : nodeToEdgesOut.get(node))
                children.add(edge.target);
            return children;
        }
        return null;
    }

    LinkedList<GraphNode> getParentsOf(GraphNode node) {
        LinkedList<GraphNode> parents = new LinkedList<>();
        for (Edge edge : nodeToEdgesIn.get(node))
            parents.add(edge.start);
        return parents;
    }

    void setEdgeTypes(LinkedList<HelperTypes.EdgeType> edgeTypes) {
        this.edgeTypeList = edgeTypes;
    }


    Edge getEdgeBetween(GraphNode start, GraphNode end) {
        for (Edge edge : edgeSet)
            if (edge.start.getLabel().equals(start.getLabel()) && edge.target.getLabel().equals(end.getLabel())) {
                System.out.println("found it !!! edge = " + edge);
                return edge;
            }
        return null;
    }

}
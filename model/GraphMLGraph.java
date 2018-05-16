package model;

import model.HelperTypes.EdgeType;

import java.util.*;

public class GraphMLGraph {
    public final LinkedHashSet<Node> nodeList = new LinkedHashSet<>();
    private final LinkedHashSet<Edge> edgeList = new LinkedHashSet<>();
    private final LinkedHashSet<EdgeType> edgeTypeList = new LinkedHashSet<>();
    private LinkedHashSet<String> visitedNodesForExtractSubtreeSet = null;

    public boolean addEdgeType(String id, String attrType) {
        EdgeType et = new EdgeType(id, "double");
        if (!edgeTypeList.contains(et)) {
            edgeTypeList.add(et);
            return true;
        } else {
            System.out.println("EdgeType " + et + " already in list");
            return false;
        }
    }

    public ArrayList<EdgeType> getEdgeTypes() {
        return new ArrayList<>(edgeTypeList);
    }


    public List<String> getRelevantEdgeTypeLabels() {
        List<String> returnList = new LinkedList<>();
        for (Node node : nodeList)
            if (getEdgesIn(node).isEmpty()) for (Edge outGoingEdge : getEdgesOut(node))
                if (!returnList.contains(outGoingEdge.edgeType)) returnList.add(outGoingEdge.edgeType);
        return returnList;
    }

    public List<String> getPossibleRootLabels(String edgeType) {
        List<String> possibleRoots = new LinkedList<>();
        for (Node node : nodeList)
            if (getEdgesIn(node).isEmpty() && getEdgesOut(node).toString().contains(edgeType)) {
                Tree tree = extractSubtreeFromNode(node, edgeType);
                int depth = tree.getTreeDepth();
                int numNodes = tree.nodeList.size()-1;
                possibleRoots.add(node.label + " (" + depth + "|" + numNodes + ")");
            }
        return possibleRoots.size() > 0 ? possibleRoots : null;
    }

    void addAllNodes(ArrayList<Node> nodes) {
        nodeList.addAll(nodes);
    }

    void addAllEdges(ArrayList<Edge> edges) {
        //nodeParent.put(e.target, e.source);
        edgeList.addAll(edges);
    }

    public Tree extractSubtreeFromNode(Node root, String edgeType) { //setzt mit hilfe der Edge Liste und des gewählten Edge Types -> Parent und children für alle Noten, überschreibt bestehende Infos, damit konsekutive Auswahlen ihct interferieren
        if (root == null) {
            return null;
        }
        boolean bEdgeTypeValid = false;
        for (EdgeType et : edgeTypeList) {
            if (et.id.equals(edgeType)) {
                bEdgeTypeValid = true;
                break;
            }
        }
        if (!bEdgeTypeValid) {
            System.out.println("Error: chosen edgeType: " + edgeType + " not in edgeTypeList");
            return null;
        }
        root.parent = null;
        HashSet<Edge> temporaryEdgeSubset = new HashSet<>(); //startlabel, edge
        for (Edge edge : edgeList) { //zum schnelleren finden der Kanten des Teilbaumes
            if (edge.edgeType.equals(edgeType)) {
                temporaryEdgeSubset.add(edge);
            }
        }
        //System.out.println(temporaryEdgeSubset);
        visitedNodesForExtractSubtreeSet = new LinkedHashSet<>();
        extractSubtreeFromRootRecursion(root, temporaryEdgeSubset);
        return new Tree(root);
    }

    private void extractSubtreeFromRootRecursion(Node node, Set<Edge> tset) {
//        System.out.println("recursion at node:" + node);
        node.resetChildren();
        visitedNodesForExtractSubtreeSet.add(node.label);
        for (Edge edge : tset) {
            if (edge.start.equals(node)) {
                if (!visitedNodesForExtractSubtreeSet.contains(edge.target.label)) {
                    node.addChild(edge.target);
                    extractSubtreeFromRootRecursion(edge.target, tset);
                } else {
//                    System.out.println("Cycle found, I wont search any further.");
                }
            }
        }
    }

    public List<Node> getRoots() {
        List<Node> roots = new LinkedList<>();
        for (Node node : nodeList) {
//            System.out.println(node.label);
            if (getEdgesIn(node).isEmpty()) {
                roots.add(node);
            }
        }
        return roots;
    }

    public Node labelToNode(String label) {
        // here we need to stripOf the (#TreeDepth)
        String[] tmp = label.split(" \\(");
        label = tmp[0];
        System.out.println("tmp = " + tmp);
        System.out.println("label = " + label);
        Node particularNode = null;
        for (Node node : nodeList) {
            if (node.label.equals(label)) particularNode = node;
        }
        if (particularNode != null) return particularNode;
        else System.out.println("somehow could'nt find node for label: " + label);

        return null;
    }


    public List<Node> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    public List<Edge> getEdgesOut(Node node) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node)) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesOutWithType(Node node, String edgeType) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node) && e.edgeType.equals(edgeType)) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesIn(Node node) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.target.equals(node)) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public List<Edge> getEdgesInWithType(Node node, String edgeType) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.target.equals(node) && e.edgeType.equals(edgeType)) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        dumpNodeStructure(builder, null, "- ");
        return builder.toString();
    }

    private void dumpNodeStructure(StringBuilder builder, Node node, String prefix) {
        if (node != null) {
            builder.append(prefix);
            builder.append(node.toString());
            builder.append('\n');
            prefix = "    " + prefix;
        }
        for (Node child : node.getChildren()) {
            dumpNodeStructure(builder, child, prefix);
        }
    }
}

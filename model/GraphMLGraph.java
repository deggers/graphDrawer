package model;

import model.HelperTypes.*;

import java.util.*;

public class GraphMLGraph {
    private final LinkedHashSet<protoNode> nodeList = new LinkedHashSet<>();
    private final LinkedHashSet<Edge> edgeList = new LinkedHashSet<>();
    private final LinkedHashSet<EdgeType> edgeTypeList = new LinkedHashSet<>();
    private LinkedHashSet<String> visitedNodesForExtractSubtreeSet = null;
    private Set<Edge> temporaryEdgeSubset = null;

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
        for (protoNode node : nodeList)
            if (getEdgesIn(node).isEmpty()) for (Edge outGoingEdge : getEdgesOut(node))
                if (!returnList.contains(outGoingEdge.edgeType)) returnList.add(outGoingEdge.edgeType);
        return returnList;
    }

    public List<Tree> getPossibleRootLabels(String edgeType) {
        List<Tree> possibleRoots = new LinkedList<>();
        for (protoNode node : nodeList)
            if (getEdgesIn(node).isEmpty() && getEdgesOut(node).toString().contains(edgeType)) {
                Tree tree = extractSubtreeFromProtoNode(node, edgeType);
                possibleRoots.add(tree);
            }
        return possibleRoots.size() > 0 ? possibleRoots : null;
    }

    void addAllNodes(ArrayList<protoNode> nodes) {
        nodeList.addAll(nodes);
    }

    void addAllEdges(ArrayList<Edge> edges) {
        edgeList.addAll(edges);
    }

    public Tree extractSubtreeFromProtoNode(protoNode root, String edgeType) { //setzt mit hilfe der Edge Liste und des gewählten Edge Types -> Parent und children für alle Noten, überschreibt bestehende Infos, damit konsekutive Auswahlen nicht interferieren
        if (root == null) {
            System.out.println("eh, das war ja null!");
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
        Node rootnode = root.toNode();
        rootnode.parent = null;
        temporaryEdgeSubset = new HashSet<>(); //startlabel, edge
        for (Edge edge : edgeList) { //zum schnelleren finden der Kanten des Teilbaumes
            if (edge.edgeType.equals(edgeType)) {
                temporaryEdgeSubset.add(edge);
            }
        }
        //System.out.println(temporaryEdgeSubset);
        // reset the temporary sets for computing the subtree
        visitedNodesForExtractSubtreeSet = new LinkedHashSet<>();
        String treeName = root.getLabel();
        extractSubtreeFromRootRecursion(rootnode, treeName);
        temporaryEdgeSubset = null;
        visitedNodesForExtractSubtreeSet = null;
        return new Tree(rootnode);
    }

    private void extractSubtreeFromRootRecursion(Node node, String treeName) {
//        System.out.println("recursion at node:" + node);
        node.associatedTree = treeName;
        visitedNodesForExtractSubtreeSet.add(node.label);
        
        for (Edge edge : temporaryEdgeSubset) {
            if (edge.start.getLabel().equals(node.label)) {
                if (!visitedNodesForExtractSubtreeSet.contains(edge.target.getLabel())) {
                    Node child = edge.target.toNode();
                    node.addChild(child);
                    extractSubtreeFromRootRecursion(child, treeName);
                } else {
//                    System.out.println("Cycle found, I wont search any further.");
                }
            }
        }
    }

    public List<protoNode> getRoots() {
        List<protoNode> roots = new LinkedList<>();
        for (protoNode node : nodeList) {
//            System.out.println(node.label);
            if (getEdgesIn(node).isEmpty()) {
                roots.add(node);
            }
        }
        return roots;
    }

    public protoNode labelToProtoNode(String label) {
        protoNode particularNode = null;
        for (protoNode node : nodeList) {
            if (node.getLabel().equals(label)) particularNode = node;
        }
        if (particularNode != null) return particularNode;
        else System.out.println("somehow could'nt find node for label: " + label);

        return null;
    }


    public List<protoNode> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    public List<Edge> getEdgesOut(protoNode node) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node)) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesOutWithType(protoNode node, String edgeType) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node) && e.edgeType.equals(edgeType)) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesIn(protoNode node) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.target.equals(node)) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public List<Edge> getEdgesInWithType(protoNode node, String edgeType) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.target.equals(node) && e.edgeType.equals(edgeType)) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }


//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        dumpNodeStructure(builder, null, "- ");
//        return builder.toString();
//    }
//
//    private void dumpNodeStructure(StringBuilder builder, Node node, String prefix) {
//        if (node != null) {
//            builder.append(prefix);
//            builder.append(node.toString());
//            builder.append('\n');
//            prefix = "    " + prefix;
//        }
//        for (Node child : node.getChildren()) {
//            dumpNodeStructure(builder, child, prefix);
//        }
//    }
}

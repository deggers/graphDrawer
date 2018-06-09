package model;

import model.HelperTypes.EdgeType;
import model.HelperTypes.ProtoNode;

import java.util.*;

public class GraphMLGraph {
    private final LinkedHashSet<ProtoNode> nodeList = new LinkedHashSet<>();
    private final LinkedHashSet<Edge> edgeList = new LinkedHashSet<>();
    private final LinkedHashSet<EdgeType> edgeTypeList = new LinkedHashSet<>();
    private LinkedHashSet<String> visitedNodesForExtractSubtreeSet = null;
    private HashSet<Edge> temporaryEdgeSubset = null;

    public boolean addEdgeType(String id, String attrType) {
        EdgeType et = new EdgeType(id, attrType);
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

    public List<String> getPossibleRootLabels(String edgeType) {
        List<String> possibleRoots = new LinkedList<>();
        boolean bTest;
        for (ProtoNode node : nodeList) {
            if (edgeType.equals("package")) {
                bTest = (!getEdgesOutWithType(node, edgeType).isEmpty() && node.getGraphMLType().equals("package"));
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

    void addAllNodes(ArrayList<ProtoNode> nodes) {
        nodeList.addAll(nodes);
    }

    void addAllEdges(ArrayList<Edge> edges) {
        edgeList.addAll(edges);
    }

    public Tree extractSubtreeFromProtoNode(ProtoNode root, String edgeType) { //setzt mit hilfe der Edge Liste und des gewählten Edge Types -> Parent und children für alle Noten, überschreibt bestehende Infos, damit konsekutive Auswahlen nicht interferieren
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
        TreeNode rootnode = root.toTreeNode();
        rootnode.parent = null;
        temporaryEdgeSubset = new HashSet<>(); //startlabel, edge
        for (Edge edge : edgeList) { //zum schnelleren finden der Kanten des Teilbaumes
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
            ProtoNode startNode = (ProtoNode) edge.start;
            if (startNode.getLabel().equals(node.label)) {
                ProtoNode targetNode = (ProtoNode) edge.target;
                if (!visitedNodesForExtractSubtreeSet.contains(targetNode.getLabel())) {
                    TreeNode child = targetNode.toTreeNode();
                    node.addChild(child);
                    extractSubtreeFromRootRecursion(child);
                }
            }
        }
    }

    public List<ProtoNode> getRoots() {
        List<ProtoNode> roots = new LinkedList<>();
        for (ProtoNode node : nodeList) {
//            System.out.println(node.label);
            if (getEdgesIn(node).isEmpty()) {
                roots.add(node);
            }
        }
        return roots;
    }

    public ProtoNode labelToProtoNode(String label) {
        // here we need to stripOf the (#TreeDepth)
        label = label.split(" \\(")[0];
        ProtoNode particularNode = null;
        for (ProtoNode node : nodeList) {
            if (node.getLabel().equals(label)) particularNode = node;
        }
        if (particularNode != null) return particularNode;
        else System.out.println("somehow could'nt find node for label: " + label);

        return null;
    }


    public List<ProtoNode> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    public List<Edge> getEdgesOut(ProtoNode node) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node)) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesOutWithType(ProtoNode node, String edgeType) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node) && e.edgeType.equals(edgeType)) {
                outgoingEdges.add(e);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getEdgesIn(ProtoNode node) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.target.equals(node)) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public List<Edge> getEdgesInWithType(ProtoNode node, String edgeType) {
        List<Edge> incomingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.target.equals(node) && e.edgeType.equals(edgeType)) {
                incomingEdges.add(e);
            }
        }
        return incomingEdges;
    }

    public List<Edge> getEdgesOfType(String edgeType) {
        List<Edge> relevantEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.edgeType.equals(edgeType)) {
                System.out.println("e = " + e);
                relevantEdges.add(e);
            }
        }
        return relevantEdges;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        dumpNodeStructure(builder, null, "- ");
        return builder.toString();
    }
    private void dumpNodeStructure(StringBuilder builder, TreeNode node, String prefix) {
        if (node != null) {
            builder.append(prefix);
            builder.append(node.toString());
            builder.append('\n');
            prefix = "    " + prefix;
        }
        for (TreeNode child : node.getChildren()) {
            dumpNodeStructure(builder, child, prefix);
        }
    }
}
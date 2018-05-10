package model;

import java.util.*;
import model.HelperTypes.EdgeType;
import model.Node;

public class GraphMLGraph{
    public final LinkedHashSet<Node> nodeList = new LinkedHashSet<>();
    private final LinkedHashSet<Edge> edgeList = new LinkedHashSet<>();
    private final LinkedHashSet<EdgeType> edgeTypeList = new LinkedHashSet<>();
    private LinkedHashSet<String> visitedNodesForExtractSubtreeSet = null;
    
    public boolean addEdgeType(String id, String attrType){
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

    public List<String> getEdgeTypeLabels() {
        List<String> returnList = new LinkedList<>();
        for (EdgeType edgeType : edgeTypeList){
            returnList.add(edgeType.getId());
        }
        return returnList;
    }

    public List<String> getRelevantEdgeTypeLabels() {
        List<String> returnList = new LinkedList<>();
        for (EdgeType edgeType : edgeTypeList){
            for (Node node : nodeList) {
                if (getEdgesIn(node).isEmpty()) {
                    for (Edge outGoingEdges : getEdgesOut(node)) {
//                        System.out.println("outGoingEdges = " + outGoingEdges.edgeType);
//                        System.out.println("edgeType.toString() = " + edgeType.getId());
                        if (outGoingEdges.edgeType.equals(edgeType.getId())) {
                            if (!returnList.contains(edgeType.getId())) {
                                returnList.add(edgeType.getId());
                            }
                            break;
                        }
                    }
                }
            }
        }
        return returnList;
    }


    public List<String> getLabelsFromRoots(String selectedEdgeType) {
        List<String> roots = new LinkedList<>();
        for (Node node : nodeList) {
            if (getEdgesInWithType(node, selectedEdgeType).isEmpty()) {
                for (Edge outGoingEdges: getEdgesOutWithType(node, selectedEdgeType))
                    if (true) {
                        roots.add(node.label);
                        break;
                    }
            }
        }
        if (roots.size() == 0) {
            roots.add("empty");
        }
        return roots;
    }
    
    void addAllNodes(ArrayList<Node> nodes) {
        nodes.forEach(n -> {
            nodeList.add(n);
        });
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
        if (!bEdgeTypeValid){ 
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
    
    private void extractSubtreeFromRootRecursion(Node node, Set<Edge> tset){
//        System.out.println("recursion at node:" + node);
        node.resetChildren();
        visitedNodesForExtractSubtreeSet.add(node.label);
        for (Edge edge : tset) {
            if (edge.start.equals(node)) {
                if (!visitedNodesForExtractSubtreeSet.contains(edge.target.label)) {
                    node.addChild(edge.target);
                    extractSubtreeFromRootRecursion(edge.target, tset);
                } else {
                    System.out.println("Cycle found, I wont search any further.");
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

    public Node labelToNode(String label){
        Node particularNode = null;
        for (Node node : nodeList){
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
            if (e.start.equals(node)&&e.edgeType.equals(edgeType)) {
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
            if (e.target.equals(node)&&e.edgeType.equals(edgeType)) {
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

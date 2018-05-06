package model;

import java.util.*;
import model.HelperTypes.EdgeType;
import model.Node;
// inspired heavily from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class GraphMLGraph extends Tree{
    public final LinkedHashSet<Node> nodeList = new LinkedHashSet<>();
    private final HashSet<Edge> edgeList = new HashSet<>();
    private final HashSet<EdgeType> EdgeTypeList = new HashSet<>();
    
    public boolean addEdgeType(String id, String attrType){
        EdgeType et = new EdgeType(id, "double");
        if (!EdgeTypeList.contains(et)) {
            EdgeTypeList.add(et);
            return true;
        } else {
            System.out.println("EdgeType " + et + " already in list");
            return false;
        } 
    }
    public boolean addEdgeType(String id){
        return addEdgeType(id, "none");
    }
    public ArrayList<EdgeType> getEdgeTypes() {
        return new ArrayList<>(EdgeTypeList);
    }
    
    void addAllNodes(ArrayList<Node> nodes) {
        nodes.forEach(n -> {
            nodeList.add(n);
        });
    }

    void addAllEdges(ArrayList<Edge> edges) {
        edges.forEach(e -> {
            edgeList.add(e);
        });
    }

    void finalizeGraphFromParser() {
        
    }

    //coppied from MappedTreeStructure for now
    /*@Override
    public boolean add(Node parent, Node node) {
        checkNotNull(parent, "parent");
        checkNotNull(node, "node");

        // check for cycles
        Node current = parent;
        do {
            if (node.equals(current)) {
                throw new IllegalArgumentException(" node must not be the same or an ancestor of the parent");
            }
        } while ((current = getParent(current)) != null);

        boolean added = nodeList.add(node);
        nodeList.add(parent);
        edgeList.add(new Edge(parent, node));
        return added;
    }
    
    private void checkNotNull(Node node, String parameterName) {
        if (node == null)
            throw new IllegalArgumentException(parameterName + " must not be null");
    }

    @Override
    public boolean remove(Node node, boolean cascade) { //nötig bei uns?

        checkNotNull(node, "node");

        if (!nodeList.contains(node)) {
            return false;
        }
        if (cascade) {
            getChildren(node).forEach((child) -> {
                remove(child, true);                    //löscht keine Einträge aus nodeParent!
            });
        } else {
            getChildren(node).forEach((child) -> {
                nodeParent.remove(child);
            });
        }
        nodeList.remove(node);
        return true;
    }*/

    public List<Node> getRoots() {
        List<Node> roots = new LinkedList<>();
        for (Node node : nodeList) {
            if (!getEdgesIn(node).isEmpty()) {
                roots.add(node);
            }
        }
        return roots;
    }

    public List<Node> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    /*@Override
    public Node getParent(Node node) {
        checkNotNull(node, "node");
        return nodeParent.get(node);
    }*/

    //@Override
    public List<Edge> getEdgesOut(Node node) {
        List<Edge> outgoingEdges = new LinkedList<>();
        for (Edge e : edgeList) {
            if (e.start.equals(node)) {
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
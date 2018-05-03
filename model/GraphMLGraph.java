package model;

import java.util.*;
import model.HelperTypes.EdgeType;
import model.Node;
// inspired heavily from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class GraphMLGraph implements MutableTree<Node> {
    public final Map<Node, Node> nodeParent = new HashMap<>();
    public final LinkedHashSet<Node> nodeList = new LinkedHashSet<>();
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
    
    //coppied from MappedTreeStructure for now
    @Override
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
        nodeParent.put(node, parent);
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
    }

    @Override
    public List<Node> getRoots() {
        return getChildren(null);
    }

    public List<Node> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    @Override
    public Node getParent(Node node) {
        checkNotNull(node, "node");
        return nodeParent.get(node);
    }

    @Override
    public List<Node> getChildren(Node node) {
        List<Node> children = new LinkedList<>();
        for (Node n : nodeList) {
            Node parent = nodeParent.get(n);
            if (node == null && parent == null) {
                children.add(n);
            } else if (node != null && parent != null && parent.equals(node)) { //parent != 0 redundant für node != 0 && parent.equals(node) ???
                children.add(n);
            }
        }
        return children;
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
        for (Node child : getChildren(node)) {
            dumpNodeStructure(builder, child, prefix);
        }
    }
}
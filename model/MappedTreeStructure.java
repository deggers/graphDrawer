package model;

import java.util.*;

// inspired heavely from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class MappedTreeStructure<T> implements MutableTree<T> {
    private final Map<T, T> nodeParent = new HashMap<>();
    private final LinkedHashSet<T> nodeList = new LinkedHashSet<>();

    public MappedTreeStructure(T root) {
        fillTree((Node) root);
    }

    private void fillTree(Node node) {
        T e = (T) node;
        nodeList.add(e);
        //System.out.println("added: " + node.label);
        try {
            int indexAsChildSetter = 0;
            for (Node child : node.getChildren()) {
                child.parent = node;
                child.indexAsChild = indexAsChildSetter;
                indexAsChildSetter++;
                nodeParent.put( (T) child, e);
                //System.out.println("added pair (n/p): " + child + e);
                fillTree(child);
            }
        } catch (Exception ex) {
        }
    }

    private void checkNotNull(T node, String parameterName) {
        if (node == null)
            throw new IllegalArgumentException(parameterName + " must not be null");
    }

    @Override
    public boolean add(T parent, T node) {
        checkNotNull(parent, "parent");
        checkNotNull(node, "node");

        // check for cycles
        T current = parent;
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

    @Override

    public boolean remove(T node, boolean cascade) { //nötig bei uns?

        checkNotNull(node, "node");

        if (!nodeList.contains(node)) {
            return false;
        }
        if (cascade) {
            getChildren(node).forEach((child) -> {
                remove(child, true);					//löscht keine Einträge aus nodeParent!
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
    public List<T> getRoots() {
        return getChildren(null);
    }

    public List<T> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    @Override
    public T getParent(T node) {
        checkNotNull(node, "node");
        return nodeParent.get(node);
    }

    @Override
    public List<T> getChildren(T node) {
        List<T> children = new LinkedList<>();
        for (T n : nodeList) {
            T parent = nodeParent.get(n);
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

    private void dumpNodeStructure(StringBuilder builder, T node, String prefix) {
        if (node != null) {
            builder.append(prefix);
            builder.append(node.toString());
            builder.append('\n');
            prefix = "    " + prefix;
        }
        for (T child : getChildren(node)) {
            dumpNodeStructure(builder, child, prefix);
        }
    }

    public String echoContent() {
        return nodeList.toString() + "\n" + nodeParent.toString();
    }
}
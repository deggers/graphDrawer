package model;

import java.util.*;
// inspired heavely from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class MappedTreeStructure implements MutableTree<Node> {
    public final Map<Node, Node> nodeParent = new HashMap<>();
    public final LinkedHashSet<Node> nodeList = new LinkedHashSet<>();
    private boolean yValuesHasBeenSet = false;

    public MappedTreeStructure(Node root) {
        nodeList.add(root);
    }

    public LinkedList<Node> getNodesFromLevel(int level) {
        LinkedList<Node> levelList = new LinkedList<>();
        if (!this.yValuesHasBeenSet) getYCoords(getRoot(), 0);
        for (Node node : nodeList)
            if (node.y == level) {
                levelList.add(node);
                System.out.println("node.y = " + node.y + " , level: " + level);
                System.out.println("levelList = " + levelList);
            }
        return levelList;
    }

    private void getYCoords(Node node, int level) {
        node.y = level;
        for (Node child : node.getChildren())
            getYCoords(child, level + 1);
        this.yValuesHasBeenSet = true;
    }


    private void fillTree(Node node) {
        nodeList.add(node);
        //System.out.println("added: " + node.label);
        try {
            int indexAsChildSetter = 0;
            for (Node child : node.getChildren()) {
                child.parent = node;
                child.indexAsChild = indexAsChildSetter;
                indexAsChildSetter++;
                nodeParent.put(child, node);
                //System.out.println("added pair (n/p): " + child + e);
                fillTree(child);
            }
        } catch (Exception ex) {
        }
    }

    private void checkNotNull(Node node, String parameterName) {
        if (node == null)
            throw new IllegalArgumentException(parameterName + " must not be null");
    }


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

    public Node getRoot() {
        return nodeList.iterator().next();
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

    public String echoContent() {
        return nodeList.toString() + "\n" + nodeParent.toString();
    }
}
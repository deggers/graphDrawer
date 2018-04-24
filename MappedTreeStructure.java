import java.io.Serializable;
import java.util.*;

// copied from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class MappedTreeStructure<Node> implements MutableTree<Node> {
    private final Map<Node, Node> nodeParent = new HashMap<Node, Node>();
    private final LinkedHashSet<Node> nodeList = new LinkedHashSet<Node>();


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
//        System.out.println("added = " + added);
        nodeList.add(parent);
        nodeParent.put(node, parent);
        return added;
    }

    @Override
    public boolean remove(Node node, boolean cascade) {
        checkNotNull(node, "node");

        if (!nodeList.contains(node)) {
            return false;
        }
        if (cascade) {
            for (Node child : getChildren(node)) {
                remove(child, true);
            }
        } else {
            for (Node child : getChildren(node)) {
                nodeParent.remove(child);
            }
        }
        nodeList.remove(node);
        return true;
    }

    @Override
    public List<Node> getRoots() {
        return getChildren(null);
    }

    @Override
    public Node getParent(Node node) {
        checkNotNull(node, "node");
        return nodeParent.get(node);
    }

    @Override
    public List<Node> getChildren(Node node) {
        List<Node> children = new LinkedList<Node>();
        for (Node n : nodeList) {
            Node parent = nodeParent.get(n);
            if (node == null && parent == null) {
                children.add(n);
            } else if (node != null && parent != null && parent.equals(node)) {
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
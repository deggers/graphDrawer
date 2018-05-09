package model;

import java.util.*;
// inspired heavely from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class Tree {

    public final LinkedHashSet<Node> nodeList = new LinkedHashSet<>();
    boolean yValuesHasBeenSet = false;
    private int treeDepth = 0;

    public Tree(){
    }

    public Tree(Node root) {
        nodeList.add(root); // set root-node
        fillTree(root);
        setLevels(root, 0); // set all level-values
    }

    public LinkedList<Node> getNodesFromLevel(int level) {
        LinkedList<Node> levelList = new LinkedList<>();
        if (!this.yValuesHasBeenSet) setLevels(getRoot(), 0); // eig unnötig, aber failsave -dustyn
        for (Node node : nodeList)
            if (node.level == level) {
                levelList.add(node);
                System.out.println("node.y = " + node.y + " , level: " + level);
                System.out.println("levelList = " + levelList);
            }
        return levelList;
    }

    private void setLevels(Node node, int level) {
        node.level = level;
        if (level > this.treeDepth) this.treeDepth = level;
        for (Node child : node.getChildren())
            setLevels(child, level + 1);
        this.yValuesHasBeenSet = true;
    }


    private void fillTree(Node node) {
        nodeList.add(node);
        //System.out.println("added: " + node.label);
        try {
            int indexAsChildSetter = 0;
            for (Node child : node.getChildren()) {
                child.parent = node;
                //nodeParent.put(child, node);
                child.indexAsChild = indexAsChildSetter;
                indexAsChildSetter++;
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

    public boolean remove(Node node, boolean cascade) { //nötig bei uns?

        checkNotNull(node, "node");

        if (!nodeList.contains(node)) {
            return false;
        }
        if (cascade) {
            node.getChildren().forEach((child) -> {
                remove(child, true);                    //löscht keine Einträge aus nodeParent!
            });
        } else {
            node.getChildren().forEach((child) -> {
                //nodeParent.remove(child);            // hier löscht er doch nodeParent ?! -dustyn
            });
        }
        nodeList.remove(node);
        return true;
    }


    public List<Node> getRoots() {
        List<Node> roots = new LinkedList<>();
        for (Node n : nodeList) {
            if (n.parent == null) {
                roots.add(n);
            }
        }
        return roots;
    }

    public Node getRoot() {
        return nodeList.iterator().next();
    }

    public List<Node> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
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

    public String echoContent() {
        return nodeList.toString(); //+ "\n" + nodeParent.toString();
    }

    public int getTreeDepth() {
        return this.treeDepth;
    }

    public boolean setNodeCoords(Node node, int x, int y) {
        boolean found = false;
        for (Node lookupNode : nodeList)
            if (lookupNode.equals(node)) {
//                System.out.println("found matching node! look.");
//                System.out.println(nodeTmp);
//                System.out.println("with");
//                System.out.println(node);
                node.x = x;
                node.y = y;
                found = true;
//                System.out.println("Coords now: ");
//                System.out.println(node.x + ", " + node.y);
//                System.out.println("");
            }
        if (!found) System.out.println("setNodeX failed");
        return found;
    }

    public int getLeavesOfNode(Node node) {
        int innerCounter = 0;
        if (!node.isLeaf()) {
            for (Node rec : node.getChildren()) {
                if (rec.isLeaf()) {
                    innerCounter++;
                } else {
                    innerCounter += getLeavesOfNode(rec);
                }
            }
            return innerCounter;
        } else {
            return 1;
        }
    }

}


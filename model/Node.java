package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    public String label;
    private final int id = 0;
    public boolean checked, onlyChild, hasThread;
    public Node leftChild, rightChild, threadTo;
    public Double xtemp;
    double weight;

    //walker
    public double x = 0;
    public double y = 0;
    public double prelim = 0;
    public double modifier = 0;
    public Node parent;
    public Node leftNeighbor = null;
    private final List<Node> children = new ArrayList<>();
    public int indexAsChild = 0;
    public Node ancestor = null;
    public double change = 0;
    public double shift = 0;
    public Node thread = null;
    public static List<Integer> depthList = new ArrayList<>();// muss resetted werden


    public Node(String label) {
        this.label = label;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getChild(int index) {
        return index >= 0 ? children.get(index) : children.get(children.size() + index);
    }

    public static int getTreeDepth(Node root) {
        getListOfDepths(root, 0);
        int max = 0;
        for (Integer count : depthList){
            if (count > max) max = count;
        }
        return max;
    }

    public static int getListOfDepths(Node node, int count) {
        if (node == null) return 0;
        for (Node child : node.getChildren()){
            depthList.add(++count);
            getListOfDepths(child,count);
        }
        return 0;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean hasLeftSibling() {
        return indexAsChild >= 1;
    }

    @Override
    public String toString() {
        String out = "id: " + label + ", x:" + x + ", y:" + y + ", prelim:" + prelim + ", modifier:" + modifier + ", indexAsChild:" + indexAsChild + ";";
        for (Node c : children) {
            out = out + "\t" + c.toString();
        }
        return out;
    }


}

package model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode extends AbstractNode {
    public double weight=0;
    public static List<Integer> depthList = new ArrayList<>();    
    //walker
    // muss resetted werden
    public double prelim = 0;
    public double modifier = 0;
    private final List<TreeNode> children = new ArrayList<>();
    public TreeNode parent = null;
    public TreeNode leftNeighbor = null;
    public int indexAsChild = 0;
    public TreeNode ancestor = null;
    public double change = 0;
    public double shift = 0;
    public TreeNode thread = null;

    // RT
    public boolean checked=false, hasThread=false;
    public TreeNode leftChild, rightChild;
    public double offset=1;


    public TreeNode(String label) {
        super(label);
    }

    public List<TreeNode> getChildren() {
        return children;
    }
    
    public void addChild(TreeNode node){
        children.add(node);
    }
    
    public void resetChildren(){
        children.clear();
    }

    public TreeNode getChild(int index) {
        return index >= 0 ? children.get(index) : children.get(children.size() + index);
    }

    public static int getTreeDepth(TreeNode root) {
        getListOfDepths(root, 0);
        int max = 0;
        for (Integer count : depthList) {
            if (count > max) max = count;
        }
        return max;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean hasLeftSibling() {
        return indexAsChild >= 1;
    }

    @Override
    public String toString() {
        return  "id: " + label + ", x:" + x + ", y:" + y + ", prelim: " + prelim + ", modifier: " + modifier + ", parent: " + ", ancestor: "   + ";";
    }

    public String toStringWithAllChildren() {
        String out = "id: " + label + ", x:" + x + ", y:" + y + ", prelim:" + prelim + ", modifier:" + modifier + ", indexAsChild:" + indexAsChild + ";";
        for (TreeNode c : children) {
            out = out + "\t" + c.toString();
        }
        return out;
    }

    public static int getListOfDepths(TreeNode node, int count) {
        if (node == null) return 0;
        for (TreeNode child : node.getChildren()) {
            depthList.add(++count);
            getListOfDepths(child, count);
        }
        return 0;
    }


    public static int getDepth(TreeNode node) {
        int depth = 0;
        while (node.parent != null) {
            depth++;
        }
        return depth;
    }
}

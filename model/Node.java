
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node implements Serializable {
    
    public String associatedTree;
    public String label;
    public double weight=0;
    public int level= 0;
    String GraphMLType;
    public static List<Integer> depthList = new ArrayList<>();// muss resetted werden
    
    //walker
    public double x = 0;
    public double y = 0;
    public double prelim = 0;
    public double modifier = 0;
    public Node parent = null;
    public Node leftNeighbor = null;
    private final List<Node> children = new ArrayList<>();
    public int indexAsChild = 0;
    public Node ancestor = null;
    public double change = 0;
    public double shift = 0;
    public Node thread = null;

    // RT
    public boolean checked=false, hasThread=false;
    public Node leftChild, rightChild;
    public double offset=1;


    public Node(String label) {
        this.label = label;
        associatedTree = "default";
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public List<Node> getChildren() {
        return children;
    }
    
    public void resetChildren(){
        children.clear();
    }

    public Node getChild(int index) {
        return index >= 0 ? children.get(index) : children.get(children.size() + index);
    }

    public static int getTreeDepth(Node root) {
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
//        return  "id: " + label + ", x:" + x + ", y:" + y + ", prelim: " + prelim + ", modifier: " + modifier + ", parent: " + ", ancestor: "   + ";";
    return "id: " + label + ", level: " + level + ", parent: " + parent;
    }

    public String toStringWithAllChildren() {
        String out = "id: " + label + ", x:" + x + ", y:" + y + ", prelim:" + prelim + ", modifier:" + modifier + ", indexAsChild:" + indexAsChild + ";";
        for (Node c : children) {
            out = out + "\t" + c.toString();
        }
        return out;
    }

    public static int getListOfDepths(Node node, int count) {
        if (node == null) return 0;
        for (Node child : node.getChildren()) {
            depthList.add(++count);
            getListOfDepths(child, count);
        }
        return 0;
    }


    public static int getDepth(Node node) {
        int depth = 0;
        while (node.parent != null) {
            depth++;
        }
        return depth;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.associatedTree);
        hash = 53 * hash + Objects.hashCode(this.label);
        hash = 53 * hash + Objects.hashCode(this.GraphMLType);
        return hash;
    }
    
    public boolean equalsIgnoreCoordinates(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        if (!Objects.equals(this.associatedTree, other.associatedTree)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.GraphMLType, other.GraphMLType)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        if (this.level != other.level) {
            return false;
        }
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.prelim) != Double.doubleToLongBits(other.prelim)) {
            return false;
        }
        if (Double.doubleToLongBits(this.modifier) != Double.doubleToLongBits(other.modifier)) {
            return false;
        }
        if (this.indexAsChild != other.indexAsChild) {
            return false;
        }
        if (Double.doubleToLongBits(this.change) != Double.doubleToLongBits(other.change)) {
            return false;
        }
        if (Double.doubleToLongBits(this.shift) != Double.doubleToLongBits(other.shift)) {
            return false;
        }
        if (this.checked != other.checked) {
            return false;
        }
        if (this.hasThread != other.hasThread) {
            return false;
        }
        if (Double.doubleToLongBits(this.offset) != Double.doubleToLongBits(other.offset)) {
            return false;
        }
        if (!Objects.equals(this.associatedTree, other.associatedTree)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.GraphMLType, other.GraphMLType)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.leftNeighbor, other.leftNeighbor)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        if (!Objects.equals(this.ancestor, other.ancestor)) {
            return false;
        }
        if (!Objects.equals(this.thread, other.thread)) {
            return false;
        }
        if (!Objects.equals(this.leftChild, other.leftChild)) {
            return false;
        }
        if (!Objects.equals(this.rightChild, other.rightChild)) {
            return false;
        }
        return true;
    }
}

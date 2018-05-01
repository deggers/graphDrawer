import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    public String label;
    private final int id = 0;
    double weight=0;
    List<Integer> depthList = new ArrayList<>();

    //reingold
    public boolean checked= false, onlyChild= false, hasThread= false;
    public Node rightChild= null, leftChild= null, threadTo= null;
    public double xtemp;
    
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
    
    public void addChild(Node node) {
        children.add(node);
    }
    
    public List<Node> getChildren() {
        return children;
    }
    
    public Node getChild(int index) {
        if (index >= 0) {
            return children.get(index);
        } else {
            return children.get(children.size()+index);
        }
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public boolean hasLeftSibling() {
        return indexAsChild >= 1;
    }
    
    @Override
    public String toString() {
        String out = label + "(x:" + x + ", y:" + y + ", prelim:" + prelim + ", modifier:" + modifier+ ", indexAsChild:" + indexAsChild + ")" + "\n";
        for (Node c : children) {
            out = out + "\t" + c.toString();
        }
        return out;
    }
    
    public Node(String label) {
        this.label = label;
    }

    int findMaxLevel(Node root, int count) {
        if (root == null) return 0;
        for (Node n : root.getChildren()) {
            findMaxLevel(n,++count);
            depthList.add(count);
        }
        return 0;
    }

    public Integer getMaxDepth(Node root) {
        findMaxLevel(root,0);
        int max=0;
        for (int i: depthList){
            if(i>max){
                max= i;
            }
        }
        return max;
    }

}



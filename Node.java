import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    public String label;
    public double x = 0;
    public double y = 0;
    public int id = 0;
    public boolean thread = false;
    public int offset = 0;
    public Node parent;
    public List<Node> children = new ArrayList<>();
    public Node leftChild;
    public Node rightChild;
    public double weight=0;  // len of edge to parent

    public void addChild(Node node) {
        children.add(node);
    }

//    public void addChild(Node node, boolean left){
//        children.add(node);
//        if (left) {
//            leftChild = node;
//        } else {
//            rightChild = node;
//        }
//    }

    @Override
    public String toString() {
        String out = label + "(" + x + ", " + y + ")" + "\n";
        for (Node c : children) {
            out += c.toString();
        }
        return out;
    }
}

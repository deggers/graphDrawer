import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    public String label;
    public int x = 0, y= 0;
    double xtemp=0;
    public int id = 0, offset= 0;
    public double weight = 0;  // len of edge to parent
    public boolean thread = false, checked= false, isLeaf=false;
    public Node parent=null, leftChild=null, rightChild =null ;
    public List<Node> children = new ArrayList<>();


    public void addChild(Node node) {
        children.add(node);
    }

    @Override
    public String toString() {
        String out = label + "(" + x + ", " + y + ")" + "\n";
        for (Node c : children) {
            out += c.toString();
        }
        return out;
    }

}

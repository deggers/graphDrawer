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

    public void addChild(Node node){
        children.add(node);
    }

    @Override
    public String toString() {
        String out = label + "\n";
        for (Node c : children) {
            out += c.toString();
        }
        return out;
    }
}

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    public String label;
    private final int id = 0;
    //private boolean thread = false;
    
    //walker
    public double x = 0;
    public double y = 0;
    public double prelim = 0;
    public double modifier = 0;
    public Node parent;
    public Node leftNeighbor = null;
    private final List<Node> children = new ArrayList<>();
    public int indexAsChild = 0;
        
    public void addChild(Node node) {
        children.add(node);
    }
    
    public List<Node> getChildren() {
        return children;
    }
    
    public Node getChild(int index) {
        return children.get(index);
    }
    
    @Override
    public String toString() {
        String out = label + "(" + x + ", " + y + ")" + "\n";
        for (Node c : children) {
            out = out + "\t" + c.toString();
        }
        return out;
    }
    
    public Node(String label) {
        this.label = label;
    }
}

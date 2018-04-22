import java.io.Serializable;
import java.util.List;

interface Tree <Node extends Serializable> extends Serializable {
    public List<Node> getRoots ();
    public Node getParent (Node node);
    public List<Node> getChildren (Node node);
}

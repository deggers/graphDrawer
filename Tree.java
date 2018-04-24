import java.io.Serializable;
import java.util.List;

// necessary ?

interface Tree <Node> extends Serializable {
    public List<Node> getRoots ();
    public Node getParent (Node node);
    public List<Node> getChildren (Node node);
}

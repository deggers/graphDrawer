import java.io.Serializable;
import java.util.List;

// necessary ?

interface Tree<Node> extends Serializable {
    List<Node> getRoots();

    Node getParent(Node node);

    List<Node> getChildren(Node node);
}

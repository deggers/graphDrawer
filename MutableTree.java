import java.io.Serializable;

interface MutableTree <Node extends Serializable> extends Tree<Node> {
    public boolean add (Node parent, Node node);
    public boolean remove (Node node, boolean cascade);
}
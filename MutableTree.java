interface MutableTree <Node> extends Tree<Node>  {
    boolean add(Node parent, Node node);
    boolean remove(Node node, boolean cascade);
}
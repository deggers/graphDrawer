interface MutableTree <Node> extends Tree<Node>  {
    public boolean add (Node parent, Node node);
    public boolean remove (Node node, boolean cascade);
}
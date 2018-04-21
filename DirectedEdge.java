public final class DirectedEdge extends Edge {
    private final Node[] _to;
    public DirectedEdge( Node from, Node ... to ) {
        super( from );
        _to = to;
    }
    public Node getFrom() {
        return _endPoint1;
    }
    public Node[] getTo() {
        return _to;
    }
}

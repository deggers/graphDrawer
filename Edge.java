public abstract class Edge {
    final Node _endPoint1;

    Edge(Node endPoint) {
        _endPoint1 = endPoint;
    }

    public Node getEndPoint1() {
        return _endPoint1;
    }
}
package structure;
import structure.GraphNode;

public class           Edge {
    public GraphNode tail, head;
    private boolean reversed = false;
    private String edgeType;


    public boolean markedType1Conflict = false;

    public boolean isReversed() {
        return reversed;}

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    private double weight = 1;

    public Edge(){}

    public Edge(GraphNode tail, GraphNode head) {
        this.tail = tail;
        this.head = head;
    }

    public Edge(GraphNode tail, GraphNode head, String edgeType) {
        this(tail, head);
        this.edgeType = edgeType;
    }

    public Edge(GraphNode tail, GraphNode head, String edgeType, boolean reversed) {
        this(tail, head);
        this.edgeType = edgeType;
        this.reversed = reversed;
    }

    public GraphNode getStart() {
        return this.tail;}

    public GraphNode getTarget() {return this.head;}

    public boolean contains(GraphNode graphNode) {
        return this.head.equals(graphNode) || this.tail.equals(graphNode);
    }

    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    public boolean isMarkedType1Conflict() {
        return markedType1Conflict;
    }

    public void setMarkedType1Conflict(boolean markedType1Conflict) {
        this.markedType1Conflict = markedType1Conflict;
    }

    @Override
    public String toString() {
        return String.format("'%s' to '%s'", tail, head);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (tail != null ? !tail.equals(edge.tail) : edge.tail != null) return false;
        if (head != null ? !head.equals(edge.head) : edge.head != null) return false;
        return edgeType != null ? edgeType.equals(edge.edgeType) : edge.edgeType == null;
    }

    @Override
    public int hashCode() {
        int result = tail != null ? tail.hashCode() : 0;
        result = 31 * result + (head != null ? head.hashCode() : 0);
        result = 31 * result + (edgeType != null ? edgeType.hashCode() : 0);
        return result;
    }

    public boolean getReversed() {
        return this.reversed;
    }

    public String getEdgeType() {
        return this.edgeType;
    }
}
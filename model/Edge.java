package model;

import java.util.LinkedList;

public class Edge {

    Object start;
    Object target;
    String edgeType;
    Double weight;
    LinkedList<GraphNode> incidentTo;

    public Edge() {
    }

    public Edge(Object start, Object target) {
        this.start = start;
        this.target = target;

        edgeType = "none";
        weight = 1.0;
        // set children and so
        ((GraphNode) start).addParent((GraphNode) target);
        ((GraphNode) target).addChild((GraphNode) start);

//        dont whteher be used or not... --dustyn
        LinkedList<GraphNode> incidentTo = new LinkedList<>();
        incidentTo.add((GraphNode) start);
        incidentTo.add((GraphNode) target);
        this.incidentTo = incidentTo;

    }


    @Override
    public String toString() {
        return this.start + " to " + this.target + " type: " +edgeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (start != null ? !start.equals(edge.start) : edge.start != null) return false;
        if (target != null ? !target.equals(edge.target) : edge.target != null) return false;
        if (edgeType != null ? !edgeType.equals(edge.edgeType) : edge.edgeType != null) return false;
        return weight != null ? weight.equals(edge.weight) : edge.weight == null;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (edgeType != null ? edgeType.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        return result;
    }
}

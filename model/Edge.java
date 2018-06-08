package model;

import model.HelperTypes.ProtoNode;

public class Edge {

    Object start;
    Object target;
    String edgeType;
    Double weight;

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
    }

    public Edge(Object start, Object target, String edgeType, Double weight) {
        this.start = start;
        this.target = target;
        this.edgeType = edgeType;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return this.start + " to " + this.target;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (start != null ? !start.equals(edge.start) : edge.start != null) return false;
        return target != null ? target.equals(edge.target) : edge.target == null;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }
}

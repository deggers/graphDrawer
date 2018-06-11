package model;

import java.util.Objects;

public class Edge {

    GraphNode start;
    GraphNode target;
    String edgeType;
    Double weight;

    Edge() {
    }

    Edge(GraphNode start, GraphNode target) {
        this.start = start;
        this.target = target;
        edgeType = "none";
        weight = 1.0;
    }

    public Edge(GraphNode start, GraphNode target, String edgeType) {
        this.start = start;
        this.target = target;
        this.edgeType = edgeType;
    }

    public Edge(GraphNode start, GraphNode target, String edgeType, Double weight) {
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
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        final Edge other = (Edge) obj;
        if (!Objects.equals(this.start, other.start) || !Objects.equals(this.target, other.target))
            return false;

        return Objects.equals(this.edgeType, other.edgeType);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.start);
        hash = 13 * hash + Objects.hashCode(this.target);
        hash = 13 * hash + Objects.hashCode(this.edgeType);
        return hash;
    }


    boolean isSelfEdge() {
        return (this.start.equals(this.target));
    }
}

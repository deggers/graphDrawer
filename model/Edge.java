package model;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.util.LinkedList;

public class Edge {

    private int uniqueEdgeId;
    private static int edgeIdCounter = 0;

    Object start;
    Object target;
    String edgeType;
    Double weight;

    public Edge() {
    }

    public Edge(Object u, Object v) {
        this.start = u;
        this.target = v;

        edgeIdCounter++;
        this.uniqueEdgeId = edgeIdCounter;

        edgeType = "none";
        weight = 1.0;

        GraphNode start = (GraphNode) u;
        GraphNode target = (GraphNode) v;
        start.addParent(target);
        target.addChild(start);
    }

    @Override
    public String toString() {
        return "id:" + this.uniqueEdgeId + " | " + this.start + " to " + this.target + " type: " + edgeType + " ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (uniqueEdgeId != edge.uniqueEdgeId) return false;
        if (start != null ? !start.equals(edge.start) : edge.start != null) return false;
        if (target != null ? !target.equals(edge.target) : edge.target != null) return false;
        if (edgeType != null ? !edgeType.equals(edge.edgeType) : edge.edgeType != null) return false;
        return weight != null ? !weight.equals(edge.weight) : edge.weight != null;
    }

    @Override
    public int hashCode() {
        int result = uniqueEdgeId;
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (edgeType != null ? edgeType.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        return result;
    }
}

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

    
    
}

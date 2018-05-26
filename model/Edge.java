package model;

import model.HelperTypes.protoNode;

public class Edge {

    protoNode start;
    protoNode target;
    String edgeType;
    Double weight;

    public Edge() {
    }

    public Edge(protoNode start, protoNode target) {
        this.start = start;
        this.target = target;
        edgeType = "none";
        weight = 1.0;
    }

    public Edge(protoNode start, protoNode target, String edgeType, Double weight) {
        this.start = start;
        this.target = target;
        this.edgeType = edgeType;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + start.getLabel() + ",\ttarget=" + target.getLabel() + ",\tedgeType=" + edgeType + ",\tweight=" + weight + '}';
    }

    
    
}

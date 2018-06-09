package model;

import java.util.Objects;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.start);
        hash = 13 * hash + Objects.hashCode(this.target);
        hash = 13 * hash + Objects.hashCode(this.edgeType);
        hash = 13 * hash + Objects.hashCode(this.weight);
        return hash;
    }

    
    
}

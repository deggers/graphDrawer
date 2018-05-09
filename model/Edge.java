/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author gross
 */
public class Edge {

    Node start;
    Node target;
    String edgeType;
    Double weight;

    public Edge() {
    }

    public Edge(Node start, Node target) {
        this.start = start;
        this.target = target;
        edgeType = "none";
        weight = 1.0;
    }

    public Edge(Node start, Node target, String edgeType, Double weight) {
        this.start = start;
        this.target = target;
        this.edgeType = edgeType;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + start + ",\ttarget=" + target + ",\tedgeType=" + edgeType + ",\tweight=" + weight + '}';
    }

    
    
}

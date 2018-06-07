package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class GraphNode extends AbstractNode {

    private LinkedHashSet<GraphNode> children = new LinkedHashSet<>();
    private LinkedHashSet<GraphNode> parents = new LinkedHashSet<>();
    private boolean dummyNode = false;

    private char dfsStatus = 'u'; //unvisited, visited, final
    char getDfsStatus() {
        return dfsStatus;
    }
    void setDfsStatus(char dfsStatus) {
        if (dfsStatus=='u' || dfsStatus=='v' || dfsStatus=='f') {
            this.dfsStatus = dfsStatus;
        } else {
            throw new IllegalArgumentException("dfsStatus can only be set to u, v or f");
        }
    }

    GraphNode(String label, String type, boolean isDummyNode) {
        super(label);
        this.GraphMLType = type;
        this.dummyNode = isDummyNode;
        children = new LinkedHashSet<>();
        parents = new LinkedHashSet<>();
    }
    GraphNode(String label) {
        super(label);
        GraphMLType = null;
    }

    public String getLabel() {
        return this.label;
    }
    public List<GraphNode> getChildren() {
        return new LinkedList<>(children);
    }
    public List<GraphNode> getParents() {
        return new LinkedList<>(parents);
    }


    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }
    public boolean isRoot() {
        return parents.isEmpty();
    }
    public boolean isDummyNode(){
        return dummyNode;
    }



    @Override
    public String toString() {
//        return String.format("label: %s parents: %s children: %s \n", label,parentLabels(),childrenLabels());
        return String.format("%s", label);
    }
     List<String> parentLabels(){
        List<String> returnlist = new LinkedList<>();
        for (GraphNode node : parents) {
            returnlist.add(node.label);
        }
        return returnlist;
    }
     List<String> childrenLabels(){
        List<String> returnlist = new LinkedList<>();
        for (GraphNode node : children) {
            returnlist.add(node.label);
        }
        return returnlist;
    }

    void addChild(GraphNode node){
        children.add(node);
    }
    void removeChild(GraphNode node) {
        children.remove(node);
    }
    void addParent(GraphNode node){
        parents.add(node);
    }
    void removeParent(GraphNode node) {
        parents.remove(node);
    }
    
    
}

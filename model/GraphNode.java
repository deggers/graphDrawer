package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphNode extends AbstractNode {
    
    private final List<GraphNode> children;
    private final List<GraphNode> parents;
    private final boolean dummyNode;
    private char dfsStatus = 'u'; //unvisited, visited, final

    public char getDfsStatus() {
        return dfsStatus;
    }

    public void setDfsStatus(char dfsStatus) {
        if (dfsStatus=='u' || dfsStatus=='v' || dfsStatus=='f') {
            this.dfsStatus = dfsStatus;
        } else {
            throw new IllegalArgumentException("dfsStatus can only be set to u, v or f");
        }
    }

    public GraphNode(String label, String type, boolean isDummyNode) {
        super(label);
        this.GraphMLType = type;
        this.dummyNode = isDummyNode;
        children = new ArrayList<>();
        parents = new ArrayList<>();
    }
    
    public List<GraphNode> getChildren() {
        return children;
    }
    
    public void addChild(GraphNode node){
        children.add(node);
    }
    
    public List<GraphNode> getParents() {
        return parents;
    }
    
    public void addParent(GraphNode node){
        parents.add(node);
    }
    
    public boolean isDummyNode(){
        return dummyNode;
    }
    
    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public boolean isRoot() {
        return parents.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("label: %s parents: %s children: %s", label,parentLabels(),childrenLabels());
    }
    private List<String> parentLabels(){
        List<String> returnlist = new LinkedList<>();
        for (GraphNode node : parents) {
            returnlist.add(node.label);
        }
        return returnlist;
    }

    private List<String> childrenLabels(){
        List<String> returnlist = new LinkedList<>();
        for (GraphNode node : children) {
            returnlist.add(node.label);
        }
        return returnlist;
    }

    public void removeChild(GraphNode node) {
        children.remove(node);
    }

    public void removeParent(GraphNode node) {
        parents.remove(node);
    }
    
    
}

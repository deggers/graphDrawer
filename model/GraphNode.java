package model;

import java.util.ArrayList;
import java.util.List;

public class GraphNode extends AbstractNode {
    
    private final List<GraphNode> children;
    private final List<GraphNode> parents;
    private final boolean dummyNode;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

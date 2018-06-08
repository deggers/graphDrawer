package model;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GraphNode extends AbstractNode {

    private LinkedHashSet<GraphNode> children = new LinkedHashSet<>();
    private LinkedHashSet<GraphNode> parents = new LinkedHashSet<>();
    private boolean dummyNode = false;
    private int layer = -1;

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

    GraphNode(String label) {
        super(label);
        GraphMLType = null;
    }
    GraphNode(String label, String type, boolean isDummyNode) {
        super(label);
        this.GraphMLType = type;
        this.dummyNode = isDummyNode;
        children = new LinkedHashSet<>();
        parents = new LinkedHashSet<>();
    }
    GraphNode(String label, String type, boolean isDummy, int layer) {
        super(label);
        this.GraphMLType = type;
        this.dummyNode = isDummy;
        this.layer = layer;
        children = new LinkedHashSet<>();
        parents = new LinkedHashSet<>();
    }
    // not sure if useful - hoped so but did not solve to create node with parent and child --dustyn
    GraphNode(String label, String type, boolean isDummy, int layer, GraphNode parent, GraphNode children) {
        super(label);
        this.GraphMLType = type;
        this.dummyNode = isDummy;
        this.layer = layer;
        this.children.add(children);
        this.parents.add(parent);
    }


    public String getLabel() {
        return this.label;
    }
    public LinkedList<GraphNode> getChildren() {
        return new LinkedList<>(children);
    }
    public LinkedList<GraphNode> getParents() {
        return new LinkedList<>(parents);
    }

    int getLayer() {
        return layer;
    }
    String getType() {
        return GraphMLType;}
    void setLayer(int layer) {
        this.layer = layer;
    }
    
    int indegree(){
        return parents.size();
    }
    
    int outdegree(){
        return children.size();
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
//        return String.format("%s,level:%s", label, layer);
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

    boolean addChild(GraphNode node){
        return children.add(node);
    }
    boolean removeChild(GraphNode node) {
        return children.remove(node);
    }

    boolean addParent(GraphNode node){
        return parents.add(node);
    }
    boolean removeParent(GraphNode node) {
        return parents.remove(node);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;

        GraphNode node = (GraphNode) o;

        return label.equals(node.label);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < label.length(); i++)
            hash = hash*31 + label.charAt(i);
        return hash;
    }
}

package model;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class GraphNode extends AbstractNode {

    private LinkedHashSet<GraphNode> children = new LinkedHashSet<>();
    private LinkedHashSet<GraphNode> parents = new LinkedHashSet<>();
    private boolean dummyNode = false;
    private int layer = -1;
    private static int uniqueGraphNodeCounter = 0;
    private int uniqueGraphNodeId;

    private char dfsStatus = 'u'; //unvisited, visited, final


    GraphNode(String label) {
        super(label);
        this.uniqueGraphNodeId = setNodeId();
        GraphMLType = null;
    }

    GraphNode(String label, String type, boolean isDummyNode) {
        super(label);
        this.uniqueGraphNodeId = setNodeId();
        this.GraphMLType = type;
        this.dummyNode = isDummyNode;
    }

    GraphNode(String label, String type, boolean isDummy, int layer) {
        super(label);
        this.uniqueGraphNodeId = setNodeId();
        this.GraphMLType = type;
        this.dummyNode = isDummy;
        this.layer = layer;
    }

     char getDfsStatus() {
        return dfsStatus;
    }

    void setDfsStatus(char dfsStatus) {
        if (dfsStatus == 'u' || dfsStatus == 'v' || dfsStatus == 'f') {
            this.dfsStatus = dfsStatus;
        } else {
            throw new IllegalArgumentException("dfsStatus can only be set to u, v or f");
        }
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
        return GraphMLType;
    }

    void setLayer(int layer) {
        this.layer = layer;
    }

    int indegree() {
        return parents.size();
    }

    int outdegree() {
        return children.size();
    }

    private int setNodeId()
    {
        uniqueGraphNodeCounter++;
        return uniqueGraphNodeCounter;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parents.isEmpty();
    }

    boolean isDummyNode() {
        return dummyNode;
    }


    @Override
    public String toString() {
//        return String.format("label: %s parents: %s children: %s \n", label,parentLabels(),childrenLabels());
//        return String.format("%s,level:%s", label, layer);
        return String.format("'%s'", label);
    }

    List<String> parentLabels() {
        List<String> returnlist = new LinkedList<>();
        for (GraphNode node : parents) {
            returnlist.add(node.label);
        }
        return returnlist;
    }

    List<String> childrenLabels() {
        List<String> returnlist = new LinkedList<>();
        for (GraphNode node : children) {
            returnlist.add(node.label);
        }
        return returnlist;
    }

    boolean addChild(GraphNode node) {
        return children.add(node);
    }

    boolean removeChild(GraphNode node) {
        return children.remove(node);
    }

    boolean addParent(GraphNode node) {
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

        return uniqueGraphNodeId == node.uniqueGraphNodeId;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < label.length(); i++)
            hash = hash * 31 + label.charAt(i);
        return hash * uniqueGraphNodeId;
    }
}

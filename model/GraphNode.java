package model;

public class GraphNode {
    private String label;
    private String nodeType;
    private boolean dummyNode = false;
    private int layer = -1;
    private char dfsStatus = 'u'; //unvisited, visited, final

    GraphNode(String label, String nodeType ) {
        this.label = label;
        this.nodeType = nodeType;
    }
    GraphNode(String label, String type, boolean isDummyNode) {
        this.label = label;
        this.nodeType = type;
        this.dummyNode = isDummyNode;
    }
    GraphNode(String label, String type, boolean isDummyNode, int layer) {
        this.label = label;
        this.nodeType = type;
        this.dummyNode = isDummyNode;
        this.layer = layer;
    }

    String getNodeType() {
        return nodeType;
    }
    public String getLabel() {
        return this.label;
    }


    int getLayer() {
        return layer;
    }

    void setLayer(int layer) {
        this.layer = layer;
    }

    boolean isDummyNode(){
        return dummyNode;
    }



    @Override
    public String toString() {
//        return String.format("label: %s parents: %s children: %s \n", label,parentLabels(),childrenLabels());
        return String.format("'%s':%s", label, layer);
    }


    TreeNode toTreeNode(){
        String label = this.label;
        String type = this.getNodeType();
        return new TreeNode(label,type);
    }


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;

        GraphNode node = (GraphNode) o;

        if (dummyNode != node.dummyNode) return false;
        if (layer != node.layer) return false;
        if (label != null ? !label.equals(node.label) : node.label != null) return false;
        return nodeType != null ? nodeType.equals(node.nodeType) : node.nodeType == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
        result = 31 * result + (dummyNode ? 1 : 0);
        result = 31 * result + layer;
        return result;
    }
}

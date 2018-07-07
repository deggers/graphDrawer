package structure;

import java.util.LinkedHashMap;

public class GraphNode {
    private String label, nodeType;
    public double x, y;
    public double x_Bary;
    private int layer = -1;
    private boolean isDummy = false;
    private LinkedHashMap<Double, Boolean> portMap = new LinkedHashMap<>();

    public boolean isDummy() {
        return isDummy;
    }
    public void setDummy(boolean dummy) {
        isDummy = dummy;
    }


    public LinkedHashMap<Double, Boolean> getPortMap() {
        return portMap;
    }
    public void setPortMap(LinkedHashMap<Double, Boolean> portMap) {
        this.portMap = portMap;
    }
    public void resetPortMap() {
        this.portMap = new LinkedHashMap<>();
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public double getX_Bary() {
        return x_Bary;
    }

    public enum STATUS {done, unvisited, visited}

    private STATUS status = STATUS.unvisited;

    public GraphNode(GraphNode origNode) { // da real Copy-Constructor
        this.label = origNode.label;
        this.nodeType = origNode.nodeType;
        this.x = origNode.x;
        this.y = origNode.y;
        this.layer = origNode.layer;
        this.isDummy = origNode.isDummy;
        this.status = origNode.status;
    }
    public GraphNode(String label) {
        this.label = label;
    }
    public GraphNode(String label, boolean isDummy) {
        this(label);
        this.isDummy = isDummy;
    }
    public GraphNode(String label, String nodeType) {
        this(label);
        this.nodeType = nodeType;
    }
    public GraphNode(String label, String nodeType, boolean isDummy) {
        this(label,isDummy);
        this.isDummy = isDummy;}

    public GraphNode(String label, boolean isDummy, int layer) {
        this(label,isDummy);
        this.layer = layer;
    }

    public STATUS getDfsStatus() {
        return this.status;
    }

    public void setDfsStatus(STATUS status) {
        this.status = status;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getLabel()  {
        return this.label;
    }

    public double compareTo(GraphNode o) { return Double.compare(x_Bary, o.x_Bary); }

    @Override
    public String toString() {
        return this.label+":"+layer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;
        GraphNode graphNode = (GraphNode) o;
        return label != null ? label.equals(graphNode.label) : graphNode.label == null;
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }
}

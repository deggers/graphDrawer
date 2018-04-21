import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class Graph {
    private /* */ String _name = "G";
    private final Map<String, Node> _nodes = new LinkedHashMap<>();
    private final Set<DirectedEdge> edges = new LinkedHashSet<>();

    public boolean addNode(Node node) {
        return _nodes.put(node.label, node) == null;
    }

    public void addEdge(DirectedEdge edge) {
        edges.add(edge);
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public final Map<String, Node> getNodes() {
        return _nodes;
    }

    public final Set<DirectedEdge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return _name + " " + _nodes;
    }
}
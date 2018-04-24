import java.io.Serializable;

public class Node implements Serializable {
    public String label;
    private double x = 0;
    private double y = 0;
    private int id = 0;
    private boolean thread = false;
    private int offset = 0;

    public Node(String label) {
        this.label = label;
    }
}

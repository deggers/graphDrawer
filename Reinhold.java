import java.util.HashMap;
import java.util.Map;

public class Reinhold {

    Map<Integer, Integer> tempXPos = new HashMap<>(); //x_l(l)

    public void layout(Node root) {
        addYCoords(root, 0);
    }

    private void addYCoords(Node node, int level) {
        tempXPos.put(level, 0);             // all x initially 0
        node.y = level;
        for (Node child : node.children) {
            addYCoords(child, level + 1);
        }
    }

    // post order: l r w
    public void postOrder(Node root) {
        if(root !=  null) {
//            postOrder(root.left);
//            postOrder(root.right);

            addTempXPos(root); // stommt das?
        }
    }
    // oder muss doch ein node retun erfolgen?


    private void addTempXPos(Node node) {
        if(node != null){

        }
    }


}



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reinhold {

    //key= level value= Hilfsfkt x_l(l)
    Map<Double, Double> tempXPos = new HashMap<>();
    Map<List<Node>, Double> minDistance = new HashMap<>();
    Map<Node, Node> leftChild = new HashMap<>();
    Map<Node, Node> rightChild = new HashMap<>();

    // step 1: set y coordinates= level
    // step 2: add tempXcoord, tree travers in postorder
    // step 3: position of subtrees
    // step 4: final x coords
    public void layout(Node root) {
        addYCoords(root, 0);
        setChildrenBinaryTree(root);
        postOrder(root);
    }

    //step 1
    private void addYCoords(Node node, double level) {
        // node.checked = false; // bisschen geschummelt, damit später die funktion setChildren klappt
        tempXPos.put(level, 0.0);             // all x initially 0
        node.y = level;
        for (Node child : node.getChildren()) {
            addYCoords(child, level + 1);
        }
    }

    // set right, left, parent
    public void setChildrenBinaryTree(Node node) {
        if (!node.checked) {
            //System.out.println("\n current node = " + node.label);
            List<Node> kids = new ArrayList<>();
            node.checked = true;
            for (Node c : node.getChildren()) {
                if (!c.checked && (c.y == (node.y + 1))) {
                    kids.add(c);
                    //System.out.println("kid = " + c.label);
                    // c.parent = node;       wird im parser bereits ausgeführt
                    //System.out.println("parent=" + node.label);
                }
                //System.out.println("Listsize= "+ kids.size());
                if (kids.size() == 0) {
                    //node.isLeaf = true;
                    node.checked = true;
                }
                if (kids.size() == 1) {
                    node.leftChild = kids.get(0);
                    // System.out.println("leftchild= "+ kids.get(0).label);
                    setChildrenBinaryTree(kids.get(0));
                }
                if (kids.size() == 2) {
                    node.leftChild = kids.get(0);
                    //System.out.println("leftchild= "+ kids.get(0).label);
                    node.rightChild = kids.get(1);
                    // System.out.println("rightchild= "+ kids.get(1).label);
                    setChildrenBinaryTree(kids.get(0));
                    setChildrenBinaryTree(kids.get(1));
                }
            }
        }
    }

    // step 2: add x temp, post order: l r w
    public void postOrder(Node root) {
        if (root != null) {
            postOrder(root.leftChild);
            postOrder(root.rightChild);
            addTempXCoords(root);
        }
    }


    private void addTempXCoords(Node node) {
        if (node != null) {
            if (node.isLeaf()) {
                node.xtemp = tempXPos.get(node.y);
            } else if (node.leftChild != null && node.rightChild == null) { // stimmt das so???
                node.xtemp = node.leftChild.xtemp + 1; // oder x+1?
            } else if (node.rightChild != null && node.leftChild == null) {
                node.xtemp = node.rightChild.xtemp - 1; // oder x+1?
            } else if (node.leftChild != null && node.rightChild != null) {
                node.xtemp = 0.5 * (node.rightChild.xtemp + node.leftChild.xtemp);
            }
            if (node.xtemp < tempXPos.get(node.y)) {
                node.xtemp = tempXPos.get(node.y);
            }
            tempXPos.put(node.y, node.y + 2);

        }
    }

    //step 3: position of subtrees ,get mindist of 2 subtrees:
    // compare right conture of left & left conture of right subtree
    public void getSubtreePositions(Node root) {
        getMinDist(root.leftChild, root.rightChild);

    }

    // calc min Dist based on xtemp (correct???)
    public void getMinDist(Node left, Node right) {
        // try catch falls node null etc? 
        List<Node> NodesDist = new ArrayList<>();
        NodesDist.add(left);
        NodesDist.add(right);
        double minDist = right.xtemp - left.xtemp;
        minDistance.put(NodesDist, minDist);
        // checken ob es überhaupt noch kinder gibt? 
        getMinDist(left.rightChild, right.leftChild);
    }

}



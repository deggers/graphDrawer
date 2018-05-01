import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

public class Reinhold {

    //key= level value= Hilfsfkt x_l(l)
    Map<Double, Double> tempXPos = new HashMap<>();
    Map<List<Node>, Double> sCuccrent = new HashMap<>();
    //    Map<Node, Node> leftChild = new HashMap<>();
//    Map<Node, Node> rightChild = new HashMap<>();
    double smin = 2;
    Node LL;

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
        node.checked = false; // bisschen geschummelt, damit später die funktion setChildren klappt
        tempXPos.put(level, 0.0);             // all x initially 0
        node.y = level;
        for (Node child : node.getChildren()) {
            addYCoords(child, level + 1);
        }
    }

    // set right, left, parent -- nur 1 dann gleichz l und r
    public void setChildrenBinaryTree(Node node) {
        if (!node.checked) {
            //System.out.println("\n current node = " + node.label);
            List<Node> kids = new ArrayList<>();
            node.checked = true;
            for (Node c : node.getChildren()) {
                if (!c.checked && (c.y == (node.y + 1))) {
                    kids.add(c);
                }
                if (kids.size() == 0) {
                    node.checked = true;
                }
                if (kids.size() == 1) {
                    node.leftChild = kids.get(0);
                    kids.get(0).onlyChild = true;
                    setChildrenBinaryTree(kids.get(0));
                    // gleichzeitig left und right???
                    // node.rightChild = kids.get(0);
                }
                if (kids.size() == 2) {
                    node.leftChild = kids.get(0);
                    node.rightChild = kids.get(1);
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
                node.xtemp = node.leftChild.xtemp + 1;
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
        smin = root.leftChild.xtemp - root.rightChild.xtemp;
        getMinDist(root.leftChild, root.rightChild);
        for (Node c : root.getChildren()) {
            if(! c.isLeaf()) {
                getMinDist(c.leftChild, c.rightChild);
            }
        }


    }

    public void getMinDist(Node left, Node right) {
        List<Node> NodesDist = new ArrayList<>();
        NodesDist.add(left);
        NodesDist.add(right);
        double scurr = right.xtemp - left.xtemp;
        sCuccrent.put(NodesDist, scurr);
        // problem bei überlagerung dass dist negativ wird?
        if (scurr < smin) {
            smin = scurr;
        }
        // hier zeiger einführen
        if(left.rightChild.isLeaf() && !right.leftChild.isLeaf()){
            left.rightChild.threadTo = right.leftChild;
            left.rightChild.hasThread=true;
        }
        else if(right.leftChild.isLeaf() && !left.rightChild.isLeaf()){
            right.leftChild.threadTo = left.rightChild;
            right.leftChild.hasThread=true;
        }
        if (left.rightChild != null && right.leftChild != null) {
            getMinDist(left.rightChild, right.leftChild);
        }


    }

    int findMaxLevel(Node root) {
        if (root == null)
            return 0;

        return 1 + max((findMaxLevel(root.leftChild)), findMaxLevel(root.rightChild));
    }

    public void outerNodes(Node root) {
        Node LL = getLL(root.leftChild);
        Node LR = getLR(root.leftChild);
        Node RL = getLL(root.rightChild);
        Node RR = getLR(root.rightChild);

    }

    public Node getLL(Node node) {
        while (!node.isLeaf()) {
            outerNodes(node.leftChild);
        }
        return node;
    }

    public Node getLR(Node node) {
        while (!node.isLeaf()) {
            outerNodes(node.rightChild);
        }
        return node;
    }


}


//    public void preorder(TreeNode root) {
//        if(root !=  null) {
//            //Visit the node by Printing the node data
//            System.out.printf("%d ",root.data);
//            preorder(root.left);
//            preorder(root.right);
//        }
//    }
//
//    public void inOrder(TreeNode root) {
//        if(root !=  null) {
//            inOrder(root.left);
//            //Visit the node by Printing the node data
//            System.out.printf("%d ",root.data);
//            inOrder(root.right);
//        }
//    }



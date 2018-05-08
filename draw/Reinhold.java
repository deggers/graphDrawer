package draw;

import model.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Reinhold {

    public static Tree processTree(Tree tree) {
        try {
            Reinhold r = new Reinhold();
            Node root = tree.getRoot();
            if (isBinaryTree(root)) {
                r.layout(root);
                return tree;
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error while running RT Algorithm");
            System.out.println(e);
            return null;
        }
    }

    public static boolean isBinaryTree(Node root) {
        if (root.getChildren().size() > 3) {
            return false;
        } else {
            for (Node n : root.getChildren()) {
                isBinaryTree(n);
            }
        }
        return true;
    }

    private void layout(Node root) {
        addYCoords(root, 0);
        setChildrenBinaryTree(root);
        setup(root, 0, getRR(root), getLL(root));
        petrify(root, 0);
        double offset = getLL(root).x;
        petrify(root, -offset);
        stretch(root);
    }

    private void stretch(Node node) {
        node.y *= 3;
        for (Node n : node.getChildren()) {
            stretch(n);
        }
    }

    //step 1
    private void addYCoords(Node node, double level) {
        node.y = level;
        for (Node child : node.getChildren()) {
            addYCoords(child, level + 1);
        }
    }

    // set right, left, parent
    private void setChildrenBinaryTree(Node node) {
        if (!node.checked) {
            List<Node> kids = new ArrayList<>();
            node.checked = true;
            for (Node c : node.getChildren()) {
                c.parent = node;
                if ((c.y == (node.y + 1))) { //
                    kids.add(c);
                }
//                if (kids.size() == 0) {
//                }
                if (kids.size() == 1) {
                    node.leftChild = kids.get(0);
                    setChildrenBinaryTree(kids.get(0));
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

    private void setup(Node root, int level, Node rmost, Node lmost) {
        Node right, left;
        // avoid selecting an extreme
        if (root == null) {
            lmost.level = -1;
            rmost.level = -1;
        } else {
            Node ll, lr, rr, rl;
            root.y = level;
            left = root.leftChild;  // follows contour of left subtree
            right = root.rightChild;  // follows contour of right subtree
            ll = getLL(root);
            if (left == null) {
                lr = root;
            } else {
                lr = getRR(left);
            }
            rr = getRR(root);
            if (right == null) {
                rl = root;
            } else {
                rl = getLL(right);
            }

            setup(left, level + 1, lr, ll); // position subtrees recursively
            setup(right, level + 1, rr, rl); // position subtrees recusively
            if (right == null && left == null) { // leaf
                rmost = root;
                lmost = root;
                rmost.level = level;
                rmost.offset = 0;
                lmost.offset = 0;
                root.offset = 0;
            } else {
                double minsep = 2;
                double cursep = minsep;
                double rootsep = minsep;
                double loffsum = 0;
                double roffsum = 0;
                // consider each level in turn until one subtree ist exhausted, pushing subtrees
                // apart when neccessary
                while (left != null && right != null) {
                    if (cursep < minsep) {
                        rootsep = rootsep + (minsep - cursep);
                        cursep = minsep;
                    }
                    // advance l and r
                    if (left.rightChild != null) {
                        loffsum = loffsum + left.offset;
                        cursep = cursep - left.offset;
                        left = left.rightChild;
                    } else { //if (left.leftChild!= null){
                        loffsum = loffsum - left.offset;
                        cursep = cursep + left.offset;
                        left = left.leftChild;
                    }
                    if (right.leftChild != null) {
                        roffsum = roffsum - right.offset;
                        cursep = cursep - right.offset;
                        right = right.leftChild;
                    } else {//if(right.rightChild!=null) {
                        roffsum = roffsum + right.offset;
                        cursep = cursep + right.offset;
                        right = right.rightChild;
                    }
                }
                // set the offset in root and include it in accumulated offsets for l and r
                root.offset = (rootsep + 1) / 2;
                loffsum = loffsum - root.offset;
                roffsum = roffsum + root.offset;

                //UPDATE EXTREME DESCENDANTS INFORMATIONS
                if (rl.level > ll.level || root.leftChild == null) {
                    lmost = rl;
                    lmost.offset += root.offset;
                } else {
                    lmost = ll;
                    lmost.offset -= root.offset;
                }
                if (lr.level > rr.level || root.rightChild == null) {
                    rmost = lr;
                    rmost.offset -= root.offset;
                } else {
                    rmost = rr;
                    rmost.offset += root.offset;
                }

                //Threading: if subtrees are of uneven heights
                if (left != null && left != root.leftChild) {
                    rr.hasThread = true;
                    rr.offset = abs(rr.offset + root.offset - loffsum);
                    if ((loffsum - root.offset) <= rr.offset) {
                        rr.leftChild = left;
                    } else {
                        rr.rightChild = left;
                    }
                }
                if ((right != null) && (right != root.rightChild)) {
                    ll.hasThread = true;
                    ll.offset = abs(ll.offset - root.offset - roffsum);
                    if ((roffsum + root.offset) >= ll.offset) {
                        ll.rightChild = right;
                    } else {
                        ll.leftChild = right;
                    }
                }
            }
        }
    }

    //Procedure PETRIFY converts relative positionings (offsets) to absolute coordinates.
    //preorder traversal of tree

    private void petrify(Node root, double xpos) {
        if (root != null) {
            root.x = xpos;

            if (root.hasThread) {
                root.hasThread = false;
                root.rightChild = null;
                root.leftChild = null;
            }
            if (root.leftChild != null) {
                petrify(root.leftChild, xpos - root.offset);
            }
            if (root.rightChild != null) {
                petrify(root.rightChild, xpos + root.offset);
            }
        }
    }


    private Node getLL(Node node) {
        while (node != null && !node.isLeaf()) {
            node = node.leftChild;
        }
        return node;
    }

    private Node getRR(Node node) {
        while (node != null && !node.isLeaf()) {
            node = node.rightChild;
        }
        return node;
    }
}

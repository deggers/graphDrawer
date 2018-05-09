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
            r.layout(root, tree.getTreeDepth());
            return tree;
           }
        catch (Exception e) {
            System.out.println("Error while running RT Algorithm");
            e.printStackTrace();
            return null;
        }
    }

    private void layout(Node root, int depth) {
        addYCoords(root, 0);
        setChildrenBinaryTree(root);
        setup(root, 0, new Extreme(getRR(root, depth)), new Extreme(getLL(root, depth)));
        petrify(root, 0);
        double offset = getLL(root, depth).x;
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
            double childLevel= node.y + 1;
            for (Node c : node.getChildren()) {
                c.parent = node;
                if ((c.y == (childLevel))) {
                    kids.add(c);
                }
//                if(kids.size() > 2){
//                    return false;
//                }
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

    private void setup(Node root, int level, Extreme rmost, Extreme lmost) {
        // avoid selecting an extreme
        if (root == null) {
            lmost.lev = -1;
            rmost.lev = -1;
        } else {
            Node right= null, left=null;
            Extreme ll = new Extreme();
            Extreme lr = new Extreme();
            Extreme rr = new Extreme();
            Extreme rl = new Extreme();
            //root.y = level;
            left = root.leftChild;  // follows contour of left subtree
            right = root.rightChild;  // follows contour of right subtree
//
            setup(left, level + 1, lr, ll); // position subtrees recursively
            setup(right, level + 1, rr, rl); // position subtrees recusively
            if (right == null && left == null) { // leaf
                rmost.addr = root;
                lmost.addr = root;
                rmost.lev = level;
                lmost.lev=level;
                rmost.addr.offset = 0;
                lmost.addr.offset = 0;
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
                    } else {
                        loffsum = loffsum - left.offset;
                        cursep = cursep + left.offset;
                        left = left.leftChild;
                    }
                    if (right.leftChild != null) {
                        roffsum = roffsum - right.offset;
                        cursep = cursep - right.offset;
                        right = right.leftChild;
                    } else {
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
                if (rl.lev > ll.lev || root.leftChild == null) {
                    lmost.addr = rl.addr;
                    lmost.lev = rl.lev;
                    lmost.off += root.offset;
                } else {
                    lmost.addr = ll.addr;
                    lmost.lev = ll.lev;
                    lmost.off -= root.offset;
                }
                if (lr.lev > rr.lev || root.rightChild == null) {
                    rmost.addr = lr.addr;
                    rmost.lev = lr.lev;
                    rmost.off -= root.offset;
                } else {
                    rmost.addr = rr.addr;
                    rmost.lev = rr.lev;
                    rmost.off += root.offset;
                }

                //Threading: if subtrees are of uneven heights
                if (left != null && left != root.leftChild) {
                    rr.addr.hasThread = true;
                    rr.addr.offset = abs(rr.off + root.offset - loffsum);
                    if (((loffsum - root.offset) < rr.off)
                        || ((loffsum - root.offset) == rr.off)) {
                        rr.addr.leftChild = left;
                    } else {
                        rr.addr.rightChild = left;
                    }
                }
                if ((right != null) && (right != root.rightChild)) {
                    ll.addr.hasThread = true;
                    ll.addr.offset = abs(ll.off - root.offset - roffsum);
                    if (((roffsum + root.offset) > ll.off)
                         || ((roffsum + root.offset) == ll.off)){
                        ll.addr.rightChild = right;
                    } else {
                        ll.addr.leftChild = right;
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
                petrify(root.leftChild, xpos-root.offset);
            }
            if (root.rightChild != null) {
                petrify(root.rightChild, xpos+root.offset);
            }
        }
    }


    private Node getLL(Node node, int depth){
        if (node.isLeaf()) {
            if (node.y - depth < 0.0001) {
                return node;
            } else {
                return null;
            }
        }
        Node leftleft = getLL(node.leftChild, depth);
        if (leftleft != null) {
            return leftleft;
        }
        return getLL(node.rightChild, depth);
    }

    private Node getRR(Node node, int depth){
        if (node.isLeaf()) {
            if (node.y - depth < 0.0001) {
                return node;
            } else {
                return null;
            }
        }
        Node rightright = getRR(node.rightChild, depth);
        if (rightright != null) {
            return rightright;
        }
        return getRR(node.leftChild, depth);
    }

    public static class Extreme {
        public Extreme(Node n) {
            addr = n;
        }
        public Extreme(){}
        public Node addr;
        public int lev = 0;
        public double off = 0;
    }
}

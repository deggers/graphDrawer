package draw;

import draw.exception.NonBinaryTreeException;
import model.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Reinhold {


    public static Tree processTree(Tree tree) {
        if (!tree.isBinary()) {
            System.out.println("Tree is not binary, running RT not possible!");
            throw new NonBinaryTreeException("Running RT Algo not possible");
        }
        try {
            Reinhold r = new Reinhold();
            TreeNode root = tree.getRoot();
            r.layout(root, tree.getTreeDepth());
            return tree;
           }
        catch (Exception e) {
            System.out.println("Error while running RT Algorithm");
            e.printStackTrace();
            return null;
        }
    }

    private void layout(TreeNode root, int depth) {
        addYCoords(root, 0);
        setChildrenBinaryTree(root);
        setup(root, 0, new Extreme(getRR(root, depth)), new Extreme(getLL(root, depth)));
        petrify(root, 0);
        double offset = getLL(root, depth).x;
        petrify(root, -offset);
        stretch(root);
    }

    private void stretch(TreeNode node) {
        node.y *= 3;
        for (TreeNode n : node.getChildren()) {
            stretch(n);
        }
    }

    //step 1
    private void addYCoords(TreeNode node, double level) {
        node.y = level;
        for (TreeNode child : node.getChildren()) {
            addYCoords(child, level + 1);
        }
    }

    // set right, left, parent
    private void setChildrenBinaryTree(TreeNode node) {
        if (!node.checked) {
            List<TreeNode> kids = new ArrayList<>();
            node.checked = true;
            double childLevel= node.y + 1;
            for (TreeNode c : node.getChildren()) {
//                c.parent = node;
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

    private void setup(TreeNode root, int level, Extreme rmost, Extreme lmost) {
        // avoid selecting an extreme
        if (root == null) {
            lmost.lev = -1;
            rmost.lev = -1;
        } else {
            TreeNode right = null;
            TreeNode left = null;
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

    private void petrify(TreeNode root, double xpos) {
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


    private TreeNode getLL(TreeNode node, int depth){
        if(node == null){
            return null;
        }
        if (node.isLeaf()) {
            if (node.y - depth < 0.0001) {
                return node;
            } else {
                return null;
            }
        }
        TreeNode leftleft = getLL(node.leftChild, depth);
        if (leftleft != null) {
            return leftleft;
        }
        return getLL(node.rightChild, depth);
    }

    // was wenn baum nur aus einem knoten besteht oder aus root und einem knoten? der ist dann links
        private TreeNode getRR(TreeNode node, int depth){
        if(node == null){
            return null;
        }
        if (node.isLeaf()) {
            if (node.y - depth < 0.0001) {
                return node;
            } else {
                return null;
            }
        }
        TreeNode rightright = getRR(node.rightChild, depth);
        if (rightright != null) {
            return rightright;
        }
        return getRR(node.leftChild, depth);
    }

    public static class Extreme {
        Extreme(TreeNode n) {
            addr = n;
        }
        public Extreme(){}
        public TreeNode addr;
        public int lev = 0;
        public double off = 0;
    }
}

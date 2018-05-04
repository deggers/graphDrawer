package draw;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Reinhold {

    //key= level value= Hilfsfkt x_l(l)
    //private Map<Double, Double> tempXPos = new HashMap<>();
    //public Node LL, LR, RL, RR, lmost, rmost;
    private double minsep = 2, rootsep = 0, loffsum = 0, roffsum = 0, cursep = 2;

    // step 1: set y coordinates= level
    // step 2: add tempXcoord, tree travers in postorder
    // step 3: position of subtrees
    // step 4: final x coords

    public static MappedTreeStructure processTree(MappedTreeStructure tree) {
        try {
            Reinhold r = new Reinhold();
            Node root = tree.getRoot();
            r.layout(root);
            return tree;
        } catch (Exception e) {
            System.out.println("Error while running RT Algorithm");
            System.out.println(e);
            return null;
        }
    }

    public void layout(Node root) {
        addYCoords(root, 0);
        setChildrenBinaryTree(root);
        //postOrder(root);
        //outerNodes(root);
        // getSubtreePositions(root);
        //setCoords(root.leftChild, root.rightChild, root);

        setup(root, 0, getRR(root), getLL(root));
        petrify(root, 30);
    }

    //step 1
    private void addYCoords(Node node, double level) {
        //tempXPos.put(level, 0.0);             // all x initially 0
        node.y = level;
        for (Node child : node.getChildren()) {
            addYCoords(child, level + 1);
        }
    }

    // set right, left, parent
    public void setChildrenBinaryTree(Node node) {
        if (!node.checked) {
            List<Node> kids = new ArrayList<>();
            node.checked = true;
            for (Node c : node.getChildren()) {
                c.parent = node;
                if ( (c.y == (node.y + 1))) { //
                    kids.add(c);
                }
                if (kids.size() == 0) {
                    //node.checked = true;
                }
                if (kids.size() == 1) {
                    node.leftChild = kids.get(0);
                    //kids.get(0).onlyChild = true;
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
//
//    // step 2: add x temp, post order: l r w
//    public void postOrder(Node root) {
//        if (root != null) {
//            postOrder(root.leftChild);
//            postOrder(root.rightChild);
//            addTempXCoords(root);
//        }
//    }
//
//    private void addTempXCoords(Node node) {
//        if (node != null) {
//            if (node.isLeaf()) {
//                node.xtemp = tempXPos.get(node.y);
////                System.out.println("node.xtemp = " + node.xtemp);
//            }
//            if (node.leftChild != null && node.rightChild != null) {
//                System.out.println("node.rightChild.xtemp = " + node.rightChild.xtemp);
//                System.out.println("node.leftChild.xtemp = " + node.leftChild.xtemp);
//                node.xtemp = 0.5 * (node.rightChild.xtemp + node.leftChild.xtemp);
//                System.out.println("node.xtemp = " + node.xtemp);
//            }
//            if (node.leftChild != null && node.rightChild == null) {
//                node.xtemp = node.leftChild.xtemp + 1;
//            }
//            if (node.rightChild != null && node.leftChild == null) {
//                node.xtemp = node.rightChild.xtemp - 1;
//            }
//            if (node.xtemp < tempXPos.get(node.y)) {
//                node.xtemp = tempXPos.get(node.y);
//            }
//            tempXPos.put(node.y, tempXPos.get(node.y) + 2);
//        }
//    }
//
//    //step 3: position of subtrees ,get mindist of 2 subtrees: compare right conture of left & left conture of right subtree
//    public void getSubtreePositions(Node root) {
//        if (root != null && root.getChildren().size() > 1) {
//            getMinDist(root.leftChild, root.rightChild);
//            for (Node c : root.getChildren()) {
//                getSubtreePositions(c);
//            }
//        }
//    }
//
//    //threading auskommatiert
//    public void getMinDist(Node left, Node right) {
//        double scurr = (right.xtemp - left.xtemp);
//        if (scurr > 2 && scurr < minsep) {
//            minsep = scurr;
//        }
//        if (left.rightChild != null && right.leftChild != null) {
//            getMinDist(left.rightChild, right.leftChild);
//        }
//    }

    // offset wird so berechnet dass für links neg und für rechts pos-- stimmt das?
//    public void setCoords(Node left, Node right, Node root) {
//        cursep = minsep;
//        rootsep = minsep;
//        while (left != null && right != null) {
//            //left.offset = (left.xtemp - root.xtemp);
//            //right.offset = (right.xtemp - root.xtemp);
//            cursep = left.xtemp - right.xtemp;
//            if (cursep < minsep) {
//                rootsep = rootsep + (minsep - cursep);
//                cursep = minsep;
//            }
//            if (left.rightChild != null) {
//                loffsum = loffsum + left.offset;
//                cursep = cursep - left.offset;
//                left = left.rightChild;
//            } else {
//                loffsum = loffsum - left.offset;
//                cursep = cursep + left.offset;
//                left = left.leftChild;
//            }
//            if (right.leftChild != null) {
//                roffsum = roffsum - right.offset;
//                cursep = cursep - right.offset;
//                right = right.leftChild;
//            } else {
//                roffsum = roffsum + right.offset;
//                cursep = cursep + right.offset;
//                right = right.rightChild;
//            }
//        }
//
//        root.offset = (rootsep + 1) / 2;
//        loffsum = loffsum - root.offset;
//        roffsum = roffsum + root.offset;
//
//        //UPDATE EXTREME DESCENDANTS INFOREMATIOS
//        if (RL.y > LL.y || root.leftChild == null) {
//            lmost = RL;
//            lmost.offset += root.offset;
//        } else {
//            lmost = LL;
//            lmost.offset -= root.offset;
//        }
//        if (LR.y > RR.y || root.rightChild == null) {
//            rmost = LR;
//            rmost.offset -= root.offset;
//        } else {
//            rmost = RR;
//            rmost.offset += root.offset;
//        }
//
//        if (left != null && left != root.leftChild) {
//            RR.hasThread = true;
//            RR.offset = abs(RR.offset + root.offset - loffsum);
//            if (loffsum - root.offset <= RR.offset) {
//                RR.leftChild = left;
//            } else {
//                RR.rightChild = left;
//            }
//            if (right != null || right != root.rightChild) {
//                LL.hasThread = true;
//                LL.offset = abs(LL.offset - root.offset - roffsum);
//                if (roffsum + root.offset >= LL.offset) {
//                    LL.rightChild = right;
//                } else {
//                    LL.leftChild = right;
//                }
//            }
//        }
//    }
//

    // cursep = separation on current level
    // rootsep= current separation at node t - accumulates the required sepatation,
    // distance for the sons of root so that their subtrees will have a sepatation
    // of at least MINSEP at all levels
    // loffsum & roffsum = offset from l and r to t
    // Minsep = min separation allowed between two nodes on a level
    public void setup(Node root, int level, Node rmost, Node lmost) {
        // avoid selecting an extreme
        if (root == null) {
            lmost.level = -1;
            rmost.level = -1;
        } else {
            Node ll, lr, rr, rl;
            root.y = level;
            Node left = root.leftChild;  // follows contour of left subtree
            Node right = root.rightChild;  // follows contour of right subtree
            ll = getLL(root);
            if (left == null) {
                lr = root;
            } else {lr = getRR(left);}
            rr = getRR(root);
            if(right== null){
                rl= root;
            }else{rl= getLL(right);}

            setup(left, level + 1, lr, ll); // position subtrees recursively
            setup(right, level + 1, rr, rl); // position subtrees recusively
            if (right == null && left == null) { // leaf
                rmost = root;
                lmost = root;
                rmost.level = level;
                rmost.offset = 0;
                lmost.offset = 0;
                root.offset = 0;
            } // no leaf:
            // set up for subtree pushing, place roots of subtrees min dist apart
            cursep = minsep;
            rootsep = minsep;
            loffsum = 0;
            roffsum = 0;
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
                if (right != null && right != root.rightChild) {
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

    public void petrify(Node root, double xpos) {
        if (root != null) {
            root.x = xpos;
        }
        if (root.hasThread) {
            root.hasThread = false;
            root.rightChild = null;
            root.leftChild = null;
        }
        if (root.leftChild != null) {
            petrify(root.leftChild, xpos - roffsum);
        }
        if (root.rightChild != null) {
            petrify(root.rightChild, xpos + roffsum);
        }
    }


//    int findMaxLevel(Node root) {
//        if (root == null)
//            return 0;
//        return 1 + max((findMaxLevel(root.leftChild)), findMaxLevel(root.rightChild));
//    }

//    public void outerNodes(Node root) {
//        LL = getLL(root.leftChild);
//        LR = getRR(root.leftChild);
//        RL = getLL(root.rightChild);
//        RR = getRR(root.rightChild);
//    }

    public Node getLL(Node node) {
        while (node != null && !node.isLeaf()) {
            node = node.leftChild;
        }
        return node;
    }

    public Node getRR(Node node) {
        while (node != null && !node.isLeaf()) {
            node = node.rightChild;
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



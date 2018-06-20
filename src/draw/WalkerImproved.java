package draw;


import structure.*;
import java.util.List;
import java.util.ListIterator;

public class WalkerImproved {

    private static final double siblingSeparation = 3;
    private static final double subtreeSeparation = 4;
    private static final double levelSeparation = 2.5;
    private static double lowestCoord = 0;

    public static Tree processTreeNodes(Tree tree) {

        try {
            treeLayout(tree);
            return tree;
        } catch(Exception e){
            System.out.println("Error while running Walker Algorithm");
            System.out.println(e);
            return null;
        }
    }

    private static void moveSubtree(TreeNode conflictingAncestor, TreeNode node, double shift) {
        System.out.printf("!MoveSubtree called between %s and %s Shift: %f", conflictingAncestor.label, node.label, shift);
        double subtrees = (double)node.indexAsChild - conflictingAncestor.indexAsChild;
        System.out.println("number of subtrees: " + subtrees);
        node.change -= (shift / subtrees);
        node.shift += shift;
        conflictingAncestor.change += (shift/subtrees);
        node.prelim += shift;
        node.modifier += shift;
    }

    private static void executeShifts(TreeNode node) {
        double tempShift = 0;
        double tempChange = 0;
        ListIterator<TreeNode> tempIter = node.getChildren().listIterator(node.getChildren().size());
        while (tempIter.hasPrevious()) {
            TreeNode c = tempIter.previous();
            c.prelim += tempShift;
            c.modifier += tempShift;
            tempChange += c.change;
            tempShift += c.shift + tempChange;
        }
    }

    private static TreeNode nextLeft(TreeNode node) {
        System.out.printf("nextLeft called for: %s\n", node.label);
        try {
            if (node.isLeaf()) {
                System.out.println("nextLeft: " + node.thread.label);
                return node.thread;
            } else {
                System.out.println("nextLeft: " + node.getChild(0).label);
                return node.getChild(0);
            }
        } catch (Exception e) {
            System.out.println("End of contour");
            return null;
        }

    }

    private static TreeNode nextRight(TreeNode node) {
        System.out.printf("nextRight called for: %s\n", node.label);
        try {
            if (node.isLeaf()) {
                System.out.println("nextRight: " + node.thread.label);
                return node.thread;
            } else {
                System.out.println("nextRight: " + node.getChild(-1).label);
                return node.getChild(-1);
            }
        } catch (Exception e) {
            System.out.println("End of contour");
            return null;
        }

    }

    private static TreeNode findAncestor(TreeNode leftNode, TreeNode currentNode, TreeNode defaultAncestor) {
        System.out.printf("findAncestor called for: %s %s  %s\n", leftNode.label, currentNode.label, defaultAncestor.label);
        System.out.println("ancestor of left node: " + leftNode.ancestor.label);
        System.out.println("ancestor of current Node: " + currentNode.parent.label);
        if (leftNode.ancestor.parent.equals(currentNode.parent)) {
            System.out.println("Ancestor: " + leftNode.ancestor.label);
            return leftNode.ancestor;
        } else {
            System.out.println("DefaultAncestor: " + defaultAncestor.label);
            return defaultAncestor;
        }
    }

    private static void clearCoords(TreeNode node) {
        node.ancestor = node;
        node.thread = null;
        node.modifier = 0;
        node.prelim = 0;
        node.shift = 0;
        node.change = 0;
        node.x = 0;
        node.y = 0;
        node.getChildren().forEach((n) -> {
            clearCoords(n);
        });
    }

    private static void findLowest(TreeNode root){
        if (root.x<lowestCoord) lowestCoord=root.x;
        if (!root.isLeaf()) {
            for (TreeNode node : root.getChildren()) {
                findLowest(node);
            }
        }
    }

    private static void shiftIntoPane(TreeNode root){
        root.x -= lowestCoord;
        if (!root.isLeaf()) {
            for (TreeNode node : root.getChildren()) {
                shiftIntoPane(node);
            }
        }
    }

    public static void treeLayout(Tree tree) throws Exception {
        List<TreeNode> roots = tree.getRoots();
        System.out.println("Roots found: " + roots.size() + " as following: " + roots);
        if (roots.size() == 1) {
            TreeNode root = roots.get(0);
            System.out.println("Tree:\n" + tree);
            clearCoords(root);
            lowestCoord = 0;
            System.out.println("Doing First walk Now");
            firstWalk(root, 0);
            System.out.println("Tree:\n" + tree);
            System.out.println("Doing second walk now");
//            secondWalk(root, 0, -root.prelim);
            secondWalk(root, 0, 0);
            findLowest(root);
            System.out.println("------------------Lowest Coordinate: " + lowestCoord);
            System.out.println("Second walk completed");
            shiftIntoPane(root);
            System.out.println("Tree:\n" + tree);
            //return tree;
        } else {
            if (roots.isEmpty()) {
                throw new Exception("No root found");
            } else {
                //throw new Exception("More than one root was found");
                System.out.println("ATTENTION: more than root found, proceeding for all roots");
                for (TreeNode root : roots) {
                    tree.listAllNodes().forEach((TreeNode n) -> {
                        n.modifier = 0;
                        n.thread = null;
                        n.ancestor = n;
                    });
                    firstWalk(root, 0);
                    secondWalk(root, 0,0);
                }
                //return tree;
            }
        }

    }

    private static void firstWalk(TreeNode node, int level) {

        TreeNode defaultAncestor = null;
        double midPoint;
        if (node.isLeaf()) {
            System.out.println("Node is leaf: " + node.label);
            if (node.hasLeftSibling()) {
//                System.out.println("model.TreeNode has left sibling");
                try {
                    node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation; /*+ Meannodesize(leftsibling, node)*/
                } catch (Exception e) {
                    System.out.println("problem with parent.getChild");
                    node.prelim = 0;
                }
            } else {
                node.prelim = 0;
            }

        } else {
            defaultAncestor = node.getChild(0);
            for (TreeNode child : node.getChildren()) {
                firstWalk(child, level + 1);
                //System.out.println("First walk for node completed: " + node.label);
                defaultAncestor = apportion(child, level, defaultAncestor);
                //System.out.println("Apportion for node completed: " + node.label);
            }
            System.out.println("ready to execute shifts: " + node.label);
            executeShifts(node);
            midPoint = (node.getChild(0).prelim + node.getChild(-1).prelim) / 2.0;
            System.out.println("Midpoint for: " + node.label + " is: " + midPoint);
            if (node.hasLeftSibling()) {
//                System.out.println("model.TreeNode has left sibling");
                try {
                    node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation;
                } catch (Exception e) {
                    System.out.println("problme with hasLeftSibling in firstwalk");
                    node.prelim = 0;
                }
                node.modifier = node.prelim - midPoint;
//                System.out.println("model.TreeNode prelim set to: " + node.prelim);
            } else {
//                System.out.println("model.TreeNode has no left sibling");
                node.prelim = midPoint;
            }
        }
    }

    private static void secondWalk(TreeNode node, int level, double modsum) {
//        System.out.println("Second walk called, modsum: " + modsum);
        node.x = node.prelim + modsum;
        node.y = level * levelSeparation;
        if (!node.isLeaf()) {
            node.getChildren().forEach((c) -> {
                secondWalk(c, level + 1, modsum + node.modifier);
            });
        }
    }

    private static TreeNode apportion(TreeNode node, int level, TreeNode defaultAncestor) {
        TreeNode vInPLus = null;

        TreeNode vInMinus = null, vOutPlus = null, vOutMinus = null;
        double sumInPlus = 0, sumInMinus = 0, sumOutPlus = 0, sumOutMinus = 0;
        double shift;
        if (node.hasLeftSibling()) {
            System.out.println("Doing apportion for: " + node.label + " with defaultAncestor: " + defaultAncestor.label);
            vInPLus = node;
            vOutPlus = node;
            vInMinus = node.parent.getChild(node.indexAsChild - 1);
            vOutMinus = vInPLus.parent.getChild(0);
            sumInPlus = vInPLus.modifier;
            sumOutPlus = vOutPlus.modifier;
            sumInMinus = vInMinus.modifier;
            sumOutMinus = vOutMinus.modifier;
            while (nextRight(vInMinus) != null && nextLeft(vInPLus) != null) {
                System.out.println("traversing next level");
                vInMinus = nextRight(vInMinus);
                vInPLus = nextLeft(vInPLus);
                vOutMinus = nextLeft(vOutMinus);
                vOutPlus = nextRight(vOutPlus);
                System.out.printf("vom/vim/vip/vop: %s  %s  %s  %s\n", vOutMinus.label, vInMinus.label, vInPLus.label, vOutPlus.label);
                vOutPlus.ancestor = node;
                shift = (vInMinus.prelim + sumInMinus) - (vInPLus.prelim + sumInPlus) + subtreeSeparation;
                if (shift > 0) {
                    System.out.println("Shift in apportion is positive at nodes: " + "\t" + vInMinus.label + " " + (vInMinus.prelim + sumInMinus) + "\t" + vInPLus.label + " " + (vInPLus.prelim + sumInPlus));
                    moveSubtree(findAncestor(vInMinus, node, defaultAncestor), node, shift);
                    sumInPlus += shift;
                    sumOutPlus += shift;
                }
                sumInMinus += vInMinus.modifier;
                sumInPlus += vInPLus.modifier;
                sumOutMinus += vOutMinus.modifier;
                sumOutPlus += vOutPlus.modifier;
            }

            if (nextRight(vInMinus) != null && nextRight(vOutPlus) == null) {

                vOutPlus.thread = nextRight(vInMinus);
                vOutPlus.modifier = vOutPlus.modifier + sumInMinus - sumOutPlus;
                System.out.println("Setting up thread from/to/modifier: " + vOutPlus.label + "\t" + vOutPlus.thread.label + "\t" + vOutPlus.modifier);
            }
            if (nextLeft(vInPLus) != null && nextLeft(vOutMinus) == null) {
                vOutMinus.thread = nextLeft(vInPLus);
                vOutMinus.modifier += sumInPlus - sumOutMinus;
                System.out.println("Setting up thread from/to/modifier + default ancestor: " + vOutMinus.label + "\t" + vOutMinus.thread.label + "\t" + vOutMinus.modifier);
                return node; //set default ancestor for kids on the right
            }}
        return defaultAncestor;
    }

}
package draw;

import model.*;
import java.util.List;
import java.util.ListIterator;

public class WalkerImprovedDraw {

    private static int maxDepth = Integer.MAX_VALUE;
    private static double xTopAdjust;
    private static double xTemp;
    private static int levelSeparation;
    private static int yTopAdjust;
    private static int yTemp;
    private static final double siblingSeparation = 3;
    private static final double subtreeSeparation = 4;

    public static Tree processTreeNodes(Tree tree) {

        try {
            WalkerImprovedDraw w = new WalkerImprovedDraw();
            tree = w.treeLayout(tree);
//            String toProcess = tree.getRoots().toString();
//            String[] splittedTree = toProcess.substring(1,toProcess.length()-1).split(";");
////            for (String node: splittedTree){
////                System.out.println("node = " + node);
////            }
            return tree;
        } catch(Exception e){
            System.out.println("Error while running Walker Algorithm");
            System.out.println(e);
            return null;
        }
    }

    private static void moveSubtree(Node conflictingAncestor, Node node, double shift) {
        int subtrees = node.indexAsChild - conflictingAncestor.indexAsChild;
        node.change -= (shift / subtrees);
        node.shift += shift;
        conflictingAncestor.change += (shift / subtrees);
        node.prelim += shift;
        node.modifier += shift;
    }

    private static void executeShifts(Node node) {
        double tempShift = 0;
        double tempChange = 0;
        ListIterator<Node> tempIter = node.getChildren().listIterator(node.getChildren().size());
        while (tempIter.hasPrevious()) {
            Node c = tempIter.previous();
            c.prelim += tempShift;
            c.modifier += tempShift;
            tempChange += c.change;
            tempShift += c.shift + tempChange;
        }
    }

    private static Node nextLeft(Node node) {
        try {
            if (node.isLeaf()) {
                return node.thread;
            } else {
                return node.getChild(0);
            }
        } catch (Exception e) {
//            System.out.println("End of contour");
            return null;
        }

    }

    private static Node nextRight(Node node) {
//        System.out.println("nextright called");
        try {
            if (node.isLeaf()) {
                return node.thread;
            } else {
                return node.getChild(-1);
            }
        } catch (Exception e) {
//            System.out.println("End of contour");
            return null;
        }

    }

    private static Node findAncestor(Node leftNode, Node currentNode, Node defaultAncestor) {
        if (leftNode.parent == currentNode.parent) { //geht das, oder muss man .equals benutzen?
            return leftNode.ancestor;
        } else {
            return defaultAncestor;
        }
    }


    public Tree treeLayout(Tree tree) throws Exception {
        List<Node> roots = tree.getRoots();
        System.out.println("Roots found: " + roots.size() + " as following: " + roots);
        if (roots.size() == 1) {
            Node root = roots.get(0);
            System.out.println("Tree:\n" + tree);
            tree.listAllNodes().forEach((Node n) -> {
                n.modifier = 0;
                n.thread = null;
                n.ancestor = n;
            });
//            System.out.println("Doing First walk Now");
            firstWalk(root, 0);
//            System.out.println("Doing second walk now");
//            secondWalk(root, 0, -root.prelim);
            secondWalk(root, 0,0);
//            System.out.println("Second walk completed");
            return tree;
        } else {
            if (roots.isEmpty()) {
                throw new Exception("No root found");
            } else {
                //throw new Exception("More than one root was found");
                System.out.println("ATTENTION: more than root found, proceeding for all roots");
                for (Node root : roots) {
                    tree.listAllNodes().forEach((Node n) -> {
                        n.modifier = 0;
                        n.thread = null;
                        n.ancestor = n;
                    });
                    firstWalk(root, 0);
                    secondWalk(root, 0,0);
                }
                return tree;
            }
        }

    }

    private static void firstWalk(Node node, int level) {

        Node defaultAncestor = null;
        double midPoint;
        if (node.isLeaf() || level == maxDepth) {
//            System.out.println("model.Node is a leaf: " + node);
            if (node.hasLeftSibling()) {
//                System.out.println("model.Node has left sibling");
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
            for (Node child : node.getChildren()) {
                firstWalk(child, level + 1);
//                System.out.println("First walk for node completed: " + node.label);
                try {
                    apportion(child, level, defaultAncestor);
                } catch (Exception e) {
                    System.out.println("problme with apportion for model.Node: " + child.label + ", with defaultAncestor: " + defaultAncestor);
                }
//                System.out.println("Apportion for node completed: " + node.label);
            }
//            System.out.println("ready to execute shifts");
            executeShifts(node);
            midPoint = (node.getChild(0).prelim + node.getChild(-1).prelim) / 2;
            if (node.hasLeftSibling()) {
//                System.out.println("model.Node has left sibling");
                try {
                    node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation;
                } catch (Exception e) {
                    System.out.println("problme with hasLeftSibling in firstwalk");
                    node.prelim = 0;
                }
                node.modifier = node.prelim - midPoint;
//                System.out.println("model.Node prelim set to: " + node.prelim);
            } else {
//                System.out.println("model.Node has no left sibling");
                node.prelim = midPoint;
            }
        }
    }

    private static void secondWalk(Node node, int level, double modsum) {
//        System.out.println("Second walk called, modsum: " + modsum);
        node.x = node.prelim + modsum;
        node.y = level * 3;
        if (!node.isLeaf()) {
            node.getChildren().forEach((c) -> {
                secondWalk(c, level + 1, modsum + node.modifier);
            });
        }
    }

    private static void apportion(Node node, int level, Node defaultAncestor) {
        
        Node vInPLus = null, vInMinus = null, vOutPlus = null, vOutMinus = null;
        double sumInPlus = 0, sumInMinus = 0, sumOutPlus = 0, sumOutMinus = 0;
        double shift;
        if (node.hasLeftSibling()) {
            vInPLus = node;
            vOutPlus = node;
            vInMinus = node.parent.getChild(node.indexAsChild - 1);
            vOutMinus = vInPLus.parent.getChild(0);
            sumInPlus = vInPLus.modifier;
            sumOutPlus = vOutPlus.modifier;
            sumInMinus = vInMinus.modifier;
            sumOutMinus = vOutMinus.modifier;
            while (nextRight(vInMinus) != null && nextLeft(vInPLus) != null) {
                vInMinus = nextRight(vInMinus);
                vInPLus = nextLeft(vInPLus);
                vOutMinus = nextLeft(vOutMinus);
                vOutPlus = nextRight(vOutPlus);
                vOutPlus.ancestor = node;
                shift = (vInMinus.prelim + sumInMinus) - (vInPLus.prelim + sumInPlus) + subtreeSeparation; 
                if (shift > 0) {
                    moveSubtree(findAncestor(vInMinus, node, defaultAncestor), node, shift);
                    sumInPlus += shift;
                    sumOutPlus += shift;
                }
                sumInMinus += vInMinus.modifier;
                sumInPlus += vInPLus.modifier;
                sumOutMinus += vOutMinus.modifier;
                sumOutPlus += vOutPlus.modifier;
            }
        }
        if (nextRight(vInMinus) != null && nextRight(vOutPlus) == null) { 
            vOutPlus.thread = nextRight(vInMinus);
            vOutPlus.modifier += sumInMinus - sumOutPlus;
        }
        if (nextLeft(vInPLus) != null && nextLeft(vOutMinus) == null) {
            vOutMinus.thread = nextLeft(vInPLus);
            vOutMinus.modifier += sumInPlus - sumOutMinus;
            defaultAncestor = node;
        }
    }

}
package draw;

import model.Node;
import model.Tree;

public class B_Plus {
    
    private static int leaveCoordCounter;
    private static int lowestlevel;
    private static final double levelSeparation = 2;
    private static final double nodeSeparation = 2;
    
    public static Tree processTree(Tree tree) {
        try {
            Node root = tree.getRoot();
            lowestlevel = 0;
            setLevel(root, 0);
            dropLeaves(root);
            leaveCoordCounter = 0;
            setX(root);
            //scale()?
            return tree;
           }
        catch (Exception e) {
            System.out.println("Error while running naive B_Plus drawing algorithm");
            e.printStackTrace();
            return null;
        }
    }

    private static void setLevel(Node root, int level) {
        root.y = level * levelSeparation;
        if (lowestlevel<level) lowestlevel = level;
        if (!root.isLeaf()) {
            root.getChildren().forEach((c) -> {setLevel(c, level+1);});
        }
    }

    private static void dropLeaves(Node root) {
        if (root.isLeaf()) {
            root.y = lowestlevel * levelSeparation;
        } else {
            root.getChildren().forEach((c) -> {dropLeaves(c);});
        }
    }

    private static void setX(Node root) {
        if (root.isLeaf()) {
            //set to next available coordinate
            root.x = leaveCoordCounter * nodeSeparation;
            leaveCoordCounter++;
        } else {
            root.getChildren().forEach((child) -> {
                //Post-order from left to right
                setX(child);
            });
            root.x = (root.getChild(0).x + root.getChild(-1).x)/2.0;
        }
    }
    
}

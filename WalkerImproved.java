import java.util.List;

public class WalkerImproved {

    private static int maxDepth;
    private static double xTopAdjust;
    private static double xTemp;
    private static int levelSeparation;
    private static int yTopAdjust;
    private static int yTemp;
    private static MappedTreeStructure tree; //hand over in walkerMain
    private static double siblingSeparation;
    private static double subtreeSeparation;
    
    /* walker old
    private static void initPrevNodeList(){
        
    }
    
    private static Node getPrevNodeAtLevel(int level){
        Node node = new Node("");
        return node;
    }
    
    private static void setPrevNodeAtLevel(int level, Node node){
        
    }
    */
    
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
        for (Node c : node.getChildren()) {
            c.prelim += tempShift;
            c.modifier += tempShift;
            tempChange += c.change;
            tempShift += c.shift + tempChange; 
        }
    }
    
    private static Node nextLeft(Node node) {
        if (node.isLeaf()) {
            return node.thread;
        } else {
            return node.getChild(0);
        }
    }
    
    private static Node nextRight(Node node) {
        if (node.isLeaf()) {
            return node.thread;
        } else {
            return node.getChild(-1);
        }
    }
    
    private static Node findAncestor(Node leftNode, Node currentNode, Node defaultAncestor) {
        if (leftNode.parent == currentNode.parent) { //geht das, oder muss man .equals benutzen?
            return leftNode.ancestor;
        } else {
            return defaultAncestor;
        }
    }
    
    public static void treeLayout(MappedTreeStructure<Node> tree) throws Exception {
        List<Node> roots = tree.getRoots();
        if (roots.size() == 1) {
            Node root = roots.get(0);
            tree.listAllNodes().forEach((Node n) -> {
                n.modifier = 0;
                //n.thread = 0;
                n.ancestor = n;
            });
            firstWalk(root, 0);
            secondWalk(root, 0, -root.prelim);
            
        } else {
            if (roots.isEmpty()) {
                throw new Exception("No root found");
            } else {
                throw new Exception("More than one root was found");
            }
}
                
    }
    
    private static void firstWalk(Node node,int level){
        //Node leftSibling = null; //braucht Funktion um linkes Geschwister zu finden, zB mit ChildNumber beim bauen
        Node defaultAncestor = null;
        double midPoint;
        /* walker old
        node.leftNeighbor = getPrevNodeAtLevel(level);
        setPrevNodeAtLevel(level, node);
        node.modifier = 0;
        */
        if (node.isLeaf() || level == maxDepth) {
            
            node.prelim = 0;
            /* walker old
            if (node.hasLeftSibling()) {
                node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation + Meannodesize(leftsibling, node);
            } else {
                node.prelim = 0;
            }
            */
        } else {
            
            defaultAncestor = node.getChild(0);
            for (Node child : node.getChildren()) {
                firstWalk( child, level +1);
                apportion(child, level, defaultAncestor);
            }
            executeShifts(node);
            midPoint = (node.getChild(0).prelim + node.getChild(-1).prelim)/2;
            if (node.hasLeftSibling()) {
                node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation;
                node.modifier = node.prelim - midPoint;
            } else {
                node.prelim = midPoint;
            }

            /* walker old
            node.getChildren().forEach((child) -> {
                firstWalk(child, level+1);});
            midPoint = (node.getChild(0).prelim + node.getChild(-1).prelim)/2;
            if (node.hasLeftSibling()) {
                node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation + Meannodesize(leftsibling, node);
                node.modifier = node.prelim - midPoint;
                apportion(node, level);
            } else {
                node.prelim = midPoint;
            } */
        }
        
    }
    
    private static void secondWalk(Node node, int level, double modsum) /*besonderer Exception Name? */ {
        /* walker old
        if (level <= maxDepth){
            xTemp = xTopAdjust + node.prelim + modsum;
            yTemp = yTopAdjust + ( level * levelSeparation );
            //checkBoundary(xTemp, yTemp); //throws outOfDrawingSpaceException oder sowas zum catchen in WalkerMain
            node.x = xTemp;
            node.y = yTemp;
            node.getChildren().forEach((child) -> {
                secondWalk(child, level + 1, modsum + node.modifier);
            });
        }*/
        node.x = node.prelim + modsum;
        node.y = level;
        if (!node.isLeaf()) {
            node.getChildren().forEach((c) -> {
                secondWalk(c, level +1, modsum + node.modifier);
            });
        }
    }
      
    private static void apportion(Node node, int level, Node defaultAncestor) {
        /* walker old
        Node leftmost, neighbor, ancestorLeft, ancestorNeighbor, temp;
        int compareDepth = 1;
        int depthToStop = maxDepth - level;
        double leftModsum, rightModsum, moveDistance, portion;
        int leftSiblings;
        
        try {
            leftmost = node.getChild(0); 
            neighbor = leftmost.leftNeighbor;   
        } catch (Exception e) {
            leftmost = null;
            neighbor = null;
        }
                
        while (leftmost != null && neighbor != null && compareDepth <= depthToStop){
            leftModsum = 0;
            rightModsum = 0;
            ancestorLeft = leftmost;
            ancestorNeighbor = neighbor;
            for (int i = 0; i == compareDepth; i++) {
                ancestorLeft = ancestorLeft.parent;
                ancestorNeighbor = ancestorNeighbor.parent;
                rightModsum = rightModsum + ancestorLeft.modifier;
                leftModsum = leftModsum +ancestorNeighbor.modifier;
            }
            moveDistance = neighbor.prelim + leftModsum + subtreeSeparation + Meannodesize(leftsibling, node) - (leftmost.prelim + rightModsum);
            if (moveDistance > 0) {
                temp = node;
                leftSiblings = 0;
                while(temp != null && temp != ancestorNeighbor){ //durch Buchheim Jünger Leipert Zeug zu ersetzen
                    leftSiblings++;
                    //temp = leftSibling(temp);
                }
                if (temp != null){
                    portion = moveDistance/leftSiblings;
                    temp = node;
                    while (true) {
                        // hier Verbesserung zum verschieben der Teilbäume einfügen
                    }
                } else {
                    return;
                }
            } 
            compareDepth++;
            if (leftmost.isLeaf()) {
                //leftmost = getLeftMost; Verbesserung einfügen
            } else {
                leftmost = leftmost.getChild(0);
            }
        }*/
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
                shift = (vInMinus.prelim + sumInMinus) - (vInPLus.prelim + sumInPlus) + subtreeSeparation; //? richtige Distanz
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
        if (nextRight(vInMinus) != null && nextRight(vOutPlus) == null) { //isNull ?
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

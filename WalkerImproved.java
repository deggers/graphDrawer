
public class WalkerImproved {

    private static int maxDepth;
    private static double xTopAdjust;
    private static double xTemp;
    private static int levelSeparation;
    private static int yTopAdjust;
    private static int yTemp;
    private static MappedTreeStructure<Node> tree; //hand over in walkerMain
    private static double siblingSeparation;
    private static double subtreeSeparation;
    
    //variablen
    
    private static void initPrevNodeList(){
        
    }
    
    private static Node getPrevNodeAtLevel(int level){
        Node node = new Node("");
        return node;
    }
    
    private static void setPrevNodeAtLevel(int level, Node node){
        
    }
    
    private static boolean hasChild(Node node){
        return true;
    }
    
    private static void firstWalk(Node node,int level){
        Node leftSibling = null; //braucht Funktion um linkes Geschwister zu finden, zB mit ChildNumber beim bauen
        double midPoint;
        
        node.leftNeighbor = getPrevNodeAtLevel(level);
        setPrevNodeAtLevel(level, node);
        node.modifier = 0;
        if (node.isLeaf() || level == maxDepth) {
            
            if (node.hasLeftSibling()) {
                node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation /*+ Meannodesize(leftsibling, node)*/;
            } else {
                node.prelim = 0;
            }
            
        } else {

            node.getChildren().forEach((child) -> {
                firstWalk(child, level+1);});
            midPoint = (node.getChild(0).prelim + node.getChild(-1).prelim)/2;
            if (node.hasLeftSibling()) {
                node.prelim = node.parent.getChild(node.indexAsChild - 1).prelim + siblingSeparation /*+ Meannodesize(leftsibling, node)*/;
                node.modifier = node.prelim - midPoint;
                apportion(node, level);
            } else {
                node.prelim = midPoint;
            }
        }
        
    }
    
    private static void /* bool? */ secondWalk(Node node, int level, double modsum) /*besonderer Exception Name? */ {
        if (level <= maxDepth){
            xTemp = xTopAdjust + node.prelim + modsum;
            yTemp = yTopAdjust + ( level * levelSeparation );
            //checkBoundary(xTemp, yTemp); //throws outOfDrawingSpaceException oder sowas zum catchen in WalkerMain
            node.x = xTemp;
            node.y = yTemp;
            node.getChildren().forEach((child) -> {
                secondWalk(child, level + 1, modsum + node.modifier);
            });

        }
    }
      
    private static void apportion(Node node, int level) {
        
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
            moveDistance = neighbor.prelim + leftModsum + subtreeSeparation /*+ Meannodesize(leftsibling, node)*/ - (leftmost.prelim + rightModsum);
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
        }
    }
    
    
}

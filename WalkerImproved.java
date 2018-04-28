/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.util.List;

/**
 *
 * @author gross
 */
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
        
        node.setLeftNeighbor(getPrevNodeAtLevel(level));
        setPrevNodeAtLevel(level, node);
        node.setModifier(0);
        if (tree.hasChildren(node) || level == maxDepth) { //hasChildren = isLeaf
            List<Node> children = tree.getChildren(node);
            if (true /*hasLeftSibling*/) {
                node.setPrelim(leftSibling.getPrelim() + siblingSeparation /*+ Meannodesize(leftsibling, node)*/);
            } else {
                node.setPrelim(0);
            }
           
        } else {
            List<Node> children = tree.getChildren(node);
            children.forEach((child) -> {
                firstWalk(child, level+1);});
            midPoint = (children.get(0).getPrelim()+children.get(children.size()).getPrelim())/2;
            if (true /*hasLeftSibling*/) {
                node.setPrelim(leftSibling.getPrelim() + siblingSeparation /*+ Meannodesize(leftsibling, node)*/);
                node.setModifier(node.getPrelim() - midPoint);
                apportion(node, level);
            } else {
                node.setPrelim(midPoint);
            }
        }
        
    }
    
    private static void /* bool? */ secondWalk(Node node, int level, double modsum) /*besonderer Exception Name? */ {
        if (level <= maxDepth){
            xTemp = xTopAdjust + node.getPrelim() + modsum;
            yTemp = yTopAdjust + ( level * levelSeparation );
            //checkBoundary(xTemp, yTemp); //throws outOfDrawingSpaceException oder sowas zum catchen in WalkerMain
            node.setX(xTemp);
            node.setY(yTemp);
            tree.getChildren(node).forEach((child) -> {
                secondWalk(child, level + 1, modsum + node.getModifier());
            });

        }
    }
      
    private static void apportion(Node node, int level) {
        Node leftmost, neighbor, ancestorLeft, ancestorNeighbor, temp;
        try {
            leftmost = tree.getChildren(node).get(0); 
            neighbor = leftmost.getLeftNeighbor();   
        } catch (Exception e) {
            leftmost = null;
            neighbor = null;
        }
        int compareDepth = 1;
        int depthToStop = maxDepth - level;
        double leftModsum, rightModsum, moveDistance, portion;
        int leftSiblings;
        
        while (leftmost != null && neighbor != null && compareDepth <= depthToStop){
            leftModsum = 0;
            rightModsum = 0;
            ancestorLeft = leftmost;
            ancestorNeighbor = neighbor;
            for (int i = 0; i == compareDepth; i++) {
                ancestorLeft = tree.getParent(ancestorLeft);
                ancestorNeighbor = tree.getParent(ancestorNeighbor);
                rightModsum = rightModsum + ancestorLeft.getModifier();
                leftModsum = leftModsum +ancestorNeighbor.getModifier();
            }
            moveDistance = neighbor.getPrelim() + leftModsum + subtreeSeparation /*+ Meannodesize(leftsibling, node)*/ - (leftmost.getPrelim() + rightModsum);
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
            if (tree.hasChildren(leftmost)) {
                //leftmost = getLeftMost; Verbesserung einfügen
            } else {
                leftmost = tree.getChildren(leftmost).get(0);
            }
        }
    }
    
    
}

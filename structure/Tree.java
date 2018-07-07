package structure;
import java.util.*;
// inspired heavely from https://stackoverflow.com/questions/3522454/java-tree-data-structure

public class Tree {

    public final LinkedHashSet<TreeNode> nodeList = new LinkedHashSet<>();
    boolean yValuesHasBeenSet = false;
    private int treeDepth = 0;

    public Tree(){
    }

    public Tree(TreeNode root) {
        nodeList.add(root); // set root-node
        fillTree(root);     // set all .parent values from .children lists, set indexAsChild, fill nodeList
        setLevels(root, 0); // set all level-values
    }

    public LinkedList<TreeNode> getNodesFromLevel(int level) {
        LinkedList<TreeNode> levelList = new LinkedList<>();
        if (!this.yValuesHasBeenSet) setLevels(getRoot(), 0); // eig unnötig, aber failsave -dustyn
        for (TreeNode node : nodeList)
            if (node.level == level) {
                levelList.add(node);
                System.out.println("node.y = " + node.y + " , level: " + level);
                System.out.println("levelList = " + levelList);
            }
        return levelList;
    }

    private void setLevels(TreeNode node, int level) {
        node.level = level;
        if (level > this.treeDepth) this.treeDepth = level;
        for (TreeNode child : node.getChildren())
            setLevels(child, level + 1);
        this.yValuesHasBeenSet = true;
    }


    private void fillTree(TreeNode node) {
        nodeList.add(node);
        //System.out.println("added: " + node.label);
        try {
            int indexAsChildSetter = 0;
            for (TreeNode child : node.getChildren()) {
                child.parent = node;
                child.indexAsChild = indexAsChildSetter;
                indexAsChildSetter++;
                //System.out.println("added pair (n/p): " + child + e);
                fillTree(child);
            }
        } catch (Exception e) {
        }
    }

    private void checkNotNull(TreeNode node, String parameterName) {
        if (node == null)
            throw new IllegalArgumentException(parameterName + " must not be null");
    }

    public boolean remove(TreeNode node, boolean cascade) { //nötig bei uns?

        checkNotNull(node, "node");

        if (!nodeList.contains(node)) {
            return false;
        }
        if (cascade) {
            node.getChildren().forEach((child) -> {
                remove(child, true);                    //löscht keine Einträge aus nodeParent!
            });
        } else {
            node.getChildren().forEach((child) -> {
                //nodeParent.remove(child);            // hier löscht er doch nodeParent ?! -dustyn
            });
        }
        nodeList.remove(node);
        return true;
    }


    public List<TreeNode> getRoots() {
        List<TreeNode> roots = new LinkedList<>();
        for (TreeNode n : nodeList) {
            if (n.parent == null) {
                roots.add(n);
            }
        }
        return roots;
    }

    public TreeNode getRoot() {
        return nodeList.iterator().next();
    }

    public List<TreeNode> listAllNodes() {
        //System.out.println("List of all nodes returned");
        return new LinkedList<>(nodeList);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<TreeNode> roots = getRoots();
        if (roots.size()!=1){
            System.out.println("Warning: more than one root found, toString might not work as expected");
        }
        dumpNodeStructure(builder, roots.get(0), "- ");
        return builder.toString();
    }

    private void dumpNodeStructure(StringBuilder builder, TreeNode node, String prefix) {
        if (node != null) {
            builder.append(prefix);
            builder.append(node.toString());
            builder.append('\n');
            prefix = "    " + prefix;
        }
        for (TreeNode child : node.getChildren()) {
            dumpNodeStructure(builder, child, prefix);
        }
    }

    public String echoContent() {
        return nodeList.toString(); //+ "\n" + nodeParent.toString();
    }

    public int getTreeDepth() {
        return this.treeDepth;
    }

    public boolean setNodeCoords(TreeNode node, int x, int y) {
        boolean found = false;
        for (TreeNode lookupNode : nodeList)
            if (lookupNode.equals(node)) {
//                System.out.println("found matching node! look.");
//                System.out.println(nodeTmp);
//                System.out.println("with");
//                System.out.println(node);
                node.x = x;
                node.y = y;
                found = true;
//                System.out.println("Coords now: ");
//                System.out.println(node.x + ", " + node.y);
//                System.out.println("");
            }
        if (!found) System.out.println("setNodeX failed");
        return found;
    }

    public int getLeavesOfNode(TreeNode node) {
        int innerCounter = 0;
        if (!node.isLeaf()) {
            for (TreeNode rec : node.getChildren()) {
                if (rec.isLeaf()) {
                    innerCounter++;
                } else {
                    innerCounter += getLeavesOfNode(rec);
                }
            }
            return innerCounter;
        } else {
            return 1;
        }
    }

    public boolean isBinary(){
        TreeNode root = this.getRoot();
        return checkBinary(root);
    }

    private boolean checkBinary(TreeNode node){
        if(node.getChildren().size() >2){
            return false;
        }
        for(TreeNode n: node.getChildren()){
            boolean binary = checkBinary(n);
            if (!binary) {
                return false;
            }
        }
        return true;
    }

}



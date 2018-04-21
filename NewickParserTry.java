import java.util.ArrayList;
import java.util.Arrays;

public class NewickParserTry {

    private static int node_uuid = 0;
    private Node root;
    public  ArrayList<Node> nodeList = new ArrayList<>();

    static NewickParserTry readNewickFormat(String newick) {
        return new NewickParserTry().innerReadNewickFormat(newick);
    }

    private NewickParserTry innerReadNewickFormat(String newick) {
        this.root = readSubtree(newick.substring(0, newick.length() - 1));
//        System.out.println("root = " + root);
//        System.out.println("nodeList = " + Arrays.toString(nodeList.toArray()));
        return this;
    }

    private Node readSubtree(String s) {
//        System.out.println("s = " + s);
        int leftBracket = s.indexOf('(');
        int rightBracket = s.lastIndexOf(')');

        if (leftBracket != -1 && rightBracket != -1) {  // means, found two brackets
            Node node = new Node(s.substring(rightBracket + 1));
            node.children = new ArrayList<>();

            String[] childrenString = split(s.substring(leftBracket + 1, rightBracket));
//            System.out.println("Arrays.toString(childrenString = " + Arrays.toString(childrenString));
            for (String sub : childrenString) {
                Node child = readSubtree(sub);
                node.children.add(child);
                child.parent = node;
//                System.out.println("child.parent = " + child.parent);
            }
            nodeList.add(node);
//            System.out.println("node = " + node);
            return node;
        } else if (leftBracket == rightBracket) {
            Node node = new Node(s);
            nodeList.add(node);
            return node;
        } else throw new RuntimeException("unbalanced ()'s");
    }


    public static String[] split(String s) {
        ArrayList<Integer> splitIndices = new ArrayList<>();
        int rightParenCount = 0;
        int leftParenCount = 0;

        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '(':
                    leftParenCount++;
                    break;
                case ')':
                    rightParenCount++;
                    break;
                case ',':
//                    System.out.println("comma at position = " + i);
//                    System.out.println("leftParenCount = " + leftParenCount);
//                    System.out.println("rightParenCount = " + rightParenCount);
                    if (leftParenCount == rightParenCount) {
                        splitIndices.add(i);
                    }
                    break;
            }
        }
//        System.out.println("splitIndices = " + splitIndices);
        int numSplits = splitIndices.size() + 1;
        String[] splits = new String[numSplits];

        if (numSplits == 1) {
            splits[0] = s;
        } else {
            splits[0] = s.substring(0, splitIndices.get(0));
            for (int i = 1; i < splitIndices.size(); i++) {
                splits[i] = s.substring(splitIndices.get(i - 1) + 1, splitIndices.get(i));
            }
            splits[numSplits - 1] = s.substring(splitIndices.get(splitIndices.size() - 1) + 1);
        }
        return splits;
    }

    @Override
    public String toString() {
        return root.toString() + ";";
    }

    static class Node {
        final String name;
        final float weight;
        boolean realName;
        ArrayList<Node> children;
        Node parent;

        /**
         * @param name name in "actualName:weight" format, weight defaults to zero if colon absent
         */
        Node(String name) {

            /* check if node have own name */
            int colonIndex = name.indexOf(':');
            String actualNameText;
            if (colonIndex == -1) {
                actualNameText = name;
                weight = 0;
            } else {
//                System.out.println("name = " + name);
                actualNameText = name.substring(0, colonIndex);
                weight = Float.parseFloat(name.substring(colonIndex + 1, name.length()));
            }

            /* check if node got name else give number and count up */
            if (actualNameText.equals("")) {
                this.realName = false;
                this.name = Integer.toString(node_uuid);
                node_uuid++;
            } else {
                this.realName = true;
                this.name = actualNameText;
            }
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node other = (Node) o;
            return this.name.equals(other.name);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (children != null && children.size() > 0) {
                sb.append("(");
                for (int i = 0; i < children.size() - 1; i++) {
                    sb.append(children.get(i).toString());
                    sb.append(",");
                }
                sb.append(children.get(children.size() - 1).toString());
                sb.append(")");
            }
            if (name != null) sb.append(this.getName());
            return sb.toString();
        }

        String getName() {
            if (realName)
                return name;
            else
                return "";
        }
    }
}


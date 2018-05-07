package model;

import controller.ParseController;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeParserNewick {
    private static int pseudoNode_id = 0;

    public static Tree parseFileToTree(File file) {
        try {
            Stream<String> lines = Files.lines(file.toPath());
            String newickString = lines.map(line -> line.replaceAll("\\s+", "")).collect(Collectors.joining()).trim();

            if (isValidFormat(newickString)) {
                Node root = buildTreeStructure(newickString);
                Tree tree = new Tree(root);
                return tree;
            } else {
                System.out.println("format for newick seems to be wrong, daaamn");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Node parseStringToTree(String newickString) {

        newickString = newickString.replace(" ", "");
        newickString = newickString.replace("\t", "");
        newickString = newickString.replace("\n", "");

        // check if string is valid format
        if (isValidFormat(newickString)) {
            // process it
            return buildTreeStructure(newickString);
        } else {

            System.out.println("format for newick seems to be wrong, daaamn");
            return null;
        }
    }

// Problem: es müssen noch die längen der kanten der pseudoknoten gespeichert werden!
    private static Node buildTreeStructure(String string) {

        try {  // try to find branch
            int rightPar = getClosingParenthesis(string);
            int nodeId = pseudoNode_id++;
            String toProcess = string.substring(1, rightPar);
            String[] splitArray = splitToBranches(toProcess);
            Node currentNode = new Node("");
            currentNode.label = Integer.toString(nodeId);
            for (String branch : splitArray) {
                Node child = buildTreeStructure(branch);
                currentNode.addChild(child);

//                if(currentNode.children.isEmpty()){ // klappt nicht, wird immer right da
//                    // jeder knoten alle unter sich mit in children hat
//                    currentNode.leftChild= child;
//                    System.out.println("left = " + child);
//                } else {
//                    currentNode.rightChild= child;
//                    System.out.println("right = " + child);
//                }

            }
            return currentNode;
        } catch (IllegalArgumentException e) {
//            System.out.println("i guess we have a leaf here");

            // problem: if node has name but is no leaf saved as label= id instead of label= name
            Node node = new Node("");
            String[] nameSplit = string.split(":");
            node.label = nameSplit[0];
            node.weight= Double.parseDouble(nameSplit[1]);
            return node;
        }
    }

    private static int getClosingParenthesis(final String strng) {
        if (!strng.trim().startsWith("(")) {
            throw new IllegalArgumentException(String.format("Illegal Argument [%s] does not start with an opening parenthesis", strng));
        }

        int depth = 0;
        for (int i = 0; i < strng.length(); i++) {
            if (strng.charAt(i) == '(') {
                depth++;
            } else if (strng.charAt(i) == ')' && (--depth == 0)) {
                return i;
            }
        }
        return -1;
    }

    private static String[] splitToBranches(String s) {
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
                    if (leftParenCount == rightParenCount) {
                        splitIndices.add(i);
                    }
                    break;
            }
        }
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

    private static boolean isValidFormat(String inputCleaned) {
        if (inputCleaned.isEmpty()) {
            return false;
        }
        long countSemicolons = inputCleaned.chars().filter(num -> num == ';').count();

        if (!inputCleaned.endsWith(";") || countSemicolons != 1) {
            return false;
        }
        int brackets = 0;
        for (char c : inputCleaned.toCharArray()) {
            if (c == '(') {
                brackets += 1;
            }
            if (c == ')') {
                brackets -= 1;
            }
            if (brackets < 0) {
                return false;
            }
        }
        if (brackets != 0) {
            return false;
        }
        return true;
    }

}

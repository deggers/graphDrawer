import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class finalParserNewick {

    static int pseudoNode_uuid = 0;

    public boolean parseFileToTree(File file) {
        try {
            Stream<String> lines = Files.lines(file.toPath());
            String newickString = lines.map(line -> line.replaceAll("\\s+", "")).collect(Collectors.joining()).trim();

            if (isValidFormat(newickString)) {
                MutableTree<String> tree = new MappedTreeStructure<String>("");
                callMyselfRecursively(newickString, tree);
                ParseController.getInstance().setTree(tree);
                pseudoNode_uuid = 0; // count from new
                return true;
            } else {
                System.out.println("format for newick seems to be wrong, daaamn");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static String callMyselfRecursively(String string, MutableTree tree) {
        try {  // try to find branch
            int rightPar = getClosingParenthesis(string);
            int nodeName = pseudoNode_uuid++;
            try {
                String toProcess = string.substring(1, rightPar);
                try {
                    String[] splitArray = splitToBranches(toProcess);
                    for (String branch : splitArray) {
                        String child = callMyselfRecursively(branch, tree);
                        tree.add(Integer.toString(nodeName), child);
                    }
                    return Integer.toString(nodeName);
                } catch (Exception e) {
                    System.out.println("splitToBranches not possible");

                }
            } catch (Exception e) {
                System.out.println("Substring not possible ?");
                System.out.println("string = " + string);
            }
        } catch (IllegalArgumentException e) {
//            System.out.println("i guess we have a leaf here");
        }
        return string;
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

//        somehow we have sometimes no semicolon
        if (!inputCleaned.endsWith(";") || countSemicolons != 1) {
            System.out.println("Newick does not endsWith ; or contains more than one ; ");
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
        return brackets == 0;
    }
}

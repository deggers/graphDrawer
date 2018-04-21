// to-do
// good cleaning of input :) -> toAsci? or RegExp?  -optional

class NewickParser {
    private static String prepareString(String input) {
        String inputCleaned = input.replace(" ", "");
        inputCleaned = inputCleaned.replace("\t", "");
        inputCleaned = inputCleaned.replace("\n", "");
        return inputCleaned;
    }

    private String getText(String input, int i) {
        return null;
    }

    private void toRawGraph(String input) {


        int i = 0;
//        RawNode[] Children;
//        RawNode root;
        String[] rootLabelArray;

        do {
            char c = input.charAt(i);
            switch (c) {
                case '(':
                    //Funktionsaufruf muss i bis nach zugehöriger schließender Klammer erhöhen
                    break;
                case ';':
                    return;//irgendwas zu Graph
                case ',':
                    //throw Exception = new Exception("No , before first Node allowed")
                    break;
                default:
                    rootLabelArray = getText(input, i).split(":");
//                rootLabel = rootLabelArray[0];
//                rootLenght = rootLabelArray[1];
                    i = Integer.parseInt(rootLabelArray[2]);
                    break;

            }
            i++;
        } while (true);

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

    public static Graph parse(String input) {
        Graph g = new Graph();
//        toRawGraph(input);


        return g;
    }
}

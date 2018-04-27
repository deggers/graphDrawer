import java.io.File;

public class ParseController {

    private finalParserNewick parserNewick;
    private MutableTree tree;

    public static ParseController instance;

    public ParseController() {
        parserNewick = new finalParserNewick();
    }

    public static ParseController getInstance() {
        if (instance == null) {
            ParseController.instance = new ParseController();
        }
        return ParseController.instance;
    }

    public boolean initializeParsing(File file) {
        parserNewick = new finalParserNewick();
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (fileExtension.equals("nh")) {
            return parserNewick.parseFileToTree(file);
        } else {
            System.out.println("Format is not .nh ?!");
            return false;
        }
    }

    public void setTree(MutableTree<String> tree) {
        this.tree = tree;
    }

    public MutableTree getTree() {
        return this.tree;
    }
}
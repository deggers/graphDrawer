package controller;

import model.Node;
import model.TreeParserNewick;

import java.io.File;

public class ParseController {

    private         Node                tree;
    public static   ParseController     instance;

    public ParseController() {
        TreeParserNewick parserNewick = new TreeParserNewick();
    }

    public static ParseController getInstance() {
        if (instance == null) {
            ParseController.instance = new ParseController();
        }
        return ParseController.instance;
    }

    public boolean initializeParsing(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (fileExtension.equals("nh")) {
            return TreeParserNewick.parseFileToTree(file);
        } else {
            System.out.println("Format is not .nh ?!");
            return false;
        }
    }

    // SETTER & GETTER AREA
    public void setTree(Node tree) {
        this.tree = tree;
    }

    public Node getTree() {
        return this.tree;
    }

    // not used, right?
    public String[] getTreeStringArray() {
        String[] splitTree = tree.toString().split("\t");
        return splitTree;
    }
}
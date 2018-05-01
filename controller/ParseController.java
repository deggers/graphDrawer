package controller;

import model.MappedTreeStructure;
import model.Node;
import model.TreeParserNewick;

import java.io.File;

public class ParseController {

    private         File                file;
    private MappedTreeStructure<Node> tree;
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
        setFile(file);
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
    public void setTree(MappedTreeStructure<Node> tree) {
        this.tree = tree;
    }

    public MappedTreeStructure<Node> getTree() {
        return this.tree;
    }

    // not used, right?
    public String[] getTreeStringArray() {
        String[] splitTree = tree.toString().split("\t");
        return splitTree;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
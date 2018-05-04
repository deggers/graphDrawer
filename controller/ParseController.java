package controller;

import model.Tree;
import model.TreeParserNewick;

import java.io.File;

public class ParseController {

    private         File                file;
    private Tree tree;
    public static   ParseController     instance;

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
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public Tree getTree() {
        return this.tree;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
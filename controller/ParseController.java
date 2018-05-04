package controller;

import model.GraphMLGraph;
import model.GraphMLParser;
import model.MappedTreeStructure;
import model.TreeParserNewick;

import java.io.File;

public class ParseController {

    private File file;
    private MappedTreeStructure tree;
    public static ParseController instance;
    private GraphMLGraph graph;

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
        } else if (fileExtension.equalsIgnoreCase("graphml")) {
            System.out.println("here i am, GraphML File! ");
            return GraphMLParser.parseFileToTree(file);
        } else {
            System.out.println("Format is unknown WTF ?!");
            return false;
        }
    }

    // SETTER & GETTER AREA
    public void setTree(MappedTreeStructure tree) {
        this.tree = tree;
    }

    public MappedTreeStructure getTree() {
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

    public void setGraph(GraphMLGraph graph) {
        this.graph = graph;
    }
}
package controller;

import model.GraphMLGraph;
import model.GraphMLParser;
import model.MappedTreeStructure;
import model.TreeParserNewick;
import model.Graph;

import java.io.File;

public class ParseController {

    private File file;
    public static ParseController instance;
    private Graph graph;

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
    public void setTree(Graph graph) {
        this.graph = graph;
    }

    public Graph getTree() {
        return this.graph;
    }

    // not used, right?
    public String[] getTreeStringArray() {
        String[] splitTree = graph.toString().split("\t");
        return splitTree;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
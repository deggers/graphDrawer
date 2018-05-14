
package controller;

import model.GraphMLGraph;
import model.GraphMLParser;
import model.Tree;
import model.TreeParserNewick;

import java.io.File;

public class ParseController {

    private File file = null;
    private Tree tree = null;

    private GraphMLGraph graph = null;
    public static ParseController instance;

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
        if (fileExtension.equalsIgnoreCase("nh")) {
            Tree newickTree = TreeParserNewick.parseFileToTree(file);
            setTree(newickTree);
            setGraph(null);
            return true;
        } else if (fileExtension.equalsIgnoreCase("graphml")) {
            GraphMLGraph graphML = GraphMLParser.parseFileToGraph(file);
            setGraph(graphML);
            GUIController.getInstance().setChoiceBoxEdgeTypeIsSet(false);
            setTree(null);
            return true;
        } else {
            System.out.println("format is not supported ?!");
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

    public GraphMLGraph getGraph() {
        return graph;
    }
    public void setGraph(GraphMLGraph graph) {
        this.graph = graph;
    }

    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
}
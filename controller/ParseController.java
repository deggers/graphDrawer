
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
            // rather "setupNewickConfig()"
            GUIController.getInstance().choiceBoxEdgeType.setDisable(true);
            GUIController.getInstance().choiceBoxRoot.setDisable(true);
            setTree(newickTree);
            setGraph(null);
            return true;
        } else if (fileExtension.equalsIgnoreCase("graphml")) {
            /// rather "setupGraphMLConfig()"
            GraphMLGraph graphML = GraphMLParser.parseFileToGraph(file);
            setGraph(graphML);
            setTree(null);
            return true;
        } else {
            System.out.println("format is not supported ?!");
            return false;
        }
    }

    // SETTER & GETTER AREA
    public void setTree(Tree tree) {
        GUIController guiController = GUIController.getInstance();
        if (guiController != null) {
            guiController.setChoiceBoxAlgorithmIsSet(false); // new tree new Algorithms possible -> calc new
            guiController.setChoiceBoxAlgorithm(null); // do not draw the old Algorithm again, new one is necessary
        }
        if (PaneController.getInstance() != null) guiController.cleanPane();  // new tree => new drawing => clean old pane
        this.tree = tree;
    }
    public Tree getTree() {
        return this.tree;
    }

    public GraphMLGraph getGraph() {
        return graph;
    }
    public void setGraph(GraphMLGraph graph) {
        GUIController.getInstance().setChoiceBoxAlgorithmIsSet(false);
        this.graph = graph;
    }

    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
}
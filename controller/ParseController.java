
package controller;

import model.Graph;
import model.GraphMLParser;
import model.Tree;
import model.TreeParserNewick;

import java.io.File;

import static java.util.Objects.requireNonNull;

public enum ParseController{
    INSTANCE;
    private File file;
    private Tree tree;
    private Graph graph;

    public boolean initParsing(File file) {
        setFile(file);
        if (getFileExtension(file).equalsIgnoreCase("nh")) return parseNewick(file);
        else if (getFileExtension(file).equalsIgnoreCase("graphml")) return parseGraphML(file);
        // nothing worked, error!
        System.out.println("tried to parse a unknown fileExtension");
        return false;
    }

    private boolean parseGraphML(File file) {
        try {
            Graph graphML = GraphMLParser.parseFileToGraph(file);
            setGraph(graphML);
            setTree(null);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private boolean parseNewick(File file) {
        try {
            Tree newickTree = TreeParserNewick.parseFileToTree(file);
            requireNonNull(GUIController.getInstance()).choiceBoxEdgeType.setDisable(true);
            requireNonNull(GUIController.getInstance()).choiceBoxRoot.setDisable(true);
            setTree(newickTree);
            setGraph(null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    void    setGraph(Graph graph) {
        this.graph = graph;
    }
    void    setTree(Tree newickTree) {
        GUIController guiController = GUIController.getInstance();
        if (guiController != null) {
            guiController.setChoiceBoxAlgorithmIsSet(false); // new tree new Algorithms possible -> calc new
            guiController.setChoiceBoxAlgorithm(null); // do not draw the old Algorithm again, new one is necessary
        }
        if (PaneController.getInstance() != null) guiController.cleanPane();  // new tree => new drawing => clean old pane
        this.tree = newickTree;
    }
    void    setFile(File file) {
        this.file = file;
    }
    private String  getFileExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public Tree         getTree() {
        return this.tree;
    }
    public Graph getGraph() {
        return this.graph;
    }
    public File         getFile() {
        return this.file;
    }
}
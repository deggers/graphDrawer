package controller;

import parser.*;
import structure.*;
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
        return false;}

    private boolean parseGraphML(File file) {
        try {
            Graph graphML = GraphMLParser.parseFileToGraph(file);
            setGraph(graphML);
            setTree(null);
            return true;
        } catch (Exception e){
            System.out.println("Problem with parsing the graphML File");
            e.printStackTrace();
            return false;
        }
    }
    private boolean parseNewick(File file) {
        try {
            Tree newickTree = TreeParserNewick.parseFileToTree(file);
            setTree(newickTree);
            setGraph(null);
            return true;}
        catch (Exception e) {
            System.out.println("Problems with parsing the newickTree");
            e.printStackTrace();
            return false;}}

    void    setGraph(Graph graph) {
        this.graph = graph;
    }
    void    setTree(Tree newickTree) { this.tree = newickTree;}

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

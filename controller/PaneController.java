package controller;

import draw.Connector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.*;

import java.io.IOException;
import java.util.Objects;

//@formatter:off
public class PaneController {
    // CONSTANTS
    private static  final   int     OFFSET              = 20;
    private static final    int     ARROW_HEAD_SIZE     = 8;


    private static final    Paint   LEAF_COLOR          = Color.FORESTGREEN;
    private static final    Paint   NODE_COLOR          = Color.GRAY;
    private static final    Paint   RADIAL_LEVEL        = Color.LIGHTGRAY;
    private static final    Paint   ROOT_COLOR          = Color.RED;

    // Other variables
    private static          PaneController  paneInstance = null;
    static  public          PaneController  getInstance() {
        if (paneInstance == null) {
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(PaneController.class.getResource("/fxml/Pane.fxml"));
                ScrollPane scrollPane = loader.load();
                PaneController paneController;
                paneController = loader.getController();
                paneController.setScrollPane(scrollPane);
                paneInstance = paneController;
                return paneController;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return paneInstance;
        }
    }
    private static double                      spaceBetweenRadii = 0;
    private static double                      radialNodeSize = 8;
    private static int                      centerY;
    private static int                      centerX;
    // FXML in Pane.fxml
    @FXML   private         ScrollPane      scrollPane;
    @FXML   private         Pane            pane;

    // HELPERFUNCTIONS
    void            cleanPane() {
        if (pane != null) this.pane.getChildren().clear();
    }
    private double  scaleCoordinate(double number) {
        return (number * 2 * getNodeSize()) + OFFSET;
    }

    // SETTER & GETTER
    private void    setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
    ScrollPane      getScrollPane() {
        return scrollPane;
    }
    private int     getNodeSize() {
        return Objects.requireNonNull(GUIController.getInstance()).getNodeSize();
    }

    // DRAWFUNCTIONS

    void drawDAG(Graph graph) {
        drawDAGEdges(graph,5);
        drawDAGNodes(graph);
    }

    private void drawDAGEdges(Graph graph, int ports){
        for (Edge edge: graph.getEdges()) {
            GraphNode startNode = edge.getStart();
            GraphNode targetNode = edge.getTarget();

            pane.getChildren().add(new Connector(startNode,targetNode,ports,ARROW_HEAD_SIZE));
        }
    }

    private void drawDAGNodes(Graph graph){
        for (GraphNode node: graph.getNodes()){
            pane.getChildren().add(createGraphNode(node,"DAG"));
            }
    }

    private Circle createGraphNode(GraphNode node, String type){
        Circle circle;
        circle = new Circle(scaleGraphNode(node.x), scaleGraphNode(node.y), getNodeSize());
        Tooltip tip = new Tooltip((node.getLabel() + ", " + node.getLayer()));
        Tooltip.install(circle,tip);
        if (node.isDummyNode()) circle.setFill(Color.TRANSPARENT);
        return circle;
    }
    public int scaleGraphNode(int number){
        return (number * 4 * getNodeSize()) + OFFSET;
    }


    void            drawTreeStructure(Tree tree) {
        drawTreeEdges(tree);
        drawTreeNodes(tree);
    }
    void            drawTreeOrthogonally(Tree tree) {
        drawEdgesOrthogonally(tree);
        drawTreeNodes(tree);
    }
    private void    drawEdgesOrthogonally(Tree tree) {
        for (TreeNode child : tree.listAllNodes()) {
            if (child.parent != null) {
                TreeNode parent = child.parent;
                double parentX = scaleCoordinate(parent.x);
                double parentY = scaleCoordinate(parent.y);

                double childX = scaleCoordinate(child.x);
                double childY = scaleCoordinate(child.y);
                pane.getChildren().add(new Line(parentX, parentY, childX, parentY));
                pane.getChildren().add(new Line(childX, parentY, childX, childY));
            }
        }
    }
    private void    drawTreeNodes(Tree tree) {
        for (TreeNode node : tree.listAllNodes()) {
            pane.getChildren().add(createNode(node,"tree"));
        }
    }
    private void    drawTreeEdges(Tree tree) {
        for (TreeNode child : tree.listAllNodes()) {
            if (child.parent != null) {
                TreeNode parent = child.parent;
                double parentX = scaleCoordinate(parent.x);
                double parentY = scaleCoordinate(parent.y);

                double childX = scaleCoordinate(child.x);
                double childY = scaleCoordinate(child.y);
                pane.getChildren().add(new Line(parentX, parentY, childX, childY));
            }
        }
    }


    private Circle  createNode(TreeNode node, String type) {
        Circle circle;
        if (type.equalsIgnoreCase("radial"))
            circle = new Circle(node.x, node.y, radialNodeSize);
        else circle = new Circle(scaleCoordinate(node.x), scaleCoordinate(node.y), getNodeSize());
        if (node.isLeaf()) circle.setFill(LEAF_COLOR);
        else circle.setFill(NODE_COLOR);
        if (node.parent == null) circle.setFill(ROOT_COLOR);
        Tooltip tip = new Tooltip(node.label);
        Tooltip.install(circle, tip);
        return circle;
    }


        private Circle  createGuideline(int width, int height, int decreasingRadius) {
        Circle node = new Circle(width, height, decreasingRadius);
        node.setFill(Color.TRANSPARENT);
        node.setStroke(RADIAL_LEVEL);
        return node;
    }
    //@formatter:on

    public void drawRadialTreeStructure(Tree tree) {
        int halfHeight = (int) scrollPane.getHeight() / 2;
        int halfWidth = (int) scrollPane.getWidth() / 2;
        int level = tree.getTreeDepth();

        // check if not suitable for the screenresolution, then allow scrolling
        int minSpacing = 16;
        int minNodeSize = 8;

//        double maxPaneRadius = (optSize / level) < minSpacing ? minSpacing * level : optSize;
        double maxPaneRadius = Math.min(halfHeight, halfWidth);
        double scaleFactor = getNodeSize() - 7.0;

//        spaceBetweenRadii = ((maxPaneRadius / level < minSpacing) ? minSpacing : maxPaneRadius / level) * scaleFactor;
        spaceBetweenRadii = maxPaneRadius / level * scaleFactor;
        maxPaneRadius = spaceBetweenRadii * level;

        // setup radialNodeSize
        double properNodeSize = Math.min(spaceBetweenRadii / 4, 8);
        double scaledRadialNodeSize = (radialNodeSize * scaleFactor);
        boolean hasProperNodeSize = scaledRadialNodeSize > properNodeSize;
        radialNodeSize = hasProperNodeSize ? properNodeSize : properNodeSize;

//        centerY =  (int) (middleOfScreen > paneHalfHeight ? middleOfScreen : paneHalfHeight);
//        centerX = (int) (middleOfScreen > paneHalfWidth ? middleOfScreen : paneHalfWidth);
        double paneDiffWidth = (getScrollPaneWidth() - maxPaneRadius);
        double paneDiffHeight = (getScrollPaneHeight() - maxPaneRadius);

        centerX = (int) (maxPaneRadius + 2 *radialNodeSize);
        centerY = (int) (maxPaneRadius + 2 * radialNodeSize);


        for (int i = 1; i <= level; i++) {
            pane.getChildren().add(createGuideline(centerX, centerY, (int) (maxPaneRadius)));
            maxPaneRadius -= spaceBetweenRadii;
        }

        TreeNode root = tree.getRoot();
        radialPositions(tree, root, Math.toRadians(0), Math.toRadians(360));
        drawRadialTreeEdges(tree);
//        radialPositions(tree, root, Math.toRadians(0), Math.toRadians(360));
    }

    // functuion to calculate arc length
    static double arcLength(double radians, int radius) {
        double pi = Math.PI;  // or 22/7
        double angle = Math.toDegrees(radians);

        return ((angle > 360) || (angle <= 0)) ? 0 : ((2 * pi * radius) * (angle / 360.0));
    }

    private Tree radialPositions(Tree radialTree, TreeNode node, double alpha, double beta) {

        if (node.parent == null) { // if node is root
            radialTree.setNodeCoords(node, centerX, centerY);
            pane.getChildren().add(createNode(node, "radial"));
        }

        double depthOfVertex = node.level + 1;
        double theta = alpha;
        double radius = (spaceBetweenRadii * depthOfVertex);
        // number of leaves for the subtree rooted in v
        int k = radialTree.getLeavesOfNode(node);
//        System.out.println("got " + (beta - alpha) / k + " radians per node");
        for (TreeNode child : node.getChildren()) {
            int lambda = radialTree.getLeavesOfNode(child);
            double mi = theta + (((double) lambda / k) * (beta - alpha));
//            System.out.println("space " + mi + " for " + child.label);
            double term = (theta + mi) / 2;
            double cosTerm = Math.cos(term);
            double sinTerm = Math.sin(term);

            int x = (int) (centerX + (radius * cosTerm));
            int y = (int) (centerY + (radius * sinTerm));
            radialTree.setNodeCoords(child, x, y);
            pane.getChildren().add(createNode(child, "radial"));
//            System.out.println("printed node " + child.label);

            if (!child.isLeaf()) {
                radialPositions(radialTree, child, theta, mi);
            }
            theta = mi;
        }
        return radialTree;
    }

    public void drawRadialTreeEdges(Tree tree) {
        for (TreeNode child : tree.listAllNodes()) {
            if (child.parent != null) {
                TreeNode parent = child.parent;
                double parentX = parent.x;
                double parentY = parent.y;

                double childX = child.x;
                double childY = child.y;
                Line line = new Line(parentX, parentY, childX, childY);
                pane.getChildren().add(line);
            }
        }
    }

    /*public void drawGraph(Graph theGraph) {
        for (TreeNode node : theGraph.nodeList) {
            if (node.parent == null) {
                pane.getChildren().add(createNode(node, "graph"));
            }
        }
    }*/

    public int getScrollPaneHeight() {
        return scrollPane == null ? 0 : (int) scrollPane.getHeight();
    }

    public int getScrollPaneWidth() {
        return scrollPane == null ? 0 : (int) scrollPane.getWidth();
    }


}
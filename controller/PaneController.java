package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.Node;
import model.Tree;

import java.io.IOException;
import java.util.Objects;

//@formatter:off
public class PaneController {
    // CONSTANTS
    private static  final   double  OFFSET              = 20;
    private static final    Paint   LEAF_COLOR          = Color.FORESTGREEN;
    private static final    Paint   NODE_COLOR          = Color.GRAY;
    private static final    Paint   RADIAL_LEVEL        = Color.LIGHTGRAY;
    private static final    Paint   ROOT_COLOR          = Color.RED;

    // Other variables
    private static          PaneController  paneInstance = null;
    static                  PaneController  getInstance() {
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
    static int                              spaceBetweenRadii;
    static int                              radialNodeSize;

    // FXML in Pane.fxml
    @FXML   private         ScrollPane      scrollPane;
    @FXML   private         Pane            pane;

    // HELPERFUNCTIONS
    void            cleanPane() {
        this.pane.getChildren().clear();
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
    void            drawTreeStructure(Tree tree) {
        drawTreeEdges(tree);
        drawTreeNodes(tree);
    }
    private void    drawTreeNodes(Tree tree) {
        for (Node node : tree.listAllNodes()) {
            pane.getChildren().add(createNode(node));
        }
    }
    private void    drawTreeEdges(Tree tree) {
        for (Node child : tree.listAllNodes()) {
            if (child.parent != null) {
                Node parent = child.parent;
                double parentX = scaleCoordinate(parent.x);
                double parentY = scaleCoordinate(parent.y);

                double childX = scaleCoordinate(child.x);
                double childY = scaleCoordinate(child.y);
                pane.getChildren().add(new Line(parentX, parentY, childX, childY));
            }
        }
    }

    private Circle  createGuideline(int halfWidth, int halfHeight, int decreasingRadius) {
        Circle node = new Circle(halfWidth, halfHeight, decreasingRadius);
        node.setFill(Color.TRANSPARENT);
        node.setStroke(RADIAL_LEVEL);
        return node;
    }
    private Circle  createNode(Node node) {
        Circle circle = new Circle(scaleCoordinate(node.x), scaleCoordinate(node.y), getNodeSize());
        if (node.isLeaf()) {
            circle.setFill(LEAF_COLOR);
        } else circle.setFill(NODE_COLOR);
        Tooltip tip = new Tooltip(node.label);
        Tooltip.install(circle, tip);
        return circle;
    }
       private Circle  createRadialNode(Node node) {
        Circle circle = new Circle(node.x, node.y, radialNodeSize);
        if (node.isLeaf()) {
            circle.setFill(LEAF_COLOR);
        } else circle.setFill(NODE_COLOR);
        if (node.parent == null) circle.setFill(ROOT_COLOR);
        Tooltip tip = new Tooltip(node.label);
        Tooltip.install(circle, tip);
        return circle;
    }

    //@formatter:on

    public void drawRadialTreeStructure(Tree tree) {
        Node root = tree.getRoot();
        radialPositions(tree, root, Math.toRadians(0), Math.toRadians(360));
        drawRadialTreeEdges(tree);

        int halfHeight = (int) scrollPane.getHeight() / 2;
        int halfWidth = (int) scrollPane.getWidth() / 2;
        int level = tree.getTreeDepth();

        int maxRadius = (Math.min(halfHeight, halfWidth));
        spaceBetweenRadii = maxRadius / level;
        radialNodeSize = spaceBetweenRadii >> 2;
        if (radialNodeSize > 16) radialNodeSize = 16;

        for (int i = 0; i <= level + 1; i++) {
            pane.getChildren().add(createGuideline(halfWidth, halfHeight, maxRadius+radialNodeSize));
            maxRadius -= spaceBetweenRadii;
        }
        radialPositions(tree, root, Math.toRadians(0), Math.toRadians(360));

    }

    private Tree radialPositions(Tree radialTree, Node node, double alpha, double beta) {
        int centerY = (int) scrollPane.getHeight() / 2;
        int centerX = (int) scrollPane.getWidth() / 2;

        if (node.parent == null) { // if node is root
            radialTree.setNodeCoords(node, centerX, centerY);
            pane.getChildren().add(createRadialNode(node));
        }

        double depthOfVertex = node.level + 1;
        double theta = alpha;
        double radius = (spaceBetweenRadii * depthOfVertex) + radialNodeSize;

        // number of leaves for the subtree rooted in v
        int k = radialTree.getLeavesOfNode(node);

        for (Node child : node.getChildren()) {
            int lambda = radialTree.getLeavesOfNode(child);
            double betaAlphaTemp = beta - alpha;
            double mi = theta + (((double) lambda / k) * (beta - alpha));

            double term = (theta + mi) / 2;
            double cosTerm = Math.cos(term);
            double sinTerm = Math.sin(term);

            // x = center x + radius * cos(angle)
            int x = (int) (centerX + (radius * cosTerm));
            int y = (int) (centerY + (radius * sinTerm));
            radialTree.setNodeCoords(child, x, y);
            pane.getChildren().add(createRadialNode(child));

            if (!child.isLeaf()) {
                radialPositions(radialTree, child, theta, mi);
            }
            theta = mi;
        }
        return radialTree;
    }

    public void drawRadialTreeEdges(Tree tree) {
        for (Node child : tree.listAllNodes()) {
            if (child.parent != null) {
                Node parent = child.parent;
                double parentX = parent.x;
                double parentY = parent.y;

                double childX = child.x;
                double childY = child.y;
                Line line = new Line(parentX, parentY, childX, childY);
                pane.getChildren().add(line);
            }
        }
    }
}
package draw;


import structure.*;
import controller.GUIController;
import controller.PaneController;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author kn - modified by Dustyn
 */
public class Connector extends Path {
    private static final boolean VERBOSE = false;
    private static final double defaultArrowHeadSize = 6.0;
    private static final double defaultStrokeWidth = 3.0;
    private static final Color defaultConnectorColor = Color.BLACK;
    private static final Color defaultReversedEdgeColor = Color.DARKGRAY;

    public Connector(Edge edge, int ports) {
        super();
        strokeProperty().bind(fillProperty());
        if (edge.isReversed()) setFill(defaultReversedEdgeColor);
        else setFill(defaultConnectorColor);
        setStrokeWidth(defaultStrokeWidth);

        GraphNode startNode = edge.getStart();
        GraphNode targetNode = edge.getTarget();
        double angle = Math.abs(Math.toDegrees(Math.atan2((targetNode.y - startNode.y), (targetNode.x - startNode.x)) - Math.PI / 2));
        double RealAngle = Math.toDegrees(Math.atan2((targetNode.y - startNode.y), (targetNode.x - startNode.x)) - Math.PI / 2.0);
        if (VERBOSE) System.out.println("Edge " + edge + " coming with degree: " + RealAngle);
        if (ports == 8) connectWith8Ports(edge, RealAngle);
    }

    private void connectWith8Ports(Edge edge, double angle) {

        if (degreeBetween(330, 360, Math.abs(angle)) || degreeBetween(0, 30, Math.abs(angle)))
            createLineWithAnchor(edge, 0.0);

        else if (degreeBetween(30, 60, Math.abs(angle)))
            if (angle < 0)  createLineWithAnchor(edge, 45);
            else            createLineWithAnchor(edge, 315);

        else if (degreeBetween(60, 120, Math.abs(angle)))
            if (angle < 0) createLineWithAnchor(edge, 90);
            else createLineWithAnchor(edge, 270);
        else {
            System.out.println("could'nt connect.. in Connector.java");
            createLineWithAnchor(edge, 0);
        }
    }

    private void createLineWithAnchor(Edge edge, double anchorAngle) {
        GraphNode startNode = edge.getStart();
        GraphNode targetNode = edge.getTarget();
        if (edge.isReversed()) startNode = edge.getTarget();
        if (edge.isReversed()) targetNode = edge.getStart();
        PaneController paneController = Objects.requireNonNull(PaneController.getInstance());
        double startX = paneController.scaleGraphNode(startNode.x);
        double startY = paneController.scaleGraphNode(startNode.y);
        double targetX = paneController.scaleGraphNode(targetNode.x);
        double targetY = paneController.scaleGraphNode(targetNode.y);
        double endX, endY;
        double radius = Objects.requireNonNull(GUIController.getInstance()).getNodeSize();

        LinkedList<Double> coords = new LinkedList<>();
        if (targetNode.isDummy()) { // full length of edge necessary
            endX = targetX;
            endY = targetY;
        } else {
            coords = getPointOnCircumference(targetX, targetY, radius, anchorAngle, edge.isReversed());
            endX = coords.getFirst();
            endY = coords.getLast();
        }

        LinkedHashMap<Double, Boolean> portMap = targetNode.getPortMap();
        if (portMap.containsKey(anchorAngle)) {
            createLine(startX, startY, endX, endY);
        } else {
            createLineWithArrow(startX, startY, endX, endY, targetNode.isDummy(), edge.isReversed());
            portMap.put(anchorAngle, true);
            targetNode.setPortMap(portMap);
        }
    }

    private void createLine(double startX, double startY, double endX, double endY) {
        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));
    }

    private void createLineWithArrow(double startX, double startY, double endX, double endY, boolean isDummy, boolean reversed) {

        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));

        if (!isDummy) {
            //ArrowHead
            double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            //point1
            double x1, y1, x2, y2;
            x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * defaultArrowHeadSize + endX;
            y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * defaultArrowHeadSize + endY;
            //point2
            x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * defaultArrowHeadSize + endX;
            y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * defaultArrowHeadSize + endY;

            getElements().add(new LineTo(x1, y1));
            getElements().add(new LineTo(x2, y2));
            getElements().add(new LineTo(endX, endY));
        }
    }

    private LinkedList<Double> getPointOnCircumference(double x,double y, double radius, double angle, boolean reversed) {
        LinkedList<Double> coords = new LinkedList<>();
        angle = Math.abs(angle);
        if (!reversed) {
            coords.add( x - radius * Math.sin(Math.toRadians(angle)));
            coords.add( y - radius * Math.cos(Math.toRadians(angle)));
        } else {
            coords.add( x + radius * Math.sin(Math.toRadians(angle)));
            coords.add( y + radius * Math.cos(Math.toRadians(angle)));
        }
        return coords;
    }

    private static boolean degreeBetween(double left, double right, double degree) {
        if (left > right) return degree >= right && degree <= left; // e.g. 30 20 for 15
        else return degree >= left && degree <= right;  // e.g. 20 30 for 15
    }

}


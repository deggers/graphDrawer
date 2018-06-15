package draw;

import controller.GUIController;
import controller.PaneController;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import model.Edge;
import model.GraphNode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author kn - modified by Dustyn
 */
public class Connector extends Path {
    private static final double defaultArrowHeadSize = 6.0;
    private static final double defaultStrokeWidth = 1.0;
    private static final Color defaultConnectorColor = Color.BLACK;

    public Connector(Edge edge, int ports) {
        super();
        strokeProperty().bind(fillProperty());
        if (edge.isReversed()) setFill(Color.RED);
        else setFill(defaultConnectorColor);
        setStrokeWidth(defaultStrokeWidth);

        GraphNode startNode = edge.getStart();
        GraphNode targetNode = edge.getTarget();

        double angle = Math.abs(Math.toDegrees(Math.atan2((targetNode.y - startNode.y), (targetNode.x - startNode.x)) - Math.PI / 2.0));
//        if (ports == 8) connectWith8Ports(startNode, targetNode, angle);
//        if (ports == 4) connectWith4Ports(startNode, targetNode, angle);
        if (ports == 5) connectWith5Ports(edge, angle);
    }

//    private void connectWith5Ports(Edge edge, double angle) {
//        if (degreeBetween(270-22.5, 270+22.5, angle))
//            createLineWithAnchor(edge, 270.0);
//        else if (degreeBetween(315-22.5, 315+22.5, angle))
//            createLineWithAnchor(edge, 315.0);
//        else if (degreeBetween(315+22.5, 0, angle) || degreeBetween(0.00,22.5,angle))
//            createLineWithAnchor(edge, 0.0);
//        else if (degreeBetween(22.5, 45+22.5, angle))
//            createLineWithAnchor(edge, 45.0);
//        else if (degreeBetween(45+22.5, 90+22.5, angle))
//            createLineWithAnchor(edge, 90.0);
//        else System.out.println("uups, 5Ports did not found a port!");
//    }

    private void connectWith5Ports(Edge edge, double angle) {
        if (degreeBetween(0, 112.5, angle))
            createLineWithAnchor(edge, 90.0);
        else if (degreeBetween(112.5, 157.5, angle))
            createLineWithAnchor(edge, 135.0);
        else if (degreeBetween(157.5, 202.5, angle))
            createLineWithAnchor(edge, 180);
        else if (degreeBetween(202.5, 247.5, angle))
            createLineWithAnchor(edge, 225);
        else if (degreeBetween(247.5, 360, angle))
            createLineWithAnchor(edge, 270);
        else createLineWithAnchor(edge, 270);
    }

//    private void connectWith4Ports(GraphNode startNode, GraphNode targetNode, double angle) {
//        if (angle >= 0 && angle <= 45 || angle < 360 && angle >= 315) {
//            createLineWithAnchor(startNode, targetNode, (double) 0);
//        } else if (angle < 315 && angle >= 225) {
//            createLineWithAnchor(startNode, targetNode, (double) -270);
//        } else if (angle < 225 && angle >= 135) {
//            createLineWithAnchor(startNode, targetNode, (double) -180);
//        } else if (angle < 135 && angle >= 45) {
//            createLineWithAnchor(startNode, targetNode, (double) -90);
//        }
//    }
//
//    private void connectWith8Ports(GraphNode startNode, GraphNode targetNode, double angle) {
//        if (angle >= 180 - 22.5 && angle <= 180 + 22.5) { // case: bottom - go to 180°
//            createLineWithAnchor(startNode, targetNode, (double) -180);
//        } else if (angle >= 225 - 22.5 && angle <= 225 + 22.5) {
//            createLineWithAnchor(startNode, targetNode, (double) -225);
//        } else if (angle >= 270 - 22.5 && angle <= 270 + 22.5) {
//            createLineWithAnchor(startNode, targetNode, (double) -270);
//        } else if (angle >= 135 - 22.5 && angle <= 135 + 22.5) {
//            createLineWithAnchor(startNode, targetNode, (double) -135);
//        } else if (angle >= 90 - 22.5 && angle <= 90 + 22.5) {
//            createLineWithAnchor(startNode, targetNode, (double) -90);
//        }
//    }

    private void createLineWithAnchor(Edge edge, double anchorAngle) {
        GraphNode startNode = edge.getStart();
        GraphNode targetNode = edge.getTarget();
        if (edge.isReversed()) startNode = edge.getTarget();
        if (edge.isReversed()) targetNode = edge.getStart();
        PaneController paneController = Objects.requireNonNull(PaneController.getInstance());
        int startX = paneController.scaleGraphNode(startNode.x);
        int startY = paneController.scaleGraphNode(startNode.y);
        int targetX = paneController.scaleGraphNode(targetNode.x);
        int targetY = paneController.scaleGraphNode(targetNode.y);
        double endX, endY;
        double radius = Objects.requireNonNull(GUIController.getInstance()).getNodeSize();

        LinkedList<Double> coords = new LinkedList<>();
        if (targetNode.isDummyNode()) { // full length of edge necessary
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
            createLineWithArrow(startX, startY, endX, endY, targetNode.isDummyNode(), edge.isReversed());
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

    private LinkedList<Double> getPointOnCircumference(int x, int y, double radius, double angle, boolean reversed) {
        LinkedList<Double> coords = new LinkedList<>();
        angle = Math.abs(angle);
        if (!reversed) {
            coords.add((double) x - radius * Math.sin(Math.toRadians(angle)));
            coords.add((double) y - radius * Math.cos(Math.toRadians(angle)));
        } else {
            coords.add((double) x + radius * Math.sin(Math.toRadians(angle)));
            coords.add((double) y + radius * Math.cos(Math.toRadians(angle)));
        }
        return coords;
    }

    // copy paste from https://stackoverflow.com/questions/1992970/check-if-int-is-between-two-numbers
    // needs to check out :)
    private static boolean isBetween(double a, double b, double c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }

    /**
     * Returns boolean if Value in between
     *
     * @param left  smaller as
     * @param right smaller or equal as
     * @return boolean
     */
    private static boolean degreeBetween(double left, double right, double degree) {
        if (left > right) return degree >= right && degree < left; // e.g. 30 20 for 15
        else return degree > left && degree <= right;  // e.g. 20 30 for 15
    }

}

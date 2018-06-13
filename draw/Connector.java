package draw;

import controller.GUIController;
import controller.PaneController;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import model.GraphNode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author kn - modified by Dustyn
 */
public class Connector extends Path {
    private static final double defaultArrowHeadSize = 6.5;

    public Connector(GraphNode startNode, GraphNode targetNode, int ports, double arrowHeadSize) {
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.BLACK);

        double angle = Math.abs(Math.toDegrees(Math.atan2((targetNode.y - startNode.y), (targetNode.x - startNode.x)) - Math.PI / 2.0));
        if (ports == 8) connectWith8Ports(startNode, targetNode, angle);
        if (ports == 4) connectWith4Ports(startNode, targetNode, angle);
        if (ports == 5) connectWith5Ports(startNode, targetNode, angle);
    }

    private void connectWith5Ports(GraphNode startNode, GraphNode targetNode, double angle) {
//        if (angle >= 0 && angle < 112.5)
        if (degreeBetween(0, 112.5, angle))
            createLineWithAnchor(startNode, targetNode, 90.0);
        else if (degreeBetween(112.5, 157.5, angle))
//        if (angle >= 112.5 && angle < 157.5)
            createLineWithAnchor(startNode, targetNode, 135.0);
//        if ( angle >= 157.5 && angle < 202.5)
        else if (degreeBetween(157.5, 202.5, angle))
            createLineWithAnchor(startNode, targetNode, 180);
        else if (degreeBetween(202.5, 247.5, angle))
//        if (angle >= 202.5 && angle < 247.5)
            createLineWithAnchor(startNode, targetNode, 225);
//        if (angle >= 247.5 && angle < 360)
        else if (degreeBetween(247.5, 360, angle))
            createLineWithAnchor(startNode, targetNode, 270);
        else System.out.println("uups, 5Ports did not found a port!");
    }

    private void connectWith4Ports(GraphNode startNode, GraphNode targetNode, double angle) {
        if (angle >= 0 && angle <= 45 || angle < 360 && angle >= 315) {
            createLineWithAnchor(startNode, targetNode, (double) 0);
        } else if (angle < 315 && angle >= 225) {
            createLineWithAnchor(startNode, targetNode, (double) -270);
        } else if (angle < 225 && angle >= 135) {
            createLineWithAnchor(startNode, targetNode, (double) -180);
        } else if (angle < 135 && angle >= 45) {
            createLineWithAnchor(startNode, targetNode, (double) -90);
        }
    }

    private void connectWith8Ports(GraphNode startNode, GraphNode targetNode, double angle) {
        if (angle >= 180 - 22.5 && angle <= 180 + 22.5) { // case: bottom - go to 180°
            createLineWithAnchor(startNode, targetNode, (double) -180);
        } else if (angle >= 225 - 22.5 && angle <= 225 + 22.5) {
            createLineWithAnchor(startNode, targetNode, (double) -225);
        } else if (angle >= 270 - 22.5 && angle <= 270 + 22.5) {
            createLineWithAnchor(startNode, targetNode, (double) -270);
        } else if (angle >= 135 - 22.5 && angle <= 135 + 22.5) {
            createLineWithAnchor(startNode, targetNode, (double) -135);
        } else if (angle >= 90 - 22.5 && angle <= 90 + 22.5) {
            createLineWithAnchor(startNode, targetNode, (double) -90);
        }
    }

    private void createLineWithAnchor(GraphNode startNode, GraphNode targetNode, double anchorAngle) {
        PaneController paneController = Objects.requireNonNull(PaneController.getInstance());
        int startX = paneController.scaleGraphNode(startNode.x);
        int startY = paneController.scaleGraphNode(startNode.y);
        int targetX = paneController.scaleGraphNode(targetNode.x);
        int targetY = paneController.scaleGraphNode(targetNode.y);
        double endX, endY;
        double radius = Objects.requireNonNull(GUIController.getInstance()).getNodeSize();

        if (targetNode.isDummyNode()) { // full length of edge necessary
            endX = targetX;
            endY = targetY;
        } else {
            LinkedList<Double> coords = getPointOnCircumference(targetX, targetY, radius, anchorAngle);
            endX = coords.getFirst();
            endY = coords.getLast();
        }

        LinkedHashMap<Double, Boolean> portMap = targetNode.getPortMap();
        if (portMap.containsKey(anchorAngle)) {
            createLine(startX, startY, endX, endY);
        } else {
            createLineWithArrow(startX, startY, endX, endY, targetNode.isDummyNode(), defaultArrowHeadSize);
            portMap.put(anchorAngle, true);
            targetNode.setPortMap(portMap);
        }
    }

    private void createLine(double startX, double startY, double endX, double endY) {
        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));
    }

    private void createLineWithArrow(double startX, double startY, double endX, double endY, boolean isDummy, double arrowHeadSize) {

        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));

        if (!isDummy) {
            //ArrowHead
            double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            //point1
            double x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
            double y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
            //point2
            double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
            double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

            getElements().add(new LineTo(x1, y1));
            getElements().add(new LineTo(x2, y2));
            getElements().add(new LineTo(endX, endY));
        }
    }

    private LinkedList<Double> getPointOnCircumference(int x, int y, double radius, double angle) {
        LinkedList<Double> coords = new LinkedList<>();
        angle = Math.abs(angle);
        coords.add((double) x - radius * Math.sin(Math.toRadians(angle)));
        coords.add((double) y - radius * Math.cos(Math.toRadians(angle)));
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

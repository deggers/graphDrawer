package draw;

import controller.GUIController;
import controller.PaneController;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import model.GraphNode;

import java.util.Objects;

/**
 *
 * @author kn - modified by Dustyn
 */
public class Connector extends Path{
    private static final double defaultArrowHeadSize = 8.0;

    public Connector(GraphNode startNode, GraphNode targetNode, double arrowHeadSize){
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.BLACK);

        PaneController paneController = Objects.requireNonNull(PaneController.getInstance());
        double startX = paneController.scaleGraphNode(startNode.x);
        double startY = paneController.scaleGraphNode(startNode.y);
        double endX =  paneController.scaleGraphNode(targetNode.x);
        double endY = paneController.scaleGraphNode(targetNode.y);

        double angle;
        double radius;
        double sin;
        double cos;
        double x1;
        double x2;
        double y1;
        double y2;
        if (!targetNode.isDummyNode()) {
            // make line shorter if to realNode :)
            radius = GUIController.getInstance().getNodeSize();
            angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
            sin = Math.sin(angle);
            cos = Math.cos(angle);
            //point1
            x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * radius + endX;
            y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * radius + endY;
            //point2
            x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * radius + endX;
            y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * radius + endY;

            endX = (x1 + x2) / 2;
            endY = (y1 + y2) / 2;
        }

        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));

        if (!targetNode.isDummyNode()) {

            //ArrowHead
            angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
            sin = Math.sin(angle);
            cos = Math.cos(angle);
            //point1
            x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
            y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
            //point2
            x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
            y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

            getElements().add(new LineTo(x1, y1));
            getElements().add(new LineTo(x2, y2));
            getElements().add(new LineTo(endX, endY));
        }
    }

    public Connector(GraphNode startNode, GraphNode targetNode){
        this(startNode,targetNode, defaultArrowHeadSize);
    }



}

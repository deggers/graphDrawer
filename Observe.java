
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Observe {
    static double xOffset;
    static double yOffset;

    public static void moveAction(Stage stage, Scene scene) {
        scene.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }
}
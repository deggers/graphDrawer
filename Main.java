import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {
    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FrameController frameController = FrameController.getInstance();
        Parent root = frameController.getRoot();
//        root.setStyle("-fx-background-color:transparent;");
        Scene scene = new Scene(root);
        stage.setTitle("Awesome project for BioVis 2018");
        stage.setScene(scene);
        stage.show();
        frameController.init();
    }
}

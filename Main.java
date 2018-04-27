import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        System.out.println("start was called");
        FrameController frameController = FrameController.getInstance();
        Parent root = frameController.getRoot();
        Scene scene = new Scene(root);
        stage.setTitle("Awesome project for BioVis 2018");
        stage.setScene(scene);
        stage.show();
        frameController.init();
    }
}

import controller.GUIController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Observe;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GUIController guiController = GUIController.getInstance();
        Parent root = guiController.getRoot();
        Scene scene = new Scene(root);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        Observe.moveAction(stage, scene);
        guiController.init();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
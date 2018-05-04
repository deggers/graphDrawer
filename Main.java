import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Observe;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        VBox root = fxmlLoader.load(getClass().getResource("GUI.fxml").openStream());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        Observe.moveAction(stage, scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
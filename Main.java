import controller.GUIController;
import controller.ParseController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Observe;

import java.io.File;
import java.util.List;

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
        String username = System.getProperty("user.name");

        File file;
        String arg1;
        if (args.length > 0) {
            arg1 = args[0];
        } else  arg1 = "graphML";

        switch (arg1) {
            case "nh":
                file = new File("C:\\Users\\gross\\Desktop\\Programmierung\\BioVis\\BioVis18\\Baeume\\Data\\Phylogeny\\DTListenerTest.nh");
                break;
            case "graphML":
                if (username.equals("dusty")) {
                    file = new File("C:\\Users\\dusty\\Desktop\\Zeckzer\\Vorlesung\\Baeume_1\\Baeume\\Data\\Software-Engineering\\test.graphml");
                }
                else {
                    file = new File("C:\\Users\\gross\\Desktop\\Programmierung\\BioVis\\BioVis18\\Baeume\\Data\\Software-Engineering\\test.graphml");
                }
                break;
            default:
                file = null;
                break;
        }

        ParseController.getInstance().initializeParsing(file);
        GUIController guiInstance = GUIController.getInstance();

        assert guiInstance != null;
        List<File> filesInFolder = guiInstance.getFilesFromFolder(file);
        guiInstance.setFilesInFolder(filesInFolder);
        launch(args);
    }
}
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;

public class Controller {


    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ChoiceBox chooseAlgo;


    @FXML
//    private void initialize() {
//        chooseAlgo.setValue("Algo1");
//        ObservableList<String> chooseAlgoObserver = FXCollections.observableArrayList("Algo1", "Algo2", "Algo3");
//        final List options = chooseAlgo.getItems();
//        chooseAlgo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
//            @Override
//            public void changed(ObservableValue ov, Number oldSelected, Number newSelected) {
//                System.out.println("Old Selected Option: " + options.get(oldSelected.intValue()));
//                System.out.println("New Selected Option: " + options.get(newSelected.intValue()));
//            }
//        });
//    }

    private void clickMe(ActionEvent event) {
        System.out.println("you changed something here, right? ");
    }

    public Controller() {
    }
}

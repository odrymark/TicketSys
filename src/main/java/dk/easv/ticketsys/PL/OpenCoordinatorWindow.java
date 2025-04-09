package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenCoordinatorWindow {

    public OpenCoordinatorWindow(Event event, User loggedinUser)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/add_coordinator.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            AddCoordinatorController controller = fxmlLoader.getController();
            controller.setLoggedinUser(loggedinUser);
            controller.getCoordinators(event);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EventEditController
{
    @FXML
    private Button saveBtn;

    @FXML
    private void cancel()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/coordinator.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}

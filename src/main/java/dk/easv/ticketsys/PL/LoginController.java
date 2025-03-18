package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController
{
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField email;

    @FXML
    private void login()
    {
        if(!username.getText().isEmpty() && !password.getText().isEmpty() && !email.getText().isEmpty())
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/admin.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage) username.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
}
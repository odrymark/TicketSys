package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.be.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewUserController
{
    @FXML
    private Label userTitle;
    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    private User user;

    public void getUser(User user)
    {
        this.user = user;
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        password.setText(user.getPassword());
        userTitle.setText("Edit User");
    }

    @FXML
    private void addUser()
    {
        if(!username.getText().isEmpty() && !email.getText().isEmpty() && !password.getText().isEmpty())
        {
            Stage stage = (Stage) username.getScene().getWindow();
            stage.close();
        }
    }
}

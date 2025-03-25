package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {


    @FXML private VBox vbPassword;
    @FXML private Label lblUserTitle;
    @FXML private  HBox hbTop;
    @FXML private  VBox vboxForm;
    @FXML private  TextField txtFullName;
    @FXML private  TextField txtEmail;
    @FXML private  TextField txtUsername;
    @FXML private  CheckBox chkAdmin;
    @FXML private  CheckBox chkOrganiser;
    @FXML private  CheckBox chkPassword;
    @FXML private  HBox hbBottom;
    @FXML private  Button btnNewUser;
    @FXML private  Button btnDeleteUser;
    @FXML private  Button btnSaveUser;
    @FXML private HBox hboxRoles;
    @FXML private HBox hboxResetPassAdmin;
    @FXML private TextField txtOldPass;
    @FXML private TextField txtNewPass1;
    @FXML private TextField txtNewPass2;
    @FXML private HBox hboxSnack;
    @FXML private VBox vBox;

    private boolean isNewUser;
    private User userToReturn;
    private User userToEdit;
    private boolean isSaveUser;

    private final BLLManager bllManager;

    {
        try {
            bllManager = new BLLManager();
        } catch (TicketExceptions e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isNewUser = true;
        userToReturn = null;
        userToEdit = null;
        isSaveUser = false;
        hboxSnack.setVisible(false);
        hboxSnack.setManaged(false);
    }

    @FXML private void btnSaveClicked(ActionEvent event) {
        if (checkFieldsCorrectness()) {
            isSaveUser = true;
            if (isNewUser){
                userToReturn = new User(-1, txtUsername.getText(),
                        txtFullName.getText().toLowerCase().trim() + "01", txtEmail.getText(),
                        txtFullName.getText(), getRole());
            userToReturn.setPassword(bllManager.hashPass(txtFullName.getText().trim().toLowerCase() + "01"));
            userToReturn.setId(bllManager.insertNewUser(userToReturn));
            }

            else {
                userToEdit.setFullName(txtFullName.getText());
                userToEdit.setEmail(txtEmail.getText());
                userToEdit.setUsername(txtUsername.getText());
                userToEdit.setRoleID(getRole());
                userToReturn = userToEdit;
                bllManager.userToUpdate(userToReturn);
            }

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

        //Gives the warning snackbar at the bottom of the screen
        else {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("FXML/snackbar.fxml"));
            Parent snackBar = null;
            try {
                snackBar = loader.load();
                SnackbarController controller = loader.getController();
                double windowWidth = vBox.getScene().getWidth();
                hboxSnack.getChildren().clear();
                hboxSnack.getChildren().add(snackBar);
                String warningTextTitle = "Please check the correctness of all the fields.";
                String warningText = "(Full name (min 3 character long), e-mail is correct\nand user name(no space and min 2 character long).";
                Image img = new Image(getClass().getResourceAsStream("../Images/exclamation-triangle.png"));

                controller.setSnackBar(warningTextTitle, warningText, img, 5, hboxSnack, windowWidth, controller.getDANGER(), controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    private boolean checkFieldsCorrectness() {
        boolean isCorrect = true;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (txtFullName.getText().isEmpty() || txtFullName.getText().length() < 3) {
            isCorrect = false;
            System.out.println("Problem with txtFullName");
        }
        if(!txtEmail.getText().matches(emailRegex)) {
            isCorrect = false;
            System.out.println("Problem with the txtEmail");
        }
        if (txtUsername.getText().length() < 2 && txtUsername.getText().contains(" ")) {
            isCorrect = false;
            System.out.println("Problem with the txtUsername");
        }
        return  isCorrect;
    }

    private int getRole() {
        if (chkAdmin.isSelected() && chkOrganiser.isSelected())
            return 5;
        else if (chkAdmin.isSelected() && !chkOrganiser.isSelected())
            return 3;
        else if (!chkAdmin.isSelected() && chkOrganiser.isSelected())
            return 4;
        else return 0;
    }


    public void clearFields() {
        txtFullName.clear();
        txtEmail.clear();
        txtUsername.clear();
        chkAdmin.setSelected(false);
        chkOrganiser.setSelected(false);
        chkPassword.setSelected(false);
    }

    public void hideAdminButtons() {
        lblUserTitle.setText("Profile");
        vboxForm.setVisible(true);
        txtFullName.setDisable(true);
        txtUsername.setDisable(true);
        hboxRoles.setVisible(false);
        hboxRoles.setManaged(false);
        hboxResetPassAdmin.setVisible(false);
        hboxResetPassAdmin.setManaged(false);

    }

    public void hidePasswordFields() {
        vbPassword.setVisible(false);
        vbPassword.setManaged(false);
    }

    public void setRole (String role) {
        if (role.equals("Admin") || role.equals("SuperUser"))
                hidePasswordFields();
        else
            hideAdminButtons();

    }

    public void isNewUser(boolean b) {
        this.isNewUser = b;
        if (isNewUser) {
            clearFields();
            hboxResetPassAdmin.setVisible(false);
        }
    }

    public void setUserToEdit (User user) {
        System.out.println(user);
        this.userToEdit = user;
        populateFields(user);
    }

    private void populateFields(User user) {
        txtFullName.setText(user.getFullName());
        txtEmail.setText(user.getEmail());
        txtUsername.setText(user.getUsername());
        if (user.getRole().equals("Admin") || user.getRole().equals("SuperUser"))
            chkAdmin.setSelected(true);
        if (user.getRole().equals("Event Coordinator") || user.getRole().equals("SuperUser"))
            chkOrganiser.setSelected(true);
    }

    public User getUserToReturn() {
        return userToReturn;
    }

    public boolean getIsSaveUser() {
        return isSaveUser;
    }
}

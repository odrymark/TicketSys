package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.bll.PasswordValidator;
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
    @FXML private  RadioButton chkAdmin;
    @FXML private  RadioButton chkOrganiser;
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
    @FXML private Label lblPassInfo;

    private boolean isNewUser;
    private User userToReturn;
    private User userToEdit;
    private User loggedinUser;
    private boolean isSaveUser;
    boolean isPasswordChanging;

    ToggleGroup group;




    private boolean isAdminEditingSelf;

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
        loggedinUser = null;
        isSaveUser = false;
        isPasswordChanging = false;
        isAdminEditingSelf = false;
        hboxSnack.setVisible(false);
        hboxSnack.setManaged(false);
        group = new ToggleGroup();
        chkAdmin.setToggleGroup(group);
        chkOrganiser.setToggleGroup(group);
        setChkPassListener();
    }



    @FXML private void btnSaveClicked(ActionEvent event) {
        String errorMessage = checkFieldsCorrectness();
        if (errorMessage.equals("Error:")) {
            isSaveUser = true;
            String defaultPassword = bllManager.hashPass(txtUsername.getText(),
                    (txtFullName.getText().toLowerCase().replaceAll("\\s", "") + "01"));
            if (isNewUser){
                userToReturn = new User(-1, txtUsername.getText(),
                        defaultPassword, txtEmail.getText(),
                        txtFullName.getText(), getRole());
            userToReturn.setId(bllManager.insertNewUser(userToReturn));
            }

            else {
                userToEdit.setFullName(txtFullName.getText());
                userToEdit.setEmail(txtEmail.getText());
                userToEdit.setUsername(txtUsername.getText());
                userToEdit.setRoleID(getRole());
                if(chkPassword.isSelected())
                    userToEdit.setPassword(defaultPassword);
                if (isPasswordChanging) {
                    userToEdit.setPassword(bllManager.hashPass(txtUsername.getText(), txtNewPass1.getText()));
                    isPasswordChanging = false;
                }
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
                String warningText = errorMessage;
                Image img = new Image(getClass().getResourceAsStream("../Images/exclamation-triangle.png"));

                controller.setSnackBar(warningTextTitle, warningText, img, 5, hboxSnack, windowWidth, controller.getDANGER(), controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    private String checkFieldsCorrectness() {
        String errorMessage = "Error:";
        int errorNo = 0;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (txtFullName.getText().isEmpty() || txtFullName.getText().length() < 3) {
            errorNo++;
            errorMessage += " User's full name must be at least 3 characters long";
            setRedBorder (txtFullName);
        }
        if(!txtEmail.getText().matches(emailRegex)) {
            if (errorNo > 0)
                errorMessage += "\n";
            errorMessage += " User's email address is incorrect";
            setRedBorder (txtEmail);
            System.out.println("Problem with the txtEmail");
        }
        if (txtUsername.getText().length() < 2 && txtUsername.getText().contains(" ")) {
            if (errorNo > 0)
                errorMessage += "\n";
            errorMessage += " User's username must be at least 2 characters long";
            setRedBorder (txtUsername);
            System.out.println("Problem with the txtUsername");
        }

        //Checking password change fields
        if (vbPassword.isVisible() && !txtNewPass1.getText().isEmpty()) {
            PasswordValidator validator = new PasswordValidator();
            if (!bllManager.checkPassword(txtUsername.getText(), txtOldPass.getText())) {
                errorMessage += " Old password is incorrect";
                setRedBorder (txtOldPass);
            }
            else if(!txtNewPass1.getText().equals(txtNewPass2.getText())) {
                errorMessage += " New password fields are not matching";
            }
            else if (!validator.isValidPassword(txtNewPass1.getText())) {
                errorMessage += " New password must be at least 5 characters,\ncontains at least a number and a special character.";
                setRedBorder (txtNewPass1);
                setRedBorder (txtNewPass2);
            }
            else
                isPasswordChanging = true;
        }

        //Checking role settings
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        if (selectedRadioButton == null) {
            System.out.println("No role is selected");
            errorMessage += " User's role must be selected";
            if(!hboxRoles.getStyleClass().contains("error")) {
                hboxRoles.getStyleClass().add("error");
            }
            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) { // If a radio button is selected
                    hboxRoles.getStyleClass().remove("error");
                }
            });
        }
        return  errorMessage;
    }

    private void setRedBorder(TextField txtField) {
        txtField.requestFocus();
        if (!txtField.getStyle().contains("error")) {
            txtField.getStyleClass().add("error");
        }
        System.out.println("Problem with txtFullName");
        txtField.requestFocus();
        txtField.textProperty().addListener((observable, oldValue, newValue) -> {
            txtField.getStyleClass().remove("error");
        });
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
        if ((role.equals("Admin") || role.equals("SuperUser")) && !isAdminEditingSelf || isNewUser)
                hidePasswordFields();
        else if (!role.equals("Admin") && !role.equals("SuperUser"))
            hideAdminButtons();
    }

    public void isNewUser(boolean b) {
        this.isNewUser = b;
        if (isNewUser) {
            clearFields();
            hboxResetPassAdmin.setVisible(false);
            hboxResetPassAdmin.setManaged(false);
            hidePasswordFields();
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

    public void setLoggedinUser(User loggedinUser) {
        this.loggedinUser = loggedinUser;
    }

    public void setAdminEditingSelf(boolean adminEditingSelf) {
        isAdminEditingSelf = adminEditingSelf;
    }

    private void setChkPassListener() {
        chkPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lblPassInfo.setText("Default password will be recovered: full name\nlower case, no space + \"01\"");
                lblPassInfo.setVisible(true);
                lblPassInfo.setManaged(true);
            }
            else {
                lblPassInfo.setVisible(false);
                lblPassInfo.setManaged(false);
            }
        });
    }
}

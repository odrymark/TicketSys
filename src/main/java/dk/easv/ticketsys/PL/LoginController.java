package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private MFXPasswordField password;
    @FXML
    private MFXTextField username;

    private BLLManager bllManager;

    @FXML
    public void initialize() {
        try {
            bllManager = new BLLManager();
        } catch (TicketExceptions e) {
            e.printStackTrace();
            System.out.println("Error initializing BLL!");
        }
    }

    /*@FXML
    private void login() {
        if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
            try {
                //FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/admin.fxml"));
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/coordinator.fxml"));

                Scene scene = new Scene(fxmlLoader.load());
                AdminController aController;
                CoordinatorController cController;
                //aController = fxmlLoader.getController();
                cController = fxmlLoader.getController();
                bllManager = new BLLManager();
                User loggedIn = bllManager.loginTest();
                //aController.setLoggedInUser(loggedIn);
                cController.setLoggedinUser(loggedIn);
                Stage stage = (Stage) username.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }*/

    @FXML
    private void login() {
        String uName = username.getText().trim();
        String pwd = password.getText().trim();

        if (uName.isEmpty() || pwd.isEmpty()) {
            System.out.println("Please fill in username and password!");
            return;
        }

        User foundUser = bllManager.login(uName, pwd);

        if (foundUser == null) {
            System.out.println("Invalid username or password!");
            return;
        }

        int roleID = foundUser.getRoleID();
        System.out.println("Login roleId:" + roleID);
        switch (roleID) {
            case 3:
                openAdminWindow(foundUser);
                break;
            case 4:
                openCoordinatorWindow(foundUser);
                break;
            default:
                System.out.println("Unknown roleID: " + roleID);
        }
    }

    private void openCoordinatorWindow(User foundUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/coordinator.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            CoordinatorController cController;
            cController = fxmlLoader.getController();
            cController.setLoggedinUser(foundUser);
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error opening Coordinator window!");
            e.printStackTrace();
        }
    }

    private void openAdminWindow(User foundUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/admin.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            AdminController aController;
            aController = fxmlLoader.getController();
            aController.setLoggedInUser(foundUser);
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error opening Admin window!");
            e.printStackTrace();
        }
    }
    }


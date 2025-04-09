package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddCoordinatorController implements Initializable {
    @FXML private VBox vbParent;
    @FXML private Label lblEventName;
    @FXML private VBox vbUsers;
    @FXML private Button btnSave;
    private BLLManager bllManager;
    private ArrayList<User> allCoordinator;
    private int eventID;
    private HashMap<Integer, CheckBox> userCheckBoxMap;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            bllManager = new BLLManager();
        } catch (TicketExceptions e) {
            throw new RuntimeException(e);
        }
        loadUsers();
    }

    private void loadUsers() {
        allCoordinator = new ArrayList<>();
        eventID = -1;
        userCheckBoxMap = new HashMap<>();
        allCoordinator.addAll(bllManager.getAllCoordinators());
        for (User coordinator : allCoordinator) {
            CheckBox cb = new CheckBox();
            cb.setText(coordinator.getFullName());
            cb.setId(coordinator.getId()+"");
            userCheckBoxMap.put(coordinator.getId(), cb);
            vbUsers.getChildren().add(cb);
            Platform.runLater(this::setWindowHeight);
        }
    }

    public void getCoordinators (Event event) {
        this.eventID = event.getId();
        ArrayList<User> coordinators = bllManager.getActiveCoordinators(eventID);
        lblEventName.setText(event.getTitle());
        for (User coordinator : coordinators) {
            System.out.println(coordinator.toString());
            CheckBox cb = userCheckBoxMap.get(coordinator.getId());
            cb.setSelected(true);
        }
    }

    @FXML private void btnSaveClicked(ActionEvent e, int eventID) {
        //TODO: Save the selected coordinators for the event

        System.out.println("Saving...");
        //Closing the window
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();

    }

    private void setWindowHeight() {
        double usersHeight = vbUsers.getHeight();
        double usersWidth = vbUsers.getWidth();
        vbParent.setPrefHeight(usersHeight+50);
        vbParent.setPrefWidth(usersWidth+10);
    }
}

package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.Ticket;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GetTicketController {
    @FXML private Label eventTitleLabel;
    @FXML private ImageView eventImageView;
    @FXML private ListView<String> participantsList;
    @FXML private TextField participantEmailField; // Змінено з participantNameField
    @FXML private Button btnGetTicket;
    @FXML private Button btnGetCoupon;
    @FXML private Button closeButton;

    private Event event;
    private BLLManager bllManager;

    public GetTicketController() {
        try {
            bllManager = new BLLManager();
        } catch (TicketExceptions e) {
            e.printStackTrace();
        }
    }

    public void setEvent(Event event) {
        this.event = event;
        updateUI();
    }

    private void updateUI() {
        if (event != null) {
            eventTitleLabel.setText(event.getTitle());
            // Додати завантаження зображення
        }
    }

    @FXML
    private void addParticipant(ActionEvent actionEvent) {
        String email = participantEmailField.getText().trim();
        if (!email.isEmpty() && email.contains("@")) { // check mail @
            if (!participantsList.getItems().contains(email)) {
                participantsList.getItems().add(email);
            }
            //We remove the text field clearing so that the email remains
            // participantEmailField.clear();
        } else {
            showAlert("Invalid Email", "Please enter a valid email address");
        }
    }


    @FXML
    private void deleteParticipant(ActionEvent actionEvent) {
        int selectedIndex = participantsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            participantsList.getItems().remove(selectedIndex);
        }
    }

    @FXML
    private void getTicket(ActionEvent actionEvent) {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/ticket.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            TicketController ticketController = fxmlLoader.getController();
            ticketController.getEvent(event);

            stage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void getCoupon(ActionEvent actionEvent) {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/Coupon.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            TicketController ticketController = fxmlLoader.getController();
            ticketController.getEvent(event);

            stage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void close(ActionEvent actionEvent) {
        closeWindow(actionEvent);
    }

    @FXML
    private void saveEvent(ActionEvent actionEvent) {
        closeWindow(actionEvent);
    }

    private void closeWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
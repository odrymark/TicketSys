package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Customer;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.TicketType;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class GetTicketController {

    @FXML private Label eventTitleLabel;
    @FXML private ListView<String> participantsList;
    @FXML private TextField participantEmailField;
    @FXML private ComboBox<TicketType> couponTypeComboBox;
    @FXML private TextField customerNameField;
    @FXML private TextField customerEmailField;
    @FXML private TableView<Customer> customersTable;

    private Event event;
    private BLLManager bllManager;

    @FXML
    public void initialize() {
        try {
            bllManager = new BLLManager();
            couponTypeComboBox.setItems(FXCollections.observableArrayList(bllManager.getTicketTypes()));
            if (!couponTypeComboBox.getItems().isEmpty()) {
                couponTypeComboBox.getSelectionModel().selectFirst();
            }

            TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
            nameCol.setPrefWidth(150);
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
            emailCol.setPrefWidth(250);
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

            customersTable.getColumns().setAll(nameCol, emailCol);

            loadCustomers();
        } catch (TicketExceptions e) {
            e.printStackTrace();
            System.err.println("Error initializing BLLManager in GetTicketController!");
        }
    }

    public void setEvent(Event event) {
        this.event = event;
        eventTitleLabel.setText(event.getTitle());
    }

    @FXML
    private void addParticipant(ActionEvent actionEvent)  {
        String name = customerNameField.getText().trim();
        String email = customerEmailField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Invalid Name", "Please enter a valid name.");
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }
        try {
            int newId = bllManager.insertCustomer(name, email);
            if (newId > 0) {
                System.out.println("Customer saved with ID: " + newId);
                customerNameField.clear();
                customerEmailField.clear();
                loadCustomers();
            } else {
                System.out.println("Error saving customer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save customer.");
        }
    }

    private void loadCustomers() {
        try {
            List<Customer> customerList = bllManager.getAllCustomers();
            ObservableList<Customer> customers = FXCollections.observableArrayList(customerList);
            customersTable.setItems(customers);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load customers.");
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
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/ticket.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            TicketController ticketController = fxmlLoader.getController();
            ticketController.getEvent(event);

            String customerEmail = participantEmailField.getText().trim();

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void getCoupon(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/coupon.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            TicketType selectedCouponType = couponTypeComboBox.getValue();
            CouponController couponController = fxmlLoader.getController();
            couponController.setEvent(event);
            couponController.setCouponType(selectedCouponType);

            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
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

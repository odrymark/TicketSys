package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Customer;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.Ticket;
import dk.easv.ticketsys.be.TicketType;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GetTicketController {

    @FXML private Label eventTitleLabel;
    @FXML private ImageView eventImageView;
    @FXML private TextField customerNameField;
    @FXML private TextField customerEmailField;
    @FXML private TableView<Customer> customersTable;
    @FXML private ComboBox<TicketType> couponTypeComboBox;

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

            loadCustomers();
        } catch (TicketExceptions e) {
            e.printStackTrace();
            showAlert("Error", "Could not initialize BLLManager properly.");
        }
    }

    public void setEvent(Event event) {
        this.event = event;
        eventTitleLabel.setText(event.getTitle());
        loadCustomers();
    }

    @FXML
    private void addParticipant(ActionEvent actionEvent) {
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

                Customer newCustomer = new Customer(newId, name, email);
                customersTable.getItems().add(newCustomer);

                customerNameField.clear();
                customerEmailField.clear();
            } else {
                showAlert("Database Error", "Failed to save customer.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save customer: " + e.getMessage());
        }
    }


/*
    @FXML
    private void addParticipant(ActionEvent actionEvent) {
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

                Customer newCustomer = new Customer(newId, name, email);


            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/ticket.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            TicketController ticketController = loader.getController();
            ticketController.getEvent(event);
            ticketController.setCustomer(newCustomer);
            ticketController.printTicket();
            stage.showAndWait();


                customerNameField.clear();
                customerEmailField.clear();
                loadCustomers();
            } else {
                showAlert("Database Error", "Failed to save customer.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save customer: " + e.getMessage());
        }
    }
*/

    @FXML
    private void deleteParticipant(ActionEvent actionEvent) {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Selection Error", "Please select a customer to delete.");
            return;
        }
        try {
            boolean deleted = bllManager.deleteCustomer(selectedCustomer.getId());
            if (deleted) {
                System.out.println("Customer deleted successfully.");
                loadCustomers();
            } else {
                showAlert("Delete Error", "Failed to delete customer.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete customer: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            if (event == null) {
                return;
            }

            List<Customer> customerList = bllManager.getCustomersForEvent(event.getId());
            customersTable.setItems(FXCollections.observableArrayList(customerList));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load event's customers: " + e.getMessage());
        }
    }


    @FXML
    private void getTicket(ActionEvent actionEvent) {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("No customer selected", "Please select a customer from the table first.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/ticket.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            TicketController ticketController = fxmlLoader.getController();
            ticketController.getEvent(event);
            ticketController.setCustomer(selectedCustomer);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void getCoupon(ActionEvent actionEvent) {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("No customer selected", "Please select a customer from the table first.");
            return;
        }

        TicketType selectedCouponType = couponTypeComboBox.getValue();
        if (selectedCouponType == null) {
            showAlert("No coupon type selected", "Please select a coupon type.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/coupon.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            CouponController couponController = fxmlLoader.getController();
            couponController.setEvent(event);
            couponController.setCustomer(selectedCustomer);
            couponController.setCouponType(selectedCouponType);

            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void close(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void saveEvent(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Changes");
        alert.setHeaderText(null);
        alert.setContentText("Event changes saved!");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

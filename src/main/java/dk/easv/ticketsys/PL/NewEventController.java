package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.TicketType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NewEventController implements Initializable {
    @FXML private Label lblConnectToEvent;
    @FXML private Label lblNewTicketTitle;
    @FXML private HBox hbNewEventTitle;
    @FXML private HBox hbConnectEvent;
    @FXML private ChoiceBox choiceEvents;
    @FXML private CheckBox chkSpecialInNewTicketType;
    @FXML private GridPane gridPaneForm;
    @FXML private Label lblTitle;
    @FXML private TextField txtTitle;
    @FXML private TextField txtStartDate;
    @FXML private TextField txtEndDate;
    @FXML private TextField txtLocation;
    @FXML private TextArea txtaLocation;
    @FXML private ChoiceBox dropEventType;
    @FXML private TextField txtNewEventType;
    @FXML private Button btnEventType;
    @FXML private TextField txtFileName;
    @FXML private Button btnImage;
    @FXML private FlowPane flowTicketTypes;
    @FXML private FlowPane flowSpecialTickets;
    @FXML private TextArea txtaDescription;
    @FXML private Button btnAddTicketType;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private VBox vboxShader;
    @FXML private VBox vboxNewTicketType;
    @FXML private TextField txtNewTicketType;
    @FXML private Button btnNewTicketCancel;
    @FXML private Button btnNewTicketSave;
    @FXML private DatePicker dateEnd;
    @FXML private Spinner spEndHour;
    @FXML private Spinner spEndMinute;
    @FXML private DatePicker dateStart;
    @FXML private Spinner spStartHour;
    @FXML private Spinner spStartMinute;


    public void setEventToEdit(Event eventToEdit) {
        if (eventToEdit != null) {
            txtTitle.setText(eventToEdit.getTitle());
            txtStartDate.setText(eventToEdit.getStartDate());
            if (eventToEdit.getEndDate() != null)
                txtEndDate.setText(eventToEdit.getEndDate().toString());
            txtLocation.setText(eventToEdit.getLocation());
            if (eventToEdit.imageSrcProperty() != null)
                txtFileName.setText(eventToEdit.imageSrcProperty().getValue());
            txtaLocation.setText(eventToEdit.getLocationGuide());
            choiceEvents.getSelectionModel().select(eventToEdit.getTypeOfEvent());
            txtaDescription.setText(eventToEdit.getNotes());
            //TODO: select ticket types
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setDummyTicketTypes();
        getDummyType();
        setSpinners();
        Platform.runLater(this::setTicketTypesHeight);
    }

    @FXML private void btnAddTicketTypeClicked(ActionEvent event) {
        showNewTicketPopup();
    }

    @FXML private void btnCancelClicked(ActionEvent event) {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/coordinator.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @FXML private void btnSaveClicked(ActionEvent event) {
        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = dateEnd.getValue();
        String startDateString = startDate.toString() + " "
                + formatTime((Integer) spStartHour.getValueFactory().getValue()) + ":"
                + formatTime((Integer) spStartMinute.getValueFactory().getValue());
        Event eventToSave = new Event(txtTitle.getText(), startDateString, txtLocation.getText(), 1, "a.jpg" );
        eventToSave.setEndDate(endDate.toString() + " " + formatTime((Integer) spEndHour.getValueFactory().getValue())
                + ":" + formatTime((Integer) spEndMinute.getValueFactory().getValue()));
        eventToSave.setLocationGuide(txtaLocation.getText());
        eventToSave.setNotes(txtaDescription.getText());
        eventToSave.setTypeOfEvent(dropEventType.getItems().indexOf(dropEventType.getSelectionModel().getSelectedItem()));
        System.out.println(eventToSave.toString());
        System.out.println("Saving...");
    }

    private String formatTime(int value) {
        if (value < 10)
            return "0" + value;
        return "" + value;
    }

    @FXML private void btnNewTicketCancelClicked(ActionEvent event) {
        closeNewTicketType();
    }

    @FXML private void btnNewTicketSaveClicked(ActionEvent event) {
        //Do some stuff here
        closeNewTicketType();
    }

    public Button getCancelButton() {
        return btnCancel;
    }

    public Button getSaveButton() {
        return btnSave;
    }

    @FXML private void btnEventTypeClicked(ActionEvent event) {
        if (dropEventType.isManaged()) {
            dropEventType.setManaged(false);
            dropEventType.setVisible(false);
            txtNewEventType.setManaged(true);
            txtNewEventType.setVisible(true);
            btnEventType.setText("Cancel");
        }
        else {
            txtNewEventType.setManaged(false);
            txtNewEventType.setVisible(false);
            dropEventType.setManaged(true);
            dropEventType.setVisible(true);
            btnEventType.setText("New event type");
        }
    }

    @FXML private void btnImageClicked(ActionEvent event) {
        System.out.println("Browse");
    }

    @FXML private void btnDeleteTicketType(ActionEvent event) {
        showNewTicketPopup();
        lblNewTicketTitle.setText("Delete ticket type");
        hbNewEventTitle.setVisible(false);
        hbConnectEvent.setVisible(true);
        lblConnectToEvent.setText("Ticket types");
        chkSpecialInNewTicketType.setVisible(false);
        btnNewTicketSave.setText("Delete");


        //Add ticket types to the choicebox
        choiceEvents.getItems().clear();
    }


    private void showNewTicketPopup() {
        vboxShader.setVisible(true);
        vboxNewTicketType.setVisible(true);
        chkSpecialInNewTicketType.setSelected(false);
        chkSpecialInNewTicketType.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (chkSpecialInNewTicketType.isSelected()) {
                choiceEvents.setVisible(true);
                hbConnectEvent.setVisible(true);
                choiceEvents.getItems().clear();
                choiceEvents.getItems().add("All");
                if (!(txtTitle.getText() !=null) || !txtTitle.getText().isEmpty()) {
                    choiceEvents.getItems().add(txtTitle.getText());
                }
                //Add all the other events to the choicebox
            }
            else {
                hbConnectEvent.setVisible(false);
                //choiceEvents.setVisible(false);
            }
        });
    }

    private void closeNewTicketType() {
        txtNewTicketType.setText("");
        vboxNewTicketType.setVisible(false);
        vboxShader.setVisible(false);
        hbNewEventTitle.setVisible(true);
        //hbNewEventTitle.setManaged(true);
        //hbConnectEvent.setManaged(false);
        hbConnectEvent.setVisible(false);
        //chkSpecialInNewTicketType.setManaged(true);
        chkSpecialInNewTicketType.setVisible(true);
        lblConnectToEvent.setText("Connect to event");
        btnNewTicketSave.setText("Save");
        lblNewTicketTitle.setText("New ticket type");

    }

    private void setSpinners() {
// Configure Hour Spinner (0-23)
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 24, 12);
        spStartHour.setValueFactory(hourFactory);
        spStartHour.getValueFactory().setValue(12);

        // Configure Minute Spinner (0-59)
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 60, 0);
        spStartMinute.setValueFactory(minuteFactory);

        // Make spinners editable
        spStartHour.setEditable(true);
        spStartMinute.setEditable(true);

        // Configure Hour Spinner (0-23)
        SpinnerValueFactory<Integer> hourFactoryEnd = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 24, 12);
        spEndHour.setValueFactory(hourFactoryEnd);
        spEndHour.getValueFactory().setValue(12);

        // Configure Minute Spinner (0-59)
        SpinnerValueFactory<Integer> minuteFactoryEnd = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 60, 0);
        spEndMinute.setValueFactory(minuteFactoryEnd);

        // Make spinners editable
        spEndHour.setEditable(true);
        spEndMinute.setEditable(true);

        //Listener to get around
        // Add wrap-around behavior for hour spinner
        spStartHour.getEditor().textProperty().addListener((obs, oldValue, newValue) -> wrapSpinner(spStartHour, 0, 23));
        spEndHour.getEditor().textProperty().addListener((obs, oldValue, newValue) -> wrapSpinner(spEndHour, 0, 23));

        // Add wrap-around behavior for minute spinner
        spStartMinute.getEditor().textProperty().addListener((obs, oldValue, newValue) -> wrapSpinner(spStartMinute, 0, 59));
        spEndMinute.getEditor().textProperty().addListener((obs, oldValue, newValue) -> wrapSpinner(spEndMinute, 0, 59));

        //Set date fields
        // Disable past dates in end date picker
        dateEnd.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(dateStart.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });

        // Update end date's min date when start date changes
        dateStart.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (dateEnd.getValue() == null || dateEnd.getValue().isBefore(newDate)) {
                dateEnd.setValue(dateStart.getValue());
                dateEnd.setDisable(false);
            }
            updateEndDateRestrictions(newDate);
        });
    }

    private void updateEndDateRestrictions(LocalDate minDate) {
        dateEnd.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(minDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;"); // Grey out disabled dates
                }
            }
        });
    }

    private void wrapSpinner(Spinner<Integer> spinner, int min, int max) {
        try {
            int value = Integer.parseInt(spinner.getEditor().getText());

            if (value < min) {
                spinner.getValueFactory().setValue(max); // Jump to max if below min
            } else if (value > max) {
                spinner.getValueFactory().setValue(min); // Jump to min if above max
            }
        } catch (NumberFormatException e) {
            // Reset to min if input is invalid
            spinner.getValueFactory().setValue(min);
        }
    }

    public ArrayList<TicketType> getDummyTicketTypes() {
        ArrayList<TicketType> ticketTypes = new ArrayList<>();
        ticketTypes.add(new TicketType(1, "Normal", false));
        ticketTypes.add(new TicketType(2, "VIP", false));
        ticketTypes.add(new TicketType(3, "Guest", false));
        ticketTypes.add(new TicketType(4, "Fast track", false));
        ticketTypes.add(new TicketType(5, "Participant", false));
        ticketTypes.add(new TicketType(6, "Free beer", true));
        ticketTypes.add(new TicketType(7, "Free pizza", true));
        ticketTypes.add(new TicketType(8, "Free taxi", true));
        ticketTypes.add(new TicketType(9, "Chuck Noris", false));
        ticketTypes.add(new TicketType(10, "Madonna", false));
        ticketTypes.add(new TicketType(12, "Steve Jobs", false));
        return ticketTypes;
    }
    public void getDummyType() {
        ArrayList<String> dummyTypes = new ArrayList<>();
        dummyTypes.add("Normal");
        dummyTypes.add("Culture");
        dummyTypes.add("Sport");
        dummyTypes.add("Hiking");
        dummyTypes.add("Sight seeing");
        dummyTypes.add("Dance");
        dropEventType.getItems().clear();
        dropEventType.getItems().addAll(dummyTypes);
    }

    private void setDummyTicketTypes() {
        ArrayList<TicketType> ticketTypes = new ArrayList<>(getDummyTicketTypes());


        for (TicketType ticketType : ticketTypes) {
            CheckBox cb = new CheckBox();
            cb.setText(ticketType.getName());
            cb.setId("cb_" + ticketType.getId());

            if (ticketType.getSpecial()) {
                flowSpecialTickets.getChildren().add(cb);
            } else {
                flowTicketTypes.getChildren().add(cb);
            }
        }
    }

    private void setTicketTypesHeight() {
        RowConstraints ticketRow = gridPaneForm.getRowConstraints().get(7);
        RowConstraints ticketRow2 = gridPaneForm.getRowConstraints().get(8);
        double flowTicketHeight = flowTicketTypes.getHeight();
        double flowSpecialTicketHeight = flowSpecialTickets.getHeight();
        ticketRow.setPrefHeight(flowTicketHeight+5);
        ticketRow2.setPrefHeight(flowSpecialTicketHeight);
    }



}

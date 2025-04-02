package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.EventType;
import dk.easv.ticketsys.be.TicketType;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import static java.lang.Integer.valueOf;

public class NewEventController implements Initializable {
    @FXML private Label lblConnectToEvent;
    @FXML private Label lblNewTicketTitle;
    @FXML private HBox hbNewEventTitle;
    @FXML private HBox hbConnectEvent;
    @FXML private ChoiceBox<TicketType> choiceEvents;
    @FXML private CheckBox chkSpecialInNewTicketType;
    @FXML private GridPane gridPaneForm;
    @FXML private Label lblTitle;
    @FXML private TextField txtTitle;
    @FXML private TextField txtStartDate;
    @FXML private TextField txtEndDate;
    @FXML private TextField txtLocation;
    @FXML private TextArea txtaLocation;
    @FXML private TextField txtFileName;
    @FXML private Button btnImage;
    @FXML private FlowPane flowTicketTypes;
    @FXML private FlowPane flowSpecialTickets;
    @FXML private TextArea txtaDescription;
    @FXML private Button btnAddTicketType;
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

    private final ArrayList<TicketType> dummyTicketTypes = new ArrayList<>();
    private final BLLManager bllManager = new BLLManager();
    private int isEditing;
    private Boolean isDeletingEventType;
    private User loggedinUser;
    private Event eventToSave;

    public void setTicketTypeHashMap(HashMap<Integer, TicketType> ticketTypeHashMap) {
        this.ticketTypeHashMap = ticketTypeHashMap;
    }

    public void addTicketType (int id, TicketType ticketType) {
        this.ticketTypeHashMap.put(id, ticketType);
    }

    public TicketType getTicketType(int id) {
        return this.ticketTypeHashMap.get(id);
    }

    private HashMap<Integer, TicketType> ticketTypeHashMap;

    public NewEventController() throws TicketExceptions {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketTypeHashMap = new HashMap<>();
        isEditing = 0;
        loggedinUser = null;
        eventToSave = null;
        isDeletingEventType = false;
        // 1) load "fake" types into dummyTicketTypes
        dummyTicketTypes.addAll(bllManager.getTicketTypes());
        // 3) display CheckBoxes on the form
        setTicketTypes();
        // 4) Configure spinners (hours/minutes) and date change listeners
        setSpinners();
        // 5) After loading the layout, adjust the height for flowTicketTypes
        Platform.runLater(this::setTicketTypesHeight);
    }

    public void setEventToEdit(Event eventToEdit) {
        if (eventToEdit != null) {
            isEditing = eventToEdit.getId();
            System.out.println(eventToEdit);
            txtTitle.setText(eventToEdit.getTitle());
            String[] startDateSplit = eventToEdit.getStartDate().split(" ");
            dateStart.setValue(LocalDate.parse(startDateSplit[0]));
            String[] startTimeSplit = startDateSplit[1].split(":");
            spStartHour.getValueFactory().setValue(Integer.parseInt(startTimeSplit[0]));
            spStartMinute.getValueFactory().setValue(Integer.parseInt(startTimeSplit[1]));
            if (eventToEdit.getEndDate() != null) {
                String[] endDateSplit = eventToEdit.getEndDate().split(" ");
                String[] endTimeSplit = endDateSplit[1].split(":");
                dateEnd.setValue(LocalDate.parse(endDateSplit[0]));
                spEndHour.getValueFactory().setValue(Integer.parseInt(endTimeSplit[0]));
                spEndMinute.getValueFactory().setValue(Integer.parseInt(endTimeSplit[1]));
            }
            txtLocation.setText(eventToEdit.getLocation());
            if (eventToEdit.getImgSrc() != null)
                txtFileName.setText(eventToEdit.getImgSrc());
            txtaLocation.setText(eventToEdit.getLocationGuide());
            txtaDescription.setText(eventToEdit.getNotes());
            //select ticket types
            HashMap<Integer, TicketType> editTicketTypeHash = new HashMap<>();
            for (TicketType ticketType : eventToEdit.getTicketTypes()) {
                System.out.println(ticketType.toString());
                editTicketTypeHash.put(ticketType.getId(), ticketType);
            }
            for (Node node : flowTicketTypes.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    int id = Integer.parseInt(cb.getId().replace("cb_", ""));
                    System.out.println(id + "");
                    if (editTicketTypeHash.containsKey(id))
                        Platform.runLater(() -> cb.setSelected(true));
                }
            }
            for (Node node : flowSpecialTickets.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    int id = Integer.parseInt(cb.getId().replace("cb_", ""));
                    if (editTicketTypeHash.containsKey(id));
                        cb.setSelected(true);
                }
            }
            //TODO: update the ticket types as well for the event
        }
    }

    @FXML private void btnAddTicketTypeClicked(ActionEvent event) {
        isDeletingEventType = false;
        showNewTicketPopup(true);
    }

    @FXML private void btnSaveClicked(ActionEvent event) throws TicketExceptions {
        eventToSave = getEventToSave();
        if (eventToSave == null) {
            System.out.println("Event is null!");
            return;
        }
        if (isEditing > 0) {
            eventToSave.setId(isEditing);
            if (bllManager.updateEvent(eventToSave))
                System.out.println("Event updated success.");
            else {
                System.out.println("Event not updated.");
                return;
            }
        }
        else {
            int newId = bllManager.uploadNewEvent(eventToSave);
            if (newId > 0) {
                eventToSave.setId(newId);
                //btnCancelClicked();
            } else {
                System.out.println("Upload did not succeed!");
                return;
            }
        }
        System.out.println("Saving...");
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

    }

    private Event getEventToSave() {
        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = dateEnd.getValue();
        if (startDate == null) {
            System.out.println("Start date is null - please select it!");
            return null;
        }
        String startDateString = startDate.toString() + " "
                + formatTime((Integer) spStartHour.getValueFactory().getValue()) + ":"
                + formatTime((Integer) spStartMinute.getValueFactory().getValue()) + ":00";
        Event eventToSave = new Event(txtTitle.getText(), startDateString, txtLocation.getText(), txtaDescription.getText(), loggedinUser.getId() );
        eventToSave.setEndDate(endDate.toString() + " " + formatTime((Integer) spEndHour.getValueFactory().getValue())
                + ":" + formatTime((Integer) spEndMinute.getValueFactory().getValue()) + ":00");
        eventToSave.setLocationGuide(txtaLocation.getText());
        eventToSave.setNotes(txtaDescription.getText());
        String filePath = "/dk/easv/ticketsys/Save/" + txtFileName.getText();
        String[] imgSrcSplit= txtFileName.getText().split("\\.");
        StringBuilder imgSrc = new StringBuilder();
        for (int i = 1; i < imgSrcSplit.length; i++)
            imgSrc.append("." + imgSrcSplit[i]);

        eventToSave.setImgSrc(filePath);
        ArrayList<TicketType> ticketTypes = new ArrayList<>();
        for (Node node : flowTicketTypes.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                if (cb.isSelected()) {
                    if (cb.isSelected()) {
                        String cbId = cb.getId();
                        String cbIdSplit = cbId.split("_")[1];
                        int id = Integer.parseInt(cbIdSplit);
                        String name = cb.getText();
                        System.out.println(cb.getText());
                        ticketTypes.add(new TicketType(id, name, false));
                    }
                }
            }
        }
        for (Node node : flowSpecialTickets.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                if (cb.isSelected()) {
                    String cbId = cb.getId();
                    String cbIdSplit = cbId.split("_")[1];
                    int id = Integer.parseInt(cbIdSplit);
                    String name = cb.getText();
                    ticketTypes.add(new TicketType(id, name, true));

                }
            }
        }
        if (loggedinUser != null) {
            eventToSave.setCreatedBy(loggedinUser.getId());
        }
        else
            System.out.println("No userID, who created the event.");
        if (!ticketTypes.isEmpty()) {
            eventToSave.setTicketTypes(ticketTypes);
        }
        return eventToSave;
    }

    private String formatTime(int value) {
        if (value < 10)
            return "0" + value;
        return "" + value;
    }

    @FXML private void btnNewTicketCancelClicked() {
        closeNewTicketType();
        isDeletingEventType = false;
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

    @FXML private void btnNewTicketSaveClicked() {
        String newTypeName = txtNewTicketType.getText();
        if (isDeletingEventType) {
            TicketType ticketTypeToDelete = choiceEvents.getSelectionModel().getSelectedItem();
            if (bllManager.deleteTicketType(ticketTypeToDelete))
            {
                System.out.println("Deleting ticket " + ticketTypeToDelete + " successfully.");
                dummyTicketTypes.remove(ticketTypeToDelete);
                removeCBTicketType(ticketTypeToDelete);
                closeNewTicketType();
            }
            return;
        }
        if (newTypeName == null || newTypeName.trim().isEmpty()) {
            System.out.println("No ticket type name entered!");
            return;
        }

        boolean isSpecial = chkSpecialInNewTicketType.isSelected();


        int generatedId = bllManager.uploadNewTicketType(new TicketType(0, newTypeName, isSpecial));
        TicketType newlyCreated = new TicketType(generatedId, newTypeName, isSpecial);

        CheckBox cb = new CheckBox(newTypeName);
        cb.setId("cb_" + generatedId);

        if (isSpecial) {
            flowSpecialTickets.getChildren().add(cb);
        } else {
            flowTicketTypes.getChildren().add(cb);
        }

        dummyTicketTypes.add(newlyCreated);

        System.out.println("Created new ticket type: " + newlyCreated.getName()
                + " (id=" + newlyCreated.getId() + ", special=" + isSpecial + ")");

        closeNewTicketType();
    }

    private void removeCBTicketType(TicketType ticketTypeToDelete) {
        if (ticketTypeToDelete.getSpecial()) {
            for (Node node : flowSpecialTickets.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    if (cb.getId().equals("cb_" + ticketTypeToDelete.getId())) {
                        flowSpecialTickets.getChildren().remove(cb);
                        return;
                    }
                }
            }
        }
        for (Node node : flowTicketTypes.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox cb = (CheckBox) node;
                if (cb.getId().equals("cb_" + ticketTypeToDelete.getId())) {
                    flowTicketTypes.getChildren().remove(cb);
                    return;
                }
            }
        }
    }



    public Button getSaveButton() {
        return btnSave;
    }


    @FXML private void btnImageClicked() throws TicketExceptions {
        BLLManager bllManager = new BLLManager();
        String filePath = bllManager.chooseFile(btnImage.getScene().getWindow());
        if (filePath != null) {
            txtFileName.setText(filePath);
        }
    }

    @FXML private void btnDeleteTicketType() {
        isDeletingEventType = true;
        showNewTicketPopup(false);
        lblNewTicketTitle.setText("Delete ticket type");
        hbNewEventTitle.setVisible(false);
        hbConnectEvent.setVisible(true);
        choiceEvents.getItems().clear();
        choiceEvents.getSelectionModel().clearSelection();
        choiceEvents.getItems().addAll(dummyTicketTypes);
        lblConnectToEvent.setText("Ticket types");
        chkSpecialInNewTicketType.setVisible(false);
        btnNewTicketSave.setText("Delete");
    }


    private void showNewTicketPopup(Boolean isCreating) {
        vboxShader.setVisible(true);
        vboxNewTicketType.setVisible(true);
        chkSpecialInNewTicketType.setSelected(false);
        if (isCreating) {
            lblNewTicketTitle.setText("Create new ticket type");
        }
        else
            lblNewTicketTitle.setText("Delete ticket type");

    }

    private void setSpinners() {

        //This converts the numbers on the dial to two digits
        StringConverter<Integer> convertToTwoDigits = new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return String.format("%02d", value); // Format as two digits
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        };


        // Configure Hour Spinner (0-23)
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 24, 12);
        hourFactory.setConverter(convertToTwoDigits);
        spStartHour.setValueFactory(hourFactory);
        spStartHour.getValueFactory().setValue(12);

        // Configure Minute Spinner (0-59)
        SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 60, 0);
        minuteFactory.setConverter(convertToTwoDigits);
        spStartMinute.setValueFactory(minuteFactory);

        // Make spinners editable
        spStartHour.setEditable(true);
        spStartMinute.setEditable(true);

        // Configure Hour Spinner (0-23)
        SpinnerValueFactory<Integer> hourFactoryEnd = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 24, 12);
        hourFactoryEnd.setConverter(convertToTwoDigits);
        spEndHour.setValueFactory(hourFactoryEnd);
        spEndHour.getValueFactory().setValue(12);

        // Configure Minute Spinner (0-59)
        SpinnerValueFactory<Integer> minuteFactoryEnd = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 60, 0);
        minuteFactoryEnd.setConverter(convertToTwoDigits);
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
            if (newDate != null) {
                setSpinnersEnabled();
            }
        });
    }

    private void setSpinnersEnabled() {
        spStartHour.setDisable(false);
        spStartMinute.setDisable(false);
        spEndHour.setDisable(false);
        spEndMinute.setDisable(false);
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
            //Check which spinner was changed and adjust the end/start timer as well
            // but only if the dates are on the same day
            if (dateStart.getValue().isEqual(dateEnd.getValue())) {
                int startHour = (int) spStartHour.getValueFactory().getValue();
                int startMinute = (int) spStartMinute.getValueFactory().getValue();
                int endHour = (int) spEndHour.getValueFactory().getValue();
                int endMinute = (int) spEndMinute.getValueFactory().getValue();

                //Checking if the start time was set behind the end time
                if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                    if (endHour == 0) {
                        LocalDate newEndDate = dateEnd.getValue().plusDays(1);
                        dateEnd.setValue(newEndDate);
                        spEndMinute.getValueFactory().setValue(0);
                    } else {
                        int newEndHour = startHour + 1;
                        int newEndMinute = startMinute;


                        if (newEndMinute >= 60) {
                            newEndMinute = 0;
                            newEndHour++;
                        }
                        if (newEndHour > 23) {
                            newEndHour = 0;

                        }

                        spEndHour.getValueFactory().setValue(newEndHour);
                        spEndMinute.getValueFactory().setValue(newEndMinute);
                    }
                }
            }
            int value = Integer.parseInt(spinner.getEditor().getText());
            // Make it move around
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

    public ArrayList<TicketType> getTicketTypes() {
        ArrayList<TicketType> ticketTypes = new ArrayList<>(bllManager.getTicketTypes());
        return ticketTypes;
    }


    private void setTicketTypes(){
        ArrayList<TicketType> ticketTypes = new ArrayList<>(bllManager.getTicketTypes());


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
        RowConstraints ticketRow = gridPaneForm.getRowConstraints().get(6);
        RowConstraints ticketRow2 = gridPaneForm.getRowConstraints().get(7);
        double flowTicketHeight = flowTicketTypes.getHeight();
        double flowSpecialTicketHeight = flowSpecialTickets.getHeight();
        ticketRow.setPrefHeight(flowTicketHeight+5);
        ticketRow2.setPrefHeight(flowSpecialTicketHeight);
    }


    public void setLoggedinUser(User loggedinUser) {
        this.loggedinUser = loggedinUser;
    }

    public Event getNewEvent() {
        return this.eventToSave;
    }
}

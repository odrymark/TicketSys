package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.util.AttendeeWriter;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class CoordinatorController {
    @FXML
    private FlowPane eventsPane;
    @FXML
    private Button newEvent;
    @FXML
    private HBox searchBox;
    @FXML
    private ChoiceBox<String> user;
    @FXML
    private HBox coord;
    @FXML
    private StackPane stackP;
    @FXML
    private TextField search;
    private ObservableList<HBox> events = FXCollections.observableArrayList();
    private FilteredList<HBox> filteredEvents = new FilteredList<>(events);


    private BLLManager bllManager;
    private User loggedinUser;
    private HashMap<Integer, HBox> eventCardHash;

    @FXML
    public void initialize() {
        coord.prefHeightProperty().bind(stackP.heightProperty());
        coord.prefWidthProperty().bind(stackP.widthProperty());
        try {
            bllManager = new BLLManager();
            //loadEvents();
        } catch (TicketExceptions e) {
            e.printStackTrace();
        }
        loggedinUser = null;
    }

    private void loadEvents() {
        events.clear();
        eventsPane.getChildren().clear();
        eventCardHash = new HashMap<>();
        List<Event> retrEvents = bllManager.getAllEvents(loggedinUser.getId());
        for (Event event : retrEvents) {
            try {
                InputStream imageStream = getImage(event);
                HBox eventCard = createEventCard(imageStream, event);
                events.add(eventCard);
                eventCardHash.put(event.getId(), eventCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        eventsPane.getChildren().addAll(filteredEvents);
    }

    @FXML
    private void applySearch()
    {
        eventsPane.getChildren().clear();
        if(search.getText().isEmpty())
        {
            filteredEvents.setPredicate(event -> true);
        }
        else
        {
            filteredEvents.setPredicate(event -> {
                String title = (String) event.getProperties().get("title");
                return title.toLowerCase().contains(search.getText().toLowerCase());
            });
        }
        eventsPane.getChildren().addAll(filteredEvents);
    }

    @FXML
    private void newEventTab() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/new_event.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            NewEventController controller = fxmlLoader.getController();
            controller.setLoggedinUser(loggedinUser);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            Event event = controller.getNewEvent();
            InputStream imageStream = getImage(event);
            System.out.println("NEw card: " + event.toString());
                HBox eventCard = createEventCard(imageStream, event);
                eventsPane.getChildren().add(eventCard);
                eventCardHash.put(event.getId(), eventCard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream getImage(Event event) {
        InputStream imageStream = null;
        System.out.println("GetImg: " + event.getImgSrc());
        if (event.getImgSrc() != null && !event.getImgSrc().isEmpty()) {
            imageStream = Main.class.getResourceAsStream(event.getImgSrc());
        }
        if (imageStream == null)
            imageStream = Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/noImg.jpg");
        return imageStream;
    }

    private void openTicket(Event event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/getTicket.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            GetTicketController getTicketController = fxmlLoader.getController();
            getTicketController.setEvent(event);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createEventCard(InputStream imagePath, Event event) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setId("card");

        Image image = new Image(imagePath);
        ImageView eventImage = bllManager.cropImage(image);


        VBox eventDetails = new VBox(5);
        eventDetails.setAlignment(Pos.CENTER_LEFT);

        HBox controls = new HBox(5);

        Label titleLabel = new Label(event.getTitle());
        titleLabel.setId("cardTitle");

        Label locationLabel = new Label(event.getLocation());
        locationLabel.setId("cardTextLocation");

        Label dateLabel = new Label("\uD83D\uDD52 " + event.getStartDate());
        dateLabel.setId("cardTextDate");

        Button editBtn = new Button("");
        editBtn.setFont(new Font("Arial", 16));
        editBtn.setMinWidth(45);
        editBtn.setId("cardButton");
        editBtn.getStyleClass().add("edit_button");
        editBtn.setOnAction(e -> {
            openEventEditPage(event, e);
        });

        Image ticketImg = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/images/ticket.png"));
        ImageView ticketIcon = new ImageView(ticketImg);
        ticketIcon.setPreserveRatio(true);
        ticketIcon.setFitWidth(27);
        Button ticketBtn = new Button("", ticketIcon);
        ticketBtn.setOnAction(_ -> openTicket(event));
        ticketBtn.setId("cardButton");

        Button deleteBtn = new Button("");
        deleteBtn.setMinWidth(40);
        deleteBtn.setId("cardButton");
        deleteBtn.getStyleClass().add("delete_button");
        deleteBtn.setFont(new Font("Arial", 16));
        deleteBtn.setOnAction(e -> {
            bllManager.deleteEvent(event);
            eventsPane.getChildren().remove(card);
            events.remove(card);
        });

        Button exportBtn = new Button("");
        exportBtn.setMinWidth(40);
        exportBtn.setId("cardButton");
        exportBtn.setFont(new Font("Arial", 14));
        exportBtn.getStyleClass().add("cardButton");
        exportBtn.getStyleClass().add("list_button");
        exportBtn.setOnAction(e -> {
            AttendeeWriter.exportAttendeesToFile(event, bllManager);
        });

        Button addCoordinatorBtn = new Button("");
        addCoordinatorBtn.setId("cardButton");
        addCoordinatorBtn.getStyleClass().add("cardButton");
        addCoordinatorBtn.getStyleClass().add("coordinator_button");
        addCoordinatorBtn.setOnAction(e -> {
            OpenCoordinatorWindow open = new OpenCoordinatorWindow(event, loggedinUser);
            //openCoordinatorWindow(event);
        });

        controls.getChildren().addAll(ticketBtn, editBtn, deleteBtn, exportBtn, addCoordinatorBtn);
        eventDetails.getChildren().addAll(titleLabel, locationLabel, dateLabel, controls);
        card.getChildren().addAll(eventImage, eventDetails);

        card.getProperties().put("title", event.getTitle());

        return card;
    }

    private void openEventEditPage(Event event, ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/new_event.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            NewEventController eventController = fxmlLoader.getController();
            eventController.setEventToEdit(event);
            eventController.setLoggedinUser(loggedinUser);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            Event newEvent = eventController.getNewEvent();
            InputStream imageStream = getImage(event);
            HBox eventCard = createEventCard(imageStream, event);
            editEventCard(event, newEvent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void editEventCard(Event event, Event newEvent) {
        System.out.println("Editting card: " + newEvent.getId() + " - " + newEvent.toString());
        HBox eventCard = eventCardHash.get(event.getId());
        if (eventCard == null) {
            InputStream imageStream = getImage(newEvent);
            int position = eventsPane.getChildren().indexOf(eventCard);
            eventsPane.getChildren().remove(position);
            //eventsPane.getChildren().add(position, createEventCard(imageStream, newEvent));
        }


    }


    public void setLoggedinUser(User loggedinUser) {
        if (loggedinUser != null) {
            this.loggedinUser = loggedinUser;
            setDropDown();
            loadEvents();
        } else
            System.out.println("No user is set who logged in");
    }

    private void setDropDown() {
        user.getItems().add(loggedinUser.getFullName());
        user.getItems().add("Edit profile");
        user.getItems().add("Logout");
        user.getSelectionModel().select(0);
        user.setOnAction(event -> {
            String selected = user.getSelectionModel().getSelectedItem();

            if ("Edit profile".equals(selected)) {
                newUserTab();
            } else if ("Logout".equals(selected)) {
                logout();
            }
        });
    }

    private void newUserTab() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("FXML/users.fxml"));
        Parent root;
        try {
            root = loader.load();
            UsersController usersController = loader.getController();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("../Images/user.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            usersController.isNewUser(false);
            usersController.setUserToEdit(loggedinUser);
            usersController.setAdminEditingSelf(false);
            stage.setTitle("Edit profile");
            usersController.setLoggedinUser(loggedinUser);
            usersController.setRole(loggedinUser.getRole());
            user.getSelectionModel().select(0);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            User newUser = usersController.getUserToReturn();
            System.out.println("Updated the details");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void logout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/login.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) user.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }




}

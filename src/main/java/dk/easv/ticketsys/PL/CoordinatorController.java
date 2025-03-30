package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CoordinatorController
{
    @FXML private FlowPane eventsPane;
    @FXML private Button newEvent;
    @FXML private HBox searchBox;
    @FXML private ChoiceBox<String> user;


    private BLLManager bllManager;
    private User loggedinUser;

    @FXML
    public void initialize() {
        try {
            bllManager = new BLLManager();
            loadEvents();
        } catch (TicketExceptions e) {
            e.printStackTrace();
        }
        loggedinUser = null;
    }

    private void loadEvents() {
        eventsPane.getChildren().clear();

        List<Event> events = bllManager.getAllEvents();
        for (Event event : events) {
            try {
                InputStream imageStream = Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/events.png");
                HBox eventCard = createEventCard(imageStream, event);
                eventsPane.getChildren().add(eventCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void newEventTab()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/new_event.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void openTicket(Event event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dk/easv/ticketsys/FXML/getTicket.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            GetTicketController getTicketController = fxmlLoader.getController();
            getTicketController.setEvent(event);
            stage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private HBox createEventCard(InputStream imagePath, Event event) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setId("card");

        ImageView eventImage = new ImageView(new Image(imagePath));
        eventImage.setFitWidth(120);
        eventImage.setFitHeight(120);
        eventImage.setPreserveRatio(true);

        VBox eventDetails = new VBox(5);
        eventDetails.setAlignment(Pos.CENTER_LEFT);

        HBox controls = new HBox(5);

        Label titleLabel = new Label(event.getTitle());
        titleLabel.setId("cardTitle");

        Label locationLabel = new Label(event.getLocation());
        locationLabel.setId("cardText");

        Label dateLabel = new Label("\uD83D\uDD52 " + event.getStartDate());
        dateLabel.setId("cardText");

        Button editBtn = new Button("Edit");
        editBtn.setMinWidth(45);
        editBtn.setId("cardButton");
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

        Button deleteBtn = new Button("\uD83D\uDDD1");
        deleteBtn.setMinWidth(40);
        deleteBtn.setId("cardButton");
        deleteBtn.setFont(new Font("Arial", 16));
        deleteBtn.setOnAction(e -> {
            bllManager.deleteEvent(event);
            eventsPane.getChildren().remove(card);
        });

        controls.getChildren().addAll(ticketBtn, editBtn, deleteBtn);
        eventDetails.getChildren().addAll(titleLabel, locationLabel, dateLabel, controls);
        card.getChildren().addAll(eventImage, eventDetails);

        return card;
    }

    private void openEventEditPage(Event event, ActionEvent actionEvent) {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/new_event.fxml"));
            Parent root = fxmlLoader.load();
            NewEventController eventController = fxmlLoader.getController();
            eventController.setEventToEdit(event);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void setLoggedinUser(User loggedinUser) {
        if (loggedinUser != null) {
            this.loggedinUser = loggedinUser;
            setDropDown();
        }
        else
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

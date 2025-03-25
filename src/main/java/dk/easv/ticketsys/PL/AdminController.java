package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController
{
    private Boolean isEventsWin = true;
    @FXML
    private Button sideBtnSelected;
    @FXML
    private Button sideBtnNotSelected;
    @FXML
    private ImageView usersImage;
    @FXML
    private ImageView eventsImage;
    @FXML
    private FlowPane eventsPane;
    @FXML
    private Label currentP;
    @FXML
    private Button newUser;
    private Image usersSel = new Image(getClass().getResourceAsStream("/dk/easv/ticketsys/Images/user.png"));
    private Image eventsSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/events.png"));
    private Image usersNotSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/userNotSel.png"));
    private Image eventsNotSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/eventsNotSel.png"));
    private ObservableList<HBox> events = FXCollections.observableArrayList();
    private ObservableList<VBox> users = FXCollections.observableArrayList();
    private final Button temp = new Button();
    @FXML
    private ScrollPane scrollP;
    private User userToEdit;
    private Map<User, VBox> userCardMap = new HashMap<>();

    private BLLManager bllManager;

    @FXML
    public void initialize() throws TicketExceptions {
        bllManager = new BLLManager();
        loadEvents();
        eventsPane.prefWidthProperty().bind(scrollP.widthProperty());
        eventsPane.prefHeightProperty().bind(scrollP.heightProperty());
        userToEdit = null;
    }

    @FXML
    private void eventsTab()
    {
        if(!isEventsWin)
        {
            //TEMPORARY TEST FOR EVENT CARDS
            //events.add(createEventCard(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/eventsNotSel.png"), "Title", "Location", "2025-02-12 19:30"));

            sideBtnNotSelected.setId("sideBtnNotSelected");
            sideBtnSelected.setId("sideBtnSelected");
            usersImage.setImage(usersNotSel);
            eventsImage.setImage(eventsSel);
            isEventsWin = true;
            currentP.setText("Events");
            loadEvents();
            newUser.setVisible(false);
            newUser.setDisable(true);
        }
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

    private void loadUsers() {
        eventsPane.getChildren().clear();

        List<User> users = bllManager.getAllUsers();
        for (User user : users) {
            try {
                VBox userCard = createUserCard(user);
                eventsPane.getChildren().add(userCard);
                userCardMap.put(user, userCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void usersTab()
    {
        //TEMPORARY TEST FOR USER CARDS
        //users.add(createUserCard("User", "user@email.com", "User"));

        if(isEventsWin)
        {
            sideBtnNotSelected.setId("sideBtnSelected");
            sideBtnSelected.setId("sideBtnNotSelected");
            usersImage.setImage(usersSel);
            eventsImage.setImage(eventsNotSel);
            isEventsWin = false;
            currentP.setText("Users");
            loadUsers();
            newUser.setVisible(true);
            newUser.setDisable(false);
        }
    }

    @FXML
    private void newUserTab(){
        boolean isNew = false;
        //TODO: save user only if save button was clicked. If the window just closes, don't do anything
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("FXML/users.fxml"));
        Parent root;
        try {
            root = loader.load();
            UsersController usersController = loader.getController();
            usersController.setRole("Admin");
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("../Images/user.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (userToEdit != null) {
                usersController.isNewUser(false);
                usersController.setUserToEdit(userToEdit);
                stage.setTitle("Edit User");
            }
            else {
                isNew = true;
                usersController.isNewUser(true);
                usersController.clearFields();
                stage.setTitle("Add User");
            }

            stage.setScene(new Scene(root));
            stage.showAndWait();
            User newUser = usersController.getUserToReturn();
            if (usersController.getIsSaveUser()) {
                SnackbarController controller = new SnackbarController();
                if (isNew) {
                    VBox userCard = createUserCard(newUser);
                    eventsPane.getChildren().add(userCard);
                    userCardMap.put(newUser, userCard);
                } else {
                    VBox userCard = userCardMap.get(userToEdit);
                    if (userCard != null) {
                        updateUserCard(userCard, newUser);
                    } else
                        System.out.println("UserCard was not found");
                }
            }

            userToEdit = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUserCard(VBox userCard, User newUser) {
        userCard.getChildren().clear();
        userCard.setOnMouseClicked(_ -> { userToEdit = newUser; newUserTab();});
        userCard.setId("usersCard");

        Label nameLabel = new Label(newUser.getFullName());
        nameLabel.setId("cardTitle");

        Label emailLabel = new Label("Email: " + newUser.getEmail());

        Label typeLabel = new Label("Type: " + newUser.getRole());

        userCard.getChildren().addAll(nameLabel, emailLabel, typeLabel);
    }

    private HBox createEventCard(InputStream imagePath, Event event) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setId("eventsCard");

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

        Button infoBtn = new Button("Info");
        infoBtn.setMinWidth(45);
        infoBtn.setId("cardButton");

        Button deleteBtn = new Button("\uD83D\uDDD1");
        deleteBtn.setMinWidth(40);
        deleteBtn.setId("cardButton");
        deleteBtn.setFont(new Font("Arial", 16));
        deleteBtn.setOnAction(e -> {
            bllManager.deleteEvent(event);
            eventsPane.getChildren().remove(card);
        });

        controls.getChildren().addAll(infoBtn, deleteBtn);
        eventDetails.getChildren().addAll(titleLabel, locationLabel, dateLabel, controls);
        card.getChildren().addAll(eventImage, eventDetails);

        return card;
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(5);
        card.setOnMouseClicked(_ -> { userToEdit = user; newUserTab();});
        card.setId("usersCard");

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setId("cardTitle");

        Label emailLabel = new Label("Email: " + user.getEmail());

        Label typeLabel = new Label("Type: " + user.getRole());

        card.getChildren().addAll(nameLabel, emailLabel, typeLabel);
        return card;
    }
}

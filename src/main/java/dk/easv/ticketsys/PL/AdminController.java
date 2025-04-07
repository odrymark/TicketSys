package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML private TextField search;
    @FXML private Button sideBtnSelected;
    @FXML private Button sideBtnNotSelected;
    @FXML private ImageView usersImage;
    @FXML private ImageView eventsImage;
    @FXML private FlowPane eventsPane;
    @FXML private Label currentP;
    @FXML private Button newUser;
    @FXML private ChoiceBox<String> user;
    private Image usersSel = new Image(getClass().getResourceAsStream("/dk/easv/ticketsys/Images/user.png"));
    private Image eventsSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/events.png"));
    private Image usersNotSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/userNotSel.png"));
    private Image eventsNotSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/eventsNotSel.png"));
    private ObservableList<HBox> events = FXCollections.observableArrayList();
    private ObservableList<HBox> users = FXCollections.observableArrayList();
    private FilteredList<HBox> filteredEvents = new FilteredList<>(events);
    private FilteredList<HBox> filteredUsers = new FilteredList<>(users);
    @FXML
    private ScrollPane scrollP;
    private User userToEdit;
    private Map<String, HBox> userCardMap;



    private User loggedInUser;

    private BLLManager bllManager;

    @FXML
    public void initialize() throws TicketExceptions {
        bllManager = new BLLManager();
        userCardMap = new HashMap<>();
        initEvents();
        initUsers();
        loadEvents();
        eventsPane.prefWidthProperty().bind(scrollP.widthProperty());
        eventsPane.prefHeightProperty().bind(scrollP.heightProperty());
        userToEdit = null;
        loggedInUser = null;
        user.getItems().clear();
    }

    private void initEvents() {
        eventsPane.getChildren().clear();

        List<Event> retrEvents = bllManager.getAllEvents();
        for (Event event : retrEvents) {
            try {
                GetImage getImage = new GetImage();
                InputStream imageStream = getImage.getImage(event);
                HBox eventCard = createEventCard(imageStream, event);
                events.add(eventCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initUsers() {
        eventsPane.getChildren().clear();

        List<User> retrUsers = bllManager.getAllUsers();
        for (User user : retrUsers) {
            try {
                HBox userCard = createUserCard(user);
                eventsPane.getChildren().add(userCard);
                users.add(userCard);
                userCardMap.put(user.getUsername(), userCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadEvents()
    {
        eventsPane.getChildren().clear();
        eventsPane.getChildren().addAll(filteredEvents);
        newUser.setDisable(true);
        newUser.setVisible(false);
        search.setText("");
        applySearch();
    }

    private void loadUsers()
    {
        eventsPane.getChildren().clear();
        eventsPane.getChildren().addAll(filteredUsers);
        newUser.setDisable(false);
        newUser.setVisible(true);
        search.setText("");
        applySearch();
    }

    @FXML
    private void eventsTab()
    {
        if(!isEventsWin)
        {
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

    @FXML
    private void usersTab()
    {
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
    private void applySearch()
    {
        eventsPane.getChildren().clear();
        if(search.getText().isEmpty())
        {
            filteredEvents.setPredicate(event -> true);
            filteredUsers.setPredicate(user -> true);
        }
        else
        {
            filteredEvents.setPredicate(event -> {
                String title = (String) event.getProperties().get("title");
                return title.toLowerCase().contains(search.getText().toLowerCase());
            });
            filteredUsers.setPredicate(user -> {
                String title = (String) user.getProperties().get("username");
                return title.toLowerCase().contains(search.getText().toLowerCase());
            });
        }

        if(newUser.isDisable())
        {
            eventsPane.getChildren().addAll(filteredEvents);
        }
        else
        {
            eventsPane.getChildren().addAll(filteredUsers);
        }
    }

    @FXML
    private void newUserTab(){
        boolean isNew = false;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("FXML/users.fxml"));
        Parent root;
        try {
            root = loader.load();
            UsersController usersController = loader.getController();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("../Images/user.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (userToEdit != null) {
                usersController.isNewUser(false);
                usersController.setUserToEdit(userToEdit);
                if (loggedInUser != null && loggedInUser.getUsername().equals(userToEdit.getUsername())) {
                    usersController.setAdminEditingSelf(true);
                    stage.setTitle("Edit profile");
                }
                else
                    stage.setTitle("Edit User");
                usersController.setLoggedinUser(loggedInUser);
                usersController.setRole(loggedInUser.getRole());
                user.getSelectionModel().select(0);
            }
            else {
                isNew = true;
                usersController.isNewUser(true);
                //usersController.setRole(loggedInUser.getRole());
                usersController.clearFields();
                stage.setTitle("Add User");
            }

            stage.setScene(new Scene(root));
            stage.showAndWait();
            User newUser = usersController.getUserToReturn();
            if (usersController.getIsSaveUser()) {
                SnackbarController controller = new SnackbarController();
                if (isNew) {
                    HBox userCard = createUserCard(newUser);
                    eventsPane.getChildren().add(userCard);
                    users.add(userCard);
                    userCardMap.put(newUser.getUsername(), userCard);
                } else {
                    HBox userCard = userCardMap.get(userToEdit.getUsername());
                    if (userCard != null) {
                        updateUserCard(userCard, newUser);
                        if (userToEdit.getUsername().equals(loggedInUser.getUsername())) {
                            loggedInUser.setFullName(newUser.getFullName());
                            user.getItems().set(0, newUser.getFullName());
                            user.getSelectionModel().select(0);
                        }
                    } else {
                        System.out.println("UserCard was not found");
                    }
                }
            }

            userToEdit = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUserCard(HBox userCard, User newUser) {
        //userCard.getChildren().clear();
        userCard.setId("usersCard");
        for (Node node : ((VBox) userCard.getChildren().getFirst()).getChildren()) { // Get the details VBox
            if (node instanceof Label label) {
                String text = label.getText();
                if (text.startsWith("Email:")) {
                    label.setText("Email: " + newUser.getEmail());
                } else if (text.startsWith("Type:")) {
                    label.setText("Type: " + newUser.getRole());
                } else { // Assume this is the name label
                    label.setText(newUser.getFullName());
                }
            }
        }
    }

    private HBox createEventCard(InputStream imagePath, Event event) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setId("eventsCard");

        ImageView eventImage = bllManager.cropImage(new Image(imagePath));
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

        /**Image ticketImg = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/images/ticket.png"));
        ImageView ticketIcon = new ImageView(ticketImg);
        ticketIcon.setPreserveRatio(true);
        ticketIcon.setFitWidth(27);
        Button ticketBtn = new Button("", ticketIcon);
        ticketBtn.setOnAction(_ -> openTicket(event));
        ticketBtn.setId("cardButton");*/

        Button infoBtn = new Button("\uD83D\uDEC8");
        infoBtn.setFont(new Font("Arial", 16));
        infoBtn.setMinWidth(45);
        infoBtn.setId("cardButton");

        Button deleteBtn = new Button("\uD83D\uDDD1");
        deleteBtn.setMinWidth(40);
        deleteBtn.setId("cardButton");
        deleteBtn.setFont(new Font("Arial", 16));
        deleteBtn.setOnAction(e -> {
            bllManager.deleteEvent(event);
            eventsPane.getChildren().remove(card);
            events.remove(card);
        });

        controls.getChildren().addAll(/**ticketBtn, */infoBtn, deleteBtn);
        eventDetails.getChildren().addAll(titleLabel, locationLabel, dateLabel, controls);
        card.getChildren().addAll(eventImage, eventDetails);

        card.getProperties().put("title", event.getTitle());

        return card;
    }

    private void openTicket(Event event) {

    }

    private HBox createUserCard(User user) {
        HBox card = new HBox(5);
        card.setId("usersCard");

        VBox details = new VBox(5);
        details.setId("cardDetails");

        HBox controls = new HBox(5);
        controls.setAlignment(Pos.BOTTOM_RIGHT);
        controls.setPadding(new Insets(0, 0, 0, 5));

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setId("cardTitle");

        Label emailLabel = new Label("Email: " + user.getEmail());

        Label typeLabel = new Label("Type: " + user.getRole());

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(_ -> editUser(user));
        editBtn.setId("btn");
        editBtn.setMinWidth(40);
        editBtn.setMinHeight(30);

        Button deleteBtn = new Button("\uD83D\uDDD1");
        deleteBtn.setId("usersCardButton");
        deleteBtn.setOnAction(e -> {
            bllManager.deleteUser(user);
            eventsPane.getChildren().remove(card);
            users.remove(card);
        });

        controls.getChildren().addAll(editBtn, deleteBtn);
        details.getChildren().addAll(nameLabel, emailLabel, typeLabel);
        card.getChildren().addAll(details, controls);

        card.getProperties().put("username", user.getFullName());

        return card;
    }

    private void editUser(User user) {
        userToEdit = user;
        newUserTab();
    }

    public void setLoggedInUser(User loggedInUser) {
        if (loggedInUser != null) {
            this.loggedInUser = loggedInUser;
            setDropDown();
        }
        else
            System.out.println("No user is set who logged in");
    }

    private void setDropDown() {
        user.getItems().add(loggedInUser.getFullName());
        user.getItems().add("Edit profile");
        user.getItems().add("Logout");
        user.getSelectionModel().select(0);
        user.setOnAction(event -> {
            String selected = user.getSelectionModel().getSelectedItem();

            if ("Edit profile".equals(selected)) {
                userToEdit = loggedInUser;
                newUserTab();
            } else if ("Logout".equals(selected)) {
                logout();
            }
        });
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

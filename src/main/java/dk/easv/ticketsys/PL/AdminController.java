package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.InputStream;

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

    @FXML
    public void initialize()
    {
        eventsPane.prefWidthProperty().bind(scrollP.widthProperty());
        eventsPane.prefHeightProperty().bind(scrollP.heightProperty());
    }

    @FXML
    private void eventsTab()
    {
        if(!isEventsWin)
        {
            //TEMPORARY TEST FOR EVENT CARDS
            events.add(createEventCard(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/eventsNotSel.png"), "Title", "Location", "2025-02-12 19:30"));

            sideBtnNotSelected.setId("sideBtnNotSelected");
            sideBtnSelected.setId("sideBtnSelected");
            usersImage.setImage(usersNotSel);
            eventsImage.setImage(eventsSel);
            isEventsWin = true;
            currentP.setText("Events");
            eventsPane.getChildren().clear();
            eventsPane.getChildren().setAll(events);
            newUser.setVisible(false);
            newUser.setDisable(true);
        }
    }

    @FXML
    private void usersTab()
    {
        //TEMPORARY TEST FOR USER CARDS
        users.add(createUserCard("User", "user@email.com", "User"));

        if(isEventsWin)
        {
            sideBtnNotSelected.setId("sideBtnSelected");
            sideBtnSelected.setId("sideBtnNotSelected");
            usersImage.setImage(usersSel);
            eventsImage.setImage(eventsNotSel);
            isEventsWin = false;
            currentP.setText("Users");
            eventsPane.getChildren().clear();
            eventsPane.getChildren().setAll(users);
            newUser.setVisible(true);
            newUser.setDisable(false);
        }
    }

    @FXML
    private void newUserTab()
    {

    }

    private HBox createEventCard(InputStream imagePath, String title, String location, String dateTime) {
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

        Label titleLabel = new Label(title);
        titleLabel.setId("cardTitle");

        Label locationLabel = new Label(location);
        locationLabel.setId("cardText");

        Label dateLabel = new Label("\uD83D\uDD52 " + dateTime);
        dateLabel.setId("cardText");

        Button editBtn = new Button("Edit");
        editBtn.setMinWidth(45);
        editBtn.setId("cardButton");

        Button infoBtn = new Button("Info");
        infoBtn.setMinWidth(45);
        infoBtn.setId("cardButton");

        Button deleteBtn = new Button("\uD83D\uDDD1");
        deleteBtn.setMinWidth(40);
        deleteBtn.setId("cardButton");
        deleteBtn.setFont(new Font("Arial", 16));
        deleteBtn.setOnAction(e -> card.setVisible(false));

        controls.getChildren().addAll(infoBtn, editBtn, deleteBtn);
        eventDetails.getChildren().addAll(titleLabel, locationLabel, dateLabel, controls);
        card.getChildren().addAll(eventImage, eventDetails);

        return card;
    }

    private VBox createUserCard(String name, String email, String type) {
        VBox card = new VBox(5);
        card.setOnMouseClicked(_ -> { });
        card.setId("usersCard");

        Label nameLabel = new Label(name);
        nameLabel.setId("cardTitle");

        Label emailLabel = new Label("Email: " + email);

        Label typeLabel = new Label("Type: " + type);

        card.getChildren().addAll(nameLabel, emailLabel, typeLabel);
        return card;
    }
}

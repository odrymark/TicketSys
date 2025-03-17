package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private Image usersSel = new Image(getClass().getResourceAsStream("/dk/easv/ticketsys/Images/user.png"));
    private Image eventsSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/events.png"));
    private Image usersNotSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/userNotSel.png"));
    private Image eventsNotSel = new Image(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/eventsNotSel.png"));
    private final Button temp = new Button();

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
        }
    }

    @FXML
    private void usersTab()
    {
        //TEMPORARY TEST FOR CARDS
        eventsPane.getChildren().add(createEventCard(Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/eventsNotSel.png"), "Title", "Location", "2025-02-12 19:30"));


        if(isEventsWin)
        {
            sideBtnNotSelected.setId("sideBtnSelected");
            sideBtnSelected.setId("sideBtnNotSelected");
            usersImage.setImage(usersSel);
            eventsImage.setImage(eventsNotSel);
            isEventsWin = false;
        }
    }

    private static HBox createEventCard(InputStream imagePath, String title, String location, String dateTime) {
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
}

package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class CoordinatorController
{
    @FXML
    private FlowPane eventsPane;
    @FXML
    private Button newEvent;
    @FXML
    private HBox searchBox;

    @FXML
    private void newEventTab()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/manageEvent.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) newEvent.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
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

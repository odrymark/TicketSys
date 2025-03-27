package dk.easv.ticketsys.PL;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


public class SnackbarController {
    @FXML private HBox hboxSnack;
    @FXML private ImageView imgView;
    @FXML private Label txtSnack;
    @FXML private Label txtTitle;

    private final int SUCCESS = 1;
    private final int INFO = 2;
    private final int WARNING = 3;
    private final int DANGER = 4;
    private final int ALERT = 5;

    public void setSnackBar(String title, String text, Image img, int timing, HBox parentBox, double width, int type, SnackbarController controller) {
        //Sets the parameters for the message
        parentBox.setVisible(true);
        parentBox.setManaged(true);
        parentBox.setMinWidth(width-20);
        parentBox.setMaxWidth(width-20);
        hboxSnack.setMinWidth(width-20);
        hboxSnack.setMaxWidth(width-20);
        hboxSnack.setVisible(true);
        txtTitle.setText(title);
        txtTitle.setWrapText(true);
        txtTitle.getStyleClass().add("alert-title");
        txtTitle.setMaxWidth(width-img.getWidth()-10);
        txtTitle.setMinWidth(width-img.getWidth()-10);
        txtSnack.setMaxWidth(width-img.getWidth()-10);
        txtSnack.setMinWidth(width-img.getWidth()-10);
        txtSnack.setText(text);
        txtSnack.setWrapText(true);
        txtSnack.getStyleClass().add("alert-body");
        imgView.setImage(img);
        imgView.setFitWidth(img.getWidth());
        imgView.setFitHeight(img.getHeight());
        setStyleClass(type, parentBox);
        //fadeIn(parentBox);
        slideIn(parentBox);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(timing), event -> {
            //fadeOut(parentBox);
            slideOut(parentBox, controller);
        }));

        timeline.setCycleCount(1);
        timeline.play();
    }



    private void fadeIn (HBox parentBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), parentBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void fadeOut (HBox parentBox) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), parentBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            parentBox.setTranslateX(0);
            parentBox.setTranslateY(0);
            parentBox.setVisible(false);

        });
        //fadeOut.setOnFinished(e -> parentBox.setVisible(false)); // Hide after fading
        fadeOut.play();
    }

    private void slideIn (HBox parentBox) {
        parentBox.setVisible(true);
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), parentBox);
        slideIn.setFromY(50); // Start 50px lower
        slideIn.setToY(0);    // Move to normal position
        slideIn.play();
    }

    private void slideOut (HBox parentBox,  SnackbarController controller) {
        TranslateTransition slideOut = new TranslateTransition(Duration.seconds(0.5), parentBox);
        slideOut.setFromY(0);
        slideOut.setToY(60);
        slideOut.setOnFinished(e -> {
            parentBox.setVisible(false);
        });
        slideOut.play();
    }

    private void setStyleClass(int type, HBox parentBox) {
        switch (type) {
            case SUCCESS:
                hboxSnack.getStyleClass().add("alert-success");
                parentBox.getStyleClass().add("alert-success");

                break;
            case INFO:
                hboxSnack.getStyleClass().add("alert-info");
                parentBox.getStyleClass().add("alert-info");
                break;
            case WARNING:
                hboxSnack.getStyleClass().add("alert-warning");
                parentBox.getStyleClass().add("alert-warning");
                break;
            case DANGER:
                hboxSnack.getStyleClass().add("alert-danger");
                parentBox.getStyleClass().add("alert-danger");
                break;
            default:
                hboxSnack.getStyleClass().setAll("alert");
        }
    }

    public int getSUCCESS() {
        return SUCCESS;
    }

    public int getINFO() {
        return INFO;
    }

    public int getWARNING() {
        return WARNING;
    }

    public int getDANGER() {
        return DANGER;
    }

    public int getALERT() {
        return ALERT;
    }
}

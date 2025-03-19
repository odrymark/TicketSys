package dk.easv.ticketsys.dal;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ChooseFile {
    File chosenFile;
    public ChooseFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        //Sets the window title
        fileChooser.setTitle("Open Image File");

        //Sets what kind of files the user can open
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("PNG Files", "*.png");
        //Sets the filter for the browser
        fileChooser.getExtensionFilters().addAll(extFilter, extFilter2);
        //Open dialogs, when finishes the File will be stored in chosenFile
        chosenFile = fileChooser.showOpenDialog(window);

    }

    public String getSelectedFilePath() {
        if (chosenFile != null) {
            return chosenFile.getAbsolutePath();
        }
        else
            throw new RuntimeException("File is null, can not return filePath");
    }
}

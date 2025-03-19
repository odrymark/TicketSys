package dk.easv.ticketsys.dal;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ChooseFile {
    File chosenFile;

    public ChooseFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        //Sets the window title
        fileChooser.setTitle("Open Image File");

        //Sets what kind of files the user can open
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        //Sets the filter for the browser
        fileChooser.getExtensionFilters().addAll(extFilter);
        //Open dialogs, when finishes the File will be stored in chosenFile
        chosenFile = fileChooser.showOpenDialog(window);

        if (chosenFile != null) {
            File targetDir = new File("./Save");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File targetFile = new File(targetDir, chosenFile.getName());

            try {
                Files.copy(chosenFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image saved to " + targetFile.getAbsolutePath());
                chosenFile = targetFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String getSelectedFilePath() {
        if (chosenFile != null) {
            return chosenFile.getAbsolutePath();
        } else
            throw new RuntimeException("File is null, can not return filePath");
    }
}

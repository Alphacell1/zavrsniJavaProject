package screens;

import changes.ChangeController;
import exceptions.EmptyFileNameException;
import file_reader.FileContents;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sql.DatabaseUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddRevisionController {
    private static final Logger logger = Logger.getLogger(AddRevisionController.class.getName());

    @FXML
    TextField fileNameTextField;
    @FXML
    TextArea fileContentTextArea;

    public void uploadRevision() {
        try {
            String revisionContent = fileContentTextArea.getText();
            String fileName = fileNameTextField.getText();
            if (fileName.isEmpty()) {
                throw new EmptyFileNameException("File name is not inserted!");
            }
            if (revisionContent.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please insert every field!");
                alert.showAndWait();
                return;
            }
            FileContents fileContents = new FileContents(LoginController.user, LocalDateTime.now(), revisionContent, fileName);
            try {
                FileContents.createFileFromContents(fileContents);
                if (DatabaseUtils.storeRevision(fileContents)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successfully uploaded");
                    alert.setContentText("Data successfully uploaded!");
                    alert.show();
                    ChangeController.saveChange("New revision uploaded");
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Creating file not successful");
            }

        } catch (EmptyFileNameException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please insert every field!");
            alert.showAndWait();
        }
    }
}

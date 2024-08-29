package screens;

import changes.ChangeController;
import difference.FileDifference;
import difference.FilePair;
import file_reader.FileContents;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import sql.DatabaseUtils;
import user.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RevisionPopupController {

    Logger logger = Logger.getLogger("RevisionPopupController");
    @FXML
    TextArea revisionTextArea;

    FileContents localFileContents;

    FileContents revisionFileContents;

    public FileContents getFileContents() {
        return localFileContents;
    }

    public void setFileContents(FileContents revisionedFileContents) {
        revisionFileContents = revisionedFileContents;
        try {
            File file = new File("/files/" + revisionedFileContents.getFileName());
            localFileContents = FileContents.updateFileContents(file);
            localFileContents.setUser(revisionedFileContents.getUser());
            FileDifference fileDifference = new FileDifference();
            FilePair<FileContents, FileContents> filePair = new FilePair<>(revisionedFileContents, localFileContents);

            revisionTextArea.setText(fileDifference.getFileDifferences(filePair));
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }


    public void onPush() {
        boolean isSuccessful = DatabaseUtils.storeRevision(getFileContents());
        if (isSuccessful) {
            ChangeController.saveChange("Pushana nova revizija za " + revisionFileContents.getFileName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success!");
            alert.setContentText("Revision pushed succesfully");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Error occured while pushing revision");
            alert.showAndWait();
        }
        closeWindow();
    }


    private void closeWindow() {
        Stage stage = (Stage) revisionTextArea.getScene().getWindow();
        stage.close();
    }

    public void onCancel() {
        closeWindow();
    }

    public void onDelete() {
        boolean isSuccessful = DatabaseUtils.deleteRevision(revisionFileContents);
        Alert alert;
        if (isSuccessful) {
            ChangeController.saveChange("Izbrisana revizija za " + revisionFileContents.getFileName());
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success!");
            alert.setContentText("Revision deleted succesfully");
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Error occured while deleting revision");
        }
        alert.showAndWait();
        closeWindow();
    }
}

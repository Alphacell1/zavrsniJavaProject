package screens;

import file_reader.FileContents;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sql.DatabaseUtils;
import user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RevisionOverviewController {

    @FXML
    TableColumn<FileContents, String> dateChangedColumn;
    @FXML
    TableColumn<FileContents, String> fileNameColumn;
    @FXML
    TableColumn<FileContents, String> userColumn;
    @FXML
    DatePicker fromDateField;
    @FXML
    DatePicker toDateField;
    @FXML
    TableView<FileContents> tablicaRevizija;
    @FXML
    TextField flitriranjeContentTextField;
    @FXML
    TextField filtriranjeImeTextField;
    @FXML
    ComboBox<User> korisnikComboBox;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<FileContents> allRevisions = new ArrayList<>(); // Store the fetched revisions

    @FXML
    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        dateChangedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateUpdated().format(formatter)));
        fileNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getRole() + " " + cellData.getValue().getUser().getUsername()));

        allRevisions = DatabaseUtils.getAllRevisions();

        if (allRevisions != null) {
            allRevisions.sort(Comparator.comparing(FileContents::getDateUpdated).reversed());
            ObservableList<User> users = FXCollections.observableArrayList(Objects.requireNonNull(DatabaseUtils.getAllUsers()));
            korisnikComboBox.setItems(users);

            ObservableList<FileContents> observableTablicaRevizija = FXCollections.observableArrayList(allRevisions);
            tablicaRevizija.setItems(observableTablicaRevizija);
        }
    }

    public void onFilterClick() {
        executorService.submit(() -> {
            LocalDateTime fromDate = fromDateField != null && fromDateField.getValue() != null ? fromDateField.getValue().atStartOfDay() : null;
            LocalDateTime toDate = toDateField != null && toDateField.getValue() != null ? toDateField.getValue().atTime(23, 59, 59) : null;
            String contentTextFieldText = flitriranjeContentTextField != null ? flitriranjeContentTextField.getText() : null;
            String imeTextFieldText = filtriranjeImeTextField != null ? filtriranjeImeTextField.getText() : null;
            User user = korisnikComboBox != null ? korisnikComboBox.getValue() : null;

            List<FileContents> filteredList = new ArrayList<>(allRevisions);

            if (fromDate != null) {
                filteredList.removeIf(x -> x.getDateUpdated().isBefore(fromDate));
            }

            if (toDate != null) {
                filteredList.removeIf(x -> x.getDateUpdated().isAfter(toDate));
            }

            if (contentTextFieldText != null && !contentTextFieldText.isEmpty()) {
                filteredList.removeIf(x -> !x.getFileContent().contains(contentTextFieldText));
            }

            if (imeTextFieldText != null && !imeTextFieldText.isEmpty()) {
                filteredList.removeIf(x -> !x.getFileName().contains(imeTextFieldText));
            }

            if (user != null) {
                filteredList.removeIf(x -> !x.getUser().equals(user));
            }

            filteredList.sort(Comparator.comparing(FileContents::getDateUpdated).reversed());

            ObservableList<FileContents> observableFilteredTablicaRevizija = FXCollections.observableArrayList(filteredList);
            tablicaRevizija.setItems(observableFilteredTablicaRevizija);
        });
    }

    public void onEdit() {
        FileContents selectedRevision = tablicaRevizija.getSelectionModel().getSelectedItem();
        if (selectedRevision != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RevisionPopup.fxml"));
                Parent root = loader.load();
                RevisionPopupController controller = loader.getController();
                controller.setFileContents(selectedRevision);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Show revision");
                stage.setScene(new Scene(root));
                stage.showAndWait();
                initialize();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Loading error");
                alert.setContentText("An error occurred while loading the edit popup.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No selection");
            alert.setContentText("Please select a revision to edit.");
            alert.showAndWait();
        }
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}

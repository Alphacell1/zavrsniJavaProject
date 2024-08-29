package screens;

import changes.Change;
import changes.ChangeController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import login_manager.LoginManager;
import sql.DatabaseUtils;
import user.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminConsoleController {
    public TableView<Change> tablicaRevizija;
    public TableColumn<Change, String> changeColumn;
    public TableColumn<Change, String> userColumn;
    public ComboBox<User> userComboBox;

    private Map<User, List<Change>> userChangesMap;


    public void initialize() {
        userChangesMap = new HashMap<>();

        List<Change> list = ChangeController.getAllChanges();
        Collections.reverse(list);
        List<User> userList = DatabaseUtils.getAllUsers();
        userList.remove(LoginController.user);

        for (User user : userList) {
            List<Change> changesForUser = ChangeController.getChangesForUser(user);
            userChangesMap.put(user, changesForUser);
        }

        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username()));
        changeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().changeDetails()));
        ObservableList<Change> observableList = FXCollections.observableArrayList(list);
        tablicaRevizija.setItems(observableList);
        ObservableList<User> usersObservableList = FXCollections.observableList(userList);
        userComboBox.setItems(usersObservableList);

        userComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                List<Change> changesForSelectedUser = userChangesMap.get(newValue);
                ObservableList<Change> userChangesObservableList = FXCollections.observableArrayList(changesForSelectedUser);
                tablicaRevizija.setItems(userChangesObservableList);
            }
        });
    }


    public void onDeleteUserClicked() {
        if (userComboBox.getSelectionModel().getSelectedItem() != null) {
            User user = userComboBox.getSelectionModel().getSelectedItem();
            if (DatabaseUtils.deleteUser(user)) {
                userChangesMap.remove(user);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setContentText("Successfully deleted the selected user!");
                alert.showAndWait();
                LoginManager loginManager = new LoginManager();
                loginManager.deleteUserById(user.getId());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Deleting error");
                alert.setContentText("An error occurred while deleting the user");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Deleting error");
            alert.setContentText("Please select a user to be deleted!");
            alert.showAndWait();
        }
    }
}

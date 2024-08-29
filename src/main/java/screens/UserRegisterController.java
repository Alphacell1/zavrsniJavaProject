package screens;


import changes.ChangeController;
import exceptions.CredentialTooLongException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import login_manager.LoginManager;
import router.AppRouter;
import sql.DatabaseUtils;
import user.Role;
import user.User;

public class UserRegisterController {


    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passField;
    @FXML
    ComboBox<Role> userRoleComboBox;

    public void initialize() {
        userRoleComboBox.getItems().addAll(Role.values());
    }

    public void register() {
        try {
            String username = usernameTextField.getText();
            String password = passField.getText();
            Role userRole = userRoleComboBox.getSelectionModel().getSelectedItem();
            if (username == null || password == null || userRole == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Field empty error");
                alert.setContentText("Please fill all the necessary fields.");
                alert.showAndWait();
            } else {
                if (username.length() > 20 || password.length() > 20 || password.length() < 6 || username.length() < 6) {
                    throw new CredentialTooLongException("Username or password not between 6 or 20 characters");
                }
                LoginManager loginManager = new LoginManager();
                Integer id = loginManager.addUser(username, password, userRole);
                User user = new User.UserBuilder().setId(id).setUsername(username).setRole(userRole).build();
                LoginController.user = user;
                ChangeController.saveChange("Novi korisnik registriran");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setContentText("Dobrodosli " + username + "!");
                alert.showAndWait();
                AppRouter.openRevisionOverview();
            }
        } catch (CredentialTooLongException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Credential error");
            alert.setContentText("Please make the credentials shorter");
            alert.showAndWait();
        }
    }
}

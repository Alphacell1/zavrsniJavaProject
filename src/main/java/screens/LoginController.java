package screens;

import exceptions.LoginFailedException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import login_manager.LoginManager;
import router.AppRouter;
import user.User;

import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passField;

    public static User user;

    public void login() {
        String username = usernameTextField.getText();
        String pass = passField.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Krivi unos!");
            alert.setContentText("Molimo Vas unesite sve podatke");
            alert.showAndWait();
            return;
        }

        LoginManager loginManager = new LoginManager();
        Optional<User> optionalUser;
        try {
            optionalUser = loginManager.login(username, pass);
        } catch (LoginFailedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ne postoji takav korisnik!");
            alert.setContentText("Molimo Vas unesite korisnika koji postoji");
            alert.showAndWait();
            return;
        }

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ne postoji takav korisnik!");
            alert.setContentText("Molimo Vas unesite korisnika koji postoji");
            alert.showAndWait();
            return;
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login uspješan!");
        alert.setContentText("Dobrodosli " + user.getUsername() + "!");
        alert.showAndWait();
        AppRouter.openRevisionOverview();

    }


    public void registerUser() {
        AppRouter.openRegisterUser();
    }


}

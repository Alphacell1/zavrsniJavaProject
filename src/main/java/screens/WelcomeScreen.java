package screens;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login_manager.LoginManager;
import router.AppRouter;
import user.Role;

import java.io.IOException;

public class WelcomeScreen extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WelcomeScreen.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.sizeToScene();
        stage.setTitle("Revizije");
        stage.setScene(scene);
        AppRouter.stage = stage;
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

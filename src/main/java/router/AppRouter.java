package router;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import screens.WelcomeScreen;


public class AppRouter {

    public static Stage stage;

    public static void openScreen(String screenName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WelcomeScreen.class.getResource(screenName));
            Scene scene = new Scene(fxmlLoader.load());
            stage.sizeToScene();
            stage.setTitle("Revizije");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Neuspjesno otvaranje file-a!");
            alert.showAndWait();
        }
    }

    public static void openRevisionOverview() {
        openScreen("revisionOverview.fxml");
    }

    public static void openPregledLokalnihRevizija(){
        openScreen("viewAllLocalRevisions.fxml");
    }

    public static void openDodavanjeRevizija(){
        openScreen("addFileRevision.fxml");
    }

    public static void openAdminConsole(){
        openScreen("adminConsole.fxml");
    }

    public static void openRegisterUser(){
        openScreen("registrirajKorisnika.fxml");
    }

    public static void odlogiraj(){
        openScreen("login.fxml");
    }


}

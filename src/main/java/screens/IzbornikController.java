package screens;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import router.AppRouter;
import user.Role;

public class IzbornikController {

    public MenuItem konzola;

    public void openPregledSvihRevizija() {
        AppRouter.openRevisionOverview();
    }

    public void openPregledLokalnihRevizija() {
        AppRouter.openPregledLokalnihRevizija();
    }

    public void openDodavanjeRevizija() {
        AppRouter.openDodavanjeRevizija();
    }

    public void openKonzola() {
        AppRouter.openAdminConsole();
    }

    public void odlogiraj(){
        AppRouter.odlogiraj();
    }

    @FXML
    public void initialize() {
        if (!LoginController.user.getRole().equals(Role.ADMIN)) {
            konzola.setVisible(false);
        }
    }
}

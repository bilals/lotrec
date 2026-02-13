package lotrec.guifx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;

public class DialogsFactory {
    private final Stage ownerStage;

    public DialogsFactory(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public Stage getOwnerStage() { return ownerStage; }

    public static boolean confirmDialog(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.initOwner(owner);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    public static void errorDialog(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    public static void infoDialog(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.initOwner(owner);
        alert.setResizable(true);
        alert.showAndWait();
    }
}

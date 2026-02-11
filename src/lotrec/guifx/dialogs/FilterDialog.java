package lotrec.guifx.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilterDialog extends Dialog<Void> {

    public FilterDialog(Stage owner) {
        setTitle("Premodels Filter");
        initOwner(owner);

        VBox content = new VBox(10);
        content.getChildren().add(new Label("Node and expression filtering options"));

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
    }
}

package lotrec.guifx.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SatisfiabilityDialog extends Dialog<Void> {

    public SatisfiabilityDialog(Stage owner) {
        setTitle("Satisfiability Options");
        initOwner(owner);

        VBox content = new VBox(10);
        content.getChildren().add(new Label("Satisfiability check configuration"));

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
}

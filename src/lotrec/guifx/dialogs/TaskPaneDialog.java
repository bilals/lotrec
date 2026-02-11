package lotrec.guifx.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

public class TaskPaneDialog extends Dialog<String> {
    private static final List<String> OPTIONS = Arrays.asList(
        "Open Predefined Logic",
        "Open Existing File",
        "Create New Logic"
    );

    public TaskPaneDialog(Stage owner) {
        setTitle("How do you prefer to start?");
        initOwner(owner);

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Welcome to LoTREC");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);

        ToggleGroup group = new ToggleGroup();
        for (String option : OPTIONS) {
            RadioButton rb = new RadioButton(option);
            rb.setToggleGroup(group);
            content.getChildren().add(rb);
        }
        // Select first option by default
        ((RadioButton) group.getToggles().get(0)).setSelected(true);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Toggle selected = group.getSelectedToggle();
                if (selected instanceof RadioButton) {
                    return ((RadioButton) selected).getText();
                }
            }
            return null;
        });
    }

    public List<String> getOptions() {
        return OPTIONS;
    }
}

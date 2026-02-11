package lotrec.guifx.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lotrec.dataStructure.tableau.Rule;

public class NewRuleDialog extends Dialog<Rule> {

    private final TextField nameField;
    private final TextArea commentArea;

    public NewRuleDialog(Stage owner) {
        this(owner, null);
    }

    public NewRuleDialog(Stage owner, Rule existing) {
        setTitle(existing == null ? "New Rule" : "Edit Rule");
        initOwner(owner);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText("Rule name");
        commentArea = new TextArea();
        commentArea.setPrefRowCount(3);
        commentArea.setPromptText("Optional comment");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Comment:"), 0, 1);
        grid.add(commentArea, 1, 1);

        if (existing != null) {
            nameField.setText(existing.getName());
            if (existing.getComment() != null) {
                commentArea.setText(existing.getComment());
            }
        }

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Rule name is required.", ButtonType.OK);
                alert.initOwner(owner);
                alert.showAndWait();
                event.consume();
            }
        });

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                Rule rule;
                if (existing != null) {
                    rule = existing;
                    rule.setName(nameField.getText().trim());
                } else {
                    rule = new Rule(nameField.getText().trim(), false);
                }
                rule.setComment(commentArea.getText().trim());
                return rule;
            }
            return null;
        });
    }

    public String getRuleName() {
        return nameField.getText().trim();
    }
}

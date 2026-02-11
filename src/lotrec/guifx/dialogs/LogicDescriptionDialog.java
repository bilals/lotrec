package lotrec.guifx.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogicDescriptionDialog extends Dialog<Void> {
    private final Label nameLabel;
    private final TextArea descriptionArea;
    private String logicName;

    public LogicDescriptionDialog(Stage owner) {
        setTitle("Logic Description");
        initOwner(owner);

        nameLabel = new Label();
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setPrefRowCount(8);
        descriptionArea.setWrapText(true);

        VBox content = new VBox(10);
        content.getChildren().addAll(nameLabel, descriptionArea);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    public void setDescription(String name, String description) {
        this.logicName = name;
        nameLabel.setText(name);
        descriptionArea.setText(description);
    }

    public String getLogicName() { return logicName; }
    public String getDescription() { return descriptionArea.getText(); }
}

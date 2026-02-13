package lotrec.guifx.components;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * A collapsible comment field: a TextArea with a toggle button
 * that switches between 1-row (collapsed) and 4-row (expanded) views.
 */
public class ExpandableCommentField extends VBox {

    private final TextArea textArea;
    private final Button toggleButton;
    private boolean expanded = false;

    public ExpandableCommentField() {
        setSpacing(2);

        textArea = new TextArea();
        textArea.setPrefRowCount(1);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPromptText("Comment");

        toggleButton = new Button("Comment \u25BC");
        toggleButton.setStyle("-fx-font-size: 10px; -fx-padding: 1 6;");
        toggleButton.setOnAction(e -> toggleExpanded());

        getChildren().addAll(toggleButton, textArea);
    }

    private void toggleExpanded() {
        expanded = !expanded;
        if (expanded) {
            textArea.setPrefRowCount(4);
            toggleButton.setText("Comment \u25B2");
        } else {
            textArea.setPrefRowCount(1);
            toggleButton.setText("Comment \u25BC");
        }
    }

    public void setText(String text) {
        textArea.setText(text != null ? text : "");
    }

    public String getText() {
        return textArea.getText();
    }

    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    public void clear() {
        textArea.clear();
    }
}

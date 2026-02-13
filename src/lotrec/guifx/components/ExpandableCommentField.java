package lotrec.guifx.components;

import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * A collapsible comment field using a TitledPane that wraps a TextArea.
 * Collapsed by default; matches the LogicDefTab description pattern.
 */
public class ExpandableCommentField extends VBox {

    private final TextArea textArea;
    private final TitledPane titledPane;

    public ExpandableCommentField() {
        textArea = new TextArea();
        textArea.setPrefRowCount(3);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPromptText("Comment");

        titledPane = new TitledPane("Comment", textArea);
        titledPane.setExpanded(false);

        getChildren().add(titledPane);
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

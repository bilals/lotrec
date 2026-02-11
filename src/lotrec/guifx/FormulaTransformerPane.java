package lotrec.guifx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * JavaFX pane for formula infix/prefix conversion display.
 * Mirrors Swing FormulaTransformerGUI JPanel.
 */
public class FormulaTransformerPane extends VBox {

    private final TextField infixField;
    private final TextField prefixField;
    private final Button toPrefixButton;
    private final Button toInfixButton;

    public FormulaTransformerPane() {
        setSpacing(10);
        setPadding(new Insets(10));

        infixField = new TextField();
        infixField.setPromptText("Infix notation (e.g., p & q -> ~r)");
        HBox.setHgrow(infixField, Priority.ALWAYS);

        prefixField = new TextField();
        prefixField.setPromptText("Prefix notation (e.g., imp and p q not r)");
        HBox.setHgrow(prefixField, Priority.ALWAYS);

        toPrefixButton = new Button("To Prefix \u2192");
        toInfixButton = new Button("\u2190 To Infix");

        HBox buttonBar = new HBox(10, toPrefixButton, toInfixButton);

        getChildren().addAll(
            new Label("Infix:"), infixField,
            buttonBar,
            new Label("Prefix:"), prefixField
        );
    }

    public TextField getInfixField() { return infixField; }
    public TextField getPrefixField() { return prefixField; }
    public Button getToPrefixButton() { return toPrefixButton; }
    public Button getToInfixButton() { return toInfixButton; }
}

package lotrec.guifx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;

public class PremodelSettingsPane extends VBox {

    private final TextArea formulaField;
    private final Button buildButton;
    private final Button stepButton;
    private final Button satCheckButton;
    private final ComboBox<String> testingFormulaeCombo;
    private Logic currentLogic;

    public PremodelSettingsPane() {
        setSpacing(10);
        setPadding(new Insets(10));

        // Formula input (must be created before combo handler references it)
        formulaField = new TextArea();

        // Testing formulae dropdown
        testingFormulaeCombo = new ComboBox<>();
        testingFormulaeCombo.setPromptText("Select predefined formula...");
        testingFormulaeCombo.setMaxWidth(Double.MAX_VALUE);
        testingFormulaeCombo.setOnAction(e -> {
            String selected = testingFormulaeCombo.getValue();
            if (selected != null) {
                formulaField.setText(selected);
            }
        });
        formulaField.setPromptText("Enter formula in prefix notation (e.g., and p q)");
        formulaField.setPrefRowCount(3);
        formulaField.setWrapText(true);
        VBox.setVgrow(formulaField, Priority.SOMETIMES);

        // Build buttons
        buildButton = new Button("Build Premodels");
        stepButton = new Button("Step By Step...");
        satCheckButton = new Button("Satisfiability Check...");
        HBox buttonBar = new HBox(5, buildButton, stepButton, satCheckButton);

        getChildren().addAll(
            new Label("Testing Formulae:"), testingFormulaeCombo,
            new Label("Formula Code:"), formulaField,
            buttonBar
        );
    }

    public void setLogic(Logic logic) {
        this.currentLogic = logic;
        ObservableList<String> formulae = FXCollections.observableArrayList();
        if (logic != null && logic.getTestingFormulae() != null) {
            for (Object obj : logic.getTestingFormulae()) {
                formulae.add(obj.toString());
            }
        }
        testingFormulaeCombo.setItems(formulae);
    }

    public MarkedExpression parseFormula() throws ParseException {
        if (currentLogic == null || formulaField.getText().trim().isEmpty()) {
            return null;
        }
        OldiesTokenizer tokenizer = new OldiesTokenizer(currentLogic);
        tokenizer.initializeTokenizerAndProps();
        MarkedExpression formula = new MarkedExpression(
            tokenizer.parseExpression(formulaField.getText().trim())
        );
        tokenizer.verifyCodeEnd();
        return formula;
    }

    public TextArea getFormulaField() { return formulaField; }
    public Button getBuildButton() { return buildButton; }
    public Button getStepButton() { return stepButton; }
    public Button getSatCheckButton() { return satCheckButton; }
    public ComboBox<String> getTestingFormulaeCombo() { return testingFormulaeCombo; }
    public Logic getCurrentLogic() { return currentLogic; }
}

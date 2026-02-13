package lotrec.guifx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;

import java.util.ArrayList;
import java.util.List;

public class PremodelSettingsPane extends VBox {

    private final TextArea formulaField;
    private final TextField infixField;
    private final Button buildButton;
    private final Button stepButton;
    private final Button satCheckButton;
    private final ComboBox<String> testingFormulaeCombo;
    private Logic currentLogic;
    private final List<TestingFormula> formulaRefs = new ArrayList<>();

    public PremodelSettingsPane() {
        setSpacing(10);
        setPadding(new Insets(10));

        // Formula input (must be created before combo handler references it)
        formulaField = new TextArea();

        // Infix display field
        infixField = new TextField();
        infixField.setEditable(false);
        infixField.setStyle("-fx-background-color: #f4f4f4;");
        infixField.setPromptText("Infix display will appear here");

        // Testing formulae dropdown
        testingFormulaeCombo = new ComboBox<>();
        testingFormulaeCombo.setPromptText("Select predefined formula...");
        testingFormulaeCombo.setMaxWidth(Double.MAX_VALUE);
        testingFormulaeCombo.setOnAction(e -> {
            int index = testingFormulaeCombo.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < formulaRefs.size()) {
                TestingFormula tf = formulaRefs.get(index);
                formulaField.setText(tf.getCode());
                infixField.setText(tf.getDisplayName());
            }
        });
        formulaField.setPromptText("Enter formula in prefix notation (e.g., and p q)");
        formulaField.setPrefRowCount(3);
        formulaField.setWrapText(true);
        VBox.setVgrow(formulaField, Priority.SOMETIMES);

        // Try to parse and show infix when focus leaves the formula field
        formulaField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                updateInfixFromCode();
            }
        });

        // Build buttons
        buildButton = new Button("Build Premodels");
        stepButton = new Button("Step By Step...");
        satCheckButton = new Button("Satisfiability Check...");
        HBox buttonBar = new HBox(5, buildButton, stepButton, satCheckButton);

        getChildren().addAll(
            new Label("Testing Formulae:"), testingFormulaeCombo,
            new Label("Formula Code:"), formulaField,
            new Label("Display Format:"), infixField,
            buttonBar
        );
    }

    public void setLogic(Logic logic) {
        this.currentLogic = logic;
        formulaRefs.clear();
        ObservableList<String> formulae = FXCollections.observableArrayList();
        if (logic != null && logic.getTestingFormulae() != null) {
            for (Object obj : logic.getTestingFormulae()) {
                if (obj instanceof TestingFormula) {
                    TestingFormula tf = (TestingFormula) obj;
                    formulaRefs.add(tf);
                    formulae.add(tf.getDisplayName());
                }
            }
        }
        testingFormulaeCombo.setItems(formulae);
        infixField.clear();
    }

    private void updateInfixFromCode() {
        String code = formulaField.getText().trim();
        if (code.isEmpty() || currentLogic == null) {
            infixField.clear();
            return;
        }
        try {
            OldiesTokenizer tokenizer = new OldiesTokenizer(currentLogic);
            tokenizer.initializeTokenizerAndProps();
            MarkedExpression me = new MarkedExpression(tokenizer.parseExpression(code));
            tokenizer.verifyCodeEnd();
            infixField.setText(me.expression.toString());
        } catch (Exception e) {
            infixField.setText("(parse error)");
        }
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

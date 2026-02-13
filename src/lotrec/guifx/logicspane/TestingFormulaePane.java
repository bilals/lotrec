package lotrec.guifx.logicspane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.guifx.components.ExpandableCommentField;

import java.util.ArrayList;
import java.util.List;

public class TestingFormulaePane extends VBox {

    private final Logic logic;
    private final ListView<String> formulaList;
    private final ObservableList<String> formulaDisplayNames;
    private final TextArea codeArea;
    private final ExpandableCommentField commentField;
    // Keep parallel list of TestingFormula references for index-based lookup
    private final List<TestingFormula> formulaRefs = new ArrayList<>();

    public TestingFormulaePane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        formulaDisplayNames = FXCollections.observableArrayList();
        formulaList = new ListView<>(formulaDisplayNames);
        formulaList.setPrefHeight(200);
        VBox.setVgrow(formulaList, Priority.ALWAYS);

        // Code area (replaces single-line TextField)
        codeArea = new TextArea();
        codeArea.setPromptText("Formula code (prefix notation)");
        codeArea.setPrefRowCount(2);
        codeArea.setWrapText(true);

        // Comment field
        commentField = new ExpandableCommentField();

        Button addBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        Button runBtn = new Button("Run Test");
        HBox buttonBar = new HBox(5, addBtn, removeBtn, runBtn);

        // Selection listener
        formulaList.getSelectionModel().selectedIndexProperty().addListener(
            (obs, oldVal, newVal) -> showFormulaDetails(newVal.intValue())
        );

        getChildren().addAll(new Label("Predefined Formulas:"), formulaList,
            new Label("Formula Code:"), codeArea, commentField, buttonBar);
        refreshList();
    }

    public void refreshList() {
        int previousIndex = formulaList.getSelectionModel().getSelectedIndex();
        formulaDisplayNames.clear();
        formulaRefs.clear();
        if (logic.getTestingFormulae() != null) {
            for (Object obj : logic.getTestingFormulae()) {
                if (obj instanceof TestingFormula) {
                    TestingFormula tf = (TestingFormula) obj;
                    formulaRefs.add(tf);
                    formulaDisplayNames.add(tf.getDisplayName());
                }
            }
        }
        // Handle duplicate display names by appending index
        for (int i = 0; i < formulaDisplayNames.size(); i++) {
            String name = formulaDisplayNames.get(i);
            int count = 0;
            for (int j = 0; j < i; j++) {
                if (formulaDisplayNames.get(j).startsWith(name)) {
                    count++;
                }
            }
            if (count > 0) {
                formulaDisplayNames.set(i, name + " (" + (count + 1) + ")");
            }
        }
        // Restore selection or auto-select first
        if (previousIndex >= 0 && previousIndex < formulaDisplayNames.size()) {
            formulaList.getSelectionModel().select(previousIndex);
        } else if (!formulaDisplayNames.isEmpty()) {
            formulaList.getSelectionModel().selectFirst();
        }
    }

    private void showFormulaDetails(int index) {
        if (index < 0 || index >= formulaRefs.size()) {
            codeArea.clear();
            commentField.clear();
            return;
        }
        TestingFormula tf = formulaRefs.get(index);
        codeArea.setText(tf.getCode());
        commentField.setText(tf.getComment());
    }

    public ListView<String> getFormulaList() { return formulaList; }
    public TextArea getCodeArea() { return codeArea; }
    public Logic getLogic() { return logic; }
}

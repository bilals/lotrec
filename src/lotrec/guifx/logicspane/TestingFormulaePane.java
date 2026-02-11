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

public class TestingFormulaePane extends VBox {

    private final Logic logic;
    private final ListView<String> formulaList;
    private final ObservableList<String> formulaNames;
    private final TextField formulaField;

    public TestingFormulaePane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        formulaNames = FXCollections.observableArrayList();
        formulaList = new ListView<>(formulaNames);
        formulaList.setPrefHeight(200);
        VBox.setVgrow(formulaList, Priority.ALWAYS);

        formulaField = new TextField();
        formulaField.setPromptText("Enter formula...");

        Button addBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        Button runBtn = new Button("Run Test");
        HBox buttonBar = new HBox(5, addBtn, removeBtn, runBtn);

        getChildren().addAll(new Label("Predefined Formulas:"), formulaList,
            new Label("Formula:"), formulaField, buttonBar);
        refreshList();
    }

    public void refreshList() {
        formulaNames.clear();
        if (logic.getTestingFormulae() != null) {
            for (Object obj : logic.getTestingFormulae()) {
                if (obj instanceof TestingFormula) {
                    formulaNames.add(((TestingFormula) obj).getCode());
                }
            }
        }
    }

    public ListView<String> getFormulaList() { return formulaList; }
    public TextField getFormulaField() { return formulaField; }
    public Logic getLogic() { return logic; }
}

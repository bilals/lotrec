package lotrec.guifx.logicspane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.process.Strategy;

public class StratTabPane extends VBox {

    private final Logic logic;
    private final ListView<String> strategyList;
    private final ObservableList<String> strategyNames;
    private final TextArea codeArea;

    public StratTabPane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        strategyNames = FXCollections.observableArrayList();
        strategyList = new ListView<>(strategyNames);
        strategyList.setPrefHeight(150);

        codeArea = new TextArea();
        codeArea.setPromptText("Strategy code...");
        codeArea.setPrefRowCount(6);
        VBox.setVgrow(codeArea, Priority.ALWAYS);

        Button addBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        HBox buttonBar = new HBox(5, addBtn, removeBtn);

        strategyList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showStrategyDetails(newVal)
        );

        getChildren().addAll(new Label("Strategies:"), strategyList, new Label("Code:"), codeArea, buttonBar);
        refreshList();
    }

    public void refreshList() {
        strategyNames.clear();
        if (logic.getStrategies() != null) {
            for (Object obj : logic.getStrategies()) {
                if (obj instanceof Strategy) {
                    strategyNames.add(((Strategy) obj).getWorkerName());
                }
            }
        }
    }

    private void showStrategyDetails(String name) {
        if (name == null) return;
        Strategy strategy = logic.getStrategy(name);
        if (strategy != null) {
            codeArea.setText(strategy.getCode());
        }
    }

    public ListView<String> getStrategyList() { return strategyList; }
    public TextArea getCodeArea() { return codeArea; }
    public Logic getLogic() { return logic; }
}

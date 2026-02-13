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
import lotrec.guifx.components.ExpandableCommentField;

public class StratTabPane extends VBox {

    private final Logic logic;
    private final ListView<String> strategyList;
    private final ObservableList<String> strategyNames;
    private final TextArea codeArea;
    private final ComboBox<String> mainStrategyCombo;
    private final ExpandableCommentField commentField;

    public StratTabPane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        // Main strategy selector
        mainStrategyCombo = new ComboBox<>();
        mainStrategyCombo.setMaxWidth(Double.MAX_VALUE);
        mainStrategyCombo.setOnAction(e -> {
            String selected = mainStrategyCombo.getValue();
            if (selected != null) {
                logic.setMainStrategyName(selected);
            }
        });
        HBox mainStratBox = new HBox(5, new Label("Main Strategy:"), mainStrategyCombo);
        HBox.setHgrow(mainStrategyCombo, Priority.ALWAYS);

        strategyNames = FXCollections.observableArrayList();
        strategyList = new ListView<>(strategyNames);
        strategyList.setPrefHeight(150);

        codeArea = new TextArea();
        codeArea.setPromptText("Strategy code...");
        codeArea.setPrefRowCount(6);
        VBox.setVgrow(codeArea, Priority.ALWAYS);

        // Comment field
        commentField = new ExpandableCommentField();

        Button addBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        HBox buttonBar = new HBox(5, addBtn, removeBtn);

        strategyList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showStrategyDetails(newVal)
        );

        getChildren().addAll(mainStratBox, new Label("Strategies:"), strategyList,
            new Label("Code:"), codeArea, commentField, buttonBar);
        refreshList();
    }

    public void refreshList() {
        String previousSelection = strategyList.getSelectionModel().getSelectedItem();
        strategyNames.clear();
        ObservableList<String> comboItems = FXCollections.observableArrayList();
        if (logic.getStrategies() != null) {
            for (Object obj : logic.getStrategies()) {
                if (obj instanceof Strategy) {
                    String name = ((Strategy) obj).getWorkerName();
                    strategyNames.add(name);
                    comboItems.add(name);
                }
            }
        }

        // Update main strategy combo
        mainStrategyCombo.setItems(comboItems);
        String mainName = logic.getMainStrategyName();
        if (mainName != null && comboItems.contains(mainName)) {
            mainStrategyCombo.setValue(mainName);
        }

        // Restore previous selection, or select main strategy, or select first
        if (previousSelection != null && strategyNames.contains(previousSelection)) {
            strategyList.getSelectionModel().select(previousSelection);
        } else if (mainName != null && strategyNames.contains(mainName)) {
            strategyList.getSelectionModel().select(mainName);
        } else if (!strategyNames.isEmpty()) {
            strategyList.getSelectionModel().selectFirst();
        }
    }

    private void showStrategyDetails(String name) {
        if (name == null) {
            codeArea.clear();
            commentField.clear();
            return;
        }
        Strategy strategy = logic.getStrategy(name);
        if (strategy != null) {
            codeArea.setText(strategy.getCode());
            commentField.setText(strategy.getComment());
        }
    }

    public ListView<String> getStrategyList() { return strategyList; }
    public TextArea getCodeArea() { return codeArea; }
    public Logic getLogic() { return logic; }
}

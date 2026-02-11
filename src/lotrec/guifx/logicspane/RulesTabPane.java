package lotrec.guifx.logicspane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.tableau.Rule;
import lotrec.guifx.dialogs.NewRuleDialog;
import lotrec.guifx.dialogs.ConditionDialog;
import lotrec.guifx.dialogs.ActionDialog;

public class RulesTabPane extends VBox {

    private final Logic logic;
    private final ListView<String> rulesList;
    private final ObservableList<String> ruleNames;
    private final TreeView<String> conditionsTree;
    private final TreeView<String> actionsTree;

    public RulesTabPane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        // Rules list
        ruleNames = FXCollections.observableArrayList();
        rulesList = new ListView<>(ruleNames);
        rulesList.setPrefHeight(150);

        // Conditions tree
        TreeItem<String> condRoot = new TreeItem<>("Conditions");
        condRoot.setExpanded(true);
        conditionsTree = new TreeView<>(condRoot);
        conditionsTree.setPrefHeight(120);

        // Actions tree
        TreeItem<String> actRoot = new TreeItem<>("Actions");
        actRoot.setExpanded(true);
        actionsTree = new TreeView<>(actRoot);
        actionsTree.setPrefHeight(120);

        // Buttons
        Button addRuleBtn = new Button("Add Rule");
        addRuleBtn.setOnAction(e -> addRule());
        Button removeRuleBtn = new Button("Remove Rule");
        removeRuleBtn.setOnAction(e -> removeRule());
        Button addCondBtn = new Button("Add Condition");
        addCondBtn.setOnAction(e -> addCondition());
        Button addActBtn = new Button("Add Action");
        addActBtn.setOnAction(e -> addAction());
        HBox ruleButtons = new HBox(5, addRuleBtn, removeRuleBtn);
        HBox editButtons = new HBox(5, addCondBtn, addActBtn);

        // Selection listener
        rulesList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showRuleDetails(newVal)
        );

        VBox.setVgrow(rulesList, Priority.SOMETIMES);
        VBox.setVgrow(conditionsTree, Priority.SOMETIMES);
        VBox.setVgrow(actionsTree, Priority.SOMETIMES);

        getChildren().addAll(
            new Label("Rules:"), rulesList, ruleButtons,
            new Label("Conditions:"), conditionsTree,
            new Label("Actions:"), actionsTree, editButtons
        );
        refreshList();
    }

    public void refreshList() {
        ruleNames.clear();
        if (logic.getRules() != null) {
            for (Object obj : logic.getRules()) {
                if (obj instanceof Rule) {
                    ruleNames.add(((Rule) obj).getName());
                }
            }
        }
    }

    private void showRuleDetails(String name) {
        if (name == null) return;
        Rule rule = logic.getRule(name);
        if (rule == null) return;

        // Populate conditions tree
        TreeItem<String> condRoot = conditionsTree.getRoot();
        condRoot.getChildren().clear();
        if (rule.getConditions() != null) {
            for (Object cond : rule.getConditions()) {
                condRoot.getChildren().add(new TreeItem<>(cond.toString()));
            }
        }

        // Populate actions tree
        TreeItem<String> actRoot = actionsTree.getRoot();
        actRoot.getChildren().clear();
        if (rule.getActions() != null) {
            for (Object act : rule.getActions()) {
                actRoot.getChildren().add(new TreeItem<>(act.toString()));
            }
        }
    }

    private void addRule() {
        if (getScene() == null || getScene().getWindow() == null) return;
        NewRuleDialog dialog = new NewRuleDialog(
            (javafx.stage.Stage) getScene().getWindow());
        dialog.showAndWait().ifPresent(rule -> {
            logic.addRule(rule);
            refreshList();
        });
    }

    private void removeRule() {
        String selected = rulesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Rule rule = logic.getRule(selected);
            if (rule != null) {
                logic.removeRule(rule);
                refreshList();
            }
        }
    }

    private void addCondition() {
        String selectedRule = rulesList.getSelectionModel().getSelectedItem();
        if (selectedRule == null) return;
        if (getScene() == null || getScene().getWindow() == null) return;
        ConditionDialog dialog = new ConditionDialog(
            (javafx.stage.Stage) getScene().getWindow());
        dialog.showAndWait().ifPresent(result -> {
            // Result stored for condition creation; actual instantiation
            // requires formula parsing which is deferred to engine integration
            showRuleDetails(selectedRule);
        });
    }

    private void addAction() {
        String selectedRule = rulesList.getSelectionModel().getSelectedItem();
        if (selectedRule == null) return;
        if (getScene() == null || getScene().getWindow() == null) return;
        ActionDialog dialog = new ActionDialog(
            (javafx.stage.Stage) getScene().getWindow());
        dialog.showAndWait().ifPresent(result -> {
            showRuleDetails(selectedRule);
        });
    }

    /**
     * Enables drag-and-drop reorder on the rules list.
     */
    public void enableDragAndDropReorder() {
        rulesList.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.getItem() == null) return;
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(cell.getIndex()));
                db.setContent(content);
                event.consume();
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    int draggedIdx = Integer.parseInt(db.getString());
                    int targetIdx = cell.isEmpty() ? ruleNames.size() - 1 : cell.getIndex();

                    if (draggedIdx != targetIdx && draggedIdx >= 0 && draggedIdx < ruleNames.size()) {
                        // Reorder in underlying logic
                        java.util.Vector<Rule> rules = logic.getRules();
                        if (draggedIdx < rules.size() && targetIdx < rules.size()) {
                            Rule draggedRule = rules.remove(draggedIdx);
                            rules.add(targetIdx, draggedRule);
                            refreshList();
                            rulesList.getSelectionModel().select(targetIdx);
                        }
                    }
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });

            cell.setOnDragDone(javafx.event.Event::consume);
            return cell;
        });
    }

    public ListView<String> getRulesList() { return rulesList; }
    public TreeView<String> getConditionsTree() { return conditionsTree; }
    public TreeView<String> getActionsTree() { return actionsTree; }
    public Logic getLogic() { return logic; }
}

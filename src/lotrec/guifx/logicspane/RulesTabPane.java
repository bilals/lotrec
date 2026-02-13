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
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.process.AbstractAction;
import lotrec.guifx.components.ExpandableCommentField;
import lotrec.guifx.dialogs.NewRuleDialog;
import lotrec.guifx.dialogs.ConditionDialog;
import lotrec.guifx.dialogs.ActionDialog;

import java.util.ArrayList;
import java.util.List;

public class RulesTabPane extends VBox {

    private final Logic logic;
    private final ListView<String> rulesList;
    private final ObservableList<String> ruleNames;
    private final TreeView<String> conditionsTree;
    private final TreeView<String> actionsTree;
    private final ExpandableCommentField commentField;

    public RulesTabPane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        // Rules list
        ruleNames = FXCollections.observableArrayList();
        rulesList = new ListView<>(ruleNames);
        rulesList.setPrefHeight(150);

        // Rule buttons
        Button addRuleBtn = new Button("Add Rule");
        addRuleBtn.setOnAction(e -> addRule());
        Button editRuleBtn = new Button("Edit Rule");
        editRuleBtn.setOnAction(e -> editRule());
        Button removeRuleBtn = new Button("Remove Rule");
        removeRuleBtn.setOnAction(e -> removeRule());
        HBox ruleButtons = new HBox(5, addRuleBtn, editRuleBtn, removeRuleBtn);

        // Comment field for rule
        commentField = new ExpandableCommentField();

        // Conditions tree
        TreeItem<String> condRoot = new TreeItem<>("Conditions");
        condRoot.setExpanded(true);
        conditionsTree = new TreeView<>(condRoot);
        conditionsTree.setPrefHeight(120);

        // Condition buttons
        Button addCondBtn = new Button("Add");
        addCondBtn.setOnAction(e -> addCondition());
        Button editCondBtn = new Button("Edit");
        editCondBtn.setOnAction(e -> editCondition());
        Button deleteCondBtn = new Button("Delete");
        deleteCondBtn.setOnAction(e -> deleteCondition());
        HBox condButtons = new HBox(5, addCondBtn, editCondBtn, deleteCondBtn);

        // Actions tree
        TreeItem<String> actRoot = new TreeItem<>("Actions");
        actRoot.setExpanded(true);
        actionsTree = new TreeView<>(actRoot);
        actionsTree.setPrefHeight(120);

        // Action buttons
        Button addActBtn = new Button("Add");
        addActBtn.setOnAction(e -> addAction());
        Button editActBtn = new Button("Edit");
        editActBtn.setOnAction(e -> editAction());
        Button deleteActBtn = new Button("Delete");
        deleteActBtn.setOnAction(e -> deleteAction());
        HBox actButtons = new HBox(5, addActBtn, editActBtn, deleteActBtn);

        // Selection listener
        rulesList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showRuleDetails(newVal)
        );

        VBox.setVgrow(rulesList, Priority.SOMETIMES);
        VBox.setVgrow(conditionsTree, Priority.SOMETIMES);
        VBox.setVgrow(actionsTree, Priority.SOMETIMES);

        getChildren().addAll(
            new Label("Rules:"), rulesList, ruleButtons, commentField,
            new Label("Conditions:"), conditionsTree, condButtons,
            new Label("Actions:"), actionsTree, actButtons
        );
        refreshList();
    }

    public void refreshList() {
        String previousSelection = rulesList.getSelectionModel().getSelectedItem();
        ruleNames.clear();
        if (logic.getRules() != null) {
            for (Object obj : logic.getRules()) {
                if (obj instanceof Rule) {
                    ruleNames.add(((Rule) obj).getName());
                }
            }
        }
        // Restore previous selection or auto-select first
        if (previousSelection != null && ruleNames.contains(previousSelection)) {
            rulesList.getSelectionModel().select(previousSelection);
        } else if (!ruleNames.isEmpty()) {
            rulesList.getSelectionModel().selectFirst();
        }
    }

    private void showRuleDetails(String name) {
        if (name == null) {
            conditionsTree.getRoot().getChildren().clear();
            actionsTree.getRoot().getChildren().clear();
            commentField.clear();
            return;
        }
        Rule rule = logic.getRule(name);
        if (rule == null) return;

        // Update comment
        commentField.setText(rule.getComment());

        // Populate conditions tree with readable code
        TreeItem<String> condRoot = conditionsTree.getRoot();
        condRoot.getChildren().clear();
        if (rule.getConditions() != null) {
            for (Object cond : rule.getConditions()) {
                String display = (cond instanceof AbstractCondition)
                    ? ((AbstractCondition) cond).getCode()
                    : cond.toString();
                condRoot.getChildren().add(new TreeItem<>(display));
            }
        }

        // Populate actions tree with readable code
        TreeItem<String> actRoot = actionsTree.getRoot();
        actRoot.getChildren().clear();
        if (rule.getActions() != null) {
            for (Object act : rule.getActions()) {
                String display = (act instanceof AbstractAction)
                    ? ((AbstractAction) act).getCode()
                    : act.toString();
                actRoot.getChildren().add(new TreeItem<>(display));
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
            rulesList.getSelectionModel().select(rule.getName());
        });
    }

    private void editRule() {
        String selected = rulesList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Rule rule = logic.getRule(selected);
        if (rule == null) return;
        if (getScene() == null || getScene().getWindow() == null) return;
        NewRuleDialog dialog = new NewRuleDialog(
            (javafx.stage.Stage) getScene().getWindow(), rule);
        dialog.showAndWait().ifPresent(updated -> {
            refreshList();
            rulesList.getSelectionModel().select(updated.getName());
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
            showRuleDetails(selectedRule);
        });
    }

    private void editCondition() {
        String selectedRule = rulesList.getSelectionModel().getSelectedItem();
        if (selectedRule == null) return;
        TreeItem<String> selectedItem = conditionsTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem == conditionsTree.getRoot()) return;

        int index = conditionsTree.getRoot().getChildren().indexOf(selectedItem);
        Rule rule = logic.getRule(selectedRule);
        if (rule == null || index < 0 || index >= rule.getConditions().size()) return;

        AbstractCondition cond = (AbstractCondition) rule.getConditions().get(index);
        // Build ConditionResult from existing condition
        List<String> params = new ArrayList<>();
        for (Parameter param : cond.getParameters()) {
            String code = param.getValueCode();
            if (code != null) params.add(code);
        }
        ConditionDialog.ConditionResult existing =
            new ConditionDialog.ConditionResult(cond.getName(), params);

        if (getScene() == null || getScene().getWindow() == null) return;
        ConditionDialog dialog = new ConditionDialog(
            (javafx.stage.Stage) getScene().getWindow(), existing);
        dialog.showAndWait().ifPresent(result -> {
            showRuleDetails(selectedRule);
        });
    }

    private void deleteCondition() {
        String selectedRule = rulesList.getSelectionModel().getSelectedItem();
        if (selectedRule == null) return;
        TreeItem<String> selectedItem = conditionsTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem == conditionsTree.getRoot()) return;

        int index = conditionsTree.getRoot().getChildren().indexOf(selectedItem);
        Rule rule = logic.getRule(selectedRule);
        if (rule == null || index < 0 || index >= rule.getConditions().size()) return;

        AbstractCondition cond = (AbstractCondition) rule.getConditions().get(index);
        rule.removeCondition(cond);
        showRuleDetails(selectedRule);
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

    private void editAction() {
        String selectedRule = rulesList.getSelectionModel().getSelectedItem();
        if (selectedRule == null) return;
        TreeItem<String> selectedItem = actionsTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem == actionsTree.getRoot()) return;

        int index = actionsTree.getRoot().getChildren().indexOf(selectedItem);
        Rule rule = logic.getRule(selectedRule);
        if (rule == null || index < 0 || index >= rule.getActions().size()) return;

        AbstractAction act = (AbstractAction) rule.getActions().get(index);
        List<String> params = new ArrayList<>();
        for (Parameter param : act.getParameters()) {
            String code = param.getValueCode();
            if (code != null) params.add(code);
        }
        ActionDialog.ActionResult existing =
            new ActionDialog.ActionResult(act.getName(), params);

        if (getScene() == null || getScene().getWindow() == null) return;
        ActionDialog dialog = new ActionDialog(
            (javafx.stage.Stage) getScene().getWindow(), existing);
        dialog.showAndWait().ifPresent(result -> {
            showRuleDetails(selectedRule);
        });
    }

    private void deleteAction() {
        String selectedRule = rulesList.getSelectionModel().getSelectedItem();
        if (selectedRule == null) return;
        TreeItem<String> selectedItem = actionsTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem == actionsTree.getRoot()) return;

        int index = actionsTree.getRoot().getChildren().indexOf(selectedItem);
        Rule rule = logic.getRule(selectedRule);
        if (rule == null || index < 0 || index >= rule.getActions().size()) return;

        AbstractAction act = (AbstractAction) rule.getActions().get(index);
        rule.removeAction(act);
        showRuleDetails(selectedRule);
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

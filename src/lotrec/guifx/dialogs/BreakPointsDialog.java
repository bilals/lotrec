package lotrec.guifx.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lotrec.process.AbstractWorker;
import lotrec.process.EventMachine;
import lotrec.process.Routine;
import lotrec.process.Strategy;

import java.util.ArrayList;

public class BreakPointsDialog extends Dialog<ArrayList<Integer>> {

    private final TreeView<String> strategyTree;
    private final CheckBoxTreeItem<String> rootItem;
    private final ArrayList<CheckBoxTreeItem<String>> leafItems = new ArrayList<>();
    private int levelCounter;

    public BreakPointsDialog(Stage owner) {
        setTitle("Step By Step — Break Points");
        initOwner(owner);
        setResizable(true);

        rootItem = new CheckBoxTreeItem<>("Strategy");
        rootItem.setExpanded(true);
        strategyTree = new TreeView<>(rootItem);
        strategyTree.setCellFactory(CheckBoxTreeCell.forTreeView());
        strategyTree.setPrefHeight(400);
        strategyTree.setPrefWidth(400);
        VBox.setVgrow(strategyTree, Priority.ALWAYS);

        Button selectAllBtn = new Button("Select All");
        selectAllBtn.setOnAction(e -> setAllSelected(rootItem, true));
        Button invertBtn = new Button("Invert Selection");
        invertBtn.setOnAction(e -> invertSelection(rootItem));
        HBox buttonBar = new HBox(5, selectAllBtn, invertBtn);

        VBox content = new VBox(10, strategyTree, buttonBar);
        content.setPadding(new Insets(20));

        getDialogPane().setContent(content);

        ButtonType startButton = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(startButton, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == startButton) {
                ArrayList<Integer> breakPoints = new ArrayList<>();
                for (int i = 0; i < leafItems.size(); i++) {
                    if (leafItems.get(i).isSelected()) {
                        breakPoints.add(i);
                    }
                }
                return breakPoints;
            }
            return null;
        });
    }

    /**
     * Populates the tree from a parsed Strategy, matching the Swing
     * ControlsPanel.displayStepsTree() logic.
     */
    public void populateFromStrategy(Strategy strategy) {
        rootItem.getChildren().clear();
        leafItems.clear();
        levelCounter = 0;
        rootItem.setValue(strategy.getWorkerName());
        fillWorkerChildren(strategy, rootItem);
    }

    private void fillWorkerChildren(AbstractWorker parentWorker, CheckBoxTreeItem<String> parentNode) {
        if (!(parentWorker instanceof Routine)) return;
        for (AbstractWorker subWorker : ((Routine) parentWorker).getWorkers()) {
            if (subWorker instanceof Routine) {
                CheckBoxTreeItem<String> branchNode = new CheckBoxTreeItem<>(subWorker.getWorkerName());
                branchNode.setExpanded(true);
                parentNode.getChildren().add(branchNode);
                fillWorkerChildren(subWorker, branchNode);
            } else {
                // Leaf node (EventMachine = individual rule)
                CheckBoxTreeItem<String> leafNode = new CheckBoxTreeItem<>(subWorker.getWorkerName());
                parentNode.getChildren().add(leafNode);
                if (subWorker instanceof EventMachine) {
                    ((EventMachine) subWorker).setLevel(levelCounter);
                }
                leafItems.add(leafNode);
                levelCounter++;
            }
        }
    }

    private void setAllSelected(CheckBoxTreeItem<String> item, boolean selected) {
        if (item.isLeaf()) {
            item.setSelected(selected);
        }
        for (TreeItem<String> child : item.getChildren()) {
            if (child instanceof CheckBoxTreeItem) {
                setAllSelected((CheckBoxTreeItem<String>) child, selected);
            }
        }
    }

    private void invertSelection(CheckBoxTreeItem<String> item) {
        if (item.isLeaf()) {
            item.setSelected(!item.isSelected());
        }
        for (TreeItem<String> child : item.getChildren()) {
            if (child instanceof CheckBoxTreeItem) {
                invertSelection((CheckBoxTreeItem<String>) child);
            }
        }
    }
}

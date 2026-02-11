package lotrec.guifx.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BreakPointsDialog extends Dialog<List<String>> {

    private final ListView<String> rulesListView;
    private final ObservableList<String> ruleNames;
    private final List<String> selectedBreakPoints = new ArrayList<>();

    public BreakPointsDialog(Stage owner) {
        setTitle("Break Points");
        initOwner(owner);

        ruleNames = FXCollections.observableArrayList();
        rulesListView = new ListView<>(ruleNames);
        rulesListView.setPrefHeight(300);
        rulesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        VBox content = new VBox(10,
            new Label("Select rules for break points:"),
            rulesListView
        );
        content.setPadding(new Insets(20));

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new ArrayList<>(rulesListView.getSelectionModel().getSelectedItems());
            }
            return null;
        });
    }

    public void setRuleNames(List<String> names) {
        ruleNames.setAll(names);
    }

    public ListView<String> getRulesListView() { return rulesListView; }
}

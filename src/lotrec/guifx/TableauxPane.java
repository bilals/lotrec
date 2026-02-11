package lotrec.guifx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lotrec.guifx.graph.CytoscapeSwingBridge;

public class TableauxPane extends VBox {

    private final ListView<String> premodelsList;
    private final ObservableList<String> premodelsItems;
    private final StackPane graphDisplayArea;
    private final CytoscapeSwingBridge cytoscapeBridge;
    private final Label tableauxCountLabel;
    private final Label elapsedTimeLabel;
    private final Label engineStatusLabel;
    private final CheckBox filterClosedCheckBox;

    public TableauxPane() {
        setSpacing(8);
        setPadding(new Insets(10));

        // Premodels list
        premodelsItems = FXCollections.observableArrayList();
        premodelsList = new ListView<>(premodelsItems);
        premodelsList.setPrefHeight(150);

        // Filter
        filterClosedCheckBox = new CheckBox("Filter closed tableaux");

        // Graph display area with CytoscapeSwingBridge
        cytoscapeBridge = new CytoscapeSwingBridge();
        graphDisplayArea = new StackPane();
        graphDisplayArea.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        graphDisplayArea.setPrefHeight(300);
        graphDisplayArea.getChildren().add(cytoscapeBridge);
        VBox.setVgrow(graphDisplayArea, Priority.ALWAYS);

        // Status bar
        tableauxCountLabel = new Label("Tableaux: 0");
        elapsedTimeLabel = new Label("Time: --");
        engineStatusLabel = new Label("Status: Idle");
        HBox statusBar = new HBox(15, engineStatusLabel, tableauxCountLabel, elapsedTimeLabel);

        getChildren().addAll(
            new Label("Premodels:"), premodelsList, filterClosedCheckBox,
            graphDisplayArea, statusBar
        );
    }

    public void setTableauxCount(int count) {
        tableauxCountLabel.setText("Tableaux: " + count);
    }

    public void setElapsedTime(String time) {
        elapsedTimeLabel.setText("Time: " + time);
    }

    public void setEngineStatus(String status) {
        engineStatusLabel.setText("Status: " + status);
    }

    public void addPremodel(String name) {
        premodelsItems.add(name);
    }

    public void clearPremodels() {
        premodelsItems.clear();
    }

    public ListView<String> getPremodelsList() { return premodelsList; }
    public StackPane getGraphDisplayArea() { return graphDisplayArea; }
    public Label getTableauxCountLabel() { return tableauxCountLabel; }
    public Label getElapsedTimeLabel() { return elapsedTimeLabel; }
    public Label getEngineStatusLabel() { return engineStatusLabel; }
    public CheckBox getFilterClosedCheckBox() { return filterClosedCheckBox; }
    public CytoscapeSwingBridge getCytoscapeBridge() { return cytoscapeBridge; }
}

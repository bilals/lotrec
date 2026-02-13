package lotrec.guifx.logicspane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.guifx.components.ExpandableCommentField;
import lotrec.guifx.dialogs.NewConnectorDialog;

public class ConnTabPane extends VBox {

    private final Logic logic;
    private final ListView<String> connectorList;
    private final ObservableList<String> connectorNames;
    private final TextField nameField;
    private final TextField arityField;
    private final TextField outputFormatField;
    private final TextField priorityField;
    private final ExpandableCommentField commentField;

    public ConnTabPane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        // Connector list
        connectorNames = FXCollections.observableArrayList();
        connectorList = new ListView<>(connectorNames);
        connectorList.setPrefHeight(200);
        VBox.setVgrow(connectorList, Priority.ALWAYS);

        // Detail fields in GridPane layout
        nameField = new TextField();
        nameField.setEditable(false);
        arityField = new TextField();
        arityField.setEditable(false);
        outputFormatField = new TextField();
        outputFormatField.setEditable(false);
        priorityField = new TextField();
        priorityField.setEditable(false);

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);
        detailsGrid.add(new Label("Name:"), 0, 0);
        detailsGrid.add(nameField, 1, 0);
        detailsGrid.add(new Label("Arity:"), 0, 1);
        detailsGrid.add(arityField, 1, 1);
        detailsGrid.add(new Label("Output Format:"), 0, 2);
        detailsGrid.add(outputFormatField, 1, 2);
        detailsGrid.add(new Label("Priority:"), 0, 3);
        detailsGrid.add(priorityField, 1, 3);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(arityField, Priority.ALWAYS);
        GridPane.setHgrow(outputFormatField, Priority.ALWAYS);
        GridPane.setHgrow(priorityField, Priority.ALWAYS);

        // Comment field
        commentField = new ExpandableCommentField();

        // Buttons
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> addConnector());
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> editConnector());
        Button removeBtn = new Button("Remove");
        removeBtn.setOnAction(e -> removeConnector());
        HBox buttonBar = new HBox(5, addBtn, editBtn, removeBtn);

        // Selection listener
        connectorList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showConnectorDetails(newVal)
        );

        getChildren().addAll(connectorList, detailsGrid, commentField, buttonBar);
        refreshList();
    }

    public void refreshList() {
        String previousSelection = connectorList.getSelectionModel().getSelectedItem();
        connectorNames.clear();
        if (logic.getConnectors() != null) {
            for (Object obj : logic.getConnectors()) {
                if (obj instanceof Connector) {
                    connectorNames.add(((Connector) obj).getName());
                }
            }
        }
        // Restore previous selection or auto-select first
        if (previousSelection != null && connectorNames.contains(previousSelection)) {
            connectorList.getSelectionModel().select(previousSelection);
        } else if (!connectorNames.isEmpty()) {
            connectorList.getSelectionModel().selectFirst();
        }
    }

    public ListView<String> getConnectorList() { return connectorList; }
    public Logic getLogic() { return logic; }

    private void showConnectorDetails(String name) {
        if (name == null) {
            nameField.clear();
            arityField.clear();
            outputFormatField.clear();
            priorityField.clear();
            commentField.clear();
            return;
        }
        Connector conn = logic.getConnector(name);
        if (conn != null) {
            nameField.setText(conn.getName());
            arityField.setText(String.valueOf(conn.getArity()));
            outputFormatField.setText(conn.getOutString());
            priorityField.setText(String.valueOf(conn.getPriority()));
            commentField.setText(conn.getComment());
        }
    }

    private void addConnector() {
        NewConnectorDialog dialog = new NewConnectorDialog(
            (javafx.stage.Stage) getScene().getWindow());
        dialog.showAndWait().ifPresent(conn -> {
            if (logic.getConnector(conn.getName()) == null) {
                logic.addConnector(conn);
                refreshList();
                connectorList.getSelectionModel().select(conn.getName());
            }
        });
    }

    private void editConnector() {
        String selected = connectorList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Connector conn = logic.getConnector(selected);
        if (conn == null) return;
        NewConnectorDialog dialog = new NewConnectorDialog(
            (javafx.stage.Stage) getScene().getWindow(), conn);
        dialog.showAndWait().ifPresent(updated -> {
            refreshList();
            connectorList.getSelectionModel().select(updated.getName());
        });
    }

    private void removeConnector() {
        String selected = connectorList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Connector conn = logic.getConnector(selected);
            if (conn != null) {
                logic.removeConnector(conn);
                refreshList();
            }
        }
    }
}

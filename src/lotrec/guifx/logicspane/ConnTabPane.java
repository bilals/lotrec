package lotrec.guifx.logicspane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.guifx.dialogs.NewConnectorDialog;

public class ConnTabPane extends VBox {

    private final Logic logic;
    private final ListView<String> connectorList;
    private final ObservableList<String> connectorNames;
    private final TextField nameField;
    private final TextField arityField;
    private final TextField outputFormatField;
    private final TextField priorityField;

    public ConnTabPane(Logic logic) {
        this.logic = logic;
        setSpacing(10);
        setPadding(new Insets(10));

        // Connector list
        connectorNames = FXCollections.observableArrayList();
        connectorList = new ListView<>(connectorNames);
        connectorList.setPrefHeight(200);
        VBox.setVgrow(connectorList, Priority.ALWAYS);

        // Detail fields
        nameField = new TextField();
        nameField.setPromptText("Name");
        arityField = new TextField();
        arityField.setPromptText("Arity");
        outputFormatField = new TextField();
        outputFormatField.setPromptText("Output Format");
        priorityField = new TextField();
        priorityField.setPromptText("Priority");

        VBox detailsBox = new VBox(5,
            new Label("Name:"), nameField,
            new Label("Arity:"), arityField,
            new Label("Output Format:"), outputFormatField,
            new Label("Priority:"), priorityField
        );

        // Buttons
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> addConnector());
        Button removeBtn = new Button("Remove");
        removeBtn.setOnAction(e -> removeConnector());
        HBox buttonBar = new HBox(5, addBtn, removeBtn);

        // Selection listener
        connectorList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showConnectorDetails(newVal)
        );

        getChildren().addAll(connectorList, detailsBox, buttonBar);
        refreshList();
    }

    public void refreshList() {
        connectorNames.clear();
        if (logic.getConnectors() != null) {
            for (Object obj : logic.getConnectors()) {
                if (obj instanceof Connector) {
                    connectorNames.add(((Connector) obj).getName());
                }
            }
        }
    }

    public ListView<String> getConnectorList() { return connectorList; }
    public Logic getLogic() { return logic; }

    private void showConnectorDetails(String name) {
        if (name == null) return;
        Connector conn = logic.getConnector(name);
        if (conn != null) {
            nameField.setText(conn.getName());
            arityField.setText(String.valueOf(conn.getArity()));
            outputFormatField.setText(conn.getOutString());
            priorityField.setText(String.valueOf(conn.getPriority()));
        }
    }

    private void addConnector() {
        NewConnectorDialog dialog = new NewConnectorDialog(
            (javafx.stage.Stage) getScene().getWindow());
        dialog.showAndWait().ifPresent(conn -> {
            if (logic.getConnector(conn.getName()) == null) {
                logic.addConnector(conn);
                refreshList();
            }
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

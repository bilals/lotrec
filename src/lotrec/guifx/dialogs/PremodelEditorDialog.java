package lotrec.guifx.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PremodelEditorDialog extends Dialog<Void> {

    private final TableView<String> nodesTable;
    private final TableView<String> edgesTable;

    public PremodelEditorDialog(Stage owner) {
        setTitle("Premodel Editor");
        initOwner(owner);

        nodesTable = new TableView<>();
        nodesTable.setPrefHeight(200);
        nodesTable.setPlaceholder(new Label("No nodes"));
        TableColumn<String, String> nodeNameCol = new TableColumn<>("Node");
        TableColumn<String, String> nodeFormulasCol = new TableColumn<>("Formulas");
        nodesTable.getColumns().addAll(nodeNameCol, nodeFormulasCol);

        edgesTable = new TableView<>();
        edgesTable.setPrefHeight(150);
        edgesTable.setPlaceholder(new Label("No edges"));
        TableColumn<String, String> edgeFromCol = new TableColumn<>("From");
        TableColumn<String, String> edgeToCol = new TableColumn<>("To");
        TableColumn<String, String> edgeRelCol = new TableColumn<>("Relation");
        edgesTable.getColumns().addAll(edgeFromCol, edgeToCol, edgeRelCol);

        VBox content = new VBox(10,
            new Label("Nodes:"), nodesTable,
            new Label("Edges:"), edgesTable
        );
        content.setPadding(new Insets(20));

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
    }

    public TableView<String> getNodesTable() { return nodesTable; }
    public TableView<String> getEdgesTable() { return edgesTable; }
}

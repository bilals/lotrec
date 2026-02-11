package lotrec.guifx.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lotrec.PredefinedLogicsLoader;
import java.util.List;

public class PredefinedLogicsDialog extends Dialog<String> {
    private final ListView<String> logicsList;
    private final ObservableList<String> logicNames;

    public PredefinedLogicsDialog(Stage owner) {
        setTitle("Predefined Logics");
        initOwner(owner);

        logicNames = FXCollections.observableArrayList();
        // Load predefined logic names from resources
        String[] predefined = PredefinedLogicsLoader.LOGICS_FILES;
        if (predefined != null) {
            for (String name : predefined) {
                logicNames.add(name);
            }
        }

        logicsList = new ListView<>(logicNames);
        logicsList.setPrefHeight(400);
        logicsList.setPrefWidth(300);

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Select a predefined logic:"), logicsList);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return logicsList.getSelectionModel().getSelectedItem();
            }
            return null;
        });
    }

    public List<String> getLogicNames() {
        return logicNames;
    }
}

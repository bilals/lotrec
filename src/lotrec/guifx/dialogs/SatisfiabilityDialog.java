package lotrec.guifx.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lotrec.engine.Engine;

public class SatisfiabilityDialog extends Dialog<Integer> {

    public SatisfiabilityDialog(Stage owner) {
        setTitle("Satisfiability Check");
        initOwner(owner);
        setResizable(true);

        ToggleGroup group = new ToggleGroup();
        RadioButton stopRadio = new RadioButton("Stop after finding a first open premodel");
        stopRadio.setToggleGroup(group);
        stopRadio.setSelected(true);

        RadioButton pauseRadio = new RadioButton("Pause after each found open premodel");
        pauseRadio.setToggleGroup(group);

        VBox content = new VBox(15, stopRadio, pauseRadio);
        content.setPadding(new Insets(20));

        getDialogPane().setContent(content);

        ButtonType startButton = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(startButton, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == startButton) {
                if (pauseRadio.isSelected()) {
                    return Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU;
                }
                return Engine.STOP_WHEN_HAVING_OPEN_TABLEAU;
            }
            return null;
        });
    }
}

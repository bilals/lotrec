package lotrec.guifx.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RunInfoDialog extends Dialog<Void> {
    private final Label rulesLabel;
    private final Label nodesLabel;
    private final Label timeLabel;
    private long elapsedTime;

    public RunInfoDialog(Stage owner) {
        setTitle("Run Information");
        initOwner(owner);

        rulesLabel = new Label("0");
        nodesLabel = new Label("0");
        timeLabel = new Label("0 ms");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Rules Applied:"), 0, 0);
        grid.add(rulesLabel, 1, 0);
        grid.add(new Label("Nodes Created:"), 0, 1);
        grid.add(nodesLabel, 1, 1);
        grid.add(new Label("Elapsed Time:"), 0, 2);
        grid.add(timeLabel, 1, 2);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    public void setStatistics(int rulesApplied, int nodesCreated, long elapsedMs) {
        this.elapsedTime = elapsedMs;
        rulesLabel.setText(String.valueOf(rulesApplied));
        nodesLabel.setText(String.valueOf(nodesCreated));
        timeLabel.setText(elapsedMs + " ms");
    }

    public long getElapsedTime() { return elapsedTime; }
}

package lotrec.guifx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControlsPane extends VBox {

    private final Button nextStepButton;
    private final Button pauseResumeButton;
    private final Button stopButton;
    private final Label statusLabel;
    private final Label elapsedTimeLabel;
    private final Label appliedRulesLabel;
    private boolean paused;

    public ControlsPane() {
        setSpacing(8);
        setPadding(new Insets(10));

        nextStepButton = new Button("Next Step");
        nextStepButton.setDisable(true);

        pauseResumeButton = new Button("Pause");
        pauseResumeButton.setDisable(true);
        pauseResumeButton.setOnAction(e -> togglePause());

        stopButton = new Button("Stop");
        stopButton.setDisable(true);

        HBox buttonBar = new HBox(5, nextStepButton, pauseResumeButton, stopButton);

        statusLabel = new Label("Idle");
        elapsedTimeLabel = new Label("Time: --");
        appliedRulesLabel = new Label("Rules: --");

        HBox statusBar = new HBox(15, statusLabel, elapsedTimeLabel, appliedRulesLabel);

        getChildren().addAll(new Label("Runtime Controls:"), buttonBar, statusBar);
        paused = false;
    }

    public void enableControls() {
        pauseResumeButton.setDisable(false);
        stopButton.setDisable(false);
    }

    public void enableStepControls() {
        nextStepButton.setDisable(false);
        pauseResumeButton.setDisable(true);
        stopButton.setDisable(true);
    }

    public void disableControls() {
        nextStepButton.setDisable(true);
        pauseResumeButton.setDisable(true);
        stopButton.setDisable(true);
    }

    private void togglePause() {
        if (paused) {
            pauseResumeButton.setText("Pause");
            paused = false;
        } else {
            pauseResumeButton.setText("Resume");
            paused = true;
        }
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setElapsedTime(String time) {
        elapsedTimeLabel.setText("Time: " + time);
    }

    public void setAppliedRules(String count) {
        appliedRulesLabel.setText("Rules: " + count);
    }

    public Button getNextStepButton() { return nextStepButton; }
    public Button getPauseResumeButton() { return pauseResumeButton; }
    public Button getStopButton() { return stopButton; }
    public Label getStatusLabel() { return statusLabel; }
    public Label getElapsedTimeLabel() { return elapsedTimeLabel; }
    public Label getAppliedRulesLabel() { return appliedRulesLabel; }
}

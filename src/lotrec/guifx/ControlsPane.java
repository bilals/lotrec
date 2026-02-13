package lotrec.guifx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlsPane extends HBox {

    private final Button nextStepButton;
    private final Button pauseResumeButton;
    private final Button stopButton;
    private boolean paused;

    public ControlsPane() {
        setSpacing(5);
        setPadding(new Insets(0));

        nextStepButton = new Button("Next Step");
        nextStepButton.setDisable(true);

        pauseResumeButton = new Button("Pause");
        pauseResumeButton.setDisable(true);
        pauseResumeButton.setOnAction(e -> togglePause());

        stopButton = new Button("Stop");
        stopButton.setDisable(true);

        getChildren().addAll(nextStepButton, pauseResumeButton, stopButton);
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

    public Button getNextStepButton() { return nextStepButton; }
    public Button getPauseResumeButton() { return pauseResumeButton; }
    public Button getStopButton() { return stopButton; }
}

package lotrec.engine;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lotrec.guifx.MainFrameFX;
import lotrec.process.EventMachine;

/**
 * JavaFX implementation of EngineListener.
 * All callbacks dispatch on the JavaFX Application Thread via Platform.runLater().
 * Mechanical port of SwingEngineListener with SwingUtilities.invokeLater() replaced.
 */
public class JavaFXEngineListener implements EngineListener {

    private final MainFrameFX mainFrameFX;

    public JavaFXEngineListener(MainFrameFX mainFrameFX) {
        if (mainFrameFX == null) {
            throw new IllegalArgumentException("MainFrameFX cannot be null");
        }
        this.mainFrameFX = mainFrameFX;
    }

    @Override
    public void onBuildStart() {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().enableControls();
                mainFrameFX.getControlsPane().setStatus("Building...");
            }
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().clearPremodels();
            }
        });
    }

    @Override
    public void onBuildEnd(boolean wasStopped) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().disableControls();
                mainFrameFX.getControlsPane().setStatus(wasStopped ? "Stopped" : "Finished");
            }
        });
    }

    @Override
    public void onStatusChanged(EngineStatus status) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().setStatus(status.toString());
            }
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setEngineStatus(status.toString());
            }
        });
    }

    @Override
    public void onTableauxCountChanged(int count) {
        Platform.runLater(() -> {
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setTableauxCount(count);
            }
        });
    }

    @Override
    public void onElapsedTimeChanged(long elapsedMs) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().setElapsedTime(elapsedMs + " ms");
            }
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setElapsedTime(elapsedMs + " ms");
            }
        });
    }

    @Override
    public void onAppliedRulesChanged(int appliedRules) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().setAppliedRules(String.valueOf(appliedRules));
            }
        });
    }

    @Override
    public void onTotalAppliedRulesChanged(int total) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().setAppliedRules(total + " total");
            }
        });
    }

    @Override
    public void onRuleApplied(String ruleName, String tableauName) {
        // No direct UI update needed; status updates handle this
    }

    @Override
    public void onPausedAtRule(String ruleName) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().setStatus("Paused at: " + ruleName);
            }
        });
    }

    @Override
    public void onPause() {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().enableControls();
            }
        });
    }

    @Override
    public void onResume() {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().enableControls();
            }
        });
    }

    @Override
    public void onStepPause(EventMachine ruleEM) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().enableStepControls();
            }
        });
    }

    @Override
    public void onStepResume(EventMachine ruleEM) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().disableControls();
            }
        });
    }

    @Override
    public void onRuntimeError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                "Runtime error during rule application:\n" + message,
                ButtonType.OK);
            alert.initOwner(mainFrameFX.getStage());
            alert.showAndWait();
        });
    }

    @Override
    public void refreshTableauxDisplay() {
        // Will be connected to TableauxPane.fillList() in full integration
    }

    @Override
    public void refreshLastChosenTableaux() {
        // Will be connected to TableauxPane display refresh in full integration
    }

    public MainFrameFX getMainFrameFX() { return mainFrameFX; }
}

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
            }
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setEngineStatus("Building...");
            }
            if (mainFrameFX.getTableauxPane() != null && mainFrameFX.getEngine() != null) {
                mainFrameFX.getTableauxPane().wireSelectionListener(mainFrameFX.getEngine());
                mainFrameFX.getTableauxPane().fillTabListAndDisplayFirst(mainFrameFX.getEngine());
            }
        });
    }

    @Override
    public void onBuildEnd(boolean wasStopped) {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().disableControls();
            }
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setEngineStatus(wasStopped ? "Stopped" : "Finished");
            }
            if (mainFrameFX.getTableauxPane() != null && mainFrameFX.getEngine() != null) {
                mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
            }
        });
    }

    @Override
    public void onStatusChanged(EngineStatus status) {
        Platform.runLater(() -> {
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
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setElapsedTime(elapsedMs + " ms");
            }
        });
    }

    @Override
    public void onAppliedRulesChanged(int appliedRules) {
        Platform.runLater(() -> {
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setAppliedRules(String.valueOf(appliedRules));
            }
        });
    }

    @Override
    public void onTotalAppliedRulesChanged(int total) {
        Platform.runLater(() -> {
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setAppliedRules(total + " total");
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
            if (mainFrameFX.getTableauxPane() != null) {
                mainFrameFX.getTableauxPane().setEngineStatus("Paused at: " + ruleName);
            }
        });
    }

    @Override
    public void onPause() {
        Platform.runLater(() -> {
            if (mainFrameFX.getControlsPane() != null) {
                mainFrameFX.getControlsPane().enableControls();
            }
            if (mainFrameFX.getTableauxPane() != null && mainFrameFX.getEngine() != null) {
                mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
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
            if (mainFrameFX.getTableauxPane() != null && mainFrameFX.getEngine() != null) {
                mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
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
        Platform.runLater(() -> {
            if (mainFrameFX.getTableauxPane() != null && mainFrameFX.getEngine() != null) {
                mainFrameFX.getTableauxPane().fillTabListAndDisplayFirst(mainFrameFX.getEngine());
            }
        });
    }

    @Override
    public void refreshLastChosenTableaux() {
        Platform.runLater(() -> {
            if (mainFrameFX.getTableauxPane() != null && mainFrameFX.getEngine() != null) {
                mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
            }
        });
    }

    public MainFrameFX getMainFrameFX() { return mainFrameFX; }
}

/*
 * SwingEngineListener.java
 *
 * GUI implementation of EngineListener that wraps MainFrame operations.
 */
package lotrec.engine;

import java.awt.Cursor;

import javax.swing.SwingUtilities;

import lotrec.gui.DialogsFactory;
import lotrec.gui.MainFrame;
import lotrec.process.EventMachine;

/**
 * GUI implementation of EngineListener that wraps MainFrame operations.
 * All callbacks use SwingUtilities.invokeLater() for thread safety since
 * the Engine runs on a separate thread.
 *
 * @author LoTREC Team
 */
public class SwingEngineListener implements EngineListener {

    private final MainFrame mainFrame;

    /**
     * Creates a SwingEngineListener wrapping the given MainFrame.
     * @param mainFrame the main application frame
     */
    public SwingEngineListener(MainFrame mainFrame) {
        if (mainFrame == null) {
            throw new IllegalArgumentException("MainFrame cannot be null");
        }
        this.mainFrame = mainFrame;
    }

    @Override
    public void onBuildStart() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().setSelectionModeEnabled(false);
            mainFrame.getTableauxPanel().resetSelectionMode();
            mainFrame.getTableauxPanel().enableControlsButtons();
            mainFrame.getControlsPanel().disableBuildButtons();
            mainFrame.showWaitCursor();
            mainFrame.getTableauxPanel().getControlsPanel()
                    .setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainFrame.getTableauxPanel().fillTabListAndDisplayFirst();
        });
    }

    @Override
    public void onBuildEnd(boolean wasStopped) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().setSelectionModeEnabled(true);
            mainFrame.getTableauxPanel().disableControlsButtons();
            mainFrame.getControlsPanel().enableBuildButtons();
            mainFrame.hideWaitCursor();
            mainFrame.getTableauxPanel().fillTabListAndDisplayLastChosenOnes();
        });
    }

    @Override
    public void onStatusChanged(EngineStatus status) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayEngineStatus(status.toString());
        });
    }

    @Override
    public void onTableauxCountChanged(int count) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayTableauxCount(count);
        });
    }

    @Override
    public void onElapsedTimeChanged(long elapsedMs) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayEngineElapsedTime(elapsedMs + " ms");
        });
    }

    @Override
    public void onAppliedRulesChanged(int appliedRules) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayEngineAppliedRules(String.valueOf(appliedRules));
        });
    }

    @Override
    public void onTotalAppliedRulesChanged(int total) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayEngineTotalAppliedRules(String.valueOf(total));
        });
    }

    @Override
    public void onRuleApplied(String ruleName, String tableauName) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayLastAppliedRule(ruleName, tableauName);
        });
    }

    @Override
    public void onPausedAtRule(String ruleName) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().displayPausedAtRule(ruleName);
        });
    }

    @Override
    public void onPause() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().enableControlsButtons();
            mainFrame.hideWaitCursor();
            mainFrame.getTableauxPanel().fillTabListAndDisplayLastChosenOnes();
            mainFrame.getTableauxPanel().setSelectionModeEnabled(true);
        });
    }

    @Override
    public void onResume() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().enableControlsButtons();
            mainFrame.showWaitCursor();
            mainFrame.getTableauxPanel().getControlsPanel()
                    .setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainFrame.getTableauxPanel().setSelectionModeEnabled(false);
        });
    }

    @Override
    public void onStepPause(EventMachine ruleEM) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().enableStepControlsButtons();
            mainFrame.hideWaitCursor();
            mainFrame.getTableauxPanel().fillTabListAndDisplayLastChosenOnes();
            mainFrame.getTableauxPanel().setSelectionModeEnabled(true);
        });
    }

    @Override
    public void onStepResume(EventMachine ruleEM) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().disableStepControlsButtons();
            mainFrame.showWaitCursor();
            mainFrame.getTableauxPanel().getControlsPanel()
                    .setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            mainFrame.getTableauxPanel().setSelectionModeEnabled(false);
        });
    }

    @Override
    public void onRuntimeError(String message) {
        SwingUtilities.invokeLater(() -> {
            DialogsFactory.runTimeErrorMessage(mainFrame,
                    "The following run-time exception occurred during rules application:\n" + message);
        });
    }

    @Override
    public void refreshTableauxDisplay() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().fillTabListAndDisplayFirst();
        });
    }

    @Override
    public void refreshLastChosenTableaux() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getTableauxPanel().fillTabListAndDisplayLastChosenOnes();
        });
    }

    /**
     * @return the wrapped MainFrame (for backward compatibility)
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }
}

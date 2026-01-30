/*
 * EngineListener.java
 *
 * Observer interface for Engine lifecycle and status callbacks.
 * Allows decoupling Engine from GUI dependencies for headless testing.
 */
package lotrec.engine;

import lotrec.process.EventMachine;

/**
 * Listener interface for Engine events.
 * Implementations handle engine lifecycle, status updates, and UI synchronization.
 *
 * <p>Two main implementations:
 * <ul>
 *   <li>{@link SwingEngineListener} - GUI implementation wrapping MainFrame</li>
 *   <li>{@link HeadlessEngineListener} - No-op implementation for testing</li>
 * </ul>
 *
 * @author LoTREC Team
 */
public interface EngineListener {

    // ========== Lifecycle Events ==========

    /**
     * Called when the engine build process starts.
     * GUI implementations should disable build buttons and show wait cursor.
     */
    void onBuildStart();

    /**
     * Called when the engine build process ends.
     * @param wasStopped true if the engine was stopped by user, false if finished normally
     */
    void onBuildEnd(boolean wasStopped);

    // ========== Status Updates ==========

    /**
     * Called when the engine status changes.
     * @param status the new engine status
     */
    void onStatusChanged(EngineStatus status);

    /**
     * Called when the number of tableaux changes.
     * @param count the current number of tableaux in the wallet
     */
    void onTableauxCountChanged(int count);

    /**
     * Called when elapsed time should be updated.
     * @param elapsedMs the elapsed time in milliseconds
     */
    void onElapsedTimeChanged(long elapsedMs);

    /**
     * Called when the applied rules count changes.
     * @param appliedRules the number of rules applied in current tableau
     */
    void onAppliedRulesChanged(int appliedRules);

    /**
     * Called when the total applied rules count changes.
     * @param total the total number of rules applied across all tableaux
     */
    void onTotalAppliedRulesChanged(int total);

    /**
     * Called when a rule is applied.
     * @param ruleName the name of the rule that was applied
     * @param tableauName the name of the tableau the rule was applied to
     */
    void onRuleApplied(String ruleName, String tableauName);

    /**
     * Called when the engine pauses at a specific rule.
     * @param ruleName the name of the rule where engine paused
     */
    void onPausedAtRule(String ruleName);

    // ========== Pause/Resume Events ==========

    /**
     * Called when the engine enters pause state.
     * GUI implementations should enable controls and hide wait cursor.
     */
    void onPause();

    /**
     * Called when the engine resumes from pause.
     * GUI implementations should show wait cursor and disable selection.
     */
    void onResume();

    /**
     * Called when the engine pauses after completing a step.
     * @param ruleEM the EventMachine for the rule that just completed
     */
    void onStepPause(EventMachine ruleEM);

    /**
     * Called when the engine resumes to the next step.
     * @param ruleEM the EventMachine for the rule resuming
     */
    void onStepResume(EventMachine ruleEM);

    // ========== Error Handling ==========

    /**
     * Called when a runtime error occurs during rule application.
     * GUI implementations should show an error dialog.
     * @param message the error message
     */
    void onRuntimeError(String message);

    // ========== Display Updates ==========

    /**
     * Called to refresh the tableaux list display.
     * GUI implementations should update the tableaux panel.
     */
    default void refreshTableauxDisplay() {}

    /**
     * Called to refresh and display the last chosen tableaux.
     * GUI implementations should update the display with current selections.
     */
    default void refreshLastChosenTableaux() {}
}

/*
 * HeadlessEngineListener.java
 *
 * No-op implementation of EngineListener for headless testing.
 */
package lotrec.engine;

import java.util.ArrayList;
import java.util.List;

import lotrec.process.EventMachine;

/**
 * No-op implementation of EngineListener for headless testing.
 * Optionally records events for test assertions.
 *
 * <p>Example usage:
 * <pre>
 * HeadlessEngineListener listener = new HeadlessEngineListener(true);
 * Engine engine = new Engine(logic, strategy, formula, listener);
 * engine.buildTableaux();
 * engine.run();
 *
 * assertThat(listener.getLastAppliedRules()).isGreaterThan(0);
 * assertThat(listener.getLastStatus()).isEqualTo(EngineStatus.FINISHED);
 * </pre>
 *
 * @author LoTREC Team
 */
public class HeadlessEngineListener implements EngineListener {

    private final boolean recordEvents;
    private final List<String> eventLog;

    private EngineStatus lastStatus;
    private int lastTableauxCount;
    private long lastElapsedMs;
    private int lastAppliedRules;
    private int lastTotalAppliedRules;
    private String lastRuleName;
    private String lastTableauName;
    private String lastPausedAtRule;
    private String lastError;
    private boolean buildStarted;
    private boolean buildEnded;
    private boolean wasStopped;

    /**
     * Creates a headless listener that does not record events.
     */
    public HeadlessEngineListener() {
        this(false);
    }

    /**
     * Creates a headless listener with optional event recording.
     * @param recordEvents if true, events are logged for later inspection
     */
    public HeadlessEngineListener(boolean recordEvents) {
        this.recordEvents = recordEvents;
        this.eventLog = recordEvents ? new ArrayList<>() : null;
    }

    @Override
    public void onBuildStart() {
        buildStarted = true;
        if (recordEvents) {
            eventLog.add("BUILD_START");
        }
    }

    @Override
    public void onBuildEnd(boolean wasStopped) {
        buildEnded = true;
        this.wasStopped = wasStopped;
        if (recordEvents) {
            eventLog.add("BUILD_END:" + (wasStopped ? "STOPPED" : "FINISHED"));
        }
    }

    @Override
    public void onStatusChanged(EngineStatus status) {
        this.lastStatus = status;
        if (recordEvents) {
            eventLog.add("STATUS:" + status);
        }
    }

    @Override
    public void onTableauxCountChanged(int count) {
        this.lastTableauxCount = count;
        if (recordEvents) {
            eventLog.add("TABLEAUX_COUNT:" + count);
        }
    }

    @Override
    public void onElapsedTimeChanged(long elapsedMs) {
        this.lastElapsedMs = elapsedMs;
        if (recordEvents) {
            eventLog.add("ELAPSED:" + elapsedMs);
        }
    }

    @Override
    public void onAppliedRulesChanged(int appliedRules) {
        this.lastAppliedRules = appliedRules;
        if (recordEvents) {
            eventLog.add("APPLIED_RULES:" + appliedRules);
        }
    }

    @Override
    public void onTotalAppliedRulesChanged(int total) {
        this.lastTotalAppliedRules = total;
        if (recordEvents) {
            eventLog.add("TOTAL_APPLIED_RULES:" + total);
        }
    }

    @Override
    public void onRuleApplied(String ruleName, String tableauName) {
        this.lastRuleName = ruleName;
        this.lastTableauName = tableauName;
        if (recordEvents) {
            eventLog.add("RULE_APPLIED:" + ruleName + "@" + tableauName);
        }
    }

    @Override
    public void onPausedAtRule(String ruleName) {
        this.lastPausedAtRule = ruleName;
        if (recordEvents) {
            eventLog.add("PAUSED_AT:" + ruleName);
        }
    }

    @Override
    public void onPause() {
        if (recordEvents) {
            eventLog.add("PAUSE");
        }
    }

    @Override
    public void onResume() {
        if (recordEvents) {
            eventLog.add("RESUME");
        }
    }

    @Override
    public void onStepPause(EventMachine ruleEM) {
        if (recordEvents) {
            eventLog.add("STEP_PAUSE:" + ruleEM.getWorkerName());
        }
    }

    @Override
    public void onStepResume(EventMachine ruleEM) {
        if (recordEvents) {
            eventLog.add("STEP_RESUME:" + ruleEM.getWorkerName());
        }
    }

    @Override
    public void onRuntimeError(String message) {
        this.lastError = message;
        if (recordEvents) {
            eventLog.add("ERROR:" + message);
        }
    }

    // ========== Getters for Test Assertions ==========

    /**
     * @return the event log if recording is enabled, null otherwise
     */
    public List<String> getEventLog() {
        return eventLog;
    }

    /**
     * @return the last reported engine status
     */
    public EngineStatus getLastStatus() {
        return lastStatus;
    }

    /**
     * @return the last reported tableaux count
     */
    public int getLastTableauxCount() {
        return lastTableauxCount;
    }

    /**
     * @return the last reported elapsed time in milliseconds
     */
    public long getLastElapsedMs() {
        return lastElapsedMs;
    }

    /**
     * @return the last reported applied rules count
     */
    public int getLastAppliedRules() {
        return lastAppliedRules;
    }

    /**
     * @return the last reported total applied rules count
     */
    public int getLastTotalAppliedRules() {
        return lastTotalAppliedRules;
    }

    /**
     * @return the name of the last applied rule
     */
    public String getLastRuleName() {
        return lastRuleName;
    }

    /**
     * @return the name of the tableau where the last rule was applied
     */
    public String getLastTableauName() {
        return lastTableauName;
    }

    /**
     * @return the name of the rule where engine last paused
     */
    public String getLastPausedAtRule() {
        return lastPausedAtRule;
    }

    /**
     * @return the last error message, or null if no error occurred
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * @return true if onBuildStart was called
     */
    public boolean isBuildStarted() {
        return buildStarted;
    }

    /**
     * @return true if onBuildEnd was called
     */
    public boolean isBuildEnded() {
        return buildEnded;
    }

    /**
     * @return true if the engine was stopped, false if finished normally
     */
    public boolean wasStopped() {
        return wasStopped;
    }

    /**
     * Resets all recorded state for reuse in multiple tests.
     */
    public void reset() {
        if (eventLog != null) {
            eventLog.clear();
        }
        lastStatus = null;
        lastTableauxCount = 0;
        lastElapsedMs = 0;
        lastAppliedRules = 0;
        lastTotalAppliedRules = 0;
        lastRuleName = null;
        lastTableauName = null;
        lastPausedAtRule = null;
        lastError = null;
        buildStarted = false;
        buildEnded = false;
        wasStopped = false;
    }
}

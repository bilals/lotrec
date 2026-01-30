package lotrec.engine;

import java.util.ArrayList;
import java.util.List;

import lotrec.dataStructure.tableau.Tableau;
import lotrec.process.Strategy;

/**
 * Minimal Engine implementation for unit testing actions that need engine interaction.
 * Captures calls to stopTableau(), add(), remove() for test verification.
 *
 * <p>This class provides a lightweight Engine for testing without requiring
 * full Logic/Strategy configuration. It records engine interactions so tests
 * can verify that actions correctly call engine methods.
 *
 * <p>Example usage:
 * <pre>
 * TestableEngine engine = TestableEngine.create();
 * TestableEventMachine em = TestableEventMachine.configured(tableau, engine);
 *
 * // Execute action that stops tableau
 * StopStrategyAction action = new StopStrategyAction(nodeScheme);
 * action.apply(em, instanceSet);
 *
 * // Verify engine was called
 * assertThat(engine.getStoppedTableaux()).contains(tableau);
 * </pre>
 */
public class TestableEngine extends Engine {

    private final List<Tableau> stoppedTableaux = new ArrayList<>();
    private final List<Strategy> addedStrategies = new ArrayList<>();
    private final List<Strategy> removedStrategies = new ArrayList<>();
    private int appliedRulesCount = 0;
    private int totalAppliedRulesCount = 0;

    /**
     * Creates a TestableEngine with a no-op listener.
     */
    public TestableEngine() {
        super();
        // Initialize with a headless listener to prevent NPEs
    }

    // ========== Factory Methods ==========

    /**
     * Creates a basic TestableEngine for action testing.
     * @return new TestableEngine
     */
    public static TestableEngine create() {
        return new TestableEngine();
    }

    /**
     * Creates a TestableEngine with event recording enabled.
     * @return new TestableEngine with HeadlessEngineListener(true)
     */
    public static TestableEngine withRecording() {
        TestableEngine engine = new TestableEngine();
        // Configure additional recording if needed
        return engine;
    }

    // ========== Override Engine Methods to Capture Calls ==========

    /**
     * Captures stopTableau calls for test verification.
     */
    @Override
    public void stopTableau(Tableau tableau) {
        stoppedTableaux.add(tableau);
        // Don't call super to avoid actual stopping behavior in tests
        tableau.setShouldStopStrategy(true);
    }

    /**
     * Captures strategy add calls for test verification.
     */
    @Override
    public void add(Strategy strategy) {
        addedStrategies.add(strategy);
        super.add(strategy);
    }

    /**
     * Captures strategy remove calls for test verification.
     */
    @Override
    public void remove(Strategy strategy) {
        removedStrategies.add(strategy);
        super.remove(strategy);
    }

    /**
     * Captures applied rules increment.
     */
    @Override
    public void increaseAppliedRules() {
        appliedRulesCount++;
        // Don't call super to avoid listener issues in unit tests
    }

    /**
     * Override to prevent listener null issues in tests.
     */
    @Override
    public void updateTableauxCount() {
        // No-op in tests
    }

    /**
     * Override to prevent listener null issues in tests.
     */
    @Override
    public void updateEngineStatus() {
        // No-op in tests
    }

    /**
     * Override to prevent listener null issues in tests.
     */
    @Override
    public void updateElapsedTime() {
        // No-op in tests
    }

    /**
     * Override to prevent listener null issues in tests.
     */
    @Override
    public void updateAppliedRules() {
        // No-op in tests
    }

    /**
     * Override to prevent listener null issues in tests.
     */
    @Override
    public void updateTotalAppliedRules() {
        // No-op in tests
    }

    /**
     * Override to prevent listener null issues in tests.
     */
    @Override
    public void updateLastAppliedRule(String ruleName, String onTableauName) {
        // No-op in tests
    }

    // ========== Test Verification Methods ==========

    /**
     * Returns list of tableaux that had stopTableau() called on them.
     * @return list of stopped tableaux
     */
    public List<Tableau> getStoppedTableaux() {
        return new ArrayList<>(stoppedTableaux);
    }

    /**
     * Checks if a specific tableau was stopped.
     * @param tableau the tableau to check
     * @return true if stopTableau was called with this tableau
     */
    public boolean wasTableauStopped(Tableau tableau) {
        return stoppedTableaux.contains(tableau);
    }

    /**
     * Returns list of strategies that had add() called with them.
     * @return list of added strategies
     */
    public List<Strategy> getAddedStrategies() {
        return new ArrayList<>(addedStrategies);
    }

    /**
     * Returns list of strategies that had remove() called with them.
     * @return list of removed strategies
     */
    public List<Strategy> getRemovedStrategies() {
        return new ArrayList<>(removedStrategies);
    }

    /**
     * Returns count of times increaseAppliedRules() was called.
     * @return applied rules count
     */
    public int getCapturedAppliedRulesCount() {
        return appliedRulesCount;
    }

    /**
     * Returns count of times setTotalAppliedRules() would have incremented.
     * @return total applied rules count
     */
    public int getCapturedTotalAppliedRulesCount() {
        return totalAppliedRulesCount;
    }

    // ========== Reset for Test Reuse ==========

    /**
     * Resets all captured state for reuse in multiple tests.
     */
    public void reset() {
        stoppedTableaux.clear();
        addedStrategies.clear();
        removedStrategies.clear();
        appliedRulesCount = 0;
        totalAppliedRulesCount = 0;
    }

    /**
     * Returns the number of stop operations recorded.
     * @return count of stopTableau calls
     */
    public int getStopCount() {
        return stoppedTableaux.size();
    }

    /**
     * Checks if any tableau was stopped.
     * @return true if any stopTableau call was made
     */
    public boolean hasStoppedAnyTableau() {
        return !stoppedTableaux.isEmpty();
    }
}

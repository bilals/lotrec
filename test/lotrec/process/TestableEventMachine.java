package lotrec.process;

import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;

/**
 * Factory for creating EventMachine instances configured for unit testing.
 * Provides helper methods to set up an EventMachine without full rule/strategy setup.
 *
 * <p>This class creates and configures EventMachine instances to allow tests to:
 * <ul>
 *   <li>Execute actions via their apply() method with a proper EventMachine context</li>
 *   <li>Test conditions via their restriction chain with proper InstanceSet handling</li>
 *   <li>Control applyOnOneOccurrence behavior for condition matching tests</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * Tableau tableau = TableauTestFixtures.createTableau("test");
 * EventMachine em = TestableEventMachine.forTableau(tableau);
 *
 * // Execute an action
 * AddExpressionAction action = new AddExpressionAction(nodeScheme, exprScheme);
 * action.apply(em, instanceSet);
 * </pre>
 */
public final class TestableEventMachine {

    private TestableEventMachine() {
        // Factory class - prevent instantiation
    }

    // ========== Factory Methods ==========

    /**
     * Creates an EventMachine configured for the given tableau.
     * This is the most common factory method for action execution tests.
     *
     * @param tableau the tableau to associate with this event machine
     * @return configured EventMachine
     */
    public static EventMachine forTableau(Tableau tableau) {
        EventMachine em = new EventMachine("TestRule");
        em.setRelatedTableau(tableau);
        return em;
    }

    /**
     * Creates an EventMachine configured with a tableau and engine.
     * Use this when testing actions that need engine interaction (stop, duplicate).
     *
     * @param tableau the tableau to associate with this event machine
     * @param engine the engine to associate with this event machine
     * @return configured EventMachine
     */
    public static EventMachine configured(Tableau tableau, Engine engine) {
        EventMachine em = new EventMachine("TestRule");
        em.setRelatedTableau(tableau);
        em.setEngine(engine);
        return em;
    }

    /**
     * Creates an EventMachine configured for condition matching tests.
     * Sets up applyOnOneOccurrence based on test requirements.
     *
     * @param tableau the tableau to associate
     * @param applyOnOneOccurrence if true, conditions stop after first match
     * @return configured EventMachine
     */
    public static EventMachine forConditionTesting(Tableau tableau, boolean applyOnOneOccurrence) {
        EventMachine em = forTableau(tableau);
        em.setApplyOnOneOccurence(applyOnOneOccurrence);
        return em;
    }

    /**
     * Convenience method for quick setup in tests.
     * @param ruleName the rule name
     * @param tableau the tableau
     * @return configured EventMachine
     */
    public static EventMachine quickSetup(String ruleName, Tableau tableau) {
        EventMachine em = new EventMachine(ruleName);
        em.setRelatedTableau(tableau);
        return em;
    }

    /**
     * Creates an ActionStocking for use with condition testing.
     * @param em the EventMachine to configure the stocking for
     * @param tableau the tableau to associate
     * @return new ActionStocking configured for the event machine
     */
    public static ActionStocking createActionStocking(EventMachine em, Tableau tableau) {
        ActionStocking stocking = new ActionStocking();
        stocking.setCallerEM(em);
        stocking.setCurrentTableau(tableau);
        return stocking;
    }

    /**
     * Creates an ActionContainer for use with condition restriction chains.
     * @return new empty ActionContainer
     */
    public static ActionContainer createActionContainer() {
        return new ActionContainer();
    }
}

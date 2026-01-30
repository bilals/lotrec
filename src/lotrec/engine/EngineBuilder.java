/*
 * EngineBuilder.java
 *
 * Builder for convenient Engine setup, especially for testing.
 */
package lotrec.engine;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.process.Strategy;

/**
 * Builder for convenient Engine construction.
 * Simplifies test setup by providing sensible defaults.
 *
 * <p>Example usage:
 * <pre>
 * Engine engine = EngineBuilder.forLogic(logic)
 *     .withFormula(formula)
 *     .stopOnOpenTableau()
 *     .build();
 *
 * engine.buildTableaux();
 * engine.run();
 * </pre>
 *
 * @author LoTREC Team
 */
public class EngineBuilder {

    private Logic logic;
    private Strategy strategy;
    private MarkedExpression formula;
    private EngineListener listener;
    private int openTableauAction = Engine.NOP_WHEN_HAVING_OPEN_TABLEAU;
    private boolean runningBySteps = false;

    private EngineBuilder() {
    }

    /**
     * Creates a builder for the given logic.
     * Uses the logic's main strategy by default.
     *
     * @param logic the logic to use
     * @return a new EngineBuilder
     */
    public static EngineBuilder forLogic(Logic logic) {
        EngineBuilder builder = new EngineBuilder();
        builder.logic = logic;
        builder.strategy = logic.getStrategy(logic.getMainStrategyName());
        builder.listener = new HeadlessEngineListener();
        return builder;
    }

    /**
     * Sets the strategy to use (overrides the logic's main strategy).
     *
     * @param strategy the strategy
     * @return this builder
     */
    public EngineBuilder withStrategy(Strategy strategy) {
        this.strategy = strategy;
        return this;
    }

    /**
     * Sets the strategy by name from the logic.
     *
     * @param strategyName the name of the strategy
     * @return this builder
     */
    public EngineBuilder withStrategy(String strategyName) {
        this.strategy = logic.getStrategy(strategyName);
        return this;
    }

    /**
     * Sets the formula to analyze.
     *
     * @param formula the formula
     * @return this builder
     */
    public EngineBuilder withFormula(MarkedExpression formula) {
        this.formula = formula;
        return this;
    }

    /**
     * Sets the listener for engine events.
     *
     * @param listener the listener
     * @return this builder
     */
    public EngineBuilder withListener(EngineListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Uses a HeadlessEngineListener with event recording enabled.
     * The listener can be retrieved via {@link Engine#getListener()}.
     *
     * @return this builder
     */
    public EngineBuilder withRecordingListener() {
        this.listener = new HeadlessEngineListener(true);
        return this;
    }

    /**
     * Configures the engine to stop when finding an open (satisfiable) tableau.
     *
     * @return this builder
     */
    public EngineBuilder stopOnOpenTableau() {
        this.openTableauAction = Engine.STOP_WHEN_HAVING_OPEN_TABLEAU;
        return this;
    }

    /**
     * Configures the engine to do nothing special when finding an open tableau.
     *
     * @return this builder
     */
    public EngineBuilder continueOnOpenTableau() {
        this.openTableauAction = Engine.NOP_WHEN_HAVING_OPEN_TABLEAU;
        return this;
    }

    /**
     * Configures the engine to pause when finding an open tableau.
     * Only effective in GUI mode.
     *
     * @return this builder
     */
    public EngineBuilder pauseOnOpenTableau() {
        this.openTableauAction = Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU;
        return this;
    }

    /**
     * Configures the engine to run in step-by-step mode.
     * Only effective in GUI mode.
     *
     * @return this builder
     */
    public EngineBuilder runBySteps() {
        this.runningBySteps = true;
        return this;
    }

    /**
     * Builds the Engine with the configured settings.
     *
     * @return a new Engine instance
     * @throws IllegalStateException if logic is not set
     */
    public Engine build() {
        if (logic == null) {
            throw new IllegalStateException("Logic must be set");
        }
        if (strategy == null) {
            strategy = logic.getStrategy(logic.getMainStrategyName());
        }
        if (listener == null) {
            listener = new HeadlessEngineListener();
        }

        Engine engine = new Engine(logic, strategy, formula, listener);
        engine.setOpenTableauAction(openTableauAction);
        engine.setRunningBySteps(runningBySteps);
        return engine;
    }

    /**
     * Builds the Engine and initializes tableaux (calls buildTableaux()).
     *
     * @return a new Engine instance with tableaux built
     */
    public Engine buildAndInit() {
        Engine engine = build();
        engine.buildTableaux();
        return engine;
    }
}

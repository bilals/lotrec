package lotrec.dataStructure.graph;

import lotrec.util.Duplicator;
import lotrec.util.Duplicateable;
import lotrec.util.DuplicateException;
import lotrec.process.Strategy;

/**
Extends the graph to contain the strategy. So, nodes and other objects that contains reference to the graph can access the strategy managing it.
@author David Fauthoux
 */
public class ExtendedGraph extends Graph {

//    private Engine engine; // to get the global strategy object in classes such as DuplicateAction & KillAction
    private Strategy strategy; // to get the direct applied duplicate-copy-strategy on this duplicate-copy-tableau
    private boolean shouldStopStrategy=false;

    /**
    Creates a managed graph.
     */
    public ExtendedGraph() {
        super();
//        engine = null;
        strategy = null;
    }

    /**
    Creates a managed graph.
    @param name the name of the graph
     */
    public ExtendedGraph(String name) {
        super(name);
//        engine = null;
        strategy = null;
    }

//    public void setEngine(Engine engine) {
//        this.engine = engine;
//    }
//
//    public Engine getEngine() {
//        return engine;
//    }

    /**
    Sets the strategy that manages this graph. It will be duplicated with the graph.
    @param strategy the strategy managing this graph
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
    Returns the strategy managing this graph
    @return the strategy managing this graph
     */
    public Strategy getStrategy() {
        return strategy;
    }

    // duplication
    /**
    Creates an extended graph with the toDuplicate's strategy.
    <p><b>During the duplication process, the strategy will be duplicated.</b>
    @param toDuplicate the graph to duplicate
     */
    public ExtendedGraph(ExtendedGraph toDuplicate) {
        super(toDuplicate);
//        engine = toDuplicate.engine;
        strategy = toDuplicate.strategy;
        shouldStopStrategy=toDuplicate.shouldStopStrategy;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
        strategy = (Strategy) strategy.duplicate(duplicator);
        strategy.completeDuplication(duplicator);
//        engine = (Engine) ((Duplicateable) engine).duplicate(duplicator);
//        ((CompleteDuplicateable) engine).completeDuplication(duplicator);
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        strategy.translateDuplication(duplicator);
//        ((CompleteDuplicateable) engine).translateDuplication(duplicator);
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new ExtendedGraph(this);
        duplicator.setImage(this, d);
        return d;
    }

    public boolean shouldStopStrategy() {
        return shouldStopStrategy;
    }

    public void setShouldStopStrategy(boolean shouldStopStrategy) {
        this.shouldStopStrategy = shouldStopStrategy;
    }
}

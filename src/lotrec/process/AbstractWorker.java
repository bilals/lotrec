/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.process;

import java.io.Serializable;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;

/**
 *
 * @author said
 */
public abstract class AbstractWorker implements Duplicateable, Serializable {

    private String workerName;
    protected boolean worked = false;
    //every worker knows well the engine that controls it, 
    //and can easily talk to it
    private Engine engine;
    private Tableau relatedTableau;

    public AbstractWorker() {

    }

    public AbstractWorker(AbstractWorker toDuplicate) {
        workerName = toDuplicate.workerName;
        worked = toDuplicate.worked;
        //No duplication for the engine!!!
        engine = toDuplicate.engine;
        relatedTableau = toDuplicate.relatedTableau;
    }

    public abstract void work();

    public abstract boolean isQuiet();

    @Override
    public abstract Duplicateable duplicate(Duplicator duplicator);

    @Override
    public abstract void completeDuplication(Duplicator duplicator) throws ClassCastException;

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException{
        relatedTableau = (Tableau) duplicator.getImage(relatedTableau);
    }

    /**
     * Deprecated and not used any more..
     * 
     * <p>A Routine or a Strategy never wants to pass the deal ! 
     * An external user of it (usually another containing Routine)
     * stops its work when it answers 'yes' to its 'isQuiet' -method- question.
     * <p>An EventMachine (i.e. a Rule) always passes the deal. 
     * An EventMachine is not egoistic :
     * it has not priority in front of other workers.
     * Its work method defines the one step working specification of
     * the worker interface as a complete work on the event queue 
     * (emptying this queue), so no need to keep the deal !
     * <p>@return true if it's an instance of EventMachine, false otherwise.
     */
    @Deprecated
    public boolean passTheDeal() {
        if (this instanceof EventMachine) {
            return true;
        } else {
            return false;
        }
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    @Override
    public String toString() {
        return getWorkerName();
    }

    public boolean hasWorked() {
        return worked;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Tableau getRelatedTableau() {
        return relatedTableau;
    }

    public void setRelatedTableau(Tableau relatedTableau) {
        this.relatedTableau = relatedTableau;
    }
}

package lotrec.process;

import java.util.Vector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.util.CompleteDuplicateable;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;

/**
Contains and manages execution of many workers.
@author David Fauthoux
 */
public abstract class Routine extends AbstractWorker {

    protected Vector<AbstractWorker> workers;

    public Routine() {
        workers = new Vector();
    }

    public Routine(Routine toDuplicate) {
        super(toDuplicate);
        workers = (Vector) toDuplicate.workers.clone();
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        for (int j = 0; j < workers.size(); j++) {
            AbstractWorker w = (AbstractWorker) ((Duplicateable) workers.get(j)).duplicate(duplicator);
            workers.setElementAt(w, j);
            ((CompleteDuplicateable) w).completeDuplication(duplicator);
        }
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        for (int j = 0; j < workers.size(); j++) {
            ((CompleteDuplicateable) workers.get(j)).translateDuplication(duplicator);
        }
    }

    /**
     * Adds the specified worker to this keep in order to execute it, according to this keep law
     * @param worker the worker to add
     * @param constraint the constraint used to know how to add the worker
     */
    public void add(AbstractWorker worker, Object constraint) {
        if (constraint == null) {
            workers.add(worker);
        } else {
            if (!(constraint instanceof Integer)) {
                throw new IllegalArgumentException("The constraint to add a worker to a fair keep must be Integer greater than 0");
            }
            int position = ((Integer) constraint).intValue();
            if (position < 0) {
                throw new IllegalArgumentException("Fair keep only accepts location greater than 0");
            }
            if (position >= workers.size()) {
                workers.setSize(position + 1);
            }
            workers.set(position, worker);
        }
    }

    public boolean isCallingRule(String ruleName) {
        for (AbstractWorker worker : workers) {
            if (worker.getWorkerName().equals(ruleName)) {
                return true;
            } else {
                if (worker instanceof Routine) {
                    if (((Routine) worker).isCallingRule(ruleName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCallingStrategy(Strategy str) {
        for (AbstractWorker worker : workers) {
            if (worker.getWorkerName().equals(str.getWorkerName())) {
                return true;
            } else {
                if (worker instanceof Routine) {
                    if (((Routine) worker).isCallingStrategy(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void replaceRuleCalls(String oldRuleName, Rule newRule) {
        for (int i = 0; i < workers.size(); i++) {
            AbstractWorker worker = workers.get(i);
            //when found the oldRuleName's EventMachine worker, 
            //replace it by newRule.createEventMachine
            if ((worker instanceof EventMachine) &&
                    worker.getWorkerName().equals(oldRuleName)) {
                workers.setElementAt(newRule.createMachine(), workers.indexOf(worker));
            } else {
                //else, worker would be an instance of Routine
                //so it may contains the oldRuleName's EventMachine worker...
                //then, try to replace it..
                if (worker instanceof Routine) {
                    ((Routine) worker).replaceRuleCalls(oldRuleName, newRule);
                }
            }
        }
    }

    public void replaceStrategyCalls(Strategy oldStr, Strategy newStr) {
        for (int i = 0; i < workers.size(); i++) {
            AbstractWorker worker = workers.get(i);
            //when found the oldStr worker, replace it by newStr
            //and don't look for 'it' inside 'it'...
            if ((worker instanceof Strategy) &&
                    worker.getWorkerName().equals(oldStr.getWorkerName())) {
                workers.setElementAt(newStr, workers.indexOf(worker));
            } else {
                //else, worker seems to be a diffrent Routine
                //so it may contains the oldStr worker...
                //then, try to replace it..
                if (worker instanceof Routine) {
                    ((Routine) worker).replaceStrategyCalls(oldStr, newStr);
                }
            }
        }
    }

    /**
     */
    public void remove(AbstractWorker worker) {
        workers.remove(worker);
    }

    /**
     * Returns the contained workers.
     * @return the contained workers.
     */
    public Vector<AbstractWorker> getWorkers() {
        return workers;
    }

    public void setSelfAndWorkersRelatedTableau(Tableau relatedTableau) {
        this.setRelatedTableau(relatedTableau);
        for (AbstractWorker w : workers) {
            if (w instanceof Routine) {
                ((Routine) w).setSelfAndWorkersRelatedTableau(relatedTableau);
            } else if (w instanceof EventMachine) {
                w.setRelatedTableau(relatedTableau);
            }
        }
    }

    @Override
    public String toString() {
        return "(" + super.toString() + " " + workers + ")";
    }
}

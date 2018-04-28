package lotrec.process;

import java.util.Enumeration;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;

/**
 * Keep with the <i>dynamic</i> law :
 * The contained workers are ordered according to a priority value. The workers with the highest priority are placed in this keep in the lowest location.
 * <p>The keep choose the worker at the lowest location which is not quiet.
 * <p><i>One step</i> work for this keep fits with the <i>one step</i> work of the first found worker, in the keep order of workers.
 * <p>Example : if the fair keep contains workers 1, 2 and 3.
 * <p>The worker 1 needs two calls to work to be quiet, the worker 2 one, the worker 3 two.
 * <p>The keep execution will call the <i>work</i> method on (in this order) :
 * <p>1, then 1, then 2, then 3, then 3 and then will be quiet.
 * <p>The keep is not quiet until all its workers are quiet.
 * <p>
 * <p>Note : this keep doesn't take care about the deal passing (<code>passTheDeal</code> method).
 */
public class FirstRule extends Routine {

    /**
     * Creates an empty dynamic keep, ready to contains and execute workers, according to its law.
     */
    public FirstRule() {
        super();
        this.setWorkerName("FirstRule");
    }

    public void work() {
        worked = false;
        if (workers.isEmpty()) {
            return;
        }
        for (AbstractWorker w : workers) {            
            // test if to STOP
            synchronized (getEngine()) {
                if (getEngine().shouldStop()) {
                    return;
                }
            }
            if (!w.isQuiet()) {
                if (getRelatedTableau().shouldStopStrategy()) {
                    return;
                }                
                w.work();
                if (w.hasWorked()) {
                    worked = true;
                    return;
                }
            }
        }
    }

    public boolean isQuiet() {
        if (workers.isEmpty()) {
            return true;
        }

        for (Enumeration enumr = workers.elements(); enumr.hasMoreElements();) {
            AbstractWorker w = (AbstractWorker) enumr.nextElement();
            if (!w.isQuiet()) {
                return false;
            }
        }
        return true;
    }

    //duplication
    /**
     * Creates a dynamic keep with the toDuplicate's workers.
     * <p><b>The duplication process will duplicate and translate the workers.</b>
     */
    public FirstRule(FirstRule toDuplicate) {
        super(toDuplicate);
    // to duplicate other fields...
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new FirstRule(this);
        duplicator.setImage(this, d);
        return d;
    }
}


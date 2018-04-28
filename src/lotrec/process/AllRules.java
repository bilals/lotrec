package lotrec.process;

import java.util.Enumeration;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;

/**
 * Keep with the <i>Star</i> law :
 *
 * <p>Note : this keep doesn't take care about the deal passing
 * (<code>passTheDeal</code> method).
 */
public class AllRules extends Routine {

    /**
     * Creates an empty Star keep, ready to contains and execute workers, according to its law.
     */
    public AllRules() {
        super();
        this.setWorkerName("AllRules");
    }

    @Override
    public void work() {
        worked = false;
        if (workers.isEmpty()) {
            return;
        }
        for (AbstractWorker w : workers) {
            // test for STOP
            synchronized (getEngine()) {
                if (getEngine().shouldStop()) {
                    return;
                }
            }
            if (!w.isQuiet()) {
                w.work();
                if (w.hasWorked()) {
                    worked = true;
                }
                if (getRelatedTableau().shouldStopStrategy()) {
                    return;
                }
            }         
        }
    }

    @Override
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
     * Creates a Star keep with the toDuplicate's workers.
     * <p><b>The duplication process will duplicate and translate the workers.</b>
     */
    public AllRules(AllRules toDuplicate) {
        super(toDuplicate);
    // other fields to duplicate..
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new AllRules(this);
        duplicator.setImage(this, d);
        return d;
    }
}


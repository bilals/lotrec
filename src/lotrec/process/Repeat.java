package lotrec.process;

/* ADDED/MODIFIED 00/12/10 */
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;

/**
 * Keep with the <i>fair</i> law :
 * all the contained workers will be <i>one step</i> worked before a worker can be worked again
 * (<i>worked</i> meaning a call to its <i>work</i> method).
 * <p>Example : if the fair keep contains workers 1, 2 and 3.
 * <p>The worker 1 needs two calls to work to be quiet, the worker 2 one, the worker 3 two.
 * <p>Note : In this exemple, the three contained workers pass the deal (don't keep the work managing).
 * <p>The keep execution will call the <i>work</i> method on (in this order) :
 * <p>1, then 2, then 3, then 1, then 3 and then will be quiet.
 * <p>The keep is not quiet until all its workers are quiet.
 * <p>
 * <p>Note : If the contained worker 3 doesn't pass the deal, the execution will be :
 * <p>1, then 2, then 3, then 3, then 1. (3 finishes working before passing the deal).
 */
public class Repeat extends Routine {

    protected int maxTurns; // NOT USED : ALWAYS EQUAL -1
    protected int countTurns;

    /**
     * Creates an empty fair keep, ready to contains and execute workers, according to its law.
     */
    /**
     * Creates an empty fair keep, ready to contains and execute workers, according to its law, limited to 'n' turns.
     * @param n maximum number of turns
     */
    public Repeat() {
        super();
        this.setWorkerName("Repeat");
        maxTurns = -1;
        countTurns = 0;
    }

    public void work() {
        worked = false;
        if (workers.isEmpty()) {
            return;
        }

        if (maxTurns > 0) {
            while (!isQuiet() && (countTurns < maxTurns)) {
                for (AbstractWorker w : workers) {
                    // check if to STOP            
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
                countTurns++;
            }
        } else {
            while (!isQuiet()) {
                for (AbstractWorker w : workers) {
                    // check if to STOP            
                    if (getEngine().shouldStop()) {
                        return;
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
        }
    }

    public boolean isQuiet() {
        if (workers.isEmpty()) {
            return true;
        }
        if ((maxTurns > 0) && (countTurns >= maxTurns)) {
            return true;
        }
        for (int pos = 0; pos < workers.size(); pos++) {
            AbstractWorker w = (AbstractWorker) workers.get(pos);
            if ((w != null) && !w.isQuiet()) {
                return false;
            }
        }
        return true;
    }

    //duplication
    /**
     * Creates a fair keep with the toDuplicate's workers.
     * <p><b>The duplication process will duplicate and translate the workers.</b>
     */
    public Repeat(Repeat toDuplicate) {
        super(toDuplicate);
        maxTurns = toDuplicate.maxTurns;
        countTurns = toDuplicate.countTurns;
    // to ducplicate other fields...
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Repeat(this);
        duplicator.setImage(this, d);
        return d;
    }
}


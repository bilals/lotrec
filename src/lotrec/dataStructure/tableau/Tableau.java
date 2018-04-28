package lotrec.dataStructure.tableau;

import java.util.Vector;
import lotrec.dataStructure.graph.ExtendedGraph;
import lotrec.dataStructure.graph.Node;
import lotrec.dataStructure.tableau.action.DuplicateAction;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.CommonDuplicator;
import lotrec.util.DuplicateException;

/**
 * Useful class to represent the tableau used in Lotrec : it manages the nodes event processing, and it contains the strategy working on it.
 * Users should set the strategy using the <i>setStrategy</i> method; and users should add the first created tableau of a wallet in the wallet.
 * The tableaux created from the first tableau (usually with the <i>or</i> rule) will be automatically added to the wallet, in the <i>duplicateAsItMustBeDone</i> method.
 * @author David Fauthoux
 */
public final class Tableau extends ExtendedGraph {

    private String baseName = null; // becomes != null when the tableau becomes to have duplicatas
    private Tableau duplicationInitialParent = null;
    /**
     * Creates an empty tableau. The tableaux created in this way should be add to a wallet, and should receive a strategy.
     * @see lotrec.graph.Wallet#add
     * @see lotrec.graph.ExtendedGraph#setStrategy
     */
    // NO BODY USES THIS METHOD!!!!
    public Tableau() {
        super();
    }

    /**
     * Creates an empty tableau with the specified name. The tableaux created in this way should be add to a wallet, and should receive a strategy.
     * @param name the name of this tableau
     * @see lotrec.graph.Wallet#add
     * @see lotrec.graph.ExtendedGraph#setStrategy
     */
    public Tableau(String name) {
        super(name);
    }

    public boolean hasDuplicataIn(Vector<Tableau> tableauList){
        for (Tableau otherTab : tableauList) {
            if (otherTab.getDuplicationInitialParent() !=null
                    && otherTab.getDuplicationInitialParent().equals(this)) {
                return true;
            }
        }
        return false;
    }

    // duplication
    /**
     * User should use this method to duplicate consistently this tableau : this method fit with the duplication specifications of all the used objects (nodes, edges, event processing, strategy, engine)
     * @return a duplicator that can be used to get images of duplicated objects such as nodes, edges or events
     */
    public final Duplicator duplicateAsItMustBeDone() {
        Duplicator d = new CommonDuplicator();
        Tableau t = (Tableau) duplicate(d);
        ((CommonDuplicator) d).setDuplicatedSource(this);
        ((CommonDuplicator) d).setDuplicatedImage(t);
        try {
            t.completeDuplication(d);
            // We should not increase duplicataNum before duplicating all the nodes
            DuplicateAction.initialRelatedTableau.duplicataNum++;
            t.translateDuplication(d);
            //t.getStrategy().setSelfAndWorkersRelatedTableau(t); //Omitted here because implemented correctly in AbstractWorker.translateDuplication
        } catch (ClassCastException exception) {
            System.err.println(exception);
            return null;
        } catch (DuplicateException exception) {
            System.out.println("Exception is in Tableau: " + exception);
            return null;
        }
        getWallet().add(t);
        return d;
    }

    /**
     * Creates a tableau with the toDuplicate's fields and behaviour.
     * <p><b>See the superclasses duplication behaviour.</b>
     * @param toDuplicate the tableau to duplicate
     */
    public Tableau(Tableau toDuplicate) {
        super(toDuplicate);
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Tableau(this);
        duplicator.setImage(this, d);
        return d;
    }

    /* ADDED 00/12/21 */
    @Override
    public void mark(Object o) {
        super.mark(o);
        getDispatcher().process(new MarkEvent(this, MarkEvent.MARK, o));
    }

    @Override
    public void unmark(Object o) {
        super.unmark(o);
        getDispatcher().process(new MarkEvent(this, MarkEvent.UNMARK, o));
    }

    public Tableau getDuplicationInitialParent() {
        return duplicationInitialParent;
    }

    public void setDuplicationInitialParent(Tableau duplicationInitialParent) {
        this.duplicationInitialParent = duplicationInitialParent;
    }

    /**
     * @return the baseName
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * @param baseName the baseName to set
     */
    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
    /* */

    public int getNbFormulas(){
        int nbFormulas = 0;
        for(Node n:getNodes()){
            nbFormulas += ((TableauNode)n).getMarkedExpressions().size();
        }
        return nbFormulas;
    }
}

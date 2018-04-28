package lotrec.dataStructure.tableau;

import lotrec.dataStructure.graph.Graph;
import lotrec.dataStructure.graph.Edge;
import lotrec.dataStructure.graph.Node;
import lotrec.dataStructure.expression.Expression;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.process.ProcessEvent;

/**
Defines an edge usable in a <code>Tableau</code>. It contains <i>one</i> <code>Expression</code> that specifies the relation.
<p>Note : here, the expression cannot be marked. User should mark the edge because it can contains only one expression.
@see Tableau
@author David Fauthoux
 */
public final class TableauEdge extends Edge {
    private Expression relation;

    /**
    Creates an edge for a tableau with a relation
    @param begin source node
    @param end destination node
    @param relation the expression specifying the relation
     */
    public TableauEdge(Node begin, Node end, Expression relation) {
        super(begin, end);
        this.relation = relation;
    }

    /**
    Returns the edge relation
    @return the edge relation
     */
    public Expression getRelation() {
        return relation;
    }

    /**
    Specify the relation defined by the edge
    @param relation the expression specifying the relation
     */
    public void setRelation(Expression relation) {
        this.relation = relation;
    }

    /** MODIFIED 26 juillet 2000 */

    @Override
    public String toString() {
        return super.toString() + " with " + relation;
    }

    /**                           */

    // duplication

    /**
    Creates a tableau edge with the toDuplicate's relation.
    <b> Calls the superclass constructor with <i>toDuplicate</i>. The relation is not translated.</b>
    @param toDuplicate the egde to duplicate
     */
    public TableauEdge(TableauEdge toDuplicate) {
        super(toDuplicate);
        relation = toDuplicate.relation;
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new TableauEdge(this);
        duplicator.setImage(this, d);
        return d;
    }

    /////////////////////////

    /* ADDED 00/12/21 */
    
    /* Mark action concerns only nodes..
     * and Hide action concerns only expression within nodes.. 
     * So Why these methods were added to TableauEdge Class??
     * I guess that these are the garbage of "extends Marked" obligations :s :s
     */
    
    protected void sendEvent(ProcessEvent e) {
        Graph g = getBeginNode().getGraph();
        if(g != null) g.getDispatcher().process(e);
    }
    @Override
    public void mark(Object o) {
        super.mark(o);
        sendEvent(new MarkEvent(this, MarkEvent.MARK, o));
    }
    @Override
    public void unmark(Object o) {
        super.unmark(o);
        sendEvent(new MarkEvent(this, MarkEvent.UNMARK, o));
    }
    /* */


}

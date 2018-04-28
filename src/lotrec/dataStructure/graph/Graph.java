package lotrec.dataStructure.graph;

import java.util.Enumeration;
import java.util.Vector;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.action.DuplicateAction;
import lotrec.util.Marked;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;
import lotrec.process.Dispatcher;
import lotrec.process.ProcessListener;

/**
Contains nodes, and manages nodes' events, sending them to its listeners
@see Node
@author David Fauthoux
 */
public class Graph extends Marked implements Duplicateable {

    /**
    name given to graphes created with empty constructor
     */
    public static String defaultName = "graph";
    private static int forName = 0;
    private String name;
    private Vector<Node> nodeSet;
    private Wallet container;
    private Dispatcher dispatcher;

    /**
    Creates an empty graph with a default name. The default name is computed as follow : <code>defaultName</code> + an incremented int to distinguish graphes.
     */
    public Graph() {
        this(defaultName + forName);
        forName++;
    }

    /**
    intialise the for name compteur
     */
    public void initialise() {
        forName = 0;
    }

    /**
    Creates an empty graph
    @param name the name of this graph
     */
    public Graph(String name) {
        this.name = name;
        nodeSet = new Vector();
        container = null;
        dispatcher = new Dispatcher();
    }

    /**
    Sets the name of this graph
    @param name the name of this graph
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
    Returns the name of this graph
    @return the name of this graph
     */
    public String getName() {
        return name;
    }

    //called by the wallet when it is added to
    void setWallet(Wallet container) {
        this.container = container;
    }

    /**
    Returns the wallet containing this graph
    @return the wallet containing this graph
     */
    public Wallet getWallet() {
        return container;
    }

    /**
    Adds the specified listener to receive process event from this graph
    @param listener the listener to add
     */
    public void addProcessListener(ProcessListener listener) {
        dispatcher.addProcessListener(listener);
    }

    /**
    Removes the specified listener so it no longer receives process events from this graph
    @param listener the listener to remove
     */
    public void removeProcessListener(ProcessListener listener) {
        dispatcher.removeProcessListener(listener);
    }

    //called by Node /* ADDED 00/12/10 */ or by which want to send event /* */
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    /**
    Adds the node to this graph.
    Sends a <code>NodeEvent</code>
    @param n the node to add
     */
    public void add(Node n) {
        nodeSet.add(n);
        n.setGraph(this);
        dispatcher.process(new NodeEvent(this, NodeEvent.ADDED, n));
    }

    /**
    Removes the node from this graph, completely unlinking it.
    Sends a <code>NodeEvent</code>
    @param n the node to remove
    @return true if this graph contains the node, false otherwise
     */
    public boolean remove(Node n) {
        if (!nodeSet.contains(n)) {
            return false;
        }
        n.unlinkAll();
        nodeSet.remove(n);
        dispatcher.process(new NodeEvent(this, NodeEvent.REMOVED, n));
        return true;
    }

    /**
    Returns the contained nodes
    @return the contained nodes
     */
    public Enumeration getNodesEnumeration() {
        return nodeSet.elements();
    }

    public Node getNode(String nodeName) {
        for (Node node : getNodes()) {
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    public Vector<Node> getNodes() {
        return nodeSet;
    }

    @Override
    public String toString() {
        return name + " [" + container + "] " + super.toString();
    }
    //duplication
    protected int duplicataNum = 1;

    /**
    Creates a graph with the toDuplicate's fields.<b> The nodes will be duplicated with a call to completeDuplication, and translated with a call to translateDuplication. The event dispatcher will be duplicated and translated, and, according to the law of this, the listeners of this graph will be duplicated if they implements the Duplicateable interface. </b>
    @param toDuplicate graph to duplicate
     */
    public Graph(Graph toDuplicate) {
        super(toDuplicate);
//        name = toDuplicate.name + "." + toDuplicate.duplicataNum;
        if(!DuplicateAction.initialRelatedTableau.hasDuplicataIn(DuplicateAction.initialRelatedTableau.getWallet().getGraphesAsTableaux())){
            DuplicateAction.initialRelatedTableau.setBaseName(DuplicateAction.initialRelatedTableau.getName());
            DuplicateAction.initialRelatedTableau.setName(
                    DuplicateAction.initialRelatedTableau.getName()+ "." +
                    DuplicateAction.initialRelatedTableau.duplicataNum);
            DuplicateAction.initialRelatedTableau.duplicataNum++;
        }
        name = DuplicateAction.initialRelatedTableau.getBaseName() + "." + DuplicateAction.initialRelatedTableau.duplicataNum;
//        Tableau tabToDuplicate = (Tableau) toDuplicate;
//        if (!tabToDuplicate.hasDuplicata()) {
//            tabToDuplicate.setBaseName(tabToDuplicate.getName());
//            tabToDuplicate.setName(
//                    tabToDuplicate.getName() + "." +
//                    tabToDuplicate.duplicataNum);
//            tabToDuplicate.duplicataNum++;
//        }
//        name = tabToDuplicate.getBaseName() + "." + tabToDuplicate.duplicataNum;
        nodeSet = (Vector) toDuplicate.nodeSet.clone();
        container = toDuplicate.container;
        dispatcher = toDuplicate.dispatcher;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
        for (int i = 0; i < nodeSet.size(); i++) {
            Node n = (Node) (((Duplicateable) nodeSet.get(i)).duplicate(duplicator));
            n.completeDuplication(duplicator);
            nodeSet.setElementAt(n, i);
        }
        dispatcher = (Dispatcher) dispatcher.duplicate(duplicator);
        dispatcher.completeDuplication(duplicator);
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        for (int i = 0; i < nodeSet.size(); i++) {
            ((Node) nodeSet.get(i)).translateDuplication(duplicator);
        }
        dispatcher.translateDuplication(duplicator);
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Graph(this);
        duplicator.setImage(this, d);
        return d;
    }

    /**
     * (see Node to understand what closed node means)
     * @return true if the graph has AT LEAST one closed node.
     */
    public boolean isClosed() {
        for (Node n : getNodes()) {
            if (n.isClosed()) {
                return true;
            }
        }
        return false;
    }

    public int getDuplicataNum() {
        return duplicataNum;
    }
}

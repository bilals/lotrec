package lotrec.dataStructure.graph;

import java.util.Enumeration;
import java.util.Vector;
import lotrec.util.Marked;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;
import lotrec.process.ProcessEvent;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.InstanceSet;
import lotrec.dataStructure.tableau.TableauEdge;

/**
 * A node is a link and unlink capable object, contained by a <code>Graph</code>.
 * @see Edge
 * @see Graph
 * @author SAHADE Mohamad
 */
public class Node extends Marked implements Duplicateable {

    /**
     * name given to nodes created with empty constructor
     */
    public static String defaultName = "node";
    private static int forName = 1;
    private String name;
    public int number;
    private Vector<Edge> edgeSetNext;
    private Vector<Edge> edgeSetLast;
    private Graph container;
    private boolean closed;

    /**
     * Creates a disconnected node with a default name. The default name is computed as follow : <code>defaultName</code> + an incremented int to distinguish nodes.
     */
    public Node() {
        this(defaultName + /*"[" + */ forName /*+ "]"*/);
        forName++;
    }

    /**
     * intialize the forName counter
     */
    public static void initialiseForName() {
        forName = 1;
    }

    /**
     * Creates a disconnected node (no edge)
     * @param name the name of this node
     */
    public Node(String name) {
        this.name = name;
        number = forName;
        //should be omitted!! After omitting CompareConstarint 
        //and CompareCondition (that are not used..)
        edgeSetNext = new Vector();
        edgeSetLast = new Vector();
        container = null;
        // (closed == true) is equivalent to the fact 
        // that the node contains the Falsum.
        closed = false;
    }

    /**
     * Sets the name of this node
     * @param name the name of this node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this node
     * @return the name of this node
     */
    public String getName() {
        return name;
    }

    //called by the graph when it is added to
    void setGraph(Graph container) {
        this.container = container;
    }

    /**
     * Returns the graph containing this node
     * @return the graph containing this node
     */
    public Graph getGraph() {
        return container;
    }

    public void sendEvent(ProcessEvent e) {
        if (container != null) {
            container.getDispatcher().process(e);
        }
    }

    public boolean equals(Node n) {
        return name.equals(n.getName()) && this.container.equals(n.getGraph());
    }
    //to link
    /**
     * Adds the edge to this node, using the edge specification to link from or to this; also adds the edge to the other node.
     * Sends a <code>LinkEvent</code>
     * @param e edge to add to this node, must begin or end with this node
     * @throws LinkNodeException If the edge does not begin nor end with this node
     */
    public void link(Edge e) throws LinkNodeException {
        if (e.getEndNode().equals(this)) {
            edgeSetLast.add(e);
            e.getBeginNode().edgeSetNext.add(e);
        } else if (e.getBeginNode().equals(this)) {
            edgeSetNext.add(e);
            e.getEndNode().edgeSetLast.add(e);
        } else {
            throw new LinkNodeException("Cannot link node " + this + " with arc " + e);
        }

        sendEvent(new LinkEvent(this, LinkEvent.LINKED, e));
    }

    //to unlink
    /**
     * Removes the edge to this node, using the edge specification to unlink from or to this; also removes the edge from the other node.
     * Sends a <code>LinkEvent</code>
     * @param e the edge to remove
     * @return true if the edge can be removed, false otherwise
     */
    public boolean unlink(Edge e) {

        if (e.getEndNode().equals(this)) {
            if (!edgeSetLast.contains(e)) {
                return false;
            }
            edgeSetLast.remove(e);
            e.getBeginNode().edgeSetNext.remove(e);
        }
        if (e.getBeginNode().equals(this)) {
            if (!edgeSetNext.contains(e)) {
                return false;
            }
            edgeSetNext.remove(e);
            e.getEndNode().edgeSetLast.remove(e);
        }

        sendEvent(new LinkEvent(this, LinkEvent.UNLINKED, e));
        return true;
    }

    /**
     * Removes all the edges from this node and from all the linked nodes.
     */
    public void unlinkAll() {
        for (Enumeration enumr = getNextEdgesEnum(); enumr.hasMoreElements();) {
            unlink((Edge) enumr.nextElement());
        }
        for (Enumeration enumr = getLastEdgesEnum(); enumr.hasMoreElements();) {
            unlink((Edge) enumr.nextElement());
        }
    }

    //util
    /**
     * Returns the edges linking this node <b>to</b> other nodes
     * @return the edges linking this node to other nodes
     */
    public Enumeration getNextEdgesEnum() {
        return edgeSetNext.elements();
    }

    /**
     * Returns the edges linking this node <b>from</b> other nodes
     * @return the edges linking this node to other nodes
     */
    public Enumeration getLastEdgesEnum() {
        return edgeSetLast.elements();
    }

    public Vector<Edge> getNextEdges() {
        return edgeSetNext;
    }

    public Vector<Edge> getLastEdges() {
        return edgeSetLast;
    }


    @Override
    public String toString() {
        return name + "-of-" + /*" [" +*/ container.getName() /*+ "]" */ + super.toString();
    }

    /**
     * Returns the first edge of label "e" if this node has a Succsessor by "e"
     * @return null if this node hasn't any   succsesseur linked to it by "e", the first edge otherwise
     */
    public Edge hasSuccessor(Expression e) {
        try {
            for (Enumeration enumr = getNextEdgesEnum(); enumr.hasMoreElements();) {
                TableauEdge edge = (TableauEdge) enumr.nextElement();

                if (edge.getRelation().equals(e)) {
                    return edge;
                }
            }
        } catch (Exception ex) {
            System.out.println("  	 iccccc");
        }
        return null;

    }

    public Edge hasParent(Expression e) {
        try {
            for (Enumeration enumr = this.getLastEdgesEnum(); enumr.hasMoreElements();) {
                TableauEdge edge = (TableauEdge) enumr.nextElement();

                if (edge.getRelation().equals(e)) {
                    return edge;
                }
            }
        } catch (Exception ex) {
            System.out.println("  	 iccccc");
        }
        return null;
    }

    //useful
    /**
     * Finds the edges linked with this node, parameter node being the destination of the edges
     * @param node destination node used to find the edges
     * @return edges linked with this node
     */
    /*
    public Enumeration getEdgesWithSuchNextNode(Node node) {
    return new SpecialEnumeration(node);
    }
     */
    private class SpecialEnumeration implements Enumeration {

        private Object next;
        private Enumeration current;
        private Node node;

        public SpecialEnumeration(Node n) {
            node = n;
            current = getNextEdgesEnum();
            next = null;
            while (current.hasMoreElements()) {
                Edge e = (Edge) current.nextElement();
                if (e.getEndNode().equals(node)) {
                    next = e;
                    break;
                }
            }
        }

        @Override
        public boolean hasMoreElements() {
            return current.hasMoreElements();
        }

        @Override
        public Object nextElement() {
            Object r = next;
            next = null;
            while (current.hasMoreElements()) {
                Edge e = (Edge) current.nextElement();
                if (e.getEndNode().equals(node)) {
                    next = e;
                    break;
                }
            }
            if (r == null) {
                throw new java.util.NoSuchElementException();
            }
            return r;
        }
    }
    // duplication
//    private int duplicatesNum = 1;
    //private Node toDuplicateNode;

    /**
     * Creates a node with the toDuplicate's fields.<b>
     * The edges where this node is source will be duplicated and translated to reference the duplicated destination nodes.
     * The edges where this node is destination will not be
     * duplicated, but must be duplicated when you call translateDuplication.</b>
     * @param toDuplicate node to duplicate
     */
    public Node(Node toDuplicate) {
        super(toDuplicate);
        //toDuplicateNode = toDuplicate;
        name = toDuplicate.name;// + "." + toDuplicate.getGraph().getDuplicataNum();
        number = toDuplicate.number;
        edgeSetNext = (Vector) toDuplicate.edgeSetNext.clone();
        edgeSetLast = (Vector) toDuplicate.edgeSetLast.clone();
        container = toDuplicate.container;
        closed = toDuplicate.closed;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
        for (int i = 0; i < edgeSetNext.size(); i++) {
            Edge e = (Edge) ((Duplicateable) edgeSetNext.get(i)).duplicate(duplicator);
            e.completeDuplication(duplicator);
            edgeSetNext.setElementAt(e, i);
        }
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        for (int i = 0; i < edgeSetNext.size(); i++) {
            ((Edge) edgeSetNext.get(i)).translateDuplication(duplicator);
        }
        for (int i = 0; i < edgeSetLast.size(); i++) {
            edgeSetLast.setElementAt((Edge)duplicator.getImage(edgeSetLast.get(i)), i);
        }
        container = (Graph) duplicator.getImage(container);
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Node(this);
        duplicator.setImage(this, d);
        return d;
    }

    /**
     * return all  R successor of this node if it has any one, null otherwise.
     * @param e the relation
     */
    public Enumeration getAllSuccessors(Expression relation, InstanceSet instanceSet) {
        Vector allSuccessors = new Vector();
        try {
            for (Enumeration enumr = getNextEdgesEnum(); enumr.hasMoreElements();) {
                TableauEdge edge = (TableauEdge) enumr.nextElement();
                Expression r;
                r = edge.getRelation();
//                InstanceSet newInstanceSet = r.matchWith(relation, instanceSet);
                InstanceSet newInstanceSet = relation.matchWith(r, instanceSet);
//                if (newInstanceSet != null ){
                if (newInstanceSet != null && newInstanceSet.equals(instanceSet)) {
                    allSuccessors.add(edge.getEndNode());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return allSuccessors.elements();
    }

    public boolean isClosed() {
        return closed;
    }
    
    // This method should be used only when Falsum is added or deleted to the node.
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}


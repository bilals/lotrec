
/*
  File: FlagFilter.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

//---------------------------------------------------------------------------
//  $Revision: 7760 $ 
//  $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
//  $Author: mes $
//---------------------------------------------------------------------------
package cytoscape.data;
//---------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.Node;
import giny.model.Edge;
import giny.model.RootGraph;
import giny.filter.Filter;
//---------------------------------------------------------------------------
/**
 * This class implements the ability to attach a flag to every node or
 * edge in a GraphPerspective. The flag can be either on or off. Methods are
 * provided for inspecting the current state of any graph object, for
 * setting the state, or getting the full set of currently flagged
 * nodes or edges. This functionality is often used to identify a set
 * of interesting nodes or edges in the graph.<P>
 *
 * A non-null GraphPerspective reference is required to construct an instance
 * of this class. This class will listen to the graph to respond to
 * the removal of graph objects. A currently flagged object that is
 * removed from the graph will lose its flag, even if it is later
 * added back to the graph.<P>
 *
 * When the state of a node or edge is changed, a event of type FlagEvent
 * is fired. When a group of nodes or edges are changed together in a single
 * operation, one event will be fired for the whole group (but separate
 * events for nodes and edges). Note: a listener should not be removed from
 * this object in response to the firing of an event, as this may cause
 * a ConcurrentModificationException.<P>
 *
 * WARNING: for performance reasons, the set of objects returned by the
 * getSelectedXX methods is the actual data object, not a copy. Users should
 * not directly modify these sets.<P>
 *
 * Performance note: the implementation is a HashSet of flagged objects,
 * so most methods are O(1). Operations on groups of nodes are O(N) where
 * N is either the number of flagged objects or the number of objects in
 * the graph, as applicable.<P>
 * 
 * @deprecated As of 2.3, replaced with {@link cytoscape.data.SelectFilter}
 */
public class FlagFilter implements Filter, GraphPerspectiveChangeListener {
    
	protected SelectFilter selectFilter;
	protected SelectEventListener myListener = new SelectEventListener (){
		public void onSelectEvent (SelectEvent event){
			fireEvent(event.getTarget(),event.getEventType());
		}
	};
	 List listeners = new ArrayList();
	
    /**
     * Standard Constructor. The argument is the graph that this filter will
     * apply to; it cannot be null.
     *
     * @throws NullPointerException  if the argument is null.
     */
    public FlagFilter(GraphPerspective graph) {
    		this.selectFilter = new SelectFilter(graph);
    		this.selectFilter.addSelectEventListener(this.myListener);
    }
    
    /**
     * Constructor added so that FlagFilter can be properly deprecated
     * 
     * @param selectFilter the SelectFilter that performs all the methods in this FlagFilter
     */
    public FlagFilter (SelectFilter selectFilter){
    		this.selectFilter = selectFilter;
    		this.selectFilter.addSelectEventListener(this.myListener);
    } 
   
    /**
     * Returns the set of all flagged nodes in the referenced GraphPespective.<P>
     *
     * WARNING: the returned set is the actual data object, not a copy. Don't
     * directly modify this set.
     */
    public Set getFlaggedNodes() {return this.selectFilter.getSelectedNodes();}
    /**
     * Returns the set of all flagged edges in the referenced GraphPespective.<P>
     *
     * WARNING: the returned set is the actual data object, not a copy. Don't
     * directly modify this set.
     */
    public Set getFlaggedEdges() {return this.selectFilter.getSelectedEdges();}
    
    
    /**
     * Returns true if the argument is a flagged Node in the referenced
     * GraphPerspective, false otherwise.
     */
    public boolean isFlagged(Node node) {return this.selectFilter.isSelected(node);}
    /**
     * Returns true if the argument is a flagged Edge in the referenced
     * GraphPerspective, false otherwise.
     */
    public boolean isFlagged(Edge edge) {return this.selectFilter.isSelected(edge);}
    
    /**
     * Implementation of the Filter interface. Returns true if the argument
     * is a flagged Node or Edge in the referenced GraphPerspective, false otherwise.
     */
    public boolean passesFilter(Object o) {
        return this.selectFilter.passesFilter(o);
    }
    
    
    /**
     * If the first argument is a Node in the referenced GraphPerspective,
     * sets its flagged state to the value of the second argument. An event
     * will be fired iff the new state is different from the old state.
     *
     * @return  true if an actual change was made, false otherwise
     */
    public boolean setFlagged(Node node, boolean newState) {
    		return this.selectFilter.setSelected(node,newState);
    }
    
    /**
     * If the first argument is an Edge in the referenced GraphPerspective,
     * sets its flagged state to the value of the second argument. An event
     * will be fired iff the new state is different from the old state.
     *
     * @return  true if an actual change was made, false otherwise
     */
    public boolean setFlagged(Edge edge, boolean newState) {
    		return this.selectFilter.setSelected(edge,newState);
    }
    
    /**
     * Sets the flagged state defined by the second argument for all Nodes
     * contained in the first argument, which should be a Collection of Node objects
     * contained in the referenced GraphPerspective. One event will be fired
     * for the full set of changes. This method does nothing if the first
     * argument is null.
     *
     * @return a Set containing the objects for which the flagged state changed
     * @throws ClassCastException  if the first argument contains objects other
     *                             than giny.model.Node objects
     */
    public Set setFlaggedNodes(Collection nodesToSet, boolean newState) {
    		return this.selectFilter.setSelectedNodes(nodesToSet,newState);
    }
    
    /**
     * Sets the flagged state defined by the second argument for all Edges
     * contained in the first argument, which should be a Collection of Edge objects
     * contained in the referenced GraphPerspective. One event will be fired
     * for the full set of changes. This method does nothing if the first
     * argument is null.
     *
     * @return a Set containing the objects for which the flagged state changed
     * @throws ClassCastException  if the first argument contains objects other
     *                             than giny.model.Edge objects
     */
    public Set setFlaggedEdges(Collection edgesToSet, boolean newState) {
        return this.selectFilter.setSelectedEdges(edgesToSet,newState);
    }
    
    /**
     * Sets the flagged state to true for all Nodes in the GraphPerspective.
     * @return a Set of nodes that changed state
     */
    public Set flagAllNodes() {
    		return this.selectFilter.selectAllNodes();
    }
    
    /**
     * Sets the flagged state to true for all Edges in the GraphPerspective.
     */
    public Set flagAllEdges() {
        return this.selectFilter.selectAllEdges();
    }
    
    /**
     * Sets the flagged state to false for all Nodes in the GraphPerspective.
     */
    public Set unflagAllNodes() {
    		return this.selectFilter.unselectAllNodes();
    }
    
    /**
     * Sets the flagged state to false for all Edges in the GraphPerspective.
     */
    public Set unflagAllEdges() {
    		return this.selectFilter.unselectAllEdges();
    }
    
    
    /**
     * Implementation of the GraphPerspectiveChangeListener interface. Responds
     * to the removal of nodes and edges by removing them from the set of
     * flagged graph objects if needed. Fires an event only if there was an
     * actual change in the current flagged set.
     */
    public void graphPerspectiveChanged(GraphPerspectiveChangeEvent event) {
        this.selectFilter.graphPerspectiveChanged(event);
    }
    
    /**
     * If the argument is not already a listener to this object, it is added.
     * Does nothing if the argument is null.
     */
    public void addFlagEventListener(FlagEventListener listener) {
        if (listener != null) {listeners.add(listener);}
    }
    
    /**
     * If the argument is a listener to this object, removes it from the list
     * of listeners.
     */
    public void removeFlagEventListener(FlagEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets a List of All Registered Listeners.
     * @return
     */
    public List getFlagEventListeners() {
        return listeners;
    }

    /**
     * Fires an event to all registered listeners that represents the operation
     * described by the arguments. The first argument should be the graph object
     * whose flagged state changed, or a Set of such objects. The second argument
     * identifies the change; true for setting a flag and false for removing it.
     * Creates a suitable event and passes it to all listeners.
     */
    protected void fireEvent(Object target, boolean selectOn) {
        //assert(target != null);//should never get called with null target
        FlagEvent event = new FlagEvent(this, target, selectOn);
        for (Iterator i = this.listeners.iterator(); i.hasNext(); ) {
            FlagEventListener listener = (FlagEventListener)i.next();
            listener.onFlagEvent(event);
        }
    }
    
            
}


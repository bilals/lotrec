/*
  File: SelectEvent.java 
  
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
import java.util.Set;

import giny.model.Node;
import giny.model.Edge;
//---------------------------------------------------------------------------
/**
 * Events that are fired when the selected state of a Node or Edge, or a group
 * of Nodes or Edges, is changed.
 */
public class SelectEvent {

    /**
     * Static constant indicating a change to a single Node.
     */
    public static final int SINGLE_NODE = 0;
    /**
     * Static constant indicating a change to a single Edge.
     */
    public static final int SINGLE_EDGE = 1;
    /**
     * Static constant indicating a change to a group of Nodes.
     */
    public static final int NODE_SET = 2;
    /**
     * Static constant indicating a change to a group of Edges.
     */
    public static final int EDGE_SET = 3;
    
    private SelectFilter source;
    private Object target;
    private int targetType;
    private boolean selectOn = true;
    
    /**
     * Standard constructor.<P>
     *
     * The first argument is the object that fired this event.<P>
     *
     * The second argument decribes what objects were affected; it should be of
     * type Node, Edge, a Set of Nodes, or a Set of Edges. If the argument is a
     * Set, it should contain at least one element.
     *
     * The third argument is a boolean indicating the type of event. It should be true
     * if the change is setting the selected state to true for the target objects, or false
     * if the change is setting it to false.<P>
     *
     * @throws IllegalArgumentException if the target is null or an invalid type,
     */
    public SelectEvent(SelectFilter source, Object target, boolean selectOn) {
        this.source = source;
        this.target = target;
        this.selectOn = selectOn;
        if (target == null) {
            throw new IllegalArgumentException("Unexpected null target");
        } else if (target instanceof Node) {
            this.targetType = this.SINGLE_NODE;
        } else if (target instanceof Edge) {
            this.targetType = this.SINGLE_EDGE;
        } else if (target instanceof Set) {
            Set targetSet = (Set)target;
            if (targetSet.size() == 0) {
                throw new IllegalArgumentException("Unexpected empty target set");
            }
            Object first = targetSet.iterator().next();
            if (first instanceof Node) {
                this.targetType = this.NODE_SET;
            } else if (first instanceof Edge) {
                this.targetType = this.EDGE_SET;
            } else {//unknown object type
                throw new IllegalArgumentException("Unknown object type in target set");
            }
        } else {
            throw new IllegalArgumentException("Unexpected target type");
        }
    }
    
    /**
     * Returns the source of this event.
     */
    public SelectFilter getSource() {return source;}
    
    /**
     * Returns an object reference to the target that was changed. This should
     * be a Node, an Edge, a Set of Nodes, or a Set of Edges. The return value
     * of getTargetType determines which of the four cases applies.
     */
    public Object getTarget() {return target;}

    /**
     * Returns a static constant identifying the type of object; either SINGLE_NODE
     * for a Node, SINGLE_EDGE for an Edge, NODE_SET for a Set of Nodes, or
     * EDGE_SET for a Set of Edges.
     */
    public int getTargetType() {return targetType;}
    
    /**
     * Returns a boolean identifying the type of event, true the selectes state was set to true, false if it was set to false
     */
    public boolean getEventType() {return selectOn;}
    
    /**
     * Returns a String representation of this object's data.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String lineSep = System.getProperty("line.separator");
        sb.append("SelectEvent:" + lineSep);
        sb.append("    target = " + getTarget() + lineSep);
        sb.append("    target type = ");
        switch (getTargetType()) {
            case SINGLE_NODE:
                sb.append("SINGLE_NODE");
                break;
            case SINGLE_EDGE:
                sb.append("SINGLE_EDGE");
                break;
            case NODE_SET:
                sb.append("NODE_SET");
                break;
            case EDGE_SET:
                sb.append("EDGE_SET");
                break;
            default: //should never happen
                sb.append(getTargetType());
                break;
        }
        sb.append(lineSep);
        sb.append("    event type = ");
        if (getEventType()) {sb.append("ON");} else {sb.append("OFF");}
        sb.append(lineSep);
        return sb.toString();
    }
}


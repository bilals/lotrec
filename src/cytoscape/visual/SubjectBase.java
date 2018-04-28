
/*
  File: SubjectBase.java 
  
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

package cytoscape.visual;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;

/**
 * Abstract Base Class for Subject in the Subject / Observer Pattern.
 * Also Known as Publisher / Subscriber Pattern.
 *
 * A Subject class notifies all its subscribers whenever its state
 * changes.
 *
 * Note that this code duplicates some code in the AbstractCalculator class.
 * May be a good place to refactor in the future.
 */
public abstract class SubjectBase {
    /**
     * An Array List of All Observers who want to be notified of changes.
     */
    protected ArrayList observers = new ArrayList();

    /**
     * Add a ChangeListener. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * @param listener ChangeListener to add
     */
    public void addChangeListener(ChangeListener listener) {
        this.observers.add(listener);
    }

    /**
     * Remove a ChangeListener from the calcaultor. When the state underlying
     * the calculator changes, all ChangeListeners will be notified.
     *
     * @param listener ChangeListener to add
     */
    public void removeChangeListener(ChangeListener listener) {
        this.observers.remove(listener);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.
     */
    public void fireStateChanged() {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = this.observers.size() - 1; i >= 0; i--) {
            ChangeListener listener = (ChangeListener) this.observers.get(i);
            ChangeEvent changeEvent = new ChangeEvent(this);
            listener.stateChanged(changeEvent);
        }
    }
}
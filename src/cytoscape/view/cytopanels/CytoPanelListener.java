
/*
  File: CytoPanelListener.java 
  
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

//     
// $Id: CytoPanelListener.java 7760 2006-06-26 16:28:49Z mes $
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

// imports
import java.util.EventListener;

/**
 * This listener interface provides the
 * mechanism to respond to CytoPanel Events.
 *
 * @author Ben Gross
 */
public interface CytoPanelListener extends EventListener {

    /**
     * Notifies the listener on a change in the CytoPanel state.
     *
     * @param newState The new CytoPanel state - see CytoPanelState class.
     */
    public void onStateChange(CytoPanelState newState);

    /**
     * Notifies the listener when a new component on the CytoPanel is selected.
     *
	 * @param componentIndex The index of the component selected.
     */
    public void onComponentSelected(int componentIndex);

    /**
     * Notifies the listener when a component is added to the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel after the add.
     */
    public void onComponentAdded(int count);

    /**
     * Notifies the listener when a component is removed from the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel after the remove.
     */
    public void onComponentRemoved(int count);
}

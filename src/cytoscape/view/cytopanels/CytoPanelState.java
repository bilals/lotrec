
/*
  File: CytoPanelState.java 
  
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
// $Id: CytoPanelState.java 7760 2006-06-26 16:28:49Z mes $
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

/**
 *  CytoPanelState Class.  The following States are supported:
 * <UL>
 * <LI>CytoPanelState.HIDE:  Hide the CytoPanel.
 * <LI>CytoPanelState.FLOAT: Float the CytoPanel.
 * <LI>CytoPanelState.DOCK:  Dock the CytoPanel.
 * </UL>
 *
 * @author Ben Gross
 */
public class CytoPanelState {

	/*
	 * The state name.
	 */
	private final String name;

	/*
	 * Private constructor.
	 */
	private CytoPanelState(String name){
		this.name = name;
	}

	/*
	 * In case a someone wants to translated a state into printable strings
	 */
	public String toString() {
		return name;
	}

    /**
     * Hide state of a CytoPanel.
     */
	public static final CytoPanelState HIDE = new CytoPanelState("hide");

    /**
     * Float state of a CytoPanel.
     */
	public static final CytoPanelState FLOAT = new CytoPanelState("float");

    /**
     * Dock state of a CytoPanel.
     */
	public static final CytoPanelState DOCK = new CytoPanelState("dock");
}

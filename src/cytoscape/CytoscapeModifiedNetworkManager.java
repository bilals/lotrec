
/*
  File: CytoscapeModifiedNetworkManager.java 
  
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

/*
 * Created on Sep 13, 2005
 *
 */
package cytoscape;

import java.beans.PropertyChangeEvent;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import java.util.HashMap;
import java.beans.PropertyChangeListener;

/**
 * 
 * CytoscapeModifiedNetworkManager manages the modified state settings for the networks and listens for 
 * PropertyChangeEvents.  This enables functionality such as prompting the user to save modified
 * networks when exiting Cytoscape.
 * 
 * @author Allan Kuchinsky
 * @version 1.0
 *
 * 
 *
 */
public class CytoscapeModifiedNetworkManager  implements PropertyChangeListener {

	
	public static final String MODIFIED = "Modified";
	public static final String CLEAN = "Clean";
	private static HashMap networkStateMap = new HashMap();
	
	/**
	 * 
	 */
	public CytoscapeModifiedNetworkManager()  {
		super();

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						Cytoscape.NETWORK_MODIFIED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						Cytoscape.NETWORK_SAVED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(
				Cytoscape.NETWORK_CREATED, this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
	}

    /**
     * 
     */
	public void propertyChange(PropertyChangeEvent e) {

		//		System.out.println ("Property changed: " + e.getPropertyName());
		//		System.out.println ("Old value = " + e.getOldValue());
		//		System.out.println ("New value = " + e.getNewValue());

			if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
				CyNetwork net = (CyNetwork) e.getNewValue();
				if (net instanceof CyNetwork) {
				setModified(net, MODIFIED);
				}
			} else if (e.getPropertyName().equals(Cytoscape.NETWORK_SAVED)) {
			    // MLC 09/19/05 BEGIN:
			    // CyNetwork net = (CyNetwork) e.getNewValue();
			    CyNetwork net = (CyNetwork)(((Object[]) e.getNewValue())[0]);
			    // MLC 09/19/05 END.
				if (net instanceof CyNetwork) {
					setModified(net, CLEAN);
				}
			}
		}
	
	/**
	 * 
	 * @param net
	 * @return
	 */
	public static boolean isModified(CyNetwork net)
	{
		Object modObj = networkStateMap.get(net);
		if (modObj == null)   // no network in table, so it can't be modified
		{
			return false;
		}
		else if (modObj.toString().equals(MODIFIED))
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	
	/**
	 * set the state of the network
	 * @param net
	 * @param state values supported in this version: CLEAN, MODIFIED
	 */
	public static void setModified (CyNetwork net, String state)
	{
		networkStateMap.put(net, state);
	}
	
	
}

/*
 File: NewWindowSelectedNodesEdgesAction.java 
 
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

//-------------------------------------------------------------------------
// $Revision: 8514 $
// $Date: 2006-10-18 15:02:53 -0700 (Wed, 18 Oct 2006) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;

//-------------------------------------------------------------------------
public class NewWindowSelectedNodesEdgesAction extends CytoscapeAction {

	public NewWindowSelectedNodesEdgesAction() {
		super("From selected nodes, selected edges");
		setPreferredMenu("File.New.Network");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
				| ActionEvent.SHIFT_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		// save the vizmapper catalog

		CyNetwork current_network = Cytoscape.getCurrentNetwork();

		Set nodes = current_network.getSelectedNodes();
		Set edges = current_network.getSelectedEdges();

		CyNetwork new_network = Cytoscape.createNetwork(nodes, edges,
				CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
				current_network);

		String title = " selection";
		Cytoscape.createNetworkView(new_network, title);
		
		// Set visual style
		Cytoscape.getNetworkView(new_network.getIdentifier())
			.setVisualStyle(Cytoscape.getCurrentNetworkView().getVisualStyle().getName());

	}
}

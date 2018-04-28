/*
 File: NewWindowSelectedNodesOnlyAction.java 
 
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
import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

public class NewWindowSelectedNodesOnlyAction extends CytoscapeAction {

	public NewWindowSelectedNodesOnlyAction() {
		super("From selected nodes, all edges");
		setPreferredMenu("File.New.Network");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK);
	}

	public void actionPerformed(ActionEvent e) {
		// save the vizmapper catalog

		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		CyNetworkView current_network_view = null;
		if (Cytoscape.viewExists(current_network.getIdentifier())) {
			current_network_view = Cytoscape.getNetworkView(current_network
					.getIdentifier());
		} // end of if ()

		Set nodes = current_network.getSelectedNodes();

		CyNetwork new_network = Cytoscape.createNetwork(nodes, current_network
				.getConnectingEdges(new ArrayList(nodes)), CyNetworkNaming
				.getSuggestedSubnetworkTitle(current_network), current_network);

		CyNetworkView new_view = Cytoscape.getNetworkView(new_network
				.getIdentifier());
		if (new_view == Cytoscape.getNullNetworkView()) {
			return;
		}

		if (current_network_view != Cytoscape.getNullNetworkView()) {

			Iterator i = new_network.nodesIterator();
			while (i.hasNext()) {
				Node node = (Node) i.next();
                                new_view.getNodeView( node ).setOffset( current_network_view.getNodeView(node).getXPosition(),
                                                                        current_network_view.getNodeView(node).getYPosition());
			}
                        new_view.fitContent();

			// Set visual style
			VisualStyle newVS = current_network_view.getVisualStyle();
			if(newVS != null) {
				new_view.setVisualStyle(newVS.getName());
			} else {
				new_view.setVisualStyle("default");
			}
			
		} else {
			new_view.setVisualStyle("default");
		}

	}

}

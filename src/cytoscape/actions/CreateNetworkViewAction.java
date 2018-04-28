/*
 File: CreateNetworkViewAction.java 
 
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

package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;

public class CreateNetworkViewAction extends CytoscapeAction implements
		PropertyChangeListener {

	public CreateNetworkViewAction() {
		super("Create View");
		setPreferredMenu("Edit");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_V, ActionEvent.ALT_MASK);

		super.setEnabled(false);

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
	}

	public CreateNetworkViewAction(boolean label) {
		super();
	}

	public void actionPerformed(ActionEvent e) {
		CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
		createViewFromCurrentNetwork(cyNetwork);
	}

	public static void createViewFromCurrentNetwork(CyNetwork cyNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");
		if (cyNetwork.getNodeCount() > Integer.parseInt(CytoscapeInit
				.getProperties().getProperty("secondaryViewThreshold"))) {
			int n = JOptionPane
					.showConfirmDialog(
							Cytoscape.getDesktop(),
							"Network contains "
									+ formatter
											.format(cyNetwork.getNodeCount())
									+ " nodes and "
									+ formatter
											.format(cyNetwork.getEdgeCount())
									+ " edges.  "
									+ "\nRendering a network this size may take several "
									+ "minutes.\n" + "Do you wish to proceed?",
							"Rendering Large Network",
							JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				Cytoscape.createNetworkView(cyNetwork);
			} else {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"Create View Request Cancelled by User.");
			}
		} else {
			Cytoscape.createNetworkView(cyNetwork);
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		if ((e.getPropertyName()
				.equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED))) {
			super.setEnabled(true);
		}
	}
}

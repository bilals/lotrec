
/*
  File: SelectionModeAction.java 
  
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
import javax.swing.*;
import giny.view.GraphView;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;

public class SelectionModeAction extends JMenu {

	public SelectionModeAction() {
		super("Mouse Drag Selects");

		ButtonGroup modeGroup = new ButtonGroup();
		JCheckBoxMenuItem nodes = new JCheckBoxMenuItem(new AbstractAction(
				"Nodes Only") {
			public void actionPerformed(ActionEvent e) {
				// Do this in the GUI Event Dispatch thread...
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Cytoscape.setSelectionMode(Cytoscape.SELECT_NODES_ONLY);
					}
				});
			}
		});
		nodes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK));

		JCheckBoxMenuItem edges = new JCheckBoxMenuItem(new AbstractAction(
				"Edges Only") {
			public void actionPerformed(ActionEvent e) {
				// Do this in the GUI Event Dispatch thread...
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Cytoscape.setSelectionMode(Cytoscape.SELECT_EDGES_ONLY);
					}
				});
			}
		});
		edges.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK));

		JCheckBoxMenuItem nodesAndEdges = new JCheckBoxMenuItem(
				new AbstractAction("Nodes and Edges") {
					public void actionPerformed(ActionEvent e) {
						// Do this in the GUI Event Dispatch thread...
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								Cytoscape
										.setSelectionMode(Cytoscape.SELECT_NODES_AND_EDGES);
							}
						});
					}
				});
		nodesAndEdges.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK));

		modeGroup.add(nodes);
		modeGroup.add(edges);
		modeGroup.add(nodesAndEdges);

		add(nodes);
		add(edges);
		add(nodesAndEdges);
		// nodes.setSelected(true);
		nodesAndEdges.setSelected(true);
		GraphView view = Cytoscape.getCurrentNetworkView();
		view.enableNodeSelection();
		view.enableEdgeSelection();
		Cytoscape.setSelectionMode(Cytoscape.SELECT_NODES_AND_EDGES);
	}

}

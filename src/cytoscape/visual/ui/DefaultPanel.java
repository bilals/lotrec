/*
 File: DefaultPanel.java 
 
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

//----------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.ui;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

//----------------------------------------------------------------------------
/**
 * Defines a class to provide the interface for specifying global defaults such
 * as background color.
 * <p>
 * Note: New defaults are added for 2.3.
 * 
 * @see cytoscape.visual.ui.VizMapUI
 * 
 */
public class DefaultPanel extends JPanel implements ChangeListener {
	// private DefaultBackgroundRenderer bgRender;
	private VisualMappingManager vmm;

	private ValueDisplayer backColor;
	private ValueDisplayer selectedNodeColor;
	private ValueDisplayer reverseSelectedNodeColor;
	private ValueDisplayer selectedEdgeColor;
	private ValueDisplayer reverseSelectedEdgeColor;

	private VizMapUI parentDialog;

	/**
	 * Constructor.<br>
	 * Create panel for global defaults.
	 * 
	 * @param parentDialog
	 * @param vmm
	 */
	public DefaultPanel(VizMapUI parentDialog, VisualMappingManager vmm) {
		super(false);
		this.parentDialog = parentDialog;
		this.vmm = vmm;

		// Register class to receive notifications of changes in the
		// GlobalAppearance Calculator.
		VisualStyle vs = vmm.getVisualStyle();
		GlobalAppearanceCalculator gCalc = vs.getGlobalAppearanceCalculator();
		gCalc.addChangeListener(this);

		// Also, get notifications is user changes to a different
		// visual style.
		vmm.addChangeListener(this);

		/*
		 * Add buttons to the panel.
		 */
		addColorButtons();
	}

	/**
	 * Add button to set default colors.<br>
	 * 
	 */
	private void addColorButtons() {
		/**
		 * TODO Need to replace old & stupid Java's layout system with
		 * swing-layout.
		 * 
		 */
		final GridBagGroup def = new GridBagGroup();
		def.panel = this;
		setLayout(def.gridbag);
		MiscGB.pad(def.constraints, 2, 2);
		MiscGB.inset(def.constraints, 3);

		final GlobalAppearanceCalculator gCalc = vmm.getVisualStyle()
				.getGlobalAppearanceCalculator();

		/*
		 * Background color
		 */
		this.backColor = ValueDisplayer.getDisplayFor(parentDialog,
				"Background Color", gCalc.getDefaultBackgroundColor());
		backColor.addItemListener(new BackColorListener());
		JButton backColorBut = new JButton("Background Color");
		backColorBut.addActionListener(backColor.getInputListener());
		MiscGB.insert(def, backColorBut, 0, 0);VisualStyle vs = vmm.getVisualStyle();
		MiscGB.insert(def, backColor, 1, 0);

		/*
		 * Selected node color
		 */
		this.selectedNodeColor = ValueDisplayer.getDisplayFor(parentDialog,
				"Selected Node Color 1", gCalc.getDefaultNodeSelectionColor());
		this.selectedNodeColor.addItemListener(new SelectedNodeColorListener());
		JButton selectedNodeColorButton = new JButton("Selected Node Color 1");
		selectedNodeColorButton
				.setToolTipText("Change color for selected nodes. This is for normal selection.");
		selectedNodeColorButton.addActionListener(selectedNodeColor
				.getInputListener());
		MiscGB.insert(def, selectedNodeColorButton, 0, 1);
		MiscGB.insert(def, selectedNodeColor, 1, 1);

		/*
		 * Reverse selected node color
		 */
		this.reverseSelectedNodeColor = ValueDisplayer.getDisplayFor(
				parentDialog, "Selected Node Color 2", gCalc
						.getDefaultNodeReverseSelectionColor());
		this.reverseSelectedNodeColor
				.addItemListener(new ReverseSelectedNodeColorListener());
		JButton reverseSelectedNodeColorButton = new JButton(
				"Selected Node Color 2");
		reverseSelectedNodeColorButton
				.setToolTipText("Change color for selected nodes in attribute browser.");
		reverseSelectedNodeColorButton
				.addActionListener(reverseSelectedNodeColor.getInputListener());
		MiscGB.insert(def, reverseSelectedNodeColorButton, 0, 2);
		MiscGB.insert(def, reverseSelectedNodeColor, 1, 2);

		/*
		 * Selected edge color
		 */
		this.selectedEdgeColor = ValueDisplayer.getDisplayFor(parentDialog,
				"Selected Edge Color 1", gCalc.getDefaultEdgeSelectionColor());
		this.selectedEdgeColor.addItemListener(new SelectedEdgeColorListener());
		JButton selectedEdgeColorButton = new JButton("Selected Edge Color 1");
		selectedEdgeColorButton
				.setToolTipText("Change color for selected edges. This is for normal selection.");
		selectedEdgeColorButton.addActionListener(selectedEdgeColor
				.getInputListener());
		MiscGB.insert(def, selectedEdgeColorButton, 0, 3);
		MiscGB.insert(def, selectedEdgeColor, 1, 3);

		/*
		 * Reverse selected node color
		 */
		this.reverseSelectedEdgeColor = ValueDisplayer.getDisplayFor(
				parentDialog, "Selected Edge Color 2", gCalc
						.getDefaultEdgeReverseSelectionColor());
		this.reverseSelectedEdgeColor
				.addItemListener(new ReverseSelectedEdgeColorListener());
		JButton reverseSelectedEdgeColorButton = new JButton(
				"Selected Edge Color 2");
		reverseSelectedEdgeColorButton
				.setToolTipText("Change color for selected edges in attribute browser.");
		reverseSelectedEdgeColorButton
				.addActionListener(reverseSelectedEdgeColor.getInputListener());
		MiscGB.insert(def, reverseSelectedEdgeColorButton, 0, 4);
		MiscGB.insert(def, reverseSelectedEdgeColor, 1, 4);

	}

	public void stateChanged(ChangeEvent e) {
		VisualStyle vs = vmm.getVisualStyle();
		GlobalAppearanceCalculator gCalc = vs.getGlobalAppearanceCalculator();
		Color backgroundColor = gCalc.getDefaultBackgroundColor();
		backColor.setBackground(backgroundColor);
		selectedNodeColor.setBackground(gCalc.getDefaultNodeSelectionColor());
		reverseSelectedNodeColor.setBackground(gCalc.getDefaultNodeReverseSelectionColor());
		selectedEdgeColor.setBackground(gCalc.getDefaultEdgeSelectionColor());
		reverseSelectedEdgeColor.setBackground(gCalc.getDefaultEdgeReverseSelectionColor());
		this.repaint();
	}

	private class BackColorListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				final Color newBG = (Color) backColor.getValue();
				vmm.getVisualStyle().getGlobalAppearanceCalculator()
						.setDefaultBackgroundColor(newBG);
			}
		}
	}

	private class SelectedNodeColorListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				final Color newSelectedNode = (Color) selectedNodeColor
						.getValue();
				vmm.getVisualStyle().getGlobalAppearanceCalculator()
						.setDefaultNodeSelectionColor(newSelectedNode);
			}
		}
	}

	private class ReverseSelectedNodeColorListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				final Color newReverseSelectedNode = (Color) reverseSelectedNodeColor
						.getValue();
				vmm.getVisualStyle().getGlobalAppearanceCalculator()
						.setDefaultNodeReverseSelectionColor(
								newReverseSelectedNode);
			}
		}
	}

	private class SelectedEdgeColorListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				final Color newSelectedEdge = (Color) selectedEdgeColor
						.getValue();
				vmm.getVisualStyle().getGlobalAppearanceCalculator()
						.setDefaultEdgeSelectionColor(newSelectedEdge);
			}
		}
	}

	private class ReverseSelectedEdgeColorListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				final Color newReverseSelectedEdge = (Color) reverseSelectedEdgeColor
						.getValue();
				vmm.getVisualStyle().getGlobalAppearanceCalculator()
						.setDefaultEdgeReverseSelectionColor(
								newReverseSelectedEdge);
			}
		}
	}

}

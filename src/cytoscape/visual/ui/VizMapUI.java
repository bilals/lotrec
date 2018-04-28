/*
 File: VizMapUI.java 
 
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

//------------------------------------------------------------------------------
// $Revision: 9356 $
// $Date: 2007-01-10 16:50:16 -0800 (Wed, 10 Jan 2007) $
// $Author: pwang $
//------------------------------------------------------------------------------
package cytoscape.visual.ui;

//------------------------------------------------------------------------------
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cytoscape.CyNetwork;
import cytoscape.CyNetworkEvent;
import cytoscape.CyNetworkListener;
import cytoscape.Cytoscape;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;
import cytoscape.util.SwingWorker;

//------------------------------------------------------------------------------

/**
 * Primary UI class for the Set Visual Properties dialog box.
 */
public class VizMapUI extends JDialog implements CyNetworkListener {
	// constants for attribute types, one for each tab
	public static final byte NODE_COLOR = 0;
	public static final byte NODE_BORDER_COLOR = 1;
	public static final byte NODE_LINETYPE = 2;
	public static final byte NODE_SHAPE = 3;
	public static final byte NODE_SIZE = 4;
	public static final byte NODE_LABEL = 5;
	public static final byte NODE_LABEL_FONT = 6;
	public static final byte NODE_LABEL_COLOR = 7;
	public static final byte EDGE_COLOR = 8;
	public static final byte EDGE_LINETYPE = 9;
	public static final byte EDGE_SRCARROW = 10;
	public static final byte EDGE_TGTARROW = 11;
	public static final byte EDGE_LABEL = 12;
	public static final byte EDGE_LABEL_FONT = 13;
	public static final byte NODE_TOOLTIP = 14;
	public static final byte EDGE_TOOLTIP = 15;

	// for node and edge label position... if you hadn't already guessed
	public static final byte NODE_LABEL_POSITION = 16;
	public static final byte EDGE_LABEL_POSITION = 17;

	public static final byte EDGE_LABEL_COLOR = 18;

	// for creating VizMapTabs with font face/size on one page
	public static final byte NODE_FONT_FACE = 122;
	public static final byte NODE_FONT_SIZE = 123;
	public static final byte EDGE_FONT_FACE = 124;
	public static final byte EDGE_FONT_SIZE = 125;

	// for creating VizMapTabs with locked node height/width
	public static final byte NODE_HEIGHT = 126;
	public static final byte NODE_WIDTH = 127;

	// VisualMappingManager for the graph.
	protected VisualMappingManager VMM;
	/** The content pane for the dialog */
	private JPanel mainPane;
	// private GridBagGroup mainGBG;
	private JPanel actionButtonsPanel, attrSelectorPanel;
	/** The content pane for the JTabbedPanes */
	private JPanel tabPaneContainer;
	/** Keeps track of contained tabs */
	// private VizMapTab[] tabs;
	private List<VizMapTab> tabs;
	/**
	 * All known VisualStyles
	 */
	protected Collection styles;
	/**
	 * StyleSelector sub-dialog
	 */
	protected StyleSelector styleSelector;
	// kludge!
	private boolean initialized = false;

	/**
	 * Make and display the Set Visual Properties UI.
	 * 
	 * @param VMM
	 *            VisualMappingManager for the graph
	 */
	public VizMapUI(VisualMappingManager VMM, JFrame mainFrame) {
		super(mainFrame, "VizMapper\u2122");
		this.VMM = VMM;

		// Register to Listen for Changes in Underlying Network.
		// Code Added by Ethan Cerami: April 2, 2004.
		CyNetwork cyNetwork = VMM.getNetwork();
		cyNetwork.addCyNetworkListener(this);

		// this.mainGBG = new GridBagGroup();
		// this.mainPane = mainGBG.panel;
		mainPane = new JPanel(new GridBagLayout());
		// MiscGB.pad(mainGBG.constraints, 2, 2);
		// MiscGB.inset(mainGBG.constraints, 3);
		this.tabs = new ArrayList<VizMapTab>();
		// this.tabs = new VizMapTab[EDGE_LABEL_FONT + 1];
		this.tabPaneContainer = new JPanel(new java.awt.GridLayout(), false);

		JTabbedPane nodePane = new JTabbedPane();
		JTabbedPane edgePane = new JTabbedPane();

		nodePane.setName("nodePane");
		edgePane.setName("edgePane");
		
		// add panels to tabbed panes
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 0, VMM,
				NODE_COLOR));
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 1, VMM,
				NODE_BORDER_COLOR));
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 2, VMM,
				NODE_LINETYPE));
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 3, VMM,
				NODE_SHAPE));
		addTab(tabs, nodePane, new VizMapSizeTab(this, nodePane, 4, VMM,
				NODE_SIZE));
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 5, VMM,
				NODE_LABEL));
		addTab(tabs, nodePane, new VizMapFontTab(this, nodePane, 6, VMM,
				NODE_LABEL_FONT));
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 7, VMM,
				NODE_LABEL_COLOR));
		addTab(tabs, nodePane, new VizMapAttrTab(this, nodePane, 8, VMM,
				NODE_LABEL_POSITION));

		addTab(tabs, edgePane, new VizMapAttrTab(this, edgePane, 0, VMM,
				EDGE_COLOR));
		addTab(tabs, edgePane, new VizMapAttrTab(this, edgePane, 1, VMM,
				EDGE_LINETYPE));
		addTab(tabs, edgePane, new VizMapAttrTab(this, edgePane, 2, VMM,
				EDGE_SRCARROW));
		addTab(tabs, edgePane, new VizMapAttrTab(this, edgePane, 3, VMM,
				EDGE_TGTARROW));
		addTab(tabs, edgePane, new VizMapAttrTab(this, edgePane, 4, VMM,
				EDGE_LABEL));
		addTab(tabs, edgePane, new VizMapFontTab(this, edgePane, 5, VMM,
				EDGE_LABEL_FONT));
		addTab(tabs, edgePane, new VizMapAttrTab(this, edgePane, 6, VMM,
				EDGE_LABEL_COLOR));

		// global default pane
		JPanel defaultPane = new DefaultPanel(this, VMM);

		// node/edge/default selector
		ButtonGroup grp = new ButtonGroup();

		JToggleButton nodeSelect = new JToggleButton("Node Attributes", true);
		JToggleButton edgeSelect = new JToggleButton("Edge Attributes", false);
		JToggleButton defSelect = new JToggleButton("Global Defaults", false);
		grp.add(defSelect);
		grp.add(nodeSelect);
		grp.add(edgeSelect);

		nodeSelect.addActionListener(new AttrSelector(nodePane));
		edgeSelect.addActionListener(new AttrSelector(edgePane));
		defSelect.addActionListener(new AttrSelector(defaultPane));

		this.attrSelectorPanel = new JPanel(new FlowLayout(), false);
		attrSelectorPanel.add(nodeSelect);
		attrSelectorPanel.add(edgeSelect);
		attrSelectorPanel.add(defSelect);
		// attrSelectorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		this.styleSelector = new StyleSelector(this, mainFrame);

		// MiscGB.insert(mainGBG, vizStylePanel, 0, 0, 1, 1, 1, 0,
		// GridBagConstraints.HORIZONTAL);
		// MiscGB.insert(mainGBG, attrSelectorPanel, 0, 0, 1, 1, 1, 0,
		// GridBagConstraints.HORIZONTAL);
		// MiscGB.insert(mainGBG, tabPaneContainer, 0, 1, 1, 1, 1, 1,
		// GridBagConstraints.BOTH);

		// add apply & cancel button
		this.actionButtonsPanel = new JPanel();

		JButton applyButton = new JButton("Apply to Network");
		applyButton.addActionListener(new ApplyAction());
		JButton closeButton = new JButton("Close");

		closeButton.addActionListener(new CloseAction());
		actionButtonsPanel.add(applyButton);
		actionButtonsPanel.add(closeButton);

		// MiscGB.insert(mainGBG, actionButtonsPanel, 0, 3, 1, 1, 1, 0,
		// GridBagConstraints.HORIZONTAL);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
		gridBagConstraints.weightx = 1.0;
		mainPane.add(attrSelectorPanel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		mainPane.add(tabPaneContainer, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		mainPane.add(actionButtonsPanel, gridBagConstraints);

		setContentPane(mainPane);
		pack();
		nodeSelect.doClick();
		initialized = true;
	}

	private void addTab(List<VizMapTab> tabs, JTabbedPane pane, VizMapTab t) {
		tabs.add(t);
		pane.add(t);
	}

	public StyleSelector getStyleSelector() {
		return this.styleSelector;
	}

	/**
	 * StyleSelector implements the style selection control. It has been
	 * separated visually from the dialog shown by VizMapUI, but is still very
	 * much a part of the parent VizMapUI. Keeping StyleSelector as an internal
	 * class of VizMapUI makes the program simpler since many variables must be
	 * kept synchronized between the two classes.
	 */
	public class StyleSelector extends JDialog implements ChangeListener {
		/**
		 * Reference to catalog
		 */
		protected CalculatorCatalog catalog;

		/**
		 * Model for combo boxes
		 */
		protected DefaultComboBoxModel styleComboModel = new DefaultComboBoxModel();

		/**
		 * Combo box for style selection
		 */
		protected JComboBox styleComboBox = new JComboBox(styleComboModel);

		/**
		 * Duplicate combo box for style selection - used in toolbar
		 */
		protected JComboBox styleComboBoxDupe = new JComboBox(styleComboModel);

		/**
		 * GridBagGroup for layout
		 */
		protected GridBagGroup styleGBG;

		/**
		 * Currently selected style
		 */
		protected VisualStyle currentStyle;

		/**
		 * Reference to style definition UI
		 */
		protected VizMapUI styleDefUI;

		/**
		 * Reference back to self for action listeners
		 */
		protected StyleSelector myself;

		/**
		 * Lazily create visual style parameter UI.
		 */
		protected boolean styleDefNeedsUpdate = true;

		/**
		 * Flag to trap events triggered by myself
		 */
		protected boolean rebuilding = false;

		protected JFrame mainFrame;

		protected StyleSelector(VizMapUI styleDef, JFrame mainFrame) {
			super(mainFrame, "VizMapper\u2122");
			this.mainFrame = mainFrame;
			this.currentStyle = VMM.getVisualStyle();
			this.styleDefUI = styleDef;
			this.catalog = VMM.getCalculatorCatalog();
			styles = catalog.getVisualStyles();
			this.styleGBG = new GridBagGroup("Visual Styles");
			this.myself = this;
			// attach listener
			StyleSelectionListener listen = new StyleSelectionListener();
			this.styleComboBox.addItemListener(listen);
			// the duplicate styleComboBox doesn't need a listener because
			// JComboBox fires ItemEvents when the underlying model changes.
			// this.styleComboBoxDupe.addItemListener(listen);
			String comboBoxHelp = "Change the current visual style";
			this.styleComboBox.setToolTipText(comboBoxHelp);
			this.styleComboBoxDupe.setToolTipText(comboBoxHelp);
			MiscGB.pad(styleGBG.constraints, 2, 2);
			MiscGB.inset(styleGBG.constraints, 3);

			resetStyles();

			// new style button
			JButton newStyle = new JButton("New");
			newStyle.addActionListener(new NewStyleListener());
			MiscGB.insert(styleGBG, newStyle, 0, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			newStyle.setToolTipText("Create a new style");

			// duplicate style button
			JButton dupeStyle = new JButton("Duplicate");
			dupeStyle.addActionListener(new DupeStyleListener());
			MiscGB.insert(styleGBG, dupeStyle, 1, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			dupeStyle.setToolTipText("Duplicate the current style");

			// rename style button
			JButton renStyle = new JButton("Rename");
			renStyle.addActionListener(new RenameStyleListener());
			MiscGB.insert(styleGBG, renStyle, 2, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			renStyle.setToolTipText("Rename the current style");

			// remove style button
			JButton rmStyle = new JButton("Delete");
			rmStyle.addActionListener(new RemoveStyleListener());
			MiscGB.insert(styleGBG, rmStyle, 3, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			rmStyle.setToolTipText("Delete the current style");

			// create legend button

			JButton createLegend = new JButton("Create Legend");
			createLegend.addActionListener(new LegendListener(this));
			MiscGB.insert(styleGBG, createLegend, 4, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			createLegend
					.setToolTipText("Create a figure legend for the selected visual style");

			// close button
			JButton closeBut = new JButton("Close");
			closeBut.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			MiscGB.insert(styleGBG, closeBut, 5, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			closeBut.setToolTipText("Close this dialog");

			// define style button
			JButton defStyle = new JButton("Define");
			defStyle.addActionListener(new DefStyleListener());
			MiscGB.insert(styleGBG, defStyle, 5, 0, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			defStyle.setToolTipText("Change the current style's settings");

			MiscGB.insert(this.styleGBG, this.styleComboBox, 0, 0, 5, 1, 1, 0,
					GridBagConstraints.HORIZONTAL);
			setContentPane(styleGBG.panel);
			styleGBG.panel
					.setToolTipText("Visual styles are a collection of attribute mappings.");
			pack();
		}

		public String getStyleName(VisualStyle s) {
			String suggestedName = null;
			if (s != null)
				suggestedName = this.catalog.checkVisualStyleName(s.getName());
			// keep prompting for input until user cancels or we get a valid
			// name
			while (true) {
				String ret = (String) JOptionPane
						.showInputDialog(myself, "Name for new visual style",
								"Visual Style Name Input",
								JOptionPane.QUESTION_MESSAGE, null, null,
								suggestedName);
				if (ret == null) {
					return null;
				}
				String newName = catalog.checkVisualStyleName(ret);
				if (newName.equals(ret))
					return ret;
				int alt = JOptionPane.showConfirmDialog(myself,
						"Visual style with name " + ret
								+ " already exists,\nrename to " + newName
								+ " okay?", "Duplicate visual style name",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
						null);
				if (alt == JOptionPane.YES_OPTION)
					return newName;
			}
		}

		protected class DefStyleListener extends AbstractAction {
			public void actionPerformed(ActionEvent e) {
				if (styleDefNeedsUpdate) {
					styleDefUI.visualStyleChanged();
					styleDefNeedsUpdate = false;
				}
				styleDefUI.setVisible(true);
			}
		}

		protected class LegendListener extends AbstractAction {
			JDialog parent;

			public LegendListener(JDialog p) {
				parent = p;
			}

			public void actionPerformed(ActionEvent e) {
				final SwingWorker worker = new SwingWorker() {
					public Object construct() {
						LegendDialog ld = new LegendDialog(parent, currentStyle);
						ld.setVisible(true);
						return null;
					}
				};
				worker.start();
			}
		}

		protected class NewStyleListener extends AbstractAction {
			public void actionPerformed(ActionEvent e) {
				// just create a new style with all mappers set to none
				// get a name for the new calculator
				String name = getStyleName(null);
				if (name == null) {
					return;
				}
				// create the new style
				currentStyle = new VisualStyle(name);
				// add it to the catalog
				catalog.addVisualStyle(currentStyle);
				resetStyles(); // rebuild the combo box
				// set the new style in VMM, which will trigger an update
				// to the current selection in the combo box
				VMM.setVisualStyle(currentStyle);

				Cytoscape.getNetworkView(
						Cytoscape.getCurrentNetwork().getIdentifier())
						.setVisualStyle(currentStyle.getName());

				// this applies the new style to the graph
				VMM.getNetworkView().redrawGraph(false, true);

			}
		}

		/**
		 * Rename a Visual Style<br>
		 * 
		 */
		protected class RenameStyleListener extends AbstractAction {
			public void actionPerformed(ActionEvent e) {
				String oldName = currentStyle.getName();
				String name = getStyleName(currentStyle);

				// System.out.println("******** Old VS name = " + oldName + ",
				// New VS name = " + name);

				if (name == null) {
					return;
				}

				// no need to inform the VMM, since only the name changed <--
				// This is VERY wrong!
				// We need to update calatog AND VMM.
				currentStyle.setName(name);

				catalog.removeVisualStyle(oldName);
				catalog.addVisualStyle(currentStyle);
				resetStyles(); // rebuild the combo box

				VMM.setVisualStyle(currentStyle);
			}
		}

		protected class RemoveStyleListener extends AbstractAction {
			public void actionPerformed(ActionEvent e) {
				if (styles.size() == 1) {
					JOptionPane.showMessageDialog(myself,
							"There must be at least one visual style",
							"Cannot remove style", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// make sure the user really wants to do this
				String styleName = currentStyle.getName();
				String checkString = "Are you sure you want to permanently delete"
						+ " the visual style named '" + styleName + "'?";
				int ich = JOptionPane.showConfirmDialog(myself, checkString,
						"Confirm Delete Style", JOptionPane.YES_NO_OPTION);
				if (ich == JOptionPane.YES_OPTION) {
					catalog.removeVisualStyle(currentStyle.getName());
					// try to switch to the default style
					currentStyle = catalog.getVisualStyle("default");
					if (currentStyle == null) {// not found, pick the first
						// valid style
						currentStyle = (VisualStyle) styles.iterator().next();
					}
					resetStyles(); // rebuild the combo box
					// set the new style in VMM, which will trigger an update
					// to the current selection in the combo box
					VMM.setVisualStyle(currentStyle);
					// this applies the new style to the graph
					VMM.getNetworkView().redrawGraph(false, true);
				}
			}
		}

		protected class DupeStyleListener extends AbstractAction {
			public void actionPerformed(ActionEvent e) {
				VisualStyle clone = null;
				try {
					clone = (VisualStyle) currentStyle.clone();
				} catch (CloneNotSupportedException exc) {
					System.err.println("Clone not supported exception!");
				}
				// get new name for clone
				String newName = getStyleName(clone);
				if (newName == null) {
					return;
				}
				clone.setName(newName);
				// add new style to the catalog
				catalog.addVisualStyle(clone);
				currentStyle = clone;
				resetStyles(); // rebuild the combo box
				// set the new style in VMM, which will trigger an update
				// to the current selection in the combo box
				VMM.setVisualStyle(currentStyle);
				// this applies the new style to the graph
				VMM.getNetworkView().redrawGraph(false, true);
			}
		}

		protected class StyleSelectionListener implements ItemListener {
			/**
			 * There's three ways this method can get called. First, it's
			 * triggered when the refreshStyleComboBox method rebuilds the combo
			 * box. These are meaningless events that are trapped by the
			 * 'rebuilding' flag set by refreshStyleComboBox that tells this
			 * method to skip the event.
			 * <P>
			 * 
			 * Second, when the visual style is changed in the underlying
			 * VisualMappingManager, it calls the stateChanged method. That
			 * method updates the current style and selects it in the combo box,
			 * triggering this method. Since there's nothing for this method to
			 * do in this case, we recognize this by doing nothing if the 'new'
			 * style is the same as the current style.
			 * <P>
			 * 
			 * The third case is when the user actually selected a style from
			 * the user interface, and this method is supposed to make a change
			 * in the underlying VisualMappingManager. In that case (after
			 * checking that the new style is really new) this method updates
			 * the current style and changes the style in the underlying
			 * VisualMappingManager, which triggers the stateChanged method,
			 * which does nothing since this method has already updated the
			 * current style.
			 */
			public void itemStateChanged(ItemEvent e) {
				if (rebuilding) {
					return;
				}
				if (e.getStateChange() == ItemEvent.SELECTED) {
					VisualStyle newStyle = (VisualStyle) ((JComboBox) e
							.getSource()).getSelectedItem();
					if (newStyle != currentStyle && newStyle != null) {
						// this call triggers an event caught by a listener in
						// this class
						// that updates the currentStyle held by this class
						VMM.setVisualStyle(newStyle);

						// this call will apply the new visual style
						// VMM.getNetworkView().redrawGraph(false, true);
						Cytoscape.getNetworkView(
								Cytoscape.getCurrentNetwork().getIdentifier())
								.setVisualStyle(newStyle.getName());

						Cytoscape.getCurrentNetworkView().redrawGraph(false,
								true);

						// Need to update CyNetworkView's VS
						// Cytoscape.getCurrentNetworkView().setVisualStyle(
						// newStyle.getName());

					}
				}
			}
		}

		/**
		 * Retrieve copy of style selection combo box for toolbar
		 */
		public JComboBox getToolbarComboBox() {
			return this.styleComboBoxDupe;
		}

		/**
		 * Update the style combo box model. This method only rebuilds the combo
		 * box: it does not trigger any events.
		 */
		protected void refreshStyleComboBox(VisualStyle selectedStyle) {
			/*
			 * When we remove and add the elements in the following code, it
			 * triggers events caught by the StyleSelectionListener. To get
			 * around this, we set a boolean flag to tell the listener to ignore
			 * these events
			 */
			this.rebuilding = true;
			this.styleComboModel.removeAllElements();
			for (Iterator styleIter = styles.iterator(); styleIter.hasNext();) {
				this.styleComboModel.addElement(styleIter.next());
			}
			if (selectedStyle != null)
				this.styleComboModel.setSelectedItem(selectedStyle);
			else
				this.styleComboModel.setSelectedItem(currentStyle);
			this.rebuilding = false;
		}

		/**
		 * Reset the style selection controls.
		 */
		public void resetStyles() {
			// reset local style collection
			styles = catalog.getVisualStyles();
			refreshStyleComboBox(null);
		}

		public void resetStyles(String selected) {
			// reset local style collection
			styles = catalog.getVisualStyles();
			refreshStyleComboBox(catalog.getVisualStyle(selected));
		}

		/**
		 * Called when the underlying VisualMappingManger object changes
		 */
		public void stateChanged(ChangeEvent ce) {
			if (currentStyle != VMM.getVisualStyle()) {
				currentStyle = VMM.getVisualStyle();
				if (styleComboModel.getIndexOf(currentStyle) == -1) {// not
					// in
					// combo
					// box
					styleComboModel.addElement(currentStyle);
				}
				// this triggers an event from the combo box that will be
				// ignored
				// since we've already updated the current style
				styleComboModel.setSelectedItem(currentStyle);
				// let the style definition UI know it should update
				if (styleDefUI.isShowing()) {
					visualStyleChanged();
				} else {
					styleDefNeedsUpdate = true;
				}
			}
		}

	} // StyleSelector

	private class AttrSelector implements ActionListener {
		private JComponent myTab;

		private AttrSelector(JComponent myTab) {
			this.myTab = myTab;
		}

		public void actionPerformed(ActionEvent e) {
			tabPaneContainer.removeAll();
			tabPaneContainer.add(myTab);
			pack();
			repaint();
		}
	}

	// apply button action listener
	private class ApplyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			VMM.getNetworkView().redrawGraph(false, true);
			//Set the focus to emit a NETWORK_VIEW_FOCUS event for sync the BirdsEyeView
			Cytoscape.getDesktop().setFocus(VMM.getNetworkView().getIdentifier());
		}
	}

	// close button action listener
	private class CloseAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	/**
	 * When the data structures (eg. NodeAttributes, EdgeAttributes) change,
	 * refresh the UI.
	 */
	public void refreshUI() {
		for (int i = 0; i < tabs.size(); i++) {
			tabs.get(i).refreshUI();
		}
		validate();
		repaint();
	}

	/**
	 * When the currently selected visual styles changed, a new set of
	 * calculators with their corresponding interfaces must be switched into the
	 * UI.
	 */
	public void visualStyleChanged() {
		for (int i = 0; i < tabs.size(); i++) {
			tabs.get(i).visualStyleChanged();
		}
		validate();
		pack();
		repaint();
	}

	/**
	 * Due to a Java AWT design choice, {@link Component}s may belong to only
	 * one {@link Container}. This causes problems in the VizMapper when there
	 * are attributes that share calculators, such as Node Color and Node Border
	 * Color. Each VizMapTab calls this method when switching calculators to
	 * ensure that the newly selected calculator is not already selected
	 * elsewhere.
	 * 
	 * @param selectedCalc
	 *            Calculator that the calling VizMapTab is trying to switch to.
	 * @return true if calculator already selected elsewhere, false otherwise
	 */
	VizMapTab checkCalcSelected(Calculator selectedCalc) {
		if (!initialized)
			return null;
		VizMapTab selected = null;
		for (int i = 0; i < tabs.size() && (selected == null); i++) {
			VizMapTab t = tabs.get(i);
			selected = t.checkCalcSelected(selectedCalc);
		}
		return selected;
	}

	/**
	 * Ensure that the calculator to be removed isn't used in other visual
	 * styles. If it is, return the names of visual styles that are currently
	 * using it.
	 * 
	 * @param c
	 *            calculator to check usage for
	 * @return names of visual styles using the calculator
	 */
	public Vector checkCalculatorUsage(Calculator c) {
		Vector conflicts = new Vector();
		for (Iterator iter = styles.iterator(); iter.hasNext();) {
			VisualStyle vs = (VisualStyle) iter.next();
			Vector styleName = vs.checkConflictingCalculator(c);
			if (styleName.size() != 1)
				conflicts.add(styleName);
		}
		return conflicts;
	}

	/**
	 * State of Underlying CyNework has Changed. Refresh UI.
	 */
	public void onCyNetworkEvent(CyNetworkEvent event) {
		this.refreshUI();
	}
}


/*
  File: VizMapSizeTab.java 
  
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

// VizMapSizeTab.java
//--------------------------------------------------------------------------------
// $Revision: 9280 $
// $Date: 2006-12-22 17:38:38 -0800 (Fri, 22 Dec 2006) $
// $Author: pwang $
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import java.util.*;
/**
 * Create a tab for the Set Visual Properties dialog for Node Size. Because
 * node height and node width are mapped separately, some special behavior
 * needs to be implemented for node size in case the user would like to lock
 * both to the same calculator. VizMapSizeTab encapsulates three VizMapAttrTabs,
 * one for node height, one for node width, and one for locked node height/width.
 */
public class VizMapSizeTab extends VizMapTab {
    private JPanel hwPanel, lockPanel, mainPanel;

    private VizMapAttrTab height, width, size;

    private VisualMappingManager VMM;
    private NodeAppearanceCalculator nodeCalc;
    private VizMapUI mainUIDialog;

    private boolean locked;
    JCheckBox lockBox;

    /**
     *	create a new tab representing the node size. Retrieve current
     *	calculator and default settings from the VMM. Start with node height/width
     *  locked to the same calculator.
     *
     *	@param	VMM	VisualMappingManager for the window
     *  @param	tabContainer	The containing JTabbedPane
     *  @param	tabIndex	index of this tab in tabContainer
     *	@param	n	Underlying network
     *	@param	type	Must be {@link VizMapUI#NODE_SIZE}
     *
     *  @throws IllegalArgumentException if type is not {@link VizMapUI#NODE_SIZE}
     */
    public VizMapSizeTab (VizMapUI mainUI, JTabbedPane tabContainer, int tabIndex, VisualMappingManager VMM, byte type) throws IllegalArgumentException {
	//this(mainUI, tabContainer, tabIndex, VMM, type, true);
        //the above is a bug; instead of always setting the locked field to true,
        //we should get it from the node appearance calculator
        this(mainUI, tabContainer, tabIndex, VMM, type,
            VMM.getVisualStyle().getNodeAppearanceCalculator().getNodeSizeLocked());
    }

    /**
     *	create a new tab representing the node size. Retrieve current
     *	calculator and default settings from the VMM.
     *
     *	@param	VMM	VisualMappingManager for the window
     *  @param	tabContainer	The containing JTabbedPane
     *  @param	tabIndex	index of this tab in tabContainer
     *	@param	n	Underlying network
     *	@param	type	Must be {@link VizMapUI#NODE_SIZE}
     *  @param	locked	true to lock node height/width to the same calculator,
     *			false otherwise.
     *
     *  @throws IllegalArgumentException if type is not {@link VizMapUI#NODE_SIZE}
     */

    public VizMapSizeTab (VizMapUI mainUI, JTabbedPane tabContainer, int tabIndex, VisualMappingManager VMM, byte type, boolean locked) throws IllegalArgumentException {
	super(new BorderLayout(), false);

	if (type != VizMapUI.NODE_SIZE)
	    throw new IllegalArgumentException("You can only create a VizMapSizeTab for the Node Size attribute");
	
	// set the name of this component appropriately
	setName("Node Size");

        this.VMM = VMM;
	this.nodeCalc = VMM.getVisualStyle().getNodeAppearanceCalculator();
	this.mainUIDialog = mainUI;
	this.locked = locked;

	JPanel lockBoxPanel = new JPanel(false);
	this.lockBox = new JCheckBox("Lock height/width calculators");
	//lockBox.setSelected(true);
        //the above is also a bug; should set to value of locked
        lockBox.setSelected(locked);
	lockBox.addItemListener(new LockCalcListener());
	lockBoxPanel.add(lockBox);

	this.hwPanel = new JPanel(false);
	this.hwPanel.setLayout(new BoxLayout(hwPanel, BoxLayout.Y_AXIS));
	this.lockPanel = new JPanel(new GridLayout(), false);
	// generate panel for height and width
	height = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_HEIGHT);
	width = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_WIDTH);

	// Lock at the nodeAppearanceCalculator level, since can't have duplicate
	// calculators. We have to be careful to keep size and height in sync.
	size = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_SIZE);

	width.setBorder(BorderFactory.createTitledBorder(width.getName()));
	height.setBorder(BorderFactory.createTitledBorder(height.getName()));

	hwPanel.add(height);
	hwPanel.add(width);

	lockPanel.add(size);

	mainPanel = new JPanel(new GridLayout(),false);
	if (this.locked) {
	    // initially set to locked width/height display
	    mainPanel.add(lockPanel);
	}
	else {
	    // initially set to separate width/height calculators
	    mainPanel.add(hwPanel);
	}

	nodeCalc.setNodeSizeLocked(this.locked);

	add(lockBoxPanel, BorderLayout.NORTH);
	add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * LockCalcListener toggles locking on the calculators
     */
    private class LockCalcListener implements ItemListener {
	// initialize to initial state as defined in cytoscape.props
	private Calculator widthCalc = nodeCalc.getCalculator(VizMapUI.NODE_WIDTH);
	private Calculator heightCalc = nodeCalc.getCalculator(VizMapUI.NODE_HEIGHT);

	// maintain separate memoization for the locked calculator
	private Calculator lockCalc = nodeCalc.getCalculator(VizMapUI.NODE_SIZE);

	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
	    	System.out.println("node size selected");
		mainPanel.removeAll();
		mainPanel.add(lockPanel);
		locked = true;

		// set nodeSizeLocked in the NodeAppearanceCalculator
		nodeCalc.setNodeSizeLocked(true);

		// memoize currently selected calculators
		// have to clear the current calculators to prevent
		// conflicts in panels - calculators can only be selected in one
		// VizMapAttrTab
		this.widthCalc = nodeCalc.getCalculator(VizMapUI.NODE_WIDTH);
		width.setComboBox(null);

		this.heightCalc = nodeCalc.getCalculator(VizMapUI.NODE_HEIGHT);
		height.setComboBox(null);

		// set the locked calculator as memoized
		size.setComboBox(this.lockCalc);
	    }
	    else {
	    	System.out.println("node size UNselected");
		mainPanel.removeAll();
		mainPanel.add(hwPanel);
		locked = false;
		
		// set nodeSizeLocked in the NodeAppearanceCalculator
		nodeCalc.setNodeSizeLocked(false);

		// memoize currently selected calculator
		this.lockCalc = nodeCalc.getCalculator(VizMapUI.NODE_SIZE);
		size.setComboBox(null);

		// reset back to old calculators
		height.setComboBox(this.heightCalc);
		width.setComboBox(this.widthCalc);
	    }
	    
	    // redraw the panel
	    validate();
	    repaint();
	    
	    // resize the window is required
	    mainUIDialog.pack();
	    mainUIDialog.repaint();
	    
	}
    }

    public void refreshUI() {
	this.height.refreshUI();
	this.width.refreshUI();
	this.size.refreshUI();
    }

    public void visualStyleChanged() {
        //must grab the new node appearance calculator
        this.nodeCalc = VMM.getVisualStyle().getNodeAppearanceCalculator();
	this.height.visualStyleChanged();
	this.width.visualStyleChanged();
	this.size.visualStyleChanged();
        //must update the locked status and the lock box selection
        this.locked = nodeCalc.getNodeSizeLocked();
        if (this.lockBox.isSelected() != this.locked) {
            lockBox.setSelected(locked);
        }
    }

    VizMapTab checkCalcSelected(Calculator c) {
	if (this.locked) { // pass on to size
	    return size.checkCalcSelected(c);
	}
	else { // pass on to height and width
	    /* Either height or width has the calculator. Return the first non-null
	       response.
	    */
	    VizMapTab ret = height.checkCalcSelected(c);
	    if (ret == null)
		return width.checkCalcSelected(c);
	    else
		return ret;
	}
    }
}

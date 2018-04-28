
/*
  File: VizMapFontTab.java 
  
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

// VizMapFontTab.java
//--------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
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
 * Create a tab for the Set Visual Properties dialog for Node and Edge Font.
 * Because font size and face are mapped separately, the UI should integrate the
 * UI of the font size and font face calculators to present a unified UI for
 * these closely related attributes.
 */
public class VizMapFontTab extends VizMapTab {
    private VizMapAttrTab faceTab, sizeTab;

    /**
     *	create a new tab for font face and size. Retrieve current
     *	calculator and default settings from the VMM.
     *
     *	@param	VMM	VisualMappingManager for the window
     *  @param	tabContainer	The containing JTabbedPane
     *  @param	tabIndex	index of this tab in tabContainer
     *	@param	n	Underlying network
     *	@param	type	Must be {@link VizMapUI#NODE_LABEL_FONT} or
     *                  {@link VizMapUI#EDGE_LABEL_FONT}
     *
     *  @throws IllegalArgumentException if type is not {@link VizMapUI#NODE_LABEL_FONT} or {@link VizMapUI#EDGE_LABEL_FONT}
     */
    public VizMapFontTab (VizMapUI mainUI, JTabbedPane tabContainer, int tabIndex, VisualMappingManager VMM, byte type) throws IllegalArgumentException {
	super(false);
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
	//set the name of this component appropriately
	switch(type) {
	case VizMapUI.NODE_LABEL_FONT:
	    setName("Node Font");
	    this.faceTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_FONT_FACE);
	    this.sizeTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_FONT_SIZE);
	    break;
	case VizMapUI.EDGE_LABEL_FONT:
	    setName("Edge Font");
	    this.faceTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.EDGE_FONT_FACE);
	    this.sizeTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.EDGE_FONT_SIZE);
	    break;
	default:
	    throw new IllegalArgumentException("You can only create a VizMapFontTab for the Node/Edge Font attribute, called with " + type);
	}

	this.faceTab.setBorder(BorderFactory.createTitledBorder("Font Face"));
	this.sizeTab.setBorder(BorderFactory.createTitledBorder("Font Size"));

	this.add(this.faceTab);
	this.add(this.sizeTab);
    }

    public void refreshUI() {
	this.faceTab.refreshUI();
	this.sizeTab.refreshUI();
	validate();
    }

    public void visualStyleChanged() {
	this.faceTab.visualStyleChanged();
	this.sizeTab.visualStyleChanged();
    }

    VizMapTab checkCalcSelected(Calculator c) {
	// calculators not shared, just return null
	return null;
    }
}

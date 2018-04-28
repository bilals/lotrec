

/*
 File: NodeBypass.java 
 
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

package cytoscape.visual.ui;


import cytoscape.Cytoscape;
import cytoscape.visual.LineType;
import cytoscape.visual.LabelPosition;

import giny.model.Node;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;

class NodeBypass extends VizMapBypass {

    JMenuItem addMenu(Node n) {
    	graphObj = n;
	attrs = Cytoscape.getNodeAttributes();

        JMenu menu =new JMenu("Visual Mapping Bypass");
	menu.add( new JLabel("Change Node Visualization") ); 
	menu.addSeparator();
	
	addMenuItem(menu, "Fill Color", "node.fillColor", Color.class); 
	addMenuItem(menu, "Border Color", "node.borderColor", Color.class); 
	addMenuItem(menu, "Border Line Type", "node.lineType", LineType.class); 

	if ( vmm.getVisualStyle().getNodeAppearanceCalculator().getNodeSizeLocked() ) {
		addMenuItem(menu, "Size", "node.size",Double.class); 
	} else {
		addMenuItem(menu,"Width", "node.width",Double.class); 
		addMenuItem(menu,"Height", "node.height",Double.class); 
	}

	addMenuItem(menu,"Shape", "node.shape",Byte.class); 
	addMenuItem(menu,"Label", "node.label",String.class); 
	addMenuItem(menu,"Label Color", "node.labelColor",Color.class); 
	addMenuItem(menu,"Label Position", "node.labelPosition", LabelPosition.class); 
	addMenuItem(menu,"Font", "node.font",Font.class); 
	addMenuItem(menu,"Font Size", "node.fontSize",Double.class); 

	menu.addSeparator();

	addResetAllMenuItem(menu); 
	
        return menu;
    }

    protected String[] getBypassNames() {
    	String[] names = { 
	"node.fillColor"
	,"node.borderColor"
	,"node.lineType"
	,"node.size"
	,"node.width"
	,"node.height"
	,"node.shape"
	,"node.label"
	,"node.labelColor"
	,"node.labelPosition"
	,"node.font"
	,"node.fontSize" };
	return names;
    }
}

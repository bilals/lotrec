

/*
 File: VizMapBypass.java 
 
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
import cytoscape.data.CyAttributes;
import cytoscape.util.CyColorChooser;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.parsers.ObjectToString;

import giny.model.GraphObject;

import java.awt.Frame;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;

/** 
 * An abstract class providing common methods and data structures to the 
 * Node and Edge bypass classes.
 */
abstract class VizMapBypass {

	protected Frame parent = Cytoscape.getDesktop();

	protected VisualMappingManager vmm = Cytoscape.getVisualMappingManager();

	protected CyAttributes attrs = null; 

	protected GraphObject graphObj = null;

	abstract protected String[] getBypassNames(); 

	protected void addResetAllMenuItem(JMenu menu) {
		JMenuItem jmi = new JMenuItem (new AbstractAction("Reset All") {
			public void actionPerformed (ActionEvent e) {
				String[] names = getBypassNames();
				String id = graphObj.getIdentifier();
				for (String attrName : names)
					if ( attrs.hasAttribute(id,attrName) )
						attrs.deleteAttribute(id,attrName);
				vmm.getNetworkView().redrawGraph(false, true);
			}
		});
		menu.add( jmi );
	}

	protected void addResetMenuItem(JMenu menu, final String title, final String attrName) {
		JMenuItem jmi = new JMenuItem (new AbstractAction("[ Reset " + title + " ]") {
			public void actionPerformed (ActionEvent e) {
				String id = graphObj.getIdentifier();
				if ( attrs.hasAttribute(id,attrName) )
					attrs.deleteAttribute(id,attrName);
				vmm.getNetworkView().redrawGraph(false, true);
			}
		});
		menu.add( jmi );
	}


	protected void addMenuItem(JMenu menu, final String title, final String attrName, final Class c) {

		JMenuItem jmi = new JCheckBoxMenuItem (new AbstractAction(title) {
			public void actionPerformed (ActionEvent e) {
				Object obj = getBypassValue(title,c);
				if ( obj == null )
					return;
				String val = ObjectToString.getStringValue(obj);
				attrs.setAttribute(graphObj.getIdentifier(),attrName,val);
				vmm.getNetworkView().redrawGraph(false, true);
			}

			private Object getBypassValue(String title, Class c) {
				if ( c == Color.class ) {
					return CyColorChooser.showDialog(parent,"Choose " + title,null);
				} else if ( c == Double.class ) {
					return PopupStringChooser.showDialog(parent,"Choose " + title,"Input a double:",
									     null,ValueDisplayer.DOUBLE);
				} else if ( c == Integer.class ) {
					return PopupStringChooser.showDialog(parent,"Choose " + title,"Input an integer:",
									     null,ValueDisplayer.INT);
				} else if ( c == Byte.class ) {
					IconSupport is = new IconSupport(null,ValueDisplayer.NODESHAPE);
					PopupIconChooser chooser = new PopupIconChooser("Choose " + title,null,
											is.getIcons(),null,parent);
					return chooser.showDialog();
				} else if ( c == Arrow.class ) {
					IconSupport is = new IconSupport(null,ValueDisplayer.ARROW);
					PopupIconChooser chooser = new PopupIconChooser("Choose " + title,null,
											is.getIcons(),null,parent);
					return chooser.showDialog();
				} else if ( c == LineType.class ) {
					IconSupport is = new IconSupport(null,ValueDisplayer.LINETYPE);
					PopupIconChooser chooser = new PopupIconChooser("Choose " + title,null,
											is.getIcons(),null,parent);
					return chooser.showDialog();
				} else if ( c == String.class ) {
					return PopupStringChooser.showDialog(parent,"Choose " + title,"Input a String:",
									     null,ValueDisplayer.STRING);
				} else if ( c == LabelPosition.class ) {
					return PopupLabelPositionChooser.showDialog(parent,null);
				} else if ( c == Font.class ) {
					return PopupFontChooser.showDialog(parent,null);
				}

				return null;
			}
		}); 

		menu.add(jmi);

		String attrString = attrs.getStringAttribute(graphObj.getIdentifier(),attrName);
		if ( attrString == null || attrString.length() == 0 )
			jmi.setSelected(false);
		else {
			jmi.setSelected(true);
			addResetMenuItem(menu, title, attrName);
		}

	}
}

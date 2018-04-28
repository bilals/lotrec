
/*
  File: LabelTextPanel.java 
  
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

// LabelTextPanel.java


// ---------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
// ---------------------------------------------------------------------------------------
package cytoscape.dialogs;

// ---------------------------------------------------------------------------------------
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.MutableString;

import cytoscape.visual.calculators.AbstractCalculator;

// --------------------------------------------------------------------------------------
public class LabelTextPanel extends JPanel {

	String[] attributeNames;
	MutableString nodeLabelKey;
	JComboBox theBox;

	// --------------------------------------------------------------------------------------
	public LabelTextPanel(CyAttributes nodeAttribs, MutableString writeHere) {
		super();
		attributeNames = nodeAttribs.getAttributeNames();
		nodeLabelKey = writeHere;

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);

		// using Model so we can manually add "canonicalName" at the end.
		DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
		for (int i = 0; i < attributeNames.length; i++)
			boxModel.addElement(new String(attributeNames[i]));
		//boxModel.addElement(new String("canonicalName"));
		boxModel.addElement(AbstractCalculator.ID);
		theBox = new JComboBox(boxModel);
		theBox.setSelectedItem(nodeLabelKey.getString());
		theBox.addActionListener(new BoxAction());

		JLabel label = new JLabel("Node Label: ");
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(label, c);
		this.add(label);
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(theBox, c);
		this.add(theBox);

	} // LabelTextPanel ctor

	public class BoxAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			JComboBox jcb = (JComboBox) e.getSource();
			nodeLabelKey.setString((String) jcb.getSelectedItem());
		}
	}

} // class LabelTextPanel


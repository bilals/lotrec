
/*
  File: EdgeAttributesPopupDetails.java 
  
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

//


// EdgeAttributesPopupDetails.java
//
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//
package cytoscape.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cytoscape.data.CyAttributes;

/**
 * This class provides a detailed list of attribute information for a given
 * edge.
 */
public class EdgeAttributesPopupDetails extends JDialog {

	public EdgeAttributesPopupDetails(Frame parentFrame, String name,
			CyAttributes edgeAttributes) {
		super(parentFrame, "Edge Attributes - " + name, false);

		JScrollPane scrollPanel = new JScrollPane(getContentComponent(
				edgeAttributes, name));

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKAction(this));
		buttonPanel.add(okButton, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(panel);
	}

	protected Component getContentComponent(CyAttributes edgeAttributes,
			String name) {

		String contents = name;

		if (name.length() == 0) {
			contents = "Unable to locate attributes for selected edge";
		} else {
			String attributes[] = edgeAttributes.getAttributeNames();
			for (int i = 0; i < attributes.length; i++) {
				// Object value = edgeAttributes.getValue(attributes[i], name);
				Object value = null;
				byte attrType = edgeAttributes.getType(attributes[i]);
				if (attrType == CyAttributes.TYPE_BOOLEAN) {
					value = edgeAttributes.getBooleanAttribute(attributes[i],
							name);
				} else if (attrType == CyAttributes.TYPE_FLOATING) {
					value = edgeAttributes.getDoubleAttribute(attributes[i],
							name);
				} else if (attrType == CyAttributes.TYPE_INTEGER) {
					value = edgeAttributes.getIntegerAttribute(attributes[i],
							name);
				} else if (attrType == CyAttributes.TYPE_STRING) {
					value = edgeAttributes.getStringAttribute(attributes[i],
							name);
				}

				if (value != null)
					contents += "\n\n" + attributes[i] + ":\n" + value;
			}
		}

		JTextArea textArea = new JTextArea(contents, 8, 40);

		return textArea;
	}

	protected class OKAction extends AbstractAction {
		private JDialog dialog;

		OKAction(JDialog dialog) {
			super("");
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
		}
	}
}

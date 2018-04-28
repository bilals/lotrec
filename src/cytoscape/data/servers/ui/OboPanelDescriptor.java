/*
 File: BioDataServerPanel6Descriptor.java 
 
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

package cytoscape.data.servers.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.nexes.wizard.WizardPanelDescriptor;


/*
 * This panel is for new Gene Ontology data formats. Ask user a OBO file and
 * multiple Gene Association Files.
 */
public class OboPanelDescriptor extends WizardPanelDescriptor implements
		ActionListener {

	public static final String IDENTIFIER = "OBO_PANEL";

	OboPanel oboPanel;

	public OboPanelDescriptor() {

		oboPanel = new OboPanel();
		oboPanel.addButtonActionListener(this);

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(oboPanel);

	}

	public Object getNextPanelDescriptor() {
		return AnotationPanelDescriptor.IDENTIFIER;
		// return BioDataServerPanel3Descriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return SelectFormatPanelDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		File oboFile = oboPanel.getOboFile();
		if (oboFile != null && oboFile.canRead()) {
			getWizard().setNextFinishButtonEnabled(true);
		} else {
			getWizard().setNextFinishButtonEnabled(false);
		}
	}
	
	protected File getOboFile() {
		return oboPanel.getOboFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * Handle actions
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("selectOboButton")) {
			oboPanel.selectButtonActionPerformed();

			File oboFile = oboPanel.getOboFile();
			if (oboFile != null && oboFile.canRead()) {
				getWizard().setNextFinishButtonEnabled(true);
			}
		}
	}
}

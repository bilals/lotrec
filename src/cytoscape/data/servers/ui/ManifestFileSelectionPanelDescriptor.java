/*
 File: BioDataServerPanel4Descriptor.java 
 
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


public class ManifestFileSelectionPanelDescriptor extends WizardPanelDescriptor
		implements ActionListener {

	public static final String IDENTIFIER = "SELECT_OLDFILE_PANEL";

	ManifestfileSelectionPanel manifestPanel;
	String manifestFileName;
	private int finalState;

	public ManifestFileSelectionPanelDescriptor() {
		finalState = 0;
		manifestFileName = null;
		manifestPanel = new ManifestfileSelectionPanel();
		manifestPanel.addSelectButtonActionListener(this);

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(manifestPanel);
	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	// back leads to the 1st panel.
	public Object getBackPanelDescriptor() {
		return SelectFormatPanelDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		getWizard().setNextFinishButtonEnabled(false);
		getWizard().setBackButtonEnabled(true);

	}

	private void setNextButtonAccordingToFileChooser(boolean ma) {
		if (ma == false)
			getWizard().setNextFinishButtonEnabled(false);
		else
			getWizard().setNextFinishButtonEnabled(true);
	}

	public String getManifestFileName() {
		return manifestFileName;
	}

	public int getFinalState() {
		// if null, it means new file format are created.
		if (manifestFileName == null) {
			finalState = 1; // old file loaded
		}
		return finalState;
	}

	public void actionPerformed(ActionEvent e) {
		manifestPanel.createManifestFileChooser();
		File manifestFile = manifestPanel.getManifestFile(true);
		manifestPanel.setManifestFileName(manifestFile.getPath());
		manifestFileName = manifestPanel.getManifestFileName();
		setNextButtonAccordingToFileChooser(true);
	}

	public void aboutToHidePanel() {
		// Can do something here, but we've chosen not not.
	}
}

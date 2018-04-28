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
import java.util.Map;

import com.nexes.wizard.WizardPanelDescriptor;


/*
 * This panel is for new Gene Ontology data formats. Ask user a OBO file and
 * multiple Gene Association Files.
 */
public class AnotationPanelDescriptor extends WizardPanelDescriptor implements
		ActionListener {

	// Name of the panel
	public static final String IDENTIFIER = "ANNOTATION_CHOOSE_PANEL";

	AnotationPanel anotationPanel;
	boolean gaFlag;
	boolean flip;

	String manifestFullPath;
	String speciesName = null;

	public AnotationPanelDescriptor() {

		gaFlag = false;
		flip = false;
		manifestFullPath = null;

		anotationPanel = new AnotationPanel();
		anotationPanel.addButtonActionListener(this);

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(anotationPanel);

	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	public Object getBackPanelDescriptor() {
		return OboPanelDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		if(anotationPanel.getGAFiles().size() == 0) {
			getWizard().setNextFinishButtonEnabled(false);
		} else {
			getWizard().setNextFinishButtonEnabled(true);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * Handle actions
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("Annotation button pushed.");
		anotationPanel.gaButtonMouseClicked();
		if (anotationPanel.isFilesSelected() == true) {
			getWizard().setNextFinishButtonEnabled(true);
		}
	}


	/*
	 * Create a temp. file called auto_generated_manifest. This manifest file is
	 * different from the old manifest. By writing proper parser in other
	 * classes, it can store arbitrary many arguments.
	 */
	
	public Map getAnotationFiles() {
		return anotationPanel.getGAFiles();
	}

	public boolean isFlip() {
		return anotationPanel.getFlipCheckBoxStatus();
	}

	public String getManifestName() {
		return manifestFullPath;
	}

}
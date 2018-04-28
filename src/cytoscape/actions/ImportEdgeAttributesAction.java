/*
 File: ImportEdgeAttributesAction.java 
 
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

// $Revision: 9191 $
// $Date: 2006-12-15 17:57:18 -0800 (Fri, 15 Dec 2006) $
// $Author: kono $
package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of edge / edge attributes from the GUI
 */

public class ImportEdgeAttributesAction extends CytoscapeAction {

	/**
	 * Constructor.
	 */
	public ImportEdgeAttributesAction() {
		super("Edge Attributes...");
		setPreferredMenu("File.Import");
	}

	/**
	 * User Initiated Request.
	 * 
	 * @param e
	 *            Action Event.
	 */
	public void actionPerformed(ActionEvent e) {

		// Use a Default CyFileFilter: enables user to select any file type.
		CyFileFilter nf = new CyFileFilter();

		// get the file name
		File[] files = FileUtil.getFiles("Import Edge Attributes",
				FileUtil.LOAD, new CyFileFilter[] { nf });

		if (files != null) {

			// Create Load Attributes Task
			ImportAttributesTask task = new ImportAttributesTask(files,
					ImportAttributesTask.EDGE_ATTRIBUTES);

			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			// Execute Task in New Thread; pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		}
	}
}

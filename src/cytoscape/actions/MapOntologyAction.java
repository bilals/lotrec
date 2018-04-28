/*
 File: ImportBioDataServerAction.java 
 
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

// $Revision: 8887 $
// $Date: 2006-11-20 17:50:13 -0800 (Mon, 20 Nov 2006) $
// $Author: mes $
package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.data.annotation.OntologyMapperDialog;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;

/*
 * Ontology Mapper will be invoked from this action.
 */
public class MapOntologyAction extends CytoscapeAction implements PropertyChangeListener {

	public MapOntologyAction() {
		super("Map Ontology...");
		initMenu();
	}

	public MapOntologyAction(boolean isMenu, ImageIcon icon) {
		super("Map Ontology...", icon);
		initMenu();
	}

	private void initMenu() {
		super.setEnabled(false);
		setPreferredMenu("File.Import.Ontology");
		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (Cytoscape.getCyNodesList().size() == 0
				|| Cytoscape.getBioDataServer() == null) {
			JOptionPane
					.showMessageDialog(
							Cytoscape.getDesktop(),
							"There is no network data in memory.\n"
									+ "Please import/create a network before using GO Mapper.",
							"No Networks in Memory", JOptionPane.ERROR_MESSAGE);

			return;
		}

		// Create Task
		MapOntologyTask task = new MapOntologyTask();

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();

		jTaskConfig.displayCancelButton(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	public void propertyChange(PropertyChangeEvent e) {
		// TODO Auto-generated method stub
		if ((e.getPropertyName().equals(Cytoscape.NETWORK_LOADED)
				|| e.getPropertyName().equals(Cytoscape.NETWORK_DESTROYED) || e
				.getPropertyName().equals(Cytoscape.DATASERVER_CHANGED))) {
			if (Cytoscape.getBioDataServer() == null) {
				super.setEnabled(false);
			} else if (Cytoscape.getBioDataServer().getAnnotationCount() == 0
					|| Cytoscape.getNetworkSet().size() == 0) {
				super.setEnabled(false);
			} else {
				super.setEnabled(true);
			}
		}
	}
}

class MapOntologyTask implements Task {

	private OntologyMapperDialog god;
	private TaskMonitor taskMonitor;

	MapOntologyTask() {
	}

	/**
	 * Executes Task
	 */
	public void run() {
		taskMonitor.setStatus("Building Ontology Mapper.  Please wait...");
		taskMonitor.setPercentCompleted(-1);

		god = new OntologyMapperDialog(Cytoscape.getDesktop(), false);

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Building mapper...");

		god.setVisible(true);
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return new String("Building Mapper");
	}

} // End of SaveSessionAction


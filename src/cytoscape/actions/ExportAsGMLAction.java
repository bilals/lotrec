
/*
  File: ExportAsGMLAction.java 
  
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

// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $

// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GMLWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

public class ExportAsGMLAction extends CytoscapeAction {

	public ExportAsGMLAction() {
		super("Network as GML...");
		setPreferredMenu("File.Export");
	}

	public ExportAsGMLAction(boolean label) {
		super();
	}

	// MLC: 09/19/05 BEGIN:
	/**
	 * User-initiated action to save the current network in GML format to a
	 * user-specified file. If successfully saved, fires a PropertyChange event
	 * with property=Cytoscape.NETWORK_SAVED, old_value=null, and new_value=a
	 * three element Object array containing:
	 * <OL>
	 * <LI>first element = CyNetwork saved
	 * <LI>second element = URI of the location where saved
	 * <LI>third element = an Integer representing the format in which the
	 * Network was saved (e.g., Cytoscape.FILE_GML).
	 * </OL>
	 * 
	 * @param e
	 *            ActionEvent Object.
	 */
	// MLC: 09/19/05 END.
	public void actionPerformed(ActionEvent e) {
		String name;
		try {
			name = FileUtil.getFile("Export Network as GML", FileUtil.SAVE,
					new CyFileFilter[] {}).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return;
		}

		if (!name.endsWith(".gml"))
			name = name + ".gml";

		// Get Current Network and View
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		//CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());

		// Create Task
		ExportAsGMLTask task = new ExportAsGMLTask(name, network, view);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
} // SaveAsGMLAction

/**
 * Task to Save Graph Data to GML Format.
 */
class ExportAsGMLTask implements Task {
	private String fileName;
	private CyNetwork network;
	private CyNetworkView view;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 * 
	 * @param network
	 *            Network Object.
	 * @param view
	 *            Network View Object.
	 */
	ExportAsGMLTask(String fileName, CyNetwork network, CyNetworkView view) {
		this.fileName = fileName;
		this.network = network;
		this.view = view;
	}

	/**
	 * Executes Task
	 */
	public void run() {
		taskMonitor.setStatus("Saving Network...");
		taskMonitor.setPercentCompleted(-1);
		try {
			int numNodes = network.getNodeCount();
			if (numNodes == 0) {
				throw new IllegalArgumentException("Network is empty.");
			}
			saveGraph();
			taskMonitor.setPercentCompleted(100);
			taskMonitor
					.setStatus("Network successfully saved to:  " + fileName);
		} catch (IllegalArgumentException e) {
			taskMonitor.setException(e, "Network is Empty.  Cannot be saved.");
		} catch (IOException e) {
			taskMonitor.setException(e, "Unable to save network.");
		}
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
		return new String("Saving Network");
	}

	/**
	 * Saves Graph to File.
	 * 
	 * @throws IOException
	 *             Error Writing to File.
	 */
	private void saveGraph() throws IOException {
		FileWriter fileWriter = new FileWriter(fileName);
		List list = null;
		GMLReader reader = (GMLReader) network.getClientData("GML");
		if (reader != null) {
			list = reader.getList();
		} else {
			list = new Vector();
		}
		GMLWriter gmlWriter = new GMLWriter();
		gmlWriter.writeGML(network, view, list);
		GMLParser.printList(list, fileWriter);
		fileWriter.close();
		// MLC: 09/19/05 BEGIN:
		// // AJK: 09/14/05 BEGIN
		// Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, network);
		// // AJK: 09/14/05 END
		Object[] ret_val = new Object[3];
		ret_val[0] = network;
		ret_val[1] = new File(fileName).toURI();
		ret_val[2] = new Integer(Cytoscape.FILE_GML);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
		// MLC: 09/19/05 END.
	}
}

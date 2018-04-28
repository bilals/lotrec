/*
 File: LoadNetworkTask.java 
 
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

// $Revision: 8703 $
// $Date: 2006-11-06 23:17:02 -0800 (Mon, 06 Nov 2006) $
// $Author: pwang $
package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.servers.BioDataServer;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.dialogs.ImportNetworkDialog;
import cytoscape.dialogs.VisualStyleBuilderDialog;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyMenus;



/**
 * Task to load a new network. 
 */
public class LoadNetworkTask implements Task {


	public static void loadURL(URL u, boolean skipMessage) {
		LoadNetworkTask task = new LoadNetworkTask(u);
		setupTask(task,skipMessage);
	}

	public static void loadFile(File file, boolean skipMessage) {
		// Create LoadNetwork Task
		LoadNetworkTask task = new LoadNetworkTask(file);
		setupTask(task,skipMessage);
	}

	private static void setupTask(LoadNetworkTask task, boolean skipMessage) {

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(skipMessage);

		// Execute Task in New Thread; pops open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	private URI uri;
	private TaskMonitor taskMonitor;
	private GraphReader reader; 
	private String name;

	private LoadNetworkTask(URL u) {
		reader = Cytoscape.getImportHandler().getReader(u);
		try {
		uri = u.toURI();
		} catch ( URISyntaxException e ) {
			e.printStackTrace();
			uri = null ;
		}
		name = u.toString();
	}

	private LoadNetworkTask(File file) {
		reader = Cytoscape.getImportHandler().getReader(file.getAbsolutePath());
		uri = file.toURI();
		name = file.getName(); 
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		taskMonitor.setStatus("Reading in Network Data...");

		try {
			taskMonitor.setPercentCompleted(-1);

			taskMonitor.setStatus("Creating Cytoscape Network...");

			CyNetwork cyNetwork = Cytoscape.createNetwork(reader,true,null);

			Object[] ret_val = new Object[2];
			ret_val[0] = cyNetwork;
			ret_val[1] = uri; 
			
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);

			if (cyNetwork != null) {
				informUserOfGraphStats(cyNetwork);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not read network from: ");
				sb.append( name );
				sb.append("\nThis file may not be a valid file format.");
				taskMonitor.setException(new IOException(sb.toString()), sb.toString());
			}
			taskMonitor.setPercentCompleted(100);

		} catch (Exception e) {
			taskMonitor.setException(e, "Unable to load network.");
		}
	}

	/**
	 * Inform User of Network Stats.
	 */
	// Mod. by Kei 08/26/2005
	//
	// For the new GML format import function, added some messages
	// for the users.
	//
	private void informUserOfGraphStats(CyNetwork newNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Succesfully loaded network from:  ");
		sb.append(name);
		sb.append("\n\nNetwork contains "
				+ formatter.format(newNetwork.getNodeCount()));
		sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
		sb.append(" edges.\n\n");

		if (newNetwork.getNodeCount() < Integer.parseInt(CytoscapeInit
				.getProperties().getProperty("viewThreshold"))) {
			sb.append("Network is under "
					+ CytoscapeInit.getProperties()
							.getProperty("viewThreshold")
					+ " nodes.  A view will be automatically created.");
		} else {
			sb.append("Network is over "
					+ CytoscapeInit.getProperties()
							.getProperty("viewThreshold")
					+ " nodes.  A view has not been created."
					+ "  If you wish to view this network, use "
					+ "\"Create View\" from the \"Edit\" menu.");
		}
		taskMonitor.setStatus(sb.toString());
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
		return new String("Loading Network");
	}
}

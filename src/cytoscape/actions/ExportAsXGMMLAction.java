
/*
  File: ExportAsXGMMLAction.java 
  
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

package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.writers.XGMMLWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

/**
 * This action is for exporting network and attributes in XGMML file.
 * 
 * @author kono
 * 
 */
public class ExportAsXGMMLAction extends CytoscapeAction {

	public ExportAsXGMMLAction() {
		super("Network and attributes as XGMML...");
		setPreferredMenu("File.Export");
	}

	public ExportAsXGMMLAction(boolean label) {
		super();
	}

	protected boolean checkNetworkCount() {
		Set networks = Cytoscape.getNetworkSet();
		if (networks.size() == 0) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"No network in this session!", "No network Error",
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		} else {
			return true;
		}

	}

	public void actionPerformed(ActionEvent e) {

		if (checkNetworkCount() == false)
			return;

		// Create FileFilters
		CyFileFilter xgmmlFilter = new CyFileFilter();
		CyFileFilter xmlFilter = new CyFileFilter();
		CyFileFilter graphFilter = new CyFileFilter();
		// Add accepted File Extensions
		xgmmlFilter.addExtension("xgmml");
		xgmmlFilter.setDescription("XGMML files");
		xmlFilter.addExtension("xml");
		xmlFilter.setDescription("XML files");
		graphFilter.addExtension("xgmml");
		graphFilter.addExtension("xml");
		graphFilter.setDescription("All xml-based network files");

		// XGMML file name
		String name;

		try {
			name = FileUtil.getFile("Export Network and Attributes as XGMML",
					FileUtil.SAVE,
					new CyFileFilter[] { xgmmlFilter, xmlFilter, graphFilter })
					.toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return;
		}

		if (!name.endsWith(".xgmml"))
			name = name + ".xgmml";

		// Get Current Network and View
		final CyNetwork network = Cytoscape.getCurrentNetwork();
		final CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());

		// Create Task
		final ExportAsXGMMLTask task = new ExportAsXGMMLTask(name, network, view);

		// Configure JTask Dialog Pop-Up Box
		final JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
} // SaveAsGMLAction

/**
 * Task to Save Graph Data to XGMML Format.
 */
class ExportAsXGMMLTask implements Task {
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
	public ExportAsXGMMLTask(String fileName, CyNetwork network,
			CyNetworkView view) {
		this.fileName = fileName;
		this.network = network;
		this.view = view;
	}

	/**
	 * Executes Task
	 * 
	 * @throws Exception
	 */
	public void run() {
		taskMonitor.setStatus("Exporting Network and Attributes...");
		taskMonitor.setPercentCompleted(-1);

		int numNodes = network.getNodeCount();
		if (numNodes == 0) {
			throw new IllegalArgumentException("Network is empty.");
		}

		try {
			saveGraph();
		} catch (Exception e) {
			taskMonitor.setException(e, "Cannot export graph as XGMML.");
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor
				.setStatus("Network and attributes are successfully saved to:  "
						+ fileName);

		System.out
				.println("Network and attributes are exported as an XGMML file: "
						+ fileName);
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
		return new String("Exporting Network and Attributes");
	}

	/**
	 * Saves Graph to File.
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 * @throws URISyntaxException 
	 * @throws FactoryConfigurationError 
	 * @throws XMLStreamException 
	 */
	private void saveGraph() throws IOException, JAXBException, URISyntaxException, XMLStreamException, FactoryConfigurationError {

		final FileWriter fileWriter = new FileWriter(fileName);
		final XGMMLWriter writer = new XGMMLWriter(network, view);

		try {
			writer.write(fileWriter);
		} finally {
			fileWriter.close();
		}
		
		final Object[] ret_val = new Object[3];
		ret_val[0] = network;
		ret_val[1] = new File(fileName).toURI();
		ret_val[2] = new Integer(Cytoscape.FILE_XGMML);
		
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
	}
}
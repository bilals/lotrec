/*
 File: SaveSessionAction.java 
 
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
import java.awt.event.KeyEvent;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

/**
 * Save Session action.<br>
 * Write current states into a CYS file.<br>
 * 
 * @version 1.0
 * @since Cytoscape 2.3
 * @see cytoscape.data.writers.CytoscapeSessionWriter
 * @author kono
 * 
 */
public class SaveSessionAction extends CytoscapeAction {

	// Extension for the new cytoscape session file
	public final static String SESSION_EXT = ".cys";

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label for the menu item.
	 */
	public SaveSessionAction(String label) {
		super(label);
		setPreferredMenu("File");
		setAcceleratorCombo(KeyEvent.VK_S, ActionEvent.CTRL_MASK);
	}

	/**
	 * Constructor.<br>
	 * This is mainly for the toolbar icon button.<br>
	 * 
	 */
	public SaveSessionAction() {
		super();
	}

	/**
	 * If no current session file exists, open dialog box to ask user a new
	 * session file name, otherwise, overwrite the file.
	 */
	public void actionPerformed(ActionEvent e) {

		// Call file chooser only when the currentFileName is null.
		String name = Cytoscape.getCurrentSessionFileName();

		if (name == null) {

			File file = FileUtil.getFile("Save Current Session as CYS File",
					FileUtil.SAVE, new CyFileFilter[] {});
			if (file == null) {
				return;
			}

			name = file.getAbsolutePath();

			if (!name.endsWith(SESSION_EXT))
				name = name + SESSION_EXT;

			Cytoscape.setCurrentSessionFileName(name);
		}
		// Create Task
		SaveSessionTask task = new SaveSessionTask(name);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();

		jTaskConfig.displayCancelButton(false);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
} //End of SaveSessionAction

/**
 * Save Session Task.<br>
 * Call the Session Writer to save the following:<br>
 * <ul>
 * <li>Networks with metadata</li>
 * <li>All attributes (for nodes, edges, and network)</li>
 * <li>Visual Styles</li>
 * <li>Cytoscape Properties</li>
 * </ul>
 * 
 * @author kono
 * 
 */
class SaveSessionTask implements Task {

	private String fileName;
	private TaskMonitor taskMonitor;

	CytoscapeSessionWriter sw;

	/**
	 * Constructor.<br>
	 * 
	 * @param fileName
	 *            Absolute path to the Session file.
	 */
	SaveSessionTask(String fileName) {
		this.fileName = fileName;
		// Create session writer object
		sw = new CytoscapeSessionWriter(fileName);
	}

	/**
	 * Execute task.<br>
	 */
	public void run() {
		taskMonitor
				.setStatus("Saving Cytoscape Session.\n\nIt may take a while.  Please wait...");
		taskMonitor.setPercentCompleted(-1);

		try {
			sw.writeSessionToDisk();
		} catch (Exception e) {
			taskMonitor.setException(e, "Could not write session to the file: "
					+ fileName);
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Session successfully saved to:  " + fileName);

		// Show the session Name as the window title.
		File shortName = new File(fileName);
		Cytoscape.setCurrentSessionFileName(fileName);
		Cytoscape.getDesktop().setTitle(
				"Cytoscape Desktop (Session: " + shortName.getName() + ")");
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
		return "Saving Cytoscape Session";
	}

} // End of SaveSessionTask

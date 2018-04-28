
/*
  File: ExportAsInteractionsAction.java 
  
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

// $Revision: 7762 $
// $Date: 2006-06-27 17:04:52 -0700 (Tue, 27 Jun 2006) $
// $Author: mes $
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.data.CyAttributes;
import cytoscape.data.writers.InteractionWriter;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.CyNetwork;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * write out the current graph to the specified file, using the standard
 * interactions format:  nodeA edgeType nodeB.
 * for example: <code>
 * <p/>
 * YMR056C pp YLL013C
 * YCR107W pp YBR265W
 * <p/>
 * </code>
 */
public class ExportAsInteractionsAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public ExportAsInteractionsAction() {
        super("Network as SIF File...");
        setPreferredMenu("File.Export");
    }
    // MLC 09/19/05 BEGIN:
    /**
     * User-initiated action to save the current network in SIF format
     * to a user-specified file.  If successfully saved, fires a
     * PropertyChange event with property=Cytoscape.NETWORK_SAVED,
     * old_value=null, and new_value=a three element Object array containing:
     * <OL>
     * <LI>first element = CyNetwork saved
     * <LI>second element = URI of the location where saved
     * <LI>third element = an Integer representing the format in which the
     * Network was saved (e.g., Cytoscape.FILE_SIF).
     * </OL>
     * @param e ActionEvent Object.
     */
    // MLC 09/19/05 END.
    public void actionPerformed(ActionEvent e) {

        // get the file name
        File file = FileUtil.getFile("Save Network as Interactions",
                    FileUtil.SAVE, new CyFileFilter[]{});

        if (file != null) {
            String fileName = file.getAbsolutePath();
            if (!fileName.endsWith(".sif"))
                fileName = fileName + ".sif";

            //  Create LoadNetwork Task
            SaveAsSifTask task = new SaveAsSifTask(fileName);

            //  Configure JTask Dialog Pop-Up Box
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(false);

            //  Execute Task in New Thread;  pop open JTask Dialog Box.
            TaskManager.executeTask(task, jTaskConfig);
        }

    }
}

/**
 * Task to Save Graph Data to SIF Format.
 */
class SaveAsSifTask implements Task {
    private String fileName;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     * @param fileName          Filename to save to
     */
    SaveAsSifTask (String fileName) {
        this.fileName = fileName;
    }

    /**
     * Executes the Task.
     */
    public void run() {
        taskMonitor.setStatus("Saving Interactions...");
        try {
	    if (Cytoscape.getCurrentNetwork().getNodeCount() == 0) {
                throw new IllegalArgumentException ("Network is empty.");
            }

	    FileWriter f = new FileWriter(fileName);
	    CyNetwork netToSave = Cytoscape.getCurrentNetwork();
	    InteractionWriter.writeInteractions(netToSave,f, taskMonitor);
	    f.close();

	    Object[] ret_val = new Object[3];
    	    ret_val[0] = netToSave;
	    ret_val[1] = new File (fileName).toURI();
	    ret_val[2] = new Integer (Cytoscape.FILE_SIF);
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);

            taskMonitor.setPercentCompleted (100);
            taskMonitor.setStatus("Network successfully saved to:  "
                    + fileName);
        } catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Network is Empty.  Cannot be saved.");
        } catch (IOException e) {
            taskMonitor.setException(e, "Unable to save network.");
        }
    }

    /**
     * Halts the Task:  Not Currently Implemented.
     */
    public void halt() {
        //   Task can not currently be halted.
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
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
        return new String ("Saving Network");
    }
}

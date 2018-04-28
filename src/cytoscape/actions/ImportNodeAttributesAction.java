
/*
  File: ImportNodeAttributesAction.java 
  
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

// $Revision: 9155 $
// $Date: 2006-12-13 17:17:34 -0800 (Wed, 13 Dec 2006) $
// $Author: mes $
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.servers.BioDataServer;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;
import java.io.File;

/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of node / edge attributes from the GUI
 */

public class ImportNodeAttributesAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public ImportNodeAttributesAction() {
        super("Node Attributes...");
        setPreferredMenu("File.Import");
    }

    /**
     * User Initiated Request.
     *
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {

        //  Use a Default CyFileFilter:  enables user to select any file type.
        CyFileFilter nf = new CyFileFilter();

        // get the file name
        File[] files = FileUtil.getFiles("Import Node Attributes",
                    FileUtil.LOAD, new CyFileFilter[]{nf});

        if (files != null) {
            //  Create Load Attributes Task
            ImportAttributesTask task = 
            	new ImportAttributesTask (files, ImportAttributesTask.NODE_ATTRIBUTES);

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
 * Task to Load New Node/Edge Attributes Data.
 */
class ImportAttributesTask implements Task {
    private TaskMonitor taskMonitor;
    private File[] files;
    private int type;
    static final int NODE_ATTRIBUTES = 0;
    static final int EDGE_ATTRIBUTES = 1;

    /**
     * Constructor.
     * @param file File Object.
     * @param type NODE_ATTRIBUTES or EDGE_ATTRIBUTES
     */
    ImportAttributesTask (File[] files, int type) {
        this.files = files;
        this.type = type;
    }

    /**
     * Executes Task.
     */
    public void run() {
        try {
            taskMonitor.setPercentCompleted(-1);
            taskMonitor.setStatus("Reading in Attributes");

            //  Read in Data
            
            // track progress. CyAttributes has separation between
            // reading attributes and storing them
            // so we need to find a different way of monitoring this task:
            // attributes.setTaskMonitor(taskMonitor);
          
            for (int i=0; i<files.length; ++i) {
	            taskMonitor.setPercentCompleted(100*i/files.length);
	            if ( type == NODE_ATTRIBUTES ) 
	                Cytoscape.loadAttributes( new String[] { files[i].getAbsolutePath() },
	                                          new String[] {});
	            else if ( type == EDGE_ATTRIBUTES ) 
	                Cytoscape.loadAttributes( new String[] {},
		                                      new String[] { files[i].getAbsolutePath() });
	            else
	                throw new Exception("Unknown attribute type: " + Integer.toString(type) );
	        }

            //  Inform others via property change event.
            taskMonitor.setPercentCompleted(100);
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null );
            taskMonitor.setStatus("Done");
        } catch (Exception e) {
            taskMonitor.setException(e, e.getMessage());
        }
    }

    /**
     * Halts the Task:  Not Currently Implemented.
     */
    public void halt() {
        //   Task can not currently be halted.
    }

    /**
     * Sets the Task Monitor Object.
     * @param taskMonitor
     * @throws IllegalThreadStateException
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
        if (type == NODE_ATTRIBUTES) {
            return new String ("Loading Node Attributes");
        } else {
            return new String ("Loading Edge Attributes");
        }
    }
}


/*
  File: MonitoredTask.java 
  
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

// MonitoredTask. An interface for classes for which progress will be monitored 
// (for example, using a progress bar UI)
//----------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: iliana
//----------------------------------------------------------------------------------------
package cytoscape.util;
//----------------------------------------------------------------------------------------

public interface MonitoredTask{
    
  // ------ Classes that implement this interface must have the following members:
    
  //int lenghtOfTask;     // estimate of how long the task will take to complete
  //int currentProgress;  // how much work has been done so far
  //String statMessage;   // if using a dialog, this message may be displayed to show progress 
                          // for example:"Completed 55%"
  //String taskName       // if using a dialog, this message may be displayed to show what task is being 
                          // performed, example: "Calculating APSP" 
  
  // ------------ Methods in alphabetical order --------------

  /**
   * Returns true if the task is done. Otherwise false.
   * This can be done by either returning (currentProgress == lengthOfTask)
   * or having a boolean "done" variable that is set to true in the code
   * whenever the task is done, and returning it here.
   */
  public boolean done();
    
  /**
   * Returns the currentProgress parameter.
   */
  public   int getCurrent();
    
  /**
   * Returns the lenghtOfTask parameter.
   */
  public int getLengthOfTask();

  /**
   * Returns a String, possibly the message to be printed on a dialog.
   * Example: "Completed 12%" 
   */
  public String getMessage();

  /**
   * Returns a String, possibly the message that describes the task being performed.
   * Example: "Calculating connecting paths." 
   */
  public String getTaskName ();
    
  /**
   * Initializes currentProgress (generally to zero) and then spawns a SwingWorker
   * to start doing the work.
   * @param wait whether or not the method should wait for the task to be done before returning
   *             if true, should call SwingWorker.get() before returning
   */
  public void go(boolean wait);
        
  /**
   * Increments the progress by one
   */
  public void incrementProgress();
  
  /**
   * Stops the task by simply setting currentProgress to lengthOfTask, 
   * or if a boolean "done" variable is used, setting it to true.
   */
  public void stop();
  
  /**
   * Gets called by the <code>CytoscapeProgressMonitor</code> when the user
   * click on the Cancel button.
   */
  public void cancel ();
  
  /**
   * @return whether the task was canceled while running or not.
   */
  public boolean wasCanceled ();

}// MonitoredTask class

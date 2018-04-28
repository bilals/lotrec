
/*
  File: CytoscapeProgressMonitor.java 
  
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

//----------------------------------------------------------------------
// CytoscapeProgressMonitor
//----------------------------------------------------------------------
/**
 * $Revision: 7760 $
 * $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
 * @author iliana iavila@systemsbiology.org
 */
//----------------------------------------------------------------------
package cytoscape.util;
//----------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.Timer;

//---------------------------------------------------------------------------------------
/**
 * CytoscapeProgressMonitor. A progress monitor for cytoscape. It is recommended 
 * to run the monitor in a different Thread, specially if the user class wants
 * to wait until the task is done running to resume.
 */
public class CytoscapeProgressMonitor extends JFrame
  implements Runnable{
  
  public final static int ONE_SECOND = 1000;
  private ProgressMonitor pMonitor;
  private Timer timer;
  private Component parentComponent;
  private MonitoredTask monitoredTask;
    
  /**
   * Construtor.
   * @param monitored_task the task to be monitored
   */
  public CytoscapeProgressMonitor(MonitoredTask monitored_task){
    this(monitored_task, (Component)null);
  }
  /**
   * Constructor.
   * @param monitored_task the task to be monitored
   * @param parent_component the component with respect to which we will draw the monitor dialog
   */
  public CytoscapeProgressMonitor(MonitoredTask monitored_task, 
                                  Component parent_component){
    this.monitoredTask = monitored_task;
    this.parentComponent = parent_component;
  }//CytoscapeProgressMonitor

  /**
   * Implementes Runnable.run()
   */
  public void run (){
    startMonitor(true);
  }//run ()

  /**
   * Starts the monitor by running the monitoredTask.
   * @param wait_for_task whether or not we should wait for the task to be done before we return 
   *                      from this method
   */
  public void startMonitor (boolean wait_for_task) {

    if(this.parentComponent != null){
      this.parentComponent.setEnabled(false);
    }
    Thread monitoringThread = new MonitoringThread(); 
    // run in separate thread so that monitor can get updated
    initializeMonitor();
    monitoringThread.start();
    // If wait_for_task is true, 
    // the current thread will return from go() only when the task is done running
    monitoredTask.go(wait_for_task); 
  }// startMonitor()

  class MonitoringThread extends Thread {
    
    MonitoringThread () {
      super();
    }//cons
    
    public void run () {
      java.util.Timer mytimer = new java.util.Timer(false);
      mytimer.scheduleAtFixedRate(new UpdateProgressMonitor(mytimer),0,ONE_SECOND);
    }//run
    
    class UpdateProgressMonitor extends TimerTask {
      private boolean firstTick = true;
      private java.util.Timer myTimer;
      
      UpdateProgressMonitor (java.util.Timer timer) {
        super();
        this.myTimer = timer;
      }//cons
      
      public void run () {
        //  if(firstTick){
        //System.out.println("UpdateProgressMonitor: " + Thread.currentThread());
        //firstTick = false;
        //System.out.flush(); 
        //}
        if(pMonitor.isCanceled()){
          //System.out.println("pMonitor.isCanceled() == " + pMonitor.isCanceled());
          pMonitor.close();
          monitoredTask.cancel();
          myTimer.cancel();
          if(parentComponent != null){
            parentComponent.setEnabled(true);
          }
        } else if(monitoredTask.done()) {
          //System.out.println(monitoredTask.getTaskName() + " monitoredTask.done() == " + monitoredTask.done());
          pMonitor.close();
          monitoredTask.stop();
          myTimer.cancel();
          if(parentComponent != null){
            parentComponent.setEnabled(true);
          }
        } else {
          pMonitor.setNote(monitoredTask.getMessage());
          pMonitor.setProgress(monitoredTask.getCurrent());
        }
      }//run
      
    }//internal class UpdateProgressMonitor
    
  }//internal class MonitoringThread
    
  /**
   * It creates and initializes a progress monitor.
   */
  protected void initializeMonitor (){
    String message = monitoredTask.getTaskName();
    pMonitor = new ProgressMonitor(this.parentComponent,
                                   new Object[] {message},
                                   null, 0, monitoredTask.getLengthOfTask());
   
    System.out.println( monitoredTask.getLengthOfTask() );
    System.out.println( "About to Popup" );
    pMonitor.setProgress(0);
    pMonitor.setMillisToDecideToPopup(0);
    pMonitor.setMillisToPopup(0);
  }//initializeMonitor

}//CytoscapeProgressMonitor class

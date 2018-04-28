
/*
  File: UndoManager.java 
  
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

package cytoscape.util;

import cytoscape.Cytoscape;

import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

public class UndoManager   {

  private  javax.swing.undo.UndoManager undo;
  private  UndoAction undoAction;
  private  RedoAction redoAction;

  public UndoManager ( cytoscape.view.CyMenus menus ) {
  
    
    undo = new javax.swing.undo.UndoManager();
    undoAction = new UndoAction();
    redoAction = new RedoAction();
    
    JMenuItem undoItem = new JMenuItem( undoAction );
    JMenuItem redoItem = new JMenuItem( redoAction );
    undoItem.setEnabled(false);
    redoItem.setEnabled(false);
    
    undoItem.setAccelerator(  javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Z,
                                                                  ActionEvent.CTRL_MASK ) );
    redoItem.setAccelerator(  javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Y,
                                                                  ActionEvent.CTRL_MASK ) );

    menus.getMenuBar().getMenu( "Edit" ).add( undoItem );
    menus.getMenuBar().getMenu( "Edit" ).add( redoItem );
    menus.getMenuBar().getMenu( "Edit" ).add(new JSeparator());

//    System.out.println("Undo Menu item isEnabled = " + undoItem.isEnabled());
    System.out.println( "UndoManager initialized" );
  }
  
  

  public  void addEdit ( UndoableEdit edit ) {
    undo.addEdit( edit );
    undoAction.update();   
    redoAction.update();
	Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, 
			Cytoscape.getCurrentNetwork());
  }
  
  /**
   * called when a network view switches
   *
   */
  public void discardAllEdits ()
  {
	  undo.discardAllEdits();
	  undoAction.update();
	  redoAction.update();
  }

  protected class UndoAction extends AbstractAction {
    public UndoAction() {
	    super("Undo");
	    setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
	    try {
        undo.undo();
	    } catch (CannotUndoException ex) {
        //System.out.println("Unable to undo: " + ex);
        //ex.printStackTrace();
	    }
	    update();
	    redoAction.update();
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, "cytoscape.util.UndoManager", 
				Cytoscape.getCurrentNetwork());
		}

    protected void update() {
	    if(undo.canUndo()) {
        setEnabled(true);
        putValue(Action.NAME, undo.getUndoPresentationName());
	    }
	    else {
        setEnabled(false);
        putValue(Action.NAME, "Undo");
	    }
    }
  }

  protected class RedoAction extends AbstractAction {
    public RedoAction() {
	    super("Redo");
	    setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
	    try {
        undo.redo();
	    } catch (CannotRedoException ex) {
        //System.out.println("Unable to redo: " + ex);
        //ex.printStackTrace();
	    }
	    update();
	    undoAction.update();
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, "cytoscape.util.UndoManager", 
				Cytoscape.getCurrentNetwork());
    }

    protected void update() {

      //System.out.println( "REDO: "+undo.canRedo() );

	    if(undo.canRedo()) {
        setEnabled(true);
        putValue(Action.NAME, undo.getRedoPresentationName());
	    }
	    else {
        setEnabled(false);
        putValue(Action.NAME, "Redo");
	    }
    }
  }
  
  

}

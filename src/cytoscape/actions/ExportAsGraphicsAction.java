
/*
  File: ExportAsGraphicsAction.java 
  
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

//-------------------------------------------------------------------------
// $Revision: 9214 $
// $Date: 2006-12-18 15:19:32 -0800 (Mon, 18 Dec 2006) $
// $Author: pwang $
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import phoebe.util.*;

import cytoscape.ding.DingNetworkView;
import cytoscape.giny.*;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import org.freehep.util.export.ExportDialog;

import ding.view.DGraphView;

//-------------------------------------------------------------------------
public class ExportAsGraphicsAction extends CytoscapeAction  {

    public final static String MENU_LABEL = "Network As Graphics";
        
    public ExportAsGraphicsAction () {
        super (MENU_LABEL);
        setPreferredMenu( "File.Export" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;

    }

    public void actionPerformed(ActionEvent e) {
    	System.out.println("netwok as graphics");
	
      CyNetworkView curr = Cytoscape.getCurrentNetworkView();
      if ( curr != Cytoscape.getNullNetworkView() ) {
    	  
    	  // Export text as shape/font based on user's setting
          DGraphView theViewToPrint = (DingNetworkView) Cytoscape.getCurrentNetworkView();
          boolean exportTextAsShape = new Boolean(CytoscapeInit.getProperties().
    				getProperty("exportTextAsShape")).booleanValue();
          theViewToPrint.setPrintingTextAsShape(exportTextAsShape);
    	  
      	ExportDialog export = new ExportDialog();
      	export.showExportDialog( curr.getComponent(), "Export view as ...", curr.getComponent(), "export" );
      } else {
      	JOptionPane.showMessageDialog(null, "No network view selected! Please select a view to export.", 
	                              "No network view selected!", JOptionPane.ERROR_MESSAGE);
      }
      
	
    } // actionPerformed
}


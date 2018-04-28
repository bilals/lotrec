
/*
  File: SetVisualPropertiesAction.java 
  
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
// $Revision: 8887 $
// $Date: 2006-11-20 17:50:13 -0800 (Mon, 20 Nov 2006) $
// $Author: mes $
//------------------------------------------------------------------------------
package cytoscape.actions;
//------------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;

//------------------------------------------------------------------------------
public class SetVisualPropertiesAction extends CytoscapeAction   {

   
  public SetVisualPropertiesAction ( ImageIcon icon ) {
    super("Open VizMapper\u2122", icon );
    setPreferredMenu( "View" );
  }
  
  /** The constructor that takes a boolean shows no label,
   *  no matter what the value of the boolean actually is.
   *  This makes is appropriate for an icon, but inappropriate
   *  for the pulldown menu system. */
  public SetVisualPropertiesAction ( boolean showLabel) {
    super();
  }
    
  public void actionPerformed (ActionEvent e) {
    //TODO: ack! this should be using the global VizMapper
    Cytoscape.getDesktop().getVizMapUI().refreshUI();
    Cytoscape.getDesktop().getVizMapUI().getStyleSelector().setVisible(true);
  }
}


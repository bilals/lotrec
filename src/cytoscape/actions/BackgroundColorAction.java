
/*
  File: BackgroundColorAction.java 
  
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
// $Revision: 7761 $
// $Date: 2006-06-27 13:47:58 -0700 (Tue, 27 Jun 2006) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------

import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CyColorChooser;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//-------------------------------------------------------------------------
/**
 * Prompts User for New Background Color.
 */
public class BackgroundColorAction extends CytoscapeAction {

   
    /**
     * Constructor.
     */
    public BackgroundColorAction () {
        super("Change Background Color");
        setPreferredMenu( "View" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_B, ActionEvent.ALT_MASK) ;
    }

    /**
     * Captures User Menu Selection.
     * @param ev Action Event.
     */
    public void actionPerformed(ActionEvent ev) {
        // Do this in the GUI Event Dispatch thread...
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Color newPaint = CyColorChooser.showDialog( 
                                                  Cytoscape.getCurrentNetworkView().getComponent(),
                                                  "Choose a Background Color",
                                                  (java.awt.Color)Cytoscape.getCurrentNetworkView().
                                                  getBackgroundPaint() );

            //  Update the Current Background Color
            //  and Synchronize with current Visual Style
            if (newPaint != null) {
                Cytoscape.getCurrentNetworkView().setBackgroundPaint(newPaint);
                synchronizeVisualStyle(newPaint);
            }
            }
        });
    }//action performed

    /**
     * Synchronizes the New Background Color with the Current Visual Style.
     * @param newColor New Color
     */
    private void synchronizeVisualStyle(Color newColor) {
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        if (vmm != null) {
            VisualStyle style = vmm.getVisualStyle();
            if (style != null) {
                GlobalAppearanceCalculator gCalc =
                    style.getGlobalAppearanceCalculator();
                gCalc.setDefaultBackgroundColor(newColor);
                vmm.applyGlobalAppearances();
            }
        }
    }
}

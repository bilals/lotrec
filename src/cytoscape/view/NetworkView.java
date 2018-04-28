
/*
  File: NetworkView.java 
  
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

// $Revision: 8721 $
// $Date: 2006-11-09 15:56:41 -0800 (Thu, 09 Nov 2006) $
// $Author: mes $

package cytoscape.view;

import javax.swing.JFrame;

import giny.view.GraphView;
//import phoebe.PGrap*View;


import cytoscape.CyNetwork;
import cytoscape.visual.VisualMappingManager;
//-------------------------------------------------------------------------
/**
 * @deprecated Will be removed April 2007.
 * This interface defines methods for displaying a graph without
 * specifying the details of the UI components surrounding the
 * graph display.
 */
public interface NetworkView {
    

    /**
     * Returns the network displayed by this object.
     */
    CyNetwork getNetwork();
    /**
    *
    */
    void setNewNetwork( CyNetwork newNetwork);
    
    /**
     *
     */
    void setWindowTitle(String title);

    /**
     * @return A GINY GraphView
     */
    GraphView getView();

    /**
     * Return the frame in which the graph is displayed. Useful for constructing
     * dialogs dependent on this frame.
     */
    JFrame getMainFrame();

    /**
     * Returns the visual mapper associated with this display.
     */
    VisualMappingManager getVizMapManager();
    
    /**
     * Redraws the graph. The arguments control what actions are
     * performed before repainting the view.
     *
     * @param doLayout     if true, applied the current layouter to the graph
     * @param applyAppearances  if true, the vizmapper will recalculate
     *                          the node and edge appearances
     */
    void redrawGraph(boolean doLayout, boolean applyAppearances);
    
    /**
    * added for giny, applies the layout on the view
    */
    void applyLayout ( GraphView view );
  
  public GraphViewController getGraphViewController ();
}


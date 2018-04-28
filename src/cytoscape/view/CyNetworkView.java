
/*
  File: CyNetworkView.java 
  
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

package cytoscape.view;

import cytoscape.*;
import giny.view.*;
import cytoscape.layout.*;
import java.util.*;
import cytoscape.visual.*;

// AJK: 05/19/06 BEGIN
//     for context menus
import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;


//

/**
 * CyNetworkView is responsible for actually getting a graph to show up on the screen.<BR>
 *<BR>
 * Cytoscape does not currently define specific classes for NodeViews and EdgeViews, the deafults from the GINY graph library ( namely phoebe.PNodeView and phoebe.PEdgeView ) are most commonly used. Making custom nodes is easy and fun.  One must implement the giny.view.NodeView interface and inherit from edu.umd.cs.piccolo.PNode.  The Piccolo project is what all of the paiting is based on, and is very fast, flexable and powerful.  Becoming acquainted with Piccolo is essential for build custom nodes.<BR><BR>
Fortunately, if you just want basic shapes and colors, it's all built into the UI already, and you really need never even use this class.  Just learn how to use the VizMapper to acclompish your data to view mappings. The manual is a good place to start.
 */
public interface CyNetworkView extends GraphView {

 
  /**
   * Returns the network displayed by this object.
   */
  public CyNetwork getNetwork ();
     
  /**
   * Sets the Title of this View
   */
  public void setTitle ( String title );

  /**
   * Returns the Title of this View
   */
  public String getTitle ();
  
  public void redrawGraph( boolean layout, boolean vizmap);

  public CyNetworkView getView ();
 
  public VisualMappingManager getVizMapManager();

  public cytoscape.visual.ui.VizMapUI getVizMapUI();
  
  public void toggleVisualMapperEnabled();

  public void setVisualMapperEnabled ( boolean state );

  public boolean getVisualMapperEnabled ();

  //--------------------//
  // Network Client Data
  
  /**
   * Networks can support client data.
   * @param data_name the name of this client data
   */
  public void putClientData ( String data_name, Object data );

  /**
   * Get a list of all currently available ClientData objects
   */
  public Collection getClientDataNames ();
  
  /**
   * Get Some client data
   * @param data_name the data to get
   */
  public Object getClientData ( String data_name );
    

  /**
   * Sets the Given nodes Selected<br>
   * @deprecated this method is not working, use {@link Cytoscape.CyNetwork#setSelectedNodeState(Collection, boolean)}
   * 
   */
  public boolean setSelected ( CyNode[] nodes );

  /**
   * Sets the Given nodes Selected<br>
   * @deprecated this method is not working, use {@link Cytoscape.CyNetwork#setSelectedNodeState(Collection, boolean)}
   */
  public boolean setSelected ( NodeView[] node_views );

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge );

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( EdgeView edge_view );


  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node );

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( NodeView node_view );

   /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge, VisualStyle style );

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( EdgeView edge_view, VisualStyle style );


  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node, VisualStyle style );

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( NodeView node_view, VisualStyle style );


  /**
   * Sets the Given edges Selected<br>
   * @deprecated this method is not working, use {@link cytoscape.CyNetwork#setSelectedEdgeState(Collection, boolean)}
   */
  public boolean setSelected ( CyEdge[] edges );

  /**
   * Sets the Given edges Selected<br>
   * @deprecated this method is not working, use {@link cytoscape.CyNetwork#setSelectedEdgeState(Collection, boolean)}
   */
  public boolean setSelected ( EdgeView[] edge_views );

  /**
   * @param applyAppearances  if true, the vizmapper will recalculate
   *                          the node and edge appearances
   */
  public void applyVizmapper ( VisualStyle style );
    
  /**
   * Applies the given layout to the entire CyNetworkView
   */
  public void applyLayout ( LayoutAlgorithm layout );

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges );

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( LayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges );

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given NodeViews and EdgeViews in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges );

  /**
   * Applies the  given layout to only the given NodeViews and EdgeViews
   */
  public void applyLayout ( LayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges );

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, int[] nodes, int[] edges );

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( LayoutAlgorithm layout, int[] nodes, int[] edges );
  
  // This is necessary since we should save the association between VS
  // AND network view.
  // kono: 03/14/2006
  public void setVisualStyle(String VSName);
  
  public VisualStyle getVisualStyle();
  
  // AJK: 05/19/06 BEGIN
  //   for context menus
  public void addNodeContextMenuListener (NodeContextMenuListener l);

  public void removeNodeContextMenuListener (NodeContextMenuListener l);

  public void addEdgeContextMenuListener(EdgeContextMenuListener l);
 
  public void removeEdgeContextMenuListener(EdgeContextMenuListener l);
  // AJK: 05/19/06 END
  
}

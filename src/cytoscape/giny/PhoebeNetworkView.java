
/*
  File: PhoebeNetworkView.java 
  
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

package cytoscape.giny;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.layout.*;
import cytoscape.actions.GinyUtils;
import cytoscape.data.CyNetworkUtilities;

import phoebe.*;
import giny.model.*;
import giny.view.*;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.util.List;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;

//AJK: 05/19/06 BEGIN
//for context menus
import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;
//AJK: 05/19/06 END

/**
 * @deprecated You should be using DingNetworkView instead.  This 
 * will be removed in whatever major release follows 2.4.
 */
public class PhoebeNetworkView 
  extends 
    PGraphView
  implements
    CyNetworkView {

  boolean vizmapEnabled = true;

  /**
   * This is the title of the NetworkView, it will be 
   * dispalyed in a Tab, or in a Window.
   */
  protected String title;
  
  /**
   * This is the label that tells how many node/edges are
   * in a CyNetworkView and how many are selected/hidden
   */
  protected JLabel statusLabel;
  
  /**
   * The FlagAndSelectionHandler keeps the selection state of view objects
   * inthe CyNetworkView nsync with the flagged state of those objects in
   * the default flagger of the associated CyNetwork.
   */
  protected FlagAndSelectionHandler flagAndSelectionHandler;
  
  /**
   * The ClientData map
   */
  protected Map clientData;

  protected PBasicInputEventHandler keyEventHandler;
//   protected PBasicInputEventHandler changeCursorHandler;
//   protected Cursor zuiCursor;

  
  // Associated visual style
  protected VisualStyle vs;

  public PhoebeNetworkView ( CyNetwork network,
                             String title ) {
    super( (GraphPerspective)network  );
    this.title = title;
    initialize();

    //getComponent().setBounds(0, 0, 1, 1);
    //getCanvas().getLayer().setBounds(0, 0, 1, 1);

  }

  protected void initialize () {
    
    //setup the StatusLabel
    this.statusLabel = new JLabel();
    ( ( JComponent )getComponent() ).add(statusLabel, BorderLayout.SOUTH);
    updateStatusLabel();
    clientData = new HashMap();

    enableNodeSelection();
    enableEdgeSelection();
    
    GraphView[] graphViews = {this};
    flagAndSelectionHandler =
    new FlagAndSelectionHandler( ( ( CyNetwork )getNetwork()).getFlagger(), this);
    //TODO:
    //     Add NetworkView specific ToolBars

    // System.out.println( "Image::::: "+ getClass().getResource("images/new/zui_cursor.gif") );

   //  try {
//       Toolkit toolkit = Toolkit.getDefaultToolkit();
//       Image image = toolkit.createImage( getClass().getResource("images/zui_cursor.gif") );
//       zuiCursor = toolkit.createCustomCursor(image , new Point(0,0), "ZUI");
//     } catch ( Exception e ) {
//       // set the zui cursor to the default cursor.
//       zuiCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
//     }
    
    // Set visual style
    vs = null;
  }
  
  public void setVisualStyle( String VSName ) {
	  //System.out.println("New Vsial Style for " + title + ": " + VSName );
	  vs = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VSName);
  }
  
  public VisualStyle getVisualStyle() {
	  return vs;
  }
   
  protected void initializeEventHandlers() {
    super.initializeEventHandlers();

    
  //   changeCursorHandler = new PBasicInputEventHandler () {
//         public void mousePressed (PInputEvent event) {
//           getCanvas().setCursor( zuiCursor );
//         }
//         public void	mouseReleased(PInputEvent event) {
//           getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
//         }
//       };
//     changeCursorHandler.setEventFilter( new PInputEventFilter(InputEvent.BUTTON1_MASK) );
//     getCanvas().addInputEventListener( changeCursorHandler );

    keyEventHandler = new PBasicInputEventHandler () {
   
        PText typeAheadNode = new PText();
        PPath background = new PPath();
        StringBuffer typeBuffer = new StringBuffer();
        int length = 0;
        boolean space_down = false;
        boolean slash_pressed = false;


        protected void selectAndZoom () {
          String search_string;
          if ( length == 0 ) {
            search_string = "";
          } else {
            search_string = typeBuffer.toString()+"*";
          }
          GinyUtils.deselectAllNodes( Cytoscape.getCurrentNetworkView() );
          typeAheadNode.setText( typeBuffer.toString() );
          CyNetworkUtilities.selectNodesStartingWith( Cytoscape.getCurrentNetwork(),
                                                      search_string,
                                                      Cytoscape.getCurrentNetworkView() );
//           cytoscape.actions.ZoomSelectedAction.zoomSelected();
        }
        
        protected void resetFind () {
          slash_pressed = false;
          length = 0;
          typeBuffer = new StringBuffer();
          typeAheadNode.setText("");
          getCanvas().getCamera().removeChild( typeAheadNode );
        }
        

        public void keyPressed ( PInputEvent event ) {
          
          //System.out.println( "Key Code Pressed: "+event.getKeyCode() );
          //System.out.println( "Key text: "+KeyEvent.getKeyText( event.getKeyCode() ) );

          if ( event.getKeyCode() == KeyEvent.VK_SPACE ) {
            space_down = true;
            getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            getCanvas().getPanEventHandler().
              setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
            if ( nodeSelection ) {
              getCanvas().removeInputEventListener( getSelectionHandler() );
            }
            if ( edgeSelection ) {
              getCanvas().removeInputEventListener( getEdgeSelectionHandler() );
            }
            
          }

          else if ( !slash_pressed && event.getKeyCode() == KeyEvent.VK_SLASH ) {
            //System.out.println( "start taf " );
            slash_pressed = true;
            getCanvas().getCamera().addChild( typeAheadNode );
            typeAheadNode.setOffset( 20, 20 );
            typeAheadNode.setPaint( new java.awt.Color( 0f, 0f, 0f, .6f ) );
            typeAheadNode.setTextPaint( java.awt.Color.white );
            typeAheadNode.setFont( typeAheadNode.getFont().deriveFont( 30f ) );
            
          }
          
          else if ( slash_pressed && event.getKeyCode() != KeyEvent.VK_ESCAPE &&  event.getKeyCode() != KeyEvent.VK_BACK_SPACE ) {
            //System.out.println( "Normal Press" );
            typeBuffer.append( KeyEvent.getKeyText( event.getKeyCode() ) );
            length++;
            selectAndZoom();
           
          } 
            
          else if ( slash_pressed && event.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            // System.out.println( "ESCAPRE PRESSED" );
            resetFind();
          }
          else if ( slash_pressed && event.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
            //System.out.println( "back space: "+length+" "+typeBuffer.toString() );
            if ( length != 0 ) {
              typeBuffer.deleteCharAt( length - 1);
              length--;
            }
            selectAndZoom();
            return;
          }


        }
           
        public void keyReleased ( PInputEvent event ) {
          if ( space_down ) {
            space_down = false;
            getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            getCanvas().getPanEventHandler().
              setEventFilter(new PInputEventFilter(InputEvent.BUTTON2_MASK));
            if ( nodeSelection ) {
              getCanvas().addInputEventListener( getSelectionHandler() );
            }
            if ( edgeSelection ) {
              getCanvas().addInputEventListener( getEdgeSelectionHandler() );
            }
          }
        }
      }; 
    getCanvas().addInputEventListener(keyEventHandler );
    getCanvas().getRoot().getDefaultInputManager().setKeyboardFocus( keyEventHandler );

 
  }
  
  public CyNetworkView getView () {
    return ( CyNetworkView )this;
  }

  public CyNetwork getNetwork () {
    return ( CyNetwork )getGraphPerspective();
  }

  public String getTitle () {
    return title;
  }

  public void setTitle ( String new_title) {
    this.title = new_title;
  }
  
  //TODO: set up the proper focus
  public void redrawGraph( boolean layout, boolean vizmap ) { 
	  
    redrawGraph();
  }
   
  public void redrawGraph() {

    Cytoscape.getVisualMappingManager().applyAppearances();
    getCanvas().setInteracting( true );
    //getCanvas().paintImmediately();
    getCanvas().setInteracting( false );

  //   if (ggetVisualMappingManager() != null && this.visualMapperEnabled) {
//         getVisualMappingManager().applyAppearances();
//     }
  }

  public void toggleVisualMapperEnabled () {
    vizmapEnabled = !vizmapEnabled;
  }

  public void setVisualMapperEnabled ( boolean state ) {
    vizmapEnabled = state;
  }

  public boolean getVisualMapperEnabled () {
    return vizmapEnabled;
  }

  public cytoscape.visual.VisualMappingManager getVizMapManager() {
    return Cytoscape.getVisualMappingManager();
  }

  public cytoscape.visual.ui.VizMapUI getVizMapUI() {
    return Cytoscape.getDesktop().getVizMapUI();
  }

  //------------------------------//
  // Client Data
  //------------------------------//

  /**
   * Networks can support client data.
   * @param data_name the name of this client data
   */
  public void putClientData ( String data_name, Object data ) {
    clientData.put( data_name, data );
  }

  /**
   * Get a list of all currently available ClientData objects
   */
  public Collection getClientDataNames () {
    return clientData.keySet();
  }
  
  /**
   * Get Some client data
   * @param data_name the data to get
   */
  public Object getClientData ( String data_name ) {
    return clientData.get( data_name );
  }
  

  //------------------------------//
  // Event Handling and Response


  /**
   * Overwritten version of fireGraphViewChanged so that
   * the label can be updated
   */
  protected void fireGraphViewChanged ( ChangeEvent event ) {
    updateStatusLabel();
    // fire the event to everyone else.
    super.fireGraphViewChanged( event );
  }

  /**
   * Resets the info label status bar text with the current number of
   * nodes, edges, selected nodes, and selected edges.
   */
  public void updateStatusLabel() {
  
    int nodeCount = getNodeViewCount();
    int edgeCount = getEdgeViewCount();
    int selectedNodes = getSelectedNodes().size();
    int selectedEdges = getSelectedEdges().size();

    statusLabel.setText("  Nodes: " + nodeCount
                      + " ("+selectedNodes+" selected)"
                      + " Edges: " + edgeCount
                      + " ("+selectedEdges+" selected)" );
  }

  //-------------------------------//
  // Layouts and VizMaps
  


  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge ) {
    return applyVizMap( ( EdgeView )getEdgeView( edge ) );
  }

  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( EdgeView edge_view ) {
    return applyVizMap( edge_view, ( VisualStyle )getClientData( CytoscapeDesktop.VISUAL_STYLE ) );
  }
                        
  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( CyEdge edge, VisualStyle style ) {
    return applyVizMap( ( EdgeView )getEdgeView( edge ), style );
  }
  /**
   * Applies the given edge to the given vizmapper
   */
  public boolean applyVizMap ( EdgeView edge_view, VisualStyle style ) {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle( style );
    Cytoscape.getVisualMappingManager().vizmapEdge( edge_view, this );
    Cytoscape.getDesktop().setVisualStyle( old_style );
    return true;
  }
      
  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node ) {
    return applyVizMap( ( NodeView )getNodeView( node ) );
  }

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( NodeView node_view ) {
    return applyVizMap( node_view, ( VisualStyle )getClientData( CytoscapeDesktop.VISUAL_STYLE ) );
  }

  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( CyNode node, VisualStyle style ) {
    return applyVizMap( ( NodeView )getNodeView( node ), style );
  }
  
  /**
   * Applies the given node to the given vizmapper
   */
  public boolean applyVizMap ( NodeView node_view, VisualStyle style ) {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle( style );
    Cytoscape.getVisualMappingManager().vizmapNode( node_view, this );
    Cytoscape.getDesktop().setVisualStyle( old_style );
    return true;
  }

  /**
   * @param style the visual style
   */
  public void applyVizmapper ( VisualStyle style ) {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle( style );
    redrawGraph( false, true );
  }
    
  /**
   * Applies the given layout to the entire CyNetworkView
   */
  public void applyLayout ( LayoutAlgorithm layout ) {
    layout.doLayout();
  }

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges ) {
    layout.lockNodes( convertToViews( nodes ) );
    layout.doLayout();
  }

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( LayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges) {
    layout.lockNodes( getInverseViews( convertToViews( nodes ) ) );
    layout.doLayout();
  }


  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given NodeViews and EdgeViews in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges ) {
    layout.lockNodes(  nodes );
    layout.doLayout();
  }

  /**
   * Applies the  given layout to only the given NodeViews and EdgeViews
   */
  public void applyLayout ( LayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges ) {
    layout.lockNodes( getInverseViews( nodes ) );
    layout.doLayout();
  }

  /**
   * Applies the given layout to the entire CyNetworkView,
   * but locks the given Nodes and Edges in place
   */
  public void applyLockedLayout ( LayoutAlgorithm layout, int[] nodes, int[] edges ) {
    layout.lockNodes( convertToNodeViews( nodes ) );
    layout.doLayout();
  }

  /**
   * Applies the  given layout to only the given Nodes and Edges
   */
  public void applyLayout ( LayoutAlgorithm layout, int[] nodes, int[] edges ) {

    layout.lockNodes( getInverseViews( convertToNodeViews( nodes ) ) );
    layout.doLayout();
  }

  //--------------------//
  // Convience Methods

   /**
   * Sets the Given nodes Selected
   */
  public boolean setSelected ( CyNode[] nodes ) {
    return setSelected( convertToViews( nodes ) );
  }

  /**
   * Sets the Given nodes Selected
   */
  public boolean setSelected ( NodeView[] node_views ) {
    for ( int i = 0; i < node_views.length; ++i ) {
      node_views[i].select();
    }
    return true;
  }

   /**
   * Sets the Given edges Selected
   */
  public boolean setSelected ( CyEdge[] edges ) {
     return setSelected( convertToViews( edges ) );
  }

  /**
   * Sets the Given edges Selected
   */
  public boolean setSelected ( EdgeView[] edge_views ) {
    for ( int i = 0; i < edge_views.length; ++i ) {
      edge_views[i].select();
    }
    return true;
  }


  protected NodeView[] convertToViews ( CyNode[] nodes ) {
    NodeView[] views = new NodeView[ nodes.length ];
    for ( int i = 0; i < nodes.length; ++i ) {
      views[i] = getNodeView( nodes[i] );
    }
    return views;    
  }

  protected EdgeView[] convertToViews ( CyEdge[] edges ) {
    EdgeView[] views = new EdgeView[ edges.length ];
    for ( int i = 0; i < edges.length; ++i ) {
      views[i] = getEdgeView( edges[i] );
    }
    return views;    
  }


  protected NodeView[] convertToNodeViews ( int[] nodes ) {
    NodeView[] views = new NodeView[ nodes.length ];
    for ( int i = 0; i < nodes.length; ++i ) {
      views[i] = getNodeView( nodes[i] );
    }
    return views;    
  }

  protected EdgeView[] convertToEdgeViews ( int[] edges ) {
    EdgeView[] views = new EdgeView[ edges.length ];
    for ( int i = 0; i < edges.length; ++i ) {
      views[i] = getEdgeView( edges[i] );
    }
    return views;    
  }



  protected NodeView[] getInverseViews ( NodeView[] given ) {
    NodeView[] inverse = new NodeView[ getNodeViewCount() - given.length ];
    List node_views = getNodeViewsList();
    int count = 0;
    Iterator i = node_views.iterator();
    Arrays.sort( given );
    while ( i.hasNext() ) {
      NodeView view = ( NodeView )i.next();
      if ( Arrays.binarySearch( given, view ) < 0 ) {
        // not a given, add
        inverse[count] = view;
        count++;
      }
    }
    return inverse;
  }

  protected EdgeView[] getInverseViews ( EdgeView[] given ) {
    EdgeView[] inverse = new EdgeView[ getEdgeViewCount() - given.length ];
    List edge_views = getEdgeViewsList();
    int count = 0;
    Iterator i = edge_views.iterator();
    Arrays.sort( given );
    while ( i.hasNext() ) {
      EdgeView view = ( EdgeView )i.next();
      if ( Arrays.binarySearch( given, view ) < 0 ) {
        // not a given, add
        inverse[count] = view;
        count++;
      }
    }
    return inverse;
  }
  // AJK: 05/19/06 BEGIN
  //   for context menus (just a compilation stub)
  public void addNodeContextMenuListener (NodeContextMenuListener l) {};

  public void removeNodeContextMenuListener (NodeContextMenuListener l){};

  public void addEdgeContextMenuListener(EdgeContextMenuListener l){};
 
  public void removeEdgeContextMenuListener(EdgeContextMenuListener l){};
  // AJK: 05/19/06 END
  
  
}

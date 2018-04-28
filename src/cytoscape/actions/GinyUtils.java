
/*
  File: GinyUtils.java 
  
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
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------

import giny.model.*;
import giny.view.*;
import java.util.*;

//-------------------------------------------------------------------------
/**
 * Utility operations for selection and hiding/unhiding nodes and edges
 * in a Giny GraphView. Most operations are self-explanatory.
 */
public class GinyUtils {
    
    public static void hideSelectedNodes(GraphView view) {
        //hides nodes and edges between them
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            // use GINY methods
            view.hideGraphObject( nview );
            
//             int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
//             for ( int i2 = 0; i2 < na.length; ++i2 ) {
//                 int[] edges = view.getGraphPerspective().
//                 getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true, true );
//                 if( edges != null )
//                     //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
//                     for ( int j = 0; j < edges.length; ++j ) {
//                         // use GINY methods
//                         view.hideGraphObject( view.getEdgeView( edges[j] ) );
//                     }
//             }
        }
        view.updateView();
    }
    
    public static void unHideSelectedNodes(GraphView view) {
        //hides nodes and edges between them
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            view.showGraphObject( nview );
            
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.
                getGraphPerspective().
                getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true, true );
                //if( edges != null )
                //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
                for ( int j = 0; j < edges.length; ++j ) {
                    view.showGraphObject( view.getEdgeView( edges[j] ) );
                }
            }
        }
        view.updateView();
    }
    
    public static void unHideAll(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            view.showGraphObject( nview );
        }
        for (Iterator ei = view.getEdgeViewsList().iterator(); ei.hasNext(); ) {
            EdgeView eview =(EdgeView) ei.next();
            view.showGraphObject( eview );
        }	
        view.updateView();
    }
    
    public static void unHideNodesAndInterconnectingEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            Node n = nview.getNode();
            
            view.showGraphObject( nview );
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.getGraphPerspective().getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true );
                if( edges != null )
                for ( int j = 0; j < edges.length; ++j ) {
                    EdgeView ev = view.getEdgeView( edges[j] );
                    view.showGraphObject( ev );
                } else {
                    //	System.out.println( "Ah" +ev.getClass().toString());		
                }
            }
        }
        view.updateView();
    }

    public static void hideSelectedEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.hideGraphObject( eview );
        }
        view.updateView();
    }
    
    public static void unHideSelectedEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.showGraphObject( eview );
        }
        view.updateView();
    }
    
    
    public static void invertSelectedNodes(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            nview.setSelected( !nview.isSelected() );
        }
        view.updateView();
    }
    
    public static void invertSelectedEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            eview.setSelected( !eview.isSelected() );
        }
        view.updateView();
    }
    
    public static void selectFirstNeighbors(GraphView view) {
        if (view == null) {return;}
        
        GraphPerspective graphPerspective = view.getGraphPerspective();
        Set nodeViewsToSelect = new HashSet();
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            Node n = nview.getNode();
            for (Iterator ni = graphPerspective.neighborsList(n).iterator(); ni.hasNext(); ) {
                Node neib =(Node) ni.next();
                NodeView neibview = view.getNodeView(neib);
                nodeViewsToSelect.add(neibview);
            }
        }
        for (Iterator si = nodeViewsToSelect.iterator(); si.hasNext(); ) {
            NodeView nview = (NodeView)si.next();
            nview.setSelected(true);
        }
        view.updateView();
    }
    
    public static void selectAllNodes(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            nview.setSelected( true );
        }
        view.updateView();
    }
    
    public static void deselectAllNodes(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            nview.setSelected( false );
        }
        view.updateView();
    }

    
    public static void selectAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            eview.setSelected( true );
        }
        view.updateView();
    }
    
    public static void deselectAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            eview.setSelected( false );
        }
        view.updateView();
    }
    
    public static void hideAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.hideGraphObject( eview );
        }
        view.updateView();
    }
    
    public static void unHideAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.showGraphObject( eview );
        }
        view.updateView();
    }
}


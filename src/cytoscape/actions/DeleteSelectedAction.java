
/*
  File: DeleteSelectedAction.java 
  
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
// $Revision: 8472 $
// $Date: 2006-10-16 17:55:34 -0700 (Mon, 16 Oct 2006) $
// $Author: mes $
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import giny.model.GraphPerspective;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
//-------------------------------------------------------------------------
/**
 * Giny version of the original class. Note that the original version was
 * only available when editing mode was enabled, and caused the selected
 * nodes to be permanently removed from the graph (and, necessarily, the view).
 * This version hides the selected nodes from both the graph and the view,
 * as there are currently no methods to remove a node view from the graph view
 * in Giny. The semantics of this and related classes for modifying the
 * graph and view should be clarified.
 */
public class DeleteSelectedAction extends AbstractAction {
    CyNetworkView networkView;
    
    public DeleteSelectedAction(CyNetworkView networkView) {
        super("Delete Selected Nodes and Edges");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "DeleteSelectedAction.actionPerformed";
        
        // AJK: 06/10/06 BEGIN
        //   make this action undo-able
        final List nodes = networkView.getSelectedNodes();
        final List edges = networkView.getSelectedEdges();        
        
        // AJK: 06/10/06 END
        
        GraphView view = networkView.getView();
        GraphPerspective perspective = view.getGraphPerspective();
        // get the Selected node and edge indices
        final int[] node_indicies = view.getSelectedNodeIndices();
        final int[] edge_indicies = view.getSelectedEdgeIndices();
        //and the node/edge vew objects
        final List selected_nodeViews = view.getSelectedNodes();
        final List selected_edgeViews = view.getSelectedEdges();

        // AJK: 06/11/06 BEGIN
        //   grab offsets for un-doing
//        List offsets = new ArrayList();
//        for (Iterator i = selected_nodeViews.iterator(); i.hasNext();)
//        {
//        	offsets.add(((NodeView) i.next()).getOffset());
//        }
//        final List offsetsList = offsets;
//        
        // AJK: 06/11/06 END
        
        // Hide the viewable things and the perspective refs
        view.hideGraphObjects( selected_nodeViews );
        view.hideGraphObjects( selected_edgeViews );
        perspective.hideEdges( edge_indicies );
        perspective.hideNodes( node_indicies );
        
 
        
        networkView.redrawGraph(false, false);
        
        // AJK: 06/10/06 BEGIN
        //     make this action undo-able
        
        System.out.println ("adding undoableEdit to undoManager: " + 
        		Cytoscape.getDesktop().undo);
 
		Cytoscape.getDesktop().undo.addEdit(new AbstractUndoableEdit() {

			final String network_id = networkView.getNetwork().getIdentifier();

			public String getPresentationName() {
				// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
//				return "Delete";
				return  "Remove";
			}

			public String getRedoPresentationName() {
				if (edges.size() == 0)
					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
					return "Redo: Removed Nodes";
//					return " ";
				else
					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
					return "Redo: Removed Nodes and Edges";
//					return " ";
		}

			public String getUndoPresentationName() {

				if (edges.size() == 0)
					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
					return "Undo: Removed Nodes";
//					return null;
				else
					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
					return "Undo: Removed Nodes and Edges";
//					return null;
			}

			public void redo() {
				super.redo();
				// removes the removed nodes and edges from the network
//				CyNetwork network = Cytoscape.getNetwork(network_id);
//
//					network.hideEdges(edges);
//					network.hideNodes(nodes);
			    GraphView view = networkView.getView();
				GraphPerspective perspective = view.getGraphPerspective();
				view.hideGraphObjects(selected_nodeViews);
				view.hideGraphObjects(selected_edgeViews);
				perspective.hideEdges(edge_indicies);
				perspective.hideNodes(node_indicies);
			}

			public void undo() {
				super.undo();
//				CyNetwork network = Cytoscape.getNetwork(network_id);
//				if (network != null) {
//					network.restoreNodes(nodes);
//					network.restoreEdges(edges);
			    GraphView view = networkView.getView();
				GraphPerspective perspective = view.getGraphPerspective();
				view.showGraphObjects(selected_nodeViews);
				view.showGraphObjects(selected_edgeViews);
				perspective.restoreEdges(edge_indicies);
				perspective.restoreNodes(node_indicies);
//				for (int i = 0; i < selected_nodeViews.size(); i++)
//				{
//					((NodeView) selected_nodeViews.get(i)).setOffset(
//							((Point2D) offsetsList.get(i)).getX(),
//							((Point2D) offsetsList.get(i)).getY());
//				}					
			}

		});        
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null,
				networkView.getNetwork());
       
        // AJK: 06/10/06
    } // actionPerformed
} // inner class DeleteSelectedAction

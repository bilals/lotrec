
/*
  File: FlagAndSelectionHandler.java 
  
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

//---------------------------------------------------------------------------
//  $Revision: 7760 $ 
//  $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
//  $Author: mes $
//---------------------------------------------------------------------------
package cytoscape.view;
//---------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import giny.model.*;
import giny.view.*;

import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEventListener;
import cytoscape.data.FlagEvent;
//---------------------------------------------------------------------------
/**
 * This class synchronizes the flagged status of nodes and edges as held by a
 * FlagFilter object of a network with the selection status of the corresponding
 * node and edge views in a GraphView. An object will be selected in the view
 * iff the matching object is flagged in the FlagFilter.
 * This class is only used by PhoebeNetworkView, which no longer used anywhere.
 */
public class FlagAndSelectionHandler implements FlagEventListener, GraphViewChangeListener {
    
    FlagFilter flagFilter;
    GraphView view;
    
    /**
     * Standard constructor takes the flag filter and the view that should be
     * synchronized. On construction, this object will synchronize the filter
     * and view by turning on flags or selections that are currently on in
     * one of the two objects.
     */
    public FlagAndSelectionHandler(FlagFilter flagFilter, GraphView view) {
        this.flagFilter = flagFilter;
        this.view = view;
        syncFilterAndView();
        flagFilter.addFlagEventListener(this);
        view.addGraphViewChangeListener(this);
    }
    
    /**
     * Synchronizes the filter and view of this object by selecting every
     * object that is currently flagged and vice versa.
     */
    private void syncFilterAndView() {
        Set flaggedNodes = flagFilter.getFlaggedNodes();
        Set flaggedEdges = flagFilter.getFlaggedEdges();
        List selectedNodes = view.getSelectedNodes();
        List selectedEdges = view.getSelectedEdges();
        //select all nodes that are flagged but not currently selected
        for (Iterator iter = flaggedNodes.iterator(); iter.hasNext(); ) {
            Node node = (Node)iter.next();
            NodeView nv = view.getNodeView(node);
            if ( nv == null || nv.isSelected() ) {continue;}
            nv.setSelected(true);
        }
        //select all edges that are flagged but not currently selected
        for (Iterator iter = flaggedEdges.iterator(); iter.hasNext(); ) {
            Edge edge = (Edge)iter.next();
            EdgeView ev = view.getEdgeView(edge);
            if ( ev == null || ev.isSelected() ) {continue;}
            ev.setSelected(true);
        }
        //flag all nodes that are selected but not currently flagged
        for (Iterator iter = selectedNodes.iterator(); iter.hasNext(); ) {
            NodeView nv = (NodeView)iter.next();
            Node node = nv.getNode();
            flagFilter.setFlagged(node, true); //does nothing if already flagged
        }
        //flag all edges that are selected but not currently flagged
        for (Iterator iter = selectedEdges.iterator(); iter.hasNext(); ) {
            EdgeView ev = (EdgeView)iter.next();
            Edge edge = ev.getEdge();
            flagFilter.setFlagged(edge, true); //does nothing if already flagged
        }
    }
    
    /**
     * Responds to selection events from the view by setting the matching flagged
     * state in the FlagFilter object.
     */
    public void graphViewChanged(GraphViewChangeEvent event) {

      

        //GINY bug: the event we get frequently has the correct indices
        //but incorrect Node and Edge objects. For now we get around this
        //by converting indices to graph objects ourselves
        GraphView source = (GraphView)event.getSource();
        RootGraph rootGraph = source.getGraphPerspective().getRootGraph();
        if (event.isNodesSelectedType()) {
          //System.out.println( "Nodes slected type:" );
          //System.out.println( "FlagAndSelectionHandler: "+event);
          //System.out.println( event.getSelectedNodeIndices()+" <- nodes selected" );
          int[] nodes = event.getSelectedNodeIndices();
          for ( int i = 0; i < nodes.length; ++i ) {
//             System.out.println( "Selected mnode: "+nodes[i]);
          }
            //Node[] selNodes = event.getSelectedNodes();
            //List selList = Arrays.asList(selNodes);
            int[] selIndices = event.getSelectedNodeIndices();
            List selList = new ArrayList();
            for (int index = 0; index < selIndices.length; index++) {
                Node node = rootGraph.getNode(selIndices[index]);
                //System.out.println( "Adding node: "+node);
                selList.add(node);
            }
            //System.out.println( "Contents of selList: " );
            //for( Iterator i = selList.iterator(); i.hasNext(); ) {
            //  System.out.println( "NOde: "+i.next() );
            //}

            flagFilter.setFlaggedNodes(selList, true);
        } else if (event.isNodesUnselectedType() || event.isNodesHiddenType()) {
            //Node[] unselNodes = event.getUnselectedNodes();
            //List unselList = Arrays.asList(unselNodes);
          //System.out.println( "nodes UNse;ected" );
          
            int[] unselIndices;
            if (event.isNodesUnselectedType()) {
              unselIndices = event.getUnselectedNodeIndices(); }
            else {
              unselIndices = event.getHiddenNodeIndices(); }
            List unselList = new ArrayList();
            for (int index = 0; index < unselIndices.length; index++) {
                Node node = rootGraph.getNode(unselIndices[index]);
                unselList.add(node);
                //System.out.println( "Unselected node:"+node+" "+node.getRootGraphIndex() );
            }
            flagFilter.setFlaggedNodes(unselList, false);
        } else if (event.isEdgesSelectedType()) {
            //Edge[] selEdges = event.getSelectedEdges();
            //List selList = Arrays.asList(selEdges);
            int[] selIndices = event.getSelectedEdgeIndices();
            List selList = new ArrayList();
            for (int index = 0; index < selIndices.length; index++) {
                Edge edge = rootGraph.getEdge(selIndices[index]);
                selList.add(edge);
            }
            flagFilter.setFlaggedEdges(selList, true);
        } else if (event.isEdgesUnselectedType() || event.isEdgesHiddenType()) {
            //Edge[] unselEdges = event.getUnselectedEdges();
            //List unselList = Arrays.asList(unselEdges);
            int[] unselIndices;
            if (event.isEdgesUnselectedType()) {
              unselIndices = event.getUnselectedEdgeIndices(); }
            else {
              unselIndices = event.getHiddenEdgeIndices(); }
            List unselList = new ArrayList();
            for (int index = 0; index < unselIndices.length; index++) {
                Edge edge = rootGraph.getEdge(unselIndices[index]);
                unselList.add(edge);
            }
            flagFilter.setFlaggedEdges(unselList, false);
        }
    }
    
    /**
     * Responds to events indicating a change in the flagged state of one or more
     * nodes or edges. Sets the corresponding selection state for views of those
     * objects in the graph view.
     */
    public void onFlagEvent(FlagEvent event) {
        if (event.getTargetType() == FlagEvent.SINGLE_NODE) {//single node
            setNodeSelected( (Node)event.getTarget(), event.getEventType() );
        } else if (event.getTargetType() == FlagEvent.SINGLE_EDGE) {//single edge
            setEdgeSelected( (Edge)event.getTarget(), event.getEventType() );
        } else if (event.getTargetType() == FlagEvent.NODE_SET) {//multiple nodes
            Set nodeSet = (Set)event.getTarget();
            for (Iterator iter = nodeSet.iterator(); iter.hasNext(); ) {
                Node node = (Node)iter.next();
                setNodeSelected( node, event.getEventType() );
            }
        } else if (event.getTargetType() == FlagEvent.EDGE_SET) {//multiple edges
            Set edgeSet = (Set)event.getTarget();
            for (Iterator iter = edgeSet.iterator(); iter.hasNext(); ) {
                Edge edge = (Edge)iter.next();
                setEdgeSelected( edge, event.getEventType() );
            }
        } else {//unexpected target type
            return;
        }
    }
    
    /**
     * Helper method to set selection for a node view.
     */
    private void setNodeSelected(Node node, boolean selectOn) {
        NodeView nodeView = view.getNodeView(node);
        if (nodeView == null) {return;} //sanity check
        //Giny fires a selection event even if there's no change in state
        //we trap this by only requesting a selection if there's a change
        if (nodeView.isSelected() != selectOn) {
            nodeView.setSelected(selectOn);
        }
    }
    
    /**
     * Helper method to set selection for an edge view.
     */
    private void setEdgeSelected(Edge edge, boolean selectOn) {
        EdgeView edgeView = view.getEdgeView(edge);
        if (edgeView == null) {return;} //sanity check
        //Giny fires a selection event even if there's no change in state
        //we trap this by only requesting a selection if there's a change
        if (edgeView.isSelected() != selectOn) {
            edgeView.setSelected(selectOn);
        }
    }
}


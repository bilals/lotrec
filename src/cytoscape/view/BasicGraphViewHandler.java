
/*
  File: BasicGraphViewHandler.java 
  
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

/**
 * @author Iliana Avila-Campillo <iavila@systemsbiology.org>
 * @version %I%, %G%
 * @since 2.0
 */

package cytoscape.view;

import java.util.*;
import cern.colt.list.IntArrayList;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.Edge;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.EdgeView;
import giny.view.NodeView;

/**
 * A basic <code>GraphViewHandler</code> that simply reflects <code>GraphPerspective</code>
 * changes on a given <code>GraphView</code>
 */

public class BasicGraphViewHandler implements GraphViewHandler {

  /**
   * Constructor
   */
  public BasicGraphViewHandler (){}//BasicGraphViewHandler
  
  /**
   * Handles the event as desired by updating the given <code>giny.view.GraphView</code>.
   *
   * @param event the event to handle
   * @param graph_view the <code>giny.view.GraphView</code> that views the 
   * <code>giny.model.GraphPerspective</code> that generated the event and that should
   * be updated as necessary
   */
  public void handleGraphPerspectiveEvent (GraphPerspectiveChangeEvent event, GraphView graph_view){
    
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.handleGraphPerspectiveEvent().");
    
    int numTypes = 0; // An event may have more than one type
    
    // Node Events:
    if(event.isNodesHiddenType()){
      //TODO: Remove
      //System.out.println("isNodesHiddenType == " + event.isNodesHiddenType());
      removeGraphViewNodes(graph_view, event.getHiddenNodeIndices());
      numTypes++;
    }

    if(event.isNodesRestoredType()){
      //TODO: Remove
      //System.out.println("isNodesRestoredType == " + event.isNodesRestoredType());
      restoreGraphViewNodes(graph_view, event.getRestoredNodeIndices(), true);
      numTypes++;
    }
    
    // A GraphPerspective cannot have selected graph objects (Rowan told me)
    //if(event.isNodesSelectedType()){
    //selectGraphViewNodes(graph_view, event.getSelectedNodes());
    //numTypes++;
    //}
    
    // Same as above
    //if(event.isNodesUnselectedType()){
    //unselectGraphViewNodes(graph_view, event.getUnselectedNodes());
    //numTypes++;
    //}
    
    // Edge events:
    if(event.isEdgesHiddenType()){
      //TODO: Remove
      //System.out.println("isEdgesHiddenType == " + event.isEdgesHiddenType());
      removeGraphViewEdges(graph_view, event.getHiddenEdgeIndices());
      numTypes++;
    }
    
    if(event.isEdgesRestoredType()){
      //TODO: Remove
      //System.out.println("isEdgesRestoredType == " + event.isEdgesRestoredType());
      restoreGraphViewEdges(graph_view, event.getRestoredEdgeIndices());
      numTypes++;
    }
    
    // A GraphPerspective cannot have selected graph objects (Rowan told me)
    //if(event.isEdgesSelectedType()){
    //selectGraphViewEdges(graph_view, event.getSelectedEdges());
    //numTypes++;
    //}
    
    // Same as above
    //if(event.isEdgesUnselectedType()){
    //unselectGraphViewEdges(graph_view, event.getUnselectedEdges());
    //numTypes++;
    //}
    
    if(numTypes == 0){
      //System.err.println("In BasicGraphViewHandler.handleGraphPerspectiveEvent, "
      //+ "unrecognized event type");
      return;
    }
    
    graph_view.updateView();
              
//     if ( graph_view instanceof cytoscape.giny.Phoeb*NetworkView ) {
//       ( ( cytoscape.giny.Phoeb*NetworkView )graph_view ).redrawGraph();
//     }

    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.handleGraphPerspectiveEvent()." +
    //" numTypes caught = " + numTypes);
  }//handleGraphPerspectiveEvent

  /**
   * It removes the views of the edges in the array from the given <code>giny.view.GraphView</code> 
   * object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which edges will be removed
   * @param edges the edges whose views will be removed
   * @return an array of edges that were removed
   */
  // TESTED: Gets an exception because the edges array has references to null.
  // USE INSTEAD: removeGraphViewEdges(GraphView, int [])
  static public Edge []  removeGraphViewEdges (GraphView graph_view,
                                             Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.removeGraphViewEdges()");
    Set removedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.removeEdgeView(edges[i]);
      if(edgeView != null){
        removedEdges.add(edges[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewEdges()," + "num removed edges = " + removedEdges.size());
    return (Edge[])removedEdges.toArray(new Edge[removedEdges.size()]);
  }//removeGraphViewEdges
  
  /**
   * It removes the views of the edges in the array from the given <code>giny.view.GraphView</code> 
   * object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which edges will be removed
   * @param edge_indices the indices of the edges that will be removed
   * @return an array of edge indices that were removed
   */
  // TESTED
  // NOTE: USE THIS INSTEAD OF removeGraphViewEdges (GraphView,Edge[])
  static public int []  removeGraphViewEdges (GraphView graph_view,
                                              int [] edge_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.removeGraphViewEdges()");
    IntArrayList removedEdges = new IntArrayList(edge_indices.length);
    for(int i = 0; i < edge_indices.length; i++){
      EdgeView edgeView = graph_view.removeEdgeView(edge_indices[i]);
      if(edgeView != null){
        removedEdges.add(edge_indices[i]);
      }
    }//for i
    removedEdges.trimToSize();
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewEdges()," + "num removed edges = " + removedEdges.size());
    return removedEdges.elements();
  }//removeGraphViewEdges
  
  /**
   * It restores the views of the edges in the array in the given <code>giny.view.GraphView</code> 
   * object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be restored
   * @param edges the edges that will be restored
   * @return an array of edges that were restored
   */
  // TESTED
  static public Edge [] restoreGraphViewEdges (GraphView graph_view,
                                               Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewEdges()");
    Set restoredEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      boolean restored = false;
      if(edgeView == null){
        // This means that the restored edge had not been viewed before
        // by graph_view
        edgeView = graph_view.addEdgeView(edges[i].getRootGraphIndex());
        if(edgeView != null){
          restored = true;
        }
      }else{
        // This means that the restored edge had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show it
        restored = graph_view.showGraphObject(edgeView);
      }
      if(restored){
        restoredEdges.add(edgeView.getEdge());
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewEdges(), "+"num restored edges = " + restoredEdges.size() );
    return (Edge[])restoredEdges.toArray(new Edge[restoredEdges.size()]);
  }//restoreGraphViewEdges

  /**
   * It restores the views of the edges with the given indices in the given 
   * <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges' views 
   * will be restored
   * @param edge_indices the indices of the edges that will be restored
   * @return an array of indices of edges that were restored
   */
  // TODO: What if a connected node is not in the graph view or graph perspective?
  static public int [] restoreGraphViewEdges (GraphView graph_view,
                                              int [] edge_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewEdges()");
    IntArrayList restoredEdgeIndices = new IntArrayList(edge_indices.length);
    for(int i = 0; i < edge_indices.length; i++){
      // TEST: See if the NodeViews of the connected Nodes are in graph_view
      // TODO: What to do in this case? I would say throw exception.
      //GraphPerspective graphPerspective = graph_view.getGraphPerspective();
      //int sourceRootIndex = 
      // graphPerspective.getRootGraphNodeIndex(graphPerspective.getEdgeSourceIndex(edge_indices[i]));
      //int targetRootIndex =
      //graphPerspective.getRootGraphNodeIndex(graphPerspective.getEdgeTargetIndex(edge_indices[i]));
      //NodeView sourceNodeView = graph_view.getNodeView(sourceRootIndex);
      //NodeView targetNodeView = graph_view.getNodeView(targetRootIndex);
      //if(sourceNodeView == null){
      //System.err.println("ERROR: Source NodeView for edge "+edge_indices[i]+" is null");
      //}
      //if(targetNodeView == null){
      //System.err.println("ERROR: Target NodeView for edge "+edge_indices[i]+" is null");
      //}

      // The given index can be either RootGraph index or GraphPerspective index
      EdgeView edgeView = graph_view.getEdgeView(edge_indices[i]);
      boolean restored = false;
      if(edgeView == null){
        // This means that the restored edge had not been viewed before
        // by graph_view
        edgeView = graph_view.addEdgeView(edge_indices[i]);
        if(edgeView != null){
          restored = true;
        }
      }else{
        // This means that the restored edge had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show it
        restored = graph_view.showGraphObject(edgeView);
      }
      if(restored){
        restoredEdgeIndices.add(edge_indices[i]);
      }
    }//for i
    
    restoredEdgeIndices.trimToSize();
    
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewEdges(), "+"num restored edges = " + restoredEdgeIndices.size() );
    return restoredEdgeIndices.elements();
  }//restoreGraphViewEdges
  
  /**
   * It selects the edges in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be selected
   * @param edges the edges in <code>graph_view</code> that will be selected
   * @return the edges that were selected
   */
  static public Edge [] selectGraphViewEdges (GraphView graph_view,
                                              Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.selectGraphViewEdges()");
    Set selectedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      if(edgeView != null){
        edgeView.setSelected(true);
        selectedEdges.add(edges[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.selectGraphViewEdges()," +"num selected edges = " + selectedEdges.size());
    return (Edge[])selectedEdges.toArray(new Edge[selectedEdges.size()]);
  }//selectGraphViewEdges

  /**
   * It unselects the edges in the array in the given  <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be unselected
   * @param edges the edges that will be unselected in <code>graph_view</code>
   * @return an array of edges that were unselected
   */
  static public Edge[] unselectGraphViewEdges (GraphView graph_view,
                                               Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.unselectGraphViewEdges()");
    Set unselectedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      if(edgeView != null){
        edgeView.setSelected(false);
        unselectedEdges.add(edges[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.unselectGraphViewEdges()," +"num unselected edges = " + unselectedEdges.size());
    return (Edge[])unselectedEdges.toArray(new Edge[unselectedEdges.size()]);
  }//unselectGraphViewEdges

  /**
   * It removes the nodes in the array from the given <code>giny.view.GraphView</code> object,
   * it also removes the connected edges to these nodes (an edge without a connecting node makes
   * no mathematical sense).
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which nodes will be removed
   * @param nodes the nodes whose views will be removed from <code>graph_view</code>
   * @return an array of nodes that were removed
   */
  // NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
  // and this hiding fires a hideEdgesEvent, so removeGraphViewEdges will get called on those
  // edges and we don't need to hide them in this method
  // TESTED
  static public Node[] removeGraphViewNodes (GraphView graph_view,
                                           Node [] nodes){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.removeGraphViewNodes()");
    Set removedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      NodeView nodeView = graph_view.removeNodeView(nodes[i]);
      if(nodeView != null){ 
        removedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewNodes(), " +"num removed nodes = " + removedNodes.size());
    return (Node[])removedNodes.toArray(new Node[removedNodes.size()]);
  }//removeGraphViewNodes

   /**
   * It removes the views of the nodes with the given indices that are contained in the given 
   * <code>giny.view.GraphView</code> object, it also removes the connected edges to 
   * these nodes (an edge without a connecting node makes no mathematical sense).
   *
   * @param graph_view the <code>giny.view.GraphView</code> object from which nodes will be removed
   * @param node_indices the indices of the nodes that will be removed
   * @return an array of indices of nodes that were removed
   */
  // NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
  // and this hiding fires a hideEdgesEvent, so removeGraphViewEdges will get called on those
  // edges and we don't need to remove them in this method
  static public int[] removeGraphViewNodes (GraphView graph_view,
                                          int [] node_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.removeGraphViewNodes()");
    IntArrayList removedNodesIndices = new IntArrayList(node_indices.length);
    for(int i = 0; i < node_indices.length; i++){
      NodeView nodeView = graph_view.removeNodeView(node_indices[i]);
      if(nodeView != null){
        removedNodesIndices.add(node_indices[i]);
      }
    }//for i
    removedNodesIndices.trimToSize();
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.removeGraphViewNodes(), " +"num removed nodes = " + removedNodesIndices.size());

    return removedNodesIndices.elements();
  }//removeGraphViewNodes
  
  /**
   * It restores the views of the nodes in the array in the given 
   * <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be restored
   * @param nodes the nodes whose views will be restored in <code>graph_view</code>
   * @param restore_connected_edges whether or not the connected edges to the restored nodes
   * should also be restored or not (for now this argument is ignored)
   * @return an array of nodes that were restored
   */
  // TODO: Depending on restore_connected_edges, restore connected edges or not.
  // TESTED
  static public Node[] restoreGraphViewNodes (GraphView graph_view,
                                              Node [] nodes,
                                              boolean restore_connected_edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewNodes()");
    Set restoredNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      boolean restored = false;
      if(nodeView == null){
        // This means that the nodes that were restored had never been viewed by
        // the graph_view, so we need to create a new NodeView.
        nodeView = graph_view.addNodeView(nodes[i].getRootGraphIndex());
        if(nodeView != null){
          restored = true;
        }
      }else{
        // This means that the nodes that were restored had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show them
        restored = graph_view.showGraphObject(nodeView);
      }
      if(restored){
        //TODO: Remove
        //System.err.println("Restored node w/index " + nodes[i].getRootGraphIndex());
        positionToBarycenter(nodeView);
        restoredNodes.add(nodeView.getNode());
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewNodes()." +"Total restored nodes == " + restoredNodes.size());
    return (Node[])restoredNodes.toArray(new Node[restoredNodes.size()]);
  }//restoreGraphViewNodes

  /**
   * It restores the views of the nodes with the given indices in the given 
   * <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which node views will be restored
   * @param node_indices the indices of the nodes whose views will be restored
   * @param restore_connected_edges whether or not the connected edges to the restored nodes
   * should also be restored or not (for now this argument is ignored)
   * @return an array of indices of the nodes whose views were restored
   */
  //TODO: Depending on restore_connected_edges, restore connected edges or not.
  static public int[] restoreGraphViewNodes (GraphView graph_view,
                                              int [] node_indices,
                                              boolean restore_connected_edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewNodes()");
    IntArrayList restoredNodeIndices = new IntArrayList(node_indices.length);
    for(int i = 0; i < node_indices.length; i++){
      
      NodeView nodeView = graph_view.getNodeView(node_indices[i]);
      boolean restored = false;
      if(nodeView == null){
        // This means that the nodes that were restored had never been viewed by
        // the graph_view, so we need to create a new NodeView.
        nodeView = graph_view.addNodeView(node_indices[i]);
        if(nodeView != null){
          restored = true;
        }
      }else{
        // This means that the nodes that were restored had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show them
        restored = graph_view.showGraphObject(nodeView);
      }
      if(restored){
        restoredNodeIndices.add(node_indices[i]);
        positionToBarycenter(nodeView);
        //TODO: Remove
        //System.err.println("NodeView for node index " + node_indices[i] + " was added to graph_view");
      }else{
        //TODO: Remove
        //System.err.println("ERROR: NodeView for node index " + node_indices[i] +" was NOT added to graph_view");
      }
    }//for i
    restoredNodeIndices.trimToSize();
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewNodes()." +"Showed in graph_view/Restored in GP == " + restoredNodeIndices.size() +"/" + node_indices.length);
    return restoredNodeIndices.elements();
  }//restoreGraphViewNodes
  
  
  /**
   * It selects the nodes in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be selected
   * @param nodes the nodes in <code>graph_view</code> that will be selected
   * @return the nodes that were selected
   */
  static public Node [] selectGraphViewNodes (GraphView graph_view,
                                              Node [] nodes){
    //TODO: Remove
    ////System.out.println("In BasicGraphViewHandler.selectGraphViewNodes()"); 
    Set selectedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null){
        nodeView.setSelected(true);
        selectedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.selectGraphViewNodes(),"+"num selected nodes = " + selectedNodes.size()); 
    return (Node[])selectedNodes.toArray(new Node[selectedNodes.size()]);
  }//selectGraphViewNodes

  /**
   * It unselects the nodes in the array in the given  <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be unselected
   * @param nodes the nodes that will be unselected in <code>graph_view</code>
   * @return an array of nodes that were unselected
   */
  static public Node[] unselectGraphViewNodes (GraphView graph_view,
                                               Node [] nodes){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.unselectGraphViewNodes()");
    Set unselectedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null){
        nodeView.setSelected(false);
        unselectedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.unselectGraphViewNodes()," +"num unselected nodes = " + unselectedNodes.size());
    return (Node[])unselectedNodes.toArray(new Node[unselectedNodes.size()]);
  }//unselectGraphViewNodes

  /**
   * If the node that node_view represents is a meta-node, then it 
   * positions it at the barycenter of its viewable children nodes.
   *
   * @param node_view the <code>giny.view.NodeView</code> that will be positioned
   * to the barycenter of its children
   */
  static public void positionToBarycenter (NodeView node_view){
    Node node = node_view.getNode();
    int rootIndex = node.getRootGraphIndex();
    GraphView graphView = node_view.getGraphView();
    GraphPerspective gp = graphView.getGraphPerspective();
    
    int [] childrenNodeIndices = gp.getNodeMetaChildIndicesArray(rootIndex);
    if(childrenNodeIndices == null || childrenNodeIndices.length == 0){return;}
    
    GraphPerspective childGP = node.getGraphPerspective();
    if(childGP == null || childGP.getNodeCount() == 0){
      throw new IllegalStateException("Node " + node.getIdentifier() + " has a non-empty array " +
                                  " of children-node indices, but, it has no child GraphPerspective");
    }
    List childrenNodeList = childGP.nodesList();
    Iterator it = childrenNodeList.iterator();
    double x = 0.0;
    double y = 0.0;
    double viewableChildren = 0;
    while(it.hasNext()){
      Node childNode = (Node)it.next();
      if(gp.containsNode(childNode, false)){
        NodeView childNV = graphView.getNodeView(childNode.getRootGraphIndex());
        if(childNV != null){
          x += childNV.getXPosition();
          y += childNV.getYPosition();
          viewableChildren++;
        }
      }
    }//while it
    if(viewableChildren != 0){
      x /= viewableChildren;
      y /= viewableChildren;
      node_view.setXPosition(x);
      node_view.setYPosition(y);
    }
  }//positionToBarycenter

  /**
   * Updates the given graph_view to contain node and edge visual representations
   * of only nodes and edges that are in its <code>GraphPerspective</code>
   * 
   * @see GraphViewController#resumeListening()
   * @see GraphViewController#resumeListening(GraphView)
   */
  public void updateGraphView (GraphView graph_view){
    
    GraphPerspective graphPerspective = graph_view.getGraphPerspective();
    
    IntArrayList gpNodeIndices = new IntArrayList(graphPerspective.getNodeIndicesArray());
    IntArrayList gpEdgeIndices = new IntArrayList(graphPerspective.getEdgeIndicesArray());
    
    IntArrayList gvNodeIndices = new IntArrayList(graph_view.getNodeViewCount());
    IntArrayList gvEdgeIndices = new IntArrayList(graph_view.getEdgeViewCount());
    
    // Obtain a list of nodes' root indices that are represented in graph_view
    Iterator it = graph_view.getNodeViewsIterator();
    while(it.hasNext()){
      NodeView nodeView = (NodeView)it.next();
      Node gvNode = nodeView.getNode();
      if(gvNode == null){
        System.err.println("Node for nodeView is null (nodeView  = " + nodeView + ")");
        continue;
      }
      int nodeIndex = gvNode.getRootGraphIndex();
      gvNodeIndices.add(nodeIndex);
    }// while there are more graph view nodes
    
    // Obtain a list of edges that are represented in graph_view,
    // and remove EdgeViews that are no longer in graph_perspective
    it = graph_view.getEdgeViewsIterator();
    while(it.hasNext()){
      EdgeView edgeView = (EdgeView)it.next();
      Edge gvEdge = edgeView.getEdge();
      if(gvEdge == null){
        System.err.println("Edge for edgeView is null (edgeView  = " + edgeView + ")");
        continue;
      }
      int edgeIndex = gvEdge.getRootGraphIndex();
      gvEdgeIndices.add(edgeIndex);
    }// while there are more graph view edges
    
    // Make sure that graph_view represents all nodes that are
    // currently in graphPerspective
    for(int i = 0; i < gpNodeIndices.size(); i++){
      int nodeIndex = gpNodeIndices.getQuick(i);
      NodeView nodeView = graph_view.getNodeView(nodeIndex);
      if(nodeView == null){
        graph_view.addNodeView(nodeIndex);
      }else{
        graph_view.showGraphObject(nodeView);
      }
    }// for each graphPerspective node
    
    // Make sure that graph_view represents all edges that are
    // currently in graphPerspective
    for(int i = 0; i < gpEdgeIndices.size(); i++){
      int edgeIndex = gpEdgeIndices.getQuick(i);
      EdgeView edgeView = graph_view.getEdgeView(edgeIndex);
      if(edgeView == null){
        graph_view.addEdgeView(edgeIndex);
      }else{
        graph_view.showGraphObject(edgeView);
      }
    }// for each GraphPerspective edge

    // Remove from graph_view all edge representations that are not in graphPerspective
    gvEdgeIndices.removeAll(gpEdgeIndices);
    gvEdgeIndices.trimToSize();
    for( int i = 0;  i < gvEdgeIndices.size(); i++){
      graph_view.removeEdgeView(gvEdgeIndices.getQuick(i));
    }// for each edge that is in graph_view but that is not in graphPerspective
    
    // Remove from graph_view all node representations that are not in graphPerspective
    gvNodeIndices.removeAll(gpNodeIndices);
    gvNodeIndices.trimToSize();
    for( int i = 0;  i < gvNodeIndices.size(); i++){
      graph_view.removeNodeView(gvNodeIndices.getQuick(i));
    }// for each node that is in graph_view but that is not in graphPerspective
   

  }//updateGraphview

}//classs BasicGraphViewHandler

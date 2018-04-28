
/*
  File: GraphViewController.java 
  
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
 * @author Iliana Avila-Campillo
 * @version %I%, %G%
 * @since 2.0
 */

package cytoscape.view;
import java.util.*;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;
import giny.view.GraphView;
import cern.colt.list.IntArrayList;

public class GraphViewController 
  implements giny.model.GraphPerspectiveChangeListener{
  
  protected Map graphViewToHandler;  // a map of GraphViews to GraphViewHandlers
  protected Map gpToGv;              // a map of GraphPerspectives to GraphViews
  public static final GraphViewHandler DEFAULT_GRAPH_VIEW_HANDLER =
    new BasicGraphViewHandler();
  
  /**
   * Empty constructor, initializes class members to empty HashMaps.
   */
  public GraphViewController (){
    this.graphViewToHandler = new HashMap();
    this.gpToGv = new HashMap();
  }//GraphViewController
    
  /**
   * Constructor, assigns <code>GraphViewController.DEFAULT_GRAPH_VIEW_HANDLER</code>
   * to all the given <code>giny.view.GraphView</code> objects in the array.
   *
   * @param graph_views an array of <code>giny.view.GraphView</code> objects
   * that this <code>GraphViewController</code> will keep synchronized to
   * their corresponding <code>giny.model.GraphPerspective</code> objects
   * available through their <code>getGraphPerspective()</code> method.
   */
  public GraphViewController (GraphView [] graph_views){
    this.graphViewToHandler = new HashMap();
    this.gpToGv = new HashMap();
    setGraphViews(graph_views);
  }//GraphViewController
  
  /**
   * Constructor, specifies the <code>cytoscape.view.GraphViewHandler</code> objects
   * for the given <code>giny.view.GraphView</code> objects in the arrays.
   *
   * @param graph_views an array of <code>giny.view.GraphView</code> objects
   * that this <code>GraphViewController</code> will keep synchronized to
   * their corresponding <code>giny.model.GraphPerspective</code> objects
   * available through their <code>getGraphPerspective()</code> method
   * @param gv_to_handler a <code>Map</code> with <code>giny.view.GraphView</code>
   * objects as keys, and <code>cytoscape.view.GraphViewHandler</code> objects
   * as values, two or more different keys can share the same value, the
   * DEFAULT_GRAPH_VIEW_HANDLER will be set for <code>giny.view.GraphView</code>
   * objects that are not keys in the given <code>Map</code>
   */
  public GraphViewController (GraphView [] graph_views, Map gv_to_handler){
    this.graphViewToHandler = new HashMap();
    this.gpToGv = new HashMap();
    setGraphViews(graph_views, gv_to_handler);
  }//GraphViewController

  /**
   * Sets the array of <code>giny.view.GraphView</code> objects
   * that this <code>GraphViewController</code> will keep synchronized with
   * their corresponding <code>giny.model.GraphPerspective</code> objects
   * available through their <code>getGraphPerspective()</code> method, the
   * DEFAULT_GRAPH_VIEW_HANDLER is set for all the views. Any previous <code>GraphView</code>
   * objects are removed from this listener
   *
   * @param graph_views an array of <code>giny.view.GraphView</code> objects
   */
  public void setGraphViews (GraphView [] graph_views){
    removeAllGraphViews();
    for(int i = 0; i < graph_views.length; i++){
      GraphPerspective graphPerspective = graph_views[i].getGraphPerspective();
      graphPerspective.addGraphPerspectiveChangeListener(this);
      this.gpToGv.put(graphPerspective, graph_views[i]);
      this.graphViewToHandler.put(graph_views[i], DEFAULT_GRAPH_VIEW_HANDLER);
    }//for i
  }//setGraphViews

   /**
   * Sets the array of <code>giny.view.GraphView</code> objects
   * that this <code>GraphViewController</code> will keep synchronized with
   * their corresponding <code>giny.model.GraphPerspective</code> objects
   * available through their <code>getGraphPerspective()</code> method, the
   * <code>cytoscape.view.GraphViewHandler</code> objects for the 
   * <code>GraphView</code>s are obtained from the given <code>Map</code>,
   * if a <code>GraphView</code> in the array is not a key in the <code>Map</code>
   * then DEFAULT_GRAPH_VIEW_HANDLER is used. Any previous <code>GraphView</code>
   * objects are removed from this listener
   *
   * @param graph_views an array of <code>giny.view.GraphView</code> objects
   * @param gv_to_handler a <code>Map</code> with <code>giny.view.GraphView</code> 
   * for keys and <code>cytoscape.view.GraphViewHandler</code> objects for values
   */
  public void setGraphViews (GraphView [] graph_views, Map gv_to_handler){
    removeAllGraphViews();
    for(int i = 0; i < graph_views.length; i++){
      GraphPerspective graphPerspective = graph_views[i].getGraphPerspective();
      graphPerspective.addGraphPerspectiveChangeListener(this);
      this.gpToGv.put(graphPerspective, graph_views[i]);
      GraphViewHandler handler = (GraphViewHandler)gv_to_handler.get(graph_views[i]);
      if(handler == null){
        this.graphViewToHandler.put(graph_views[i], DEFAULT_GRAPH_VIEW_HANDLER);
      }else{
        this.graphViewToHandler.put(graph_views[i], handler);
      }
    }//for i
  }//setGraphViews

  /**
   * Gets an array of <code>giny.view.GraphView</code> objects
   * that this <code>GraphViewController</code> will keep synchronized with
   * their corresponding <code>giny.model.GraphPerspective</code> objects
   * available through their <code>getGraphPerspective()</code> method
   *
   * @return an array of <code>giny.view.GraphView</code> objects
   */
  public GraphView []  getGraphViews (){
    Set keySet = this.graphViewToHandler.keySet();
    return (GraphView[])keySet.toArray(new GraphView[keySet.size()]);
  }//getGraphViews

  /**
   * Gets a <code>Map</code> with <code>giny.view.GraphView</code>
   * objects as keys, and <code>cytoscape.view.GraphViewHandler</code> objects
   * as values, two or more different keys can share the same value.
   *
   * @return the <code>Map</code> that specifies what <code>cytoscape.view.GraphViewHandler</code>
   * objects handle the <code>giny.view.GraphView</code> objects in this controller.
   */
  public Map getGraphViewHandlersMap (){
    return this.graphViewToHandler;
  }//getGraphViewHandlersMap

  /**
   * Gets the <code>cytoscape.view.GraphViewHandler</code> for the given
   * <code>giny.view.GraphView</code>
   *
   * @return a <code>cytoscape.view.GraphViewHandler</code>, or null if this
   * <code>GraphViewController</code> does not control the given 
   * <code>giny.view.GraphView</code>
   */
  public GraphViewHandler getGraphViewHandler (GraphView graph_view){
    return (GraphViewHandler)this.graphViewToHandler.get(graph_view);
  }//getGraphViewHandler
  
  /**
   * If this <code>GraphViewController</code> contains the given 
   * <code>giny.view.GraphView</code>, then it is removed from it, and it no longer listens 
   * for change events from the removed <code>giny.view.GraphView</code>'s 
   * <code>giny.model.GraphPerspective</code>.
   *
   * @param graph_view the <code>giny.view.GraphView</code> that will be removed
   * @return the removed  <code>giny.view.GraphView</code>'s 
   * <code>cytoscape.view.GraphViewHandler</code>, or null if it is not in this 
   * <code>GraphViewController</code>
   */
  public GraphViewHandler removeGraphView (GraphView graph_view){
    if(this.graphViewToHandler.containsKey(graph_view)){
      GraphPerspective graphPerspective = graph_view.getGraphPerspective();
      graphPerspective.removeGraphPerspectiveChangeListener(this);
      this.gpToGv.remove(graphPerspective);
      GraphViewHandler gvHandler = 
        (GraphViewHandler)this.graphViewToHandler.remove(graph_view);
      return gvHandler;
    }// if containsKey
    return null;
  }//removeGraphView

  /**
   * Adds to the set of <code>giny.view.GraphView</code> objects that this 
   * <code>GraphViewController</code> keeps synchronized with their 
   * <code>giny.model.GraphPerspective</code> objects. 
   * DEFAULT_GRAPH_VIEW_HANDLER is used for the given <code>giny.view.GraphView</code>
   *
   * @param graph_view the <code>giny.view.GraphView</code> to be added
   * @return true if succesfully added, false otherwise (if it was already added)
   * @see GraphViewController.setGraphViewHandler
   */
  public boolean addGraphView (GraphView graph_view){
    if(this.graphViewToHandler.containsKey(graph_view)){
      // already contained in this controller
      return false;
    }
    GraphPerspective graphPerspective = graph_view.getGraphPerspective();
    graphPerspective.addGraphPerspectiveChangeListener(this);
    this.gpToGv.put(graphPerspective, graph_view);
    this.graphViewToHandler.put(graph_view,DEFAULT_GRAPH_VIEW_HANDLER);
    return true;
  }//addGraphView
  
  /**
   * Adds to the set of <code>giny.view.GraphView</code> objects that this 
   * <code>GraphViewController</code> keeps synchronized to their 
   * <code>giny.model.GraphPerspective</code> objects. The given <code>GraphViewHandler</code>
   * is used for the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> to be added
   * @param gv_to_handler the <code>GraphViewHandler</code> that will handle
   * change events from <code>graph_view</code>'s <code>giny.model.GraphPerspective</code> member
   * @return true if succesfully added, false otherwise (if <code>graph_view</code> is
   * already in this controller)
   * @see #setGraphViewHandler(GraphView, GraphViewHandler) setGraphViewHandler
   */
  public boolean addGraphView (GraphView graph_view, GraphViewHandler gv_handler){
    if(this.graphViewToHandler.containsKey(graph_view)){
      // already contained
      return false;
    }
    GraphPerspective graphPerspective = graph_view.getGraphPerspective();
    graphPerspective.addGraphPerspectiveChangeListener(this);
    this.gpToGv.put(graphPerspective, graph_view);
    this.graphViewToHandler.put(graph_view, gv_handler);
    return true;
  }//addGraphView

  /**
   * If the given <code>giny.view.GraphView</code> object belongs to this 
   * <code>GraphViewController</code>, then its <code>GraphViewHandler</code>
   * is set to the given one.
   *
   * @param graph_view the <code>giny.view.GraphView</code> to be updated
   * @param gv_handler the <code>GraphViewHandler</code> that will handle
   * change events from <code>graph_view</code>'s <code>giny.model.GraphPerspective</code>
   * @return true if the method was successful, false otherwise (if <code>graph_view</code> 
   * is not in this controller)
   */
  public boolean setGraphViewHandler (GraphView graph_view, GraphViewHandler gv_handler){
    if(this.graphViewToHandler.containsKey(graph_view)){
      this.graphViewToHandler.put(graph_view, gv_handler);
      return true;
    }
    return false;
  }//setGraphViewHandler

  /**
   * Removes all of the current <code>giny.view.GraphView</code> objects that this
   * <code>GraphViewController</code> keeps synchronized to their corresponding
   * <code>giny.model.GraphPerspective</code> members. This <code>GraphViewController</code>
   * will no longer receive events from <code>giny.model.GraphPerspective</code>s after
   * this call.
   * 
   * @return the array of removed <code>giny.view.GraphView</code> objects
   */
  public GraphView [] removeAllGraphViews (){
    GraphView [] gViews = getGraphViews();
    for(int i = 0; i < gViews.length; i++){
      GraphPerspective graphPerspective = gViews[i].getGraphPerspective();
      graphPerspective.removeGraphPerspectiveChangeListener(this);
    }//for i
    this.gpToGv.clear();
    this.graphViewToHandler.clear();
    return gViews;
  }//removeAllGraphViews
  
  /**
   * Whether or not the given <code>giny.view.GraphView</code> is kept synchronized
   * with its <code>giny.model.GraphPerspective</code> member by this
   * <code>GraphViewController</code>.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object to test
   */ 
  public boolean containsGraphView (GraphView graph_view){
    return this.graphViewToHandler.containsKey(graph_view);
  }//containsGraphView

  /**
   * It temporarily removes this <code>GraphViewController</code> as a listener for
   * all <code>giny.model.GraphPerspective</code> objects that it currently
   * listens to
   *
   * @see #resumeListening() resumeListening
   */
  public void stopListening (){
    GraphView [] graphViews = getGraphViews();
    for(int i = 0; i < graphViews.length; i++){
      GraphPerspective graphPerspective = graphViews[i].getGraphPerspective();
      graphPerspective.removeGraphPerspectiveChangeListener(this);
    }//for i
  }//stopListening
  
  /**
   * It temporarily removes this <code>GraphViewController</code> listener
   * from the <code>giny.model.GraphPerspective</code> object that the given
   * <code>giny.view.GraphView</code> views.
   *
   * @see #resumeListening(GraphView)
   */
  // TODO: Catch all change events even of stopListening has been called, and when
  // listening is resumed, update the graph view
  public void stopListening (GraphView graph_view){
    GraphPerspective graphPerspective = graph_view.getGraphPerspective();
    graphPerspective.removeGraphPerspectiveChangeListener(this);
  }//stopListening
  
  /**
   * It adds this <code>GraphViewController</code> as a listener for
   * all <code>giny.model.GraphPerspective</code> that were temporarily
   * "removed" by calling <code>stopListening()</code>, it updates the <code>GraphViews</code>
   * of the <code>GraphPerspectives</code> so that they are synchronized to reflect changes that
   * may have occured while not listening.
   *
   * @see #stopListening() stopListening
   */
  public void resumeListening (){
    GraphView [] graphViews = getGraphViews();
    for(int i = 0; i < graphViews.length; i++){
      GraphPerspective graphPerspective = graphViews[i].getGraphPerspective();
      GraphViewHandler handler = (GraphViewHandler)this.graphViewToHandler.get(graphViews[i]);
      handler.updateGraphView(graphViews[i]);
      graphPerspective.addGraphPerspectiveChangeListener(this);
    }//for i
  }//resumeListening

  /**
   * It adds this <code>GraphViewController</code> listener to the
   * <code>giny.model.GraphPerspective</code> of the given <code>giny.view.GraphView</code>
   * that was temporarily "removed" by a call to <code>stopListening(GraphView)</code>, it updates
   * <code>graph_view</code> so that it's synchronized to its <code>GraphPerspective</code> 
   * due to changes that may have occured while not listening.
   *
   * @see #stopListening(GraphView)
   */
  public void resumeListening (GraphView graph_view){
    GraphViewHandler handler = (GraphViewHandler)this.graphViewToHandler.get(graph_view);
    handler.updateGraphView(graph_view);
    GraphPerspective graphPerspective = graph_view.getGraphPerspective();
    graphPerspective.addGraphPerspectiveChangeListener(this);
  }//resumeListening
  
  /**
   * Invoked when a graph change to any of the <code>giny.model.GraphPerspective</code>
   * objects accessed through <code>giny.view.GraphView.getGraphPerspective()</code> of 
   * this object's graphViews is made.
   *
   * @param event the event that was generated, contains the source 
   * <code>giny.model.GraphPerspective</code>
   */
  public void graphPerspectiveChanged (GraphPerspectiveChangeEvent event){
    //TODO: Remove
    //System.out.println("In GraphViewController.graphPerspectiveChanged()");
    Object source = event.getSource();
    if(! (source instanceof GraphPerspective) ){
      // TODO: What to do? There was a change in the RootGraph, do we propagate it
      // to all the GraphPerspectives????
      return;
    }
    GraphPerspective changedGraphPers = (GraphPerspective)source;
    GraphView graphView = (GraphView)this.gpToGv.get(changedGraphPers);
    if(graphView == null){
      // Somehow, we are listening to events of a GraphPerspective that
      // no GraphView in our data structures views
      System.err.println("Oops! the GraphPerspective " + changedGraphPers +
                         " does not have a corresponding GraphView in the GraphViewController!!!");
      return;
    }// if graphView == null
    GraphViewHandler gvHandler = (GraphViewHandler)this.graphViewToHandler.get(graphView);
    if(gvHandler == null){
      // Somehow, we have a graphView with no handler!
      System.err.println("Oops! the GraphView " + graphView +
                     " has no GraphViewHandler in the GraphViewController!!!");
      return;
    }//if gvHandler == null
    gvHandler.handleGraphPerspectiveEvent(event, graphView);
//     if ( graphView instanceof PhoebeNetworkView )
//       ( ( PhoebeNetworkView )graphView).updateStatusLabel();
  }//graphPerspectiveChanged

}//class GraphViewController

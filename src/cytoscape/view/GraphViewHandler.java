
/*
  File: GraphViewHandler.java 
  
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

import giny.model.GraphPerspectiveChangeEvent;
import giny.view.GraphView;

/**
 * This interface represents an object that handles a change in a 
 * <code>giny.mode.GraphPerspective</code> by updating one of its 
 * <code>giny.view.GraphView</code>s.
 */

public interface GraphViewHandler {

  /**
   * Handles the event as desired by updating the given <code>giny.view.GraphView</code>.
   *
   * @param event the event to handle
   * @param graph_view the <code>giny.view.GraphView</code> that views the 
   * <code>giny.model.GraphPerspective</code> that generated the event and that should
   * be updated as necessary
   */
  public void handleGraphPerspectiveEvent (GraphPerspectiveChangeEvent event, GraphView graph_view);

  /**
   * Updates graph_view so that it is synchronized with its <code>giny.model.GraphPerspective</code>
   * Useful if <code>GraphPerspectiveChangeEvents</code> haven't been handled, 
   * and a <code>graph_view</code> must be made synchronized with its GraphPerspective.
   *
   * @param graph_view the <code>giny.view.GraphView</code> that views the should
   * be updated as necessary
   */
  public void updateGraphView (GraphView graph_view);
  
}//GraphViewHandler

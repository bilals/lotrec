
/*
  File: CytoscapeFingRootGraph.java 
  
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

import giny.model.*;
import cytoscape.*;
import fing.model.*;

import cytoscape.util.intr.*;
import java.util.Collection;

import giny.model.Node;
import giny.model.Edge;

import com.sosnoski.util.hashmap.StringIntHashMap;

public class CytoscapeFingRootGraph  
  extends FingExtensibleRootGraph 
  implements  CytoscapeRootGraph {


  StringIntHashMap node_name_index_map;
  StringIntHashMap edge_name_index_map;
  

  public CytoscapeFingRootGraph () {
    super( new CyNodeDepot(),
           new CyEdgeDepot() );

    node_name_index_map = new StringIntHashMap();
    edge_name_index_map = new StringIntHashMap();

  }

  public CyNetwork createNetwork ( Collection nodes, Collection edges ) {
    Node[] node = ( Node[] )  nodes.toArray( new Node[] {} );
    Edge[] edge = ( Edge[] )  edges.toArray( new Edge[] {} );
    return  createNetwork( node, edge ) ;

  }
  /**
   * Creates a new Network
   */
  public CyNetwork createNetwork ( Node[] nodes, Edge[] edges ) {

    final Node[] nodeArr = ((nodes != null) ? nodes : new Node[0]);
    final Edge[] edgeArr = ((edges != null) ? edges : new Edge[0]);
    final RootGraph root = this;
    try {
      return new FingCyNetwork
        (this,
         new IntIterator() {
           private int index = 0;
           public boolean hasNext() { return index < nodeArr.length; }
           public int nextInt() {
             if (nodeArr[index] == null ||
                 nodeArr[index].getRootGraph() != root)
               throw new IllegalArgumentException();
             return nodeArr[index++].getRootGraphIndex(); } },
         new IntIterator() {
           private int index = 0;
           public boolean hasNext() { return index < edgeArr.length; }
           public int nextInt() {
             if (edgeArr[index] == null ||
                 edgeArr[index].getRootGraph() != root)
               throw new IllegalArgumentException();
             return edgeArr[index++].getRootGraphIndex(); } }); }
    catch (IllegalArgumentException exc) { return null; } 
  }

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public CyNetwork createNetwork ( int[] nodeInx, int[] edgeInx ) {
    if (nodeInx == null) nodeInx = new int[0];
    if (edgeInx == null) edgeInx = new int[0];
    try { return new FingCyNetwork
            (this, new ArrayIntIterator(nodeInx, 0, nodeInx.length),
             new ArrayIntIterator(edgeInx, 0, edgeInx.length)); }
    catch (IllegalArgumentException exc) { return null; } 
  }

  public cytoscape.CyNode getNode ( String identifier ) {
  	if ( node_name_index_map.containsKey( identifier ) )
    		return ( cytoscape.CyNode )getNode( node_name_index_map.get( identifier ) );
	else	
		return null;
  }

  public cytoscape.CyEdge getEdge ( String identifier ) {
  	if ( edge_name_index_map.containsKey( identifier ) )
    		return ( cytoscape.CyEdge )getEdge( edge_name_index_map.get( identifier ) );
	else
		return null;
  }

  public void setNodeIdentifier ( String identifier, int index ) {
    if (index == 0) {
      node_name_index_map.remove(identifier); }
    else {
      node_name_index_map.add(identifier, index); } }

  public void setEdgeIdentifier ( String identifier, int index ) {
    if (index == 0) {
      edge_name_index_map.remove(identifier); }
    else {
      edge_name_index_map.add(identifier, index); } }

}

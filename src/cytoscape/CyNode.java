
/*
  File: CyNode.java 
  
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

package cytoscape;

import giny.model.*;
import cytoscape.giny.CytoscapeFingRootGraph;

  // Package visible class.
public class CyNode implements giny.model.Node
{

  // Variables specific to public get/set methods.
  CytoscapeFingRootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  String m_identifier = null;

  public CyNode(RootGraph root,
         int rootGraphIndex) { 
    this.m_rootGraph = ( CytoscapeFingRootGraph )root;
    this.m_rootGraphIndex = rootGraphIndex;
    this.m_identifier = new Integer(m_rootGraphIndex).toString();
  }


  public GraphPerspective getGraphPerspective()
  {
    return m_rootGraph.createGraphPerspective
      (m_rootGraph.getNodeMetaChildIndicesArray(m_rootGraphIndex),
       m_rootGraph.getEdgeMetaChildIndicesArray(m_rootGraphIndex));
  }

  public boolean setGraphPerspective(GraphPerspective gp)
  {
    if (gp.getRootGraph() != m_rootGraph) return false;
    final int[] nodeInx = gp.getNodeIndicesArray();
    final int[] edgeInx = gp.getEdgeIndicesArray();
    for (int i = 0; i < nodeInx.length; i++)
      m_rootGraph.addNodeMetaChild(m_rootGraphIndex, nodeInx[i]);
    for (int i = 0; i < edgeInx.length; i++)
      m_rootGraph.addEdgeMetaChild(m_rootGraphIndex, edgeInx[i]);
    return true;
  }

  public RootGraph getRootGraph()
  {
    return m_rootGraph;
  }

  public int getRootGraphIndex()
  {
    return m_rootGraphIndex;
  }

  public String getIdentifier()
  {
    return m_identifier;
  }

  public boolean setIdentifier(String new_id)
  {
    if (new_id == null) {
      m_rootGraph.setNodeIdentifier(m_identifier, 0); }
    else {
      m_rootGraph.setNodeIdentifier(new_id, m_rootGraphIndex); }
    m_identifier = new_id;
    return true;
  }

} 

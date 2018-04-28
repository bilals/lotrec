
/*
  File: CyNetworkUtilities.java 
  
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


// $Revision: 8711 $
// $Date: 2006-11-07 17:19:44 -0800 (Tue, 07 Nov 2006) $
// $Author: mes $
package cytoscape.data;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;

import ViolinStrings.Strings;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNode;

//-------------------------------------------------------------------------
/**
 * This class provides static methods that operate on a CyNetwork to perform
 * various useful tasks. Many of these methods make assumptions about the data
 * types that are available in the node and edge attributes of the network.
 */
public class CyNetworkUtilities {
	// -------------------------------------------------------------------------
	/**
	 * Saves all selected nodes in the current view to a file with the given
	 * name.
	 * TODO: The CyNetworkView is not a needed parameter
	 */
	public static boolean saveSelectedNodeNames(CyNetworkView networkView,
			CyNetwork network, String filename) {
		if (networkView == null || network == null || filename == null) {
			return false;
		}

		Set selectedNodes = network.getSelectedNodes();
		if(selectedNodes == null || selectedNodes.size() == 0){
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"No selected nodes.","Message",JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		String lineSep = System.getProperty("line.separator");
		try {
			File file = new File(filename);
			FileWriter fout = new FileWriter(file);
			
			for (Iterator i = selectedNodes.iterator(); i.hasNext();) {
				CyNode node = (CyNode)i.next();
				String nodeUID = node.getIdentifier();
				fout.write(nodeUID + lineSep);
			} // for i
			fout.close();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.toString(),
					"Error Writing to \"" + filename + "\"",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

	} // saveSelectedNodeNames

	// -------------------------------------------------------------------------

	/**
	 * Saves all nodes in the given network to a file with the given
	 * name.
	 */
	public static boolean saveVisibleNodeNames(CyNetwork network,
			String filename) {
		if (network == null || filename == null) {
			return false;
		}

		String callerID = "CyNetworkUtilities.saveVisibleNodeNames";

		//GraphPerspective theGraph = network.getGraphPerspective();
		//CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		String lineSep = System.getProperty("line.separator");
		try {
			File file = new File(filename);
			FileWriter fout = new FileWriter(file);
			for (Iterator i = network.nodesIterator(); i.hasNext();) {
				CyNode node = (CyNode) i.next();
				// String canonicalName = nodeAttributes.getCanonicalName(node);
				//String canonicalName = nodeAttributes.getStringAttribute(node
				//		.getIdentifier(), "canonicalName");

				fout.write(node.getIdentifier() + lineSep);
			} // for i
			fout.close();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.toString(),
					"Error Writing to \"" + filename + "\"",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	// -------------------------------------------------------------------------
	/**
	 * Selects every node in the current view whose canonical name, label, or
	 * any known synonym starts with the string specified by the second
	 * argument. Note that synonyms are only available if a naming server is
	 * available.
	 * 
	 * This method does not change the selection state of any node that doesn't
	 * match the given key, allowing multiple selection queries to be
	 * concatenated.
	 */
	public static boolean selectNodesStartingWith(CyNetwork network,
			String key, CyNetworkView networkView) {
		if (network == null || key == null || networkView == null) {
			return false;
		}
		key = key.toLowerCase();
		boolean found = false;
		String callerID = "CyNetworkUtilities.selectNodesStartingWith";

		//GraphPerspective theGraph = network.getGraphPerspective();
		//CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		int nodeFound = 0;
		Vector matchedNodes = new Vector();
		for (Iterator i = network.nodesIterator(); i.hasNext();) {
			CyNode node = (CyNode) i.next();
			String nodeUID = node.getIdentifier();
			
			boolean matched = false;
			if (nodeUID != null && Strings.isLike(nodeUID, key, 0, true)) {
				matched = true;
				found = true;
				matchedNodes.add(node);
			} else {
				// this list always includes the canonical name itself
				List synonyms = Semantics.getAllSynonyms(nodeUID, network);
				for (Iterator synI = synonyms.iterator(); synI.hasNext();) {
					String synonym = (String) synI.next();
					if (Strings.isLike(synonym, key, 0, true)) {
						matched = true;
						found = true;
						matchedNodes.add(node);
						break;
					}
				}//inner for
			}//else
			if (matched) nodeFound++;
		}//for

		if (nodeFound == 0) {
			JOptionPane.showMessageDialog(null, "No match for the string \""
					+ key + "\"", "Error: Node Not Found", JOptionPane.ERROR_MESSAGE);
		}
		
		if(nodeFound > 0){
			network.setSelectedNodeState(matchedNodes, true);
		}

		//System.out.println("node found = " + nodeFound);
		return found;
	}
	// -------------------------------------------------------------------------
}

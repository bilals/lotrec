
/*
  File: InteractionWriter.java 
  
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

package cytoscape.data.writers;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;
import cytoscape.task.TaskMonitor;

import java.util.Iterator;
import java.util.List;
import java.io.Writer;
import java.io.IOException;

/**
 * A few utility methods to assist in writing SIF files.
 */
public class InteractionWriter {

    /**
     * Writes a SIF formatted string of the specified network to the specified writer.
     * @param network The network to be written.
     * @param writer The writer the network should be written to.
     * @param taskMonitor An optional task monitor.
     */
    public static void writeInteractions(CyNetwork network, Writer writer, TaskMonitor taskMonitor) throws IOException {
    	String sif = getInteractionString(network,taskMonitor);
	writer.write(sif);
    }
    
    /**
     * Writes a SIF formatted string of the specified network to the specified writer.
     * @param network The network to be written.
     * @param writer The writer the network should be written to.
     */
    public static void writeInteractions(CyNetwork network, Writer writer) throws IOException {
    	writeInteractions(network,writer,null);
    }

    /**
     * Returns a SIF formatted string of the specified network.
     * @param network The network to be formatted as a SIF string.
     * @return A string of a CyNetwork in SIF format.
     */
    public static String getInteractionString(CyNetwork network) { 
    	return getInteractionString(network,null);
    }

    /**
     * Returns a SIF formatted string of the specified network.
     * @param network The network to be formatted as a SIF string.
     * @param taskMonitor An optional taskMonitor in case you want to 
     * use one. Use null otherwise.
     * @return A string of a CyNetwork in SIF format.
     */
    public static String getInteractionString(CyNetwork network, TaskMonitor taskMonitor) { 

    	if ( network == null ) {
		return "";
	}

    	StringBuffer sb = new StringBuffer();	

        String lineSep = System.getProperty("line.separator");
        List nodeList = network.nodesList();

        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
        giny.model.Node[] nodes = 
        	(giny.model.Node[]) nodeList.toArray(new giny.model.Node[0]);
        
        for (int i = 0; i < nodes.length; i++) {

	    if ( taskMonitor != null ) {
		//  Report on Progress
		double percent = ((double) i / nodes.length) * 100.0;
		taskMonitor.setPercentCompleted((int) percent);
	    }

            giny.model.Node node = nodes[i];
            String canonicalName = node.getIdentifier();
            List edges = network.getAdjacentEdgesList(node, true, true, true);

            if (edges.size() == 0) {
                sb.append(canonicalName + lineSep);
            } else {
                Iterator it = edges.iterator();
                while (it.hasNext()) {
                    giny.model.Edge edge = (giny.model.Edge) it.next();
                    if (node == edge.getSource()) { //do only for outgoing edges
                        giny.model.Node target = edge.getTarget();
                        
                        String canonicalTargetName = target.getIdentifier();
                        
                        String edgeName = edge.getIdentifier();
                        
                        String interactionName =
                                edgeAtts.getStringAttribute(edge.getIdentifier(),Semantics.INTERACTION);
                        
                        if (interactionName == null) {
                            interactionName = "xx";
                        }
                        sb.append(canonicalName);
                        sb.append("\t");
                        sb.append(interactionName);
                        sb.append("\t");
                        sb.append(canonicalTargetName);
                        sb.append(lineSep);
                    }
                } // while
            } // else: this node has edges, write out one line for every
        }  // for i

	return sb.toString();
    }
}

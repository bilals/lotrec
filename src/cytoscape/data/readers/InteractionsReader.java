/*
 File: InteractionsReader.java 
 
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

// InteractionsReader:  from semi-structured text file, into an array of Interactions
//------------------------------
// $Revision: 8471 $
// $Date: 2006-10-16 15:20:07 -0700 (Mon, 16 Oct 2006) $
// $Author: mes $
//------------------------------
package cytoscape.data.readers;

//------------------------------
import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;
import giny.view.GraphView;
import giny.view.NodeView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.Interaction;
import cytoscape.data.servers.BioDataServer;
import cytoscape.task.TaskMonitor;
import cytoscape.util.FileUtil;
import cytoscape.util.PercentUtil;

/**
 * Reader for graphs in the interactions file format. Given the filename,
 * provides the graph and attributes objects constructed from the file.
 */
public class InteractionsReader extends AbstractGraphReader {
	private TaskMonitor taskMonitor;
	private PercentUtil percentUtil;

	/**
	 * A Vector that holds all of the Interactions
	 */
	protected Vector allInteractions = new Vector();
	BioDataServer dataServer;
	String species;
	String zip_entry;
	boolean is_zip = false;

	IntArrayList node_indices;
	OpenIntIntHashMap edges;

	/**
	 * @deprecated Will be removed 12/2006. BioDataServer and species are no longer needed or used.
	 */
	public InteractionsReader(BioDataServer dataServer, String species, String filename) {
		this(filename,null);
	}

	/**
	 * @deprecated Will be removed 12/2006. BioDataServer and species are no longer needed or used.
	 */
	public InteractionsReader(BioDataServer dataServer, String species, String filename, TaskMonitor taskMonitor) {
		this(filename,taskMonitor);
	}

	/**
	 * @deprecated Will be removed 12/2006. BioDataServer and species are no longer needed or used.
	 */
	public InteractionsReader(BioDataServer dataServer, String species, String zip_entry, boolean is_zip) {
		this(zip_entry,null,is_zip);
	}

		
	/**
	 * Creates an interaction reader based on a string consisting of data that has
	 * been read from a zip file.
	 * @param zip_entry The zip entry data.
	 * @param monitor An optional task monitor.  May be null.
	 * @param is_zip Indicates that the data is from a zip file - Should almost 
	 * always be true.
	 */
	public InteractionsReader(String zip_entry, TaskMonitor monitor, boolean is_zip) {
		super("zip_data");
		this.zip_entry = zip_entry;
		this.is_zip = is_zip;
		this.taskMonitor = monitor;
	}

	/**
	 * Creates an interaction reader based on the string file name. 
	 * @param filename The filename that contains the interaction data 
	 * to be read.
	 */
	public InteractionsReader(String filename) {
		this(filename,null);
	}

	/**
	 * Creates an interaction reader based on the string file name. 
	 * @param filename The filename that contains the interaction data 
	 * to be read.
	 * @param monitor An optional task monitor.  May be null.
	 */
	public InteractionsReader(String filename, TaskMonitor monitor) {
		super(filename);
		this.taskMonitor = monitor;
	}

	public void layout(GraphView view) {
		double distanceBetweenNodes = 50.0d;
		int columns = (int) Math.sqrt(view.nodeCount());
		Iterator nodeViews = view.getNodeViewsIterator();
		double currX = 0.0d;
		double currY = 0.0d;
		int count = 0;
		while (nodeViews.hasNext()) {
			NodeView nView = (NodeView) nodeViews.next();
			nView.setOffset(currX, currY);
			count++;
			if (count == columns) {
				count = 0;
				currX = 0.0d;
				currY += distanceBetweenNodes;
			} else {
				currX += distanceBetweenNodes;
			}
		}
	}

	// ----------------------------------------------------------------------------------------
	public void read(boolean canonicalize) throws IOException {
		String rawText;
		if (!is_zip) {
			rawText = FileUtil.getInputString(fileName);
		} else {
			rawText = zip_entry;
		}

		String delimiter = " ";
		if (rawText.indexOf("\t") >= 0)
			delimiter = "\t";

		String[] lines = rawText.split(System.getProperty("line.separator"));

		// There are a total of 6 steps to read in a complete SIF File
		if (taskMonitor != null) {
			percentUtil = new PercentUtil(6);
		}

		for (int i = 0; i < lines.length; i++) {

			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(1,
						i, lines.length));
			}
			String newLine = lines[i];
			Interaction newInteraction = new Interaction(newLine, delimiter);
			allInteractions.addElement(newInteraction);
		}
		createRootGraphFromInteractionData(canonicalize);

	}

	// -----------------------------------------------------------------------------------------
	/**
	 * Calls read(false)
	 */
	public void read() throws IOException {
		read(false);
	} // readFromFile

	// -------------------------------------------------------------------------------------------
	public int getCount() {
		return allInteractions.size();
	}

	// -------------------------------------------------------------------------------------------
	public Interaction[] getAllInteractions() {
		Interaction[] result = new Interaction[allInteractions.size()];

		for (int i = 0; i < allInteractions.size(); i++) {
			Interaction inter = (Interaction) allInteractions.elementAt(i);
			result[i] = inter;
		}

		return result;
	}

	/*
	 * KONO: 5/4/2006 "Canonical Name" is no longer used in Cytoscape. Use ID
	 * instead.
	 */
	protected String canonicalizeName(String name) {

		String canonicalName = name;
		if (dataServer != null) {
			canonicalName = dataServer.getCanonicalName(species, name);
			if (canonicalName == null) {
				canonicalName = name;
			}
		}
		return canonicalName;
	} // canonicalizeName

	// -------------------------------------------------------------------------------------------
	protected void createRootGraphFromInteractionData(boolean canonicalize) {
		Interaction[] interactions = getAllInteractions();
		// figure out how many nodes and edges we need before we create the
		// graph;
		// this improves performance for large graphs
		Set nodeNameSet = new HashSet();
		int edgeCount = 0;

		for (int i = 0; i < interactions.length; i++) {
			Interaction interaction = interactions[i];
			String sourceName = interaction.getSource();
			if (canonicalize)
				sourceName = canonicalizeName(sourceName);
			nodeNameSet.add(sourceName); // does nothing if already there

			String[] targets = interaction.getTargets();
			for (int t = 0; t < targets.length; t++) {
				String targetNodeName = targets[t];
				if (canonicalize)
					targetNodeName = canonicalizeName(targetNodeName);
				nodeNameSet.add(targetNodeName); // does nothing if already
				// there
				edgeCount++;
			}
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(2,
						i, interactions.length));
			}
		}

		Cytoscape.ensureCapacity(nodeNameSet.size(), edgeCount);
		node_indices = new IntArrayList(nodeNameSet.size());
		edges = new OpenIntIntHashMap(edgeCount);

		// now create all of the nodes, storing a hash from name to node
		// Map nodes = new HashMap();
		int counter = 0;
		for (Iterator si = nodeNameSet.iterator(); si.hasNext();) {
			String nodeName = (String) si.next();

			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(3,
						counter, nodeNameSet.size()));
				counter++;
			}
			// use the static method
			Node node = (Node) Cytoscape.getCyNode(nodeName, true);
			node_indices.add(node.getRootGraphIndex());
		}

		// ---------------------------------------------------------------------------
		// now loop over the interactions again, this time creating edges
		// between
		// all sources and each of their respective targets.
		// for each edge, save the source-target pair, and their interaction
		// type,
		// in Cytoscape.getEdgeNetworkData() -- a hash of a hash of name-value
		// pairs, like this:
		// Cytoscape.getEdgeNetworkData() ["interaction"] = interactionHash
		// interactionHash [sourceNode::targetNode] = "pd"
		// ---------------------------------------------------------------------------
		String targetNodeName;
		for (int i = 0; i < interactions.length; i++) {

			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(4,
						i, interactions.length));
			}

			Interaction interaction = interactions[i];
			String nodeName = interaction.getSource();
			if (canonicalize)
				nodeName = canonicalizeName(interaction.getSource());

			String interactionType = interaction.getType();

			String[] targets = interaction.getTargets();
			for (int t = 0; t < targets.length; t++) {

				if (canonicalize)
					targetNodeName = canonicalizeName(targets[t]);
				else
					targetNodeName = targets[t];

				String edgeName = CyEdge.createIdentifier(nodeName,interactionType,
				                                          targetNodeName);
				Edge edge = (Edge) Cytoscape.getCyEdge(nodeName, edgeName,
						targetNodeName, interactionType);
				edges.put(edge.getRootGraphIndex(), 0);

			} // for t
		} // for i

	} // createRootGraphFromInteractionData

	public int[] getNodeIndicesArray() {
		node_indices.trimToSize();
		return node_indices.elements();
	}

	public int[] getEdgeIndicesArray() {
		// edges.trimToSize();
		// return edges.elements();
		IntArrayList edge_indices = new IntArrayList(edges.size());
		edges.keys(edge_indices);
		edge_indices.trimToSize();
		return edge_indices.elements();

	}

} // InteractionsReader


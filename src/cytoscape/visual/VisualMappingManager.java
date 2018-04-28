/*
 File: VisualMappingManager.java 
 
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

package cytoscape.visual;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.Label;
import giny.view.NodeView;

import ding.view.DGraphView;
import ding.view.DingCanvas;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CytoscapeInit;
import cytoscape.view.CyNetworkView;

/**
 * Top-level class for controlling the visual appearance of nodes and edges
 * according to data attributes, as well as some global visual attributes. This
 * class holds a reference to a NetworkView that displays the network, a
 * CalculatorCatalog that holds the set of known visual styles and calculators,
 * and a current VisualStyle that is used to determine the values of the visual
 * attributes. A Logger is also supplied to report errors.
 * <P>
 * 
 * Note that a null VisualStyle is not allowed; this class always provides at
 * least a default object.
 * <P>
 * 
 * The key methods are the apply* methods. These methods first recalculate the
 * visual appearances by delegating to the calculators contained in the current
 * visual style. The usual return value of these methods is an Appearance object
 * that contains the visual attribute values; these values are then applied to
 * the network by calling the appropriate set methods in the graph view API.
 * <P>
 */
public class VisualMappingManager extends SubjectBase {

	CyNetworkView networkView; // the object displaying the network
	CalculatorCatalog catalog; // catalog of visual styles and calculators
	VisualStyle visualStyle; // the currently active visual style
	Logger logger; // for reporting errors

	// reusable appearance objects
	NodeAppearance myNodeApp = new NodeAppearance();
	EdgeAppearance myEdgeApp = new EdgeAppearance();
	GlobalAppearance myGlobalApp = new GlobalAppearance();

	public VisualMappingManager(CyNetworkView networkView) {
		this.networkView = networkView;
		this.logger = logger;

		loadCalculatorCatalog();

		String defStyle = CytoscapeInit.getProperties().getProperty("defaultVisualStyle");
		if ( defStyle == null )
			defStyle = "default";

		VisualStyle vs = catalog.getVisualStyle(defStyle);
		if(vs == null)
			vs = catalog.getVisualStyle("default");
		setVisualStyle(vs);

	}

	/**
	 * Attempts to load a CalculatorCatalog object, using the information from
	 * the CytoscapeConfig object.
	 * 
	 * Does nothing if a catalog has already been loaded.
	 * 
	 * @see CalculatorCatalog
	 * @see CalculatorCatalogFactory
	 */
	public void loadCalculatorCatalog() {
		loadCalculatorCatalog(null);
	}

	public void loadCalculatorCatalog(String vizmapLocation) {
		if (catalog == null) {

			catalog = CalculatorCatalogFactory.loadCalculatorCatalog();

		} else if (vizmapLocation != null) {
			catalog = CalculatorCatalogFactory.loadCalculatorCatalog();
		}
	}

	public void setNetworkView(CyNetworkView new_view) {

		this.networkView = new_view;
	}

	public CyNetworkView getNetworkView() {
		return networkView;
	}

	public CyNetwork getNetwork() {
		return networkView.getNetwork();
	}

	public CalculatorCatalog getCalculatorCatalog() {
		return catalog;
	}

	public VisualStyle getVisualStyle() {
		return visualStyle;
	}

	/**
	 * Sets a new visual style, and returns the old style. Also fires an event
	 * to attached listeners.
	 * 
	 * If the argument is null, no change is made, an error message is passed to
	 * the logger, and null is returned.
	 */
	public VisualStyle setVisualStyle(VisualStyle vs) {
		if (vs != null) {
			VisualStyle tmp = visualStyle;
			visualStyle = vs;
			this.fireStateChanged();
			return tmp;
		} else {
			// String s = "VisualMappingManager: Attempt to set null
			// VisualStyle";
			// logger.severe(s);
			// return null;
			// Thread.dumpStack();
			return visualStyle;
		}
	}

	/**
	 * Sets a new visual style. Attempts to get the style with the given name
	 * from the catalog and pass that to setVisualStyle(VisualStyle). The return
	 * value is the old style.
	 * 
	 * If no visual style with the given name is found, no change is made, an
	 * error message is passed to the logger, and null is returned.
	 */
	public VisualStyle setVisualStyle(String name) {
		VisualStyle vs = catalog.getVisualStyle(name);
		if (vs != null) {
			return setVisualStyle(vs);
		} else {
			// String s = "VisualMappingManager: unknown VisualStyle: " + name;
			// logger.severe(s);
			// return null;
			return visualStyle;
		}
	}

	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances() {
		applyNodeAppearances(getNetwork(), getNetworkView());
	}

	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances(CyNetwork network, CyNetworkView network_view) {

		NodeAppearanceCalculator nodeAppearanceCalculator = visualStyle
				.getNodeAppearanceCalculator();
		for (Iterator i = network_view.getNodeViewsIterator(); i.hasNext();) {

			NodeView nodeView = (NodeView) i.next();
			Node node = nodeView.getNode();

			nodeAppearanceCalculator.calculateNodeAppearance(myNodeApp, node, network);
			myNodeApp.applyAppearance(nodeView);
		}
	}

	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances() {
		applyEdgeAppearances(getNetwork(), getNetworkView());
	}

	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances(CyNetwork network,
			CyNetworkView network_view) {

		EdgeAppearanceCalculator edgeAppearanceCalculator = visualStyle
				.getEdgeAppearanceCalculator();
		for (Iterator i = network_view.getEdgeViewsIterator(); i.hasNext();) {
			EdgeView edgeView = (EdgeView) i.next();
			if (edgeView == null) {
				// WARNING: This is a hack, edgeView should not be null, but
				// for now do this! (iliana)
				continue;
			}

			Edge edge = edgeView.getEdge();
			edgeAppearanceCalculator.calculateEdgeAppearance(myEdgeApp, edge, network);
			myEdgeApp.applyAppearance(edgeView);

		}
	}

	/**
	 * Recalculates and reapplies the global visual attributes. The
	 * recalculation is done by delegating to the GlobalAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyGlobalAppearances() {
		applyGlobalAppearances(getNetwork(), getNetworkView());
	}

	/**
	 * Recalculates and reapplies the global visual attributes. The
	 * recalculation is done by delegating to the GlobalAppearanceCalculator
	 * member of the current visual style.
	 * 
	 * @param network
	 *            the network to apply to
	 * @param network_view
	 *            the view to apply to
	 */
	public void applyGlobalAppearances(CyNetwork network,
			CyNetworkView network_view) {

		GlobalAppearanceCalculator globalAppearanceCalculator = visualStyle
				.getGlobalAppearanceCalculator();
		globalAppearanceCalculator.calculateGlobalAppearance(myGlobalApp,
				network);

		// setup proper background colors
		if (network_view instanceof DGraphView) {
			DingCanvas backgroundCanvas =
				((DGraphView)network_view).getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
			backgroundCanvas.setBackground(myGlobalApp.getBackgroundColor());
		}
		else {
			System.out.println("VisualMappingManager.applyGlobalAppearances() - DGraphView not found!");
			network_view.setBackgroundPaint(myGlobalApp.getBackgroundColor());
		}
		// will ignore sloppy & reverse selection color for now
		
		// Set selection colors
		Iterator nodeIt = network.nodesIterator();
		while(nodeIt.hasNext()) {
			network_view.getNodeView((CyNode)nodeIt.next()).setSelectedPaint(myGlobalApp.getNodeSelectionColor());
		}
		
		Iterator edgeIt = network.edgesIterator();
		while(edgeIt.hasNext()) {
			network_view.getEdgeView((CyEdge)edgeIt.next()).setSelectedPaint(myGlobalApp.getEdgeSelectionColor());
		}
	}

	/**
	 * Recalculates and reapplies all of the node, edge, and global visual
	 * attributes. This method delegates to, in order, applyNodeAppearances,
	 * applyEdgeAppearances, and applyGlobalAppearances.
	 */
	public void applyAppearances() {
		Date start = new Date();
		/** first apply the node appearance to all nodes */
		applyNodeAppearances();
		/** then apply the edge appearance to all edges */
		applyEdgeAppearances();
		/** now apply global appearances */
		applyGlobalAppearances();
		/** we rely on the caller to redraw the graph as needed */
		Date stop = new Date();
		// System.out.println("Time to apply node styles: " + (stop.getTime() -
		// start.getTime()));
	}

	// ------------------------------//
	// Single Node/Edge Mapping Methods
	// ------------------------------//

	public void vizmapNode(NodeView nodeView, CyNetworkView network_view) {
		CyNode node = (CyNode) nodeView.getNode();
		NodeAppearanceCalculator nodeAppearanceCalculator = visualStyle
				.getNodeAppearanceCalculator();
		nodeAppearanceCalculator.calculateNodeAppearance(myNodeApp, node,
				network_view.getNetwork());
		myNodeApp.applyAppearance(nodeView);
	}

	public void vizmapEdge(EdgeView edgeView, CyNetworkView network_view) {
		CyEdge edge = (CyEdge) edgeView.getEdge();
		EdgeAppearanceCalculator edgeAppearanceCalculator = visualStyle
				.getEdgeAppearanceCalculator();
		edgeAppearanceCalculator.calculateEdgeAppearance(myEdgeApp, edge,
				network_view.getNetwork());
		myEdgeApp.applyAppearance(edgeView);

	}
}

/*
 File: GMLReader.java 
 
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

package cytoscape.data.readers;

// -----------------------------------------------------------------------------------------
import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.task.TaskMonitor;
import cytoscape.util.PercentUtil;
import cytoscape.visual.Arrow;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineType;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.ui.VizMapUI;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.GenericEdgeTargetArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeSourceArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeColorCalculator;
import cytoscape.visual.calculators.GenericEdgeLineTypeCalculator;
import cytoscape.visual.calculators.GenericNodeFillColorCalculator;
import cytoscape.visual.calculators.GenericNodeBorderColorCalculator;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeLineTypeCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.GenericNodeWidthCalculator;
import cytoscape.visual.calculators.GenericNodeHeightCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import cytoscape.dialogs.VisualStyleBuilderDialog;
import cytoscape.CytoscapeInit;
import cytoscape.init.CyInitParams;


/**
 * This class is responsible for converting a gml object tree into cytoscape
 * objects New features to the current version: 1. Small bug fixes. 2. Translate
 * all features in the GML file. This includes 3. New Visual Style will be
 * generated when you call this class. The new style saves all visual features
 * (like node shape) and will not be lost even after other style selected.
 */
public class GMLReader extends AbstractGraphReader {
	/**
	 * The following are all taken to be reserved keywords for gml (note that
	 * not all of them are actually keywords according to the spec)
	 * 
	 * Currently, only keywords below are supported by the Visual Style
	 * generation methods.
	 * 
	 * (Maybe we need some documents on "cytoscape-style" GML format...)
	 */

	// Graph Tags
	protected static String GRAPH = "graph";
	protected static String NODE = "node";
	protected static String EDGE = "edge";
	protected static String GRAPHICS = "graphics";
	protected static String LABEL = "label";
	protected static String SOURCE = "source";
	protected static String TARGET = "target";

	// The following elements are in "graphics" section of GML
	protected static String X = "x";
	protected static String Y = "y";
	protected static String H = "h";
	protected static String W = "w";
	protected static String TYPE = "type";
	protected static String ID = "id";
	protected static String ROOT_INDEX = "root_index";

	// Shapes used in Cytoscape (not GML standard)
	// In GML, they are called "type"
	protected static String RECTANGLE = "rectangle";
	protected static String ELLIPSE = "ellipse";
	protected static String LINE = "Line"; // This is the Polyline object.
	// no support for now...
	protected static String POINT = "point";
	protected static String DIAMOND = "diamond";
	protected static String HEXAGON = "hexagon";
	protected static String OCTAGON = "octagon";
	protected static String PARALELLOGRAM = "parallelogram";
	protected static String TRIANGLE = "triangle";

	// Other GML "graphics" attributes
	protected static String FILL = "fill";
	protected static String WIDTH = "width";

	protected static String STRAIGHT_LINES = "line";
	protected static String CURVED_LINES = "curved";

	protected static String SOURCE_ARROW = "source_arrow";
	protected static String TARGET_ARROW = "target_arrow";

	// States of the ends of arrows
	protected static String ARROW = "arrow";
	protected static String ARROW_NONE = "none";
	protected static String ARROW_FIRST = "first";
	protected static String ARROW_LAST = "last";
	protected static String ARROW_BOTH = "both";

	protected static String OUTLINE = "outline";
	protected static String OUTLINE_WIDTH = "outline_width";

	protected static String DEFAULT_EDGE_INTERACTION = "pp";

	protected static String VERSION = "Version";
	protected static String CREATOR = "Creator";

	private String mapSuffix;

	private Color DEF_COLOR = new Color(153, 153, 255);

	// Entries in the file
	List keyVals;

	// Node ID's
	OpenIntIntHashMap nodeIDMap;

	IntArrayList nodes, sources, targets;

	Vector node_labels, edge_labels, edge_root_index_pairs,
			node_root_index_pairs;

	Vector edge_names, node_names;

	IntArrayList giny_nodes, giny_edges;

	private TaskMonitor taskMonitor;

	private PercentUtil percentUtil;

	// Name for the new visual style
	String styleName;

	// New Visual Style comverted from GML file.
	VisualStyle gmlstyle;

	// Node appearence
	NodeAppearanceCalculator nac;

	// Edge appearence
	EdgeAppearanceCalculator eac;

	// Global appearence
	GlobalAppearanceCalculator gac;

	CalculatorCatalog catalog;

	// Hashes for node & edge attributes
	HashMap nodeW, nodeH, nodeShape, nodeCol, nodeBWidth, nodeBCol;

	HashMap edgeCol, edgeWidth, edgeArrow, edgeShape;

	/**
	 * Constructor.
	 * 
	 * @param filename File name.
	 */
	public GMLReader(String filename) {
		this(filename, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param filename File name.
	 * @param taskMonitor TaskMonitor Object.
	 */
	public GMLReader(String filename, TaskMonitor taskMonitor) {
		super(filename);	

		// Set new style name
		styleName = createVSName();
		initializeHash();
		initStyle();
		if (taskMonitor != null){
			this.taskMonitor = taskMonitor;
			percentUtil = new PercentUtil(5);
		}
	}

	private String createVSName() {
		// Create new style name

		String target = null;

		File fileTest = new File(fileName);
		target = fileTest.getName();
		System.out.println("Target GML file is " + target);

		mapSuffix = " for " + fileName;

		return target.concat("_GML_style");
	}

	private void initializeHash() {
		// Initialize HashMap for new visual style
		nodeW = new HashMap();
		nodeH = new HashMap();
		nodeShape = new HashMap();
		nodeCol = new HashMap();
		nodeBWidth = new HashMap();
		nodeBCol = new HashMap();
		edgeCol = new HashMap();
		edgeWidth = new HashMap();
		edgeArrow = new HashMap();
		edgeShape = new HashMap();

		edge_names = new Vector();
		node_names = new Vector();
	}

	// Initialize variables for the new style created from GML
	//
	private void initStyle() {
		nac = new NodeAppearanceCalculator();
		eac = new EdgeAppearanceCalculator();
		gac = new GlobalAppearanceCalculator();

		// Unlock the size object, then we can modify the both width and height.
		nac.setNodeSizeLocked(false);
	}

	// Create maps for the node attribute and set it as a Visual Style.
	//
	//
	public void setNodeMaps(VisualMappingManager vizmapper) {
		//
		// Set label for the nodes. (Uses "label" tag in the GML file)
		//
		String cName = "GML Labels" + mapSuffix;
		Calculator nlc = catalog.getCalculator(VizMapUI.NODE_LABEL,cName);
		if (nlc == null) {
			//System.out.println("creating passthrough mapping");
			PassThroughMapping m = new PassThroughMapping("",
					AbstractCalculator.ID);
			nlc = new GenericNodeLabelCalculator(cName, m);
		}
		nac.setCalculator(nlc);

		//
		// Set node shapes (Uses "type" tag in the GML file)
		//
		DiscreteMapping nodeShapeMapping = new DiscreteMapping(new Byte(
				ShapeNodeRealizer.ELLIPSE), AbstractCalculator.ID,
				ObjectMapping.NODE_MAPPING);
		nodeShapeMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), false);

		for (int i = 0; i < node_names.size(); i++) {
			String key = (String) node_names.get(i);
			Byte value;
			if (nodeShape.containsKey(key) == true) {
				value = (Byte) nodeShape.get(key);
			} else
				value = new Byte(ShapeNodeRealizer.ELLIPSE);

			nodeShapeMapping.putMapValue(key, value);
		}
		Calculator shapeCalculator = new GenericNodeShapeCalculator(
				"GML_Node_Shape" + mapSuffix, nodeShapeMapping);
		nac.setCalculator(shapeCalculator);

		//
		// Set the color of the node
		//
		Color defcol = Color.WHITE;

		DiscreteMapping nodeColorMapping = new DiscreteMapping(defcol,
				ObjectMapping.NODE_MAPPING);
		nodeColorMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);

		for (int i = 0; i < node_names.size(); i++) {
			String key = (String) node_names.get(i);
			String col;
			Color c;

			if (nodeCol.containsKey(key) == true) {
				col = nodeCol.get(key).toString();
				c = getColor(col);
			} else
				c = defcol;
			nodeColorMapping.putMapValue(key, c);
		}
		Calculator nodeColorCalculator = new GenericNodeFillColorCalculator(
				"GML Node Color" + mapSuffix, nodeColorMapping);
		nac.setCalculator(nodeColorCalculator);

		//
		// Set the color of the node border
		//
		Color defbcol = Color.BLACK;
		DiscreteMapping nodeBorderColorMapping = new DiscreteMapping(defcol,
				ObjectMapping.NODE_MAPPING);
		nodeBorderColorMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), true);

		for (int i = 0; i < node_names.size(); i++) {
			String key = (String) node_names.get(i);
			String col;
			Color c;
			if (nodeBCol.containsKey(key) == true) {
				col = nodeBCol.get(key).toString();
				c = getColor(col);
			} else
				c = defbcol;
			nodeBorderColorMapping.putMapValue(key, c);
		}
		Calculator nodeBorderColorCalculator = new GenericNodeBorderColorCalculator(
				"GML Node Border Color" + mapSuffix, nodeBorderColorMapping);
		nac.setCalculator(nodeBorderColorCalculator);

		//
		// Set the size of the nodes
		//
		Double defaultWidth = new Double(nac.getDefaultAppearance().getWidth());

		// First, set the width of the node
		DiscreteMapping nodeWMapping = new DiscreteMapping(defaultWidth,
				ObjectMapping.NODE_MAPPING);

		nodeWMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);
		// Set atrributes to each node
		for (int i = 0; i < node_names.size(); i++) {
			String key = (String) node_names.get(i);
			Double w;

			if (nodeW.containsKey(key) == true) {
				w = new Double(Double.parseDouble(nodeW.get(key).toString()));
			} else
				w = defaultWidth;
			nodeWMapping.putMapValue(key, w);
		}
		Calculator nodeSizeCalculatorW = new GenericNodeWidthCalculator(
				"GML Node Width" + mapSuffix, nodeWMapping);
		nac.setCalculator(nodeSizeCalculatorW);

		// Then set the height
		Double defaultHeight = new Double(nac.getDefaultAppearance().getHeight());

		DiscreteMapping nodeHMapping = new DiscreteMapping(defaultHeight,
				ObjectMapping.NODE_MAPPING);
		nodeHMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);
		// Set node height to each node
		for (int i = 0; i < node_names.size(); i++) {
			String key = (String) node_names.get(i);
			Double h;

			if (nodeH.containsKey(key) == true) {
				h = new Double(Double.parseDouble(nodeH.get(key).toString()));
			} else
				h = defaultHeight;
			nodeHMapping.putMapValue(key, h);
		}

		Calculator nodeSizeCalculatorH = new GenericNodeHeightCalculator(
				"GML Node Height" + mapSuffix, nodeHMapping);
		nac.setCalculator(nodeSizeCalculatorH);

		//
		// Set node border line type
		//
		DiscreteMapping nodeBorderTypeMapping = new DiscreteMapping(
				LineType.LINE_1, ObjectMapping.NODE_MAPPING);
		nodeBorderTypeMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), false);

		for (int i = 0; i < node_names.size(); i++) {
			String key = (String) node_names.get(i);
			double value = 1;
			int ivalue = 1;
			if (nodeBWidth.containsKey(key) == true) {
				value = Double.parseDouble(nodeBWidth.get(key).toString());
			}
			ivalue = (int) value;

			LineType lt = getLineType(ivalue);
			nodeBorderTypeMapping.putMapValue(key, lt);
		}
		Calculator nodeBoderTypeCalculator = new GenericNodeLineTypeCalculator(
				"GML Node Border" + mapSuffix, nodeBorderTypeMapping);
		nac.setCalculator(nodeBoderTypeCalculator);
	}

	//
	// Create maps for the edges
	//
	public void setEdgeMaps(VisualMappingManager vizmapper) {
		//
		// Set the color of the edges
		//
		Color defcol = eac.getDefaultAppearance().getColor();

		DiscreteMapping edgeColorMapping = new DiscreteMapping(defcol,
				ObjectMapping.EDGE_MAPPING);
		edgeColorMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), false);

		for (int i = 0; i < edge_names.size(); i++) {
			String key = (String) edge_names.get(i);
			String col;
			Color c;
			if (edgeCol.containsKey(key) == true) {
				col = edgeCol.get(key).toString();
				c = getColor(col);
			} else
				c = defcol;
			edgeColorMapping.putMapValue(key, c);
		}
		Calculator edgeColorCalculator = new GenericEdgeColorCalculator(
				"GML Edge Color" + mapSuffix, edgeColorMapping);
		eac.setCalculator(edgeColorCalculator);

		// 
		// Set line type based on the given width
		//
		DiscreteMapping edgeLineTypeMapping = new DiscreteMapping(
				LineType.LINE_1, ObjectMapping.EDGE_MAPPING);
		edgeLineTypeMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), false);

		for (int i = 0; i < edge_names.size(); i++) {
			String key = (String) edge_names.get(i);
			double value = 1;
			if (edgeWidth.containsKey(key) == true) {
				value = Double.parseDouble(edgeWidth.get(key).toString());
			}
			int ivalue = (int) value;
			LineType lt = getLineType(ivalue);
			edgeLineTypeMapping.putMapValue(key, lt);
		}
		Calculator edgeLineTypeCalculator = new GenericEdgeLineTypeCalculator(
				"GML Line Type" + mapSuffix, edgeLineTypeMapping);
		eac.setCalculator(edgeLineTypeCalculator);

		// 
		// Set arrow type.
		// GML does not include shape of the arrow, so the type is fixed.
		// Just determine the direction (node, both, source or target).
		//

		// For source
		DiscreteMapping edgeSourceArrowMapping = new DiscreteMapping(
				Arrow.NONE, ObjectMapping.EDGE_MAPPING);
		edgeSourceArrowMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), false);

		// For target
		DiscreteMapping edgeTargetArrowMapping = new DiscreteMapping(
				Arrow.NONE, ObjectMapping.EDGE_MAPPING);
		edgeTargetArrowMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), false);

		for (int i = 0; i < edge_names.size(); i++) {
			// Determine direction and arrow type
			String key = (String) edge_names.get(i);
			String value = null;
			if (edgeArrow.containsKey(key) == true) {
				value = edgeArrow.get(key).toString();
			} else
				value = "none";
			if (value.equals("none")) {
				edgeSourceArrowMapping.putMapValue(key, Arrow.NONE);
				edgeTargetArrowMapping.putMapValue(key, Arrow.NONE);
			} else if (value.equals("both")) {
				edgeSourceArrowMapping.putMapValue(key, Arrow.COLOR_ARROW);
				edgeTargetArrowMapping.putMapValue(key, Arrow.COLOR_ARROW);
			} else if (value.equals("last")) {
				edgeSourceArrowMapping.putMapValue(key, Arrow.NONE);
				edgeTargetArrowMapping.putMapValue(key, Arrow.COLOR_ARROW);
			} else if (value.equals("first")) {
				edgeSourceArrowMapping.putMapValue(key, Arrow.COLOR_ARROW);
				edgeTargetArrowMapping.putMapValue(key, Arrow.NONE);
			}

			// Alternative syntax: source and target with 0 || 1

			Calculator edgeSourceArrowCalculator = new GenericEdgeSourceArrowCalculator(
					"GML Source Arrow Type" + mapSuffix, edgeSourceArrowMapping);
			Calculator edgeTargetArrowCalculator = new GenericEdgeTargetArrowCalculator(
					"GML Target Arrow Type" + mapSuffix, edgeTargetArrowMapping);
			eac.setCalculator(edgeTargetArrowCalculator);
			eac.setCalculator(edgeSourceArrowCalculator);
		}
	}

	//
	// Apply node and edge maps by creating new visual style.
	public void applyMaps(String mapSuffix, String VSName) {
		// CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
		// VisualMappingManager vizmapper = cyDesktop.getVizMapManager();

		if (VSName != null) {
			styleName = VSName;
		}

		if (mapSuffix != null) {
			this.mapSuffix = mapSuffix;
		}

		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		catalog = vizmapper.getCalculatorCatalog();

		setNodeMaps(vizmapper);
		setEdgeMaps(vizmapper);

		//
		// Create new VS and apply it
		//
		gac.setDefaultBackgroundColor(DEF_COLOR);
		gmlstyle = new VisualStyle(styleName, nac, eac, gac);

		catalog.addVisualStyle(gmlstyle);
		vizmapper.setVisualStyle(gmlstyle);

		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
	}

	//
	// Read graph data from GML file.
	// VS will be created here.
	//
	public void read() {
		try {
			keyVals = (new GMLParser(fileName)).parseList();
		} catch (Exception io) {
			io.printStackTrace();
			if (taskMonitor != null)
				taskMonitor.setException(io, io.getMessage());
			throw new RuntimeException(io.getMessage());
		}
		initializeStructures();

		readGML(keyVals); // read the GML file
		createGraph(); // create the graph AND new visual style

		//
		// New features are called here:
		// 1 Extract (virtually) all attributes from the GML file
		// 2 Generate new VS
		// 3 Apply the new VS to the current window of Cytoscape
		//
		extract(); // Extract node & edge attributes
		// Properties prop = CytoscapeInit.getProperties();
		// String vsbSwitch = prop.getProperty("visualStyleBuilder");
		// if(vsbSwitch != null) {
		// if(vsbSwitch.equals("on")) {
		// applyMaps(null, null); // generate new VS and apply it.
		// }
		// }
		releaseStructures();
	}

	/**
	 * Returns a list containing the gml object tree
	 */
	public List getList() {
		return keyVals;
	}

	protected void initializeStructures() {
		nodes = new IntArrayList();
		sources = new IntArrayList();
		targets = new IntArrayList();
		node_labels = new Vector();
		edge_labels = new Vector();
		edge_root_index_pairs = new Vector();
		node_root_index_pairs = new Vector();
	}

	protected void releaseStructures() {
		nodes = null;
		sources = null;
		targets = null;
		node_labels = null;
		edge_labels = null;
		edge_root_index_pairs = null;
		node_root_index_pairs = null;
	}

	/**
	 * This will create the graph model objects. This function expects node
	 * labels to be unique and edge labels to be unique between a particular
	 * source and target If this condition is not met, an error will be printed
	 * to the console, and the object will be skipped. That is, it is as though
	 * that particular object never existed in the gml file. If an edge depends
	 * on a node that was skipped, then that edge will be skipped as well.
	 */
	protected void createGraph() {
		Cytoscape.ensureCapacity(nodes.size(), sources.size());
		nodeIDMap = new OpenIntIntHashMap(nodes.size());
		giny_nodes = new IntArrayList(nodes.size());
		OpenIntIntHashMap gml_id2order = new OpenIntIntHashMap(nodes.size());
		Set nodeNameSet = new HashSet(nodes.size());

		// Add All Nodes to Network
		for (int idx = 0; idx < nodes.size(); idx++) {

			// Report Status Value
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(2,
						idx, nodes.size()));
			}
			String label = (String) node_labels.get(idx);
			if (nodeNameSet.add(label)) {
				Node node = (Node) Cytoscape.getCyNode(label, true);
				giny_nodes.add(node.getRootGraphIndex());
				nodeIDMap.put(nodes.get(idx), node.getRootGraphIndex());
				gml_id2order.put(nodes.get(idx), idx);
				((KeyValue) node_root_index_pairs.get(idx)).value = (new Integer(
						node.getRootGraphIndex()));
			} else {
				throw new GMLException("GML id " + nodes.get(idx)
						+ " has a duplicated label: " + label);
				// ((KeyValue)node_root_index_pairs.get(idx)).value = null;
			}
		}
		nodeNameSet = null;

		giny_edges = new IntArrayList(sources.size());
		Set edgeNameSet = new HashSet(sources.size());

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// Add All Edges to Network
		for (int idx = 0; idx < sources.size(); idx++) {

			// Report Status Value
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(3,
						idx, sources.size()));
			}

			if (gml_id2order.containsKey(sources.get(idx))
					&& gml_id2order.containsKey(targets.get(idx))) {
				String label = (String) edge_labels.get(idx);
				String sourceName = (String) node_labels.get(gml_id2order
						.get(sources.get(idx)));
				String targetName = (String) node_labels.get(gml_id2order
						.get(targets.get(idx)));
				String edgeName = CyEdge.createIdentifier(sourceName,label,targetName);

				int duplicate_count = 1;
				while (!edgeNameSet.add(edgeName)) {
					edgeName = CyEdge.createIdentifier(sourceName,label,targetName)
							+ "_" + duplicate_count;

					duplicate_count += 1;
				}

				// String tempstr = "E name is :" + idx + "==" + edgeName;
				edge_names.add(idx, edgeName);

				Edge edge = Cytoscape.getRootGraph().getEdge(edgeName);

				if (edge == null) {
					Node node_1 = Cytoscape.getCyNode(sourceName);
					Node node_2 = Cytoscape.getCyNode(targetName);
					// edge = (Edge) rootGraph.getEdge
					// (rootGraph.createEdge(node_1, node_2));
					edge = Cytoscape.getCyEdge(node_1, node_2,
							Semantics.INTERACTION, label, true, true);
				}

				// Set correct ID, canonical name and interaction name
				edge.setIdentifier(edgeName);
				edgeAttributes.setAttribute(edgeName, Semantics.INTERACTION,
						label);

				giny_edges.add(edge.getRootGraphIndex());
				((KeyValue) edge_root_index_pairs.get(idx)).value = (new Integer(
						edge.getRootGraphIndex()));
			} else {
				throw new GMLException(
						"Non-existant source/target node for edge with gml (source,target): "
								+ sources.get(idx) + "," + targets.get(idx));
			}
		}
		edgeNameSet = null;
	}

	/**
	 * This function takes the root level list which defines a gml objec tree
	 */
	protected void readGML(List list) {
		// Report Progress Message
		int counter = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {

			// Report Progress Value
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentUtil.getGlobalPercent(1,
						counter, list.size()));
				counter++;
			}

			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(GRAPH)) {
				readGraph((List) keyVal.value);
			}
		}
	}

	/**
	 * This function takes in a list which was given as the value to a "graph"
	 * key underneath the main gml list
	 */
	protected void readGraph(List list) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(NODE)) {
				readNode((List) keyVal.value);
			}
			if (keyVal.key.equals(EDGE)) {
				readEdge((List) keyVal.value);
			}
		}
	}

	/**
	 * This will extract the model information from the list which is matched a
	 * "node" key
	 */
	protected void readNode(List list) {
		String label = "";
		boolean contains_id = false;
		int id = 0;
		KeyValue root_index_pair = null;
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(ID)) {
				contains_id = true;
				id = ((Integer) keyVal.value).intValue();
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ROOT_INDEX)) {
				root_index_pair = keyVal;
			}
		}

		if (label.equals("") || label.matches("\\s+") ) {
			label = String.valueOf(id);
		}
		if (root_index_pair == null) {
			root_index_pair = new KeyValue(ROOT_INDEX, null);
			list.add(root_index_pair);
		}
		if (!contains_id) {
			StringWriter stringWriter = new StringWriter();
			try {
				GMLParser.printList(list, stringWriter);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			throw new GMLException("The node-associated list\n" + stringWriter
					+ "is missing an id field");
		} else {
			node_root_index_pairs.add(root_index_pair);
			nodes.add(id);
			node_labels.add(label);
			node_names.add(label);
		}
	}

	/**
	 * This will extract the model information from the list which is matched to
	 * an "edge" key.
	 */
	protected void readEdge(List list) {
		String label = DEFAULT_EDGE_INTERACTION;
		boolean contains_source = false, contains_target = false;
		int source = 0, target = 0;
		KeyValue root_index_pair = null;
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(SOURCE)) {
				contains_source = true;
				source = ((Integer) keyVal.value).intValue();
			} else if (keyVal.key.equals(TARGET)) {
				contains_target = true;
				target = ((Integer) keyVal.value).intValue();
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ROOT_INDEX)) {
				root_index_pair = keyVal;
			}
		}

		if (root_index_pair == null) {
			root_index_pair = new KeyValue(ROOT_INDEX, null);
			list.add(root_index_pair);
		}
		if (!contains_source || !contains_target) {
			StringWriter stringWriter = new StringWriter();
			try {
				GMLParser.printList(list, stringWriter);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			throw new GMLException("The edge-associated list\n" + stringWriter
					+ " is missing a source or target key");
		} else {
			sources.add(source);
			targets.add(target);

			edge_labels.add(label);
			edge_root_index_pairs.add(root_index_pair);
		}
	}

	public void layout(GraphView myView) {
		if (myView == null || myView.nodeCount() == 0) {
			return;
		}
		if (keyVals == null) {
			throw new RuntimeException(
					"Failed to read gml file on initialization");
		}
		for (Iterator it = keyVals.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(GRAPH)) {
				layoutGraph(myView, (List) keyVal.value);
			}
		}

	}

	//
	// Extract all attributes of nodes and edges
	//
	public void extract() {
		if (keyVals == null) {
			throw new RuntimeException(
					"Failed to read gml file on initialization");
		}
		for (Iterator it = keyVals.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(GRAPH)) {
				extractGraph((List) keyVal.value);
			}
		}
	}

	protected void extractGraph(List list) {
		String edgeName = null;

		// Count the current edge
		int ePtr = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			final KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(NODE)) {
				extractNode((List) keyVal.value);
			} else if (keyVal.key.equals(EDGE)) {
				edgeName = (String) edge_names.get(ePtr);
				ePtr++;
				extractEdge((List) keyVal.value, edgeName);
			}
		}
	}

	protected void extractNode(List list) {
		
		List graphics_list = null;
		String label = null;

		int tempid = 0;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(ROOT_INDEX)) {
				if (keyVal.value == null) {
					return;
				}
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;

			} else if (keyVal.key.equals(ID)) {
				tempid = ((Integer) keyVal.value).intValue();
			}
		}
		if (graphics_list != null) {
			if (label == null) {
				label = "node" + tempid;
				System.out
						.println("Warning: node label is missing for node ID: "
								+ tempid);
			}
			extractNodeAttributes(graphics_list, label);
		}
	}

	protected void extractEdge(List list, String edgeName) {
		List graphics_list = null;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(ROOT_INDEX)) {
				if (keyVal.value == null) {
					return;
				}
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			}
		}

		if (graphics_list != null) {
			extractEdgeAttributes(graphics_list, edgeName);
		}
	}

	/**
	 * Lays Out the Graph, based on GML.
	 */
	protected void layoutGraph(final GraphView myView, List list) {

		String edgeName = null;
		// Count the current edge
		int ePtr = 0;

		for (Iterator it = list.iterator(); it.hasNext();) {
			final KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(NODE)) {
				layoutNode(myView, (List) keyVal.value);
			} else if (keyVal.key.equals(EDGE)) {

				edgeName = (String) edge_names.get(ePtr);
				ePtr++;
				layoutEdge(myView, (List) keyVal.value, edgeName);
			}
		}

	}

	/**
	 * Assign node properties based on the values in the list matched to the
	 * "node" key. Mostly just a wrapper around layoutNodeGraphics
	 */
	protected void layoutNode(GraphView myView, List list) {
		Integer root_index = null;
		List graphics_list = null;
		String label = null;
		int tempid = 0;

		NodeView view = null;
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(ROOT_INDEX)) {
				/*
				 * For some reason we didn't make an object for this node give
				 * up now
				 */
				if (keyVal.value == null) {
					return;
				}
				root_index = (Integer) keyVal.value;
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			} else if (keyVal.key.equals(LABEL)) {
				label = (String) keyVal.value;
			} else if (keyVal.key.equals(ID)) {
				tempid = ((Integer) keyVal.value).intValue();
			}
		}

		// System.out.print( "In layout, Root index is: " + root_index );
		// System.out.print( " Checking label: " + label );
		view = myView.getNodeView(root_index.intValue());
		if (label != null) {
			view.getLabel().setText(label);
		} else {
			view.getLabel().setText("node(" + tempid + ")");
		}
		if (graphics_list != null) {
			layoutNodeGraphics(myView, graphics_list, view);
			// extractNodeAttributes( graphics_list, label );

		}

	}

	/**
	 * This will assign node graphic properties based on the values in the list
	 * matches to the "graphics" key word
	 */
	protected void layoutNodeGraphics(GraphView myView, List list,
			NodeView nodeView) {

		for (Iterator it = list.iterator(); it.hasNext();) {

			KeyValue keyVal = (KeyValue) it.next();

			if (keyVal.key.equals(X)) {
				nodeView.setXPosition(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(Y)) {
				nodeView.setYPosition(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(H)) {
				nodeView.setHeight(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(W)) {
				nodeView.setWidth(((Number) keyVal.value).doubleValue());
			} else if (keyVal.key.equals(FILL)) {
				nodeView.setUnselectedPaint(getColor((String) keyVal.value));
			} else if (keyVal.key.equals(OUTLINE)) {
				nodeView.setBorderPaint(getColor((String) keyVal.value));
			} else if (keyVal.key.equals(OUTLINE_WIDTH)) {
				nodeView.setBorderWidth(((Number) keyVal.value).floatValue());
			} else if (keyVal.key.equals(TYPE)) {

				String type = (String) keyVal.value;

				if (type.equals(ELLIPSE)) {
					nodeView.setShape(NodeView.ELLIPSE);
				} else if (type.equals(RECTANGLE)) {
					nodeView.setShape(NodeView.RECTANGLE);
				} else if (type.equals(DIAMOND)) {
					nodeView.setShape(NodeView.DIAMOND);
				} else if (type.equals(HEXAGON)) {
					nodeView.setShape(NodeView.HEXAGON);
				} else if (type.equals(OCTAGON)) {
					nodeView.setShape(NodeView.OCTAGON);
				} else if (type.equals(PARALELLOGRAM)) {
					nodeView.setShape(NodeView.PARALELLOGRAM);
				} else if (type.equals(TRIANGLE)) {
					nodeView.setShape(NodeView.TRIANGLE);
				}
			}
		}
	}

	//
	// Extract node attributes from GML file
	protected void extractNodeAttributes(List list, String nodeName) {

		// Put all attributes into hashes.
		// Key is the node name
		// (Assume we do not have duplicate node name.)
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(X) || keyVal.key.equals(Y)) {
				// Do nothing.
			} else if (keyVal.key.equals(H)) {
				nodeH.put(nodeName, keyVal.value);
			} else if (keyVal.key.equals(W)) {
				nodeW.put(nodeName, keyVal.value);
			} else if (keyVal.key.equals(FILL)) {
				nodeCol.put(nodeName, keyVal.value);
			} else if (keyVal.key.equals(OUTLINE)) {
				nodeBCol.put(nodeName, keyVal.value);
			} else if (keyVal.key.equals(OUTLINE_WIDTH)) {
				nodeBWidth.put(nodeName, keyVal.value);
			} else if (keyVal.key.equals(TYPE)) {
				String type = (String) keyVal.value;
				if (type.equals(ELLIPSE)) {
					nodeShape
							.put(nodeName, new Byte(ShapeNodeRealizer.ELLIPSE));
				} else if (type.equals(RECTANGLE)) {
					nodeShape.put(nodeName, new Byte(ShapeNodeRealizer.RECT));
				} else if (type.equals(DIAMOND)) {
					nodeShape
							.put(nodeName, new Byte(ShapeNodeRealizer.DIAMOND));
				} else if (type.equals(HEXAGON)) {
					nodeShape
							.put(nodeName, new Byte(ShapeNodeRealizer.HEXAGON));
				} else if (type.equals(OCTAGON)) {
					nodeShape
							.put(nodeName, new Byte(ShapeNodeRealizer.OCTAGON));
				} else if (type.equals(PARALELLOGRAM)) {
					nodeShape.put(nodeName, new Byte(
							ShapeNodeRealizer.PARALLELOGRAM));
				} else if (type.equals(TRIANGLE)) {
					nodeShape.put(nodeName,
							new Byte(ShapeNodeRealizer.TRIANGLE));
				}
			}

		}
	}

	//
	// Extract edge attributes from GML input
	//

	protected void extractEdgeAttributes(List list, String edgeName) {
		String value = null;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(LINE)) {
				// This represents "Polyline," which is a line (usually an edge)
				// with arbitrary number of anchors.
				// Current version of CS does not support this, so ignore this
				// at this point of time...
			} else if (keyVal.key.equals(WIDTH)) {
				edgeWidth.put(edgeName, (Number) keyVal.value);
			} else if (keyVal.key.equals(FILL)) {
				edgeCol.put(edgeName, (String) keyVal.value);
			} else if (keyVal.key.equals(ARROW)) {
				edgeArrow.put(edgeName, (String) keyVal.value);
			} else if (keyVal.key.equals(TYPE)) {
				value = (String) keyVal.value;
				if (value.equals(STRAIGHT_LINES)) {
					edgeShape.put(edgeName, (String) keyVal.value);
				} else if (value.equals(CURVED_LINES)) {
					// edgeView.setLineType(EdgeView.CURVED_LINES);
				}
			} else if (keyVal.value.equals(SOURCE_ARROW)) {
				// edgeView.setSourceEdgeEnd(((Number)keyVal.value).intValue());
			} else if (keyVal.value.equals(TARGET_ARROW)) {
				// edgeView.setTargetEdgeEnd(((Number)keyVal.value).intValue());
			}
		}

	}

	//
	// Since GML represents line type as width, we need to
	// convert it to "LINE_TYPE"
	public static LineType getLineType(int width) {
		if (width == 1) {
			return LineType.LINE_1;
		} else if (width == 2) {
			return LineType.LINE_2;
		} else if (width == 3) {
			return LineType.LINE_3;
		} else if (width == 4) {
			return LineType.LINE_4;
		} else if (width == 5) {
			return LineType.LINE_5;
		} else if (width == 6) {
			return LineType.LINE_6;
		} else if (width == 7) {
			return LineType.LINE_7;
		} else {
			return LineType.LINE_1;
		}
	}

	//
	// The arrow infomation in GML is only directions.
	// this method convert
	/*
	 * public int getArrowType( String arrow ) { int arrowtype = 0;
	 * 
	 * if( arrow.equals( "none" ) ) { return 0; } else if( arrow.equals(
	 * "source" ) ) { return 1; } else if( arrow.equals( "target" ) ) { return 2 }
	 * else if( arrow.equals( "both" ) ) { return 3; } else return 0; }
	 * 
	 */

	// Testing
	public void showMaps() {

		String e = null;
		String n = null;
		String temp = null;

		for (int i = 0; i < edge_names.size(); i++) {
			e = (String) edge_names.get(i);
			temp = e + ": ";
			temp = temp + edgeCol.get(e) + ", ";
			temp = temp + edgeWidth.get(e) + ", ";
			temp = temp + edgeArrow.get(e) + ", ";
			temp = temp + edgeShape.get(e) + ", ";
			System.out.println(temp);
			temp = null;
		}
	}

	/**
	 * Assign edge visual properties based on pairs in the list matched to the
	 * "edge" key world
	 */
	protected void layoutEdge(GraphView myView, List list, String edgeName) {
		EdgeView edgeView = null;
		List graphics_list = null;

		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(ROOT_INDEX)) {
				/*
				 * Previously, we didn't make an object for this edge for some
				 * reason. Don't try to go any further.
				 */
				if (keyVal.value == null) {
					return;
				}
				edgeView = myView.getEdgeView(((Integer) keyVal.value)
						.intValue());
			} else if (keyVal.key.equals(GRAPHICS)) {
				graphics_list = (List) keyVal.value;
			}
		}

		if (edgeView != null && graphics_list != null) {
			layoutEdgeGraphics(myView, graphics_list, edgeView);
			// extractEdgeAttributes( graphics_list, edgeName );
		}
	}

	/**
	 * Assign edge graphics properties
	 */

	// Bug fix by Kei
	// Some of the conditions used "value."
	// They should be key.
	// Now this method correctly translate the GML input file
	// into graphics.
	//
	protected void layoutEdgeGraphics(GraphView myView, List list,
			EdgeView edgeView) {
		// Local vars.
		String value = null;
		KeyValue keyVal = null;

		for (Iterator it = list.iterator(); it.hasNext();) {
			keyVal = (KeyValue) it.next();

			// This is a polyline obj. However, it will be translated into
			// straight line.
			if (keyVal.key.equals(LINE)) {
				layoutEdgeGraphicsLine(myView, (List) keyVal.value, edgeView);
			} else if (keyVal.key.equals(WIDTH)) {
				edgeView.setStrokeWidth(((Number) keyVal.value).floatValue());
			} else if (keyVal.key.equals(FILL)) {
				edgeView.setUnselectedPaint(getColor((String) keyVal.value));
			}

			else if (keyVal.key.equals(TYPE)) {
				value = (String) keyVal.value;
				if (value.equals(STRAIGHT_LINES)) {
					edgeView.setLineType(EdgeView.STRAIGHT_LINES);
				} else if (value.equals(CURVED_LINES)) {
					edgeView.setLineType(EdgeView.CURVED_LINES);
				}
			}

			else if (keyVal.key.equals(ARROW)) {
				// The position of the arrow.
				// There are 4 states: no arrows, both ends have arrows, source,
				// or target.
				//
				// The arrow type below is hard-coded since GML does not
				// support shape of the arrow.
				if (keyVal.value.equals(ARROW_FIRST)) {
					edgeView.setSourceEdgeEnd(2);
				} else if (keyVal.value.equals(ARROW_LAST)) {
					edgeView.setTargetEdgeEnd(2);
				} else if (keyVal.value.equals(ARROW_BOTH)) {
					edgeView.setSourceEdgeEnd(2);
					edgeView.setTargetEdgeEnd(2);
				} else if (keyVal.value.equals(ARROW_NONE)) {
					// Do nothing. No arrows.
				}
				if (keyVal.key.equals(SOURCE_ARROW)) {
					edgeView.setSourceEdgeEnd(((Number) keyVal.value)
							.intValue());
				} else if (keyVal.value.equals(TARGET_ARROW)) {
					edgeView.setTargetEdgeEnd(((Number) keyVal.value)
							.intValue());
				}
			}
		}
	}

	/**
	 * Assign bend points based on the contents of the list associated with a
	 * "Line" key We make sure that there is both an x,y present in the
	 * underlying point list before trying to generate a bend point
	 */
	protected void layoutEdgeGraphicsLine(GraphView myView, List list,
			EdgeView edgeView) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			KeyValue keyVal = (KeyValue) it.next();
			if (keyVal.key.equals(POINT)) {
				Number x = null, y = null;
				for (Iterator pointIt = ((List) keyVal.value).iterator(); pointIt
						.hasNext();) {
					KeyValue pointVal = (KeyValue) pointIt.next();
					if (pointVal.key.equals(X)) {
						x = (Number) pointVal.value;
					} else if (pointVal.key.equals(Y)) {
						y = (Number) pointVal.value;
					}
				}
				if (!(x == null || y == null)) {
					Point2D.Double pt = new Point2D.Double(x.doubleValue(), y
							.doubleValue());
					edgeView.getBend().addHandle(pt);
				}
			}
		}
	}

	/**
	 * Part of interface contract
	 */
	public int[] getNodeIndicesArray() {
		giny_nodes.trimToSize();
		return giny_nodes.elements();
	}

	/**
	 * Part of interace contract
	 */
	public int[] getEdgeIndicesArray() {
		giny_edges.trimToSize();
		return giny_edges.elements();
	}

	/**
	 * Create a color object from the string like it is stored in a gml file
	 */
	public Color getColor(String colorString) {
		return new Color(Integer.parseInt(colorString.substring(1), 16));
	}

	public void doPostProcessing(CyNetwork net) {
		CyInitParams init = CytoscapeInit.getCyInitParams();
		if ( init == null )
			return;

		if ( init.getMode() == CyInitParams.GUI ||
		     init.getMode() == CyInitParams.EMBEDDED_WINDOW ) {
			VisualStyleBuilderDialog vsd = new VisualStyleBuilderDialog(
				         net.getTitle(), 
					 (GraphReader) net.getClientData(Cytoscape.READER_CLIENT_KEY),
				         Cytoscape.getDesktop(), 
					 true);
			vsd.setVisible(true);
		}
	}
}

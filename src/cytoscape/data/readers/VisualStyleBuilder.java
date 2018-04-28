/*
 File: VisualStyleBuilder.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute of Systems Biology
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

import cytoscape.Cytoscape;
import cytoscape.generated2.Att;
import cytoscape.generated2.Graphics;
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
import cytoscape.visual.calculators.GenericEdgeSourceArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeTargetArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeColorCalculator;
import cytoscape.visual.calculators.GenericEdgeFontFaceCalculator;
import cytoscape.visual.calculators.GenericEdgeLabelCalculator;
import cytoscape.visual.calculators.GenericEdgeLineTypeCalculator;
import cytoscape.visual.calculators.GenericNodeFillColorCalculator;
import cytoscape.visual.calculators.GenericNodeBorderColorCalculator;
import cytoscape.visual.calculators.GenericNodeFontFaceCalculator;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeLineTypeCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.GenericNodeWidthCalculator;
import cytoscape.visual.calculators.GenericNodeHeightCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import giny.view.EdgeView;
import java.awt.Color;
import java.awt.Font;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Based on the graph/node/edge view information, build new Visual Style.
 * 
 * This class accepts JAXB object called "Graphics" as input value. We can add
 * information by adding elements to the object as attributes (Object
 * cytoscape.generated2.Att)
 * 
 * @author kono
 * 
 */
public class VisualStyleBuilder {

	protected static final byte DEFAULT_SHAPE = ShapeNodeRealizer.ELLIPSE;
	protected static final Color DEFAULT_COLOR = Color.WHITE;
	protected static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
	protected static final int DEFAULT_LINE_WIDTH = 1;
	// Name for the new visual style
	/**
	 * @uml.property  name="styleName"
	 */
	private String styleName;

	// New Visual Style comverted from GML file.
	/**
	 * @uml.property  name="xgmmlStyle"
	 * @uml.associationEnd  
	 */
	private VisualStyle xgmmlStyle;

	// Node appearence
	/**
	 * @uml.property  name="nac"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NodeAppearanceCalculator nac;

	// Edge appearence
	/**
	 * @uml.property  name="eac"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private EdgeAppearanceCalculator eac;

	// Global appearence
	/**
	 * @uml.property  name="gac"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GlobalAppearanceCalculator gac;
	/**
	 * @uml.property  name="catalog"
	 * @uml.associationEnd  
	 */
	private CalculatorCatalog catalog;

	/**
	 * @uml.property  name="nodeGraphics"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="cytoscape.generated2.Att" qualifier="key:java.lang.String cytoscape.generated2.Graphics"
	 */
	private HashMap nodeGraphics;
	/**
	 * @uml.property  name="edgeGraphics"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="cytoscape.generated2.Att" qualifier="key:java.lang.String cytoscape.generated2.Graphics"
	 */
	private HashMap edgeGraphics;
	/**
	 * @uml.property  name="globalGraphics"
	 */
	private HashMap globalGraphics;

	public VisualStyleBuilder() {

		initialize();

	}

	/**
	 * Accept List of JAXB graphics objects
	 * 
	 * @param graphics
	 */
	public VisualStyleBuilder(String newName, Map nodeGraphics,
			Map edgeGraphics, Map globalGraphics) {
		initialize();

		this.nodeGraphics = (HashMap) nodeGraphics;
		this.edgeGraphics = (HashMap) edgeGraphics;
		this.globalGraphics = (HashMap) globalGraphics;

		this.styleName = newName;

	}

	private void initialize() {
		nac = new NodeAppearanceCalculator();
		eac = new EdgeAppearanceCalculator();
		gac = new GlobalAppearanceCalculator();

		// Unlock the size object, then we can modify the both width and height.
		nac.setNodeSizeLocked(false);
	}

	public void buildStyle() {

		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		catalog = vizmapper.getCalculatorCatalog();

		setNodeMaps(vizmapper);
		setEdgeMaps(vizmapper);

		//
		// Create new VS and apply it
		//
		gac.setDefaultBackgroundColor(new Color(255, 255, 204));
		xgmmlStyle = new VisualStyle(styleName, nac, eac, gac);

		// System.out.println(nac.getDescription());
		// System.out.println(eac.getDescription());

		catalog.addVisualStyle(xgmmlStyle);
		vizmapper.setVisualStyle(xgmmlStyle);
	}

	protected void setNodeMaps(VisualMappingManager vizmapper) {
		//
		// Set label for the nodes. (Uses "label" tag in the GML file)
		//
		String cName = "XGMML Labels";
		Calculator nlc = catalog.getCalculator(VizMapUI.NODE_LABEL,cName);
		if (nlc == null) {
			PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);
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

		//
		// Set the color of the node
		//
		DiscreteMapping nodeColorMapping = new DiscreteMapping(DEFAULT_COLOR,
				ObjectMapping.NODE_MAPPING);
		nodeColorMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);

		DiscreteMapping nodeBorderColorMapping = new DiscreteMapping(
				DEFAULT_BORDER_COLOR, ObjectMapping.NODE_MAPPING);
		nodeBorderColorMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), true);

		Double defaultWidth = new Double(nac.getDefaultAppearance().getWidth());
		DiscreteMapping nodeWMapping = new DiscreteMapping(defaultWidth,
				ObjectMapping.NODE_MAPPING);
		nodeWMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);

		Double defaultHeight = new Double(nac.getDefaultAppearance().getHeight());
		DiscreteMapping nodeHMapping = new DiscreteMapping(defaultHeight,
				ObjectMapping.NODE_MAPPING);
		nodeHMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);

		DiscreteMapping nodeBorderTypeMapping = new DiscreteMapping(
				LineType.LINE_1, ObjectMapping.NODE_MAPPING);
		nodeBorderTypeMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), false);

		// Non-GML graphics attributes
		Font defaultNodeFont = nac.getDefaultAppearance().getFont();
		DiscreteMapping nodeLabelFontMapping = new DiscreteMapping(
				defaultNodeFont, ObjectMapping.NODE_MAPPING);
		nodeLabelFontMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);

		Iterator it = nodeGraphics.keySet().iterator();

		// for (int i = 0; i < node_names.size(); i++) {
		while (it.hasNext()) {
			String key = (String) it.next();
			Byte shapeValue;
			Color nodeColor;
			Color nodeBorderColor;
			Double w;
			Double h;

			LineType lt;

			// Cytoscape local graphics attributes
			String nodeLabelFont;
			String borderLineType;

			Font nodeFont = null;

			// Extract node graphics object from the given map
			Graphics curGraphics = (Graphics) nodeGraphics.get(key);
			List localNodeGraphics = null;
			Iterator localIt = null;

			if (curGraphics != null) {

				localNodeGraphics = curGraphics.getAtt();

				if (localNodeGraphics != null) {
					Att lg = (Att) localNodeGraphics.get(0);

					localIt = lg.getContent().iterator();
				}
			}
			// Get node shape
			if (curGraphics != null && curGraphics.getType() != null) {
				shapeValue = ShapeNodeRealizer
						.parseNodeShapeTextIntoByte(curGraphics.getType().value());
				nodeColor = getColor(curGraphics.getFill());
				nodeBorderColor = getColor(curGraphics.getOutline());
				w = new Double(curGraphics.getW());
				h = new Double(curGraphics.getH());
				BigInteger lineWidth = curGraphics.getWidth();
				if (lineWidth != null) {
					lt = getLineType(lineWidth.intValue());
				} else {
					lt = LineType.LINE_1;
				}

				while (localIt.hasNext()) {
					Att nodeAttr = null;
					Object curObj = localIt.next();

					if (curObj.getClass().equals(Att.class)) {
						nodeAttr = (Att) curObj;

						if (nodeAttr.getName().equals("nodeLabelFont")) {
							nodeLabelFont = nodeAttr.getValue();
							String[] fontString = nodeLabelFont.split("-");
							nodeFont = new Font(fontString[0], Integer
									.parseInt(fontString[1]), Integer
									.parseInt(fontString[2]));

						} else if (nodeAttr.getName().equals("borderLineType")) {

						}
					}
				}

			} else {
				shapeValue = new Byte(DEFAULT_SHAPE);
				nodeColor = DEFAULT_COLOR;
				nodeBorderColor = DEFAULT_BORDER_COLOR;
				w = defaultWidth;
				h = defaultHeight;
				lt = LineType.LINE_1;
				nodeFont = new Font("Default", 0, 10);
			}
			nodeShapeMapping.putMapValue(key, shapeValue);
			nodeColorMapping.putMapValue(key, nodeColor);
			nodeBorderColorMapping.putMapValue(key, nodeBorderColor);
			nodeWMapping.putMapValue(key, w);
			nodeHMapping.putMapValue(key, h);
			nodeBorderTypeMapping.putMapValue(key, lt);
			nodeLabelFontMapping.putMapValue(key, nodeFont);
		}
		Calculator shapeCalculator = new GenericNodeShapeCalculator(
				"XGMML Node Shape", nodeShapeMapping);
		nac.setCalculator(shapeCalculator);

		Calculator nodeColorCalculator = new GenericNodeFillColorCalculator(
				"XGMML Node Color", nodeColorMapping);
		nac.setCalculator(nodeColorCalculator);

		Calculator nodeBorderColorCalculator = new GenericNodeBorderColorCalculator(
				"XGMML Node Border Color", nodeBorderColorMapping);
		nac.setCalculator(nodeBorderColorCalculator);

		Calculator nodeSizeCalculatorW = new GenericNodeWidthCalculator(
				"XGMML Node Width", nodeWMapping);
		nac.setCalculator(nodeSizeCalculatorW);

		Calculator nodeSizeCalculatorH = new GenericNodeHeightCalculator(
				"XGMML Node Height", nodeHMapping);
		nac.setCalculator(nodeSizeCalculatorH);
		Calculator nodeBoderTypeCalculator = new GenericNodeLineTypeCalculator(
				"XGMML Node Border", nodeBorderTypeMapping);
		nac.setCalculator(nodeBoderTypeCalculator);

		Calculator nodeFontCalculator = new GenericNodeFontFaceCalculator(
				"XGMML Node Label Font", nodeLabelFontMapping);
		nac.setCalculator(nodeFontCalculator);
	}

	protected void setEdgeMaps(VisualMappingManager vizmapper) {

		//
		// Set label for the nodes. (Uses "label" tag in the GML file)
		//
		String cName = "XGMML Labels";
		Calculator elc = catalog.getCalculator(VizMapUI.EDGE_LABEL,cName);
		if (elc == null) {
			PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);
			elc = new GenericEdgeLabelCalculator(cName, m);
		}
		eac.setCalculator(elc);

		//
		// Set the color of the node
		//
		DiscreteMapping edgeColorMapping = new DiscreteMapping(DEFAULT_COLOR,
				ObjectMapping.EDGE_MAPPING);
		edgeColorMapping.setControllingAttributeName(AbstractCalculator.ID,
				vizmapper.getNetwork(), true);

		DiscreteMapping edgeLineTypeMapping = new DiscreteMapping(
				LineType.LINE_4, ObjectMapping.EDGE_MAPPING);
		edgeLineTypeMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), true);

		// Non-GML graphics attributes
		Font defaultEdgeFont = eac.getDefaultAppearance().getFont();
		DiscreteMapping edgeLabelFontMapping = new DiscreteMapping(
				defaultEdgeFont, ObjectMapping.EDGE_MAPPING);
		edgeLabelFontMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), true);

		// For source & target arrows
		DiscreteMapping edgeSourceArrowMapping = new DiscreteMapping(eac
				.getDefaultAppearance().getSourceArrow(), ObjectMapping.EDGE_MAPPING);
		edgeSourceArrowMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), true);

		DiscreteMapping edgeTargetArrowMapping = new DiscreteMapping(eac
				.getDefaultAppearance().getTargetArrow(), ObjectMapping.EDGE_MAPPING);
		edgeTargetArrowMapping.setControllingAttributeName(
				AbstractCalculator.ID, vizmapper.getNetwork(), true);

		Iterator it = edgeGraphics.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();

			Color edgeColor;
			Font edgeFont = null;
			LineType edgeLineType = null;

			Arrow source = null;
			Arrow target = null;

			// Extract node graphics object from the given map
			Graphics curGraphics = (Graphics) edgeGraphics.get(key);
			List localEdgeGraphics = null;
			Iterator localIt = null;
			if (curGraphics != null) {
				localEdgeGraphics = curGraphics.getAtt();
				localIt = null;
				if (localEdgeGraphics != null && localEdgeGraphics.size() != 0) {
					Att lg = (Att) localEdgeGraphics.get(0);

					localIt = lg.getContent().iterator();
				}
			}
			// Get node shape
			if (curGraphics != null) {

				edgeColor = getColor(curGraphics.getFill());

				// Edge informaiton
				Color sourceColor = null;
				Color targetColor = null;
				String sourceType = null;
				String targetType = null;

				if (localIt != null) {
					while (localIt.hasNext()) {
						Att edgeAttr = null;
						Object curObj = localIt.next();

						if (curObj.getClass().equals(Att.class)) {
							edgeAttr = (Att) curObj;

							String edgeLabelFont = null;

							if (edgeAttr.getName().equals("edgeLabelFont")) {
								edgeLabelFont = edgeAttr.getValue();
								String[] fontString = edgeLabelFont.split("-");
								edgeFont = new Font(fontString[0], Integer
										.parseInt(fontString[1]), Integer
										.parseInt(fontString[2]));

							} else if (edgeAttr.getName()
									.equals("edgeLineType")) {
								edgeLineType = LineType
										.parseLineTypeText(edgeAttr.getValue());
							} else if (edgeAttr.getName().equals("sourceArrow")) {
								sourceType = edgeAttr.getValue();
							} else if (edgeAttr.getName().equals("targetArrow")) {
								targetType = edgeAttr.getValue();
							} else if (edgeAttr.getName().equals(
									"sourceArrowColor")) {
								sourceColor = getColor(edgeAttr.getValue());
							} else if (edgeAttr.getName().equals(
									"targetArrowColor")) {
								targetColor = getColor(edgeAttr.getValue());
							}
						}
					}

					// Create arrow if available
					if (sourceColor != null && sourceType != null) {
						source = arrowBuilder(sourceType, sourceColor);
					}
					if (targetColor != null && targetType != null) {
						target = arrowBuilder(targetType, targetColor);
					}
				}

			} else {

				edgeColor = DEFAULT_COLOR;
				edgeLineType = LineType.LINE_1;
				edgeFont = new Font("Default", 0, 10);
				source = Arrow.NONE;
				target = Arrow.NONE;
			}

			edgeColorMapping.putMapValue(key, edgeColor);
			edgeLineTypeMapping.putMapValue(key, edgeLineType);
			edgeLabelFontMapping.putMapValue(key, edgeFont);

			edgeSourceArrowMapping.putMapValue(key, source);
			edgeTargetArrowMapping.putMapValue(key, target);
		}

		Calculator edgeColorCalculator = new GenericEdgeColorCalculator(
				"XGMML Edge Color", edgeColorMapping);
		eac.setCalculator(edgeColorCalculator);

		Calculator edgeLineTypeCalculator = new GenericEdgeLineTypeCalculator(
				"XGMML Edge Line Type", edgeLineTypeMapping);
		eac.setCalculator(edgeLineTypeCalculator);

		Calculator edgeFontCalculator = new GenericEdgeFontFaceCalculator(
				"XGMML Edge Label Font", edgeLabelFontMapping);
		eac.setCalculator(edgeFontCalculator);

		Calculator edgeSourceArrowCalculator = new GenericEdgeSourceArrowCalculator(
				"XGMML Source Edge Arrow", edgeSourceArrowMapping);
		eac.setCalculator(edgeSourceArrowCalculator);

		Calculator edgeTargetArrowCalculator = new GenericEdgeTargetArrowCalculator(
				"XGMML Target Edge Arrow", edgeTargetArrowMapping);
		eac.setCalculator(edgeTargetArrowCalculator);
	}

	/**
	 * Create a color object from the string like it is stored in a gml file
	 */
	private Color getColor(String colorString) {
		// int red = Integer.parseInt(colorString.substring(1,3),16);
		// int green = Integer.parseInt(colorString.substring(3,5),16);
		// int blue = Integer.parseInt(colorString.substring(5,7),16);
		return new Color(Integer.parseInt(colorString.substring(1), 16));
	}

	// Since GML represents line type as width, we need to
	// convert it to "LINE_TYPE"
	private static LineType getLineType(int width) {
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

	// Convert GINY arrow information into Arrow object in Cytoscape.
	//
	private Arrow arrowBuilder(String type, Color color) {

		// Set default to none
		Arrow ar = Arrow.NONE;

		int intType = Integer.parseInt(type);

		if (intType == EdgeView.WHITE_DIAMOND) {
			ar = Arrow.WHITE_DIAMOND;
		} else if (intType == EdgeView.BLACK_DIAMOND) {
			ar = Arrow.BLACK_DIAMOND;
		} else if (intType == EdgeView.EDGE_COLOR_DIAMOND) {
			ar = Arrow.COLOR_DIAMOND;
		}

		else if (intType == EdgeView.WHITE_DELTA) {
			ar = Arrow.WHITE_DELTA;
		} else if (intType == EdgeView.BLACK_DELTA) {
			ar = Arrow.BLACK_DELTA;
		} else if (intType == EdgeView.EDGE_COLOR_DELTA) {
			ar = Arrow.COLOR_DELTA;
		}

		else if (intType == EdgeView.WHITE_ARROW) {
			ar = Arrow.WHITE_ARROW;
		} else if (intType == EdgeView.BLACK_ARROW) {
			ar = Arrow.BLACK_ARROW;
		} else if (intType == EdgeView.EDGE_COLOR_ARROW) {
			ar = Arrow.COLOR_ARROW;
		}

		else if (intType == EdgeView.WHITE_T) {
			ar = Arrow.WHITE_T;
		} else if (intType == EdgeView.BLACK_T) {
			ar = Arrow.BLACK_T;
		} else if (intType == EdgeView.EDGE_COLOR_T) {
			ar = Arrow.COLOR_T;
		}

		else if (intType == EdgeView.WHITE_CIRCLE) {
			ar = Arrow.WHITE_CIRCLE;
		} else if (intType == EdgeView.BLACK_CIRCLE) {
			ar = Arrow.BLACK_CIRCLE;
		} else if (intType == EdgeView.EDGE_COLOR_CIRCLE) {
			ar = Arrow.COLOR_CIRCLE;
		}

		return ar;
	}

}

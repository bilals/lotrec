
/*
  File: OldStyleCalculatorIO.java 
  
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

//----------------------------------------------------------------------------
// $Revision: 8887 $
// $Date: 2006-11-20 17:50:13 -0800 (Mon, 20 Nov 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JTextArea;
import javax.swing.event.*;
import java.util.Properties;
import java.util.Enumeration;

import cytoscape.util.Misc;
import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.LineTypeParser;
import cytoscape.visual.calculators.*;
import cytoscape.visual.ui.VizMapUI;
//----------------------------------------------------------------------------
/**
 * This class provides methods to read property keys in the old vizmap format,
 * construct suitable calculator objects, and install them in a supplied
 * CalculatorCatalog.
 *
 * All of the calculators will have the name of the static member variable
 * 'calcName', except that the borderColor calculator, the node height
 * calculator, and the targetDecoration calculator will have a '2' appended
 * to the name. (This is because in the old system, node.fillColor and
 * node.borderColor were separate types, but are both NodeColorCalculators
 * in the new system, and similarly with the node size and edge arrow calculators).
 *
 * In addition to the attribute specific calculators, this class will create
 * a NodeAppearance and EdgeAppearanceCalculator, also named 'calcName', and
 * set them to use the attribute calculators defined here.
 * @deprecated Use CalculatorIO instead. Will be removed 10/2007 
 */
public class OldStyleCalculatorIO {
    
    public static String packageHeader = "cytoscape.visual.calculators.";
    public static String calcName = "oldFormat";
    public static boolean loaded = false;
    //helper variable used to halt execution until the UI is closed
    private static boolean isDone = false;
    
    /**
     * This method takes a properties object as an argument, expected to be the
     * contents of a cytoscape.props file, and a CalculatorCatalog. It searches
     * for property keys in the props that represent visual mappings in the old
     * format, and if found brings up a UI to inform the user, prompt for a name
     * for the calculators, and calls the loadCalculators method to convert and
     * install these visual mappings.
     *
     * @param props  a properties object, should come from cytoscape.props
     * @param catalog  the catalog in which to store any found visual mappings
 * @deprecated Use CalculatorIO instead. Will be removed 10/2007 
     */
    public static void checkForCalculators(Properties props, CalculatorCatalog catalog) {
        boolean found = false;
        //check every key to see if it is part of the old format visual mappings
        for (Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            if (key.startsWith("node.fillColor.") ||
                key.startsWith("node.borderColor.") ||
                key.startsWith("node.selectedColor.") ||
                key.startsWith("node.borderLinetype.") ||
                key.startsWith("node.width.") ||
                key.startsWith("node.height.") ||
                key.startsWith("node.shape.") ||
                key.startsWith("edge.color.") ||
                key.startsWith("background.color.") ||
                key.startsWith("edge.linetype.") ||
                key.startsWith("edge.sourceDecoration.") ||
                key.startsWith("edge.targetDecoration.") ) {
                    //we found at least one matching line
                    found = true;
                    break;
            }
        }
        if (!found) {return;}//not found, so there's nothing to do
        
        final JFrame frame = new JFrame();
        
        //create some text to tell the user what is going on
        String lineSep = System.getProperty("line.separator");
        StringBuffer message = new StringBuffer();
        message.append("Your cytoscape.props file contains visual mappings specified");
        message.append(" in an old format.").append(lineSep);
        message.append("Cytoscape can automatically convert these mappings into the");
        message.append(" new format.").append(lineSep);
        message.append("Simply specify a name for this group of visual mappings,");
        message.append(" and Cytoscape").append(lineSep);
        message.append("will save them to your visual mappings");
        message.append(" properties file").append(lineSep);
        message.append("(default: vizmap.props in your home directory).");
        message.append(lineSep).append(lineSep);
        message.append("To avoid seeing this message again, please remove all visual");
        message.append(" mapping").append(lineSep);
        message.append("specifications from your cytoscape.props file.");
        message.append(lineSep);
        JTextArea theText = new JTextArea( message.toString() );
        theText.setEditable(false);
        theText.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        
        //prompt for a name for the new visual style
        JLabel nameLabel = new JLabel("enter a name for these mappings");
        /* try to use the name defined by the variable 'calcName'
         * if a visual style with that name already exists, then we force the user
         * to make up a name, then check for uniqueness */
        String checkedName = catalog.checkVisualStyleName(calcName);
        if (!calcName.equals(checkedName)) {//force user to pick a new name
            calcName = "";
        }
        final JTextField nameEntry = new JTextField(calcName, 20);
        nameEntry.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                calcName = nameEntry.getText();
            }
        });
        
        //create an OK button to trigger converting and loading the visual mappings
        final Properties theProps = props;
        final CalculatorCatalog theCatalog = catalog;
        JButton okButton = new JButton("Convert old visual mapppings");
        okButton.setMargin(new Insets(0,0,0,0));
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //only load if the provided name is acceptable
                calcName = nameEntry.getText();
                if (calcName == null || calcName.equals("")) {
                    String s = "You must specify a name for these mappings.";
                    JOptionPane.showMessageDialog(frame, s, "Bad name", JOptionPane.ERROR_MESSAGE);
                } else if (checkName(theCatalog)) {
                    loadCalculators(theProps, theCatalog);
                    loaded = true;
                    frame.dispose();
                    setDone();
                } else {
                    String s = "The name you entered is already in use. Please choose another.";
                    JOptionPane.showMessageDialog(frame, s, "Bad name", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //add a button to just ignore old-style visual mappings
        JButton ignoreButton = new JButton("Ignore old visual mappings");
        ignoreButton.setMargin(new Insets(0,0,0,0));
        ignoreButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
                setDone();
            }
        });
        
        //build up the whole UI
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameEntry);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout() );
        buttonPanel.add(okButton);
        buttonPanel.add(ignoreButton);
        
        Container contentPane = frame.getContentPane();
        contentPane.setLayout( new BoxLayout(contentPane, BoxLayout.Y_AXIS) );
        contentPane.add(theText);
        contentPane.add(namePanel);
        contentPane.add(buttonPanel);
        //set ok to be the default
        frame.getRootPane().setDefaultButton(okButton);
        
        //add a window listener to allow execution to continue when the window closes
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setDone();
            }
        });
        frame.pack();
        frame.show();
        
        //halt until the UI closes
        while (!isDone) {
            //wait(100);
        }
    }
    
    /**
     * Helper method called from the UI listeners to set a boolean flag
     * that breaks the while loop in the above method.
     */
    private static void setDone() {isDone = true;}
    
    /**
     * Tests if a node or edge appearance calculator with the name specified
     * by the variable 'calcName' already exists. Returns false if a match
     * is found, true if the name is not currently used.
     */
    private static boolean checkName(CalculatorCatalog catalog) {
        VisualStyle vs = catalog.getVisualStyle(calcName);
        if (vs == null) {return true;} else {return false;}
    }
    
    /**
     * Given a properties object, which is expected to come from a
     * cytoscape.props file, and a CalculatorCatalog, loads all visual mappings
     * specified in the old format by first converting the properties into the
     * new format and then running the converted properties through the
     * machinery of the CalculatorIO class.
 * @deprecated Use CalculatorIO instead. Will be removed 10/2007 
     */
    public static void loadCalculators(Properties props, CalculatorCatalog catalog) {
        String colorInterpolator = "LinearNumberToColorInterpolator";
        String numberInterpolator = "LinearNumberToNumberInterpolator";
        String flatInterpolator = "FlatInterpolator";
        
        String nodeColorName = "nodeColorCalculator." + calcName;
        String nodeColorClass = packageHeader + "GenericNodeColorCalculator";
        String nodeColorIntClass = NodeColorCalculator.class.getName();
        loadCalculator(props, catalog, "node.fillColor", nodeColorName, nodeColorClass,
        nodeColorIntClass, colorInterpolator);
        String nodeColorName2 = nodeColorName + "2";
        loadCalculator(props, catalog, "node.borderColor", nodeColorName2,
        nodeColorClass, nodeColorIntClass, colorInterpolator);
        String nodeLineTypeName = "nodeLineTypeCalculator." + calcName;
        String nodeLineTypeClass = packageHeader + "GenericNodeLineTypeCalculator";
        String nodeLineTypeIntClass = NodeLineTypeCalculator.class.getName();
        loadCalculator(props, catalog, "node.borderLinetype", nodeLineTypeName,
        nodeLineTypeClass, nodeLineTypeIntClass, flatInterpolator);
        String nodeSizeName = "nodeSizeCalculator." + calcName;
        String nodeSizeClass = packageHeader + "GenericNodeSizeCalculator";
        String nodeSizeIntClass = NodeSizeCalculator.class.getName();
        loadCalculator(props, catalog, "node.width", nodeSizeName, nodeSizeClass,
        nodeSizeIntClass, numberInterpolator);
        String nodeSizeName2 = nodeSizeName + "2";
        loadCalculator(props, catalog, "node.height", nodeSizeName2, nodeSizeClass,
        nodeSizeIntClass, numberInterpolator);
        String nodeShapeName = "nodeShapeCalculator." + calcName;
        String nodeShapeClass = packageHeader + "GenericNodeShapeCalculator";
        String nodeShapeIntClass = NodeShapeCalculator.class.getName();
        loadCalculator(props, catalog, "node.shape", nodeShapeName, nodeShapeClass,
        nodeShapeIntClass, flatInterpolator);
        
        String edgeColorName = "edgeColorCalculator." + calcName;
        String edgeColorClass = packageHeader + "GenericEdgeColorCalculator";
        String edgeColorIntClass = EdgeColorCalculator.class.getName();
        loadCalculator(props, catalog, "edge.color", edgeColorName, edgeColorClass,
        edgeColorIntClass, colorInterpolator);
        String edgeLineTypeName = "edgeLineTypeCalculator." + calcName;
        String edgeLineTypeClass = packageHeader + "GenericEdgeLineTypeCalculator";
        String edgeLineTypeIntClass = EdgeLineTypeCalculator.class.getName();
        loadCalculator(props, catalog, "edge.linetype", edgeLineTypeName,
        edgeLineTypeClass, edgeLineTypeIntClass, flatInterpolator);
        String edgeSourceName = "edgeArrowCalculator." + calcName;
        String edgeArrowClass = packageHeader + "GenericEdgeArrowCalculator";
        String edgeArrowIntClass = EdgeArrowCalculator.class.getName();
        loadCalculator(props, catalog, "edge.sourceDecoration", edgeSourceName,
        edgeArrowClass, edgeArrowIntClass, flatInterpolator);
        String edgeTargetName = edgeSourceName + "2";
        loadCalculator(props, catalog, "edge.targetDecoration", edgeTargetName,
        edgeArrowClass, edgeArrowIntClass, flatInterpolator);
        
        //after loading the individual attribute calculators, load the
        //node and edge appearance calculators into the named visual style
        //and the default visual style
        VisualStyle vs = new VisualStyle(calcName);
        VisualStyle defVS = catalog.getVisualStyle("default");
        if (defVS == null) {
            defVS = new VisualStyle("default");
            catalog.addVisualStyle(defVS);
        }
        //these methods install the appearance calculators into the
        //visual style objects
        loadNodeAppearanceCalculator(props, catalog, vs, defVS);
        loadEdgeAppearanceCalculator(props, catalog, vs, defVS);
        loadGlobalAppearanceCalculator(props, catalog, vs, defVS);
        //finally, install the newly created visual style
        catalog.addVisualStyle(vs);
    }
    
    /**
     * Helper method called with the keys specific to one of the visual
     * attributes. First converts those properties to the new format, then
     * loads the calculators into the catalog.
 * @deprecated Use CalculatorIO instead. Will be removed 10/2007 
     */
    private static void loadCalculator(Properties props, CalculatorCatalog catalog,
                                       String oldBaseKey, String newBaseKey,
                                       String className, String intClassName,
                                       String interpolator) {
        Properties newProps =
            getNewProperties(props, oldBaseKey, newBaseKey, className, interpolator);
        if (newProps == null) {return;}
        //hack to get the right name for calculators with '2' after their name
        String name = (newBaseKey.endsWith("2")) ? calcName + "2" : calcName;
        Calculator c = CalculatorFactory.newCalculator(name, newProps, newBaseKey,
                                                       intClassName);
        if (c != null) {
            //make sure we remove any duplicate before trying to add this calculator
            CalculatorIO.removeDuplicate(c, catalog);
            catalog.addCalculator(c);
        }
    }
    
    /**
     * Given a properties object with keys specifying visual mappings in the
     * old format, returns a new properties object specifying the same mapping
     * in the new format.
     *
     * @param props  the properties file
     * @param oldBaseKey  the first part of the old format properties keys
     * @param newBaseKey  the first part of the corresponding new property keys
     * @param className  the name of the calculator class
     * @param interpolator  the name of the interpolator class, used for continuous mappings
     *
     * @return  a Properties object in the new format
     */
    private static Properties getNewProperties(Properties props, String oldBaseKey,
    String newBaseKey, String className, String interpolator) {
        Properties newProps = null;
        //look for the key specifying the controlling attribute
        String controller = props.getProperty(oldBaseKey + ".controller");
        if (controller != null) {
            String header = oldBaseKey + "." + controller;
            //should have a type for this visual mapping
            String type = props.getProperty(header + ".type");
            if (type == null) {
                //handle missing type error
                return null;
            } else if (type.equals("discrete")) {
                newProps = translateDiscrete(props, header, newBaseKey);
            } else if (type.equals("continuous")) {
                newProps = translateContinuous(props, header, newBaseKey);
                newProps.setProperty(newBaseKey + ".mapping.interpolator",interpolator);
            } else {
                //handle unknown type error
                return null;
            }
            //set the class name of the calculator
            newProps.setProperty(newBaseKey + ".class", className);
            //and the controlling attribute
            newProps.setProperty(newBaseKey + ".mapping.controller", controller);
        }
        return newProps;
    }
    
    /**
     * Called when the visual mapping is of type 'discrete'. Creates a key
     * for the discrete type and a key for each of the map definitions.
     *
     * @param props  the properties object in the old format
     * @param header  the header of the old format property keys
     * @param newBaseKey  the header for the new format property keys
     *
     * @return  a properties object in the new format
     */
    private static Properties translateDiscrete(Properties props, String header,
    String newBaseKey) {
        Properties newProps = new Properties();
        //set the mapping type, i.e. discrete
        newProps.setProperty(newBaseKey + ".mapping.type", "DiscreteMapping");
        String mapKey = header + ".map.";
        Enumeration eProps = props.propertyNames();
        //convert all of the keys defining data->visual mappings
        while(eProps.hasMoreElements()) {
            String key = (String)eProps.nextElement();
            if (key.startsWith(mapKey)) {
                String subKey = key.substring(mapKey.length());
                String newKey = newBaseKey + ".mapping.map." + subKey;
                String value = props.getProperty(key);
                newProps.setProperty(newKey, value);
            }
        }
        return newProps;
    }
    
    /**
     * Called when the visual mapping is of type 'continuous'. Creates a key
     * for the continuous type and keys for all the mapping-specific entries.
     *
     * @param props  the properties object in the old format
     * @param header  the header of the old format property keys
     * @param newBaseKey  the header for the new format property keys
     *
     * @return  a properties object in the new format
     */
    private static Properties translateContinuous(Properties props, String header,
    String newBaseKey) {
        Properties newProps = new Properties();
        //save the mapping type, i.e. continuous
        newProps.setProperty(newBaseKey + ".mapping.type", "ContinuousMapping");
        String bvNumKey = header + ".boundaryvalues";
        String bvNumString = props.getProperty(bvNumKey);
        newProps.setProperty(newBaseKey + ".mapping.boundaryvalues", bvNumString);
        int numBV;
        try {
            numBV = Integer.parseInt(bvNumString);
        } catch (NumberFormatException e) {
            //handle bad number error
            return null;
        }
        for (int i=0; i<numBV; i++) {
            String bvBase = header + ".bv" + Integer.toString(i);
            String newBvBase = newBaseKey + ".mapping.bv" + Integer.toString(i);
            
            String dvKey = bvBase + ".domainvalue";
            String dvString = props.getProperty(dvKey);
            newProps.setProperty(newBvBase + ".domainvalue", dvString);
            
            String lString = props.getProperty(bvBase + ".lesser");
            newProps.setProperty(newBvBase + ".lesser", lString);
            String eString = props.getProperty(bvBase + ".equal");
            newProps.setProperty(newBvBase + ".equal", eString);
            String gString = props.getProperty(bvBase + ".greater");
            newProps.setProperty(newBvBase + ".greater", gString);
        }
        
        return newProps;
    }
    
    /**
     * After loading all the individual attribute calculators, creates a node
     * appearance calculator representing all the visual mappings specified in
     * the old format in the props argument. Also installs any entries found
     * in the "default" node appearance calculator, so that they will be
     * automatically activated the next time the user runs Cytoscape.
     *
     * The old visual mappings defintion style did not have entries for node
     * labels. Assuming the user wants node labels, we grab the node label
     * calculator from the default visual style and set the visual style we
     * create here to use that calculator.  This assumes that a suitable
     * default was created when loading the visual styles.
     */
    private static void loadNodeAppearanceCalculator(Properties props,
                                                     CalculatorCatalog catalog,
                                                     VisualStyle vs,
                                                     VisualStyle defVS) {
        NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
        //we'll also store any entries in the default nac
        NodeAppearanceCalculator defNAC = defVS.getNodeAppearanceCalculator();
        LineTypeParser ltParser = new LineTypeParser();
        
        //set the node label calculator
        nac.setCalculator( defNAC.getCalculator(VizMapUI.NODE_LABEL) );
       
        NodeAppearance defAppr = nac.getDefaultAppearance();
        NodeAppearance defNACAppr = defNAC.getDefaultAppearance();
        String defaultNodeFillString = props.getProperty("node.fillColor.default");
        if (defaultNodeFillString != null) {
            defAppr.setFillColor( Misc.parseRGBText(defaultNodeFillString) );
            defNACAppr.setFillColor( Misc.parseRGBText(defaultNodeFillString) );
        }
        String defaultBorderColorString = props.getProperty("node.borderColor.default");
        if (defaultBorderColorString != null) {
            defAppr.setBorderColor(Misc.parseRGBText(defaultBorderColorString));
            defNACAppr.setBorderColor(Misc.parseRGBText(defaultBorderColorString));
        }
        String defaultLineTypeString = props.getProperty("node.borderLinetype.default");
        if (defaultLineTypeString != null) {
            defAppr.setBorderLineType( ltParser.parseLineType(defaultLineTypeString) );
            defNACAppr.setBorderLineType( ltParser.parseLineType(defaultLineTypeString) );
        }
        String defaultWidthString = props.getProperty("node.width.default");
        if (defaultWidthString != null) {
            try {
                double d = Double.parseDouble(defaultWidthString);
                defAppr.setWidth(d);
                defNACAppr.setWidth(d);
            } catch (NumberFormatException e) {
            }
        }
        String defaultHeightString = props.getProperty("node.height.default");
        if (defaultHeightString != null) {
            try {
                double d = Double.parseDouble(defaultHeightString);
                defAppr.setHeight(d);
                defNACAppr.setHeight(d);
            } catch (NumberFormatException e) {
            }
        }
        String defaultShapeString = props.getProperty("node.shape.default");
        if (defaultShapeString != null) {
            defAppr.setShape( ShapeNodeRealizer.parseNodeShapeText(defaultShapeString) );
            defNACAppr.setShape( ShapeNodeRealizer.parseNodeShapeText(defaultShapeString) );
        }
        
        //note that null values from the catalog are acceptable for the new nac,
        //but we don't want to trample existing values in the default nac
        Calculator nfc = catalog.getCalculator(VizMapUI.NODE_COLOR,calcName);
        if (nfc != null) {
            nac.setCalculator(nfc);
            defNAC.setCalculator(nfc);
        }
        Calculator nbc = catalog.getCalculator(VizMapUI.NODE_BORDER_COLOR,calcName + "2");
        if (nbc != null) {
            nac.setCalculator(nbc);
            defNAC.setCalculator(nbc);
        }
        Calculator nlt = catalog.getCalculator(VizMapUI.NODE_LINETYPE,calcName);
        if (nlt != null) {
            nac.setCalculator(nlt);
            defNAC.setCalculator(nlt);
        }
        Calculator nw = catalog.getCalculator(VizMapUI.NODE_WIDTH,calcName);
        if (nw != null) {
            nac.setCalculator(nw);
            defNAC.setCalculator(nw);
        }
        Calculator nh = catalog.getCalculator(VizMapUI.NODE_HEIGHT, calcName + "2");
        if (nh != null) {
            nac.setCalculator(nh);
            defNAC.setCalculator(nh);
        }
        Calculator nsh = catalog.getCalculator(VizMapUI.NODE_SHAPE,calcName);
        if (nsh != null) {
            nac.setCalculator(nsh);
            defNAC.setCalculator(nsh);
        }
    }
    
    /**
     * After loading all the individual attribute calculators, creates an edge
     * appearance calculator representing all the visual mappings specified in
     * the old format in the props argument. Also installs any entries found
     * in the "default" edge appearance calculator, so that they will be
     * automatically activated the next time the user runs Cytoscape.
     */
    private static void loadEdgeAppearanceCalculator(Properties props,
                                                     CalculatorCatalog catalog,
                                                     VisualStyle vs,
                                                     VisualStyle defVS) {
        EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();
        //we'll also store any entries in the default eac
        EdgeAppearanceCalculator defEAC = defVS.getEdgeAppearanceCalculator();
        LineTypeParser ltParser = new LineTypeParser();
        ArrowParser arrowParser = new ArrowParser();

	EdgeAppearance defAppr = eac.getDefaultAppearance();
	EdgeAppearance defEACAppr = defEAC.getDefaultAppearance();
        
        String defaultColorString = props.getProperty("edge.color.default");
        if (defaultColorString != null) {
            defAppr.setColor( Misc.parseRGBText(defaultColorString) );
            defEACAppr.setColor( Misc.parseRGBText(defaultColorString) );
        }
        String defaultLineTypeString = props.getProperty("edge.linetype.default");
        if (defaultLineTypeString != null) {
            defAppr.setLineType(ltParser.parseLineType(defaultLineTypeString));
            defEACAppr.setLineType(ltParser.parseLineType(defaultLineTypeString));
        }
        String defaultSourceString = props.getProperty("edge.sourceDecoration.default");
        if (defaultSourceString != null) {
            defAppr.setSourceArrow( arrowParser.parseArrow(defaultSourceString) );
            defEACAppr.setSourceArrow( arrowParser.parseArrow(defaultSourceString) );
        }
        String defaultTargetString = props.getProperty("edge.targetDecoration.default");
        if (defaultTargetString != null) {
            defAppr.setTargetArrow( arrowParser.parseArrow(defaultTargetString) );
            defEACAppr.setTargetArrow( arrowParser.parseArrow(defaultTargetString) );
        }
        
        Calculator ecc = catalog.getCalculator(VizMapUI.EDGE_COLOR, calcName);
        if (ecc != null) {
            eac.setCalculator(ecc);
            defEAC.setCalculator(ecc);
        }
        Calculator elt = catalog.getCalculator(VizMapUI.EDGE_LINETYPE,calcName);
        if (elt != null) {
            eac.setCalculator(elt);
            defEAC.setCalculator(elt);
        }
        Calculator esa = catalog.getCalculator(VizMapUI.EDGE_SRCARROW,calcName);
        if (esa != null) {
            eac.setCalculator(esa);
            defEAC.setCalculator(esa);
        }
        Calculator eta = catalog.getCalculator(VizMapUI.EDGE_TGTARROW,calcName + "2");
        if (eta != null) {
            eac.setCalculator(eta);
            defEAC.setCalculator(eta);
        }
    }
    
    private static void loadGlobalAppearanceCalculator(Properties props,
                                                       CalculatorCatalog catalog,
                                                       VisualStyle vs,
                                                       VisualStyle defVS) {
        GlobalAppearanceCalculator gac = vs.getGlobalAppearanceCalculator();
        //we'll also store any entries in the default gac
        GlobalAppearanceCalculator defGAC = defVS.getGlobalAppearanceCalculator();
        
        String backString = props.getProperty("background.color");
        if (backString != null) {
            gac.setDefaultBackgroundColor( Misc.parseRGBText(backString) );
            defGAC.setDefaultBackgroundColor( Misc.parseRGBText(backString) );
        }
        String selString = props.getProperty("node.selectedColor");
        if (selString != null) {
            gac.setDefaultSloppySelectionColor( Misc.parseRGBText(selString) );
            defGAC.setDefaultSloppySelectionColor( Misc.parseRGBText(selString) );
        }
    }
}

    

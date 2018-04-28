
/*
  File: CalculatorIO.java 
  
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
// $Revision: 8663 $
// $Date: 2006-11-01 17:39:57 -0800 (Wed, 01 Nov 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import cytoscape.visual.calculators.*;
import cytoscape.visual.ui.VizMapUI;
//----------------------------------------------------------------------------
/**
 * This class defines static methods for reading calculator definitions from
 * a properties object and installing them into a CalculatorCatalog, and for
 * constructing a properties object that describes all the calculators in a
 * CalculatorCatalog.
 */
public class CalculatorIO {
    
    // old labels no longer used, but that we need to account for
    public static final String nodeColorBaseKey = "nodeColorCalculator";
    public static final String nodeSizeBaseKey = "nodeSizeCalculator";
    public static final String edgeArrowBaseKey = "edgeArrowCalculator";

    // new labels to replace old labels 
    private static final String nodeFillColorBaseKey = 
    	CalculatorFactory.getPropertyLabel(VizMapUI.NODE_COLOR); 
    private static final String nodeBorderColorBaseKey =  
    	CalculatorFactory.getPropertyLabel(VizMapUI.NODE_BORDER_COLOR);
    private static final String nodeWidthBaseKey =  
    	CalculatorFactory.getPropertyLabel(VizMapUI.NODE_WIDTH);
    private static final String nodeHeightBaseKey =  
    	CalculatorFactory.getPropertyLabel(VizMapUI.NODE_HEIGHT);
    private static final String nodeUniformSizeBaseKey =  
    	CalculatorFactory.getPropertyLabel(VizMapUI.NODE_SIZE);
    private static final String edgeSourceArrowBaseKey =  
    	CalculatorFactory.getPropertyLabel(VizMapUI.EDGE_SRCARROW);
    private static final String edgeTargetArrowBaseKey =  
    	CalculatorFactory.getPropertyLabel(VizMapUI.EDGE_TGTARROW);
   
    // appearance labels
    private static final String nodeAppearanceBaseKey = "nodeAppearanceCalculator";
    private static final String edgeAppearanceBaseKey = "edgeAppearanceCalculator";
    private static final String globalAppearanceBaseKey = "globalAppearanceCalculator";
    
    
    /**
     * Writes the contents of a CalculatorCatalog to the specified file as a
     * properties file.
     * This method sorts the lines of text produced by the store method of
     * Properties, so that the properties descriptions of the calculators are
     * reasonably human-readable.
     */
    public static void storeCatalog(CalculatorCatalog catalog, File outFile) {
        try {
            //construct the header comment for the file
            String lineSep = System.getProperty("line.separator");
            StringBuffer header = new StringBuffer();
            header.append("This file specifies visual mappings for Cytoscape");
            header.append(" and has been automatically generated.").append(lineSep);
            header.append("# WARNING: any changes you make to this file while");
            header.append(" Cytoscape is running may be overwritten.").append(lineSep);
            header.append("# Any changes may make these visual mappings unreadable.");
            header.append(lineSep);
            header.append("# Please make sure you know what you are doing before");
            header.append(" modifying this file by hand.").append(lineSep);
            
            //writer that writes final version to file;
            //created now so that we crash early if the file is unwritable
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            
            //get a Properties description of the catalog
            Properties props = getProperties(catalog);
            //and dump it to a buffer of bytes
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            props.store( buffer, header.toString() );
            
            //convert the bytes to a String we can read from
            String theData = buffer.toString();
            BufferedReader reader = new BufferedReader(new StringReader(theData));
            //read all the lines and store them in a container object
            //store the header lines separately so they don't get sorted
            List headerLines = new ArrayList();
            List lines = new ArrayList();
            String oneLine = reader.readLine();
            while (oneLine != null) {
                if (oneLine.startsWith("#")) {
                    headerLines.add(oneLine);
                } else {
                    lines.add(oneLine);
                }
                oneLine = reader.readLine();
            }
            
            //now sort all the non-header lines
            Collections.sort(lines);
            //and write to file
            for (Iterator li = headerLines.iterator(); li.hasNext(); ) {
                String theLine = (String)li.next();
                writer.write(theLine, 0, theLine.length());
                writer.newLine();
            }
            for (Iterator li = lines.iterator(); li.hasNext(); ) {
                String theLine = (String)li.next();
                writer.write(theLine, 0, theLine.length());
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
    /**
     * Given a CalculatorCatalog, assembles a Properties object representing all of the
     * calculators contained in the catalog. The resulting Properties object, if passed
     * to the loadCalculators method, would reconstruct all the calculators. This method
     * works by getting each set of calculators from the catalog and calling the
     * getProperties method on each calculator with the proper header for the property key.
     */
    public static Properties getProperties(CalculatorCatalog catalog) {
        Properties newProps = new Properties();
       
       	for ( Calculator c : catalog.getCalculators() ) 
		newProps.putAll( c.getProperties() );
		
        //visual styles
        Set visualStyleNames = catalog.getVisualStyleNames();
        for (Iterator i = visualStyleNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            VisualStyle vs = catalog.getVisualStyle(name);
            try {
                Properties styleProps = new Properties();

                NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
                String nacBaseKey = nodeAppearanceBaseKey + "." + name;
                Properties nacProps = nac.getProperties(nacBaseKey);
                styleProps.putAll(nacProps);

                EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();
                String eacBaseKey = edgeAppearanceBaseKey + "." + name;
                Properties eacProps = eac.getProperties(eacBaseKey);
                styleProps.putAll(eacProps);

                GlobalAppearanceCalculator gac = vs.getGlobalAppearanceCalculator();
                String gacBaseKey = globalAppearanceBaseKey + "." + name;
                Properties gacProps = gac.getProperties(gacBaseKey);
                styleProps.putAll(gacProps);

                //now that we've constructed all the properties for this visual
                //style without Exceptions, store in the global properties object
                newProps.putAll(styleProps);
            } catch (Exception e) {
                String s = "Exception while saving visual style " + name;
                System.err.println(s);
                System.err.println(e.getMessage());
            }
        }
        
        return newProps;
    }
    
    /**
     * Equivalent to loadCalculators(props, catalog, true);
     */
    public static void loadCalculators(Properties props, CalculatorCatalog catalog) {
        loadCalculators(props, catalog, true);
    }
    
    /**
     * Loads calculators from their description in a Properties object into a
     * supplied CalculatorCatalog object. This method searches the Properties
     * object for known keys identifying calculators, then delegates to other
     * methods that use the preprocessed properties to construct valid
     * calculator objects.
     * For any calculator defined by the Properties, it is possible for the
     * catalog to already hold a calculator with the same name and interface
     * type (especially if this method has already been run with the same
     * Properties object and catalog). If the overWrite argument is true,
     * this method will remove any such duplicate calculator before adding
     * the new one to prevent duplicate name exceptions. If overwrite is
     * false, this method will get a unique name from the catalog and change
     * the name of the installed calculator as needed.
     */
    public static void loadCalculators(Properties props, CalculatorCatalog catalog,
                                       boolean overWrite) {
        // The supplied Properties object may contain any kinds of properties.
        // We look for keys that start with a name we recognize, identifying a
        // particular type of calculator. The second field of the key should
        // then be an identifying name. For example,
        //     nodeFillColorCalculator.mySpecialCalculator.{anything else}
        //
	// We begin by creating a map of calculator types (nodeFillColorCalculator)
	// to a map of names (mySpecialCalculator) to properties.  Note that this
	// will create maps for _any_ "calculator" that appears, even if it isn't
	// a Calculator.  This is OK, because the CalculatorFactory won't create
	// anything that isn't actually a Calculator.
        //
        // Note that we need separate constructs for each type of calculator,
        // because calculators of different types are allowed to share the same name.
        //
	Map<String,Map<String,Properties>> calcNames = new HashMap<String,Map<String,Properties>>();

        //use the propertyNames() method instead of the generic Map iterator,
        //because the former method recognizes layered properties objects.
        //see the Properties javadoc for details
        for (Enumeration eI = props.propertyNames(); eI.hasMoreElements(); ) {
            String key = (String)eI.nextElement();

            // handle legacy names
	    // In these cases the old calculator base key was applicable to more
	    // than one calculator. In the new system it's one key to one calculator, 
	    // so we simply apply the old calculator to all of the new types of
	    // calculators that the old calculator mapped to.
            if (key.startsWith(nodeColorBaseKey + ".")) {
	    	key = updateLegacyKey(key,props,nodeColorBaseKey,nodeFillColorBaseKey,
			"cytoscape.visual.calculators.GenericNodeFillColorCalculator");
      		storeKey(key, props, calcNames);
	    	key = updateLegacyKey(key,props,nodeFillColorBaseKey,nodeBorderColorBaseKey,
			"cytoscape.visual.calculators.GenericNodeBorderColorCalculator");
		storeKey(key, props, calcNames);

            } else if (key.startsWith(nodeSizeBaseKey + ".")) {
	    	key = updateLegacyKey(key,props,nodeSizeBaseKey,nodeUniformSizeBaseKey,
			"cytoscape.visual.calculators.GenericNodeUniformSizeCalculator");
                storeKey(key, props, calcNames);
	    	key = updateLegacyKey(key,props,nodeUniformSizeBaseKey,nodeWidthBaseKey,
			"cytoscape.visual.calculators.GenericNodeWidthCalculator");
		storeKey(key, props, calcNames);
	    	key = updateLegacyKey(key,props,nodeWidthBaseKey,nodeHeightBaseKey,
			"cytoscape.visual.calculators.GenericNodeHeightCalculator");
		storeKey(key, props, calcNames);

            } else if (key.startsWith(edgeArrowBaseKey + ".")) {
	    	key = updateLegacyKey(key,props,edgeArrowBaseKey,edgeSourceArrowBaseKey,
			"cytoscape.visual.calculators.GenericEdgeSourceArrowCalculator");
		storeKey(key, props, calcNames);
	    	key = updateLegacyKey(key,props,edgeSourceArrowBaseKey,edgeTargetArrowBaseKey,
			"cytoscape.visual.calculators.GenericEdgeTargetArrowCalculator");
		storeKey(key, props, calcNames);

	    // handle normal names
	    // This is how all "modern" properties files should work.
            } else {
                storeKey(key, props, calcNames);
            }
        }
        
        // Now that we have all the properties in groups, we pass each Map of
        // names and Properties objects to a helper function that creates a
        // calculator for each entry and stores the calculators in the catalog.
        // Before storing the calculator, we either remove any existing calculator
        // with the same name, or get a unique name from the calculator, depending
        // on the value of the overWrite argument.
	for ( String calcTypeKey : calcNames.keySet() )
		handleCalculators(calcNames.get(calcTypeKey),catalog,overWrite,calcTypeKey);

        //Map structure to hold visual styles that we build here
        Map visualStyles = new HashMap();

        //now that all the individual calculators are loaded, load the
        //Node/Edge/Global appearance calculators

	Map<String,Properties> nacNames = calcNames.get(nodeAppearanceBaseKey);
	for ( String name : nacNames.keySet() ) {
            Properties nacProps = nacNames.get(name);
            String apprType = nodeAppearanceBaseKey + "." + name;
            NodeAppearanceCalculator nac =
                new NodeAppearanceCalculator(name, nacProps, apprType, catalog);
            //store in the matching visual style, creating as needed
            VisualStyle vs = (VisualStyle)visualStyles.get(name);
            if (vs == null) {
                vs = new VisualStyle(name);
                visualStyles.put(name, vs);
            }
            vs.setNodeAppearanceCalculator(nac);
        }

	Map<String,Properties> eacNames = calcNames.get(edgeAppearanceBaseKey);
	for ( String name : eacNames.keySet() ) {
            Properties eacProps = eacNames.get(name);
            String apprType = edgeAppearanceBaseKey + "." + name;
            EdgeAppearanceCalculator eac =
                new EdgeAppearanceCalculator(name, eacProps, apprType, catalog);
            //store in the matching visual style, creating as needed
            VisualStyle vs = (VisualStyle)visualStyles.get(name);
            if (vs == null) {
                vs = new VisualStyle(name);
                visualStyles.put(name, vs);
            }
            vs.setEdgeAppearanceCalculator(eac);
        }

	Map<String,Properties> gacNames = calcNames.get(globalAppearanceBaseKey);
	for ( String name : gacNames.keySet() ) {
            Properties gacProps = gacNames.get(name);
            String apprType = globalAppearanceBaseKey + "." + name;
            GlobalAppearanceCalculator gac =
                new GlobalAppearanceCalculator(name, gacProps, apprType, catalog);
            //store in the matching visual style, creating as needed
            VisualStyle vs = (VisualStyle)visualStyles.get(name);
            if (vs == null) {
                vs = new VisualStyle(name);
                visualStyles.put(name, vs);
            }
            vs.setGlobalAppearanceCalculator(gac);
        }
        
        //now store the visual styles in the catalog
        for (Iterator si = visualStyles.values().iterator(); si.hasNext(); ) {
            VisualStyle vs = (VisualStyle)si.next();
            catalog.addVisualStyle(vs);
        }
    }

    
    /**
     * The supplied Map m maps calculator types to a map of names to Properties 
     * objects that hold all the properties entries associated with that name. 
     * Given a new key, this method first extract the calculator type, and then
     * finds the name to prop map for that calc type.  It then extracts the name 
     * field from the key, gets the matching Properties object from the name to
     * props map (creating a new map entry if needed) and stores the (key, value) 
     * property pair in that Properties object).
     */
    private static void storeKey(String key, Properties props, Map<String,Map<String,Properties>> calcNames) {

    	// get the name->props map for the given calculator type, as 
	// defined by the key.
        String calcTypeKey = extractCalcType(key);
	if ( calcTypeKey == null ) {
		System.err.println("couldn't parse calcTypeKey from '" + key +"'");
		return;
	}
	Map<String,Properties> name2props = calcNames.get(calcTypeKey);
	// if the props don't yet exist, create them
	if ( name2props == null ) {
		name2props = new HashMap<String,Properties>();
		calcNames.put(calcTypeKey,name2props);
	}

	// now the get the props from the name->props map 
        String name = extractName(key);
        if (name != null) {
	    // calcProps contains all of the properties for this calculator,
	    // e.g. the mappings, the controller, etc.
            Properties calcProps = name2props.get(name);
	    //create a new entry for this name if it doesn't already exist
            if (calcProps == null) {
                calcProps = new Properties();
                name2props.put(name, calcProps);
            }
            calcProps.setProperty( key, props.getProperty(key) );
        }//should report parse errors if we can't get a name
    }

    
    /**
     * Given the key of a property entry, extract the second field (i.e., between the
     * first and second period) and return it.
     */
    private static String extractName(String key) {
        if (key == null) {return null;}
        //find index of first period character
        int dot1 = key.indexOf(".");
        //return null if not found, or found at end of string
        if (dot1 == -1 || dot1 >= key.length()-1) {return null;}
        //find the second period character
        int dot2 = key.indexOf(".", dot1+1);
        if (dot2 == -1) {return null;}//return null if not found
        //return substring between the periods
        return key.substring(dot1+1, dot2);
    }

    /**
     * Extracts the base key from the string. 
     */
    private static String extractCalcType(String key) {
        if (key == null) {return null;}
        //find index of first period character
        int dot1 = key.indexOf(".");
        //return null if not found, or found at end of string
        if (dot1 == -1 || dot1 >= key.length()-1) {return null;}
        //return substring between the periods
        return key.substring(0, dot1);
    }
    
    /**
     * Construct and store Calculators. Ensures that there will be no name
     * collision by either removing an existing duplicate or renaming the
     * new calculator as needed.
     */
    private static void handleCalculators(Map<String,Properties> nameMap, CalculatorCatalog catalog,
                    boolean overWrite, String calcTypeKey) {

	// for each calculator name
	for ( String name : nameMap.keySet() ) {
	    // get the properties object that contains all info for
	    // that particular calculator
            Properties calcProps = nameMap.get(name);
            String keyString = calcTypeKey + "." + name;
	    // create a calculator based on the calculator name and type
            Calculator c = CalculatorFactory.newCalculator(name, calcProps, keyString);
            if (c!= null) {

		// remove any existing calculator of same name and type
                if (overWrite) {
 		   catalog.removeCalculator(c);	

		// otherwise ensure a unique name
                } else {
                    renameAsNeeded(c, catalog);
                }

                catalog.addCalculator(c);
	    } 
        }
    }
    
    /**
     * Given a Calculator of a given type and a CalculatorCatalog, checks
     * for an existing catalog with the same name and type. If one exists,
     * gets a new unique name from the catalog and applied it to the
     * calculator argument.
     */
    public static void renameAsNeeded(Calculator c, CalculatorCatalog catalog) {
        String name = c.toString();
	String newName = catalog.checkCalculatorName(c.getType(),name);
        if (!newName.equals(name)) {c.setName(newName);}
    }

    /**
     * Used for updating calculator names from old style to new style. 
     * Only used in a few cases where the old and new don't align.
     */
    private static String updateLegacyKey(String key, Properties props, String oldKey, 
                                          String newKey, String newClass ) {
    	String value = props.getProperty(key);
	key = key.replace(oldKey,newKey);
	if ( key.endsWith(".class") )
		props.setProperty(key,newClass);
	else
		props.setProperty(key,value);

	return key;
    }

// agony

    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String dirHeader = "cytoscape.visual.calculators.";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeColorClassName = "NodeColorCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String nodeLineTypeBaseKey = "nodeLineTypeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeLineTypeClassName = "NodeLineTypeCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String nodeShapeBaseKey = "nodeShapeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeShapeClassName = "NodeShapeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeSizeClassName = "NodeSizeCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String nodeLabelBaseKey = "nodeLabelCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeLabelClassName = "NodeLabelCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String nodeToolTipBaseKey = "nodeToolTipCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeToolTipClassName = "NodeToolTipCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String nodeFontFaceBaseKey = "nodeFontFaceCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeFontFaceClassName = "NodeFontFaceCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String nodeFontSizeBaseKey = "nodeFontSizeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String nodeFontSizeClassName = "NodeFontSizeCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String edgeColorBaseKey = "edgeColorCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeColorClassName = "EdgeColorCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String edgeLineTypeBaseKey = "edgeLineTypeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeLineTypeClassName = "EdgeLineTypeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeArrowClassName = "EdgeArrowCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String edgeLabelBaseKey = "edgeLabelCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeLabelClassName = "EdgeLabelCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String edgeToolTipBaseKey = "edgeToolTipCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeToolTipClassName = "EdgeToolTipCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String edgeFontFaceBaseKey = "edgeFontFaceCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeFontFaceClassName = "EdgeFontFaceCalculator";
    /** @deprecated Use Calculator.getPropertyLabel(). Will be removed 10/2007 */
    public static final String edgeFontSizeBaseKey = "edgeFontSizeCalculator";
    /** @deprecated Implement this yourself. Will be removed 10/2007 */
    public static final String edgeFontSizeClassName = "EdgeFontSizeCalculator";

    /** @deprecated WTF? Use CalculatorCatalog.removeCalculator() Will be removed 10/2007 */
    public static void removeDuplicate(Calculator c, CalculatorCatalog catalog){ 
 		   catalog.removeCalculator(c);
    }	
}


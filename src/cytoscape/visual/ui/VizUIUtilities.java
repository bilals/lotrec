
/*
  File: VizUIUtilities.java 
  
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

//--------------------------------------------------------------------------------
// $Revision: 8633 $
// $Date: 2006-10-30 16:21:52 -0800 (Mon, 30 Oct 2006) $
// $Author: mes $
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;
//--------------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;

import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
//--------------------------------------------------------------------------------
/**
 * This class provides utility functions for the UI package. Most of these
 * methods involve converting a generic operation on a byte constant specifying
 * the visual attribute type to the corresponding operation specific to the
 * particular attribute.
 *
 * These methods are package-protected because the UI is designed to make sure
 * that the arguments passed to these methods are appropriate.
 */
public class VizUIUtilities {
    
    /**
     * Gets the current default value for the visual attribute
     * specified by the second argument in the visual style specified by the
     * first argument. Returns null if the first argument is null.
     */
     // TODOOOO this is stupid and should exist somewhere else
    static Object getDefault(VisualStyle style, byte type) {
        if (style == null) {return null;}
        Object defaultObj = null;
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();

	NodeAppearance na = nodeCalc.getDefaultAppearance();
	EdgeAppearance ea = edgeCalc.getDefaultAppearance();

	//System.out.println("na " + na.getDescription("asdf"));
	//System.out.println("ea " + ea.getDescription("asdf"));
	//System.out.println("type " + type);
	defaultObj = na.get(type);
	if ( defaultObj == null ) 
		defaultObj = ea.get(type);

        return defaultObj;
    }
    
    /**
     * Sets the default value for the visual attribute specified
     * by the second argument in the visual style specified by the first
     * argument. The third argument is the new default value. Returns
     * null if the first or third argument is null.
     */
    static void setDefault(VisualStyle style, byte type, Object c) {
        if (style == null || c == null) {return;}
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();


	NodeAppearance na = nodeCalc.getDefaultAppearance();
	EdgeAppearance ea = edgeCalc.getDefaultAppearance();

	// types aren't redundant, so this is ok.
	na.set(type,c);
	ea.set(type,c);

	nodeCalc.setDefaultAppearance(na);
	edgeCalc.setDefaultAppearance(ea);
    }
    
    /**
     * Gets the current calculator for the visual attribute specified by
     * the second argument in the visual style specified by the first argument.
     * This may be null if no calculator is currently specified.
     * Returns null if the first argument is null.
     */
    static Calculator getCurrentCalculator(VisualStyle style, byte type) {
        if (style == null) {return null;}
        Calculator currentCalculator = null;
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();

	currentCalculator = nodeCalc.getCalculator(type);
	if ( currentCalculator == null ) 
		currentCalculator = edgeCalc.getCalculator(type);

        return currentCalculator;
    }
    
    /**
     * Sets the current calculator for the visual attribute specified by
     * the second argument in the visual style specified by the first argument.
     * The third argument is the new calculator and may be null. This method
     * does nothing if the first argument specifying the visual style is null.
     */
    static void setCurrentCalculator(VisualStyle style, byte type, Calculator c) {
        if (style == null) {return;}
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();

	if ( c == null ) {
		nodeCalc.removeCalculator(type);
		edgeCalc.removeCalculator(type);
	} else {
		nodeCalc.setCalculator(c);
		edgeCalc.setCalculator(c);
	}
    }
}


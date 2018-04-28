
/*
  File: CalculatorFactory.java 
  
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


package cytoscape.visual.calculators;

import java.util.Properties;
import java.lang.reflect.Constructor;
import cytoscape.visual.ui.VizMapUI;
import cytoscape.visual.mappings.ObjectMapping;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.lang.reflect.Modifier;
import java.util.jar.JarEntry;
import java.net.JarURLConnection;
import java.util.jar.JarFile;

/**
 * This class provides static factory methods for constructing instances
 * of Calculators as specified by arguments and static methods for getting
 * names and labels based on calculator type.
 */
public class CalculatorFactory {
   
    // the list of dummy calculators that this application knows about
    private static List<Calculator> calcs = new ArrayList<Calculator>();

    // create the list of dummy calcs
    static {
	calcs.add( new GenericEdgeColorCalculator() );
	calcs.add( new GenericEdgeFontFaceCalculator() );
	calcs.add( new GenericEdgeFontSizeCalculator() );
	calcs.add( new GenericEdgeLabelCalculator() );
	calcs.add( new GenericEdgeLineTypeCalculator() );
	calcs.add( new GenericEdgeSourceArrowCalculator() );
	calcs.add( new GenericEdgeTargetArrowCalculator() );
	calcs.add( new GenericEdgeToolTipCalculator() );
	calcs.add( new GenericEdgeLabelColorCalculator() );
	calcs.add( new GenericNodeBorderColorCalculator() );
	calcs.add( new GenericNodeFillColorCalculator() );
	calcs.add( new GenericNodeFontFaceCalculator() );
	calcs.add( new GenericNodeFontSizeCalculator() );
	calcs.add( new GenericNodeHeightCalculator() );
	calcs.add( new GenericNodeLabelCalculator() );
	calcs.add( new GenericNodeLabelColorCalculator() );
	calcs.add( new GenericNodeLabelPositionCalculator() );
	calcs.add( new GenericNodeLineTypeCalculator() );
	calcs.add( new GenericNodeShapeCalculator() );
	calcs.add( new GenericNodeToolTipCalculator() );
	calcs.add( new GenericNodeUniformSizeCalculator() );
	calcs.add( new GenericNodeWidthCalculator() );
    }

    /** @deprecated Use othe newCalculator - we don't need the base interface class name any
        more. Will be removed 10/2007. */
    public static Calculator newCalculator(String name, Properties calcProps, 
                                           String baseKey, String intClassName) {
    	return newCalculator(name,calcProps,baseKey);
    }

    /**
     * Attempt to construct an instance of Calculator as defined by
     * the supplied arguments.
     * It searches for a key-value pair identifying the name of the class to
     * create, verifies that that class exists, implements the desired interface,
     * and has an appropriate constructor, and calls that constructor with the
     * appropriate arguments.
     */
    public static Calculator newCalculator(String name, Properties calcProps, String baseKey) {
       
        //get the class object for the real implementation object specified by
        //these properties
        String className = calcProps.getProperty(baseKey + ".class");
        if (className == null) {
            return null; // this is normal, so don't shout about it
	}

        String errString = "CalculatorFactory: error processing baseKey " + baseKey;

        Class realClass = null;
        try {
            realClass = Class.forName(className);
        } catch (Exception e) {
            System.err.println(errString + " class not found: " + className);
            return null;
        }
	
        //get the class object representing the top-level interface Calculator
        Class calcClass = Calculator.class;
        if (!calcClass.isAssignableFrom(realClass)) {
            System.err.println(errString + " requested class " + className
                       + " does not implement the Calculator interface");
            return null;
        }

	// create the constructor for the specified class
        Class[] parameterTypes = {String.class, Properties.class, String.class};
	Constructor constructor = getConstructor(realClass,parameterTypes,className);
	if (constructor == null) {
            System.err.println(errString + " requested constructor for " + className
                      + " could not be created");
            return null;
	}
	
        //try constructing a calculator by calling the found constructor
        Object[] params = {name, calcProps, baseKey};
        Calculator calculator = getCalculator(constructor,params,className);
	if (calculator == null)
            System.err.println(errString + " requested calculator for " + className
                      + " could not be created");
           
        return calculator;
    }


    /**
     * Creates a new default Calculator based on type.
     */
    public static Calculator newDefaultCalculator(byte type, String calcName, ObjectMapping mapper) {

	Calculator calc = null;

	for ( Calculator c : calcs ) {
		if ( c.getType() == type ) {
			Class realClass = c.getClass();	
			Class[] paramTypes = {String.class,ObjectMapping.class};
			Constructor constructor = getConstructor(realClass,paramTypes,c.getPropertyLabel());
			if ( constructor == null )
				return null;
			Object[] params = {calcName,mapper};
        		calc = getCalculator(constructor,params,c.getPropertyLabel());

			return calc;
		}
	}
	return calc;
    }

    /**
     * Returns the type name for calculators of a given type.
     */
    public static String getTypeName(byte type) {
	for ( Calculator c : calcs ) 
		if ( c.getType() == type )
			return c.getTypeName();
	
	return null;
    }

    /**
     * Returns the property label for calculators of a given type.
     */
    public static String getPropertyLabel(byte type) {
	for ( Calculator c : calcs ) 
		if ( c.getType() == type )
			return c.getPropertyLabel();
	
	return null;
    }

    // utility method to create a constructor based on the params
    private static Constructor getConstructor(Class realClass, Class[] parameterTypes, String className) {

	//look for a constructor in this class that takes the right arguments
	Constructor constructor = null;
	try {
	    constructor = realClass.getDeclaredConstructor(parameterTypes);
	} catch (NoSuchMethodException nsme) {
	    String s = "no suitable constructor found in class " + className;
	    System.err.println(s);
	    nsme.printStackTrace();
	    return null;
	} catch (SecurityException se) {//highly unlikely
	    String s = "could not access constructors for class " + className;
	    System.err.println(s);
	    se.printStackTrace();
	    return null;
	}
	return constructor;
    }

    // utility method to create a calculator based on the params
    private static Calculator getCalculator(Constructor constructor, Object[] params, String className) {
	Calculator calculator = null;
	try {
	    calculator = (Calculator) (constructor.newInstance(params));
	} catch (Exception e) {
	    String s = "unable to construct an instance of class " + className;
	    System.err.println(s);
	    e.printStackTrace();
	    return null;
	}
	return calculator;
    }
}


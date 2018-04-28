
/*
  File: AppearanceCalculator.java 
  
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

import java.util.Map;
import java.util.Properties;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.ArrayList;

import giny.model.Node;
import cytoscape.data.CyAttributes;
import cytoscape.visual.LineType;
import cytoscape.visual.Arrow;
import cytoscape.visual.ShapeNodeRealizer;

import cytoscape.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.*;

/**
 * This class calculates the appearance of a Node. It holds a default value
 * and a (possibly null) calculator for each visual attribute.
 */
abstract class AppearanceCalculator implements Cloneable {

  List<Calculator> calcs = new ArrayList<Calculator>();

  protected Appearance tmpDefaultAppearance;

  /**
   * Make shallow copy of this object
   */
  public Object clone() {
    Object copy = null;
    try {
	    copy = super.clone();
    }
    catch (CloneNotSupportedException e) {
	    System.err.println("Error cloning!");
    }
    return copy;
  }
	
  public AppearanceCalculator() {}
  /**
   * Creates a new AppearanceCalculator and immediately customizes it
   * by calling applyProperties with the supplied arguments.
   */
  public AppearanceCalculator(String name, Properties nacProps,
                                  String baseKey, CalculatorCatalog catalog, Appearance appr) {
    tmpDefaultAppearance = appr;
    applyProperties(appr, name, nacProps, baseKey, catalog);
  }
    
  public AppearanceCalculator(AppearanceCalculator toCopy) {
    if (toCopy == null) {return;}

    for ( Calculator c : toCopy.getCalculators() ) {
    	setCalculator( c );
    }

    copyDefaultAppearance( toCopy );  
  }

  public Calculator getCalculator(byte type) {
  	for (Calculator nc : calcs) 
		if ( nc.getType() == type )
			return nc;
	
	return null;
  }

  public List<Calculator> getCalculators() {
  	return calcs;
  }

  public void removeCalculator(byte type) {
  	Calculator toRemove = null;
  	for (Calculator c : calcs) {
		if ( c.getType() == type ) {
			toRemove = c;
			break;
		}
	}

	calcs.remove(toRemove);
  }

  public void setCalculator(Calculator c) {
  	if ( c == null )
		return;
	
	if ( ! isValidCalculator(c) )
		return;

  	Calculator toReplace = null;
  	for (Calculator nc : calcs) 
		if ( nc.getType() == c.getType() ) {
			toReplace = nc; 
			break;
		}

	if ( toReplace != null )
		calcs.remove( toReplace );

	calcs.add( c );
  }

  protected String getDescription(String name,Appearance defaultAppr) {
    String lineSep = System.getProperty("line.separator");
    StringBuffer sb = new StringBuffer();
    sb.append(name + ":" + lineSep);
    sb.append(defaultAppr.getDescription("default")).append(lineSep);
    for (Calculator c: calcs)
        sb.append(c.toString()).append(lineSep);

    return sb.toString();
  }

  protected void applyProperties(Appearance appr, String name, Properties nacProps, String baseKey,
                              CalculatorCatalog catalog) {
    String value = null;

    appr.applyDefaultProperties(nacProps,baseKey);

    for ( Byte b : catalog.getCalculatorTypes() ) {
    	for ( Calculator c : catalog.getCalculators(b.byteValue()) ) {
        	value = nacProps.getProperty(baseKey + "." + c.getPropertyLabel() );
        	Calculator newCalc = catalog.getCalculator( c.getType(), value ); 
		setCalculator( newCalc );
        }
    }
  }
    
  protected Properties getProperties(Appearance appr,String baseKey) {
    String key = null;
    String value = null;
    Properties newProps = appr.getDefaultProperties(baseKey); 

    for ( Calculator c : calcs ) {
    	// do actual
    	key = baseKey + "." + c.getPropertyLabel();
	value = c.toString();
	newProps.setProperty(key,value);
    }

    return newProps;
  }

  protected abstract void copyDefaultAppearance(AppearanceCalculator toCopy);
  protected abstract boolean isValidCalculator(Calculator c);


}


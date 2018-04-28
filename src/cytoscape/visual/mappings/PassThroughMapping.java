
/*
  File: PassThroughMapping.java 
  
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
// $Revision: 8633 $
// $Date: 2006-10-30 16:21:52 -0800 (Mon, 30 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.*;
import java.awt.Color;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.GridLayout;
import cytoscape.CyNetwork;
import cytoscape.visual.parsers.ValueParser;
//----------------------------------------------------------------------------
/**
 * Defines a mapping from a bundle of data attributes to a visual attribute.
 * The returned value is simply the value of one of the data attributes,
 * defined by the controlling attribute name. This value is type-checked
 * against the expected range class; null is returned instead if the
 * data value is of the wrong type.
 */
public class PassThroughMapping implements ObjectMapping {
        
    Class rangeClass; //the class of values held by this mapping
    String attrName;  //the name of the controlling data attribute

    /** Standard constructor for compatibility with new calculator creation in
     *	the UI.
     * 
     *	@param	defaultObj	Default object - provided only to establish
     *				mapping's range class.
     *	@param	mapType		unused.
     */
    public PassThroughMapping(Object defaultObj, byte mapType) {
	this(defaultObj);
    }
    
    public PassThroughMapping(Object defaultObj) {
        this.rangeClass = defaultObj.getClass();
    }
    public PassThroughMapping(Object defaultObj, String attrName) {
        this.rangeClass = defaultObj.getClass();
        setControllingAttributeName(attrName, null, false);
    }
    
    public Object clone() {
	PassThroughMapping miniMe;
	try {
	    miniMe = (PassThroughMapping) super.clone();
	}
	catch (CloneNotSupportedException e) {
	    System.err.println("Critical error in PassThroughMapping - was not cloneable");
	    e.printStackTrace();
	    return null;
	}
	miniMe.attrName = new String(attrName);
	// don't need to explicitly clone rangeClass since cloned calculator
        //has same type as original.
	return miniMe;
    }

    public Class getRangeClass() {return rangeClass;}

    public Class[] getAcceptedDataClasses() {
	Class[] ret = {Object.class};
	return ret;
    }
    
    public String getControllingAttributeName() {return attrName;}
    public void setControllingAttributeName(String attrName, CyNetwork network,
                                            boolean preserveMapping) {
        this.attrName = attrName;
    }

    /**
     * Empty implementation because PassThroughMapping has no UI.
     */
    public void addChangeListener(ChangeListener l) {
    }

    /**
     * Empty implementation because PassThroughMapping has no UI.
     */
    public void removeChangeListener(ChangeListener l) {
    }
        
    public JPanel getUI(JDialog parent, CyNetwork network) {
        //construct a UI to view/edit this mapping; only needs to view/set
        //the controlling attribute name
	JPanel p = new JPanel();
	JLabel l1 = new JLabel("This is a passthrough mapping;");
	JLabel l2 = new JLabel("it has no user-editable parameters.");
	p.setLayout(new GridLayout(2,1));
	p.add(l1);
	p.add(l2);
	return p;
    }
    
    public Object calculateRangeValue(Map attrBundle) {
        if (attrBundle == null || attrName == null) {return null;}
        //extract the data value for our controlling attribute
        Object attrValue = attrBundle.get(attrName);

        if (attrValue == null) {return null;}
        //if the attrValue is a List, search for an object in the List
        //of a type matching our return type, and return it
	if(attrValue instanceof List) {
	    Iterator attrValueIt = ((List)attrValue).iterator();
	    while(attrValueIt.hasNext()) {
		Object attrSubValue = attrValueIt.next();
		if (rangeClass.isInstance(attrSubValue)) {
		    return attrSubValue;
		}
	    }
	}
        //OK, try returning the attrValue itself
        if (rangeClass.isInstance(attrValue)) {return attrValue;}
        
        //if range class is String, try converting value to String
        if (rangeClass.equals(String.class)) {
            String stringConvert = attrValue.toString();
            //sanity check to prevent ridiculously long labels
            if (stringConvert.length() > 20) {
                stringConvert = stringConvert.substring(0, 20) + "...";
            }
            return stringConvert;
        }
        //attribute value is just no good; return null
        return null;
    }
    
    /**
     * Customize this object by applying mapping defintions described by the
     * supplied Properties argument.
     */
    public void applyProperties(Properties props, String baseKey, ValueParser parser) {
        String contKey = baseKey + ".controller";
        String contValue = props.getProperty(contKey);
        if (contValue != null) {setControllingAttributeName(contValue, null, false);}
    }

    /**
     * Return a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     */
    public Properties getProperties(String baseKey) {
        Properties newProps = new Properties();
        String contKey = baseKey + ".controller";
        String contValue = getControllingAttributeName();
	if ( contKey != null && contValue != null )
        	newProps.setProperty(contKey, contValue);
        return newProps;
    }

    public JPanel getLegend(String visualAttr,byte b) {
        JPanel p = new JPanel();
	p.setBackground(Color.white);
	p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JLabel l = new JLabel(visualAttr + " is displayed as " + attrName);
        p.add(l);
        return p;
    }

}


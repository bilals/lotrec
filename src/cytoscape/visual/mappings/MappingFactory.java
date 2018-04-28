
/*
  File: MappingFactory.java 
  
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
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.Properties;
import java.lang.reflect.Constructor;

import cytoscape.visual.parsers.ValueParser;
//----------------------------------------------------------------------------
/**
 * This class provides a static factory method for constructing an instance
 * of ObjectMapping as specified by a Properties object and other arguments.
 *
 * Since there are currently only a few types of mappings known, it's easiest
 * to simply check each case and construct the right mapping without going
 * through a dynamic class-discovery instantiation process. If the number of
 * mappings ever gets out of hand, we can always switch to a dynamic algorithm.
 */
public class MappingFactory {
    
    /**
     * Attempt to construct an instance of ObjectMapping as defined by
     * the supplied arguments. Checks the value of a recognized key in
     * the Properties argument against a list of known Mappings. If found,
     * constructs the Mapping object and then customizes it by calling its
     * applyProperties method.
     */
    public static ObjectMapping newMapping(Properties props, String baseKey,
                                           ValueParser parser, Object defObj,
                                           byte mapType) {
        String typeName = props.getProperty(baseKey + ".type");
        if (typeName == null) {
            System.err.println("MappingFactory: no Mapping class specified in properties");
            return null;
        } else if (typeName.equals("DiscreteMapping")) {
            DiscreteMapping m = new DiscreteMapping(defObj, mapType);
            m.applyProperties(props, baseKey, parser);
            return m;
        } else if (typeName.equals("ContinuousMapping")) {
            ContinuousMapping m = new ContinuousMapping(defObj, mapType);
            m.applyProperties(props, baseKey, parser);
            return m;
        } else if (typeName.equals("PassThroughMapping")) {
            PassThroughMapping m = new PassThroughMapping(defObj, mapType);
            m.applyProperties(props, baseKey, parser);
            return m;
        } else {
            System.err.println("MappingFactory: unknown Mapping type: " + typeName);
            return null;
        }
    }
    
    /**
     * Gets a description of the supplied ObjectMapping as properties.
     * This method calls the getProperties() method of the ObjectMapping
     * argument and then adds a property to identify the mapping class,
     * in a form recognized by the newMapping method.
     */
    public static Properties getProperties(ObjectMapping m, String baseKey) {
        if (m == null) {return null;}
        Properties newProps = m.getProperties(baseKey);
        if (m instanceof DiscreteMapping) {
            newProps.setProperty(baseKey + ".type", "DiscreteMapping");
        } else if (m instanceof ContinuousMapping) {
            newProps.setProperty(baseKey + ".type", "ContinuousMapping");
        } else if (m instanceof PassThroughMapping) {
            newProps.setProperty(baseKey + ".type", "PassThroughMapping");
        } else {//highly unexpected type
            String c = m.getClass().getName();
            System.err.println("MappingFactory: unknown Mapping type: " + c);
            return null;
        }
        return newProps;
    }
}



/*
  File: DiscreteMappingReader.java 
  
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
// $Revision: 8215 $
// $Date: 2006-09-15 15:32:05 -0700 (Fri, 15 Sep 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import cytoscape.visual.parsers.ValueParser;
import cytoscape.visual.mappings.MappingUtil;

import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Reads in DiscreteMapping Properties.
 *
 * Unit Test for this class exists in:
 * cytoscape.visual.mappings.discrete.unitTests.TestDiscreteMappingReader.
 */
public class DiscreteMappingReader {
    private String controllingAttribute;
    private TreeMap map = new TreeMap();

    /**
     * Constructor.
     * @param props Properties Object.
     * @param baseKey Base Property Key.
     * @param parser ValueParser Object.
     */
    public DiscreteMappingReader(Properties props, String baseKey,
            ValueParser parser) {
        readProperties(props, baseKey, parser);
    }

    /**
     * Gets Controlling Attribute Name.
     * @return Controlling Attribute Name.
     */
    public String getControllingAttributeName() {
        return controllingAttribute;
    }

    /**
     * Gets the Discrete Map.
     * @return TreeMap Object.
     */
    public TreeMap getMap() {
        return map;
    }

    /**
     * Read in Settings from the Properties Object.
     */
    private void readProperties(Properties props, String baseKey,
            ValueParser parser) {
        String contKey = baseKey + ".controller";
        controllingAttribute = props.getProperty(contKey);

        String contTypeKey = baseKey + ".controllerType";
        String attrTypeString = props.getProperty(contTypeKey);
	byte attrType = -1; // UNDEFINED defaults to string
	if ( attrTypeString != null )
		attrType = new Byte(attrTypeString).byteValue();

        String mapKey = baseKey + ".map.";
        Enumeration eProps = props.propertyNames();
        while (eProps.hasMoreElements()) {
            String key = (String) eProps.nextElement();
            if (key.startsWith(mapKey)) {
                String value = props.getProperty(key);
                Object domainVal = MappingUtil.parseObjectType(key.substring(mapKey.length()),attrType); 
                Object parsedVal = parser.parseStringValue(value);
                map.put(domainVal, parsedVal);
            }
        }
    }
}

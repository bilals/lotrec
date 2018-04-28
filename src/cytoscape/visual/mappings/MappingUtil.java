
/*
  File: MappingUtil.java 
  
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


package cytoscape.visual.mappings;

import java.lang.*;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * Provides simple utility methods for the Mapping classes.
 */
public class MappingUtil {

	/**
	 * This method determines the type of the attr used and
	 * returns a string representation of it.
	 */
	public static String getAttributeTypeString(String base, String attr) {
	 	Byte B = new Byte(getAttributeType(base,attr));	

		return B.toString();
	}

	/** 
	 * This method determines the type of the attribute used
	 * for the mapping. The purpose is to support discrete attrs
	 * aside from strings.
	 */ 
	public static byte getAttributeType(String base, String attr) {

		byte b = CyAttributes.TYPE_UNDEFINED;
		if ( base.startsWith("node") )
			b = Cytoscape.getNodeAttributes().getType(attr);
		else if ( base.startsWith("edge") )
			b = Cytoscape.getEdgeAttributes().getType(attr);

		return b;
	}

	/**
	 * This method returns an object of the specified type
	 * based on the string read from the props file.
	 */
	public static Object parseObjectType(String key,byte attrType) {
		if ( attrType == CyAttributes.TYPE_INTEGER )
			return new Integer(key);
	       	else if ( attrType == CyAttributes.TYPE_FLOATING )
       			return new Double(key);
       		else if ( attrType == CyAttributes.TYPE_BOOLEAN )
       			return new Boolean(key);
       		// assume string
       		else
       			return key;
	}
}

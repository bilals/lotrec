
/*
  File: DiscreteRangeCalculator.java 
  
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
package cytoscape.visual.mappings.discrete;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Range Calculator for the Discrete Mapper.
 */
public class DiscreteRangeCalculator {
    private TreeMap map;
    private String attrName;

    /**
     * Constructor.
     * @param map Discrete Map.
     * @param attrName Controlling Attribute Name.
     */
    public DiscreteRangeCalculator(TreeMap map, String attrName) {
        this.map = map;
        this.attrName = attrName;
    }

    /**
     * Calculates Range Value.
     * @param attrBundle Attribute Bundle.
     * @return Object.
     */
    public Object calculateRangeValue(Map attrBundle) {
        if (attrBundle == null || attrName == null) {
            return null;
        }
        //extract the data value for our controlling attribute name
        Object attrValue = attrBundle.get(attrName);

        if (attrValue == null) {
            return null;
        }
        //from here we have to catch ClassCastExceptions that will be
        //thrown if the data value is not of a type comparable to the keys
        //in this SortedMap
        try {
            //if the attrValue is a List, search for an object in the List
            //that maps to a non-null value, and return the matching value
            if (attrValue instanceof List) {
                Iterator attrValueIt = ((List) attrValue).iterator();
                while (attrValueIt.hasNext()) {
                    Object attrSubValue = attrValueIt.next();
                    if (map.get(attrSubValue) != null) {
                        return map.get(attrSubValue);
                    }
                }
                //if not found, return null
                return null;
            } else {
                //OK, try the attrValue itself as a key
                return map.get(attrValue); //returns null if not found
            }
        } catch (ClassCastException e) {
            return null;
        }
    }
}
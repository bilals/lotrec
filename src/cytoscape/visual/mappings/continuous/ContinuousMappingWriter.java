
/*
  File: ContinuousMappingWriter.java 
  
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
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.InterpolatorFactory;
import cytoscape.visual.parsers.ObjectToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Writes out ContinuousMapping Properties.
 */
public class ContinuousMappingWriter {
    private Properties newProps;

    /**
     * Constructor.
     * @param points ArrayList of ContinuousMappintPoints.
     * @param baseKey Base Key String.
     * @param attrName Controlling Attribute String.
     * @param interp Interpolator Object.
     */
    public ContinuousMappingWriter(ArrayList points, String baseKey,
            String attrName, Interpolator interp) {
        newProps = new Properties();
        loadProperties(points, baseKey, attrName, interp);
    }

    /**
     * Gets Newly Defined Properties Object.
     * @return Properties Object.
     */
    public Properties getProperties() {
        return newProps;
    }

    /**
     * Return a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     */
    private void loadProperties(ArrayList points, String baseKey,
            String contAttrName, Interpolator interp) {

        // save the controlling attribute name
        String contAttrKey = baseKey + ".controller";

        if (contAttrName != null) {
            newProps.setProperty(contAttrKey, contAttrName);
        }

        // save the interpolator
        String intKey = baseKey + ".interpolator";
        String intName = InterpolatorFactory.getName(interp);
        newProps.setProperty(intKey, intName);

        //  save the number of boundary values
        String bvNumKey = baseKey + ".boundaryvalues";
        int numBV = points.size();
        String numString = Integer.toString(numBV);
        newProps.setProperty(bvNumKey, numString);

        //  save each of the boundary values
        int count = 0;
        for (Iterator si = points.iterator(); si.hasNext(); count++) {
            String bvBase = baseKey + ".bv" + count;

            //  save the domain value
            String bvKey = bvBase + ".domainvalue";
            ContinuousMappingPoint cmp = (ContinuousMappingPoint) si.next();
            Double dVal = new Double(cmp.getValue());
            String dValString = dVal.toString();
            newProps.setProperty(bvKey, dValString);

            //  save the fields of the brv object
            BoundaryRangeValues brv = (BoundaryRangeValues) cmp.getRange();
            String lKey = bvBase + ".lesser";
            String lString = ObjectToString.getStringValue(brv.lesserValue);
            newProps.setProperty(lKey, lString);
            String eKey = bvBase + ".equal";
            String eString = ObjectToString.getStringValue(brv.equalValue);
            newProps.setProperty(eKey, eString);
            String gKey = bvBase + ".greater";
            String gString = ObjectToString.getStringValue(brv.greaterValue);
            newProps.setProperty(gKey, gString);
        }
    }
}

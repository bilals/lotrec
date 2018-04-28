
/*
  File: ContinuousMappingReader.java 
  
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
import cytoscape.visual.parsers.ValueParser;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Reads in ContinuousMapping Properties.
 */
public class ContinuousMappingReader {
    private String controllingAttributeName;
    private Interpolator interpolator;
    private ArrayList points;
    private ValueParser parser;

    /**
     * Constructor.
     * @param props Properties Object.
     * @param baseKey Base Key.
     * @param parser Value Parser.
     */
    public ContinuousMappingReader(Properties props, String baseKey,
            ValueParser parser) {
        this.parser = parser;
        points = new ArrayList();
        parseProperties(props, baseKey);
    }

    /**
     * Gets Controlling Attribute Name.
     * @return Controlling Attribute Name.
     */
    public String getControllingAttributeName() {
        return controllingAttributeName;
    }

    /**
     * Gets Interpolator Object.
     * @return Interpolator Object.
     */
    public Interpolator getInterpolator() {
        return interpolator;
    }

    /**
     * Gets ArrayList of all Data Points.
     * @return ArrayList of ContinuousMappingPoint objects.
     */
    public ArrayList getPoints() {
        return points;
    }

    /**
     * Parses all Properties into internal data structures.
     */
    private void parseProperties(Properties props, String baseKey) {

        //  Get Controller Property
        String contKey = baseKey + ".controller";
        String contValue = props.getProperty(contKey);
        if (contValue != null) {
            this.controllingAttributeName = contValue;
        }

        //  Get Interpolator Property
        String intKey = baseKey + ".interpolator";
        String intValue = props.getProperty(intKey);
        if (intValue != null) {
            this.interpolator = InterpolatorFactory.newInterpolator(intValue);
        }

        //  Get Boundary Value Properties
        String bvNumKey = baseKey + ".boundaryvalues";
        String bvNumString = props.getProperty(bvNumKey);
        if (bvNumString != null) {
            try {
                int numBV = Integer.parseInt(bvNumString);
                getBoundaryValues(numBV, baseKey, props);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing attributeMap properties:");
                System.err.println("    Expected number value for key: "
                        + bvNumString);
            }
        }
    }

    /**
     * Gets all BoundaryValue Properties.
     */
    private void getBoundaryValues(int numBV, String baseKey,
            Properties props) {
        for (int i = 0; i < numBV; i++) {
            String bvBase = baseKey + ".bv" + Integer.toString(i);
            String dvKey = bvBase + ".domainvalue";
            String dvString = props.getProperty(dvKey);
            if (dvString != null) {
                try {
                    Double dVal = Double.valueOf(dvString);
                    getLesserEqualGreater(bvBase, props, dVal);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing attributeMap properties:");
                    System.err.println("    expected number value for key: "
                            + dvKey);
                }
            }
        }
    }

    /**
     * Gets Less/Equals/Greater than BoundaryValues.
     */
    private void getLesserEqualGreater(String bvBase, Properties props,
            Double dVal) {
        BoundaryRangeValues bv = new BoundaryRangeValues();
        String lKey = bvBase + ".lesser";
        String lString = props.getProperty(lKey);
        Object lValue = parser.parseStringValue(lString);
        bv.lesserValue = lValue;
        String eKey = bvBase + ".equal";
        String eString = props.getProperty(eKey);
        Object eValue = parser.parseStringValue(eString);
        bv.equalValue = eValue;
        String gKey = bvBase + ".greater";
        String gString = props.getProperty(gKey);
        Object gValue = parser.parseStringValue(gString);
        bv.greaterValue = gValue;

        ContinuousMappingPoint cmp = new ContinuousMappingPoint
                (dVal.doubleValue(), bv);
        points.add(cmp);
    }
}
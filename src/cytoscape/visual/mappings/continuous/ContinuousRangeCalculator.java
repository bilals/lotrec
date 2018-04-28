
/*
  File: ContinuousRangeCalculator.java 
  
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

import java.util.ArrayList;
import java.util.Map;

/**
 * Calculates the Range for Continuous Mappers.
 */
public class ContinuousRangeCalculator {
    private ArrayList points;
    private Interpolator interpolator;
    private Map attrBundle;

    /**
     * Constructor.
     * @param points ArrayList of ContinuousMappingPoints.
     * @param interpolator Interpolator Object.
     * @param attrBundle Attribute Bundle.
     */
    public ContinuousRangeCalculator(ArrayList points,
            Interpolator interpolator, Map attrBundle) {
        this.points = points;
        this.interpolator = interpolator;
        this.attrBundle = attrBundle;
    }

    /**
     * Calculates Range Value.
     * @param attrName Attribute Name.
     * @return Object.
     */
    public Object calculateRangeValue(String attrName) {
        if (attrBundle == null || attrName == null) {
            return null;
        }
        if (points.size() == 0) {
            return null;
        }
        Object attrValue = attrBundle.get(attrName);
        if (!(attrValue instanceof Number)) {
            return null;
        }
        Object object = getRangeValue((Number) attrValue);
        return object;
    }

    private Object getRangeValue(Number domainValue) {
        ContinuousMappingPoint firstPoint =
                (ContinuousMappingPoint) points.get(0);
        Number minDomain = new Double(firstPoint.getValue());

        //  if given domain value is smaller than any in our list,
        //  return the range value for the smallest domain value we have.
        int firstCmp = compareValues(domainValue, minDomain);
        if (firstCmp <= 0) {
            BoundaryRangeValues bv = firstPoint.getRange();
            if (firstCmp < 0) {
                return bv.lesserValue;
            } else {
                return bv.equalValue;
            }
        }

        //  if given domain value is larger than any in our Vector,
        //  return the range value for the largest domain value we have.
        ContinuousMappingPoint lastPoint = (ContinuousMappingPoint)
                points.get(points.size() - 1);
        Number maxDomain = new Double(lastPoint.getValue());
        if (compareValues(domainValue, maxDomain) > 0) {
            BoundaryRangeValues bv = lastPoint.getRange();
            return bv.greaterValue;
        }

        //  OK, it's somewhere in the middle, so find the boundaries and
        //  pass to our interpolator function. First check for a null
        //  interpolator function
        if (this.interpolator == null) {
            return null;
        }

        // Note that the list of Points is sorted.
        // Also, the case of the inValue equalling the smallest key was
        // checked above.
        ContinuousMappingPoint currentPoint;
        int index = 0;

        for (index = 0; index < points.size(); index++) {
            currentPoint = (ContinuousMappingPoint) points.get(index);
            Double currentValue = new Double(currentPoint.getValue());
            int cmpValue = compareValues(domainValue, currentValue);
            if (cmpValue == 0) {
                BoundaryRangeValues bv = currentPoint.getRange();
                return bv.equalValue;
            } else if (cmpValue < 0) {
                break;
            }
        }
        Object object = getRangeValue(index, domainValue);
        return object;
    }

    /**
     *  This is tricky. The desired domain value is greater than
     *  lowerDomain and less than upperDomain. Therefore, we want
     *  the "greater" field of the lower boundary value (because the
     *  desired domain value is greater) and the "lesser" field of
     *  the upper boundary value (semantic difficulties).
     */
    private Object getRangeValue(int index, Number domainValue) {

        //  Get Lower Domain and Range
        ContinuousMappingPoint lowerBound = (ContinuousMappingPoint)
                points.get(index - 1);
        Number lowerDomain = new Double(lowerBound.getValue());
        BoundaryRangeValues lv = lowerBound.getRange();
        Object lowerRange = lv.greaterValue;

        //  Get Upper Domain and Range
        ContinuousMappingPoint upperBound = (ContinuousMappingPoint)
                points.get(index);
        Number upperDomain = new Double(upperBound.getValue());
        BoundaryRangeValues gv = upperBound.getRange();
        Object upperRange = gv.lesserValue;
        return interpolator.getRangeValue(lowerDomain, lowerRange,
                upperDomain, upperRange, domainValue);
    }

    /**
     * Helper function to compare Number objects. This is needed because Java
     * doesn't allow comparing, for example, Integer objects to Double objects.
     */
    private int compareValues(Number probe, Number target) {
        double d1 = probe.doubleValue();
        double d2 = target.doubleValue();
        if (d1 < d2) {
            return -1;
        } else if (d1 > d2) {
            return 1;
        } else {
            return 0;
        }
    }
}

/*
  File: InterpolatorFactory.java 
  
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
//----------------------------------------------------------------------------
/**
 * Provides static factory methods for constructing known interpolators from
 * a recognized name, for example from a properties object.
 */
public class InterpolatorFactory {
    
    /**
     * Attempt to construct one of the standard interpolators. The argument
     * should be the simple class name of a known interpolator (i.e., no
     * package information).
     * 
     */
    public static Interpolator newInterpolator(String typeName) {
        if (typeName == null) {
            String s = "InterpolatorFactory: no Interpolator class specified";
            System.err.println(s);
            return null;
        } else if (typeName.equals("LinearNumberToColorInterpolator")) {
            return new LinearNumberToColorInterpolator();
        } else if (typeName.equals("LinearNumberToNumberInterpolator")) {
            return new LinearNumberToNumberInterpolator();
        } else if (typeName.equals("FlatInterpolator")) {
            return new FlatInterpolator();
        } else {
            String s = "InterpolatorFactory: unknown Interpolator type: " + typeName;
            System.err.println(s);
            return null;
        }
    }

    /**
     * Given an Interpolator, returns an identifying name as recognized
     * by the newInterpolator method. null will be returned if the argument
     * is null or of an unrecognized class type.
     */
    public static String getName(Interpolator fInt) {
        if (fInt == null) {
            return null;
        } else if (fInt instanceof LinearNumberToColorInterpolator) {
            return new String("LinearNumberToColorInterpolator");
        } else if (fInt instanceof LinearNumberToNumberInterpolator) {
            return new String("LinearNumberToNumberInterpolator");
        } else if (fInt instanceof FlatInterpolator) {
            return new String("FlatInterpolator");
        } else {
            String c = fInt.getClass().getName();
            System.err.println("Unknown Interpolator type: " + c);
            return null;
        }
    }
}

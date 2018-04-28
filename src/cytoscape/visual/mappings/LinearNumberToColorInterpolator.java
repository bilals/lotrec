
/*
  File: LinearNumberToColorInterpolator.java 
  
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

//LinearNumberToColorInterpolator.java



package cytoscape.visual.mappings;

import java.awt.Color;

/**
 * The class provides a linear interpolation between color values. The
 * (red,green,blue,alpha) values of the returned color are linearly
 * interpolated from the associated values of the lower and upper colors,
 * according the the fractional distance frac from the lower value.
 *
 * If either object argument is not a Color, null is returned.
 */
public class LinearNumberToColorInterpolator extends LinearNumberInterpolator {

    public LinearNumberToColorInterpolator() {}

    public Object getRangeValue(double frac, Object lowerRange,
				Object upperRange) {
	if ( !(lowerRange instanceof Color) ) {return null;}
	if ( !(upperRange instanceof Color) ) {return null;}

	Color lowerColor = (Color)lowerRange;
	Color upperColor = (Color)upperRange;

	double red = lowerColor.getRed()
	    + frac*( upperColor.getRed() - lowerColor.getRed() );
	double green = lowerColor.getGreen()
	    + frac*( upperColor.getGreen() - lowerColor.getGreen() );
	double blue = lowerColor.getBlue()
	    + frac*( upperColor.getBlue() - lowerColor.getBlue() );
	double alpha = lowerColor.getAlpha()
	    + frac*( upperColor.getAlpha() - lowerColor.getAlpha() );

	return new Color( (int)Math.round(red),(int)Math.round(green),
			  (int)Math.round(blue),(int)Math.round(alpha) );
    }
}



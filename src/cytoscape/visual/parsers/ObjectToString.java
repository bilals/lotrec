
/*
  File: ObjectToString.java 
  
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
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;

import cytoscape.visual.LineType;
import cytoscape.visual.Arrow;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.LabelPosition;

import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * This class contains a method that does the reverse of the various parsing
 * classes in this package, i.e. turns a Object back into a String representation.
 * Most cases either use the corresponding methods in cytoscape.util.Misc or
 * use the default toString() method of the object.
 */
public class ObjectToString {
    
    /**
     * Constructs and returns a String representation of the given Object.
     */
    public static String getStringValue(Object o) {
        if (o instanceof Color) {
            return Misc.getRGBText((Color)o);
        } else if (o instanceof LineType) {
            //return Misc.getLineTypeText((LineType)o);
            return o.toString();
        } else if (o instanceof Byte) {
            //return Misc.getNodeShapeText( ((Byte)o).byteValue() );
            return ShapeNodeRealizer.getNodeShapeText( ((Byte)o).byteValue() );
        } else if (o instanceof Arrow) {
            //return Misc.getArrowText((Arrow)o);
            return o.toString();
        } else if (o instanceof Font) {
            return getFontStringValue((Font)o);
        } else if (o instanceof Number) {
            //just trust the default String representation for numbers
            return o.toString();
        } else if (o instanceof LabelPosition) {
	    return ((LabelPosition)o).shortString();
        } else {
            //default: use the toString() method
            return o.toString();
        }
    }
    
    private static String getFontStringValue(Font f) {
        String name = f.getName();
        int style = f.getStyle();
        String styleString = "plain";
        if (style == Font.BOLD) {
            styleString = "bold";
        } else if (style == Font.ITALIC) {
            styleString = "italic";
        } else if ( style == (Font.BOLD|Font.ITALIC) ) {
            styleString = "bold|italic";
        }
        int size = f.getSize();
        String sizeString = Integer.toString(size);
        
        return name + "," + styleString + "," + sizeString;
    }
}


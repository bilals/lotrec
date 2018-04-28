
/*
  File: FontParser.java 
  
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
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import java.awt.Font;
import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a Font object.
 */
public class FontParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseFont(value);
    }
    public Font parseFont(String value) {
        //this algorithm could be moved into the Misc class with the
        //other parsing methods
        if (value == null) {return null;}
        //find index of first comma character
        int comma1 = value.indexOf(",");
        //return null if not found, or found at beginning or end of string
        if (comma1 < 1 || comma1 >= value.length()-1) {return null;}
        //find the second comma character
        int comma2 = value.indexOf(",", comma1+1);
        //return null if not found, or found immediately after the first
        //comma, or at end of string
        if (comma2 == -1 || comma2 == comma1+1 ||
        comma2 >= value.length()-1) {return null;}
        
        //extract the fields
        String name = value.substring(0,comma1);
        String typeString = value.substring(comma1+1,comma2);
        String sizeString = value.substring(comma2+1,value.length());
        //parse the strings
        int type = Font.PLAIN;
        if (typeString.equalsIgnoreCase("bold")) {
            type = Font.BOLD;
        } else if (typeString.equalsIgnoreCase("italic")) {
            type = Font.ITALIC;
        } else if (typeString.equalsIgnoreCase("bold|italic")) {
            type = Font.BOLD|Font.ITALIC;
        } else if (typeString.equalsIgnoreCase("italic|bold")) {
            type = Font.ITALIC|Font.BOLD;//presumably the same as above
        }
        int size = 0;
        try {
            size = Integer.parseInt(sizeString);
        } catch (NumberFormatException e) {
            return null;
        }
        Font f = new Font(name, type, size);
        return f;
    }
}


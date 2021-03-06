
/*
  File: NodeShapeParser.java 
  
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
import cytoscape.visual.ShapeNodeRealizer;
//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles shape, which is represented by a byte
 * identifier. The return value here is a Byte object wrapping the
 * primitive byte identifier.
 */
public class NodeShapeParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseNodeShape(value);
    }
    public Byte parseNodeShape(String value) {
        return ShapeNodeRealizer.parseNodeShapeTextIntoByte(value);
    }

  public static boolean isValidShape(byte shape) {
    if(shape == ShapeNodeRealizer.RECT){return true;}
    if(shape == ShapeNodeRealizer.ROUND_RECT){return true;}
    if(shape == ShapeNodeRealizer.RECT_3D){return true;}
    if(shape == ShapeNodeRealizer.TRAPEZOID){return true;}
    if(shape == ShapeNodeRealizer.TRAPEZOID_2){return true;}
    if(shape == ShapeNodeRealizer.TRIANGLE){return true;}
    if(shape == ShapeNodeRealizer.PARALLELOGRAM){return true;}
    if(shape == ShapeNodeRealizer.DIAMOND){return true;}
    if(shape == ShapeNodeRealizer.ELLIPSE){return true;}
    if(shape == ShapeNodeRealizer.HEXAGON){return true;}
    if(shape == ShapeNodeRealizer.OCTAGON){return true;}
        
    return false;
  }
}

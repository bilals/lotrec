
/*
  File: ShapeNodeRealizer.java 
  
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
package cytoscape.visual;
//----------------------------------------------------------------------------
import giny.view.NodeView;
//----------------------------------------------------------------------------
/**
 * This class is a replacement for the yFiles ShapeNodeRealizer class.
 * It defines byte constants specifying shape types.
 */
public class ShapeNodeRealizer {
    
    public static final byte RECT = (byte)0;
    public static final byte ROUND_RECT = (byte)1;
    public static final byte RECT_3D = (byte)2;
    public static final byte TRAPEZOID = (byte)3;
    public static final byte TRAPEZOID_2 = (byte)4;
    public static final byte TRIANGLE = (byte)5;
    public static final byte PARALLELOGRAM = (byte)6;
    public static final byte DIAMOND = (byte)7;
    public static final byte ELLIPSE = (byte)8;
    public static final byte HEXAGON = (byte)9;
    public static final byte OCTAGON = (byte)10;
    
    public static Byte parseNodeShapeTextIntoByte(String text) {
        return new Byte(parseNodeShapeText(text));
    }
    
    public static byte parseNodeShapeText(String text) {
        String nstext = text.trim();
        nstext = nstext.replaceAll("_",""); // ditch all underscores
        
        if(nstext.equalsIgnoreCase("rect")) {
            return ShapeNodeRealizer.RECT;
        } else if(nstext.equalsIgnoreCase("roundrect")) {
            return ShapeNodeRealizer.ROUND_RECT;
        } else if(nstext.equalsIgnoreCase("rect3d")) {
            return ShapeNodeRealizer.RECT_3D;
        } else if(nstext.equalsIgnoreCase("trapezoid")) {
            return ShapeNodeRealizer.TRAPEZOID;
        } else if(nstext.equalsIgnoreCase("trapezoid2")) {
            return ShapeNodeRealizer.TRAPEZOID_2;
        } else if(nstext.equalsIgnoreCase("triangle")) {
            return ShapeNodeRealizer.TRIANGLE;
        } else if(nstext.equalsIgnoreCase("parallelogram")) {
            return ShapeNodeRealizer.PARALLELOGRAM;
        } else if(nstext.equalsIgnoreCase("diamond")) {
            return ShapeNodeRealizer.DIAMOND;
        } else if(nstext.equalsIgnoreCase("ellipse") || nstext.equalsIgnoreCase("circle")) {
            return ShapeNodeRealizer.ELLIPSE;
        } else if(nstext.equalsIgnoreCase("hexagon")) {
            return ShapeNodeRealizer.HEXAGON;
        } else if(nstext.equalsIgnoreCase("octagon")) {
            return ShapeNodeRealizer.OCTAGON;
        } else {
            return ShapeNodeRealizer.RECT;
        }
    }
    
    public static String getNodeShapeText(byte shape) {
        if(shape == RECT) return "rect";
        if(shape == ROUND_RECT) return "roundrect";
        if(shape == RECT_3D) return "rect3d";
        if(shape == TRAPEZOID) return "trapezoid";
        if(shape == TRAPEZOID_2) return "trapezoid2";
        if(shape == TRIANGLE) return "triangle";
        if(shape == PARALLELOGRAM) return "parallelogram";
        if(shape == DIAMOND) return "diamond";
        if(shape == ELLIPSE) return "ellipse";
        if(shape == HEXAGON) return "hexagon";
        if(shape == OCTAGON) return "octagon";
        
        return "rect";
    }
    
    public static int getGinyShape(byte byteShape) {
        if (byteShape == TRIANGLE) {
            return NodeView.TRIANGLE;
        } else if (byteShape == PARALLELOGRAM) {
            return NodeView.PARALELLOGRAM;
        } else if (byteShape == DIAMOND) {
            return NodeView.DIAMOND;
        } else if (byteShape == ELLIPSE) {
            return NodeView. ELLIPSE;
        } else if (byteShape == HEXAGON) {
            return NodeView.HEXAGON;
        } else if (byteShape == OCTAGON) {
            return NodeView.OCTAGON;
        } else if (byteShape == ROUND_RECT ) {
          return NodeView.ROUNDED_RECTANGLE;
        } else {//rectangle, or unknown shape
            return NodeView.RECTANGLE;
        }
    }

  public boolean isValidShape(byte shape) {
    if(shape == RECT) return true;
    if(shape == ROUND_RECT) return true;
    if(shape == RECT_3D) return true;
    if(shape == TRAPEZOID) return true;
    if(shape == TRAPEZOID_2) return true;
    if(shape == TRIANGLE) return true;
    if(shape == PARALLELOGRAM) return true;
    if(shape == DIAMOND) return true;
    if(shape == ELLIPSE) return true;
    if(shape == HEXAGON) return true;
    if(shape == OCTAGON) return true;

    return false;
  }
}


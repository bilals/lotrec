
/*
  File: MiscDialog.java 
  
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

// MiscDialog.java:  miscellaneous static utilities
//--------------------------------------------------------------------------------------
// $Revision: 8633 $
// $Date: 2006-10-30 16:21:52 -0800 (Mon, 30 Oct 2006) $
// $Author: mes $
//--------------------------------------------------------------------------------------
//package cytoscape.dialogs;
package cytoscape.visual.ui;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.awt.Color;
import java.awt.Polygon;
import java.util.*;
import java.net.URL;

import javax.swing.*;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.ShapeNodeRealizer;

/** @deprecated This class has been enhanced and renamed to IconSupport. 
    This will be removed 10/2007. */
public class MiscDialog {

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static HashMap getStringToArrowHashMap(int nodeSize) {
  	return IconSupport.getStringToArrowHashMap(nodeSize);
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static HashMap getArrowToStringHashMap(int nodeSize) {
  	return IconSupport.getArrowToStringHashMap(nodeSize); 
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public  ImageIcon[] getArrowIcons() {
  	return IconSupport.getArrowIcons();
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static HashMap getStringToShapeByteHashMap() {
  	return IconSupport.getStringToShapeByteHashMap(); 
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static HashMap getShapeByteToStringHashMap() {
  	return IconSupport.getShapeByteToStringHashMap();
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static ImageIcon[] getShapeIcons() {
  	return IconSupport.getShapeIcons() ;
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static HashMap getStringToLineTypeHashMap() {
  	return IconSupport.getStringToLineTypeHashMap();
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static HashMap getLineTypeToStringHashMap() {
  	return IconSupport.getLineTypeToStringHashMap() ;
  }

  /** @deprecated Use IconSupport instead. This will be removed 10/2007. */
  public static ImageIcon[] getLineTypeIcons() {
  	return IconSupport.getLineTypeIcons();
  }
}


/*
  File: Arrow.java 
  
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
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.io.Serializable;
import giny.view.EdgeView;
//----------------------------------------------------------------------------
/**
 * This class is a replacement for the yFiles Arrow class.
 */
public class Arrow implements Serializable {
    
  public static final Arrow NONE = new Arrow("NONE");

  public static final Arrow BLACK_DIAMOND = new Arrow("BLACK_DIAMOND");
  public static final Arrow WHITE_DIAMOND = new Arrow("WHITE_DIAMOND");
  public static final Arrow COLOR_DIAMOND = new Arrow("COLOR_DIAMOND");

  public static final Arrow BLACK_DELTA = new Arrow("BLACK_DELTA");
  public static final Arrow WHITE_DELTA = new Arrow("WHITE_DELTA");
  public static final Arrow COLOR_DELTA = new Arrow("COLOR_DELTA");

  public static final Arrow BLACK_ARROW = new Arrow("BLACK_ARROW");
  public static final Arrow WHITE_ARROW = new Arrow("WHITE_ARROW");
  public static final Arrow COLOR_ARROW = new Arrow("COLOR_ARROW");

  public static final Arrow BLACK_T = new Arrow("BLACK_T");
  public static final Arrow WHITE_T = new Arrow("WHITE_T");
  public static final Arrow COLOR_T = new Arrow("COLOR_T");

  public static final Arrow BLACK_CIRCLE = new Arrow("BLACK_CIRCLE");
  public static final Arrow WHITE_CIRCLE = new Arrow("WHITE_CIRCLE");
  public static final Arrow COLOR_CIRCLE = new Arrow("COLOR_CIRCLE");

  String name;
    
  public Arrow(String name) {this.name = name;}
    
  public int getGinyArrow() {

    if (  name.equals("WHITE_DIAMOND")) {
      return EdgeView.WHITE_DIAMOND;
    } else if (  name.equals("BLACK_DIAMOND")) {
      return EdgeView.BLACK_DIAMOND;
    } else if (  name.equals("COLOR_DIAMOND")) {
      return EdgeView.EDGE_COLOR_DIAMOND;
    } 

    else if (  name.equals("WHITE_DELTA")) {
      return EdgeView.WHITE_DELTA;
    } else if (  name.equals("BLACK_DELTA")) {
      return EdgeView.BLACK_DELTA;
    } else if (  name.equals("COLOR_DELTA")) {
      return EdgeView.EDGE_COLOR_DELTA;
    } 

    else if (  name.equals("WHITE_ARROW")) {
      return EdgeView.WHITE_ARROW;
    } else if (  name.equals("BLACK_ARROW")) {
      return EdgeView.BLACK_ARROW;
    } else if (  name.equals("COLOR_ARROW")) {
      return EdgeView.EDGE_COLOR_ARROW;
    } 

    else if (  name.equals("WHITE_T")) {
      return EdgeView.WHITE_T;
    } else if (  name.equals("BLACK_T")) {
      return EdgeView.BLACK_T;
    } else if (  name.equals("COLOR_T")) {
      return EdgeView.EDGE_COLOR_T;
    } 


    else if (  name.equals("WHITE_CIRCLE")) {
      return EdgeView.WHITE_CIRCLE;
    } else if (  name.equals("BLACK_CIRCLE")) {
      return EdgeView.BLACK_CIRCLE;
    } else if (  name.equals("COLOR_CIRCLE")) {
      return EdgeView.EDGE_COLOR_CIRCLE;
    } 

    else {
      return EdgeView.NO_END;
    }
      
  }
    
  public String getName() {return name;}
  public String toString() {return getName();}
    
  public static Arrow parseArrowText(String text) {
    String arrowtext = text.trim();
        
      if (  arrowtext.equals("WHITE_DIAMOND")) {
      return Arrow.WHITE_DIAMOND;
    } else if (  arrowtext.equals("BLACK_DIAMOND")) {
      return Arrow.BLACK_DIAMOND;
    } else if (  arrowtext.equals("COLOR_DIAMOND")) {
      return Arrow.COLOR_DIAMOND;
    } 

    else if (  arrowtext.equals("WHITE_DELTA")) {
      return Arrow.WHITE_DELTA;
    } else if (  arrowtext.equals("BLACK_DELTA")) {
      return Arrow.BLACK_DELTA;
    } else if (  arrowtext.equals("COLOR_DELTA")) {
      return Arrow.COLOR_DELTA;
    } 

    else if (  arrowtext.equals("WHITE_ARROW")) {
      return Arrow.WHITE_ARROW;
    } else if (  arrowtext.equals("BLACK_ARROW")) {
      return Arrow.BLACK_ARROW;
    } else if (  arrowtext.equals("COLOR_ARROW")) {
      return Arrow.COLOR_ARROW;
    } 

    else if (  arrowtext.equals("WHITE_T")) {
      return Arrow.WHITE_T;
    } else if (  arrowtext.equals("BLACK_T")) {
      return Arrow.BLACK_T;
    } else if (  arrowtext.equals("COLOR_T")) {
      return Arrow.COLOR_T;
    } 


    else if (  arrowtext.equals("WHITE_CIRCLE")) {
      return Arrow.WHITE_CIRCLE;
    } else if (  arrowtext.equals("BLACK_CIRCLE")) {
      return Arrow.BLACK_CIRCLE;
    } else if (  arrowtext.equals("COLOR_CIRCLE")) {
      return Arrow.COLOR_CIRCLE;
    } 

    else {
      return Arrow.NONE;
    }


  
  } // parseArrowText
}


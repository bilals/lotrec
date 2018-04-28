

/*
  File: BypassHelper.java 
  
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

package cytoscape.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import cytoscape.visual.LineType;
import giny.view.NodeView;
import giny.view.Label;
import giny.model.Node;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import cytoscape.visual.parsers.ObjectToString;
import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.LineTypeParser;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.DoubleParser;
import cytoscape.visual.parsers.NodeShapeParser;
import cytoscape.visual.parsers.LabelPositionParser;
import cytoscape.visual.parsers.ValueParser;
import cytoscape.visual.parsers.ParserFactory;
import cytoscape.visual.ui.VizMapUI;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * A package local class to support node and edge vizmap bypass.  This is 
 * simply a common place to put support methods that would otherwise be duplicated.
 * Don't make this public unless you have to!
 */
class BypassHelper { 

  static Map<Class,ValueParser> parsers = new HashMap<Class,ValueParser>();

  /**
   * This method exists because the color parser used will return BLACK if
   * it fails to parse a string whereas we'd prefer null to be returned. This
   * method makes sure that if black is returned, the string really was black.
   */
  static Color getColorBypass(CyAttributes attrs, String id, String attrName) {
  	Color ret = (Color)getBypass(attrs,id,attrName,Color.class);
	if ( ret == null || !ret.equals(Color.black) )
		return ret;

	// now check to see that the attribute actually specifies black,
	// and isn't returning black by default
	String value = attrs.getStringAttribute(id,attrName);
	if ( value == null )
		return null;

	if ( value.equals("0,0,0") )
		return ret;
	else
		return null;
  }

  static Object getBypass(CyAttributes attrs, String id, String attrName, Class type) {
	String value = attrs.getStringAttribute(id,attrName);
	if ( value == null )
		return null;

	ValueParser p = parsers.get(type);
	if ( p == null ) {
		p = ParserFactory.getParser(type);
		parsers.put(type,p);
	}
	if ( p != null )
		return p.parseStringValue(value);
	else
		return null;
  }
}


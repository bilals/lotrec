
/*
  File: ParserFactory.java 
  
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
// $Revision: 8333 $
// $Date: 2006-09-25 19:37:03 -0700 (Mon, 25 Sep 2006) $
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

public class ParserFactory {
	
	public static ValueParser getParser(Object o) {
		return getParser(o.getClass());
	}

	public static ValueParser getParser(Class c) {
		ValueParser parser = null;
		if ( c.isAssignableFrom(String.class) )
			parser = new StringParser();
		else if ( c.isAssignableFrom(Font.class) )
			parser = new FontParser();
		else if ( c.isAssignableFrom(Double.class) )
			parser = new DoubleParser();
		else if ( c.isAssignableFrom(Arrow.class) )
			parser = new ArrowParser();
		else if ( c.isAssignableFrom(LineType.class) )
			parser = new LineTypeParser();
		else if ( c.isAssignableFrom(Byte.class) )
			parser = new NodeShapeParser();
		else if ( c.isAssignableFrom(LabelPosition.class) )
			parser = new LabelPositionParser();
		else if ( c.isAssignableFrom(Color.class) )
			parser = new ColorParser();
		else 
			System.err.println("couldn't construct parser for class: " + c.toString());

		return parser;
	}
}


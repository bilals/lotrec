
/*
  File: GenericEdgeLabelColorCalculator.java 
  
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

package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
import giny.model.Edge;
import java.awt.Color;
import java.util.Map;
import java.util.Properties;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.ui.VizMapUI;

public class GenericEdgeLabelColorCalculator extends EdgeCalculator {

	public byte getType() {
		return VizMapUI.EDGE_LABEL_COLOR;
	}

	public String getPropertyLabel() {
		return "edgeLabelColorCalculator";
	}

	public String getTypeName() {
		return "Edge Label Color";
	}

	GenericEdgeLabelColorCalculator() {
		super();
	}

	public GenericEdgeLabelColorCalculator(String name, ObjectMapping m) {
		super(name, m,Color.class);
	}

	public GenericEdgeLabelColorCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, new ColorParser(), Color.black);
	}
 
	public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
		Color c = (Color) getRangeValue(edge); 
	
		// default has already been set - no need to do anything
		if ( c == null )
			return;

		appr.setLabelColor( c ); 
	}
}

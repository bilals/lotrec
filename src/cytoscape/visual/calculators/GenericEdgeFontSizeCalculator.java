
/*
  File: GenericEdgeFontSizeCalculator.java 
  
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

//--------------------------------------------------------------------------
// $Revision: 8633 $
// $Date: 2006-10-30 16:21:52 -0800 (Mon, 30 Oct 2006) $
// $Author: mes $
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.*;

import giny.model.Edge;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.ui.VizMapUI;
//--------------------------------------------------------------------------
public class GenericEdgeFontSizeCalculator extends EdgeCalculator 
    implements EdgeFontSizeCalculator {

    public byte getType() {
        return VizMapUI.EDGE_FONT_SIZE;
    }

    public String getPropertyLabel() {
        return "edgeFontSizeCalculator";
    }

    public String getTypeName() {
        return "Edge Font Size";
    }
    
    GenericEdgeFontSizeCalculator() {
	super();
    }
    
    public GenericEdgeFontSizeCalculator(String name, ObjectMapping m) {
	super(name, m, Number.class);
    }
    
    public GenericEdgeFontSizeCalculator(String name, Properties props, String baseKey) {
	super(name, props, baseKey, new DoubleParser(), new Double(12));
    }

    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
		Object rangeValue = getRangeValue(edge); 
		
		// default has already been set - no need to do anything
		if (rangeValue != null)
			return;
		
		appr.setFontSize( ((Number) rangeValue).floatValue() );
    }

    public float calculateEdgeFontSize(Edge e, CyNetwork n) {
        EdgeAppearance ea = new EdgeAppearance();
        apply(ea,e,n);
        return ea.getFontSize();
    }
}

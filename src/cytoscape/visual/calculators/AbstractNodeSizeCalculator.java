
/*
  File: AbstractNodeSizeCalculator.java 
  
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
// $Revision: 8550 $
// $Date: 2006-10-23 13:04:30 -0700 (Mon, 23 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;

import giny.model.Node;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;

import cytoscape.visual.NodeAppearance;
import cytoscape.visual.ui.VizMapUI;
//----------------------------------------------------------------------------
abstract class AbstractNodeSizeCalculator extends NodeCalculator {

    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    protected byte sizeType;
    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    protected String propertyLabel;
    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    protected String typename;

    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    public void set( byte b, String p, String n) {
        sizeType = b;
        propertyLabel = p;
        typename = n;
    }


    protected int WIDTH = 1;
    protected int HEIGHT = 2;
    protected int SIZE = 4;

    public abstract byte getType(); 
    public abstract String getPropertyLabel();
    public abstract String getTypeName();

    AbstractNodeSizeCalculator() {
    	super();
    }

    public AbstractNodeSizeCalculator(String name, ObjectMapping m) {
	super(name, m,Number.class);
    }

    public AbstractNodeSizeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new DoubleParser(), new Double(0));
    }
    
    public abstract void apply(NodeAppearance appr, Node node, CyNetwork network); 

    protected void apply(NodeAppearance appr, Node node, CyNetwork network, int type) {
		Object rangeValue = getRangeValue(node); 

		// If null, don't set anything - the existing value in appr is already
		// the default.
		if(rangeValue==null)
			return; 

		double ret =  ((Number)rangeValue).doubleValue();

		if ( type == WIDTH )
			appr.setJustWidth(ret);

		if ( type == HEIGHT )
			appr.setJustHeight(ret);

		if ( type == SIZE )
			appr.setSize(ret);
    }
}


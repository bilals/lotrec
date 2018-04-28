/*
 File: AbstractEdgeArrowCalculator.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute of Systems Biology
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
// $Revision: 8522 $
// $Date: 2006-10-19 18:15:21 -0700 (Thu, 19 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;

//----------------------------------------------------------------------------
import giny.model.Edge;

import java.util.Map;
import java.util.Properties;

import cytoscape.CyNetwork;
import cytoscape.visual.Arrow;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ArrowParser;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.ui.VizMapUI;

//----------------------------------------------------------------------------
abstract class AbstractEdgeArrowCalculator extends EdgeCalculator {

    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    protected byte arrowType;
    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    protected String propertyLabel;
    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    protected String typename;

    /** @deprecated This only exists to support deprecated code. DO NOT USE!!!
        will be removed 10/2007 */
    public void set( byte b, String p, String n) {
        arrowType = b;
        propertyLabel = p;
        typename = n;
    }

	protected static final byte SOURCE=0;
	
	// AJK: 11/27/2006 BEGIN
	//   fix this bug;  Target should NOT be the same as source, right?
//	protected static final byte TARGET=0;
	protected static final byte TARGET=1;


	public abstract byte getType(); 
	public abstract String getPropertyLabel();
	public abstract String getTypeName();
	
	AbstractEdgeArrowCalculator() {
		super();
	}

	public AbstractEdgeArrowCalculator(String name, ObjectMapping m) {
		super(name, m,Arrow.class);
	}

	public AbstractEdgeArrowCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, new ArrowParser(), Arrow.NONE);
	}

	abstract public void apply(EdgeAppearance appr, Edge edge, CyNetwork network);

	protected void apply(EdgeAppearance appr, Edge edge, CyNetwork network, byte end ) {

		Arrow a = (Arrow) getRangeValue(edge); 

		// default has already been set - no need to do anything
		if ( a == null )
			return;

		if ( end  == SOURCE )
			appr.setSourceArrow( a );
		else if ( end  == TARGET )
			appr.setTargetArrow( a ); 
	}
}

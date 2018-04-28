
/*
  File: AddPointListener.java 
  
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
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens for User Request to Create New Point.
 */
public class AddPointListener implements ActionListener {
    private ContinuousUI ui;
    private ContinuousMapping cm;
    private Object defaultObject;

    /**
     * Constructor.
     * @param ui ContinuousUI Object.
     * @param cm ContinuousMapping Object.
     */
    public AddPointListener(ContinuousUI ui, ContinuousMapping cm,
            Object defaultObject) {
        this.ui = ui;
        this.cm = cm;
        this.defaultObject = defaultObject;
    }

    /**
     * User Initiated Action.
     * @param e Action Event.
     */
    public void actionPerformed(ActionEvent e) {
        double value;
        int i = cm.getPointCount();
        BoundaryRangeValues brv = new BoundaryRangeValues();

        if (i > 0) {
            //  If this is not the first point, new point
            //  is based on the previous point in the list.
            ContinuousMappingPoint previousPoint = cm.getPoint(i - 1);
            BoundaryRangeValues previousRange = previousPoint.getRange();
            brv.lesserValue = previousRange.lesserValue;
            brv.equalValue = previousRange.equalValue;
            brv.greaterValue = previousRange.greaterValue;
            value = previousPoint.getValue();
        } else {
            //  If this is the first point, use Default Object, and value = 0.0
            brv.lesserValue = defaultObject;
            brv.equalValue = defaultObject;
            brv.greaterValue = defaultObject;
            value = 0.0;
        }
        cm.addPoint(value, brv);
        ui.resetUI();
    }
}
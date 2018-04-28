
/*
  File: ValueListener.java 
  
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
import cytoscape.visual.ui.ValueDisplayer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Listens for User Value Selection.
 */
public class ValueListener implements ItemListener {
    private ContinuousUI ui;
    private ContinuousMapping cm;
    private int index;
    private int offset;

    /**
     * Constructor.
     * @param ui ContinuousUI Object.
     * @param cm ContinuousMapping Object.
     */
    public ValueListener(ContinuousUI ui, ContinuousMapping cm,
            int index, int offset) {
        this.ui = ui;
        this.cm = cm;
        this.index = index;
        this.offset = offset;
    }

    /**
     * Item State Change.
     * @param e ItemEvent.
     */
    public void itemStateChanged(ItemEvent e) {
        Object o = ((ValueDisplayer) e.getItemSelectable()).getValue();
        ContinuousMappingPoint point = cm.getPoint(index);
        BoundaryRangeValues range = point.getRange();
        if (offset == ContinuousUI.LESSER) {
            range.lesserValue = o;
        } else if (offset == ContinuousUI.EQUAL) {
            range.equalValue = o;
            int numPoints = cm.getAllPoints().size();
            //  Update Values which are not accessible from UI
            if (numPoints > 1) {
                if (index == 0) {
                    range.greaterValue = o;
                } else if (index == numPoints -1) {
                    range.lesserValue = o;
                } else {
                    range.lesserValue = o;
                    range.greaterValue = o;
                }
            }
        } else if (offset == ContinuousUI.GREATER) {
            range.greaterValue = o;
        }
        ui.resetUI();
    }
}
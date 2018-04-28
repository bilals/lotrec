

/*
  File: DiscreteLegend.java 
  
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

package cytoscape.visual.mappings.discrete;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Component;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.mappings.LegendTable;

public class DiscreteLegend extends JPanel {

    public DiscreteLegend(Map legendMap, String visualAttr, String dataAttr, byte b) {
    	super();

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setBackground(Color.white);
	setAlignmentX(0);

	JLabel title = new JLabel(visualAttr + " is discretely mapped to " + dataAttr);
	add(title);
	
	Object[][] data = new Object[legendMap.keySet().size()][2];

	Iterator it = legendMap.keySet().iterator();
	for (int i = 0; i < legendMap.keySet().size(); i++) {
		Object key = it.next();
		data[i][0] = legendMap.get(key);
		data[i][1] = key;
	}
	
	add( LegendTable.getHeader() );
	add( new LegendTable( data, b ) );
    }
}


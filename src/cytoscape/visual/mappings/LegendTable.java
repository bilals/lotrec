

/*
  File: LegendTable.java 
  
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

package cytoscape.visual.mappings;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.JComponent;
import java.util.Map;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Component;
import java.awt.GridLayout;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.ui.IconSupport;
import cytoscape.visual.ui.VizMapUI;

public class LegendTable extends JPanel {


	byte type;
	public LegendTable(Object[][] data, byte b) {
		super();
		type = b;

		setLayout(new GridLayout(data.length, data[0].length,4,4) );
		setBackground(Color.white);
		setAlignmentX(0);

		for ( int i = 0; i < data.length; i++ )
			for ( int j = 0; j < data[i].length; j++ ) 
				add( getValue(data[i][j]) );
	}


	private JComponent getValue(Object value) {
			JComponent j = null;

			if (value instanceof Byte) {
				ImageIcon i = getIcon(value);
				//table.setRowHeight( row, i.getIconHeight() );	
				j = new JLabel( i );
			} else if (value instanceof LineType) {
				ImageIcon i = getIcon(value);
				//table.setRowHeight( row, i.getIconHeight() );	
				j = new JLabel( i );
			} else if (value instanceof Arrow) {
				ImageIcon i = getIcon(value);
				//table.setRowHeight( row, i.getIconHeight() );	
				j = new JLabel( i );
			} else if (value instanceof Color) {
				//setBackground((Color) value);
				j = new JLabel( IconSupport.getColorIcon((Color)value) );
			} else if (value instanceof Font) {
				Font f = (Font) value;
				JLabel lab = new JLabel();
				lab.setText(f.getFontName());
				lab.setFont(f);
				j = lab;
			} else if (value instanceof Double) {
				if ( type == VizMapUI.NODE_SIZE )
					j = new JLabel( IconSupport.getNodeSizeIcon((Double)value) );
				else if ( type == VizMapUI.NODE_WIDTH )
					j = new JLabel( IconSupport.getNodeWidthIcon((Double)value) );
				else if ( type == VizMapUI.NODE_HEIGHT )
					j = new JLabel( IconSupport.getNodeHeightIcon((Double)value) );
			} else if (value instanceof LabelPosition) {
					j = new JLabel( IconSupport.getLabelPositionIcon((LabelPosition)value) );
			} else { 
				j = new JLabel(value.toString());
			}

			j.setAlignmentX(0);

			return j;
	}

	private ImageIcon getIcon(Object o) {
		
		if ( o == null )
			return null;

		IconSupport is = new IconSupport(o);
		return is.getCurrentIcon(); 
	}

        public static JPanel getHeader() {
                JPanel titles = new JPanel();
                titles.setLayout(new GridLayout(1,2));
                titles.setAlignmentX(0);
                titles.setBackground(Color.white);

                titles.add( new JLabel("Visual Representation"));
                titles.add( new JLabel("Attribute Value"));
                return titles;
        }

}

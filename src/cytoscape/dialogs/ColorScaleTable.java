
/*
  File: ColorScaleTable.java 
  
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

// ColorScaleTable.java


//---------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.Border;

public class ColorScaleTable extends JPanel{
    public ColorScaleTable(int width, int height, double[][] bins){
        MyTableModel myModel = new MyTableModel(bins);
        JTable table = new JTable(myModel);

        table.setPreferredScrollableViewportSize(new Dimension(width, height));

        //Create the scroll pane and add the table to it. 
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up renderer and editor for the Favorite Color column.
        setUpColorRenderer(table);

        //Add the scroll pane to this window.
	this.add(scrollPane);
    }


    class ColorRenderer extends JLabel
                        implements TableCellRenderer {
	Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;

        public ColorRenderer(boolean isBordered) {
            super();
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

	public Component getTableCellRendererComponent(
						       JTable table, Object color, 
						       boolean isSelected, boolean hasFocus,
						       int row, int column) {
	    setBackground((Color)color);
	    if (isBordered) {
		if (isSelected) {
		    if (selectedBorder == null) {
			selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
									 table.getSelectionBackground());
		    }
		    setBorder(selectedBorder);
		} else {
		    if (unselectedBorder == null) {
			unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
									   table.getBackground());
		    }
                    setBorder(unselectedBorder);
		}
	    }
	    return this;
	}
    }



    private void setUpColorRenderer(JTable table) {
        table.setDefaultRenderer(Color.class,
                                 new ColorRenderer(false));
    }

    class MyTableModel extends AbstractTableModel {
        final String[] columnNames = {"Value", 
                                      "Color"};
	final Object[][] data;
         
	MyTableModel(double [][] bins){
	    int binCount = bins.length;
	    data = new Object[binCount][2];
	    for(int i=0;i<binCount;i++){
		data[i][0] = new Double(bins[i][0]);
		Color tempColor = new Color((int)bins[i][1],
					    (int)bins[i][2],
					    (int)bins[i][3]);
		data[i][1] = tempColor;
	    }
	}

        public int getColumnCount() {
            return columnNames.length;
        }
        
        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
}



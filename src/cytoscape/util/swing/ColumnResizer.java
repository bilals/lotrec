package cytoscape.util.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * 
 * Automatically resize column based on the objects in the cell.<br>
 * 
 * <p>
 * From <i>Swing Hacks</i> by Joshua Marinacci and Chris Adamson.<br>
 * 2005 Oreilly & Associates Inc. ISBN: 0-596-00907-0<br>
 * </p>
 * Customized by Keiichiro Ono
 * 
 * @since Cytoscape 2.4
 * @version 1.0
 * @author Joshua Marinacci, Chris Adamson, Keiichiro Ono
 * 
 */
public class ColumnResizer {

	private static final int DEFLMAX_WIDTH = 250;

	public static void adjustColumnPreferredWidths(JTable table) {
		// strategy - get max width for cells in column and
		// make that the preferred width
		TableColumnModel columnModel = table.getColumnModel();
		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxwidth = 0;
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer rend = table.getCellRenderer(row, col);
				Object value = table.getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent(table,
						value, false, false, row, col);
				maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
			} // for row

			/*
			 * this version of the width set considers the column header's
			 * preferred width too
			 */
			TableColumn column = columnModel.getColumn(col);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null)
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer
					.getTableCellRendererComponent(table, headerValue, false,
							false, 0, col);
			maxwidth = Math.max(maxwidth, headerComp.getPreferredSize().width);
			/*
			 * If the value is too big, adjust to fixed maximum val.
			 */
			if (DEFLMAX_WIDTH < maxwidth) {
				maxwidth = DEFLMAX_WIDTH;
			}
			column.setPreferredWidth(maxwidth + 20);

		} // for col
	}
}

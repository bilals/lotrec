package cytoscape.dialogs;

import java.net.URISyntaxException;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import cytoscape.CyNetwork;
import cytoscape.data.readers.MetadataParser;

/**
 * 
 * Table model for the Network Metadata Dialog.
 * 
 * @version 1.0
 * @since 2.3
 * @see cytoscape.dialogs.NetworkMetaDataDialog
 * @author kono
 * 
 */
public class NetworkMetaDataTableModel extends DefaultTableModel {

	private CyNetwork network;
	private Object[][] dataArray;
	private Map data;

	private String description;

	// Define Column names
	private static String[] columnHeader = new String[] { "Data Label", "Value" };
	// Define default entries. This determins the order in the table.
	private static String[] defaultEntries = { "Title", "Identifier", "Source",
			"Type", "Format", "Date" };

	private MetadataParser mdp;

	/**
	 * Constructor for the network metadata table model.
	 * 
	 * @param network
	 * 				Metadata for this network will be edited.
	 */
	public NetworkMetaDataTableModel(CyNetwork network) {
		super();
		this.network = network;
		description = null;
		mdp = new MetadataParser(this.network);
	}

	/**
	 * Set table data based on the Map object returned by the data
	 * parser.
	 * 
	 * @throws URISyntaxException
	 */
	protected void setTable() throws URISyntaxException {

		// Always 2 columns --- Data label and value.
		Object[] column_names = new Object[2];
		column_names[0] = "Data Label";
		column_names[1] = "Value";

		data = mdp.getMetadataMap();
		description = (String) data.get("Description");
		dataArray = new Object[defaultEntries.length][2];

		// Order vector based on the labels
		for (int i = 0; i < defaultEntries.length; i++) {
			String key = defaultEntries[i];
			dataArray[i][0] = key;
			dataArray[i][1] = data.get(key);
		}

		setDataVector(dataArray, columnHeader);
	}

	/**
	 * Get Desctiption entry, which will not be included in the table.
	 * 
	 * @return
	 * 			Long string of description.
	 * 
	 */
	public String getDescription() {
		return description;
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnHeader.length;
	}

	public int getRowCount() {
		return defaultEntries.length;
	}

	/**
	 * Determine which cell is editible or not.
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			// Do not allow to edit data names.
			return false;
		} else if (row == 0) {
			return false;
		} else {
			return true;
		}
	}

	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return dataArray[arg0][arg1];
	}

	public void setValueAt(Object obj, int row, int col) {
		dataArray[row][col] = obj;
		setDataVector(dataArray, columnHeader);
		fireTableCellUpdated(row, col);
	}
}

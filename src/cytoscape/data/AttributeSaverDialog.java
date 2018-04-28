/*
 File: AttributeSaverDialog.java 
 
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

package cytoscape.data;

import giny.model.GraphObject;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.writers.CyAttributesWriter2;

/**
 * Dialog box to save various attributes.<br>
 * 
 * @version 1.1
 */
public class AttributeSaverDialog extends JDialog {
	/**
	 * Default suffix for node attribute files
	 */
	protected static final String NODE_SUFFIX = ".NA";

	/**
	 * Default suffix for edge attribute files
	 */
	protected static final String EDGE_SUFFIX = ".EA";

	/**
	 * The max preferred size for the jscrollpane, will not let the jtable
	 * expand past this point
	 */
	protected static final int MAX_PREFERRED_SIZE = 100;

	/**
	 * Constant ot specify nodes
	 */
	public static final int NODES = 0;

	/**
	 * Constant to specify edges
	 */
	public static final int EDGES = 1;

	/**
	 * The state associated with the attribute table, keeps track of the
	 * attribute, filename and booleans. Edited through the jtable
	 */
	private AttributeSaverState state;

	/**
	 * JTable for displaying boolean, attribute and filename
	 */
	private JTable attributeTable;

	/**
	 * Show a dialog of hte specified type, see above constants
	 */
	protected static void showDialog(final int type) {
		final AttributeSaverDialog dialog = new AttributeSaverDialog(type);
		dialog.setVisible(true);
	}

	/**
	 * Show a dialog box to save edge attributes
	 */
	public static void showEdgeDialog() {
		showDialog(EDGES);
	}

	/**
	 * Show a dialog box to save node attributes
	 */
	public static void showNodeDialog() {
		showDialog(NODES);
	}

	/**
	 * Create a dialog box of the specified type. Instead of constructor, use
	 * static methods to create dialog box
	 */
	public AttributeSaverDialog(final int type) {
		super(Cytoscape.getDesktop(), "Save Attributes", true);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// create the objects which will maintain the state of the dialog
		final String suffix;
		final String[] attributes;
		if (type == NODES) {
			suffix = NODE_SUFFIX;
			attributes = Cytoscape.getNodeAttributes().getAttributeNames();
		} // end of if ()
		else {
			suffix = EDGE_SUFFIX;
			attributes = Cytoscape.getEdgeAttributes().getAttributeNames();
		} // end of else

		state = new AttributeSaverState(attributes, suffix, type);

		attributeTable = new JTable(state);
		attributeTable
				.setToolTipText("Select multiple attributes to save. Modify \"Filename\" field to specify filename");
		attributeTable.setCellSelectionEnabled(false);

		// initialize the directory browser component
		JButton saveButton = new JButton("Choose Directory and Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				final JFileChooser myChooser = new JFileChooser(CytoscapeInit
						.getMRUD());
				myChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (myChooser.showOpenDialog(Cytoscape.getDesktop()) == JFileChooser.APPROVE_OPTION) {
					state.setSaveDirectory(myChooser.getSelectedFile());
					CytoscapeInit.setMRUD(myChooser.getSelectedFile());
					int count = 0;
					try {
						count = state.writeState();
					} catch (IOException e) {
						e.printStackTrace();
					}
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							"Successfully saved " + count + " files");
					AttributeSaverDialog.this.dispose();
				}
			}
		});

		JPanel centerPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(attributeTable);
		scrollPane.setPreferredSize(new Dimension(MAX_PREFERRED_SIZE,
				MAX_PREFERRED_SIZE));
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel
				.add(new JLabel(
						"Select multiple attributes to save. Edit table to change filenames"));
		centerPanel.add(scrollPane);

		JPanel southPanel = new JPanel();
		southPanel.add(saveButton);

		contentPane.add(centerPanel, BorderLayout.CENTER);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		pack();
	}

}

/**
 * Holds the state associated with the dialog.<br>
 */
class AttributeSaverState implements TableModel {
	public static String newline = System.getProperty("line.separator");
	/**
	 * The default string to append for an attribute filename
	 */
	protected final String suffix;

	/**
	 * The directory in which to save the files
	 */
	private File saveDirectory;

	/**
	 * Type of graph object to save
	 */
	private int type;

	/**
	 * List of all attributes
	 */
	private Vector<String> attributeNames;

	/**
	 * List of all filenames
	 */
	private Vector<String> filenames;

	/**
	 * List of all booleans, tells whether to save
	 */
	private Vector<Boolean> selectedAttributes;

	/**
	 * A vector of all the objects that are listening to this TableModel
	 */
	private Vector listeners;
	/**
	 * Network to from which to read graph objects
	 */
	// private CyNetwork cyNetwork;
	// colum identities
	protected static final int FILE_COLUMN = 2;
	protected static final int ATTRIBUTE_COLUMN = 1;
	protected static final int SAVE_COLUMN = 0;

	/**
	 * Initialize the state
	 * 
	 * @param nodeAttributes
	 *            An array of strings containing all node attributes
	 * @param type
	 *            operate on NODES or EDGES
	 * @param cyNetwork
	 *            the network to save
	 */
	public AttributeSaverState(final String[] nodeAttributes, final String suffix,
			int type) {
		this.type = type;
		this.suffix = suffix;
		this.listeners = new Vector();
		this.attributeNames = new Vector<String>();
		this.filenames = new Vector<String>();
		this.selectedAttributes = new Vector<Boolean>();
		for (int idx = 0; idx < nodeAttributes.length; idx++) {
			attributeNames.add(nodeAttributes[idx]);
		} // end of for ()
		Collections.sort(attributeNames);

		for (Iterator stringIt = attributeNames.iterator(); stringIt.hasNext();) {
			String attribute = (String) stringIt.next();
			filenames.add(attribute + suffix);
			selectedAttributes.add(new Boolean(false));
		} // end of for ()
	}

	/**
	 * Set the directory where the files will be saved to
	 */
	public void setSaveDirectory(final File saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public int writeState() throws IOException {
		
		final CyAttributes cyAttributes;
		if (type == AttributeSaverDialog.NODES) {
			cyAttributes = Cytoscape.getNodeAttributes();
		} else {
			cyAttributes = Cytoscape.getEdgeAttributes();
		}
		
		int count = 0;
		for (int idx = 0; idx < attributeNames.size(); idx++) {
			if (selectedAttributes.get(idx)) {
				
				final String attributeName = attributeNames.get(idx);

				final File attributeFile = new File(saveDirectory,
						filenames.get(idx));
				final FileWriter fileWriter = new FileWriter(attributeFile);
				fileWriter.write(attributeName + newline);
				
				final CyAttributesWriter2 writer = new CyAttributesWriter2(cyAttributes, attributeNames.get(idx), fileWriter);
				writer.writeAttributes();
				
				fileWriter.close();
				count++;
			}
		}
		return count;
	}

	// below here is implementing the tableModel
	// see the interface for description of the methods
	public void addTableModelListener(TableModelListener tml) {
		this.listeners.add(tml);
		return;
	}

	public void removeTableModelListener(TableModelListener tml) {
		this.listeners.remove(tml);
		return;
	}

	public Class getColumnClass(int columnIndex) {
		if (columnIndex == SAVE_COLUMN) {
			return Boolean.class;
		} // end of if ()
		else {
			return String.class;
		}
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return attributeNames.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case SAVE_COLUMN:
			return selectedAttributes.get(rowIndex);
		case ATTRIBUTE_COLUMN:
			return attributeNames.get(rowIndex);
		case FILE_COLUMN:
			return filenames.get(rowIndex);
		default:
			throw new IllegalArgumentException();
		} // end of switch ()
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case SAVE_COLUMN:
			return "Save";
		case ATTRIBUTE_COLUMN:
			return "Attribute";
		case FILE_COLUMN:
			return "Filename";
		default:
			throw new IllegalArgumentException();
		} // end of switch ()
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex != ATTRIBUTE_COLUMN) {
			return true;
		} // end of if ()
		return false;
	}

	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
		switch (columnIndex) {
		case ATTRIBUTE_COLUMN:
			throw new RuntimeException("Cell is not editable");
		case SAVE_COLUMN:
			selectedAttributes.set(rowIndex, (Boolean) aValue);
			break;
		case FILE_COLUMN:
			filenames.set(rowIndex, (String) aValue);
			break;
		default:
			break;
		} // end of switch ()
	}

}

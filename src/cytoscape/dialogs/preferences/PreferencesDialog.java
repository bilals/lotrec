
/*
  File: PreferencesDialog.java 
  
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

package cytoscape.dialogs.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

public class PreferencesDialog extends JDialog implements
		PropertyChangeListener {

	int[] selection = null;

	JScrollPane propsTablePane = new JScrollPane();

	JTable prefsTable = new JTable();

	JPanel propBtnPane = new JPanel(new FlowLayout());

	JPanel okButtonPane = new JPanel(new FlowLayout());

	JPanel vizmapPane = new JPanel(new FlowLayout());

	JPanel cyPropsPane = new JPanel(new FlowLayout());

	JCheckBox saveVizmapBtn = new JCheckBox("Make Current Visual Styles Default",false);

	JCheckBox saveCyPropsBtn = new JCheckBox("Make Current Cytoscape Properties Default",false);

	JTextArea vizmapText = new JTextArea( "Only check this option if you want the current visual styles to be defaults in ALL future cytoscape sessions.  Your current visual styles are automatically saved in your Cytoscape session file and won't be lost." );

        JTextArea cyPropsText = new JTextArea( "Only check this option if you want the current Cytoscape properties to be defaults in ALL future cytoscape sessions.  Your current Cytoscape properties are automatically saved in your Cytoscape session file and won't be lost." );


	JButton addPropBtn = new JButton("Add");

	JButton deletePropBtn = new JButton("Delete");

	JButton modifyPropBtn = new JButton("Modify");

	JButton okButton = new JButton("OK");

	JButton cancelButton = new JButton("Cancel");

	public PreferenceTableModel prefsTM = null;

	private ListSelectionModel lsm = null;

	private ListSelectionModel lsmA = null;

	private boolean saveCyPropsAsDefault = false;
	private boolean saveVizmapAsDefault = false;

	// When properties are changed, it will be processed here.
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Cytoscape.PREFERENCE_MODIFIED)) {
			System.out.println("Cytoscape Prop. has changed: ");
			System.out.println(" - Old value is " + e.getOldValue());
			System.out.println(" - New value is " + e.getNewValue());

			String propName = null;
			if (CytoscapeInit.getProperties().getProperty("defaultSpeciesName") == e
					.getOldValue() || CytoscapeInit.getProperties().getProperty("defaultSpeciesName") == e
					.getNewValue()) {
				propName = "defaultSpeciesName";
			} else if (CytoscapeInit.getProperties().getProperty(
					"defaultWebBrowser") == e.getOldValue() ||
					CytoscapeInit.getProperties().getProperty(
					"defaultWebBrowser") == e.getNewValue()) {
				propName = "defaultWebBrowser";
			}

			if (propName != null) {
				// Set to new val
				CytoscapeInit.getProperties().setProperty( propName,
						(String) e.getNewValue());
				prefsTM.setProperty( propName, (String) e
						.getNewValue());
				// refresh();
				System.out.println(propName + " updated to "
						+ CytoscapeInit.getProperties().getProperty(
								propName));
			}
		}
	}

	public void setParameter(TableModel tm, String preferenceName,
			String preferenceValue) {
			// preferences/properties
		if (tm == prefsTM) {

			Cytoscape.firePropertyChange(Cytoscape.PREFERENCE_MODIFIED, prefsTM
					.getProperty(preferenceName), preferenceValue);
			prefsTM.setProperty(preferenceName, preferenceValue);
		}

		 refresh();

		// reset state of Modify and Delete buttons to inactive
		// since update of parameter will clear any selections
		modifyPropBtn.setEnabled(false);
		deletePropBtn.setEnabled(false);
	}

	public void refresh() {
		// refresh the view
		prefsTable.setModel(prefsTM);

		prefsTable.clearSelection();
		prefsTable.revalidate();
		prefsTable.repaint();
	}

	private void initButtonPane() {
		propBtnPane.add(addPropBtn);
		propBtnPane.add(modifyPropBtn);
		propBtnPane.add(deletePropBtn);

		okButtonPane.add(okButton);
		okButtonPane.add(cancelButton);

		modifyPropBtn.setEnabled(false);
		deletePropBtn.setEnabled(false);
		addPropBtn.addActionListener(new AddPropertyListener(this));
		modifyPropBtn.addActionListener(new ModifyPropertyListener(this));
		deletePropBtn.addActionListener(new DeletePropertyListener(this));
		okButton.addActionListener(new OkButtonListener(this));
		cancelButton.addActionListener(new CancelButtonListener(this));
		saveVizmapBtn.addItemListener(new CheckBoxListener());
		saveCyPropsBtn.addItemListener(new CheckBoxListener());
	}

	public PreferenceTableModel getPTM() {
		return prefsTM;
	}

	private void initTable() {
		prefsTM = new PreferenceTableModel();

		prefsTable.setAutoCreateColumnsFromModel(false);
		prefsTable.setRowSelectionAllowed(true);
		lsm = prefsTable.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsm.addListSelectionListener(new TableListener(this, lsm));

		prefsTable.setModel(prefsTM);

		for (int i = 0; i < PreferenceTableModel.columnHeader.length; i++) {
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(PreferenceTableModel.alignment[i]);

			TableColumn Column = new TableColumn(i,
					PreferenceTableModel.columnWidth[i], renderer, null);
			Column.setIdentifier(PreferenceTableModel.columnHeader[i]);
			prefsTable.addColumn(Column);
		}
	}

	public PreferencesDialog(Frame owner) {
		super(owner);

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);

		initButtonPane();
		initTable();
		try {
			prefPopupInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setTitle("Cytoscape Preferences Editor");
		pack();
		// set location relative to owner/parent
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	private void prefPopupInit() throws Exception {

		Box outerBox = Box.createVerticalBox();
		outerBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 8, 10));

		Box propsTableBox = Box.createVerticalBox();
		propsTablePane.setBorder(BorderFactory.createEmptyBorder(2, 9, 4, 9));
		propsTablePane.getViewport().add(prefsTable, null);
		prefsTable.setPreferredScrollableViewportSize(new Dimension(400, 200));
		propsTableBox.add(propsTablePane);
		propsTableBox.add(Box.createVerticalStrut(5));
		propsTableBox.add(propBtnPane);
		propsTableBox.setBorder(BorderFactory.createTitledBorder("Properties"));
		outerBox.add(propsTableBox);
		outerBox.add(Box.createVerticalStrut(10));

		outerBox.add(Box.createVerticalStrut(10));
		JTextArea textArea = new JTextArea( "NOTE: Changes to these properties are used in the current session ONLY unless otherwise specified below." );

		textArea.setBackground(outerBox.getBackground());
		textArea.setEditable(false);
		textArea.setDragEnabled(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		outerBox.add(textArea);

		outerBox.add(Box.createVerticalStrut(10));
		Box vizmapBox = Box.createVerticalBox();
		vizmapBox.setBorder(BorderFactory.createTitledBorder("Default Visual Styles"));
		vizmapText.setBackground(outerBox.getBackground());
		vizmapText.setEditable(false);
		vizmapText.setDragEnabled(false);
		vizmapText.setLineWrap(true);
		vizmapText.setWrapStyleWord(true);
		vizmapBox.add(vizmapText);
		vizmapBox.add(Box.createVerticalStrut(5));
		vizmapPane.add(saveVizmapBtn);
		vizmapBox.add(vizmapPane);
		outerBox.add(vizmapBox);

		outerBox.add(Box.createVerticalStrut(10));
		Box cyPropsBox = Box.createVerticalBox();
		cyPropsBox.setBorder(BorderFactory.createTitledBorder("Default Cytoscape Properties"));
		cyPropsText.setBackground(outerBox.getBackground());
		cyPropsText.setEditable(false);
		cyPropsText.setDragEnabled(false);
		cyPropsText.setLineWrap(true);
		cyPropsText.setWrapStyleWord(true);
		cyPropsBox.add(cyPropsText);
		cyPropsBox.add(Box.createVerticalStrut(5));
		cyPropsPane.add(saveCyPropsBtn);
		cyPropsBox.add(cyPropsPane);
		outerBox.add(cyPropsBox);

		outerBox.add(Box.createVerticalStrut(8));
		outerBox.add(okButtonPane);

		this.getContentPane().add(outerBox, BorderLayout.CENTER);
	}

	class AddPropertyListener implements ActionListener {
		PreferencesDialog callerRef = null;

		public AddPropertyListener(PreferencesDialog caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			String key = JOptionPane.showInputDialog(addPropBtn,
					"Enter property name:", "Add Property",
					JOptionPane.QUESTION_MESSAGE);
			if (key != null) {
				String value = JOptionPane.showInputDialog(addPropBtn,
						"Enter value for property " + key + ":",
						"Add Property Value", JOptionPane.QUESTION_MESSAGE);
				if (value != null) {
					String[] vals = { key, value };
					prefsTM.addProperty(vals);
					refresh(); // refresh view in table
				}
			}
		}
	}

	class ModifyPropertyListener implements ActionListener {
		PreferencesDialog callerRef = null;

		public ModifyPropertyListener(PreferencesDialog caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < selection.length; i++) {
				String name = new String((String) (prefsTM.getValueAt(
						selection[i], 0)));
				String value = new String((String) (prefsTM.getValueAt(
						selection[i], 1)));

				PreferenceValueDialog pd = new PreferenceValueDialog(
						PreferencesDialog.this, name, value, callerRef,
						prefsTM, "Modify value...", false);
			}
		}
	}

	class DeletePropertyListener implements ActionListener {
		PreferencesDialog callerRef = null;

		public DeletePropertyListener(PreferencesDialog caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < selection.length; i++) {
				String name = new String((String) (prefsTM.getValueAt(
						selection[i], 0)));
				prefsTM.deleteProperty(name);
			}
			refresh();
		}
	}

	class OkButtonListener implements ActionListener {
		PreferencesDialog callerRef = null;

		public OkButtonListener(PreferencesDialog caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			// just saving via putAll() doesn't handle deletes...
			// therefore use TableModel's putAll() into new Properties obj
			// then clear Cytoscape's properties and
			Properties newProps = new Properties();
			callerRef.prefsTM.save(newProps);
			CytoscapeInit.getProperties().clear();
			CytoscapeInit.getProperties().putAll(newProps);
			callerRef.setVisible(false);

			if ( saveVizmapAsDefault ) {
				Cytoscape.firePropertyChange(Cytoscape.SAVE_VIZMAP_PROPS, null, null); 
				saveVizmapAsDefault = false;
				saveVizmapBtn.setSelected(false);
			}
			if ( saveCyPropsAsDefault ) {
				try {
				File file = CytoscapeInit.getConfigFile( "cytoscape.props" );
				FileOutputStream output = new FileOutputStream( file );
				CytoscapeInit.getProperties().store( output, "Cytoscape Property File" );
				System.out.println("wrote Cytoscape properties file to: " + file.getAbsolutePath());
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Could not write cytoscape.props file!");
				}
				saveCyPropsAsDefault = false;
				saveCyPropsBtn.setSelected(false);
			}
			Cytoscape.firePropertyChange(Cytoscape.PREFERENCES_UPDATED, null, null); 
		}
	}

	class CheckBoxListener implements ItemListener { 

		public CheckBoxListener() {
			super();
		}

		public void itemStateChanged(ItemEvent e) {
	        	Object source = e.getItemSelectable();
		        if (e.getStateChange() == ItemEvent.SELECTED) {
		    		if (source == saveVizmapBtn) 
					saveVizmapAsDefault = true;
		    		if (source == saveCyPropsBtn) 
					saveCyPropsAsDefault = true;
			}
		}
	}

	class CancelButtonListener implements ActionListener {
		PreferencesDialog callerRef = null;

		public CancelButtonListener(PreferencesDialog caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			Properties oldProps = CytoscapeInit.getProperties();
			callerRef.prefsTM.restore(oldProps);
			callerRef.setVisible(false);
		}
	}

	class TableListener implements ListSelectionListener {
		private ListSelectionModel model = null;

		private PreferencesDialog motherRef = null;

		public TableListener(PreferencesDialog mother, ListSelectionModel lsm) {
			motherRef = mother;
			model = lsm;
		}

		public void valueChanged(ListSelectionEvent lse) {
			if (!lse.getValueIsAdjusting()) {
				StringBuffer buf = new StringBuffer();
				selection = getSelectedIndices(model.getMinSelectionIndex(),
						model.getMaxSelectionIndex());
				if (selection.length == 0) {
				} else {
					modifyPropBtn.setEnabled(true);
					deletePropBtn.setEnabled(true);
				}
			}
		}

		protected int[] getSelectedIndices(int start, int stop) {
			if ((start == -1) || (stop == -1)) {
				return new int[0];
			}

			int guesses[] = new int[stop - start + 1];
			int index = 0;

			for (int i = start; i <= stop; i++) {
				if (model.isSelectedIndex(i)) {
					guesses[index++] = i;
				}
			}
			int realthing[] = new int[index];
			System.arraycopy(guesses, 0, realthing, 0, index);
			return realthing;
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

}

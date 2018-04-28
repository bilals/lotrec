/*
 File: BioDataServerPanel4.java 
 
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

package cytoscape.data.servers.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import cytoscape.CytoscapeInit;

/*
 * Final Step for the new file format. This panel creates the manifest file and
 * load the biodataserver.
 */
public class ManifestfileSelectionPanel extends JPanel {

	private JLabel anotherBlankSpace;
	private JLabel blankSpace;
	private JLabel jLabel1;
	private JPanel jPanel1;
	private JPanel contentPanel;
	private JLabel iconLabel;
	private JSeparator separator;
	private JLabel textLabel;
	private JPanel titlePanel;
	private JLabel jLabel3;
	private JComboBox spList;

	File start;
	String species;

	private JTextField taxonNameBox;
	private JTextField manifestFileName;
	private JButton selectManifestFile;
	private String manifestName;
	private JFileChooser jc1;

	public ManifestfileSelectionPanel() {
		super();

		species = null;
		start = CytoscapeInit.getMRUD();
		spList = new JComboBox();

		contentPanel = getContentPanel();
		ImageIcon icon = getImageIcon();

		titlePanel = new javax.swing.JPanel();
		textLabel = new javax.swing.JLabel();
		iconLabel = new javax.swing.JLabel();
		separator = new javax.swing.JSeparator();

		setLayout(new java.awt.BorderLayout());

		titlePanel.setLayout(new java.awt.BorderLayout());
		titlePanel.setBackground(Color.gray);

		textLabel.setBackground(Color.gray);
		textLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));
		textLabel.setText("Load Manifest File");
		textLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		textLabel.setOpaque(true);

		iconLabel.setBackground(Color.gray);
		if (icon != null)
			iconLabel.setIcon(icon);

		titlePanel.add(textLabel, BorderLayout.CENTER);
		titlePanel.add(iconLabel, BorderLayout.EAST);
		titlePanel.add(separator, BorderLayout.SOUTH);

		add(titlePanel, BorderLayout.NORTH);
		JPanel secondaryPanel = new JPanel();
		secondaryPanel.add(contentPanel, BorderLayout.SOUTH);
		add(secondaryPanel, BorderLayout.WEST);

	}

	public void createManifestFileChooser() {
		jc1 = new JFileChooser(start);
		jc1.setDialogTitle("Select Manifest File");
	}

	public String getManifestFileNameFromTextBox() {
		manifestName = manifestFileName.getText();
		return manifestName;
	}

	public void addSelectButtonActionListener(ActionListener l) {
		selectManifestFile.addActionListener(l);
	}

	public File getManifestFile(boolean show) {
		File targetFile = null;
		if (show == true) {
			int result = jc1.showOpenDialog(this);
		}
		targetFile = jc1.getSelectedFile();
		return targetFile;
	}

	public void setManifestFileName(String fileName) {
		manifestFileName.setText(fileName);
		manifestName = fileName;
	}

	public String getManifestFileName() {
		return manifestName;
	}

	private JPanel getContentPanel() {

		JPanel contentPanel1 = new JPanel();
		contentPanel1.setLayout(new GridLayout(3, 1));

		manifestFileName = new JTextField(44);
		selectManifestFile = new JButton("Select Manifest File");

		jPanel1 = new JPanel();
		jPanel1.setLayout(new FlowLayout());

		blankSpace = new JLabel();
		anotherBlankSpace = new JLabel();

		jLabel1 = new JLabel();
		jLabel3 = new JLabel();

		jLabel1
				.setText("Manifest file should include both annotation and ontology files.");

		contentPanel1.add(jLabel1);
		jPanel1.add(blankSpace);
		jPanel1.add(anotherBlankSpace);
		jPanel1.add(manifestFileName);
		jPanel1.add(selectManifestFile);
		contentPanel1.add(jPanel1);

		jLabel3
				.setText("Please press the Finish button to load BioDataServer.");
		contentPanel1.add(jLabel3);

		return contentPanel1;
	}

	public void setComboBoxState(boolean state) {
		spList.setEnabled(state);
	}

	public void setTextBoxState(boolean state) {
		taxonNameBox.setEnabled(state);
	}

	private ImageIcon getImageIcon() {
		return null;
	}

}

/*
 * LegendDialog.java
 */

package cytoscape.visual.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.freehep.util.export.ExportDialog;
import org.jdesktop.layout.GroupLayout; 

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ContinuousMapping;

import cytoscape.Cytoscape;

public class LegendDialog extends JDialog {


	private Map legendMap;
	private VisualStyle visualStyle;
	private JPanel jPanel1;
	private JButton jButton1;
	private JButton jButton2;
	private JScrollPane jScrollPane1;
	private Component parent;

	public LegendDialog(Dialog parent, VisualStyle vs) {
		super(parent,true);
		visualStyle = vs;
		this.parent = parent;
		initComponents();
	}

	private JPanel generateLegendPanel() {

		JPanel legend = new JPanel();
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
		legend.setBackground(Color.white);

		NodeAppearanceCalculator nac = visualStyle.getNodeAppearanceCalculator();
		List<Calculator> calcs = nac.getCalculators();
		for ( Calculator calc : calcs ) {

			// AAARGH
			if ( nac.getNodeSizeLocked() ) {
				if ( calc.getType() == VizMapUI.NODE_WIDTH ) 
					continue;
				else if ( calc.getType() == VizMapUI.NODE_HEIGHT ) 
					continue;
			} else {
				if ( calc.getType() == VizMapUI.NODE_SIZE ) 
					continue;
			}


			ObjectMapping om = calc.getMapping(0);
			JPanel mleg = om.getLegend(calc.getTypeName(),calc.getType()); 
			// Add passthrough mappings to the top since they don't
			// display anything besides the title.
			if ( om instanceof PassThroughMapping )
				legend.add( mleg, 0 ); 
			else
				legend.add( mleg ); 
		}

		EdgeAppearanceCalculator eac = visualStyle.getEdgeAppearanceCalculator();
		calcs = eac.getCalculators();
		int top = legend.getComponentCount(); 
		for ( Calculator calc : calcs ) {
			ObjectMapping om = calc.getMapping(0);
			JPanel mleg = om.getLegend(calc.getTypeName(),calc.getType()); 
			// Add passthrough mappings to the top since they don't
			// display anything besides the title.
			if ( om instanceof PassThroughMapping )
				legend.add( mleg, top ); 
			else
				legend.add( mleg ); 
		}

		return legend;
	}

	private void initComponents() {

		jPanel1 = generateLegendPanel();

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(jPanel1);

		jButton1 = new JButton();
		jButton1.setText("Export");
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				export();
			}
		});

		jButton2 = new JButton();
		jButton2.setText("Cancel");
		jButton2.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent evt) {
				dispose();	
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(jButton1);
		buttonPanel.add(jButton2);
	
		JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
		containerPanel.add(jScrollPane1);
		containerPanel.add(buttonPanel);
		
		setContentPane(containerPanel);
		setPreferredSize(new Dimension(400,400));

		pack();
	}

	private void export() {
		ExportDialog export = new ExportDialog();
		export.showExportDialog(parent, "Export legend as ...",
			jPanel1, "export");
	}
}

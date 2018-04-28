package cytoscape.util.swing;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;

/**
 * Simple status bar with 3 fields.<br>
 * 
 * @author kono
 *
 */
public class JStatusBar extends JPanel {

	public JStatusBar() {
		initComponents();
	}

	public void setLeftLabel(String text) {
		leftLabel.setText(text);
	}

	public void setCenterLabel(String text) {
		centerLabel.setText(text);
	}

	public void setRightLabel(String text) {
		rightLabel.setText(text);
	}

	private void initComponents() {
		leftPanel = new javax.swing.JPanel();
		leftLabel = new javax.swing.JLabel();
		centerPanel = new javax.swing.JPanel();
		centerLabel = new javax.swing.JLabel();
		rightPanel = new javax.swing.JPanel();
		rightLabel = new javax.swing.JLabel();

		leftPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		leftPanel.setPreferredSize(new Dimension(40, 40));
		leftLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
		leftLabel.setPreferredSize(new Dimension(20, 20));

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				leftPanel);
		leftPanel.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				jPanel1Layout.createSequentialGroup().addContainerGap().add(
						leftLabel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300,
						Short.MAX_VALUE).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout.createSequentialGroup().add(leftLabel)
						.addContainerGap(2, Short.MAX_VALUE)));

		centerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		centerLabel.setText("jLabel2");

		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
				centerPanel);
		centerPanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel2Layout.createSequentialGroup().addContainerGap().add(
						centerLabel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100,
						Short.MAX_VALUE).addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(centerLabel,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 17,
				Short.MAX_VALUE));

		rightPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		rightLabel.setText("jLabel3");

		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(
				rightPanel);
		rightPanel.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel3Layout.createSequentialGroup().addContainerGap().add(
						rightLabel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 130,
						Short.MAX_VALUE).addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel3Layout.createSequentialGroup().add(rightLabel)
						.addContainerGap(2, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(leftPanel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						centerPanel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						rightPanel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(leftPanel, 0, 21,
				Short.MAX_VALUE).add(centerPanel,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.add(rightPanel, 0, 21, Short.MAX_VALUE));
	}// </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JLabel leftLabel;
	private javax.swing.JLabel centerLabel;
	private javax.swing.JLabel rightLabel;
	private javax.swing.JPanel leftPanel;
	private javax.swing.JPanel centerPanel;
	private javax.swing.JPanel rightPanel;
	// End of variables declaration

}

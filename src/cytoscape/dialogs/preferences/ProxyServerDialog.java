package cytoscape.dialogs.preferences;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import javax.swing.JOptionPane;
import java.net.Proxy;
import cytoscape.util.ProxyHandler;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import java.net.InetSocketAddress;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


public class ProxyServerDialog extends JDialog implements ActionListener, ItemListener {


	/** Creates new form URLimportAdvancedDialog */
    public ProxyServerDialog(javax.swing.JFrame pParent) {
        super(pParent, true);
        this.setTitle("Proxy server setting");
        this.setLocationRelativeTo(pParent);

        initComponents();
        initValues();
    }
    
       
    // Variables declaration - do not modify
    private javax.swing.JComboBox cmbType;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnCancel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbHost;
    private javax.swing.JLabel lbPort;
    private javax.swing.JLabel lbType;
    private javax.swing.JTextField tfHost;
    private javax.swing.JTextField tfPort;
    // End of variables declaration

    
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbType = new javax.swing.JLabel();
        lbHost = new javax.swing.JLabel();
        lbPort = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        tfHost = new javax.swing.JTextField();
        tfPort = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lbType.setText("Type");
        getContentPane().add(lbType, new java.awt.GridBagConstraints());

        lbHost.setText("Host name");
        getContentPane().add(lbHost, new java.awt.GridBagConstraints());

        lbPort.setText("Port");
        getContentPane().add(lbPort, new java.awt.GridBagConstraints());

        cmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DIRECT", "HTTP", "SOCKS" }));
        cmbType.setMinimumSize(new java.awt.Dimension(61, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 5);
        getContentPane().add(cmbType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        getContentPane().add(tfHost, gridBagConstraints);

        tfPort.setMinimumSize(new java.awt.Dimension(43, 19));
        tfPort.setPreferredSize(new java.awt.Dimension(43, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        getContentPane().add(tfPort, gridBagConstraints);

        btnUpdate.setText("Update");
        jPanel1.add(btnUpdate);

        btnCancel.setText("Cancel");
        jPanel1.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);
        
    	btnUpdate.setEnabled(false);
        
        // add event listeners
        btnUpdate.addActionListener(this);
        btnCancel.addActionListener(this);
        cmbType.addItemListener(this);
        
        pack();
        setSize(new Dimension(350,150));

    }// </editor-fold>

    
    private void initValues(){
		Proxy p = ProxyHandler.getProxyServer();
		
		if (( p == null)|| (p.type() == Proxy.Type.DIRECT)){			
			return;
		}
		else if (p.type() == Proxy.Type.HTTP) {
			cmbType.setSelectedItem("HTTP");
		}
		else if (p.type() == Proxy.Type.SOCKS) {
			cmbType.setSelectedItem("SOCKS");
		}	
		InetSocketAddress address = (InetSocketAddress) p.address();
		tfHost.setText(address.getHostName());
		tfPort.setText(new Integer(address.getPort()).toString());		
    }


    public void itemStateChanged(ItemEvent e) {
    	
    	String selectedItem = cmbType.getSelectedItem().toString();
    	if (selectedItem.equalsIgnoreCase("DIRECT")) {
    		btnUpdate.setEnabled(false);
    	}
    	else {
    		btnUpdate.setEnabled(true);    		
    	}
    }
        
    
 	public void actionPerformed(ActionEvent e)
 	{
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton)
		{
			JButton _btn = (JButton)_actionObject;

			if (_btn == btnCancel) {				
				this.dispose();
			}
			else if (_btn == btnUpdate) {
				if (!updateProxyServer())
					return;
				this.dispose();
			}
		}
 	}

 	
 	private boolean updateProxyServer() {
		
		Proxy.Type proxyType = Proxy.Type.valueOf(cmbType.getSelectedItem().toString());

		// If proxyType is DIRECT, do nothing
		if (proxyType == java.net.Proxy.Type.DIRECT) {
			tfHost.setText("");
			tfPort.setText("");
			return false;
		}

		// Try if we can create a proxyServer, if not, report error
		if (tfHost.getText().trim().equals("")) {
		    JOptionPane.showMessageDialog(this, "Host name is empty!", "Warning", JOptionPane.INFORMATION_MESSAGE);
			return false;								
		}

		int thePort;
		try {
			Integer tmpInteger = new Integer(tfPort.getText().trim());
			thePort = tmpInteger.intValue();
		}
		catch (Exception exp) {
		    JOptionPane.showMessageDialog(this, "Port error!", "Warning", JOptionPane.INFORMATION_MESSAGE);
			return false;					
		}
				
		InetSocketAddress theAddress = new InetSocketAddress(tfHost.getText().trim(), thePort);
		try {
			new Proxy(proxyType, theAddress);					
		}
		catch (Exception expProxy) {
		    JOptionPane.showMessageDialog(this, "Proxy server error!", "Warning", JOptionPane.INFORMATION_MESSAGE);					
			return false;
		}

		// Yes, we create a proxy server successfully, Update the proxy server info 
		CytoscapeInit.getProperties().setProperty("proxy.server",tfHost.getText().trim());
		CytoscapeInit.getProperties().setProperty("proxy.server.port", tfPort.getText());
		CytoscapeInit.getProperties().setProperty("proxy.server.type",cmbType.getSelectedItem().toString());

		Cytoscape.firePropertyChange(Cytoscape.PREFERENCES_UPDATED,null,null);

    	return true;
    } 
}

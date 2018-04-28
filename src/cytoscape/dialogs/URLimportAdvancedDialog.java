package cytoscape.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.net.Proxy;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.DataSource;
import cytoscape.util.BookmarksUtil;
import cytoscape.util.ProxyHandler;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.File;
import java.net.InetSocketAddress;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


public class URLimportAdvancedDialog extends JDialog implements ActionListener, ListSelectionListener, ItemListener {

	JDialog parent;
	private String bookmarkCategory;
	private Bookmarks theBookmarks;	
	//private URL bookmarkURL;
		
    /** Creates new form URLimportAdvancedDialog */
    public URLimportAdvancedDialog(JDialog pParent, boolean modal, String pBookmarkCategory, 
    		Bookmarks pBookmarks) {
        super(pParent, modal);
        this.setTitle("Advanced Setting for " + pBookmarkCategory +" import");
        this.parent = pParent;
        this.theBookmarks = pBookmarks; 
        bookmarkCategory = pBookmarkCategory;
        initComponents();
        loadBookmarks();
    }
    

    // Variables declaration - do not modify
    private javax.swing.JPanel bookmarkPanel;
    private javax.swing.JButton btnAddBookmark;
    private javax.swing.JButton btnDeleteBookmark;
    private javax.swing.JButton btnEditBookmark;
    private javax.swing.JButton btnOK;
    private javax.swing.JPanel btnPanelBookmark;
    private javax.swing.JButton btnSetProxy;
    private javax.swing.JComboBox cmbProxyType;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbBookmarkTitle;
    private javax.swing.JLabel lbHostName;
    private javax.swing.JLabel lbPort;
    private javax.swing.JLabel lbProxyServer;
    private javax.swing.JLabel lbProxyTitle;
    private javax.swing.JLabel lbType;
    private javax.swing.JPanel proxyPanel;
    private javax.swing.JTextField tfHost;
    private javax.swing.JTextField tfPort;
	private JList bookmarkList;
    // End of variables declaration

    
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        proxyPanel = new javax.swing.JPanel();
        lbProxyTitle = new javax.swing.JLabel();
        lbProxyServer = new javax.swing.JLabel();
        lbType = new javax.swing.JLabel();
        lbHostName = new javax.swing.JLabel();
        lbPort = new javax.swing.JLabel();
        cmbProxyType = new javax.swing.JComboBox();
        tfHost = new javax.swing.JTextField();
        tfPort = new javax.swing.JTextField();
        btnSetProxy = new javax.swing.JButton();
        bookmarkPanel = new javax.swing.JPanel();
        lbBookmarkTitle = new javax.swing.JLabel();
        bookmarkList = new JList();
        jScrollPane1 = new javax.swing.JScrollPane(bookmarkList);        
        btnPanelBookmark = new javax.swing.JPanel();
        btnAddBookmark = new javax.swing.JButton();
        btnEditBookmark = new javax.swing.JButton();
        btnDeleteBookmark = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        proxyPanel.setLayout(new java.awt.GridBagLayout());

        proxyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Proxy Server"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        proxyPanel.add(lbProxyTitle, gridBagConstraints);

        lbProxyServer.setText("None");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 8, 0);
        proxyPanel.add(lbProxyServer, gridBagConstraints);

        lbType.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        proxyPanel.add(lbType, gridBagConstraints);

        lbHostName.setText("Host name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0;
        proxyPanel.add(lbHostName, gridBagConstraints);

        lbPort.setText("Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        proxyPanel.add(lbPort, gridBagConstraints);

        cmbProxyType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DIRECT", "HTTP", "SOCKS" }));
        cmbProxyType.setMinimumSize(new java.awt.Dimension(61, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 3);
        proxyPanel.add(cmbProxyType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        proxyPanel.add(tfHost, gridBagConstraints);

        tfPort.setMinimumSize(new java.awt.Dimension(50, 19));
        tfPort.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        proxyPanel.add(tfPort, gridBagConstraints);

        btnSetProxy.setText("Set");
        btnSetProxy.setMinimumSize(new java.awt.Dimension(63, 23));
        btnSetProxy.setPreferredSize(new java.awt.Dimension(63, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        proxyPanel.add(btnSetProxy, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 3, 0, 3);
        getContentPane().add(proxyPanel, gridBagConstraints);

        bookmarkPanel.setLayout(new java.awt.GridBagLayout());

        bookmarkPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bookmarks"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        bookmarkPanel.add(lbBookmarkTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        bookmarkPanel.add(jScrollPane1, gridBagConstraints);

        btnPanelBookmark.setLayout(new java.awt.GridBagLayout());

        btnAddBookmark.setText("Add");
        btnAddBookmark.setMinimumSize(new java.awt.Dimension(63, 23));
        btnAddBookmark.setPreferredSize(new java.awt.Dimension(63, 23));
        btnPanelBookmark.add(btnAddBookmark, new java.awt.GridBagConstraints());

        btnEditBookmark.setText("Edit");
        btnEditBookmark.setMinimumSize(new java.awt.Dimension(63, 23));
        btnEditBookmark.setPreferredSize(new java.awt.Dimension(63, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        btnPanelBookmark.add(btnEditBookmark, gridBagConstraints);

        btnDeleteBookmark.setText("Delete");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        btnPanelBookmark.add(btnDeleteBookmark, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        bookmarkPanel.add(btnPanelBookmark, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 3, 0, 3);
        getContentPane().add(bookmarkPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 88, 0, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        btnOK.setText("OK");
        jPanel2.add(btnOK);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 15, 0);
        getContentPane().add(jPanel2, gridBagConstraints);

        //btnEditBookmark.setVisible(false);
    	btnSetProxy.setEnabled(false);

	lbProxyServer.setText(getProxyServerString());

		btnEditBookmark.setEnabled(false);
		btnDeleteBookmark.setEnabled(false);
        
        // add event listeners
        btnOK.addActionListener(this);
        btnSetProxy.addActionListener(this);
        btnAddBookmark.addActionListener(this);
        btnEditBookmark.addActionListener(this);
        btnDeleteBookmark.addActionListener(this);
        
        bookmarkList.addListSelectionListener(this);
        
    	bookmarkList.setCellRenderer(new MyListCellRenderer());
    	bookmarkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     
    	cmbProxyType.addItemListener(this);
        //pack();
    }// </editor-fold>

    public void itemStateChanged(ItemEvent e) {
    	
    	String selectedItem = cmbProxyType.getSelectedItem().toString();
    	if (selectedItem.equalsIgnoreCase("DIRECT")) {
    		btnSetProxy.setEnabled(false);
    	}
    	else {
    		btnSetProxy.setEnabled(true);    		
    	}
    }
    
    
    // for test only
    public URLimportAdvancedDialog() {
    	this.setTitle("Advanced Setting for network import");

    	theBookmarks = getTestBookmarks();
    	initComponents();
        loadBookmarks();
    }
    
	// For test only, remove after test pass
	private Bookmarks getTestBookmarks()
	{
		Bookmarks tmpBookmarks = null;
		
		java.io.File tmpBookmarkFile = new java.io.File("bookmarks.xml");
		
    	// Load the Bookmarks object from given xml file  
   		try {
   			tmpBookmarks = BookmarksUtil.getBookmarks(tmpBookmarkFile.toURL());   			
   		}
    	catch (IOException e)
    	{
    		System.out.println("IOException -- bookmarkSource");
     	}
    	catch (JAXBException e)
    	{
    		System.out.println("JAXBException -- bookmarkSource");    
    	} 
    	catch (Exception e) {
    		System.out.println("Can not read the bookmark file, the bookmark file may not exist!");
    	}

    	return tmpBookmarks;
	}
 
    private void loadBookmarks()
    {	
    	List<DataSource> theDataSourceList = BookmarksUtil.getDataSourceList(bookmarkCategory, theBookmarks.getCategory());
	    
    	MyListModel theModel = new MyListModel(theDataSourceList);
    	bookmarkList.setModel(theModel);    	
    }
    
   	
    
 	public void actionPerformed(ActionEvent e)
 	{
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton)
		{
			JButton _btn = (JButton)_actionObject;

			if (_btn == btnOK) {				
				this.dispose();
			}
			else if (_btn == btnAddBookmark){
				BookmarkDialog theNewDialog = new BookmarkDialog(this, true, theBookmarks, bookmarkCategory, "new", null);
				theNewDialog.setSize(300, 250);
				theNewDialog.setLocationRelativeTo(this);

				theNewDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			}
			else if (_btn == btnEditBookmark){
				DataSource theDataSource = (DataSource) bookmarkList.getSelectedValue();
				BookmarkDialog theEditDialog = new BookmarkDialog(this, true, theBookmarks, bookmarkCategory, "edit", theDataSource);
				theEditDialog.setSize(300, 250);
				theEditDialog.setLocationRelativeTo(this);

				theEditDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			}
			else if (_btn == btnDeleteBookmark){
				DataSource theDataSource = (DataSource) bookmarkList.getSelectedValue();
				
				MyListModel theModel = (MyListModel) bookmarkList.getModel();
				theModel.removeElement(bookmarkList.getSelectedIndex());
							
				BookmarksUtil.deleteBookmark(theBookmarks, bookmarkCategory, theDataSource);
				
				if (theModel.getSize() == 0) {
					btnEditBookmark.setEnabled(false);
					btnDeleteBookmark.setEnabled(false);
				}
			}
			else if (_btn == btnSetProxy) {
				java.net.Proxy.Type proxyType = java.net.Proxy.Type.valueOf(cmbProxyType.getSelectedItem().toString());

				if (proxyType == java.net.Proxy.Type.DIRECT) {
					lbProxyServer.setText("None");
					return;
				}
				
				int thePort;
				Integer tmpInteger = new Integer(-1); 
				try {
					tmpInteger = new Integer(tfPort.getText().trim());
					thePort = tmpInteger.intValue();
				}
				catch (Exception exp) {
				    JOptionPane.showMessageDialog(this, "Port error!", "Warning", JOptionPane.INFORMATION_MESSAGE);
					return;					
				}

				CytoscapeInit.getProperties().setProperty("proxy.server",tfHost.getText().trim());
				CytoscapeInit.getProperties().setProperty("proxy.server.port",tmpInteger.toString());
				CytoscapeInit.getProperties().setProperty("proxy.server.type",proxyType.toString());

				Cytoscape.firePropertyChange(Cytoscape.PREFERENCES_UPDATED,null,null);
	
				lbProxyServer.setText(getProxyServerString());
			
			}
		}
 	}

	private String getProxyServerString() {
		Proxy p = ProxyHandler.getProxyServer();
		if ( p == null )
			return "None";
		else
			return p.toString();
	}
 	
 	
	/**
	 * Called by ListSelectionListener interface when a table item is selected.
	 * @param	pListSelectionEvent	
	 */
	public void valueChanged(ListSelectionEvent pListSelectionEvent)
	{
		if (bookmarkList.getSelectedIndex() == -1) { // nothing is selected
			btnEditBookmark.setEnabled(false);
			btnDeleteBookmark.setEnabled(false);						
		}
		else {
			// enable buttons
			btnEditBookmark.setEnabled(true);
			btnDeleteBookmark.setEnabled(true);			
		}

	}
    
    
    class MyListModel extends javax.swing.AbstractListModel {
    	List<DataSource> theDataSourceList = new ArrayList<DataSource>(0);
    	
    	public MyListModel(List<DataSource> pDataSourceList)
    	{
    		theDataSourceList = pDataSourceList;
    	}
    	
    	public int getSize() {
    		if (theDataSourceList == null) {
    			return 0;    			
    		}
    		return theDataSourceList.size();
    	}
    	
    	public Object getElementAt(int i)
    	{
    		if (theDataSourceList == null) {
    			return null;    			
    		}
    		return theDataSourceList.get(i);
    	}
    	
    	public void addElement(DataSource pDataSource) {
    		theDataSourceList.add(pDataSource);
    	}
    	
       	public void removeElement(int pIndex) {
    		theDataSourceList.remove(pIndex);
    		fireContentsChanged(this, pIndex, pIndex);
    	}
    	
    } //MyListModel
    
    
    //class MyListCellrenderer 
    class MyListCellRenderer extends JLabel implements ListCellRenderer {
        public MyListCellRenderer() {
            setOpaque(true);
        }
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
        	DataSource theDataSource = (DataSource) value;
            setText(theDataSource.getName());
            setToolTipText(theDataSource.getHref());
            setBackground(isSelected ? Color.red : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JDialog theDialog = new URLimportAdvancedDialog();
		theDialog.setPreferredSize(new Dimension(350,400));
		theDialog.pack();
		theDialog.setVisible(true);
	}

	
	public class BookmarkDialog extends JDialog implements ActionListener {
	    
		private String name;
		private String URLstr;
		private JDialog parent;
		private Bookmarks theBookmarks;
		private String categoryName;
		private URL bookmarkURL;
		private String mode = "new"; // new/edit
		private DataSource dataSource = null;
		
	    /** Creates new form NewBookmarkDialog */
	    public BookmarkDialog(JDialog parent, boolean modal, Bookmarks pBookmarks, String categoryName, String pMode, DataSource pDataSource) {
	        super(parent, modal);
	        this.parent = parent;
	        this.theBookmarks = pBookmarks;
	        this.categoryName = categoryName;
	        this.mode = pMode;
	        this.dataSource = pDataSource;
	        
	        initComponents();
	        
	        if (pMode.equalsIgnoreCase("new")) {
		        this.setTitle("Add new bookmark");	        	
	        }
	        if (pMode.equalsIgnoreCase("edit")) {
		        this.setTitle("Edit bookmark");	
				tfName.setText(dataSource.getName());
				tfName.setEditable(false);
				tfURL.setText(dataSource.getHref());
	        }
	    }
	    
	 	public void actionPerformed(ActionEvent e)
	 	{
			Object _actionObject = e.getSource();

			// handle Button events
			if (_actionObject instanceof JButton)
			{
				JButton _btn = (JButton)_actionObject;

				if ((_btn == btnOK)&&(mode.equalsIgnoreCase("new"))) {
						
					name = tfName.getText();
					URLstr = tfURL.getText();
					
					if (name.trim().equals("")||URLstr.trim().equals("")) {
						String msg = "Please provide a name/URL!";
					    // display info dialog
					    JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.INFORMATION_MESSAGE);
					    return;
					}					
					
					DataSource theDataSource = new DataSource();
					theDataSource.setName(name);
					theDataSource.setHref(URLstr);
										
					if (BookmarksUtil.isInBookmarks(bookmarkURL, categoryName, theDataSource)) {
						String msg = "Bookmark already existed!";
					    // display info dialog
					    JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.INFORMATION_MESSAGE);
					    return;
					}
										
					BookmarksUtil.saveBookmark(theBookmarks, categoryName,theDataSource);
					this.dispose();
				}
				if ((_btn == btnOK)&&(mode.equalsIgnoreCase("edit"))) {

					name = tfName.getText();
					URLstr = tfURL.getText();

					
					if (URLstr.trim().equals("")) {
						String msg = "URL is empty!";
					    // display info dialog
					    JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.INFORMATION_MESSAGE);
					    return;
					}					
					
					DataSource theDataSource = new DataSource();
					theDataSource.setName(name);
					theDataSource.setHref(URLstr);
										
					// first dellete the old one, then add (note: name is key of DataSource)
					BookmarksUtil.deleteBookmark(theBookmarks, bookmarkCategory, theDataSource);										
					BookmarksUtil.saveBookmark(theBookmarks, categoryName,theDataSource);
					this.dispose();
				}
				
				else if (_btn == btnCancel){
					this.dispose();
				}
			}
	 	} //End of actionPerformed()
	    
	 	
	    /** This method is called from within the constructor to
	     * initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is
	     * always regenerated by the Form Editor.
	     */
	    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	    private void initComponents() {
	        java.awt.GridBagConstraints gridBagConstraints;

	        lbName = new javax.swing.JLabel();
	        tfName = new javax.swing.JTextField();
	        lbURL = new javax.swing.JLabel();
	        tfURL = new javax.swing.JTextField();
	        jPanel1 = new javax.swing.JPanel();
	        btnOK = new javax.swing.JButton();
	        btnCancel = new javax.swing.JButton();

	        getContentPane().setLayout(new java.awt.GridBagLayout());

	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	        lbName.setText("Name");
	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
	        getContentPane().add(lbName, gridBagConstraints);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        gridBagConstraints.weightx = 1.0;
	        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
	        getContentPane().add(tfName, gridBagConstraints);

	        lbURL.setText("URL");
	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
	        getContentPane().add(lbURL, gridBagConstraints);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
	        getContentPane().add(tfURL, gridBagConstraints);

	        btnOK.setText("OK");
	        btnOK.setPreferredSize(new java.awt.Dimension(65, 23));
	        jPanel1.add(btnOK);

	        btnCancel.setText("Cancel");
	        jPanel1.add(btnCancel);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = 0;
	        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
	        getContentPane().add(jPanel1, gridBagConstraints);

	        btnOK.addActionListener(this);
	        btnCancel.addActionListener(this);
	        
	        pack();
	    }// </editor-fold>
	    	    
	    // Variables declaration - do not modify
	    private javax.swing.JButton btnCancel;
	    private javax.swing.JButton btnOK;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JLabel lbName;
	    private javax.swing.JLabel lbURL;
	    private javax.swing.JTextField tfName;
	    private javax.swing.JTextField tfURL;
	    // End of variables declaration
	}
}

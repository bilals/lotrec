package cytoscape.dialogs.preferences;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.DataSource;
import cytoscape.util.BookmarksUtil;

public class BookmarkDialog extends JDialog implements ActionListener,
		ListSelectionListener, ItemListener {

	private String bookmarkCategory;
	private Bookmarks theBookmarks = null;
	// private Category theCategory = new Category();;
	private String[] bookmarkCategories = { "network", "annotation" };

	// private URL bookmarkURL;

	/**
	 * Creates new BookmarkDialog
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	public BookmarkDialog(JFrame pParent) throws JAXBException, IOException {
		super(pParent, true);
		this.setTitle("Bookmark manager");

		initComponents();
		bookmarkCategory = cmbCategory.getSelectedItem().toString();
		theBookmarks = Cytoscape.getBookmarks();
		loadBookmarks();

		setSize(new Dimension(350, 250));
		this.setLocationRelativeTo(pParent);
	}

	// Variables declaration - do not modify
	private javax.swing.JButton btnAddBookmark;
	private javax.swing.JButton btnDeleteBookmark;
	private javax.swing.JButton btnEditBookmark;
	private javax.swing.JButton btnOK;
	private javax.swing.JComboBox cmbCategory;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	// private javax.swing.JLabel lbTitle;
	private javax.swing.JList listBookmark;

	// End of variables declaration

	private void initComponents() {

		java.awt.GridBagConstraints gridBagConstraints;

		// lbTitle = new javax.swing.JLabel();
		cmbCategory = new javax.swing.JComboBox();
		jScrollPane1 = new javax.swing.JScrollPane();
		listBookmark = new javax.swing.JList();
		jPanel1 = new javax.swing.JPanel();
		btnAddBookmark = new javax.swing.JButton();
		btnEditBookmark = new javax.swing.JButton();
		btnDeleteBookmark = new javax.swing.JButton();
		btnOK = new javax.swing.JButton();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// lbTitle.setText("Title");
		// getContentPane().add(lbTitle, new java.awt.GridBagConstraints());

		cmbCategory.setToolTipText("Bookmark category");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
		getContentPane().add(cmbCategory, gridBagConstraints);

		jScrollPane1.setViewportView(listBookmark);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
		getContentPane().add(jScrollPane1, gridBagConstraints);

		jPanel1.setLayout(new java.awt.GridBagLayout());

		btnAddBookmark.setText("Add");
		btnAddBookmark.setToolTipText("Add a new bookmark");
		btnAddBookmark.setPreferredSize(new java.awt.Dimension(63, 25));
		jPanel1.add(btnAddBookmark, new java.awt.GridBagConstraints());

		btnEditBookmark.setText("Edit");
		btnEditBookmark.setToolTipText("Edit a bookmark");
		btnEditBookmark.setMaximumSize(new java.awt.Dimension(63, 25));
		btnEditBookmark.setMinimumSize(new java.awt.Dimension(63, 25));
		btnEditBookmark.setPreferredSize(new java.awt.Dimension(63, 25));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
		jPanel1.add(btnEditBookmark, gridBagConstraints);

		btnDeleteBookmark.setText("Delete");
		btnDeleteBookmark.setToolTipText("Delete a bookmark");
		// btnDeleteBookmark.setMaximumSize(new java.awt.Dimension(63, 25));
		// btnDeleteBookmark.setMinimumSize(new java.awt.Dimension(63, 25));
		// btnDeleteBookmark.setPreferredSize(new java.awt.Dimension(, 25));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		jPanel1.add(btnDeleteBookmark, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
		getContentPane().add(jPanel1, gridBagConstraints);

		btnOK.setText("OK");
		btnOK.setToolTipText("Close Bookmark dialog");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
		getContentPane().add(btnOK, gridBagConstraints);

		for (String AnItem : bookmarkCategories) {
			cmbCategory.addItem(AnItem);
		}
		cmbCategory.addItemListener(this);

		btnEditBookmark.setEnabled(false);
		btnDeleteBookmark.setEnabled(false);

		// add event listeners
		btnOK.addActionListener(this);
		btnAddBookmark.addActionListener(this);
		btnEditBookmark.addActionListener(this);
		btnDeleteBookmark.addActionListener(this);

		listBookmark.addListSelectionListener(this);

		listBookmark.setCellRenderer(new MyListCellRenderer());
		listBookmark.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// pack();
	}// </editor-fold>

	private void loadBookmarks() {
		List<DataSource> theDataSourceList = BookmarksUtil.getDataSourceList(
				bookmarkCategory, theBookmarks.getCategory());

		MyListModel theModel = new MyListModel(theDataSourceList);
		listBookmark.setModel(theModel);
	}

	public void itemStateChanged(ItemEvent e) {
		bookmarkCategory = cmbCategory.getSelectedItem().toString();
		loadBookmarks();
	}

	public void actionPerformed(ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			if (_btn == btnOK) {
				this.dispose();
			} else if (_btn == btnAddBookmark) {
				EditBookmarkDialog theNewDialog = new EditBookmarkDialog(this,
						true, theBookmarks, bookmarkCategory, "new", null);
				theNewDialog.setSize(300, 250);
				theNewDialog.setLocationRelativeTo(this);

				theNewDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			} else if (_btn == btnEditBookmark) {
				DataSource theDataSource = (DataSource) listBookmark
						.getSelectedValue();
				EditBookmarkDialog theEditDialog = new EditBookmarkDialog(this,
						true, theBookmarks, bookmarkCategory, "edit",
						theDataSource);
				theEditDialog.setSize(300, 250);
				theEditDialog.setLocationRelativeTo(this);

				theEditDialog.setVisible(true);
				loadBookmarks(); // reload is required to update the GUI
			} else if (_btn == btnDeleteBookmark) {
				DataSource theDataSource = (DataSource) listBookmark
						.getSelectedValue();

				MyListModel theModel = (MyListModel) listBookmark.getModel();
				theModel.removeElement(listBookmark.getSelectedIndex());

				BookmarksUtil.deleteBookmark(theBookmarks, bookmarkCategory,
						theDataSource);

				if (theModel.getSize() == 0) {
					btnEditBookmark.setEnabled(false);
					btnDeleteBookmark.setEnabled(false);
				}
			}
		}
	}

	/**
	 * Called by ListSelectionListener interface when a table item is selected.
	 * 
	 * @param pListSelectionEvent
	 */
	public void valueChanged(ListSelectionEvent pListSelectionEvent) {
		if (listBookmark.getSelectedIndex() == -1) { // nothing is selected
			btnEditBookmark.setEnabled(false);
			btnDeleteBookmark.setEnabled(false);
		} else {
			// enable buttons
			btnEditBookmark.setEnabled(true);
			btnDeleteBookmark.setEnabled(true);
		}

	}

	class MyListModel extends javax.swing.AbstractListModel {
		List<DataSource> theDataSourceList = new ArrayList<DataSource>(0);

		public MyListModel(List<DataSource> pDataSourceList) {
			theDataSourceList = pDataSourceList;
		}

		public int getSize() {
			if (theDataSourceList == null) {
				return 0;
			}
			return theDataSourceList.size();
		}

		public Object getElementAt(int i) {
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

	} // MyListModel

	// class MyListCellrenderer
	class MyListCellRenderer extends JLabel implements ListCellRenderer {
		public MyListCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			DataSource theDataSource = (DataSource) value;
			setText(theDataSource.getName());
			setToolTipText(theDataSource.getHref());
			setBackground(isSelected ? Color.red : Color.white);
			setForeground(isSelected ? Color.white : Color.black);
			return this;
		}
	}

	public class EditBookmarkDialog extends JDialog implements ActionListener {

		private String name;
		private String URLstr;
		private JDialog parent;
		private Bookmarks theBookmarks;
		private String categoryName;
		private URL bookmarkURL;
		private String mode = "new"; // new/edit
		private DataSource dataSource = null;

		/** Creates new form NewBookmarkDialog */
		public EditBookmarkDialog(JDialog parent, boolean modal,
				Bookmarks pBookmarks, String categoryName, String pMode,
				DataSource pDataSource) {
			super(parent, modal);
			this.parent = parent;
			this.theBookmarks = pBookmarks;
			this.categoryName = categoryName;
			this.mode = pMode;
			this.dataSource = pDataSource;

			initComponents();

			lbCategoryValue.setText(categoryName);
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

		public void actionPerformed(ActionEvent e) {
			Object _actionObject = e.getSource();

			// handle Button events
			if (_actionObject instanceof JButton) {
				JButton _btn = (JButton) _actionObject;

				if ((_btn == btnOK) && (mode.equalsIgnoreCase("new"))) {

					name = tfName.getText();
					URLstr = tfURL.getText();

					if (name.trim().equals("") || URLstr.trim().equals("")) {
						String msg = "Please provide a name/URL!";
						// display info dialog
						JOptionPane.showMessageDialog(parent, msg, "Warning",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					DataSource theDataSource = new DataSource();
					theDataSource.setName(name);
					theDataSource.setHref(URLstr);

					if (BookmarksUtil.isInBookmarks(bookmarkURL, categoryName,
							theDataSource)) {
						String msg = "Bookmark already existed!";
						// display info dialog
						JOptionPane.showMessageDialog(parent, msg, "Warning",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					BookmarksUtil.saveBookmark(theBookmarks, categoryName,
							theDataSource);
					this.dispose();
				}
				if ((_btn == btnOK) && (mode.equalsIgnoreCase("edit"))) {

					name = tfName.getText();
					URLstr = tfURL.getText();

					if (URLstr.trim().equals("")) {
						String msg = "URL is empty!";
						// display info dialog
						JOptionPane.showMessageDialog(parent, msg, "Warning",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					DataSource theDataSource = new DataSource();
					theDataSource.setName(name);
					theDataSource.setHref(URLstr);

					// first dellete the old one, then add (note: name is key of
					// DataSource)
					BookmarksUtil.deleteBookmark(theBookmarks,
							bookmarkCategory, theDataSource);
					BookmarksUtil.saveBookmark(theBookmarks, categoryName,
							theDataSource);
					this.dispose();
				}

				else if (_btn == btnCancel) {
					this.dispose();
				}
			}
		} // End of actionPerformed()

		/**
		 * This method is called from within the constructor to initialize the
		 * form. WARNING: Do NOT modify this code. The content of this method is
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
			lbCategory = new javax.swing.JLabel();
			lbCategoryValue = new javax.swing.JLabel();

			getContentPane().setLayout(new java.awt.GridBagLayout());

			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			lbName.setText("Name:");
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridy = 1;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentPane().add(lbName, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentPane().add(tfName, gridBagConstraints);

			lbURL.setText("URL:");
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentPane().add(lbURL, gridBagConstraints);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentPane().add(tfURL, gridBagConstraints);

			btnOK.setText("OK");
			btnOK.setPreferredSize(new java.awt.Dimension(65, 23));
			jPanel1.add(btnOK);

			btnCancel.setText("Cancel");
			jPanel1.add(btnCancel);

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
			getContentPane().add(jPanel1, gridBagConstraints);

			lbCategory.setText("Category:");
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 0);
			getContentPane().add(lbCategory, gridBagConstraints);

			lbCategoryValue.setText("network");
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 0);
			getContentPane().add(lbCategoryValue, gridBagConstraints);

			btnOK.addActionListener(this);
			btnCancel.addActionListener(this);

			pack();
		}// </editor-fold>

		// Variables declaration - do not modify
		private javax.swing.JButton btnCancel;
		private javax.swing.JButton btnOK;
		private javax.swing.JPanel jPanel1;
		private javax.swing.JLabel lbCategory;
		private javax.swing.JLabel lbCategoryValue;
		private javax.swing.JLabel lbName;
		private javax.swing.JLabel lbURL;
		private javax.swing.JTextField tfName;
		private javax.swing.JTextField tfURL;
		// End of variables declaration
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JDialog theDialog = new BookmarkDialog();
		theDialog.setPreferredSize(new Dimension(350, 400));
		theDialog.pack();
		theDialog.setVisible(true);
	}

	// for test only
	public BookmarkDialog() {
		this.setTitle("Bookmark manager");

		theBookmarks = getTestBookmarks();
		initComponents();
		bookmarkCategory = cmbCategory.getSelectedItem().toString();
		// theBookmarks = Cytoscape.getBookmarks();

		loadBookmarks();
	}

	// For test only, remove after test pass
	private Bookmarks getTestBookmarks() {
		Bookmarks tmpBookmarks = null;

		java.io.File tmpBookmarkFile = new java.io.File("bookmarks_kei.xml");
		System.out.println("tmpBookmarkFile ="
				+ tmpBookmarkFile.getAbsolutePath());

		// Load the Bookmarks object from given xml file
		try {
			tmpBookmarks = BookmarksUtil.getBookmarks(tmpBookmarkFile.toURL());
		} catch (IOException e) {
			System.out.println("IOException -- bookmarkSource");
		} catch (JAXBException e) {
			System.out.println("JAXBException -- bookmarkSource");
		} catch (Exception e) {
			System.out
					.println("Can not read the bookmark file, the bookmark file may not exist!");
		}

		return tmpBookmarks;
	}

}


/*
  File: IconPopupButton.java 
  
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

// IconPopupButton.java


//---------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;

//---------------------------------------------------------------------------------------

public class IconPopupButton extends JPanel implements ActionListener {

    String title;                    // title of the button
    String objectName;
    String currentIconName;             // internal storage of node icon type
    Object currentIcon;         // and here as a icon byte
    JButton iconButton;             // the button on the left
    JLabel iconSelectionForPanel;   // the icon type displayed on the right
    JFrame mainFrame;
    JDialog mainDialog;
    JPanel mainPanel;
    HashMap iconObjectToString;
    HashMap stringToIconObject;
    JList iconList;          // ... and the equivalent list for display
    JDialog parentDialog;
    boolean alreadyConstructed;

    public IconPopupButton (String title,
			    String objectName,
			    HashMap iconObjectToString,
			    HashMap stringToIconObject,
			    ImageIcon [] icons,
			    Object startIconObject,
			    JDialog parentDialog){
	this.title = title;
	if(icons.length==0)
	    { setupErrorWindow(); return;}
	if(icons[0]==null || icons[0].getIconWidth()<0)
	    { System.out.println(icons[0].getImage().getWidth(parentDialog)); setupErrorWindow();  return; }

	alreadyConstructed = false;
	this.objectName = objectName;
	this.iconObjectToString = iconObjectToString;
	this.stringToIconObject = stringToIconObject;
	this.parentDialog = parentDialog;
	iconList = new JList (icons);
	if(startIconObject!=null)
	    this.setIconObject(startIconObject);
	else {
	    System.out.println("starticon null " + title);
	    ImageIcon icon = (ImageIcon)iconList.getModel().getElementAt(0); // default icon
	    setIconName(icon.getDescription());
	}

	setupWindow();
    }

    private void setupWindow(){
	// find the right icon
	ListModel theModel = iconList.getModel();
	ImageIcon icon = (ImageIcon)theModel.getElementAt(0); // default icon
	int modelSize = theModel.getSize();
	for (int modelIndex = 0; modelIndex < modelSize; modelIndex++) {
	    ImageIcon indexedIcon = (ImageIcon)theModel.getElementAt(modelIndex);
	    if(currentIconName == indexedIcon.getDescription()) {
		icon = indexedIcon;
		iconList.setSelectedValue(icon,true);
	    }
	}
	iconButton = new JButton(title);
	iconButton.addActionListener(this);
	mainPanel = new JPanel(new GridLayout(0,1));
	iconSelectionForPanel = new JLabel(icon);
	add(iconButton);
	add(iconSelectionForPanel);
    }

    private void setupErrorWindow() {
	iconButton = new JButton(title + ": CYTOSCAPE_HOME error");
	iconSelectionForPanel = new JLabel(" X ");
	JLabel errorLabel = new JLabel("ERROR: No CYTOSCAPE_HOME specified on java command line.");
	add(errorLabel);
    }

    // if button is pressed, launch window with list of choices
    public void actionPerformed(ActionEvent e){
	if(!alreadyConstructed) {
	mainDialog = new JDialog(parentDialog, this.title);

	// create buttons
        final JButton setButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");
	setButton.addActionListener    (new ApplyIconAction());
	cancelButton.addActionListener (new CancelIconAction());

	// create list
	iconList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	iconList.setVisibleRowCount(1);
	//iconList.setFixedCellHeight(35);
	//iconList.setFixedCellWidth(35);
	iconList.setBackground(Color.WHITE);
	iconList.setSelectionBackground(Color.RED);
	iconList.setSelectionForeground(Color.RED);
	iconList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        iconList.addMouseListener( new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    if (e.getClickCount() == 2) setButton.doClick();
		}
	    });
        JScrollPane listScroller = new JScrollPane(iconList) ;
        listScroller.setPreferredSize(new Dimension(150, 50));
        listScroller.setMinimumSize(new Dimension(150,50));
	listScroller.setAlignmentX(LEFT_ALIGNMENT);
	listScroller.setAlignmentY(BOTTOM_ALIGNMENT);
	iconList.ensureIndexIsVisible(iconList.getSelectedIndex());

	// Create a container so that we can add a title around the scroll pane
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Set " + objectName);
        label.setLabelFor(iconList);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	// Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);
        buttonPane.add(cancelButton);

	// add everything
	mainPanel.add(listPane, BorderLayout.CENTER);
	mainPanel.add(buttonPane, BorderLayout.SOUTH);
	mainDialog.setContentPane (mainPanel);
	alreadyConstructed = true;
	}

	mainDialog.pack ();
	mainDialog.setLocationRelativeTo (parentDialog);
  	mainDialog.setVisible(true);
    }

    public class ApplyIconAction extends AbstractAction{
	public void actionPerformed(ActionEvent e){
	    //setIcon((String) iconList.getSelectedValue());
	    ImageIcon icon = (ImageIcon) iconList.getSelectedValue();
	    setIconName(icon.getDescription());
	    iconSelectionForPanel.setIcon(icon);
	    mainDialog.dispose();
	}
    }
    public class CancelIconAction extends AbstractAction{
	CancelIconAction(){super ("");}
	public void actionPerformed (ActionEvent e){
	    mainDialog.dispose();
	}
    }
    public String getIconName() {
	return currentIconName;
    }
    public Object getIconObject() {
	return currentIcon;
    }
    public void setIconName(String iconName) {
	currentIconName = iconName;
	currentIcon = (Object) stringToIconObject.get(iconName);
    }
    public void setIconObject(Object iconObject) {
	currentIcon = iconObject;
	currentIconName = (String) iconObjectToString.get(iconObject);
    }
    public JLabel getLabel() {
	return iconSelectionForPanel;
    }
    public JButton getButton() {
	return iconButton;
    }
}//class IconPopupButton



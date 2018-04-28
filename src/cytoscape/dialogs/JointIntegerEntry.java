
/*
  File: JointIntegerEntry.java 
  
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

// JointIntegerEntry.java


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
import cytoscape.dialogs.IntegerEntryField;
//---------------------------------------------------------------------------------------

public class JointIntegerEntry extends JPanel {

    IntegerEntryField field1,field2;
    JLabel constraintLabel;
    JCheckBox constraintBox;
    JFrame mainFrame;
    JPanel mainPanel;
    String field1Name, field2Name, fieldName;
    boolean locked;
    
    public JointIntegerEntry (String fieldName,
			      String field1Name,
			      String field2Name,
			      int defaultValue1,
			      int defaultValue2,
			      int maxValue1,
			      int maxValue2) {
	super(new GridLayout(1,2,10,10));
	
	this.field1Name = field1Name;
	this.field2Name = field2Name;
	this.fieldName = fieldName;
	
	String fullName1 = fieldName + " " + field1Name;
	field1 = new IntegerEntryField(fullName1,defaultValue1,maxValue1);
	String fullName2 = fieldName + " " + field2Name;
	field2 = new IntegerEntryField(fullName2,defaultValue2,maxValue2);
	String constrString = "Lock " + fullName1 + " to " + field2Name + "?";
	constraintLabel = new JLabel(constrString);
	locked=false;
	if(defaultValue1 == defaultValue2) locked=true;
	constraintBox = new JCheckBox("",locked);
	constraintBox.addItemListener(new LockedItemListener());
    
	field1.getField().addFocusListener(new CopyField1To2Listener());
	whetherField2IsEnabled();
    }
    
    public Integer getInteger(String whichField) {
	if(whichField.equalsIgnoreCase(field1Name))
	    return field1.getInteger();
	else return field2.getInteger();
    }
    public void setInteger(String whichField, Integer newVal) {
	if(whichField.equalsIgnoreCase(field1Name))
	    field1.setInteger(newVal);
	else field2.setInteger(newVal);
    }
    public int getInt(String whichField) {
	if(whichField.equalsIgnoreCase(field1Name))
	    return field1.getInt();
	else return field2.getInt();
    }
    public void setInt(String whichField, int newVal) { 
	if(whichField.equalsIgnoreCase(field1Name))
	    field1.setInt(newVal);
	else field2.setInt(newVal);
    }
    
    public class LockedItemListener implements ItemListener {
	public void itemStateChanged (ItemEvent e) {
	    JCheckBox jcb = (JCheckBox)e.getItem();
	    locked = jcb.isSelected();
	    if(locked) copy1to2();
	    whetherField2IsEnabled();
	}
    }

    public void whetherField2IsEnabled() {
	field2.getLabel().setEnabled(!locked);
	field2.getField().setEnabled(!locked);
    }

    public class CopyField1To2Listener implements FocusListener { 
	public void focusGained (FocusEvent e) {
	    if(locked) copy1to2();
	}
	public void focusLost (FocusEvent e) {
	    if(locked) copy1to2();
	}
    }
    private void copy1to2() {
	field2.setInt(field1.getInt());
    }
    public JTextField getField(String whichField) {
	if(whichField.equalsIgnoreCase(field1Name))
	    return field1.getField();
	else return field2.getField();
    }
    public JLabel getLabel(String whichField) {
	if(whichField.equalsIgnoreCase(field1Name))
	    return field1.getLabel();
	else return field2.getLabel();
    }
    public JCheckBox getConstraintBox() {
	return constraintBox;
    }
    public JLabel getConstraintLabel() {
	return constraintLabel;
    }
    
}//class JointIntegerEntry



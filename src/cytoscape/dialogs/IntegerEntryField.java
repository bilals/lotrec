
/*
  File: IntegerEntryField.java 
  
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

// IntegerEntryField.java


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

public class IntegerEntryField extends JPanel {
    
    JTextField sizeField;
    JLabel sizeLabel;
    JFrame mainFrame;
    JPanel mainPanel;
    
    public IntegerEntryField (String fieldName, int defaultValue, int maxValue) {
	super(new GridLayout(1,2,10,10));
	
	sizeLabel = new JLabel(fieldName);
	sizeField = new JTextField(Integer.toString(defaultValue));
	sizeField.addFocusListener(new PositiveIntegerListener(defaultValue,maxValue));
	
	add(sizeLabel);
	add(sizeField);
    }
    
    public Integer getInteger() { return new Integer(sizeField.getText()); }
    public void setInteger(Integer newVal) { sizeField.setText(newVal.toString()); }
    public int getInt() { return Integer.parseInt(sizeField.getText()); }
    public void setInt(int newVal) { sizeField.setText(Integer.toString(newVal)); }
    
    public class PositiveIntegerListener implements FocusListener { 
	private int digits, maxval, defaultVal;
	private String maxvalString, defaultString;
	
	public PositiveIntegerListener(int defaultVal, int maxval) {
	    super();
	    this.maxval = maxval;
	    this.defaultVal = defaultVal;
	    this.maxvalString = Integer.toString(maxval);
	    this.defaultString = Integer.toString(defaultVal);
	    this.digits = this.maxvalString.length();
	}
	public void focusGained (FocusEvent e) {
	    //System.out.println("gained");
	    validate((JTextField)e.getSource());
	}
	public void focusLost (FocusEvent e) {
	    //System.out.println("lost");
	    validate((JTextField)e.getSource());
	}
	private void validate(JTextField field) {
	    String fieldStr = field.getText();
	    fieldStr = fieldStr.replaceAll("[^0-9]",""); // ditch all non-numeric
	    if(fieldStr.length()>0) {
		if(fieldStr.length()>digits) {
		    field.setText(maxvalString);
		}
		else {
		    //System.out.println(" length " + fieldStr.length());
		    try {
			int val = Integer.parseInt(fieldStr);
			if(val<=0) {
			    field.setText(defaultString);
			}
			else if(val>maxval) {
			    field.setText(maxvalString);
			}
			else {
			    field.setText(fieldStr);
			}
		    }
		    catch (NumberFormatException nfe) {
			System.out.println("Not an integer: " + fieldStr);
			field.setText(defaultString);
		    }
		}
	    }  // if gt 0
	    else {
		field.setText(defaultString);
	    }  // if gt 0 (else)
	}
	
    } // PositiveIntegerListener
    public JTextField getField() {
	return sizeField;
    }
    public JLabel getLabel() {
	return sizeLabel;
    }
    
}//class IntegerEntryField



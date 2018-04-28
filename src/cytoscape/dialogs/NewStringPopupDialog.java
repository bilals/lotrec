
/*
  File: NewStringPopupDialog.java 
  
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

// NewStringPopupDialog.java:  the name says it all.


//--------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//--------------------------------------------------------------------------------------

package cytoscape.dialogs;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *  NewStringPopupDialog: a dialog that is used for creating a
 *  new string.  Use the getString() method to retrieve the string.
 *
 */

public class NewStringPopupDialog extends JDialog {
    
    private String theString; 
    JTextField textField;
    public NewStringPopupDialog(Frame parent, String title) {
	super(parent,true);
	setTitle(title);
	textField = new JTextField();
	textField.setPreferredSize(new Dimension(200,25));
	theString=null;
	JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");
	okButton.addActionListener(new OkAction());
	cancelButton.addActionListener(new CancelAction());
	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout(); 
	GridBagConstraints c = new GridBagConstraints();
	panel.setLayout (gridbag);
	c.gridx=0;
	c.gridy=0;
	c.gridwidth=2;
	gridbag.setConstraints(textField,c);
	panel.add (textField);
	c.gridx=0;
	c.gridy=1;
	c.gridwidth=1;
	gridbag.setConstraints(okButton,c);
	panel.add (okButton);
	c.gridx=1;
	c.gridy=1;
	gridbag.setConstraints(cancelButton,c);
	panel.add (cancelButton);
	setContentPane(panel);
	pack ();
	setLocationRelativeTo (parent);
	setVisible (true);
    }
    public String getString() {
	if(theString!=null)
	    return theString.trim();
	else
	    return null;
    }
    
    public class OkAction extends AbstractAction {
	OkAction () {
	    super ("");
	}
	public void actionPerformed (ActionEvent e) {
	    theString = textField.getText();
	    NewStringPopupDialog.this.dispose();
	}
    }

    public class CancelAction extends AbstractAction {
	CancelAction () {
	    super ("");
	}
	public void actionPerformed (ActionEvent e) {
	    NewStringPopupDialog.this.dispose();
	}
    }
}




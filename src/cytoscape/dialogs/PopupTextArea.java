
/*
  File: PopupTextArea.java 
  
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

// PopupTextArea


//----------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.*;
//-------------------------------------------------------------------------------------
public class PopupTextArea extends JDialog {
  PopupTextArea popupTextArea;
    Point location;
//-------------------------------------------------------------------------------------
public PopupTextArea (JDialog parent, String title, String text) {

  super (parent, false);
  location = parent.getLocationOnScreen();
  init (title, text);
}
//-------------------------------------------------------------------------------------
public PopupTextArea (Frame parent, String title, String text)
{
  super (parent, false);
  location = parent.getLocationOnScreen();
  init (title, text);

}
//-------------------------------------------------------------------------------------
private void init (String title, String text)
{
  setTitle (title);
  popupTextArea = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  final JTextArea textArea = new JTextArea (text);
  textArea.setEditable (false);
  JScrollPane scrollPane = new JScrollPane (textArea);
  //textArea.setPreferredSize (new Dimension (600, 400));
  //scrollPane.setPreferredSize (new Dimension (600, 400));
  panel.setPreferredSize (new Dimension (600, 400));
  panel.add (scrollPane, BorderLayout.CENTER);


  JPanel buttonPanel = new JPanel ();
  JButton okButton = new JButton ("OK");
  okButton.addActionListener (new OKAction ());
  buttonPanel.add (okButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);
  setContentPane (panel);
  setLocation(location);
  pack ();
  setVisible (true);

} // PopupTextArea ctor
//------------------------------------------------------------------------------------
public class OKAction extends AbstractAction {

  OKAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    popupTextArea.dispose ();
    }

} // QuitAction
//-----------------------------------------------------------------------------------
} // class PopupTextArea



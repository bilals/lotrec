
/*
  File: GeneralColorDialogListener.java 
  
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

// GeneralColorDialogListener.java:  listener for mutable colors


//--------------------------------------------------------------------------------------
// $Revision: 7761 $
// $Date: 2006-06-27 13:47:58 -0700 (Tue, 27 Jun 2006) $
// $Author: mes $
//--------------------------------------------------------------------------------------

package cytoscape.dialogs;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import cytoscape.util.MutableColor;
//import javax.swing.event.*;
//import javax.swing.colorchooser.*;
//import java.awt.Color;

/**
 *  GeneralColorDialogListener: a listener that pops up and
 *  makes useful a JColorChooser.
 *  
 *  The GeneralColorDialogListener is a listener that might be
 *  added, for example, to a button.  The result of an ActionEvent
 *  is that the current color in the MutableColor (argument to the
 *  constructor) will be used as the starting point for a
 *  JColorChooser, and if the user selects a new color with the
 *  JColorChooser, that new color is written to the color of the
 *  MutableColor.  The parent Component is also passed to the
 *  constructor so that it may be passed on to the JColorChooser's
 *  constructor.
 * @deprecated No one uses this, so don't start.  Will be removed 12/2006.
 * If you need a JColorChooser, use CyColorChooser instead.
 */

public class GeneralColorDialogListener implements ActionListener {
    private MutableColor returnColor;
    private String title;
    private Component component;
    private JLabel label;
    public GeneralColorDialogListener(Component component, MutableColor writeToThisColor, String title) {
	super ();
	returnColor = writeToThisColor;
	this.component = component;
	this.title = title;
	this.label = null;
    }
    public GeneralColorDialogListener(Component component, MutableColor writeToThisColor, JLabel label, String title) {
	super ();
	returnColor = writeToThisColor;
	this.component = component;
	this.title = title;
	this.label = label;
    }
    
    public void actionPerformed(ActionEvent e) {
	popup();
    }

    public void popup() {
	Color tempColor = JColorChooser.showDialog(component,
						   title,
						   returnColor.getColor());
	if (tempColor != null) {
	    returnColor.setColor(tempColor);
	    if (label != null) {
		label.setBackground(tempColor);
	    }
	}
    }
}




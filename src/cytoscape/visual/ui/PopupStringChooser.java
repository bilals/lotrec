
/*
 File: PopupStringChooser.java 
 
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

package cytoscape.visual.ui;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalButtonUI;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.LabelPosition;

/** An input dialog for strings, ints, and doubles. */ 
public class PopupStringChooser {

	public static Object showDialog(Component parent, String title, String prompt, 
	                                Object input, byte type) { 
		// keep prompting for input until a valid input is received
		inputLoop: while (true) {
			String ret = (String) JOptionPane.showInputDialog(parent,
					prompt, title, JOptionPane.QUESTION_MESSAGE, null,
					null, input);
			if (ret == null) {
				return null;
			} else {
				switch (type) {
				case ValueDisplayer.DOUBLE:
					try {
						input = new Double(Double.parseDouble(ret));
						break inputLoop; 
					} catch (NumberFormatException exc) {
	        				JOptionPane.showMessageDialog(parent, 
							"That is not a valid double", 
							"Bad Input", JOptionPane.ERROR_MESSAGE);
						continue inputLoop;
					}
				case ValueDisplayer.INT:
					try {
						input = new Integer(Integer.parseInt(ret));
						break inputLoop;
					} catch (NumberFormatException exc) {
	        				JOptionPane.showMessageDialog(parent, 
							"That is not a valid integer", 
							"Bad Input", JOptionPane.ERROR_MESSAGE);
						continue inputLoop;
					}
				default: // simple string assignment
					input = ret;
					break inputLoop;
				}
			}
		}
		return input;
	}

}

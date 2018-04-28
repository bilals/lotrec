
/*
  File: FontChooser.java 
  
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

//--------------------------------------------------------------------------
// $Revision: 9217 $
// $Date: 2006-12-18 15:36:17 -0800 (Mon, 18 Dec 2006) $
// $Author: mes $
//--------------------------------------------------------------------------
package cytoscape.visual.ui;
//--------------------------------------------------------------------------
import java.awt.*;
import java.awt.font.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
//--------------------------------------------------------------------------
/**
 * Defines a generalized font chooser class. FontChooser contains three
 * components to display font face selection.
 */
public class FontChooser extends JPanel {

    private Font selectedFont;

    protected DefaultComboBoxModel fontFaceModel;
    protected JComboBox face;

    protected static float displaySize = 12F;
    protected static Font[] scaledFonts;
    protected static Font defFont = new Font("dialog", Font.PLAIN, 1);
    static { 
        scaledFonts = scaleFonts(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(),
                                  displaySize); 
    }

    /**
     * Create a FontChooser to choose between all fonts available on the system.
     */
    public FontChooser() {
	this(null);
    }
    
    public FontChooser(Font def) {
	this(scaledFonts, def);
    }
    
    /**
     * Create a FontChooser to choose between the given array of fonts.
     */
    public FontChooser(Font[] srcFonts, Font def) {

	Font[] displayFonts = scaledFonts;
    	if ( srcFonts != scaledFonts ) {
		System.out.println("scaling fonts");
		displayFonts = scaleFonts(srcFonts,displaySize);
	}

	this.fontFaceModel = new DefaultComboBoxModel(displayFonts);
	
	this.face = new JComboBox(fontFaceModel);
	face.setRenderer(new FontRenderer());
	
	// set the prototype display for the combo box
	//face.setPrototypeDisplayValue("Really really long font name Bold Italic");
	face.addItemListener(new FontFaceSelectionListener());

	// set the currently selected face, default if null
	if (def == null)
	    this.selectedFont = defFont; 
	else
	    this.selectedFont = def.deriveFont(1F);
	face.setSelectedItem(this.selectedFont);

	add(face);
    }

    public JComboBox getFaceComboBox() {
	return face;
    }

    public Font getSelectedFont() {
	return selectedFont;
    }

    private class FontFaceSelectionListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		JComboBox source = (JComboBox) e.getItemSelectable();
		selectedFont = (Font) source.getSelectedItem();
	    }
	}
    }

    private static Font[] scaleFonts(Font[] inFonts, float size) {
    	Font[] outFonts = new Font[inFonts.length];
	int i = 0;
	for (Font f : inFonts)
		outFonts[i++] = f.deriveFont(size);
	return outFonts;
    }
}

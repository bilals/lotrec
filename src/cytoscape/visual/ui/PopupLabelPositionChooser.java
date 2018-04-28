/*
 File: PopupLabelPositionChooser.java

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

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import cytoscape.visual.LabelPosition;
import giny.view.Label;

public class PopupLabelPositionChooser extends JDialog implements PropertyChangeListener { 

    protected LabelPosition lp;
    protected LabelPosition newlp;

    public static LabelPosition showDialog(Dialog f, LabelPosition pos) {
    	PopupLabelPositionChooser placer = new PopupLabelPositionChooser(f,true,pos);
	return placer.getLabelPosition();
    }

    public static LabelPosition showDialog(Frame f, LabelPosition pos) {
    	PopupLabelPositionChooser placer = new PopupLabelPositionChooser(f,true,pos);
	return placer.getLabelPosition();
    }

    private PopupLabelPositionChooser(Frame f, boolean modal, LabelPosition pos) {
    	super(f,modal);
	init(pos);
    }

    private PopupLabelPositionChooser(Dialog f, boolean modal, LabelPosition pos) {
    	super(f,modal);
	init(pos);
    }

    private void init(LabelPosition pos) {
	if ( pos == null )
		lp = new LabelPosition(Label.NONE,Label.NONE,Label.JUSTIFY_CENTER,0.0,0.0); 
	else
		lp = pos;

	newlp = new LabelPosition(lp);

	setTitle("Select Label Placement");

	JPanel placer = new JPanel();
	placer.setLayout(new BoxLayout(placer,BoxLayout.Y_AXIS));
        placer.setOpaque(true); //content panes must be opaque

	//Set up and connect the gui components.
	LabelPlacerGraphic graphic = new LabelPlacerGraphic(new LabelPosition(lp));
	LabelPlacerControl control = new LabelPlacerControl(new LabelPosition(lp));

	control.addPropertyChangeListener(graphic);
	control.addPropertyChangeListener(this);

	graphic.addPropertyChangeListener(control);
	graphic.addPropertyChangeListener(this);

	placer.add(graphic);
	placer.add(control);

	
	JPanel buttonPanel = new JPanel();
	final JButton ok = new JButton("OK");
	ok.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			lp = newlp;
			dispose();
		}
	});
	ok.addActionListener(control);

	final JButton cancel = new JButton("Cancel");
	cancel.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	});

	buttonPanel.add(ok);
	buttonPanel.add(cancel);
	placer.add(buttonPanel);
	add(placer);

	pack();
	setVisible(true);
    }

    private LabelPosition getLabelPosition() {
    	return lp;
    }

    /**
     * Handles all property changes that the panel listens for.
     */
    public void propertyChange(PropertyChangeEvent e) {
        String type = e.getPropertyName();
        if ( type.equals("LABEL_POSITION_CHANGED") ) {
		newlp = (LabelPosition)e.getNewValue();
	}
    }
}

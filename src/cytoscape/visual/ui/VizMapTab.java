/*
 File: VizMapTab.java 
 
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

// VizMapTab.java
//--------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;

import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import cytoscape.visual.calculators.Calculator;

/**
 * VizMapTab defines an organizational abstract class for tabs in the Set Visual
 * Properties dialog. You probably don't want to extend this class, since
 * {@link VizMapAttrTab}, {@link VizMapFontTab}, and {@link VizMapSizeTab}
 * provide UI functionality for all mappable attributes in Cytoscape.
 */
public abstract class VizMapTab extends JPanel {
	protected VizMapTab(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	protected VizMapTab(LayoutManager layout) {
		this(layout, true);
	}

	protected VizMapTab(boolean isDoubleBuffered) {
		this(new FlowLayout(), isDoubleBuffered);
	}

	protected VizMapTab() {
		this(true);
	}

	/**
	 * Check that the calculator selected on this tab is not c. Because Java's
	 * AWT only allows one parent per Component, bad things happen when multiple
	 * tabs attempt to use the same calculator. {@link VizMapUI} prevents these
	 * conflicts from happening by asking each VizMapTab to check if its current
	 * calculator matches one that another VizMapTab is trying to select.
	 * 
	 * The method returns VizMapTab since instances of VizMapTab should not
	 * block themselves from selecting a calculator. (eg. it ignores the warning
	 * if the VizMapTab that was attempting to select a calculator is itself)
	 * 
	 * @param c
	 *            newly selected calculator
	 * @return null if calculator is not selected by this object, or the
	 *         {@link VizMapTab} that currently owns the calculator
	 */
	abstract VizMapTab checkCalcSelected(Calculator c);

	/**
	 * When the data structures underlying the visualization system change, (eg.
	 * NodeAttributes, EdgeAttributes) refresh the UI.
	 */
	public abstract void refreshUI();

	/**
	 * When the currently selected visual styles changed, a new set of
	 * calculators with their corresponding interfaces must be switched into the
	 * UI.
	 * 
	 * This method effects that functionality.
	 */
	public abstract void visualStyleChanged();
}

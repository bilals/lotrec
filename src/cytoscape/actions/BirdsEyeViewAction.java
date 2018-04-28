/*
 File: BirdsEyeViewAction.java 
 
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

package cytoscape.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import ding.view.BirdsEyeView;
import ding.view.DGraphView;
import lotrec.Launcher;

public class BirdsEyeViewAction extends CytoscapeAction implements
		PropertyChangeListener {

	BirdsEyeView bev;
	boolean on = false;

	public BirdsEyeViewAction() {
		super("Show/Hide Network Overview");
		setPreferredMenu("View");
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED
				|| e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS
				|| e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED) {
			bev.changeView((DGraphView) Cytoscape.getCurrentNetworkView());
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (!on) {
			bev = new BirdsEyeView((DGraphView) Cytoscape
					.getCurrentNetworkView()) {
				public Dimension getMinimumSize() {
					return new Dimension(180, 180);
				}
			};
			Cytoscape.getDesktop().getNetworkPanel().setNavigator(bev);
                        //Bilal Change
                        Launcher.getTheMainFrame().getTableauxPanel().setNavigator(bev);
                        //End Bilal Change
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
					.addPropertyChangeListener(this);
			on = true;
			Cytoscape.getDesktop().getCyMenus().setOverviewEnabled(on);
		} else {
			if (bev != null) {
				bev.destroy();
				bev = null;
			}
			Cytoscape.getDesktop().getNetworkPanel().setNavigator(
					Cytoscape.getDesktop().getNetworkPanel()
							.getNavigatorPanel());
                        //Bilal Change
                        Launcher.getTheMainFrame().getTableauxPanel().setNavigator(null);
                        //End Bilal Change
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
					.removePropertyChangeListener(this);
			on = false;
			Cytoscape.getDesktop().getCyMenus().setOverviewEnabled(on);
		}
	}
}

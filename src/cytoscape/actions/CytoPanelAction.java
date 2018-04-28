
/*
  File: CytoPanelAction.java 
  
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

//     
// $Id: CytoPanelAction.java 7760 2006-06-26 16:28:49Z mes $
//------------------------------------------------------------------------------

// our package
package cytoscape.actions;

// imports
import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.cytopanels.CytoPanelListener;

/**
 * Menu item handler for CytoPanels
 */
public class CytoPanelAction extends CytoscapeAction implements CytoPanelListener {

	/**
	 * Reference to our CytoPanel.
	 */
	CytoPanel cytoPanel;

	/**
	 * Maintains state of CytoPanel just prior to being hidden
	 */
	private CytoPanelState cytoPanelPrevState =  CytoPanelState.DOCK;

	/**
	 * Reference to our parent menu.
	 */
	JCheckBoxMenuItem menuItem;
   
	/**
	 * Constructor.
	 */
	public CytoPanelAction (JCheckBoxMenuItem menuItem, CytoPanel cytoPanel) {
		// call our parent constructor
		super(cytoPanel.getTitle());
		setPreferredMenu( "View.Desktop" );

		// save reference to CytoPanel
		this.cytoPanel = cytoPanel;

		// save reference to menu
		this.menuItem = menuItem;

		// register as a CytoPanel listener
		cytoPanel.addCytoPanelListener(this);
	}

	/**
	 * Menu item select/deselect handler.
	 */
	public void actionPerformed (ActionEvent e) {

		// dock or float or hide based on cytopanel and menu item state
		if (menuItem.isSelected()){
			if (cytoPanelPrevState == CytoPanelState.DOCK){
				cytoPanel.setState(CytoPanelState.DOCK);
			}
			else{
				cytoPanel.setState(CytoPanelState.FLOAT);
			}
			syncCheckbox(true);
		}
		else{
			cytoPanelPrevState = cytoPanel.getState();
			cytoPanel.setState(CytoPanelState.HIDE);
			syncCheckbox(false);
		}
	}

    /**
     * Notifies the listener when a component is added to the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel.
     */
    public void onComponentAdded(int count){
		// no way to check if item is already enabled, so lets just enable it
		menuItem.setEnabled(true);
	}

    /**
     * Notifies the listener when a component is removed from the CytoPanel.
     *
	 * @param count The number of components on the CytoPanel.
     */
    public void onComponentRemoved(int count){

		// if no more components on cytopanel, disable menu item
		if (count == 0){
			menuItem.setEnabled(false);
			menuItem.setSelected(false);
		}
	}

    /**
     * Notifies the listener on a change in the CytoPanel state.
     *
     * @param newState The new CytoPanel state - see CytoPanelState class.
     */
    public void onStateChange(CytoPanelState newState){
		if (newState == CytoPanelState.DOCK ||
			newState == CytoPanelState.FLOAT){
			menuItem.setSelected(true);
		}
	}

    /**
     * Notifies the listener when a new component on the CytoPanel is selected.
     *
	 * @param componentIndex The index of the component selected.
     */
    public void onComponentSelected(int componentIndex){
	}
    
    /**
	 * Find menu item, and sync check box.
	 * This is a hack, but currently, this is the only way
	 * to sync. menuitem created by plugins and core.
	 * 
	 * @param on
	 * 			ON/OFF the checkbox created by browser plugin.
	 */
	private void syncCheckbox(boolean on) {
		JCheckBoxMenuItem targetCheckbox = null;
		JMenu targetMenu = Cytoscape.getDesktop().getCyMenus().getViewMenu();
		int menuCount = targetMenu.getMenuComponentCount();
		
		// Find the location of menu item
		for(int i=0; i<menuCount; i++) {
			
			Object component = targetMenu.getMenuComponent(i);
			
			if(component.getClass().equals(JCheckBoxMenuItem.class)) {
				if(cytoPanel.equals(Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)) && ((JCheckBoxMenuItem)component).getText().equals("Show/Hide network tree viewer")) {
					targetCheckbox = ((JCheckBoxMenuItem)component);
				} else if(cytoPanel.equals(Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)) && ((JCheckBoxMenuItem)component).getText().equals("Show/Hide attribute browser")) {
					targetCheckbox = ((JCheckBoxMenuItem)component);
				} else if(cytoPanel.equals(Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)) && ((JCheckBoxMenuItem)component).getText().equals("Show/Hide advanced window")) {
					targetCheckbox = ((JCheckBoxMenuItem)component);
				}
			}
		}
		
		if(targetCheckbox == null) {
			return;
		}
		
		if(on == true) {
			targetCheckbox.setSelected(true);
		} else {
			targetCheckbox.setSelected(false);
		}
	}
    
}


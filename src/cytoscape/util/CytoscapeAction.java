/*
 File: CytoscapeAction.java 
 
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

package cytoscape.util;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;

public abstract class CytoscapeAction extends AbstractAction {

	protected String preferredMenu = null;
	protected String preferredButtonGroup = null;
	protected Integer menuIndex = new Integer(-1);
	protected boolean acceleratorSet = false;
	protected int keyModifiers;
	protected int keyCode;
	protected String consoleName;

	private static List actionList = new LinkedList();

	/**
	 * @beaninfo (rwb)
	 */

	public CytoscapeAction() {
		super();
		initialize();
	}

	public CytoscapeAction(String name) {
		super(name);
		this.consoleName = name;
		consoleName = consoleName.replaceAll(":. \'", "");
		actionList.add(this);
		initialize();
	}

	public CytoscapeAction(String name, javax.swing.Icon icon) {
		super(name, icon);
		this.consoleName = name;
		consoleName = consoleName.replaceAll(" ", "");
		actionList.add(this);
		initialize();
	}

	public static List getActionList() {
		return actionList;
	}

	public void setName(String name) {
		this.consoleName = name;
	}

	public String getName() {
		return consoleName;
	}

	public String actionHelp() {
		return "";
	}

	public String[] completions() {
		return new String[] {};
	}

	public void takeArgs(String[] argv) {
	}

	// implements AbstractAction
	public abstract void actionPerformed(ActionEvent e);

	/**
	 * Initialization method called by all constructors. Envelop if you wish.
	 */
	protected void initialize() {
		// Do nothing.
	}

	/**
	 * The default clone() implementation delegates to the create() method of
	 * DataTypeUtilities.getDataTypeFactory( this.getClass() ). Override if your
	 * CytoscapeAction maintains state that must be transmitted to the clone.
	 */
	// implements Cloneable
	public Object clone() {
		return this;
	} // clone()

	/**
	 * By default all CytoscapeActions wish to be included in CommunityMenuBars,
	 * but you may override if you wish.
	 * 
	 * @return true If this Action should be included in a CommunityMenuBar.
	 * @see #getPrefferedMenu();
	 * @beaninfo (ri)
	 */
	public boolean isInMenuBar() {
		return true;
	}

	/**
	 * By default no CytoscapeActions wish to be included in CommunityToolBars,
	 * but you may override if you wish.
	 * 
	 * @return true If this Action should be included in a CommunityMenuBar.
	 * @see #getPrefferedButtonGroup();
	 * @beaninfo (ri)
	 */
	public boolean isInToolBar() {
		return false;
	}

	public void setPreferredIndex(int index) {
		menuIndex = new Integer(index);
	}

	public Integer getPrefferedIndex() {
		return menuIndex;
	}

	public void setAcceleratorCombo(int key_code, int key_mods) {
		acceleratorSet = true;
		keyCode = key_code;
		keyModifiers = key_mods;
	}

	public boolean isAccelerated() {
		return acceleratorSet;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public int getKeyModifiers() {
		return keyModifiers;
	}

	/**
	 * This method returns a Menu specification string. Submenus are preceeded
	 * by dots in this string, so the result "File.Import" specifies the submenu
	 * "Import" of the menu "File". If the result is null, the menu will be
	 * placed in a default location.
	 * 
	 * @return a Menu specification string, or null if this Action should be
	 *         placed in a default Menu.
	 * @see #inMenuBar()
	 */
	public String getPreferredMenu() {
		return preferredMenu;
	}

	/**
	 * @beaninfo (rwb)
	 */
	public void setPreferredMenu(String new_preferred) {
		if ((preferredMenu == new_preferred)
				|| ((preferredMenu != null) && preferredMenu
						.equals(new_preferred))) {
			return;
		}
		String old_preferred = preferredMenu;
		preferredMenu = new_preferred;
		firePropertyChange("preferredMenu", old_preferred, new_preferred);
	} // setPreferredMenu( String )

	/**
	 * This method returns a ButtonGroup specification string. Subgroups are
	 * preceeded by dots in this string, so the result "Edit.Selection Modes"
	 * specifies the subgroup "Selection Modes" of the group "Edit". If the
	 * result is null, the button will be placed in a default location.
	 * 
	 * @return a ButtonGroup specification string, or null if the button for
	 *         this Action should be placed in a default ButtonGroup.
	 * @see #inToolBar()
	 */
	public String getPreferredButtonGroup() {
		return preferredButtonGroup;
	}

	/**
	 * @beaninfo (rwb)
	 */
	public void setPreferredButtonGroup(String new_preferred) {
		if (preferredButtonGroup.equals(new_preferred)) {
			return;
		}
		String old_preferred = preferredButtonGroup;
		preferredButtonGroup = new_preferred;
		firePropertyChange("preferredButtonGroup", old_preferred, new_preferred);
	} // setPreferredButtonGroup( String )

} // class CytoscapeAction

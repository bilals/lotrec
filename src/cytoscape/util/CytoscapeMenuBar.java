
/*
  File: CytoscapeMenuBar.java 
  
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

//import shadegrown.Reidentifiable;
//import shadegrown.CollectionListener;
//import shadegrown.CollectionEventSource;
//import shadegrown.CollectionEvent;
//import shadegrown.DataTypeUtilities;
//import shadegrown.PropertyMap;

//import shadegrown.filters.TypeFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class CytoscapeMenuBar extends JMenuBar {

	public static final String DEFAULT_MENU_SPECIFIER = "Tools";
	
	protected static final int NO_INDEX = -2;
	

	protected String defaultMenuSpecifier = DEFAULT_MENU_SPECIFIER;
	protected Set actionMembersSet = null;
	protected Map actionMenuItemMap = null;
	protected Map menuMap = null;
	protected Map menuEffectiveLastIndexMap = null;

	/**
	 * @beaninfo (rwb)
	 */
	private String identifier;

	/**
	 * Default constructor. Calls {@link #initialize initializeCytoscapeMenuBar}.
	 */
	public CytoscapeMenuBar() {
		initializeCytoscapeMenuBar();
	}

	protected void initializeCytoscapeMenuBar() {
		// Load the first menu, just to please the layouter. Also make sure the
		// menu bar doesn't get too small.
		// "File" is always first
		setMinimumSize(getMenu("File").getPreferredSize());
	} // initializeCytoscapeMenuBar()

	public void setDefaultMenuSpecifier(String menu_name) {
		// TODO: If the existing menu exists, should we rename it?
		defaultMenuSpecifier = menu_name;
	}

	public String getDefaultMenuSpecifier() {
		return defaultMenuSpecifier;
	}

	/**
	 * If the given Action has a present and false inMenuBar property, return;
	 * otherwise delegate to addAction( String, Action ) with the value of its
	 * preferredMenu property, or null if it does not have that property.
	 */
	public boolean addAction(Action action) {
		
		return addAction(action, NO_INDEX);
	} // addAction( action )
	
	
	public boolean addAction(Action action, int index) {
		String menu_name = null;
		if (action instanceof CytoscapeAction) {
			if (((CytoscapeAction) action).isInMenuBar()) {
				menu_name = ((CytoscapeAction) action).getPreferredMenu();
			} else {
				return false;
			}
		} else {
			// PropertyMap map = DataTypeUtilities.getPropertyMap( action );
			// Boolean in_menu_bar = ( Boolean )map.get( "inMenuBar" );
			// if( ( in_menu_bar != null ) && ( in_menu_bar.booleanValue() ==
			// false ) ) {
			// return false;
			// }
			// menu_name = ( String )map.get( "preferredMenu" );
			menu_name = DEFAULT_MENU_SPECIFIER;
		}
		if(index != NO_INDEX) {
			((CytoscapeAction) action).setPreferredIndex(index);
		}
		return addAction(menu_name, action);
	}

	 

	public boolean addAction(String menu_name, Action action) {
		// At present we allow an Action to be in this menu bar only once.
		JMenuItem menu_item = null;
		if (actionMenuItemMap != null) {
			menu_item = (JMenuItem) actionMenuItemMap.get(action);
		}
		if (menu_item != null) {
			return false;
		}
		JMenu menu = getMenu(menu_name);
		menu_item = createJMenuItem(action);

		// Add an Accelerator Key, if wanted

		// If it wants to be anywhere in particular, try to put it there..
		Object index_object = new Integer(-1);
		if (action instanceof CytoscapeAction) {
			index_object = ((CytoscapeAction) action).getPrefferedIndex();
			if (((CytoscapeAction) action).isAccelerated()) {
				menu_item.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
						((CytoscapeAction) action).getKeyCode(),
						((CytoscapeAction) action).getKeyModifiers()));
			}

		}

		if (index_object != null) {
			int index = -1;
			if (index_object instanceof Integer) {
				index = ((Integer) index_object).intValue();
			} else if (index_object instanceof String) {
				try {
					index = Integer.parseInt((String) index_object);
				} catch (NumberFormatException e) {
					// TODO: Error handling
					System.err
							.println("WARNING: The action "
									+ action
									+ " has an \"index\" property but its String value cannot be converted to an int.  Ignoring.");
					index_object = null;
				}
			} else {
				// TODO: Error handling
				System.err
						.println("WARNING: The action "
								+ action
								+ " has an \"index\" property but its value is neither an Integer nor a String.  Ignoring.");
				index_object = null;
			}
			if (index_object != null) {
				if (index < 0) {
					index = (menu.getItemCount() + (index + 1));
					if (index < 0) {
						index = 0;
					} else {
						if (menuEffectiveLastIndexMap == null) {
							menuEffectiveLastIndexMap = createMenuEffectiveLastIndexMap();
						}
						Integer effective_last_index = (Integer) menuEffectiveLastIndexMap
								.get(menu);
						if (effective_last_index == null) {
							// Add a separator.
							// menu.insertSeparator( index );
							menuEffectiveLastIndexMap.put(menu, new Integer(
									index));
							index += 1;
						} else if (effective_last_index.intValue() >= index) {
							menuEffectiveLastIndexMap.put(menu, new Integer(
									index));
						}
					}
				}
				// TODO: REMOVE
				// System.err.println( "Adding action " + action + " to menu " +
				// menu_name + " at index " + index + " since its \"index\"
				// property's value is \"" + index_object + "\"." );
				menu.insert(menu_item, index);
			}
		}
		if (index_object == null) {
			boolean added_it = false;
			if (menuEffectiveLastIndexMap != null) {
				Integer effective_last_index = (Integer) menuEffectiveLastIndexMap
						.get(menu);
				if (effective_last_index != null) {
					// TODO: REMOVE
					// System.err.println( "Adding action " + action + " to menu
					// " + menu_name + " at the effective_last_index, which is "
					// + effective_last_index + "." );
					menu.insert(menu_item, effective_last_index.intValue());
					menuEffectiveLastIndexMap.put(menu, new Integer(
							effective_last_index.intValue() + 1));
					added_it = true;
				}
			}
			if (!added_it) {
				// TODO: REMOVE
				// System.err.println( "Adding action " + action + " to menu " +
				// menu_name + " at the end." );
				menu.add(menu_item);
			}
		}
		if (actionMenuItemMap == null) {
			actionMenuItemMap = createActionMenuItemMap();
		}
		actionMenuItemMap.put(action, menu_item);
		// updateUI();
		return true;
	} // addAction( menu_name, action )

	
	
	/**
	 * If the given Action has a present and false inMenuBar property, return;
	 * otherwise if there's a menu item for the action, remove it. Its menu is
	 * determined my its preferredMenu property if it is present; otherwise by
	 * defaultMenuSpecifier.
	 */
	public boolean removeAction(Action action) {
		if (actionMenuItemMap == null) {
			return false;
		}
		JMenuItem menu_item = (JMenuItem) actionMenuItemMap.remove(action);
		if (menu_item == null) {
			return false;
		}
		String menu_name = null;
		if (action instanceof CytoscapeAction) {
			if (((CytoscapeAction) action).isInMenuBar()) {
				menu_name = ((CytoscapeAction) action).getPreferredMenu();
			} else {
				return false;
			}
		} else {
			// PropertyMap map = DataTypeUtilities.getPropertyMap( action );
			// Boolean in_menu_bar = ( Boolean )map.get( "inMenuBar" );
			// if( ( in_menu_bar != null ) && ( in_menu_bar.booleanValue() ==
			// false ) ) {
			// return false;
			// }
			// menu_name = ( String )map.get( "preferredMenu" );
			menu_name = DEFAULT_MENU_SPECIFIER;
		}
		if (menu_name == null) {
			menu_name = defaultMenuSpecifier;
		}
		getMenu(menu_name).remove(menu_item);
		return true;
	} // removeAction( action )

	
	public JMenu getMenu(String menu_string) {
		return getMenu( menu_string, -1 );
	}
	
	/**
	 * @return the menu named in the given String. The String may contain
	 *         multiple menu names, separated by dots ('.'). If any contained
	 *         menu name does not correspond to an existing menu, then that menu
	 *         will be created as a child of the menu preceeding the most recent
	 *         dot or, if there is none, then as a child of this MenuBar.
	 */
	public JMenu getMenu(String menu_string, int parentPosition) {
		if (menu_string == null) {
			menu_string = getDefaultMenuSpecifier();
		}

		StringTokenizer st = new StringTokenizer(menu_string, ".");
		String menu_token;
		JMenu parent_menu = null;
		JMenu menu = null;

		if (menuMap == null) {
			menuMap = createMenuMap();
		}

		while (st.hasMoreTokens()) {
			menu_token = (String) st.nextToken();

			if (menuMap.containsKey(menu_token)) {
				parent_menu = (JMenu) menuMap.get(menu_token);
			} else {
				menu = createJMenu(menu_token);
				if (parent_menu == null) {
					this.add(menu);
					invalidate();
				} else {
					parent_menu.add(menu, parentPosition);
				}
				menuMap.put(menu_token, menu);
				parent_menu = menu;
			}
		}
		if (menu == null) {
			return parent_menu;
		}
		return menu;
	} // getMenu(..)

	/**
	 * CytoscapeMenuBars are unique -- this equals() method returns true iff the
	 * other object == this.
	 */
	public boolean equals(Object other_object) {
		return (this == other_object);
	} // equals( Object )

	// implements CommunityMember
	public String getIdentifier() {
		return identifier;
	} // getIdentifier()

	/**
	 * @beaninfo (rwb)
	 */
	// imlements Reidentifiable
	public void setIdentifier(String new_identifier) {
		if (identifier == null) {
			if (new_identifier == null) {
				return;
			}
		} else if (new_identifier != null) {
			if (identifier.equals(new_identifier)) {
				return;
			}
		}
		String old_identifier = identifier;
		identifier = new_identifier;
		firePropertyChange("identifier", old_identifier, new_identifier);
	} // setIdentifier(..)

	/**
	 * @return true (always)
	 */
	// imlements Reidentifiable
	public boolean isReidentificationEnabled() {
		return true;
	}

	/**
	 * Delegates to {@link #getIdentifier()}.
	 */
	public String toString() {
		return getIdentifier();
	} // toString()

	/**
	 * Factory method for instantiating objects of type JMenu
	 */
	public JMenu createJMenu(String title) {
		JMenu menu = new JMenu(title);
		revalidate();
		repaint();
		return menu;
	}

	/**
	 * Factory method for instantiating the buttons in the toolbar.
	 */
	protected JMenuItem createJMenuItem(Action action) {
		return new JMenuItem(action);
	}

	/**
	 * Factory method for instantiating the action->MenuItem map.
	 */
	protected Map createActionMenuItemMap() {
		return new HashMap();
	}

	/**
	 * Factory method for instantiating the String->Menu Map
	 */
	protected Map createMenuMap() {
		return new HashMap();
	}

	/**
	 * Factory method for instantiating the Menu->Integer "effective last index"
	 * Map for Menus with menu items that want to be at the end.
	 */
	protected Map createMenuEffectiveLastIndexMap() {
		return new HashMap();
	}

} // class CytoscapeMenuBar

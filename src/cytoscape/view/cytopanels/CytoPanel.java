
/*
  File: CytoPanel.java 
  
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
// $Id: CytoPanel.java 8191 2006-09-13 23:50:23Z pwang $
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

// imports
import java.awt.Component;
import javax.swing.Icon;

/**
 * Interface to a CytoPanel.
 *
 * @author Ben Gross.
 */
public interface CytoPanel {

    /**
     * Adds a component to the CytoPanel.
     *
     * @param component  Component reference.
	 * @return component Component reference.
     */
    public Component add(Component component);

    /**
     * Adds a component to the CytoPanel at specified index.
     *
     * @param component Component reference.
     * @param index     Component index.
	 * @return component Component reference.
     */
    public Component add(Component component, int index);

    /**
     * Adds a component to the CytoPanel with a specified title.
     *
     * @param title     Component title.
     * @param component Component reference.
	 * @return component Component reference.
     */
    public Component add(String title, Component component);

    /**
     * Adds a component to the CytoPanel with specified title and icon.
     *
     * @param title     Component title (can be null).
	 * @param icon      Component icon (can be null).
     * @param component Component reference.
     */
    public void add(String title, Icon icon, Component component);

    /**
     * Adds a component to the CytoPanel with specified title, icon, and tool tip.
     *
     * @param title     Component title (can be null).
	 * @param icon      Component icon (can be null).
     * @param component Component reference.
     * @param tip       Component Tool tip text.
     */
    public void add(String title, Icon icon, Component component, String tip);

	/**
	 * Returns the title of the CytoPanel.
	 *
	 * @return String Title.
	 */
	public String getTitle();

	/**
	 * Returns the number of components in the CytoPanel.
	 *
	 * @return int Number of components.
	 */
	public int getCytoPanelComponentCount();

	/**
	 * Returns the currently selected component.
	 *
	 * @return component Currently selected Component reference.
	 */
	public Component getSelectedComponent();

	/**
	 * Returns the currently selected index.
	 *
	 * @return index Currently selected index.
	 */
	public int getSelectedIndex();
	
	
	/**
	 * Returns the component at index.
	 *
	 * @return component at the given index.
	 */
	public Component getComponentAt(int index);


    /**
     * Gets the state of the CytoPanel.
     *
	 * @return A CytoPanelState.
     */
    public CytoPanelState getState();

	/**
	 * Returns the index for the specified component.
	 *
     * @param component Component reference.
	 * @return int      Index of the Component or -1 if not found.
	 */
	public int indexOfComponent(Component component);

	/**
	 * Returns the first Component index with given title.
	 *
     * @param title Component title.
	 * @return int  Component index with given title or -1 if not found.
	 */
	public int indexOfComponent(String title);

	/**
	 * Removes specified component from the CytoPanel.
	 *
	 * @param component Component reference.
	 */
	public void remove(Component component);

	/**
	 * Removes the component from the CytoPanel at the specified index.
	 *
     * @param index Component index.
	 */
	public void remove(int index);

	/**
	 * Removes all the components from the CytoPanel.
	 */
	public void removeAll();

    /**
     * Sets the selected index on the CytoPanel.
     *
     * @param index The desired index.
     */
    public void setSelectedIndex(int index);

    /**
     * Sets the state of the CytoPanel.
     *
     * @param cytoPanelState A CytoPanelState.
     */
    public void setState(CytoPanelState cytoPanelState);

	/**
	 * Adds a CytoPanel listener.
	 *
	 * @param cytoPanelListener Reference to a CytoPanelListener.
	 */
	public void addCytoPanelListener(CytoPanelListener cytoPanelListener);

	/**
	 * Removes a CytoPanel listener.
	 *
	 * @param cytoPanelListener Reference to a CytoPanelListener.
	 */
	public void removeCytoPanelListener(CytoPanelListener cytoPanelListener);

}

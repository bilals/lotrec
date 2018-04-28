
/*
  File: DiscreteUI.java 
  
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

//----------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.ui.ValueDisplayer;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Provides User Interface Controls for Discrete Mappers.
 */
public class DiscreteUI extends JPanel implements ChangeListener {
    private final static String USER_MSG = "Define Discrete Mapping";
    private JDialog parentDialog;
    private CyNetwork network;
    private String attrName;
    private TreeSet mappedKeys;
    private DiscreteMapping dm;
    private Object defaultObject;
    private byte mapType;
    private JPanel buttonPanel;
    private JScrollPane listScrollPane;
    private HashMap buttonPositions;

    /**
     * Constructor.
     * @param parentDialog Parent Dialog Object.
     * @param network CyNetwork Object.
     * @param attrName Controlling Attribute Name.
     * @param defaultObject Default Value Object.
     * @param mapType Map Type byte.
     * @param dm DiscreteMapping object.
     */
    public DiscreteUI(JDialog parentDialog, CyNetwork network,
            String attrName, Object defaultObject,
            byte mapType, DiscreteMapping dm) {
        this.parentDialog = parentDialog;
        this.network = network;
        this.mappedKeys = new TreeSet();
        this.attrName = attrName;
        this.defaultObject = defaultObject;
        this.mapType = mapType;
        this.dm = dm;
        this.buttonPositions = new HashMap();
        dm.addChangeListener(this);
        loadKeys(network);
        initUI();
    }

    /**
     * Initializes the DiscreteMapping UI.
     */
    private void initUI() {
        this.setLayout(new BorderLayout());

        // check that there is a valid attribute set
        if (this.attrName == null) {
            JLabel label = new JLabel ("Attribute is not set.");
            label.setHorizontalAlignment(JLabel.CENTER);
            this.add(label, BorderLayout.CENTER);
            return;
        }
        if (this.mappedKeys == null || this.mappedKeys.size() == 0) {
            JLabel label = new JLabel
                    ("Mapping for this attribute is not supported.");
            label.setHorizontalAlignment(JLabel.CENTER);
            this.add(label, BorderLayout.CENTER);
            return;
        }

        int numKeys = this.mappedKeys.size();
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(numKeys, 2));

        Iterator iterator = mappedKeys.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Object keyObject = iterator.next();
            Object currentMapping = dm.getMapValue(keyObject);
            JButton mapButton = new JButton(keyObject.toString());
            ValueDisplayer valueDisplayer = getMapValue(currentMapping);
            buttonPanel.add(mapButton, index);
            buttonPanel.add(valueDisplayer, ++index);
            valueDisplayer.addItemListener (new ValueChangeListener
                    (dm, keyObject));
            buttonPositions.put(keyObject, new Integer(index++));
            mapButton.addActionListener(valueDisplayer.getInputListener());
        }

        //  New feature added, at request of Trey Ideker (3/26/2004).
        if(dm.getRangeClass().getName().equals("java.awt.Color")){
            JButton button = new JButton ("Seed Mapping with Random Colors");
            button.addActionListener(new RandomColorListener(dm, mappedKeys));
            this.add (button, BorderLayout.SOUTH);
        }
        resetScrollPane(buttonPanel);
    }

    /**
     * Underlying DiscreteMapping State has changed.
     * Need to automatically update the UI to reflect this change.
     * @param e
     */
    public void stateChanged(ChangeEvent e) {
        //  Find out what key was modified, and only update this key
        //  All others remain constant. This is much more efficient than
        //  modifying all keys.
        Object lastKey = dm.getLastKeyModified();
        if (lastKey != null) {
            Object value = dm.getMapValue(lastKey);
            ValueDisplayer newValueDisplayer = ValueDisplayer.getDisplayFor
                    (parentDialog, USER_MSG, value);
            Integer pos = (Integer) buttonPositions.get(lastKey);
            if (pos != null) {
                swapValueDisplayer(newValueDisplayer, pos.intValue(), lastKey);
            }
        }
    }

    /**
     * Swaps in a New Value Displayer.
     * @param valueDisplayer Value Displayer.
     * @param position Position in Panel.
     */
    void swapValueDisplayer(ValueDisplayer valueDisplayer, int position,
            Object keyObject) {
        buttonPanel.remove(position);
        buttonPanel.add(valueDisplayer, position);

        //  Listen for Changes to this new Value Displayer.
        //  Fixes Bug #270
        valueDisplayer.addItemListener (new ValueChangeListener
            (dm, keyObject));
        this.remove(this.listScrollPane);
        this.resetScrollPane(buttonPanel);
    }

    /**
     * Gets the Map Value.
     */
    private ValueDisplayer getMapValue(Object currentMapping) {
        ValueDisplayer mapValue;
        if (currentMapping == null) {
            // display default selection
            mapValue = ValueDisplayer.getBlankDisplayFor
                    (parentDialog, USER_MSG, defaultObject);
        } else { // display current mapping
            mapValue = ValueDisplayer.getDisplayFor
                    (parentDialog, USER_MSG, currentMapping);
        }
        return mapValue;
    }

    /**
     * This method grabs all the data values for the current controlling
     * attribute from the appropriate CyAttributes member of the
     * Cytoscape object. Any data value that is not already a key in this
     * mapping is added with a null visual attribute value.
     */
    private void loadKeys(CyNetwork network) {
        if (network == null) {
            return;
        }
        CyAttributes attrs;
        if (mapType == ObjectMapping.EDGE_MAPPING) {
            attrs = Cytoscape.getEdgeAttributes();
        } else {
            attrs = Cytoscape.getNodeAttributes();
        }

        //HashMap mapAttrs = attrs.getAttribute(attrName);
        Map mapAttrs;
        if (attrName == null) { mapAttrs = null; }
        else { mapAttrs = CyAttributesUtils.getAttribute(attrName,attrs); }

        if (mapAttrs == null || mapAttrs.size() == 0) { // no attribute found <sob>
            return;
        }

        List acceptedClasses = Arrays.asList(dm.getAcceptedDataClasses());
        //Class mapAttrClass = attrs.getClass(attrName);
	Class mapAttrClass = CyAttributesUtils.getClass(attrName,attrs);
	
        if (mapAttrClass == null || !(acceptedClasses.contains(mapAttrClass))) {
            return;
        }
        loadKeySet(mapAttrs);
    }

    /**
     * Loads the Key Set.
     */
    private void loadKeySet(Map mapAttrs) {
        // get the set of keys being mapped from
        Iterator keyIter = mapAttrs.values().iterator();

        // add keys to the map, with null mappings
        while (keyIter.hasNext()) {
            Object o = keyIter.next();
            // handle vector data (from GO)
            // add all values from the vector
            if (o instanceof List) {
                List list = (List) o;
                for (int i = 0; i < list.size(); i++) {
                    Object vo = list.get(i);
                    if (!mappedKeys.contains(vo))
                        mappedKeys.add(vo);
                }
            } else {
                if (!mappedKeys.contains(o)) {
                    mappedKeys.add(o);
                }
            }
        }
    }

    /**
     * Sets up the Scrollable Pane.
     * @param internalPanel Internal Panel.
     */
    private void resetScrollPane(JPanel internalPanel) {
        listScrollPane = new JScrollPane(internalPanel,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(listScrollPane, BorderLayout.CENTER);

        // set limits on size
        Dimension d = listScrollPane.getPreferredSize();
        int prefHeight = (int) d.getHeight();
        if (prefHeight > 200) {
            prefHeight = 200;
        }
        listScrollPane.setPreferredSize
                (new Dimension((int) d.getWidth() + 10, prefHeight));

        // because parentDialog is only passed in at getUI and not
        // construction time
        if (parentDialog != null) {
            parentDialog.pack();
            parentDialog.validate();
            parentDialog.repaint();
        }
    }
}

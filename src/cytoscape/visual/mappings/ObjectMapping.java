
/*
  File: ObjectMapping.java 
  
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
// $Revision: 8633 $
// $Date: 2006-10-30 16:21:52 -0800 (Mon, 30 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.event.ChangeListener;
import cytoscape.CyNetwork;
import cytoscape.visual.parsers.ValueParser;
//----------------------------------------------------------------------------
/**
 * Mappings should implement this interface. Mappings are classes that map from
 * a value stored in the edge attributes or node attributes HashMap in
 * {@link cytoscape.CyAttributes}. The range of the mapping depends on the
 * {@link cytoscape.visual.calculators.AbstractCalculator} that owns
 * the mapping.
 * <p>
 * All classes implementing this interface <b>MUST</b> have a constructor that
 * takes the arguments Object, CyNetwork, byte, where Object is the default object
 * the mapper should map to, CyNetwork is the CyNetwork object representing the network
 * displayed in Cytoscape, and the byte is one of {@link #EDGE_MAPPING} or
 * {@link #NODE_MAPPING}.
 */
public interface ObjectMapping extends Cloneable {
    public static final byte EDGE_MAPPING = 0;
    public static final byte NODE_MAPPING = 1;    

    Class getRangeClass();
    /**
     * Return the classes that the ObjectMapping can map from, eg. the contents
     * of the data of the controlling attribute.
     * <p>
     * For example, DiscreteMapping {@link DiscreteMapping} can only accept
     * String types in the mapped attribute data. Likewise, ContinuousMapping
     * {@link ContinuousMapping} can only accept numeric types in the mapped
     * attribute data since it must interpolate.
     * <p>
     * Return null if this mapping has no restrictions on the domain type.
     * 
     * @return Array of accepted attribute data class types
     */
    Class[] getAcceptedDataClasses();

    /**
     * Set the controlling attribute name. The current mappings will be unchanged
     * if preserveMapping is true and cleared otherwise. The network argument is
     * provided so that the current values for the given attribute name can
     * be loaded for UI purposes. Null values for the network argument are allowed.
     */
    void setControllingAttributeName(String attrName, CyNetwork network,
                                     boolean preserveMapping);

    /**
     * Get the controlling attribute name
     */
    String getControllingAttributeName();

    /**
     * Add a ChangeListener to the mapping. When the state underlying the
     * mapping changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param	l	ChangeListener to add
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Remove a ChangeListener from the mapping. When the state underlying the
     * mapping changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param	l	ChangeListener to add
     */
    public void removeChangeListener(ChangeListener l);

    Object calculateRangeValue(Map attrBundle);

    JPanel getUI(JDialog parent, CyNetwork network);

    JPanel getLegend(String s, byte type);

    Object clone();
    
    void applyProperties(Properties props, String baseKey, ValueParser parser);

    Properties getProperties(String baseKey);
}


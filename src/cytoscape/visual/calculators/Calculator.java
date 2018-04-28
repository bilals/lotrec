
/*
  File: Calculator.java 
  
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

//------------------------------------------------------------------------------
// $Revision: 8633 $
// $Date: 2006-10-30 16:21:52 -0800 (Mon, 30 Oct 2006) $
// $Author: mes $
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;
//------------------------------------------------------------------------------
import java.util.Properties;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.event.ChangeListener;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.NodeAppearance;
import cytoscape.CyNetwork;
import giny.model.Node;
import giny.model.Edge;
//------------------------------------------------------------------------------
/**
 *  Calculator specifies a super-interface for all calculator interfaces.
 *  <b>DO NOT</b> create classes that only implement Calculator! When writing
 *  calculators, you <b>MUST</b> extend one of {@link NodeCalculator} or
 *  {@link EdgeCalculator} and implement one of the 11 attribute calculator interfaces.
 */
public interface Calculator extends Cloneable {

    /**
     *	Get the UI for a calculator.
     *
     *	@param	parent	Parent JDialog for the UI
     *	@param	network	CyNetwork object containing underlying graph data
     */
    JPanel getUI(JDialog parent, CyNetwork network);

    /**
     *  Gets calculator name.
     */
    public String toString();

    /**
     *  Set calculator name. <b>DO NOT CALL THIS METHOD</b> unless you first get a valid
     *  name from the CalculatorCatalog. Even if you have a guaranteed valid name from
     *  the CalculatorCatalog, it is still preferrable to use the renameCalculator method
     *	in the CalculatorCatalog.
     */
    public void setName(String newName);

    /**
     *  Clone the calculator.
     */
    public Object clone() throws CloneNotSupportedException;

    /**
     * Get a description of this calculator as a Properties object.
     */
    public Properties getProperties();

    /**
     * Add a ChangeListener to the calcaultor. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param	l	ChangeListener to add
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Remove a ChangeListener from the calcaultor. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param	l	ChangeListener to add
     */
    public void removeChangeListener(ChangeListener l);

    public String getPropertyLabel();

    public byte getType();

    public void apply(EdgeAppearance appr, Edge e, CyNetwork net);

    public void apply(NodeAppearance appr, Node n, CyNetwork net);

    public Vector getMappings();

    public ObjectMapping getMapping(int i);

    public String getTypeName();
}

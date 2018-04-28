
/*
  File: AbstractCalculator.java 
  
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
// $Revision: 9217 $
// $Date: 2006-12-18 15:36:17 -0800 (Mon, 18 Dec 2006) $
// $Author: mes $
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;
//------------------------------------------------------------------------------
import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.*;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.MappingFactory;
import cytoscape.CyNetwork;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.NodeAppearance;
import giny.model.Edge;
import giny.model.Node;
import giny.model.GraphObject;
//------------------------------------------------------------------------------
/**
 * AbstractCalculator is the top of the tree for the Calculator classes. <b>DO NOT</b>
 * extend this class directly! All calculators should extend one of {@link NodeCalculator}
 * or {@link EdgeCalculator} 
 * TODO this shouldn't be public
 */
public abstract class AbstractCalculator implements Calculator {

	/**
	 * String literal used to refer to the generic "ID" attribute.
	 */
	public static final String ID = "ID";

    /**
     * Vector of all mappings contained by this calculator. Usually small. Contains
     * ObjectMapping objects.
     */
    protected Vector mappings = new Vector(4, 2);
    // private ObjectMapping m;

    /**
     * The domain classes accepted by the mappings underlying this calculator.
     * Contains an array of classes - each representing the appropriate domain
     * classes of the mapping in the {@link #mappings} vector at the same index.
     */
    protected Vector acceptedDataClasses = new Vector(4, 2);
    //protected Class[] acceptedDataClasses;
    protected String name;
    
    /** keep track of how many times I've been duplicated */
    private int dupeCount = 0; 

    /** keep track of interested UI classes. */
    protected List changeListeners = new Vector(1,1);

    /**
     * Only one <code>ChangeEvent</code> is needed per calculator instance
     * since the event's only state is the source property.
     */
    protected transient ChangeEvent changeEvent;

    /**
     * Create a calculator with the specified object mapping and name. The
     * object mapping is used to determine what classes of attribute data
     * this calculator can map from. If the object mapping is null, no
     * filtration on the attribute data is performed.
     *
     * @param	m	Object mapping for this calculator, or null
     * @param	name	Name of this calculator
     */
    public AbstractCalculator(String name, ObjectMapping m, Class c) {
	this.name = name;
	this.addMapping(m);
	
	if (!c.isAssignableFrom(m.getRangeClass()) ) 
                throw new ClassCastException( "Invalid Calculator: Expected class " + c.toString()
	                                      + ", got " + m.getRangeClass().toString());
    }

    /**
     * A dummy constructor used only to access type information within the
     * the CalculatorFactory. 
     */
    AbstractCalculator() {
    }


    /**
     * Add a mapping to the mappings contained by the calculator.
     * @param m Mapping to add.
     */
    public void addMapping(ObjectMapping m) {
	this.mappings.add(m);
	this.acceptedDataClasses.add(m.getAcceptedDataClasses());
	//m.addChangeListener(new MappingListener());
    }

    /**
     * Listens to changes in the mapping and forwards them on to whatever listeners
     * are attached to the calculator.
     */
    protected class MappingListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    //  fireStateChanged();
	}
    }

    /**
     * Get all mappings contained by this calculator.
     * @return Vector of all mappings contained in this calculator
     */
    public Vector getMappings() {
	return mappings;
    }

    /**
     * Get the mapping at a specific index in this calculator.
     * @param i index of mapping to retrieve
     * @return ObjectMapping at index i
     */
    public ObjectMapping getMapping(int i) {
	return (ObjectMapping) this.mappings.get(i);
    }

    /**
     * Get how many times this calculator has been duplicated.
     * @return Calculator duplication count
     */
    public int getDupeCount() {
	return dupeCount;
    }

    /**
     * Clone the calculator. AbstractCalculator makes an independent clone of itself but DOES NOT
     * ensure that a unique name is created. Whoever is cloning the calculator should enter the
     * new calculator in the catalog and create an appropriate name for it if needed.
     *
     *
     * @return Clone of this calculator
     * @throws CloneNotSupportedException if something is seriously borked.
     */
    public Object clone() throws CloneNotSupportedException {
	AbstractCalculator clonedCalc = (AbstractCalculator) super.clone();
	// remove the duplication count appended to the name. This makes
	// maintaining the duplicate naming scheme much easier by starting the
	// valid name search at the first object every time
	String dupeFreeName;
	if (dupeCount != 0) {
	    int dupeCountIndex = name.lastIndexOf(new Integer(dupeCount).toString());
	    if (dupeCountIndex == -1)
		dupeFreeName = new String(name);
	    else
		dupeFreeName = name.substring(0, dupeCountIndex);
	}
	else {
	    dupeFreeName = new String(name);
	}
	clonedCalc.name = dupeFreeName;
	clonedCalc.mappings = new Vector(this.mappings.size(),2);
	// Not needed since data classes don't change and are not configurable
	// clonedCalc.acceptedDataClasses = this.acceptedDataClasses.clone();
	// clone mapping data
	// clonedCalc.m = (ObjectMapping) m.clone();
	for (int i = 0; i < this.mappings.size(); i++) {
	    ObjectMapping m = this.getMapping(i);
	    // Not needed since data classes don't change and are not configurable
	    //Class[] mClass = (Class[]) this.acceptedDataClasses.get(i);
	    //clonedCalc.acceptedDataClasses.add(mClass.clone());
	    clonedCalc.mappings.add(m.clone());
	}

	clonedCalc.dupeCount++;
	return clonedCalc;
    }

    /**
     * Get the name of this calculator.
     *
     * @return the calculator's name
     */
    public final String toString() {
	return this.name;
    }

    /**
     * Set the name of this calculator. Should only be done by the CalculatorCatalog after checking
     * that name will not be duplicated.
     *
     * @param	newName	the new name for this calculator. Must be unique.
     */
    public void setName(String newName) {
	this.name = newName;
    }

    /**
     * Returns a properties description of this calculator. Adds the
     * keyword ".mapping" to the supplied base key and calls the
     * getProperties method of MappingFactory with the ObjectMapping
     * and the augmented base key.
     */ 
    public Properties getProperties() {
        String mapBaseKey = getPropertyLabel() + "." + toString() + ".mapping";
        ObjectMapping m = getMapping(0);
	Properties props = MappingFactory.getProperties(m, mapBaseKey);
	props.put( getPropertyLabel() + "." + toString() + ".class", getClassName() );
	return props;
    }

    /**
     * This exists so that it can be overridden in legacy classes (GenericNodeSizeCalculator,
     * GenericColorCalculator, GenericArrowCalculator) such that they return the a new class name
     * instead of a legacy class name. This will help prevent legacy classes from persisting in
     * vizmap.props files.
     */
    protected String getClassName() {
    	return this.getClass().getName();
    }

    /**
     * @deprecated Just use getProperties() - baseKey is already known by the calculator. This will
     * be removed 10/2007.
     */
    public Properties getProperties(String baseKey) {
    	return getProperties();
    }

    /**
     * updateAttribute is called when the currently selected attribute changes.
     * Any changes needed in the mapping UI should be performed at this point.
     * Use {@link #updateAttribute(String, CyNetwork, int)} for best performance.
     *
     * @param	attrName	the name of the newly selected attribute
     * @param   network         the CyNetwork on which this attribute is defined
     * @param	m		the object mapping to update
     * @throws	IllegalArgumentException if the given object mapping isn't in this
     *		    calculator.
     */
    void updateAttribute(String attrName, CyNetwork network, ObjectMapping m)
    	throws IllegalArgumentException {
	int mapIndex = this.mappings.indexOf(m);
	if (mapIndex == -1) {
	    throw new IllegalArgumentException(m.getClass().getName() + " " + m.toString() +
					       " is not contained in calculator " +
					       this.toString());
	}
	this.updateAttribute(attrName, network, mapIndex);
    }	    

    /**
     * updateAttribute is called when the currently selected attribute changes.
     * Any changes needed in the mapping UI should be performed at this point.
     * <p>
     * Calls the specified mapper's setControllingAttribute method.
     * @param	attrName	the name of the newly selected attribute
     * @param   network         the CyNetwork on which this attribute is defined
     * @param	mIndex		the index of the object mapping to update
     * @throws	ArrayIndexOutOfBoundsException if the given object mapping index
     *		    is out of bounds.
     */
    void updateAttribute(String attrName, CyNetwork network, int mIndex)
    	throws ArrayIndexOutOfBoundsException {
	ObjectMapping m = (ObjectMapping) this.mappings.get(mIndex);
	m.setControllingAttributeName(attrName, network, false);
	//  fireStateChanged();
    }

    /**
     * Get the UI for the calculator.
     * 
     * @param	parent	Parent JDialog for the UI
     * @param	network	CyNetwork object containing underlying graph data
     */
    public abstract JPanel getUI(JDialog parent, CyNetwork network);

    /**
     * Get the UI for calculators. Display a JComboBox with attributes in the given
     * CyAttributes whose data are instances of the classes accepted by each
     * ObjectMapping. The resulting JComboBox calls
     * {@link #updateAttribute(String, CyNetwork, int)} when frobbed.
     *
     * @param	attr	CyAttributes to look up attributes from
     * @return	UI with controlling attribute selection facilities
     */
    protected JPanel getUI(CyAttributes attr, JDialog parent, CyNetwork network) {
	return new CalculatorUI(attr, parent, network);
    }

    /**
     * UI class for the calculator.
     */
    protected class CalculatorUI extends JPanel {
	/**
	 * Remember the grid bag group in case the mapper UI needs to be updated.
	 */
	protected GridBagGroup myGBG;
	
	public CalculatorUI(CyAttributes attr, JDialog parent, CyNetwork network) {
	    this.myGBG = new GridBagGroup(this);

	    MiscGB.inset(this.myGBG.constraints, 5,5,5,5);
	    String[] attrNames = attr.getAttributeNames();
            // 20030916 cworkman added Arrays.sort()
            Arrays.sort(attrNames);
	    int i, yPos;
	    for (i = yPos = 0; i < mappings.size(); i++, yPos++) {
		MiscGB.insert(this.myGBG, new JLabel("Map Attribute:", SwingConstants.RIGHT),
			      0, yPos);
		MiscGB.insert(this.myGBG, Box.createHorizontalStrut(5), 1, yPos);
		ObjectMapping m = (ObjectMapping) mappings.get(i);

		// filter list of interactions
		String[] validAttr;
		Class [] okClass = (Class[]) acceptedDataClasses.get(i);
		if (okClass != null) {
		    Vector validAttrV = new Vector(attrNames.length);
		    for (int j = 0; j < attrNames.length; j++) {
			//Class attrClass = attr.getClass(attrNames[j]);
			Class attrClass = CyAttributesUtils.getClass(attrNames[j],attr);

			for (int k = 0; k < okClass.length; k++) {
			    if (okClass[k].isAssignableFrom(attrClass)) {
				validAttrV.add(attrNames[j]);
				break;
			    }
			}
		    }
		    validAttr = (String[]) validAttrV.toArray(new String[0]);
		}
		else {
		    validAttr = attrNames;
		}

		// add generic "ID" attribute
		Vector v = new Vector();
		v.add(ID);
		for (int lc = 0; lc < validAttr.length; lc++) {
			v.add(validAttr[lc]);
		}
		validAttr = (String[]) v.toArray(new String[0]);

		// create the JComboBox
		JComboBox attrBox = new JComboBox(validAttr);
		attrBox.setName("attrBox");
		// set the attrBox to the currently selected attribute
		String selectedAttr = m.getControllingAttributeName();
		// make no selection first, in case the selectedAttr doesn't exist
		attrBox.setSelectedIndex(-1);
		attrBox.setSelectedItem(selectedAttr);
	    
		MiscGB.insert(this.myGBG, attrBox, 2, yPos, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);
		// underlying mapping's UI
		JPanel mapperUI = m.getUI(parent, network);
		attrBox.addItemListener(new AttributeSelectorListener(parent, network, i, ++yPos, mapperUI));
		//MiscGB.insert(this.myGBG, mapperUI, 0, yPos, 3, 1, 2, 2, GridBagConstraints.BOTH);
		MiscGB.insert(this.myGBG, mapperUI, 0, yPos, 3, 1, 1, 0, GridBagConstraints.HORIZONTAL);

		// Add a blank JLabel at the bottom to take up extra spacce in the panel
		MiscGB.insert(this.myGBG, new JLabel(), 0, yPos+1, 3, 1, 1, 1, GridBagConstraints.BOTH);
	    }
	}

	/**
	 * AttributeSelectorListener listens for events on the JComboBoxes that
	 * select the controlling attribute for mappers contained in each calculator.
	 */
	protected class AttributeSelectorListener implements ItemListener {
	    private CyNetwork network;
	    private int mapIndex;
	    private int yPos;
	    private JPanel mapperUI;
	    private JDialog parent;
	    /**
	     * Constructs an AttributeSelectorListener for the ObjectMapping at
	     * index mapIndex.
	     * @param parent   parent JDialog
	     * @param network  passed to the mapping to get data values for the
	     *                 new attribute
	     * @param mapIndex Index of the mapping in the {@link #mappings} Vector
	     *                 to report changes in the selected
	     *                 attribute to.
	     * @param yPos     Position to add the mapping UI into master GBG when
	     *		       updating mapping UI.
	     * @param mapperUI Current mapper UI panel
	     */
	    protected AttributeSelectorListener(JDialog parent, CyNetwork network, int mapIndex, int yPos, JPanel mapperUI) {
		this.parent = parent;
		this.network = network;
		this.mapIndex = mapIndex;
		this.yPos = yPos;
		this.mapperUI = mapperUI;
	    }
	    
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    JComboBox c = (JComboBox) e.getItemSelectable();
		    String attrName = (String) c.getSelectedItem();
		    updateAttribute(attrName, network, this.mapIndex);
		    // change the panel referenced to get a new panel from the mapping
		    // to reflect the new mapped attribute.
		    remove(this.mapperUI);
		    this.mapperUI = ((ObjectMapping) mappings.get(mapIndex)).getUI(this.parent, network);
		    MiscGB.insert(myGBG, this.mapperUI, 0, this.yPos, 3, 1, 2, 2, GridBagConstraints.BOTH);
            parent.validate();
		}
	    }
	}
    }
    /**
     * Add a ChangeListener to the calcaultor. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param	l	ChangeListener to add
     */
    public void addChangeListener(ChangeListener l) {
        this.changeListeners.add(l);
    }

    /**
     * Remove a ChangeListener from the calcaultor. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param	l	ChangeListener to add
     */
    public void removeChangeListener(ChangeListener l) {
        this.changeListeners.remove(l);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created.
     *
     * UI classes should attach themselves with a listener to the calculator to be
     * notified about changes in the underlying data structures that require the UI
     * classes to fetch a new copy of the UI and display it.
     *
     */
    protected void fireStateChanged() {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = this.changeListeners.size() - 1; i>=0; i--) {
	    ChangeListener listener = (ChangeListener) this.changeListeners.get(i);
	    // Lazily create the event:
	    if (this.changeEvent == null)
		this.changeEvent = new ChangeEvent(this);
	    listener.stateChanged(this.changeEvent);
        }
    }

    /**
     * Returns a map of attribute names to single values.  
     * @param canonicalName The attribute name returned from the CyNode or CyEdge.
     * @return Map of the attribute names to values.
     */
    protected Map getAttrBundle(String canonicalName, CyAttributes cyAttrs) {
    	return CyAttributesUtils.getAttributes(canonicalName, cyAttrs);
    }

    abstract protected Map getAttrBundle(String canonicalName);

    public void apply(EdgeAppearance appr, Edge e, CyNetwork net){}; 

    public void apply(NodeAppearance appr, Node n, CyNetwork net){}; 
   
    protected Object getRangeValue(GraphObject obj) {
    	if ( obj == null )
		return null;
	String canonicalName = obj.getIdentifier();
	Map attrBundle = getAttrBundle(canonicalName);
	attrBundle.put(AbstractCalculator.ID, obj.getIdentifier());
	return getMapping(0).calculateRangeValue(attrBundle);
    }

    abstract public String getTypeName();
}


/*
  File: VizMapAttrTab.java 
  
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

package cytoscape.visual.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import cytoscape.CyNetwork;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.ObjectMapping;
import java.util.*;
import java.lang.reflect.Constructor;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
/**
 *  VizMapAttrTab implements a tab for each mappable attribute of the graph except
 *  the size tab, which is a special case.
 *  These tabs are displayed in the Set Visual Properties dialog.
 */
public class VizMapAttrTab extends VizMapTab {
  /**
   *	reference to calculator catalog
   */
  private CalculatorCatalog catalog;

  /** VisualMappingManager for the window */
  private VisualMappingManager VMM;

  /**
   * The type of this VizMapAttrTab. This should be one of the constants
   * defined in VizMapUI that identifies a particular visual attribute.
   * Utility methods in VizUIUtilites are used to convert this type into
   * a particular object or method of the current visual style.
   */
  private byte type;

  /**
   * The calculator whose UI is being displayed by this tab. Note that this
   * duplicates the calculator reference held by the current visual style.
   * This is done for convenience, rather than continually using a utility
   * method to figure out which calculator corresponds to the type field
   * of this object. It is the responsibility of every method in this class
   * to make sure this field is synchronized with the current state of the
   * current visual style. This field should only be changed via the
   * setCurrentCalculator method of this class, which additionally ensures
   * that the proper listener is attached to the current calculator.
   */
  private Calculator currentCalculator;

  /**
   *	the parent JDialog
   */
  private VizMapUI mainUIDialog;

  /** Default ValueDisplayer */
  private ValueDisplayer defaultValueDisplayer;

  /** Combo box for calculator selection */
  private JComboBox calcComboBox;

  /** Calculator UI */
  private GridBagGroup mapPanelGBG;
  private JPanel calcContainer;

  /**
   * the panel containing the calculator-specific UI provided
   * by the currently selected calculator
   */
  private JPanel calcPanel;

  /** Listener for calculator UI changes */
  protected CalculatorUIListener calcListener = new CalculatorUIListener();

  /**
   *	create a new tab representing the underlying type. Retrieve current
   *	calculator and default settings from the VMM.
   *
   *	@param	VMM	VisualMappingManager for the window
   *  @param	tabContainer	The containing JTabbedPane
   *  @param	tabIndex	index of this tab in tabContainer
   *	@param	n	Underlying network
   *	@param	type	One of types defined in {@link VisualMappingManager}
   *	@param	c
   */
  public VizMapAttrTab (VizMapUI mainUI, JTabbedPane tabContainer, int tabIndex, VisualMappingManager VMM, byte type) {
    super(new BorderLayout(), false);

    // set the name of this component appropriately
    setName(CalculatorFactory.getTypeName(type));

    this.VMM = VMM;
    this.mainUIDialog = mainUI;
    this.catalog = VMM.getCalculatorCatalog();
    this.type = type;

    // register to listen for changes in the catalog
    catalog.addChangeListener(new CatalogListener(), this.type);

    // register to listen for changes in the enclosing JTabbedPane
    tabContainer.addChangeListener(new TabContainerListener(tabIndex));
  }

  /**
   * Internal class to listen for possible changes in the state of the catalog.
   * When triggered, causes this class to rebuild the combo box for selecting
   * calculators.
   *
   * TODO: should check to see if the change was to calculators relevant to
   * this UI component, instead of always rebuilding the combo box.
   */
  private class CatalogListener implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
	    rebuildCalcComboBox();
	    validate();
	    repaint();
    }
  }

  /**
   * TabContainerListener refreshes the tab's UI when the tab becomes visible.
   * This ensures that the UI stays synchronized for attributes that reuse the
   * same calculator.
   */
  protected class TabContainerListener implements ChangeListener {
    protected int tabIndex;

    public TabContainerListener(int tabIndex) {
	    this.tabIndex = tabIndex;
    }
    public void stateChanged(ChangeEvent e) {
	    JTabbedPane source = (JTabbedPane) e.getSource();
	    if (source.getModel().getSelectedIndex() == tabIndex) {
        refreshUI();
      }
    }
  }

  /**
   *  Alert the VizMapAttrTab that the relevant visual style has changed.
   */
  public void visualStyleChanged() {
    // get current defaults
    Object defaultObj = VizUIUtilities.getDefault(VMM.getVisualStyle(), this.type);
    setCurrentCalculator( VizUIUtilities.getCurrentCalculator(VMM.getVisualStyle(), this.type) );

    if (defaultValueDisplayer == null) { // haven't initialized yet
      drawDefault(defaultObj);
      drawCalc();
    } else {
      rebuildCalcComboBox();
      defaultValueDisplayer.setObject(defaultObj);
    }
    refreshUI();
  }

  /**
   * Refreshes the panel that displays the UI for the currently selected calculator.
   * This method replaces the current panel with the panel provided by the current
   * calculator, or nothing if there is no currently selected calculator.
   */
  public void refreshUI() {
    if (this.calcPanel != null) {
	    this.calcContainer.remove(this.calcPanel);
    }
    if (this.currentCalculator != null) {
	    this.calcPanel = this.currentCalculator.getUI(this.mainUIDialog, VMM.getNetwork());
	    this.calcContainer.add(this.calcPanel);
    } else {
	    this.calcPanel = null;
    }    
    validate();
    repaint();
    mainUIDialog.pack();
  }

  /**
   * Changes the 'currentCalculator' field of this class to the new calculator
   * specified by the argument. A null argument is allowed. This method also
   * detaches the CalculatorUIListener from the old calculator, and attaches
   * it to the new one iff the argument is not null.
   *
   * This method should be called whenever the visual style is changed, or when
   * the calculator for this object's visual attribute is switched to a different
   * calculator. If the change is made by a member of this class, they should
   * call this method instead of directly changing the currentCalculator field
   * (to make sure that the listener is properly updated). If the change was
   * instead made directly to the underlying visual style, then a listener
   * should respond by calling this method with the new calculator.
   */
  protected void setCurrentCalculator(Calculator newCalculator) {
    if (this.currentCalculator != null) {
      this.currentCalculator.removeChangeListener(calcListener);
    }
    this.currentCalculator = newCalculator;
    if (newCalculator != null) {
      newCalculator.addChangeListener(calcListener);
    }
  }


  /**
   * Builds the panel for displaying and changing the default value for this
   * visual attribute.
   */
  protected void drawDefault(Object defaultObj) {
    JPanel outerDefPanel = new JPanel(false);
    outerDefPanel.setLayout(new BoxLayout(outerDefPanel,BoxLayout.X_AXIS));

    JPanel defPanel = new JPanel(false);
    defPanel.setLayout(new BoxLayout(defPanel, BoxLayout.Y_AXIS));
    Box content = new Box(BoxLayout.X_AXIS);

    // create the ValueDisplayer
    this.defaultValueDisplayer = ValueDisplayer.getDisplayFor(mainUIDialog,
                                                              getName(),
                                                              defaultObj);
    defaultValueDisplayer.addItemListener(new DefaultItemChangedListener());

    // create the button
    JButton defaultButton = new JButton("Change Default");
    // attach ActionListener from ValueDisplayer to button
    defaultButton.addActionListener(defaultValueDisplayer.getInputListener());

    // dump components into content Box
    content.add(Box.createHorizontalGlue());
    content.add(defaultButton);
    content.add(Box.createHorizontalStrut(3));
    content.add(defaultValueDisplayer);
    content.add(Box.createHorizontalGlue());

    // pad the default panel
    defPanel.add(Box.createVerticalStrut(3));
    defPanel.add(content);
    defPanel.add(Box.createVerticalStrut(3));

    // attach a border
    Border defBorder = BorderFactory.createLineBorder(Color.BLACK);
    defPanel.setBorder(BorderFactory.createTitledBorder(defBorder,
                                                        "Default",
                                                        TitledBorder.CENTER,
                                                        TitledBorder.TOP));
    defPanel.validate();

    ImageIcon imageIcon = getImageIcon();
    if(imageIcon==null)
	    this.add(defPanel, BorderLayout.NORTH);
    else {
      JButton tempB = new JButton();
	    tempB.setIcon(imageIcon);
	    outerDefPanel.add(tempB);
	    outerDefPanel.add(defPanel);
	    this.add(outerDefPanel, BorderLayout.NORTH);
    }
  }

  /**
   * Returns an ImageIcon suitable for this visual attribute, or null if we
   * don't have an icon for this particular visual attribute.
   */
  protected ImageIcon getImageIcon() {
    ImageIcon icon = null;
    if (this.type == VizMapUI.NODE_COLOR) {
      String imageFile = "images/nodeColorWheel.jpg";
      icon =  new ImageIcon(this.getClass().getResource(imageFile),"Node Color");
    } else if (this.type == VizMapUI.NODE_BORDER_COLOR) {
      String imageFile = "images/nodeBorderColorWheel.jpg";
      icon = new ImageIcon(this.getClass().getResource(imageFile),"Node Border Color");
    }
    return icon;
  }

  /**
   *	Listener for the ValueDisplayer to notify VizMapAttrTab when the default
   *	object changed.
   */
  private class DefaultItemChangedListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
	    // prevent bugs, could be removed later
	    if (e.getItemSelectable() == defaultValueDisplayer &&
          e.getStateChange() == ItemEvent.SELECTED) {
        Object newDefault = defaultValueDisplayer.getValue();
        VizUIUtilities.setDefault(VMM.getVisualStyle(), type, newDefault);
	    }
    }
  }

  /**
   * Creates the combo box for selecting a calculator and adds it to the
   * appropriate panel after first removing any previous combo box.
   */
  protected void rebuildCalcComboBox() {
    if (this.calcComboBox != null) {
	    mapPanelGBG.panel.remove(this.calcComboBox);
    }
    /* build the list of known calculators - each calculator has a toString()
       method that returns the name, so calculators can be passed to the
       JComboBox.
    */
    Collection calculators = catalog.getCalculators(this.type);
    Vector comboCalcs = new Vector();
    if ( calculators != null )
    	comboCalcs.addAll(calculators);


    //it's possible the current calculator isn't in the catalog; if so,
    //add it to the combo box
    if ( currentCalculator != null && !(calculators.contains(currentCalculator)) ) {
      comboCalcs.add(currentCalculator);
    }
    //add an extra entry at the beginning for no calculator
    comboCalcs.add( 0, new String("None") );
    this.calcComboBox = new JComboBox(comboCalcs);
    calcComboBox.setName("calcComboBox");

    // set the currently selected calculator
    if (this.currentCalculator == null) {
	    /* Index 0 is always the "None" string. However, setSelectedIndex(0) does not call
	       event handlers. Thus, in RmCalcListener, switchCalculator() is called explicitly.
	    */
	    this.calcComboBox.setSelectedIndex(0);
    }
    else {
	    this.calcComboBox.setSelectedItem(this.currentCalculator);
    }
    MiscGB.insert(mapPanelGBG, calcComboBox, 0, 0, 4, 1, 1, 0, GridBagConstraints.HORIZONTAL);
    // attach listener
    this.calcComboBox.addItemListener(new CalcComboSelectionListener());
  }

  /**
   * Listens to selection events on the calculator selection combo box. When
   * a selection occurs, calls switchCalculator with the new calculator.
   */
  private class CalcComboSelectionListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
        if (calcComboBox.getSelectedIndex() == 0) // "None" selected, use null
          switchCalculator(null);
        else {
          Object selected = calcComboBox.getSelectedItem();
          switchCalculator((Calculator) selected);
          refreshUI();
        }
	    }
    }
  }

  /**
   * Called by action listener on the calculator selection combo box. Switch
   * the current calculator to the selected calculator and place the calculator's
   * UI into the calculator UI panel.
   *
   * @param	calc		new Calculator to use for this tab's mapping
   */
  void switchCalculator(Calculator calc) {
    //do nothing if the new calculator is the same as the current one
    if (calc != null && calc.equals(this.currentCalculator)) {return;}

    setCurrentCalculator(calc); //handles listeners

    // tell the respective appearance calculators
    // this method doesn't fire an event to come back to us
    VizUIUtilities.setCurrentCalculator(VMM.getVisualStyle(), this.type, calc);

    //get the view of the new calculator
    refreshUI();
    // Commented out to prevent auto-updates
    // VMM.getNetworkView().redrawGraph(false, true);
  }

  /**
   * Draws the panel containing the calculator selection combo box and the
   * calculator manipulation buttons. The panel containing the UI for the
   * current calculator is added later by refreshUI.
   * Called only once; afterwards the UI is refreshed through either
   * rebuildCalcComboBox, which refreshes the calculator selection combo box,
   * or refreshUI which refreshes the current calculator view.
   */
  protected void drawCalc() {
    //this.mapPanel = new JPanel(false);
    //mapPanel.setLayout(new BoxLayout(mapPanel, BoxLayout.Y_AXIS));

    // grid bag layout
    this.mapPanelGBG = new GridBagGroup("Mapping");
    MiscGB.pad(mapPanelGBG.constraints, 2, 2);
    MiscGB.inset(mapPanelGBG.constraints, 3);

    // Initialize here
    this.calcContainer = new JPanel(new GridLayout(), false);

    rebuildCalcComboBox();
    
    // new calculator button
    JButton newCalc = new JButton("New");
    newCalc.addActionListener(new NewCalcListener());
    newCalc.setToolTipText("Create a new calculator");
    //MiscGB.insert(mapPanelGBG, newCalc, 0, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

    // duplicate calculator button
    JButton dupeCalc = new JButton("Duplicate");
    dupeCalc.addActionListener(new DupeCalcListener());
    dupeCalc.setToolTipText("Create a copy of this calculator");
    //MiscGB.insert(mapPanelGBG, dupeCalc, 1, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

    // rename calculator button
    JButton renCalc = new JButton("Rename");
    renCalc.addActionListener(new RenCalcListener());
    renCalc.setToolTipText("Rename this calculator");
    //MiscGB.insert(mapPanelGBG, renCalc, 2, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

    // remove calculator button
    JButton rmCalc = new JButton("Delete");
    rmCalc.addActionListener(new RmCalcListener());
    rmCalc.setToolTipText("Permanently delete this calculator");
    //MiscGB.insert(mapPanelGBG, rmCalc, 3, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

    JPanel btnPanel = new JPanel();
    btnPanel.add(newCalc);
    btnPanel.add(dupeCalc);
    btnPanel.add(renCalc);
    btnPanel.add(rmCalc);
   
    MiscGB.insert(mapPanelGBG, btnPanel, 0, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);
    
    // add to gridbag
    MiscGB.insert(mapPanelGBG, calcContainer, 0, 2, 4, 1, 5, 5, GridBagConstraints.BOTH);
    add(mapPanelGBG.panel, BorderLayout.CENTER);
    //add(mapPanel);
  }

  // new calculator button pressed
  private class NewCalcListener extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
	    // get available mappings
	    Set mapperNames = catalog.getMappingNames();
	    // convert to array for JOptionPane
	    Object[] mapperArray = mapperNames.toArray();

	    // show JOptionPane with the available mappers
	    String selectedMapper = (String) JOptionPane.showInputDialog(mainUIDialog,
                                                                   "Choose a mapper",
                                                                   "New Calculator",
                                                                   JOptionPane.QUESTION_MESSAGE,
                                                                   null,
                                                                   mapperArray,
                                                                   mapperArray[0]);
	    if (selectedMapper == null)
        return;

	    // get a name for the new calculator
	    String calcName = getCalculatorName(null);
	    if (calcName == null)
        return;

	    // create the new calculator
	    // get the selected mapper
	    Class mapperClass = catalog.getMapping(selectedMapper);
	    // create the selected mapper
	    Class[] conTypes = {Object.class, byte.class};
	    Constructor mapperCon;
	    try {
        mapperCon = mapperClass.getConstructor(conTypes);
	    }
	    catch (NoSuchMethodException exc) {
        // show error message, mapper was not programmed correctly
        System.err.println("Invalid mapper " + mapperClass.getName());
        JOptionPane.showMessageDialog(mainUIDialog,
                                      "Mapper " + mapperClass.getName() + " does not have an acceptable constructor. See documentation for ObjectMapper.",
                                      "Invalid Mapper",
                                      JOptionPane.ERROR_MESSAGE);
        return;
	    }

	    // create the mapper
	    byte mapType; // node or edge calculator
	    switch(type) {
	    case VizMapUI.EDGE_COLOR:
	    case VizMapUI.EDGE_LINETYPE:
	    case VizMapUI.EDGE_SRCARROW:
	    case VizMapUI.EDGE_TGTARROW:
	    case VizMapUI.EDGE_LABEL:
	    case VizMapUI.EDGE_FONT_FACE:
	    case VizMapUI.EDGE_FONT_SIZE:
	    case VizMapUI.EDGE_TOOLTIP:
        mapType = ObjectMapping.EDGE_MAPPING;
        break;
	    default:
        mapType = ObjectMapping.NODE_MAPPING;
	    }
      Object defaultObj = VizUIUtilities.getDefault(VMM.getVisualStyle(), type);
	    Object[] invokeArgs = {defaultObj, new Byte(mapType)};
	    ObjectMapping mapper = null;
	    try {
        mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
	    }
	    catch (Exception exc) {
        System.err.println("Error creating mapping");
        JOptionPane.showMessageDialog(mainUIDialog,
                                      "Error creating mapping " + mapperClass.getName(),
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
	    }
	    // create and add a generic calculator based on this tab's type
	    Calculator calc = CalculatorFactory.newDefaultCalculator(type,calcName,mapper);
	    
	    // set current calculator to the new calculator
	    switchCalculator(calc);  //handles listeners
	    // notify the catalog - this triggers events that refresh the UI
	    catalog.addCalculator(calc);
    }
  }

  // duplicate calculator button pressed
  private class DupeCalcListener extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
	    Calculator clone = duplicateCalculator(currentCalculator);
	    // die if user cancelled in the middle of duplication
	    if (clone == null) {return;}
	    switchCalculator(clone);
      //this triggers an event that triggers rebuilding the combo box
	    catalog.addCalculator(clone);
    }
  }

  // duplicate the given calculator, prompting for a name
  private Calculator duplicateCalculator(Calculator c) {
    Calculator clone = null;
    try {
	    clone = (Calculator) c.clone();
    }
    catch (CloneNotSupportedException exc) { // this will never happen
	    System.err.println("Fatal error - Calculator didn't support Cloneable");
	    exc.printStackTrace();
	    return null;
    }
    // get new name for clone
    String newName = getCalculatorName(clone);
    if (newName == null) {return null;}
    clone.setName(newName);
    return clone;
  }

  private String getCalculatorName(Calculator c) {
    // default to the next available name for c
    String suggestedName = null;
    if (c != null) {
	    suggestedName = this.catalog.checkCalculatorName(c.toString(), this.type);
    }

    // keep prompting for input until user cancels or we get a valid name
    while(true) {
	    String ret = (String) JOptionPane.showInputDialog(mainUIDialog,
                                                        "New name for calculator",
                                                        "Calculator Name Input",
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        null, null,
                                                        suggestedName);
	    if (ret == null){
	    		// user hit cancel
	    		return null;
	    }else if(ret.length() == 0) {
	    		// user hit OK but entered no name
	    		JOptionPane.showMessageDialog(mainUIDialog, "Please enter a name for the calculator.", "Calculator Name Input Error", JOptionPane.ERROR_MESSAGE);
	    		continue;// ask again
	    	}
	    String newName = catalog.checkCalculatorName(ret, this.type);
	    if (newName.equals(ret)) {return ret;}
	    int alt = JOptionPane.showConfirmDialog(mainUIDialog,
                                              "Calculator with name " + ret + " already exists,\nrename to " + newName + " okay?",
                                              "Duplicate calculator name",
                                              JOptionPane.YES_NO_OPTION,
                                              JOptionPane.WARNING_MESSAGE,
                                              null);
	    if (alt == JOptionPane.YES_OPTION) {return newName;}
    }
  }

  // rename calculator button pressed
  private class RenCalcListener extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
	    // get the new name, keep prompting for input until a valid input is received
	    String calcName = getCalculatorName(currentCalculator);
	    if (calcName == null) {return;}
      //this triggers an event that triggers rebuilding the combo box
	    catalog.renameCalculator(currentCalculator, calcName);
    }
  }

  // remove calculator button pressed
  private class RmCalcListener extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
	    if (currentCalculator == null) {return;}
	    // check duplication
	    Vector conflicts = mainUIDialog.checkCalculatorUsage(currentCalculator);
	    // if only one conflict reported, that's myself, so ignore.
	    if (conflicts.size() != 0 && !(conflicts.size() == 1 && ((Vector)conflicts.get(0)).size() == 2)) {
        StringBuffer errMsg = new StringBuffer("Calculator ");
        errMsg.append(currentCalculator.toString());
        errMsg.append(" currently in use by:<br><ul>");
        for (int i = 0; i < conflicts.size(); i++) {
          Vector subSelect = (Vector) conflicts.get(i);
          errMsg.append("<li>");
          errMsg.append(subSelect.get(0));
          errMsg.append("<ul>");
          for (int j = 1; j < subSelect.size(); j++) {
            errMsg.append("<li>");
            errMsg.append(subSelect.get(j));
          }
          errMsg.append("</ul>");
        }
        errMsg.append("</ul><br>Do you still want to delete this calculator?");

        JEditorPane errPane = new JEditorPane("text/html", errMsg.toString());
        errPane.setEditable(false);
        //errPane.setBackground(new Color(205,206,205));
        errPane.setBackground(null);
        int conf = JOptionPane.showConfirmDialog(mainUIDialog,
                                                 errPane,
                                                 "Calculator In Use",
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.NO_OPTION) {return;}
	    } else {
        //let's still make sure the user really wanted to do this
        String s = "Are you sure you want to permanently delete this calculator?";
        int conf = JOptionPane.showConfirmDialog(mainUIDialog, s,
                                                 "Confirm Remove Calculator",
                                                 JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.NO_OPTION) {return;}
      }

	    Calculator temp = currentCalculator;
      switchCalculator(null);
	    catalog.removeCalculator(temp); // triggers events that switch the calculator
    }
  }



  /**
   * CalculatorUIListener listens to the current calculator for the visual
   * attribute displayed by this tab. When that calculator is modified, this
   * listener updates the UI for that calculator and redraws the graph.
   *
   * A single instance of this listener is maintained by this class and
   * is removed and reattached when the current calculator changes.
   */
  protected class CalculatorUIListener implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      refreshUI();
	    VMM.getNetworkView().redrawGraph(false, true);
    }
  }


  /**
   * Check that the calculator is not selected by other objects. If so,
   * pop up a dialog offering to duplicate the calculator.
   *
   * @param	c	newly selected calculator
   */
  VizMapTab checkCalcSelected(Calculator c) {
    if (this.currentCalculator == c)
	    return this;
    return null;
  }

  /**
   * Designed as a hook for VizMapSizeTab to set the combo box display when
   * node width/height are locked. Changes the display, which fires an event
   * alerting calcComboSelectionListener which changes the selected calculator.
   */
  void setComboBox(Calculator c) {
    if (c == null) { // select "None"
	    this.calcComboBox.setSelectedIndex(0);
    }
    else {
	    this.calcComboBox.setSelectedItem(c);
    }
  }
}

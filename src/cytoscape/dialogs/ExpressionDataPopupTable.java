
/*
  File: ExpressionDataPopupTable.java 
  
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

// ExpressionDataPopupTable


//---------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.AbstractTableModel;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import cytoscape.data.ExpressionData;
import cytoscape.data.mRNAMeasurement;
//---------------------------------------------------------------------------------------
public class ExpressionDataPopupTable extends JDialog {
  Frame mainFrame;
  ExpressionDataPopupTable popupTable;
  private JTable table;
  ExpressionData geneExpressionInfo = null;

//---------------------------------------------------------------------------------------
/*
 *  a ctor for single condition, multiple genes
**/
public ExpressionDataPopupTable (Frame parentFrame, String [] geneNames,
                                 String conditionName,
                                 ExpressionData geneExpressionInfo)
{
  super (parentFrame, false);
  mainFrame = parentFrame;
  setTitle (conditionName);
  this.geneExpressionInfo = geneExpressionInfo;
  popupTable = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  table = new JTable (new MultipleGenesTableModel (geneNames, conditionName));
  // table.setPreferredScrollableViewportSize (new Dimension (400, 100));
  table.setDefaultRenderer (JButton.class,
                  new ButtonCellRenderer (table.getDefaultRenderer (JButton.class)));
  table.addMouseListener (new MyMouseListener (table));

  //table.getColumnModel().getColumn(2).setPreferredWidth (6);
  //table.getColumnModel().getColumn(3).setPreferredWidth (8);

  JScrollPane scrollPane = new JScrollPane (table);
  panel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton okButton = new JButton ("OK");
  okButton.addActionListener (new OKAction (this));
  buttonPanel.add (okButton, BorderLayout.CENTER);
  pack ();
  panel.add (buttonPanel, BorderLayout.SOUTH);

  setContentPane (panel);

} // ExpressionDataPopupTable ctor
//------------------------------------------------------------------------------------
/*
 *  a ctor for single gene, multiple conditions
**/
public ExpressionDataPopupTable (Frame parentFrame, String geneName,
                                 String [] conditionNames,
                                 ExpressionData geneExpressionInfo)
{
  super (parentFrame, false);
  setTitle (geneName);
  this.geneExpressionInfo = geneExpressionInfo;
  popupTable = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  table = new JTable (new MultipleConditionsTableModel (geneName, conditionNames));
  table.setPreferredScrollableViewportSize (new Dimension (400, 400));
  table.setDefaultRenderer (JButton.class,
                  new ButtonCellRenderer (table.getDefaultRenderer (JButton.class)));
  table.addMouseListener (new MyMouseListener (table));

  JScrollPane scrollPane = new JScrollPane (table);
  panel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton okButton = new JButton ("OK");
  okButton.addActionListener (new OKAction (this));
  buttonPanel.add (okButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);

  setContentPane (panel);

} // ExpressionDataPopupTable ctor
//------------------------------------------------------------------------------------
public class OKAction extends AbstractAction {
  private JDialog dialog;

  OKAction (JDialog popup) {super (""); this.dialog = popup;}

  public void actionPerformed (ActionEvent e) {
    dialog.dispose ();
    }

} // QuitAction
//-----------------------------------------------------------------------------------
class MultipleGenesTableModel extends AbstractTableModel {
  String [] columnNames;
  Object [][] data;

  public MultipleGenesTableModel (String [] geneNames, String conditionName) {
    columnNames = new String [3];
    columnNames [0] = "GENE";
    columnNames [1] = "RATIO";
    columnNames [2] = "LAMBDA";
    int geneCount = geneNames.length;
    data = new Object [geneCount][3];
    for (int i=0; i < geneNames.length; i++) {
      mRNAMeasurement measurement =
         geneExpressionInfo.getMeasurement (geneNames [i], conditionName);
      final String condition = conditionName;
      final String name = geneNames [i];
      if (measurement != null) {
        data [i][1] = new Double (measurement.getRatio ());
        data [i][2] = new Double (measurement.getSignificance ());
        }
      JButton button = new JButton (geneNames [i]);
      button.setToolTipText ("display expression values for all conditions");
      data [i][0] = button;
      ((JButton)data[i][0]).addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent e) {
        //PopupTextArea text = new PopupTextArea (popupTable, name, name);
        //text.setLocationRelativeTo (popupTable);
         String [] conditions = geneExpressionInfo.getConditionNames ();
         ExpressionDataPopupTable crossConditionsTable =
           new ExpressionDataPopupTable (mainFrame, name, conditions, geneExpressionInfo);
         crossConditionsTable.pack ();
         crossConditionsTable.setLocationRelativeTo (mainFrame);
         crossConditionsTable.setVisible (true);
        }});
      } // for i
    } // ctor

  public String getColumnName (int col) { return columnNames[col];}
  public int getColumnCount () { return columnNames.length;}
  public int getRowCount () { return data.length; }
  public Object getValueAt (int row, int col) { return data[row][col];}
  public boolean isCellEditable (int row, int col) {return false;}
  public Class getColumnClass (int column) {return getValueAt (0, column).getClass();}

} // inner class MultipleGenesTableModel
//--------------------------------------------------------------------------------------
class MultipleConditionsTableModel extends AbstractTableModel {
  String [] columnNames;
  Object [][] data;

  public MultipleConditionsTableModel (String geneName, String [] conditionNames) {
    columnNames = new String [3];
    columnNames [0] = "CONDITION";
    columnNames [1] = "RATIO";
    columnNames [2] = "LAMBDA";
    int conditionCount = conditionNames.length;
    data = new Object [conditionCount][3];
    for (int i=0; i < conditionNames.length; i++) {
      mRNAMeasurement measurement =
         geneExpressionInfo.getMeasurement (geneName, conditionNames [i]);
      final String condition = conditionNames [i];
      data [i][0] = condition;
      if (measurement == null) {
        data [i][1] = new Double (-999999.99);
        data [i][2] = new Double (-999999.99);
        }
      else {
        data [i][1] = new Double (measurement.getRatio ());
        data [i][2] = new Double (measurement.getSignificance ());
        }
      //JButton button = new JButton (geneNames [i]);
      //data [i][0] = button;
      //((JButton)data[i][0]).addActionListener (new ActionListener () {
      //  public void actionPerformed (ActionEvent e) {
      //  PopupTextArea text = new PopupTextArea (popupTable, name, name);
      //  text.setLocationRelativeTo (popupTable);
      //  }});
      } // for i
    } // ctor

  public String getColumnName (int col) { return columnNames[col];}
  public int getColumnCount () { return columnNames.length;}
  public int getRowCount () { return data.length; }
  public Object getValueAt (int row, int col) { return data[row][col];}
  public boolean isCellEditable (int row, int col) {return false;}
  public Class getColumnClass (int column) {return getValueAt (0, column).getClass();}

} // inner class MultipleConditionsTableModel
//--------------------------------------------------------------------------------------
class ButtonCellRenderer implements TableCellRenderer {

  private TableCellRenderer defaultRenderer;

  public ButtonCellRenderer (TableCellRenderer renderer) {
   defaultRenderer = renderer;
   }

  public Component getTableCellRendererComponent (JTable table, Object value,
                                                  boolean isSelected,
                                                  boolean hasFocus,
                                                  int row, int column)
  {
    if (value instanceof Component)
      return (Component) value;
    else
      return defaultRenderer.getTableCellRendererComponent (
              table, value, isSelected, hasFocus, row, column);
  }

} // inner class ButtonCellRenderer
//-------------------------------------------------------------------------------
class MyMouseListener implements MouseListener
{
  private JTable table;

  public MyMouseListener (JTable table) {
    this.table = table;
    }

  private void forwardEventToButton (MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel ();
    int column = columnModel.getColumnIndexAtX (e.getX ());
    int row  = e.getY() / table.getRowHeight();
    Object value;
    JButton button;
    MouseEvent buttonEvent;
    if (row >= table.getRowCount () || row < 0 ||
        column >= table.getColumnCount() || column < 0)
      return;
    value = table.getValueAt (row, column);
    boolean isButton = value instanceof JButton;
    if (!isButton) return;
    button = (JButton) value;
    buttonEvent = (MouseEvent) SwingUtilities.convertMouseEvent (table, e, button);
    // button.dispatchEvent (buttonEvent);
    button.doClick ();
    table.repaint ();
    }
   public void mouseClicked  (MouseEvent e) {forwardEventToButton(e);}
   public void mouseEntered  (MouseEvent e) {}
   public void mouseExited   (MouseEvent e) {}
   public void mousePressed  (MouseEvent e) {}
   public void mouseReleased (MouseEvent e) {}

} // inner class MyMouseListener
//-------------------------------------------------------------------------------
} // class ExpressionDataPopupTable



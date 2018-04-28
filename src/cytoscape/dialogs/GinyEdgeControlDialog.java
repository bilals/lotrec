
/*
  File: GinyEdgeControlDialog.java 
  
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

// EdgeControlDialog


//---------------------------------------------------------------------------------------
// $Revision: 8901 $
// $Date: 2006-11-21 08:39:28 -0800 (Tue, 21 Nov 2006) $
// $Author: mcreech $
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

import java.util.*;

import giny.view.*;
import giny.model.*;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.actions.GinyUtils;
//--------------------------------------------------------------------------------------
/**
 * present a JTree with edge attributes names as top level nodes, and attribute values
 * as child nodes; any node/s, once selected, are operated on by buttons in the bottom
 * of the dialog box.
 */
public class GinyEdgeControlDialog extends JDialog {

  CyNetworkView networkView;
  GraphView view;
  String [] edgeNames;
  HashMap edgeNamesHash;
  TreePath [] selectedTreePaths;
  CyAttributes edgeAttributes;
  JTree tree;
//--------------------------------------------------------------------------------------
public GinyEdgeControlDialog (CyNetworkView networkView,
                          HashMap edgeNamesHash, String title)

{
  super ();
  this.networkView = networkView;
  this.edgeNamesHash = edgeNamesHash;
  this.view = networkView.getView();
  //this.graphHider = networkView.getGraphHider ();
  this.edgeAttributes = Cytoscape.getEdgeAttributes();
  setTitle (title);
  setContentPane (createTreeViewGui ());

} // EdgeControlDialog ctor
//--------------------------------------------------------------------------------------
JPanel createTreeViewGui ()
{
  JPanel contentPane = new JPanel ();
  contentPane.setLayout (new BorderLayout ());

  JScrollPane scrollPane = new JScrollPane (createTreeView (edgeNamesHash));

  contentPane.add (scrollPane, BorderLayout.CENTER);

  JPanel actionButtonPanel = new JPanel ();
  actionButtonPanel.setLayout (new GridLayout(4, 2));

  JButton hideButton= new JButton ("Hide");
  JButton hideOthersButton= new JButton ("Hide Others");
  JButton hideAllButton= new JButton ("Hide All");
  JButton showAllButton= new JButton ("Show All");

  JButton selectButton= new JButton ("Select");
  JButton selectOthersButton= new JButton ("Select Others");
  JButton selectAllButton= new JButton ("Select All");
  JButton deselectAllButton= new JButton ("Deselect All");

  JButton okButton= new JButton ("OK");


  actionButtonPanel.add (selectButton);
  actionButtonPanel.add (hideButton);

  actionButtonPanel.add (selectOthersButton);
  actionButtonPanel.add (hideOthersButton);

  actionButtonPanel.add (selectAllButton);
  actionButtonPanel.add (hideAllButton);

  actionButtonPanel.add (deselectAllButton);
  actionButtonPanel.add (showAllButton);

  hideButton.addActionListener (new HideAction ());
  hideOthersButton.addActionListener (new HideOthersAction ());
  hideAllButton.addActionListener (new HideAllAction ());
  showAllButton.addActionListener (new ShowAllAction ());

  selectButton.addActionListener (new SelectAction ());
  selectAllButton.addActionListener (new SelectAllAction ());
  deselectAllButton.addActionListener (new DeselectAllAction ());
  selectOthersButton.addActionListener (new SelectOthersAction ());

  okButton.addActionListener (new OKAction ());

  JPanel allButtonsPanel = new JPanel ();
  allButtonsPanel.setLayout (new BorderLayout ());
  allButtonsPanel.add (actionButtonPanel, BorderLayout.CENTER);
  allButtonsPanel.add (okButton, BorderLayout.SOUTH);

  contentPane.add (allButtonsPanel, BorderLayout.SOUTH);

  return contentPane;

} // createTreeViewGui
//--------------------------------------------------------------------------------------
protected JTree createTreeView (HashMap edgeNamesHash)
{
  DefaultMutableTreeNode root = new DefaultMutableTreeNode ("Edge Attributes");
  createTreeNodes (root, edgeNamesHash);
  tree = new JTree (root);
  tree.addTreeSelectionListener (new MyTreeSelectionListener ());
  return tree;

} // createTreeView
//--------------------------------------------------------------------------------------
class MyTreeSelectionListener implements TreeSelectionListener {

  public void valueChanged (TreeSelectionEvent e) {
    DefaultMutableTreeNode node =
       (DefaultMutableTreeNode) tree.getLastSelectedPathComponent ();
    selectedTreePaths = tree.getSelectionPaths ();
    } // valueChanged

} // inner class MyTreeSelectionListener
//-----------------------------------------------------------------------------------
protected void createTreeNodes (DefaultMutableTreeNode root, HashMap edgeNamesHash)
{
  DefaultMutableTreeNode branch = null;
  DefaultMutableTreeNode leaf = null;
  String [] topLevelNames = (String []) edgeNamesHash.keySet().toArray (new String [0]);
  java.util.Arrays.sort (topLevelNames, String.CASE_INSENSITIVE_ORDER);

  for (int i=0; i < topLevelNames.length; i++) {
    branch = new DefaultMutableTreeNode (topLevelNames [i]);
    String [] children = (String []) edgeNamesHash.get (topLevelNames [i]);
    java.util.Arrays.sort (children, String.CASE_INSENSITIVE_ORDER);
    for (int j=0; j < children.length; j++)
      branch.add (new DefaultMutableTreeNode (children [j]));
    root.add (branch);
    } // for i

} // createTreeNodes
//-----------------------------------------------------------------------------------
class SelectAction extends AbstractAction {
  SelectAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    if (selectedTreePaths == null || selectedTreePaths.length == 0) {
      GinyEdgeControlDialog.this.getToolkit().beep ();
      return;
      }

    GinyUtils.deselectAllEdges(view);
    for (int i=0; i < selectedTreePaths.length; i++)
      selectEdgesByName (selectedTreePaths [i]);
    networkView.redrawGraph(false, true);
    } // actionPerformed

} // SelectAction
//------------------------------------------------------------------------------
class HideAction extends AbstractAction {
  HideAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    if (selectedTreePaths == null || selectedTreePaths.length == 0) {
      GinyEdgeControlDialog.this.getToolkit().beep ();
      return;
      }

    GinyUtils.unHideAllEdges(view);
    String action = e.getActionCommand ();
    for (int i=0; i < selectedTreePaths.length; i++)
      hideEdgesByName (selectedTreePaths [i]);
    networkView.redrawGraph(false, true);
    } // actionPerformed

} // HideButtonAction
//------------------------------------------------------------------------------
boolean pathMatchesEdge (String edgeName, TreePath treePath, CyAttributes edgeAttributes)
{
  Object [] objPath = treePath.getPath ();
  String [] pathNames = new String [objPath.length];

  for (int i=0; i < pathNames.length; i++)
    pathNames [i] = objPath [i].toString ();

  int pathLength = pathNames.length;

  if (pathLength < 2)
    return false;

  if (!edgeAttributes.hasAttribute (edgeName, pathNames [1]))
    return false;

  if (pathLength == 2)
    return true;

  if (pathLength == 3) {
    java.util.List l = edgeAttributes.getListAttribute(edgeName, pathNames[1]);
    String[] values = (String[]) l.toArray(new String[0]);
    for (int i=0; i < values.length; i++)
      if (values [i].equalsIgnoreCase (pathNames [2]))
        return true;
    } // pathLength == 3

   return false;

} // pathMatchesEdge
//------------------------------------------------------------------------------
protected void hideEdgesByName (TreePath treePath)
{
  java.util.List list = view.getEdgeViewsList();

  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();

    String edgeName = ev.getEdge().getIdentifier();
    if (pathMatchesEdge (edgeName, treePath, edgeAttributes))
      view.hideGraphObject( ev );
    } // for ec
} // hideEdgesByName
//------------------------------------------------------------------------------
protected void hideOtherEdges ()
{
  Vector keepVisibleList = new Vector ();

  if (selectedTreePaths == null) return;
  java.util.List list = view.getEdgeViewsList();

  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();

    String canonicalName = ev.getEdge().getIdentifier();
    for (int p=0; p < selectedTreePaths.length; p++) {
      TreePath treePath = selectedTreePaths [p];
      if (pathMatchesEdge (canonicalName, treePath, edgeAttributes))
        keepVisibleList.add (ev);
      } // for p
   } // for ec

  list = view.getEdgeViewsList();

  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();
    if (!keepVisibleList.contains (ev))
      view.hideGraphObject(ev);
    }

} // hideOtherEdges
//------------------------------------------------------------------------------
protected void inverseHideEdgesByName (TreePath treePath)
{ // this is the same as hideEdgesByName action ... Bug?
  java.util.List list = view.getEdgeViewsList();

  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();

    String edgeName = ev.getEdge().getIdentifier();
    if (!pathMatchesEdge (edgeName, treePath, edgeAttributes))
      view.hideGraphObject( ev );
    } // for ec

} // inverseHideEdgesByName
//------------------------------------------------------------------------------
protected void selectEdgesByName (TreePath treePath)
{
  java.util.List list = view.getEdgeViewsList();
  Vector vector = new Vector();
  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();
    String canonicalName = ev.getEdge().getIdentifier();
    if (pathMatchesEdge (canonicalName, treePath, edgeAttributes))
      vector.add (ev);
    } // for ec

   for (Iterator vi = vector.iterator(); vi.hasNext(); ) {
       EdgeView edge = (EdgeView)vi.next();
       edge.setSelected(true);
   }


} // selectEdgesByName
//------------------------------------------------------------------------------
class SelectOthersAction extends AbstractAction {
  SelectOthersAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    selectOtherEdges ();
    networkView.redrawGraph(false, true);
    }

} // SelectOthersAction
//------------------------------------------------------------------------------
class DeselectAllAction extends AbstractAction {
  DeselectAllAction () {super ("");}
  public void actionPerformed (ActionEvent e) {
    deselectAllEdges ();
    networkView.redrawGraph(false, true);
    }

} // DeselectAllAction
//------------------------------------------------------------------------------
class HideOthersAction extends AbstractAction {

  HideOthersAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    GinyUtils.unHideAllEdges(view);
    hideOtherEdges ();
    networkView.redrawGraph(false, true);
    }

} // HideOthersAction
//------------------------------------------------------------------------------
class HideAllAction extends AbstractAction {

  HideAllAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    GinyUtils.hideAllEdges(view);
    }

} // HideAllAction
//------------------------------------------------------------------------------
class SelectAllAction extends AbstractAction {

  SelectAllAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    selectAllEdges ();
    networkView.redrawGraph(false, true);
    }

} // HideAllAction
//------------------------------------------------------------------------------
class ShowAllAction extends AbstractAction {

  ShowAllAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    GinyUtils.unHideAllEdges(view);
    }

} // ShowAllAction
//------------------------------------------------------------------------------
protected void selectAllEdges ()
{

  GinyUtils.selectAllEdges(view);


}
//------------------------------------------------------------------------------
protected void invertEdgeSelection ()
{
  GinyUtils.invertSelectedEdges(view);
}
//------------------------------------------------------------------------------
protected void selectOtherEdges ()
{
  Vector keepUnselectedList = new Vector ();

  java.util.List list = view.getEdgeViewsList();

  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();
    String canonicalName = ev.getEdge().getIdentifier();
    for (int p=0; p < selectedTreePaths.length; p++) {
      TreePath treePath = selectedTreePaths [p];
      if (pathMatchesEdge (canonicalName, treePath, edgeAttributes))
        keepUnselectedList.add (ev);
      } // for p
   } // for ec

  Vector selectList = new Vector ();
  list = view.getEdgeViewsList();

  for (Iterator i = list.iterator(); i.hasNext(); ) {
    EdgeView ev = (EdgeView)i.next();

    ev.setSelected (!keepUnselectedList.contains (ev));
    }


} // selectOtherEdges
//------------------------------------------------------------------------------
protected void deselectAllEdges ()
{
  GinyUtils.deselectAllEdges(view);

}
//------------------------------------------------------------------------------
private void placeInCenter ()
{
  GraphicsConfiguration gc = getGraphicsConfiguration ();
  int screenHeight = (int) gc.getBounds().getHeight ();
  int screenWidth = (int) gc.getBounds().getWidth ();
  int windowWidth = getWidth ();
  int windowHeight = getHeight ();
  setLocation ((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);

} // placeInCenter
//------------------------------------------------------------------------------
public class OKAction extends AbstractAction 
{
  OKAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    GinyEdgeControlDialog.this.dispose ();
    }

} // OKAction
//-----------------------------------------------------------------------------
} // class EdgeControlDialog



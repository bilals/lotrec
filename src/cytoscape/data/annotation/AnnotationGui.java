
/*
  File: AnnotationGui.java 
  
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

// AnnotationGui.java:  the user interface for cytoscape annotations


// $Revision: 8901 $
// $Date: 2006-11-21 08:39:28 -0800 (Tue, 21 Nov 2006) $
// $Author: mcreech $
package cytoscape.data.annotation;

import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.servers.BioDataServer;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

//----------------------------------------------------------------------------------------
/**
 */
public class AnnotationGui extends CytoscapeAction {

	protected BioDataServer dataServer;
	protected String defaultSpecies;
	AnnotationDescription[] annotationDescriptions;
	JTree availableAnnotationsTree;
	JTree currentAnnotationsTree;
	JList actionListBox;
	DefaultListModel actionListModel;
	int actionListBoxCurrentSelection;
	TreePath annotationPath;
	String currentAnnotationCategory;

	JDialog mainDialog;
	JButton annotateNodesButton;
	JButton layoutByAnnotationButton;
	JButton addSharedAnnotationEdgesButton;
	JButton deleteCreatedObjectsButton;

	CyNetworkView networkView;
	CyNetwork network;
	
	// ----------------------------------------------------------------------------------------
	public AnnotationGui() {
//		super("Ontology Mapper...");
//		setPreferredMenu("File.Import.Ontology");

		/*
		 * dataServer = cyWindow.getCytoscapeObj().getBioDataServer (); if
		 * (dataServer != null) annotationDescriptions =
		 * dataServer.getAnnotationDescriptions ();
		 */

		// defaultSpecies =
		// Semantics.getDefaultSpecies(cyWindow.getNetwork(),cyWindow.getCytoscapeObj()
		// );
	} // ctor
	
	public AnnotationGui( boolean isMenu, ImageIcon icon ) {
		super("Ontology Mapper...", icon);
		setPreferredMenu("File.Import.Ontology");
	}
	
	

	// ----------------------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {

		
		networkView = Cytoscape.getCurrentNetworkView();
		network = networkView.getNetwork();

		dataServer = Cytoscape.getBioDataServer();
		if (dataServer == null) {
			JOptionPane.showMessageDialog(null,
					"No annotations are available.", "Error!",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else if (network.getNodeCount() == 0) {
			JOptionPane.showMessageDialog(null,
					"No nodes in network to annotate.", "Error!",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			annotationDescriptions = dataServer.getAnnotationDescriptions();
		}

		
		Semantics.applyNamingServices(network);
		
		defaultSpecies = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
		// if (this.mainDialog == null) {
		mainDialog = new Gui("Import Annotation from Ontology Data");
		mainDialog.pack();
		mainDialog.setLocationRelativeTo(Cytoscape.getDesktop());
		// }
		mainDialog.setVisible(true);
	} // actionPerformed
	// ----------------------------------------------------------------------------------------
	protected class Gui extends JDialog {

		Gui(String title) {
			super(Cytoscape.getDesktop(), false);
			setTitle(title);
			setContentPane(createWidgets());
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}

		// ------------------------------------------------------------------------------
		private JPanel createWidgets() {
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());

			JPanel topPanel = new JPanel();
			topPanel.setLayout(new GridLayout(0, 2));

			availableAnnotationsTree = createAvailableAnnotationsTree();
			currentAnnotationsTree = createNodeSelectionTree();

			availableAnnotationsTree
					.addTreeSelectionListener(new AddAnnotationTreeSelectionListener());
			currentAnnotationsTree
					.addTreeSelectionListener(new SelectNodesTreeSelectionListener());

			JScrollPane chooserScrollPane = new JScrollPane(
					availableAnnotationsTree);
			JPanel chooserPanel = new JPanel();
			chooserPanel.setLayout(new BorderLayout());
			chooserPanel.add(chooserScrollPane, BorderLayout.CENTER);
			annotateNodesButton = new JButton("Apply Annotation to All Nodes");
			annotateNodesButton.setEnabled(false);
			chooserPanel.add(annotateNodesButton, BorderLayout.SOUTH);
			chooserPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Available Ontology Data"),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));

			annotateNodesButton.addActionListener(new ApplyAnnotationAction());
			topPanel.add(chooserPanel);

			JPanel currentAnnotationsButtonPanel = new JPanel();
			currentAnnotationsButtonPanel.setLayout(new GridLayout(0, 3));
			layoutByAnnotationButton = new JButton("Layout");
			layoutByAnnotationButton.setEnabled(false);
			currentAnnotationsButtonPanel.add(layoutByAnnotationButton);
			layoutByAnnotationButton
					.addActionListener(new LayoutByAnnotationAction());
			addSharedAnnotationEdgesButton = new JButton("Add Edges");
			currentAnnotationsButtonPanel.add(addSharedAnnotationEdgesButton);
			addSharedAnnotationEdgesButton.setEnabled(false);
			addSharedAnnotationEdgesButton
					.addActionListener(new DrawSharedEdgesAnnotationAction());
			deleteCreatedObjectsButton = new JButton(
					"Delete created nodes/edges");
			deleteCreatedObjectsButton.setEnabled(false);
			currentAnnotationsButtonPanel.add(deleteCreatedObjectsButton);
			deleteCreatedObjectsButton
					.addActionListener(new DeleteCreatedObjectsAction());

			JScrollPane currentChoicesScrollPane = new JScrollPane(
					currentAnnotationsTree);
			currentChoicesScrollPane.setPreferredSize(chooserScrollPane
					.getPreferredSize());
			JPanel panel3 = new JPanel();
			panel3.setLayout(new BorderLayout());
			panel3.add(currentChoicesScrollPane, BorderLayout.CENTER);
			panel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder("Annotations to be Imported"), BorderFactory
					.createEmptyBorder(10, 10, 10, 10)));

			panel3.add(currentAnnotationsButtonPanel, BorderLayout.SOUTH);
			topPanel.add(panel3);

			mainPanel.add(topPanel, BorderLayout.CENTER);
			JPanel okButtonPanel = new JPanel();
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new OKAction());
			okButtonPanel.add(okButton);
			mainPanel.add(okButtonPanel, BorderLayout.SOUTH);

			return mainPanel;

		} // createWidgets

		// ------------------------------------------------------------------------------
		private void expandAll(JTree tree, TreePath parent, boolean expand) {
			TreeNode node = (TreeNode) parent.getLastPathComponent();
			if (node.getChildCount() >= 0) {
				for (Enumeration e = node.children(); e.hasMoreElements();) {
					TreeNode n = (TreeNode) e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					expandAll(tree, path, expand);
				} // for
			} // if

			if (expand)
				tree.expandPath(parent);
			else
				tree.collapsePath(parent);

		} // expandAll

		// ------------------------------------------------------------------------------
		protected JTree createAvailableAnnotationsTree() {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(
					"Available Annotations");
			createTreeNodes(root, annotationDescriptions);
			JTree tree = new JTree(root);
			tree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setRootVisible(false);
			tree.setShowsRootHandles(true);
			// expandAll (tree, new TreePath (root), true);
			return tree;
		}

		// ------------------------------------------------------------------------------
		protected JTree createNodeSelectionTree() {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(
					"Annotations Categories");
			JTree tree = new JTree(root);
			// tree.getSelectionModel().setSelectionMode
			// (TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setRootVisible(false);
			tree.setShowsRootHandles(true);
			expandAll(tree, new TreePath(root), true);
			return tree;

		} // createNodeSelectionTree
		// ------------------------------------------------------------------------------
		class AddAnnotationTreeSelectionListener implements
				TreeSelectionListener {

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) availableAnnotationsTree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				if (!node.isLeaf())
					return;
				annotationPath = availableAnnotationsTree.getSelectionPaths()[0];
				annotateNodesButton.setEnabled(true);
			}

		} // inner class AddAnnotationTreeSelectionListener
		// -----------------------------------------------------------------------------------
		class SelectNodesTreeSelectionListener implements TreeSelectionListener {

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentAnnotationsTree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				layoutByAnnotationButton.setEnabled(!node.isLeaf());
				addSharedAnnotationEdgesButton.setEnabled(!node.isLeaf());
				if (!node.isLeaf())
					return;

				CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

				// unselect every node in the graph
				for (Iterator nvi = networkView.getNodeViewsIterator(); nvi
						.hasNext();) {
					((NodeView) nvi.next()).setSelected(false);
				}
				TreePath[] selectedPaths = currentAnnotationsTree
						.getSelectionPaths();
				HashMap selectionHash = extractAnnotationsFromSelection(selectedPaths);
				for (Iterator nvi = networkView.getNodeViewsIterator(); nvi
						.hasNext();) {
					// get the particular node view
					NodeView nv = (NodeView) nvi.next();
//					String nodeLabel = nodeAttributes.getStringAttribute(nv
//							.getNode().getIdentifier(),
//							Semantics.CANONICAL_NAME);
					String nodeLabel = nv.getNode().getIdentifier();
					
					if (nodeLabel == null) {
						continue;
					}
					// iterate over all attributes in the selectionHash
					for (Iterator mi = selectionHash.keySet().iterator(); mi
							.hasNext();) {
						String name = (String) mi.next();

						Vector categoryList = (Vector) selectionHash.get(name);
						byte type = nodeAttributes.getType(name);
						if (type == CyAttributes.TYPE_STRING) {
							String attributeValue = nodeAttributes
									.getStringAttribute(nodeLabel, name);
							if (attributeValue != null
									&& categoryList.contains(attributeValue))
								nv.setSelected(true);
							break; // no point in checking other attributes
						} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
							boolean hit = false;
							List attributeList = nodeAttributes
									.getListAttribute(nodeLabel, name);
							for (Iterator ali = attributeList.iterator(); ali
									.hasNext();) {
								if (categoryList.contains(ali.next())) {
									nv.setSelected(true);
									hit = true;
									break; // no point in checking the rest of
									// the list
								}
							}// ali iterator

							if (hit) {
								break;
							} // no point in checking other attributes

						}// if list
					}// mi iterator
				}// nvi iterator

				networkView.redrawGraph(false, false);

			} // valueChanged

			// -----------------------------------------------------------------------------
			/**
			 * create a hashmap, <String, String []>:
			 * 
			 * "KEGG Metabolic Pathway (level 1)" -> (Genetic Information
			 * Processing) "KEGG Metabolic Pathway (level 2)" -> (Amino Acid
			 * Metabolism, Nucleotide Metabolism)
			 * 
			 * this method is brittle, and will fail if the structure of the
			 * tree changes: it expects level 0: root level 1: a standard
			 * annotation name (see above) level 2: standard annotation category
			 * name (also see above)
			 */
			protected HashMap extractAnnotationsFromSelection(TreePath[] paths) {
				HashMap hash = new HashMap();

				for (int i = 0; i < paths.length; i++) {
					String annotationName = paths[i].getPathComponent(1)
							.toString();
					String annotationValue = paths[i].getPathComponent(2)
							.toString();
					Vector list;
					if (!hash.containsKey(annotationName)) {
						list = new Vector();
						hash.put(annotationName, list);
					}
					list = (Vector) hash.get(annotationName);
					list.add(annotationValue);
				} // for i

				return hash;

			} // extractAnnotationsFromSelection
			// ----------------------------------------------------------------------------------------
		} // inner class SelectNodesTreeSelectionListener

		// -----------------------------------------------------------------------------------
		protected void createTreeNodes(DefaultMutableTreeNode root,
				AnnotationDescription[] descriptions)
		// for each of the descriptions, and only if the description is of a
		// species in the
		// current graph: create a 'topLevelName' which will be the branch of
		// the JTree,
		// and a set of leaves for each logical level in that description's
		// ontology
		{
			if (descriptions == null || descriptions.length == 0)
				return;

			Set speciesInGraph = Semantics.getSpeciesInNetwork(network);

			DefaultMutableTreeNode branch = null;
			Vector topLevelNamesList = new Vector();

			for (int i = 0; i < descriptions.length; i++) {
				String species = descriptions[i].getSpecies();
				if (!speciesInGraph.contains(species)) {
					continue;
				}
				topLevelNamesList.add(descriptions[i].getCurator() + ", "
						+ descriptions[i].getType() + ", "
						+ descriptions[i].getSpecies());
				branch = new DefaultMutableTreeNode(descriptions[i]);
				Annotation annotation = dataServer
						.getAnnotation(descriptions[i]);

				if (annotation == null) {
					continue;
				}
				int maxDepth = annotation.maxDepth();
				for (int level = 0; level < maxDepth; level++)
					branch.add(new DefaultMutableTreeNode(
							new Integer(level + 1)));
				root.add(branch);
			}

		} // createTreeNodes
		// -----------------------------------------------------------------------------------
		public class OKAction extends AbstractAction {
			OKAction() {
				super("");
			}

			public void actionPerformed(ActionEvent e) {
				Gui.this.setVisible(false);
			}

		} // OKAction
		// -----------------------------------------------------------------------------
		public class LayoutByAnnotationAction extends AbstractAction {
			LayoutByAnnotationAction() {
				super("");
			}

			public void actionPerformed(ActionEvent e) {
				/*
				 * String title = "Operation not supported"; String message =
				 * "This operation is not yet supported.";
				 * JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
				 * message, title, JOptionPane.ERROR_MESSAGE);
				 */
				deleteCreatedObjectsButton.setEnabled(true);
			}

		} // LayoutByAnnotationAction
		// -----------------------------------------------------------------------------
		public class DrawSharedEdgesAnnotationAction extends AbstractAction {
			DrawSharedEdgesAnnotationAction() {
				super("");
			}

			public void actionPerformed(ActionEvent e) {
				/*
				 * String title = "Operation not supported"; String message =
				 * "This operation is not yet supported.";
				 * JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
				 * message, title, JOptionPane.ERROR_MESSAGE);
				 */
				deleteCreatedObjectsButton.setEnabled(true);
			}

		} // DrawSharedEdgesAnnotationAction
		// -----------------------------------------------------------------------------
		public class DeleteCreatedObjectsAction extends AbstractAction {
			DeleteCreatedObjectsAction() {
				super("");
			}

			public void actionPerformed(ActionEvent e) {
				deleteCreatedObjectsButton.setEnabled(false);
			}
		}
		// -----------------------------------------------------------------------------
	} // inner class Gui

	// -----------------------------------------------------------------------------------
	public String addAnnotationToNodes(AnnotationDescription aDesc, int level) {
		String callerID = "AnnotationGui.addAnnotationToNodes";

		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		// something like "GO biological process" or "KEGG metabolic pathway"
		String baseAnnotationName = aDesc.getCurator() + " " + aDesc.getType();
		String annotationNameAtLevel = baseAnnotationName + " (Level " + level
				+ ")";
		String annotationNameForLeafIDs = baseAnnotationName + " leaf IDs";

		// make a fresh start
		nodeAttributes.deleteAttribute(annotationNameAtLevel);
		nodeAttributes.deleteAttribute(annotationNameForLeafIDs);

		Iterator it = Cytoscape.getRootGraph().nodesIterator();
		ArrayList nodeLabels = new ArrayList();
		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
//			String nodeLabel = nodeAttributes.getStringAttribute(node
//					.getIdentifier(), Semantics.CANONICAL_NAME);
			String nodeLabel = node.getIdentifier();
			if (nodeLabel != null)
				nodeLabels.add(nodeLabel);
		}

		String[] nodeLabelArray = (String[]) nodeLabels
				.toArray(new String[nodeLabels.size()]);
		
		
		int unAnnotatedNodeCount = 0;
		for (int i = 0; i < nodeLabelArray.length; i++) {
			
			//System.out.println("Applying: " + nodeLabelArray[i] + ", aDesc = " + aDesc.toString() );
			
			String[][] fullAnnotations = dataServer.getAllAnnotations(aDesc,
					nodeLabelArray[i]);
			if (fullAnnotations.length == 0)
				unAnnotatedNodeCount++;
			else {
				String[] uniqueAnnotationsAtLevel = collapseToUniqueAnnotationsAtLevel(
						fullAnnotations, level);

				// List to save values in the current level
				List annotsList = new ArrayList();
				if (uniqueAnnotationsAtLevel.length == 0) {
					// No attribute available for this node
					nodeAttributes.setAttribute(nodeLabelArray[i],
							annotationNameAtLevel, "");
				} else {
					// Extract all values in the current level
					for (int j = 0; j < uniqueAnnotationsAtLevel.length; j++) {

//						System.out.print("node cn = " + canonicalNodeNames[i]
//								+ ",  and an@level = " + annotationNameAtLevel);
//						System.out.println(",  an value@level = "
//								+ uniqueAnnotationsAtLevel[j]);

						// we can do this because at the begining of the method
						// we deleted the attribute annotationNameAtLevel:
						// annotsList =
						// nodeAttributes.getListAttribute(canonicalNodeNames[i],
						// annotationNameAtLevel);

						// annotsList =
						// nodeAttributes.getListAttribute(canonicalNodeNames[i],
						// annotationNameAtLevel);
						//					
						// if(annotsList == null){
						// annotsList = new ArrayList();
						// //nodeAttributes.setListAttribute(canonicalNodeNames[i],
						// annotationNameAtLevel,annotsList);
						// annotsList.add(uniqueAnnotationsAtLevel[j]);
						// }
						//System.out.println("    " + uniqueAnnotationsAtLevel[j]);
						annotsList.add(uniqueAnnotationsAtLevel[j]);

					}// for j

					if (annotsList.size() != 0) {
						nodeAttributes.setListAttribute(nodeLabelArray[i],
								annotationNameAtLevel, annotsList);
					}
				}
				int[] annotationIDs = dataServer.getClassifications(aDesc,
						nodeLabelArray[i]);
				Integer[] integerArray = new Integer[annotationIDs.length];
				for (int j = 0; j < annotationIDs.length; j++)
					integerArray[j] = new Integer(annotationIDs[j]);
			} // else: this node is annotated
		} // for i

// 		System.err
// 				.println("Warning: a method has been called whose functionality "
// 						+ "has been partially removed "
// 						+ "(AnnotationGui.addAnnotationToNodes()).");


		return annotationNameAtLevel;

	} // addAnnotationToNodes

	// ----------------------------------------------------------------------------------------
	/**
	 * return only the unique categories (typically representing gene
	 * annotations) which exist at the specified level. genes (for example) may
	 * be annotated as belonging to two or more different categories, but at a
	 * higher logical level, these different categories may merge. for example,
	 * the halobacterium gene VNG0623G is classified by KEGG, in its 3-level
	 * ontology as belonging to two categories at the most specific (third)
	 * level
	 * <ol>
	 * <li> Valine, leucine and isoleucine degradation
	 * <li> Propanoate metabolism
	 * <ol>
	 * At level two, the annotation is:
	 * <ol>
	 * <li> Amino Acid Metabolism
	 * <li> Carbohydrate Metabolism
	 * <ol>
	 * At level one, the annotation is simply
	 * <ol>
	 * <li>Metabolism
	 * <ol>
	 * This method calculates and returns the unique annotations for a
	 * biological entity at the specified level.
	 */
	private String[] collapseToUniqueAnnotationsAtLevel(
			String[][] fullAnnotations, int level) {
		Vector collector = new Vector();

		for (int i = 0; i < fullAnnotations.length; i++) {
			int indexOfClosestAvailableLevel = level - 1;
			if (indexOfClosestAvailableLevel > (fullAnnotations[i].length - 1))
				indexOfClosestAvailableLevel = fullAnnotations[i].length - 1;
			String annotationAtLevel = fullAnnotations[i][indexOfClosestAvailableLevel];
			if (!collector.contains(annotationAtLevel))
				collector.add(annotationAtLevel);
		} // for i

		return (String[]) collector.toArray(new String[0]);

	} // collapseToUniqueAnnotationsAtLevel
	// ----------------------------------------------------------------------------------------
	class ApplyAnnotationAction extends AbstractAction {
		ApplyAnnotationAction() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {

			int max = annotationPath.getPathCount();

			for (int i = 0; i < max; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) annotationPath
						.getPathComponent(i);
				Object userObj = node.getUserObject();
			}

			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) annotationPath
					.getPathComponent(1);
			AnnotationDescription aDesc = (AnnotationDescription) node1
					.getUserObject();
			DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) annotationPath
					.getPathComponent(2);
			int level = ((Integer) node2.getUserObject()).intValue();
			if (aDesc == null)
				return;
			String callerID = "ApplyAnnotationAction.actionPerformed";
			currentAnnotationCategory = addAnnotationToNodes(aDesc, level);

			// Modified by kono@ucsd.edu
			// 10/20/2005
			//
			// - The following line of code throws exception when
			// there is no match in the annotation.
			// Now it creates pop-up window when no match error found.
			//

			Object[] uniqueAnnotationValues = null;

			// try {
			CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
			Iterator it = Cytoscape.getRootGraph().nodesIterator();
			HashSet values = new HashSet();
			while (it.hasNext()) {
				CyNode node = (CyNode) it.next();
				byte type = nodeAtts.getType(currentAnnotationCategory);
				if (type == CyAttributes.TYPE_STRING) {
					String strVal = nodeAtts.getStringAttribute(node
							.getIdentifier(), currentAnnotationCategory);
					if (strVal != null)
						values.add(strVal);
				} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
					List vals = nodeAtts.getListAttribute(node.getIdentifier(),
							currentAnnotationCategory);
					if (vals.size() > 0) {
						Object val = vals.get(0);
						if (val instanceof String)
							values.addAll(vals);
					}
				}

			}// while it.hasNext

			uniqueAnnotationValues = values.toArray();

			// } catch (Exception e1) {

			if (uniqueAnnotationValues.length == 0) {
				// System.err.println( "No match exception" + e1 );
				JOptionPane
						.showMessageDialog(
								null,
								"There is no match between the selected annotation \n"
										+ "and current nodes in the network.\n"
										+ "\nMake sure that your network data file \n"
										+ "and Gene Association files use same naming scheme.\n\n"
										+ "Please compare the 3rd column of Gene Association\n"
										+ "file (DB_Object_Symbol) and node names in your network.",
								"No match in Gene Ontology Database",
								JOptionPane.ERROR_MESSAGE);

			} // Mod by kono end (10/20/2005)

			if (uniqueAnnotationValues != null
					&& uniqueAnnotationValues.length > 0
					&& uniqueAnnotationValues[0].getClass() == "string"
							.getClass()) {
				java.util.Arrays.sort(uniqueAnnotationValues,
						(Comparator) String.CASE_INSENSITIVE_ORDER);
				appendToSelectionTree(currentAnnotationCategory,
						uniqueAnnotationValues);
			}
		}

		// --------------------------------------------------------------------------------------
		protected void appendToSelectionTree(String currentAnnotationCategory,
				Object[] uniqueAnnotationValues) {
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(
					currentAnnotationCategory);

			for (int i = 0; i < uniqueAnnotationValues.length; i++)
				branch
						.add(new DefaultMutableTreeNode(
								uniqueAnnotationValues[i]));

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) currentAnnotationsTree
					.getModel().getRoot();
			DefaultTreeModel model = (DefaultTreeModel) currentAnnotationsTree
					.getModel();
			model.insertNodeInto(branch, root, root.getChildCount());
			currentAnnotationsTree.scrollPathToVisible(new TreePath(branch
					.getPath()));
			model.reload();

		} // appendToSelectionTree
		// -----------------------------------------------------------------------------
	} // inner class ApplyAnnotationAction
	class ActionListBoxSelectionListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!lsm.isSelectionEmpty()) {
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {
						Object obj = actionListModel.elementAt(i);
						currentAnnotationCategory = (String) obj;
					}
				} // for i
			} // if !empty
		} // valueChanged

	} // inner class AttributeListSelectionListener
	// -----------------------------------------------------------------------------
} // class AnnotationGui


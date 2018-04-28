
/*
  File: AnimatedLayoutAction.java 
  
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

package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.CyNetworkView;
import cytoscape.dialogs.GraphObjectSelection;

import phoebe.*;
import phoebe.util.*;
import phoebe.event.*;
import java.util.*;

import giny.model.*;
import giny.view.*;
//import giny.util.*;

import cytoscape.ding.DingNetworkView;

public class AnimatedLayoutAction extends AbstractAction {

  CyNetworkView networkView;
  
  boolean bool = false;
  public AnimatedLayoutAction ( CyNetworkView networkView ) {
    super("Animate Layout");
    this.networkView = networkView;
  }

  public void actionPerformed (ActionEvent e) {

    JDialog dialog = new JDialog();
    JPanel main = new JPanel();

       main.add(  new JButton (new AbstractAction( "3D" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   ISOM3DLayout isom = new ISOM3DLayout( gv );
//                   isom.doLayout();

                }
              } ); } } ) );

      main.add(  new JButton (new AbstractAction( "Z Axis" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {

                  double maxZ = Double.MAX_VALUE;
                  double minZ = Double.MAX_VALUE;

                  DingNetworkView gv = ( DingNetworkView )networkView.getView();
                  Iterator nvi = gv.getNodeViewsIterator();
                  while ( nvi.hasNext() ) {
                    NodeView nv = ( NodeView )nvi.next();
                    // System.out.print( "Index: "+nv.getRootGraphIndex() );
//                     System.out.print( "Index: "+nv.getRootGraphIndex() );
//                     System.out.print( " X: "+gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_X_POSITION ) );
//                     System.out.print( " Y: "+gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Y_POSITION ) );
//                     System.out.println( " Z: "+gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION ) );
                  
                    if ( maxZ == Double.MAX_VALUE ) {
                      maxZ = gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION );
                      minZ = gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION );
                    }

                    if ( maxZ < gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION ) ) {
                      maxZ = gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION );
                    }

                    if ( minZ > gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION ) ) {
                      minZ = gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION );
                    }

                    
                  }

//                   System.out.println( "Z-RAnge: "+minZ+ " to "+maxZ );


                 

                  nvi = gv.getNodeViewsIterator();
                  while ( nvi.hasNext() ) {
                    NodeView nv = ( NodeView )nvi.next();

                    double scale = ( gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION ) - minZ ) /
                                 ( maxZ - minZ );

                    nv.setWidth( scale * 100 );
                    nv.setHeight( scale * 100 );
                    gv.setNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION, scale * 100 );
                  }


                  nvi = gv.getNodeViewsIterator();
                  while ( nvi.hasNext() ) {
                    NodeView nv = ( NodeView )nvi.next();
                    gv.addNodeView( "phoebe.util.P3DNode", nv.getRootGraphIndex() );
                  
                  }



                }
              } ); } } ) );
 


   //  main.add(  new JButton (new AbstractAction( "Grand Tour" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   PGrandTour tour = new PGrandTour( gv );
//                   tour.takeTour();
//                 }
//               } ); } } ) );

//     main.add(  new JButton (new AbstractAction( "Force" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   ModelBasedSpringLayout mbsl = new ModelBasedSpringLayout( gv );
//                   mbsl.doLayout();
//                 }
//               } ); } } ) );

//     main.add(  new JButton (new AbstractAction( "FR" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   FRLayout fr = new FRLayout( gv );
//                   fr.doLayout();
//                 }
//               } ); } } ) );

//     main.add(  new JButton (new AbstractAction( "ISOM" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {

//                    try {
//                      Class classs = Class.forName( "csplugins.sbw.SBWConnector" );
//                      System.out.println( "Class Made: "+classs );
//                    } catch ( Exception e ) {
//                      e.printStackTrace();
//                    }



//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   ISOMLayout isom = new ISOMLayout( gv );
//                   isom.doLayout();
//                 }
//               } ); } } ) );

//     main.add(  new JButton (new AbstractAction( "JUNG" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   JUNGSpringLayout jung = new JUNGSpringLayout( gv );
//                   jung.doLayout();
//                 }
//               } ); } } ) );

//      main.add(  new JButton (new AbstractAction( "ForceDirected" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   ForceDirectedLayout fdl = new ForceDirectedLayout( gv );
//                   fdl.doLayout();
//                 }
//               } ); } } ) );


//     main.add(  new JButton (new AbstractAction( "Sugiyama" ) {
//         public void actionPerformed ( ActionEvent e ) {
//           // Do this in the GUI Event Dispatch thread...
//           SwingUtilities.invokeLater( new Runnable() {
//               public void run() {
//                 PGrap*View gv = ( PGrap*View )networkView.getView();
//                 String file = null;
//                   JFileChooser chooser = new JFileChooser( "/users/xmas/CSBI/cytoscape/testData");
//                 chooser.setDialogTitle( "Load Tier Data" );
//                 if( chooser.showOpenDialog( gv.getComponent() ) == chooser.APPROVE_OPTION ) {
//                   file = chooser.getSelectedFile().toString();
//     }
//                 TieredInputReader reader = new TieredInputReader( gv );
//                 List fTierList = reader.readGraphFromInteractionsFile(file);
//                 Sugiyama sugiyama = new Sugiyama( gv );
//                 sugiyama.layout( fTierList, true );

//               }
//             } ); } } ) );
  

//      main.add(  new JButton (new AbstractAction( "Tree Select" ) {
//           public void actionPerformed ( ActionEvent e ) {
//             // Do this in the GUI Event Dispatch thread...
//             SwingUtilities.invokeLater( new Runnable() {
//                 public void run() {
//                   PGrap*View gv = ( PGrap*View )networkView.getView();
//                   int[] sel = gv.getSelectedNodeIndices();
//                   TreeLayout tl = new TreeLayout();
//                   GraphPerspective p = tl.doLayout( gv ) ;
//                   int[] edges = p.getEdgeIndicesArray();
//                   for ( int i = 0; i < p.getEdgeCount(); ++i ) {
//                     //System.out.println( "Index of Edge is: "+edges[i] );
//                     EdgeView ev = gv.getEdgeView( edges[i] );
//                     ev.setSelected( true );
//                   }


//                 }
//               } ); } } ) );


//     main.add(  new JButton (new AbstractAction( "Tree" ) {
//         public void actionPerformed ( ActionEvent e ) {
//           // Do this in the GUI Event Dispatch thread...
//           SwingUtilities.invokeLater( new Runnable() {
//               public void run() {
//                 PGrap*View gv = ( PGrap*View )networkView.getView();
//                 int[] sel = gv.getSelectedNodeIndices();
//                  TreeLayout tl = new TreeLayout();
//                  tl.doLayout( gv ) ;
//               }
//             } ); } } ) );
    

//     main.add( new JLabel( "  ") );


//     main.add(  new JButton (new AbstractAction( "Update" ) {
//         public void actionPerformed ( ActionEvent e ) {
//           // Do this in the GUI Event Dispatch thread...
//           SwingUtilities.invokeLater( new Runnable() {
//               public void run() {
//                 PGrap*View gv = ( PGrap*View )networkView.getView();
//                 Iterator nodes = gv.getNodeViewsIterator();
//                 while ( nodes.hasNext() ) {
//                   ( ( PNodeView )nodes.next() ).setNodePosition( false );
//                 }
//               }
//             } ); } } ) );
//      main.add(  new JButton (new AbstractAction( "Animate" ) {
//         public void actionPerformed ( ActionEvent e ) {
//           // Do this in the GUI Event Dispatch thread...
//           SwingUtilities.invokeLater( new Runnable() {
//               public void run() {
//                 PGrap*View gv = ( PGrap*View )networkView.getView();
//                 Iterator nodes = gv.getNodeViewsIterator();
//                 while ( nodes.hasNext() ) {
//                   ( ( PNodeView )nodes.next() ).setNodePosition( true );
//                 }
//               }
//             } ); } } ) );


     
    
 

    dialog.getContentPane().add( main );
    dialog.pack();
    dialog.setVisible( true );

  }
}

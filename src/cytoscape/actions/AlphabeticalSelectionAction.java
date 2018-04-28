
/*
  File: AlphabeticalSelectionAction.java 
  
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

// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $

//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import cytoscape.view.CyNetworkView;
import cytoscape.data.CyNetworkUtilities;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

import ViolinStrings.Strings;


public class AlphabeticalSelectionAction 
  extends 
    CytoscapeAction  
  implements 
    ActionListener {
   
  JDialog dialog;
  JButton search, cancel;
  JTextField searchField;


  public AlphabeticalSelectionAction () {
    super("By Name...");
    setPreferredMenu( "Select.Nodes" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_F, ActionEvent.CTRL_MASK );
  }


  public void actionPerformed (ActionEvent e) {

    if ( e.getSource() == cancel ) {
      dialog.setVisible( false );
      return;
    }

    if ( e.getSource() == searchField || e.getSource() == search ) {
      String search_string = searchField.getText();
      CyNetworkUtilities.selectNodesStartingWith( Cytoscape.getCurrentNetwork(),
                                                  search_string,
                                                  Cytoscape.getCurrentNetworkView() );
      Cytoscape.getCurrentNetworkView().updateView();
      return;
    }

    if ( dialog == null )
      createDialog();
    dialog.setVisible( true );

    Cytoscape.getCurrentNetworkView().updateView();
  }

  private JDialog createDialog () {

    dialog = new JDialog( Cytoscape.getDesktop(),
                          "Select Nodes By Name",
                          false );
    
    JPanel main_panel = new JPanel();
    main_panel.setLayout( new BorderLayout() );

    JLabel label = new JLabel(  "<HTML>Select nodes whose <B>name or synonym</B> is like <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
    main_panel.add( label, BorderLayout.NORTH );


    searchField = new JTextField( 30 );
    searchField.addActionListener( this );
    main_panel.add( searchField, BorderLayout.CENTER );

    JPanel button_panel = new JPanel();
    search = new JButton( "Search" );
    cancel = new JButton( "Cancel" );
    search.addActionListener( this );
    cancel.addActionListener( this );
    button_panel.add( search );
    button_panel.add( cancel );
    main_panel.add( button_panel, BorderLayout.SOUTH );

    dialog.setContentPane( main_panel );
    dialog.pack();
    return dialog;
  }


}



/*
  File: CyNetworkNaming.java 
  
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

package cytoscape.util;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import java.awt.Component;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

public class CyNetworkNaming
{

  public static String getSuggestedSubnetworkTitle(CyNetwork parentNetwork)
  {
    for (int i = 0; true; i++) {
      String nameCandidate =
        parentNetwork.getTitle() + "--child" + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(nameCandidate)) return nameCandidate; }
  }

  public static String getSuggestedNetworkTitle(String desiredTitle)
  {
    for (int i = 0; true; i++) {
      String titleCandidate = desiredTitle + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(titleCandidate)) return titleCandidate; }
  }

  private static boolean isNetworkTitleTaken(String titleCandidate)
  {
    Set existingNetworks = Cytoscape.getNetworkSet();
    Iterator iter = existingNetworks.iterator();
    while (iter.hasNext()) {
      CyNetwork existingNetwork = (CyNetwork) iter.next();
      if (existingNetwork.getTitle().equals(titleCandidate))
        return true; }
    return false;
  }
  
  /**
   * This will prompt the user to edit the title of a given CyNetork,
   * and after ensuring that the network title is not already in use,
   * this will assign that title to the given CyNetwork 
   * @para network is the CyNetwork whose title is to be changed 
   */
  public static void editNetworkTitle (CyNetwork network) {
		Component parent = Cytoscape.getDesktop();
		String pname = network.getTitle();
		String name = null;
		String sname = "";
		Object[] options = {"Try Again", "Cancel", "Use Suggestion"};
		int value = JOptionPane.NO_OPTION;
		
		while (true) {
			cytoscape.dialogs.EditNetworkTitleDialog theDialog = new cytoscape.dialogs.EditNetworkTitleDialog(parent, true, pname);
			theDialog.setLocationRelativeTo(parent);
			theDialog.setVisible(true);
			name = theDialog.getNewNetworkTitle();
			//name = JOptionPane.showInputDialog(parent,
			//		"Please enter new network title: ", "Edit Network Title",
			//				JOptionPane.QUESTION_MESSAGE);
			if (name == pname)
				break;
			else if (name == null||name.trim().equals(""))
			{
				name = pname;
				break;
			}
			else if (isNetworkTitleTaken(name)) { 
				sname = getSuggestedNetworkTitle(name);
				value = JOptionPane.showOptionDialog(parent, 
					"That network title already exists, try again or use \""
					+ sname	+"\" instead: ", "Duplicate Network Title", 
					JOptionPane.WARNING_MESSAGE,
					JOptionPane.YES_NO_CANCEL_OPTION,
					null, options,
					options[2]);
			
				if (value == JOptionPane.NO_OPTION) {
					name = pname;
					break;
				}
				else if (value == JOptionPane.CANCEL_OPTION ){
					name = sname;
					break;
				}
			}
			else
				break;
		}
		network.setTitle(name);
	  }
}

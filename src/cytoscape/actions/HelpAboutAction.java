
/*
  File: HelpAboutAction.java 
  
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
import java.lang.*;

import cytoscape.CytoscapeVersion;
import cytoscape.util.CreditScreen;

public class HelpAboutAction extends AbstractAction   {

    public HelpAboutAction() {
        super("About");
    }
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater( new Runnable() {
	    public void run() {
	       // Normally, a StringBuffer would be more appropriate,
	       // but for some reason the newline chars don't get
	       // respected and thus we're unable to tokenize 
	       // properly.
	       //
	       // If you've contributed, please add your name in the
	       // correct location.
	       // 
               String lines = "Cytoscape is a collaboration \n" +
                              "between the Institute for \n" +
			      "Systems Biology, University of \n" +
			      "California San Diego, Memorial \n" +
			      "Sloan-Kettering Cancer Center, \n" +
			      "Institut Pasteur, and Agilent \n" +
			      "Technologies.\n" +
                              " \n" +
                              "For more information, please see: \n" +
                              " http://www.cytoscape.org\n" +
                              " \n" +
                              "Cytosape Developers,\n"+
                              " past and present,\n"+
                              " in alphabetical order: \n"+
                              " \n" +
                              " Annette Adler\n" +
		              " Nada Amin\n" +
                              " Mark Anderson\n"+
		              " Iliana Avila-Campillo\n"+
                              " Gary Bader\n"+  
		              " Hamid Bolouri\n" +
		              " Ethan Cerami\n"+
		              " Rowan Christmas\n"+
                              " Melissa Cline\n" +
                              " Mike Creech\n" +
                              " Paul Edlefsen\n" +
                              " Stephanie Fan\n" +
                              " Trey Ideker\n" +
		              " Liz Kain\n" +
		              " Larissa Kamenkovich\n" +
		              " Ryan Kelley\n"+ 
		              " Brad Kohlenberg\n" +
                              " Allan Kuchinsky\n" +
		              " Nerius Landys\n"+ 
                              " Willem Ligtenberg\n"+  
		              " Samad Lotia\n" +
		              " Andrew Markiel\n"+
                              " James McIninch\n" +
		              " Keiichiro Ono\n" +
                              " Owen Ozier\n" +
		              " David Reiss\n"+ 
                              " Chris Sander\n" +
                              " Paul Shannon\n"+
		              " Robert Sheridan\n" +
                              " Benno Shwikowski\n" +
		              " Mike Smoot\n" +
		              " James Taylor\n"+
                              " Aditya Vailaya\n" +
                              " Jonathan Wang\n"+  
		              " Chris Workman\n" ;

               CreditScreen.showCredits( getClass().getResource(
			"/cytoscape/images/CytoscapeCredits.png"),
			lines );
	} } );
    } 
}


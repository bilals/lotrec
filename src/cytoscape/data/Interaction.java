
/*
  File: Interaction.java 
  
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

// Interaction.java:  protein-protein or protein-DNA: parse text file, encapsulate


//----------------------------------------------------------------------------------------
// RCS: $Revision: 7760 $   
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $ 
// $Author: mes $
//-----------------------------------------------------------------------------------
package cytoscape.data;
//----------------------------------------------------------------------------------------
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
//----------------------------------------------------------------------------------------
public class Interaction {
  private String source;
  private Vector targets = new Vector ();
  private String interactionType;
  private Vector allInteractions = new Vector ();
//----------------------------------------------------------------------------------------
public Interaction (String source, String target, String interactionType)
{
  this.source = source;
  this.interactionType = interactionType;
  this.targets.addElement (target);

} // ctor (3 args)
//----------------------------------------------------------------------------------------
public Interaction (String rawText)
{
  this (rawText, " ");
}
//----------------------------------------------------------------------------------------
public Interaction (String rawText, String delimiter)
{
  StringTokenizer strtok = new StringTokenizer (rawText, delimiter);
  int counter = 0;
  while (strtok.hasMoreTokens ()) {
    if (counter == 0) 
      source = ((String) strtok.nextToken ()).trim ();
    else if (counter == 1)
      interactionType = ((String) strtok.nextToken ()).trim ();
    else {
      String newTarget = ((String) strtok.nextToken ()).trim ();
      targets.addElement (newTarget);
      }
    counter++;
    }
  
} // ctor (String)
//---------------------------------------------------------------------------------------
public String getSource ()
{
  return source;
}
//---------------------------------------------------------------------------------------
public String getType ()
{
  return interactionType;
}
//---------------------------------------------------------------------------------------
public int numberOfTargets ()
{
  return targets.size ();
}
//---------------------------------------------------------------------------------------
public String [] getTargets ()
{
  String [] result = new String [targets.size ()];
  for (int i=0; i < result.length; i++)
    result [i] = (String) targets.elementAt (i);

  return result;

} // getTargets
//---------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (interactionType);
  sb.append ("::");
  sb.append (source);
  sb.append ("::");
  for (int i=0; i < targets.size (); i++) {
    sb.append ((String) targets.elementAt (i));
    if (i < targets.size () - 1) sb.append (",");
    }
  return sb.toString ();
}
//---------------------------------------------------------------------------------------
} // Interaction



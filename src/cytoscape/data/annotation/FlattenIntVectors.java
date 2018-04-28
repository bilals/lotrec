
/*
  File: FlattenIntVectors.java 
  
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

// FlattenIntVectors//-------------------------------------------------------------------------------


// $Revision: 7760 $   
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $ 
// $Author: mes $
//-----------------------------------------------------------------------------------
package cytoscape.data.annotation;
//-----------------------------------------------------------------------------------
import java.util.Vector;
//-------------------------------------------------------------------------------
/**  given a vector which nests other vectors, each resolving
 *   eventually to a list of Integers, flatten it out into a simple 1-level-deep
 *   vector of Integer vectors.  
 *   for example:
 *
 *    ((1 2 3 (4 5 (6))), (7 (8) (9 10 11 (12))))
 *
 *   becomes
 *
 *    ((1 2 3 4 5 6), (7 8 9 10 11 12))
 *
 */
public class FlattenIntVectors {
  Vector result = new Vector ();
//-------------------------------------------------------------------------------
public FlattenIntVectors (Vector v)
{
  flatten (v);

}
//-------------------------------------------------------------------------------
private void flatten (Vector v)
{
  if (v == null)
    return;

  Object o = v.elementAt (0);
  String className = o.getClass().getName ();

  if (!className.equalsIgnoreCase ("java.util.Vector")) {
    // System.out.println (v);
    result.addElement (v);
    }
  else {
    for (int i=0; i < v.size (); i++) {
      Vector w = (Vector) v.elementAt (i);
      flatten (w);
      } // for i
    } // else
     
} // flatten
//-------------------------------------------------------------------------
public Vector getResult ()
{
  return result;
}
//-------------------------------------------------------------------------------
} // FlattenIntVectors





/*
  File: mRNAMeasurement.java 
  
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

// mRNAMeasurement:  encapsulate the ratio/signficance pair


//-------------------------------------------------------------------------------------------
// $Revision: 7760 $   
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $ 
// $Author: mes $
//-----------------------------------------------------------------------------------
package cytoscape.data;
//-------------------------------------------------------------------------------------------
public class mRNAMeasurement implements java.io.Serializable {
  private double expressionRatio;
  private double significance;
//-------------------------------------------------------------------------------------------
public mRNAMeasurement (String ratioString, String significanceString)
{
  expressionRatio = -99999.9;
  try {
    expressionRatio = Double.parseDouble (ratioString);
    }
  catch (NumberFormatException ignore) {;}

  significance = -99999.9;
  try {
    significance = Double.parseDouble (significanceString);
    }
  catch (NumberFormatException ignore) {;}

}
//-------------------------------------------------------------------------------------------
public double getRatio ()
{
  return expressionRatio;
}
//-------------------------------------------------------------------------------------------
public double getSignificance ()
{
  return significance;
}
//-------------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (expressionRatio);
  sb.append (",  ");
  sb.append (significance);
  return sb.toString ();

} // toString
//-------------------------------------------------------------------------------------------
public void setRatio (double new_ratio)
{
  this.expressionRatio = new_ratio;
}
//-------------------------------------------------------------------------------------------
public void setSignificance (double new_significance)
{
  this.significance = new_significance;
}
//-------------------------------------------------------------------------------------------
} // mRNAMeasurement



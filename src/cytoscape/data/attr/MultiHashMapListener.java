
/*
  File: MultiHashMapListener.java 
  
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

package cytoscape.data.attr;

/**
 * A hook to receive notification when attribute values are set and removed
 * from objects.
 */
public interface MultiHashMapListener
{

  /**
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   * @param oldAttributeValue the previous attribute value that was bound
   *   at specified key sequence, or null if no previous attribute value was
   *   bound.
   * @param newAttributeValue the new attribute value that is now bound
   *   at specified key sequence.
   */
  public void attributeValueAssigned(String objectKey,
                                     String attributeName,
                                     Object[] keyIntoValue,
                                     Object oldAttributeValue,
                                     Object newAttributeValue);

  /**
   * This listener method gets called as a result of
   * MultiHashMap.removeAttributeValue(objectKey, attributeName, keyIntoValue),
   * but only if an attribute value was found [and removed] for specified key.
   * The parameter attributeValue in this listener method is the value that
   * is returned by MultiHashMap.removeAttributeValue(), and it is never null.
   * @param keyIntoValue don't modify this array; this array will be
   *   null if attributeName has a zero-dimensional keyspace.
   */
  public void attributeValueRemoved(String objectKey,
                                    String attributeName,
                                    Object[] keyIntoValue,
                                    Object attributeValue);

  /**
   * This listener method gets called as a result of
   * MultiHashMap.removeAllAttributeValues(objectKey, attributeName), but only
   * if objectKey has at least one attribute value bound in attributeName.
   */
  public void allAttributeValuesRemoved(String objectKey,
                                        String attributeName);

}

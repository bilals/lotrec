/*
  File: AttributeValueVisitor.java

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

package cytoscape.data;



/**
 * Interface for defining attribute value visitors--operations to
 * perform on each attribute value using {@link
 * cytoscape.data.CyAttributesUtils#traverseAttributeValues
 * CyAttributesUtils.traverseAttributeValues()}.
 *
 */
public interface AttributeValueVisitor {
    /**
     * Perform whatever operations are desired on the given attribute value.
     * @param  objTraversedID the identifier of the object for which we have
     *                  obtained an attribute value.
     * @param attrName the attribute name for which this is a value.
     * @param attrs the CyAttributes where this attribute value is stored.
     * @param keySpace the key used to obtain this value. For complex
     * values, this may consist of several elements (e.g., new
     * Object[] {"url1", new Integer(1), new Integer(0)). Modification
     * of this key may lead to unexpected traversal results or errors,
     * so copy this key if you wish to make modifications.
     * @param visitedValue the actual visited.
     * @see cytoscape.data.CyAttributesUtils#traverseAttributeValues
     */
    void visitingAttributeValue(String objTraverseID, String attrName,
                                CyAttributes attrs, Object[] keySpace,
                                Object visitedValue);
}

/*
  File: AttributeFilter.java

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
 * Interface for defining CyEdge-based filters used for determining
 * which Edges should be used for a particular operation, such as
 * CyAttributesUtils.copyAttributes().
 * @see cytoscape.data.CyAttributesUtils#copyAttributes
 */
public interface AttributeFilter {
    /**
     * Return true iff a given attribute should be included in
     * some operation, such as {@link
     * cytoscape.data.CyAttributesUtils#copyAttributes
     * CyAttributesUtils.copyAttributes()}.
     * @param CyAttributes attrs the CyAttributes where attrName
     *        is stored.
     * @param objID the identifer of the object whose attrName attribute
     *              is stored in attrs.
     * @param attrName the name of the Attribute to test.
     * For example, if we were performing a CyAttributesUtils.copyAttributes(),
     * returning true would mean to copy the attribute attrName
     * for the object with key objID, within the CyAttributes attrs.
     * @see cytoscape.data.CyAttributesUtils#copyAttributes
     */
    boolean includeAttribute(CyAttributes attrs, String objID, String attrName);
}

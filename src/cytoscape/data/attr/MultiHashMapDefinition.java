
/*
  File: MultiHashMapDefinition.java 
  
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
 * This interface contains the API specification for creating
 * attribute definitions.
 */
public interface MultiHashMapDefinition
{

  /**
   * This type corresponds to java.lang.Boolean.
   */
  public final byte TYPE_BOOLEAN = 1;

  /**
   * This type corresponds to java.lang.Double.
   */
  public final byte TYPE_FLOATING_POINT = 2;

  /**
   * This type corresponds to java.lang.Integer.
   */
  public final byte TYPE_INTEGER = 3;

  /**
   * This type corresponds to java.lang.String.
   */
  public final byte TYPE_STRING = 4;

  /**
   * Creates an attribute definition.  An attribute definition must be
   * created before binding an attribute value to an object.<p>
   * Perhaps the most common type of attribute definition is one where the
   * key space has zero dimensions.  For example, if I want to identify each
   * object as having a color, I would create an attribute definition which
   * stores values of TYPE_STRING (for storing "red", "blue", and so on)
   * and has no key sequence mapping color
   * values.  By "no key sequence" I mean that the input parameter
   * keyTypes would be either null or the empty array for my
   * color attribute definition.<p>
   * The more interesting case is where the key space in an attribute
   * definition has one or more dimensions.  For example, if I
   * wanted to create an attribute that represents measured p-values for
   * all objects over a set of experiments ("Ideker experiment",
   * "Salk experiment", ...) I would define a one-dimensional key space
   * of TYPE_STRING (to represent the experiment names) and a value of
   * TYPE_FLOATING_POINT (to represent p-values).<p>
   * NOTE: No constraints on attributeName are documented by this API.  In
   * other words, as far as this API is concerned, any attributeName is
   * acceptable, including the empty string ("").  The only necessary
   * condition is that each attributeName be unique.
   * @param attributeName an identifier for this attribute definition;
   *   this value must be unique from all existing attribute definitions;
   *   ideally, the choice of name would describe values being stored by this
   *   attribute definition.
   * @param valueType one of the TYPE_* constants defining what type of
   *   values are bound to objects in this attribute definition.
   * @param keyTypes defines the type (TYPE_*) of each dimension in the key
   *   space;
   *   the entry at index i defines the type of key space dimension i + 1;
   *   this parameter may either be null or the empty array if an attribute
   *   definition does not use a key space (this is perhaps the most common
   *   scenario).
   * @exception IllegalStateException if attributeName is already the name
   *   of an existing attribute definition.
   * @exception NullPointerException if attributeName is null.
   * @exception IllegalArgumentException if valueType is not one of the
   *   TYPE_* constants, or if keyTypes is [not null and] of positive length
   *   and any one of its elements is not one of the TYPE_* constants.
   */
  public void defineAttribute(String attributeName,
                              byte valueType,
                              byte[] keyTypes);

  /**
   * Returns all defined attributeNames.<p>
   * NOTE: The returned iterator does not support the remove() operation.<p>
   * NOTE: To find out whether or not an attributeName is defined, use
   * getAttributeValueType(attributeName) and test whether or not the
   * return value is negative.<p>
   * IMPORTANT: The returned iterator becomes invalid as soon as any
   * attributeName is defined or undefined in this MultiHashMapDefinition.
   * Calling methods on an invalid iterator will result in undefined
   * behavior of that iterator.
   * @return an iterator of java.lang.String; each returned string
   *   is an attributeName (an attribute definition name).
   */
  public CountedIterator getDefinedAttributes();

  /**
   * Returns the type of attribute values that exist in specified attribute
   * space.
   * @return the type (TYPE_*) of values bound to objects by this attribute
   *   definition, or -1 if specified attribute definition does not exist;
   *   note that all of the TYPE_* constants are positive.
   * @exception NullPointerException if attributeName is null.
   */
  public byte getAttributeValueType(String attributeName);

  /**
   * Returns information about the dimensionality and types in the key space
   * of specified attribute.
   * @param attributeName the attribute definition whose key space
   *   we are querying.
   * @return a carbon copy of the array that was used to initially define
   *   attributeName (see defineAttribute()); implementations are required
   *   to instantiate and return a new array on each call to this method;
   *   if attributeName has no key space defined, the empty array is returned;
   *   null is never returned.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition.
   * @exception NullPointerException if attributeName is null.
   */
  public byte[] getAttributeKeyspaceDimensionTypes(String attributeName);

  /**
   * WARNING: All bound attribute values on objects will go away in this
   * attribute namespace when this method is called.
   * @param attributeName the attribute definition to undefine.
   * @return true if and only if attributeName was defined prior to this
   *   method invocation.
   * @exception NullPointerException if attributeName is null.
   */
  public boolean undefineAttribute(String attributeName);

  /**
   * Registers a listener for receiving events having to do with
   * attribute definitions being created and deleted.
   * @param listener the listener to register.
   */
  public void addDataDefinitionListener(
                                      MultiHashMapDefinitionListener listener);

  /**
   * Unregisters a listener; this method has the opposite effect as
   * addDataDefinitionListener(listener).
   * @param listener the listener to unregister.
   */
  public void removeDataDefinitionListener(
                                      MultiHashMapDefinitionListener listener);

}

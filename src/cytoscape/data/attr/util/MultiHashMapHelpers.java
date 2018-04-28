
/*
  File: MultiHashMapHelpers.java 
  
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

package cytoscape.data.attr.util;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains static utilitarian methods that return information
 * pertaining to attributes.  The reason why the methods here are defined
 * in this helper class and not in MultiHashMap is that
 * the return values in these
 * methods are simply computed using the public API methods of MultiHashMap.
 * MultiHashMapHelpers is an implementation layer on top of MultiHashMap,
 * whereas MultiHashMap
 * provides only those methods which are either essential to its API or are
 * optimized by its implementation.
 */
public final class MultiHashMapHelpers
{
  // NOTE: This class resides in the same package as the MultiHashMapModel
  // implementation of MultiHashMap for a good reason.  We may want some
  // of these helper methods to be more optimized than just calling
  // MultiHashMapModel
  // by its MultiHashMap interface.  We could provide dual implementations for
  // every method in this class, and choose the path of execution based on
  // whether or not our input object is an instance of MultiHashMapModel.
  // Right now, though, everything is implemented in terms of MultiHashMap
  // interface methods.

  // No constructor.  Static methods only.
  private MultiHashMapHelpers() { }

  /**
   * Convenience method for discovering all attribute values on an object in
   * a given attribute definition; this method is only useful with attribute
   * definitions that have nonzero key spaces.
   * @param objectKey the object whose attribute values to return.
   * @param attributeName the attribute definition in which to find attribute
   *   values.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return a list of all bound values on objectKey in
   *   attributeName, with duplicate values included; the returned list
   *   is never null; elements in the returned list are ordered
   *   arbitrarily; subsequent operations on cyData or cyDataDef will have
   *   no effect on the returned list.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters is
   *   null.
   */
  public static List getAllAttributeValues(
                                        final String objectKey,
                                        final String attributeName,
                                        final MultiHashMap cyData,
                                        final MultiHashMapDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionTypes(attributeName).length;
    if (keyspaceDims < 1) { // It's either 0 or -1.
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, null); // May trigger exception; OK.
      if (attrVal != null) bucket.add(attrVal); }
    else { // keyspaceDims > 0.
      r_getAllAttributeValues(objectKey, attributeName, cyData,
                              bucket, new Object[0], keyspaceDims); }
    return bucket;
  }

  /**
   * Convenience method for discovering attribute values along a specified
   * key prefix; this method is only useful with attribute
   * definitions that have nonzero key spaces.
   * @param objectKey the object whose attribute values to return.
   * @param attributeName the attribute definition in which to find attribute
   *   values.
   * @param keyPrefix an array of length less than or equal to the
   *   dimensionality of key space of attributeName; entry at index i contains
   *   a "representative" from dimension i + 1 of the key space of
   *   attributeName; keyPrefix may be either null or the empty array, in
   *   which case all attribute values bound to objectKey in attributeName will
   *   be returned; if keyPrefix is not empty, all bound values having key
   *   sequences whose beginning matches the specified prefix will be returned.
   * @param cyData the data repository to use to dig for attribute values.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return a list of all bound values on objectKey in attributeName
   *   along key space prefix keyPrefix, with duplicate values included; the
   *   returned list is never null; elements in the returned
   *   list are ordered arbitrarily; subsequent operations on cyData or
   *   cyDataDef will have no effect on the returned list.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters except
   *   keyPrefix is null, or if keyPrefix is of positive length and any one
   *   of its entries is null.
   * @exception ClassCastException if keyPrefix is of positive length and any
   *   one of its entries does not match the type of object specified
   *   by corresponding dimension type in attributeName's definition.
   * @exception IllegalArgumentException if keyPrefix's length is
   *   greater than the dimensionality of attributeName's key space.
   */
  public static List getAllAttributeValuesAlongPrefix(
                                        final String objectKey,
                                        final String attributeName,
                                        final Object[] keyPrefix,
                                        final MultiHashMap cyData,
                                        final MultiHashMapDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionTypes(attributeName).length;
    final int prefixDims = (keyPrefix == null ? 0 : keyPrefix.length);
    if (keyspaceDims <= prefixDims) {
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, keyPrefix); // May trigger exception; OK.
      if (attrVal != null) bucket.add(attrVal); }
    else {
      final Object[] keyPrefixCopy = new Object[prefixDims];
      for (int i = 0; i < prefixDims; i++) keyPrefixCopy[i] = keyPrefix[i];
      r_getAllAttributeValues(objectKey, attributeName, cyData,
                              bucket, keyPrefixCopy, keyspaceDims); }
    return bucket;
  }

  // Recursive helper for getAllAttributeValues() and
  // getAllAttributeValuesAlongPrefix().
  private static void r_getAllAttributeValues(final String objectKey,
                                              final String attributeName,
                                              final MultiHashMap dataRegistry,
                                              final ArrayList bucket,
                                              final Object[] prefixSoFar,
                                              final int keyspaceDims)
  {
    final CountedIterator currentKeyspan =
      dataRegistry.getAttributeKeyspan(objectKey, attributeName, prefixSoFar);
    final Object[] newPrefix = new Object[prefixSoFar.length + 1];
    for (int i = 0; i < prefixSoFar.length; i++) newPrefix[i] = prefixSoFar[i];
    while (currentKeyspan.hasNext()) {
      newPrefix[newPrefix.length - 1] = currentKeyspan.next();
      if (keyspaceDims == newPrefix.length) // The final dimension.
        bucket.add(dataRegistry.getAttributeValue
                   (objectKey, attributeName, newPrefix));
      else // Not the final dimension.
        r_getAllAttributeValues(objectKey, attributeName, dataRegistry,
                                bucket, newPrefix, keyspaceDims); }
  }

  /**
   * Convenience method for discovering all key sequnces that map into
   * bound values; this method is primarily useful with attribute
   * definitions that have nonzero key spaces.
   * @param objectKey the object whose mapped attribute keys to return.
   * @param attributeName the attribute definition in which to find
   *   attribute keys.
   * @param cyData the data repository to use to discover attribute keys.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return a list of Object[]; each Object[] in the returned list is
   *   a unique key sequence into a bound value; the returned list is never
   *   null and always contains the full set of key sequences registered on
   *   objectKey in attributeName; subsequent operations on cyData or cyDataDef
   *   will have no effect on the returned list.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters is
   *   null.
   */
  public static List getAllAttributeKeys(
                                        final String objectKey,
                                        final String attributeName,
                                        final MultiHashMap cyData,
                                        final MultiHashMapDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionTypes(attributeName).length;
    if (keyspaceDims < 1) { // It's either 0 or -1.
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, null); // May trigger exception; OK.
      if (attrVal != null) bucket.add(new Object[0]); }
    else { // keyspaceDims > 0.
      r_getAllAttributeKeys(objectKey, attributeName, cyData,
                            bucket, new Object[0], keyspaceDims); }
    return bucket;
  }

  /**
   * Convenience method for discovering all key sequences having a given
   * prefix that map into bound values; this method is primarily useful with
   * attribute definitions that have nonzero key spaces.
   * @param objectKey the object whose mapped attribute keys to return.
   * @param attributeName the attribute definition in which to find
   *   attribute keys.
   * @param keyPrefix an array of length less than or equal to the
   *   dimensionality of key space of attributeName; entry at index i contains
   *   a "representative" from dimension i + 1 of the key space of
   *   attributeName; keyPrefix may be either null or the empty array, in which
   *   case all attribute key sequences mapped to attribute values for
   *   objectKey in attributeName will be returned; if keyPrefix is not empty,
   *   all mapped key sequences having prefix keyPrefix will be returned.
   * @param cyData the data repository to use to discover attribute keys.
   * @param cyDataDef the data definition registry to use to find out about
   *   the dimensionality of attributeName.
   * @return a list of Object[]; each Object[] in the returned list is
   *   a unique key sequence whose beginning matches keyPrefix and which
   *   is mapped to a value; the returned list contains all such key sequences;
   *   the return value is never null; subsequent operations on cyData or
   *   cyDataDef will have no effect on the returned list.
   * @exception IllegalStateException if attributeName is not an existing
   *   attribute definition in cyData and cyDataDef.
   * @exception NullPointerException if any one of the input parameters except
   *   keyPrefix is null, or if keyPrefix is of positive length and any one
   *   of its entries is null.
   * @exception ClassCastException if keyPrefix is of positive length and any
   *   one of its entries does not match the type of object specified
   *   by corresponding dimension type in attributeName's definition.
   * @exception IllegalArgumentException if keyPrefix's length is
   *   greater than the dimensionality of attributeName's key space.
   */
  public static List getAllAttributeKeysAlongPrefix(
                                        final String objectKey,
                                        final String attributeName,
                                        final Object[] keyPrefix,
                                        final MultiHashMap cyData,
                                        final MultiHashMapDefinition cyDataDef)
  {
    final ArrayList bucket = new ArrayList();
    final int keyspaceDims =
      cyDataDef.getAttributeKeyspaceDimensionTypes(attributeName).length;
    final int prefixDims = (keyPrefix == null ? 0 : keyPrefix.length);
    final Object[] keyPrefixCopy = new Object[prefixDims];
    for (int i = 0; i < prefixDims; i++) keyPrefixCopy[i] = keyPrefix[i];
    if (keyspaceDims <= prefixDims) {
      final Object attrVal = cyData.getAttributeValue
        (objectKey, attributeName, keyPrefixCopy); // ? trigger exception; OK.
      if (attrVal != null) bucket.add(keyPrefixCopy); }
    else {
      r_getAllAttributeKeys(objectKey, attributeName, cyData,
                            bucket, keyPrefixCopy, keyspaceDims); }
    return bucket;
  }

  // Recursive helper for getAllAttributeKeys() and
  // getAllAttributeKeysAlongPrefix().
  private static void r_getAllAttributeKeys(final String objectKey,
                                            final String attributeName,
                                            final MultiHashMap dataRegistry,
                                            final ArrayList bucket,
                                            final Object[] prefixSoFar,
                                            final int keyspaceDims)
  {
    final CountedIterator currentKeyspan =
      dataRegistry.getAttributeKeyspan(objectKey, attributeName, prefixSoFar);
    if (keyspaceDims == prefixSoFar.length + 1) { // The final dimension.
      while (currentKeyspan.hasNext()) {
        final Object[] newPrefix = new Object[prefixSoFar.length + 1];
        for (int i = 0; i < prefixSoFar.length; i++)
          newPrefix[i] = prefixSoFar[i];
        newPrefix[newPrefix.length - 1] = currentKeyspan.next();
        bucket.add(newPrefix); } }
    else { // Not the final dimension.
      final Object[] newPrefix = new Object[prefixSoFar.length + 1];
      for (int i = 0; i < prefixSoFar.length; i++)
        newPrefix[i] = prefixSoFar[i];
      while (currentKeyspan.hasNext()) {
        newPrefix[newPrefix.length - 1] = currentKeyspan.next();
        r_getAllAttributeKeys(objectKey, attributeName, dataRegistry,
                              bucket, newPrefix, keyspaceDims); } }
  }

}

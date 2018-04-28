
/*
  File: CyAttributesImpl.java 
  
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

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.attr.util.MultiHashMapFactory;
import java.util.*;

public class CyAttributesImpl implements CyAttributes
{

  private final MultiHashMap mmap;
  private final MultiHashMapDefinition mmapDef;

  //  used to store human readable descriptions of attributes.
  private Map descriptionMap;

  //  used to store only those attributes, which should be hidden from
  //  the end user.
  private Set userInvisibleSet;

  //  used to store only those attributes, which should not be editable
  //  by the end user.
  private Set userNonEditableSet;

  public CyAttributesImpl()
  {
    Object model = MultiHashMapFactory.instantiateDataModel();
    mmap = (MultiHashMap) model;
    mmapDef = (MultiHashMapDefinition) model;
    descriptionMap = new HashMap();
    userInvisibleSet = new HashSet();
    userNonEditableSet = new HashSet();
  }

  public void setAttributeDescription(String attributeName, String description)
  {
      descriptionMap.put(attributeName, description);
  }

  public String getAttributeDescription(String attributeName) {
      return (String) descriptionMap.get(attributeName);
  }

  public void setUserVisible(String attributeName, boolean value)
  {
      if (value) {
          if (userInvisibleSet.contains(attributeName)) {
            userInvisibleSet.remove(attributeName);
          }
      } else {
          if (!userInvisibleSet.contains(attributeName)) {
            userInvisibleSet.add(attributeName);
          }
      }
  }

  public boolean getUserVisible(String attributeName)
  {
      //  by default, all attributes are visible, return value = true
      if (userInvisibleSet.contains(attributeName)) {
          return false;
      } else {
          return true;
      }
  }

  public void setUserEditable(String attributeName, boolean value)
  {
      if (value) {
          if (userNonEditableSet.contains(attributeName)) {
            userNonEditableSet.remove(attributeName);
          }
      } else {
          if (!userNonEditableSet.contains(attributeName)) {
            userNonEditableSet.add(attributeName);
          }
      }
  }

  public boolean getUserEditable(String attributeName)
  {
      //  by default, all attributes are editable, return value = true
      if (userNonEditableSet.contains(attributeName)) {
          return false;
      } else {
          return true;
      }
  }

  public String[] getAttributeNames()
  {
    final CountedIterator citer = mmapDef.getDefinedAttributes();
    final String[] names = new String[citer.numRemaining()];
    int inx = 0;
    while (citer.hasNext()) {
      names[inx++] = (String) citer.next(); }
    return names;
  }

  public boolean hasAttribute(String id, String attributeName)
  {
    final byte valType = mmapDef.getAttributeValueType(attributeName);
    if (valType < 0) return false;
    final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
      (attributeName);
    if (dimTypes.length == 0) {
      return mmap.getAttributeValue(id, attributeName, null) != null; }
    else {
      return mmap.getAttributeKeyspan
        (id, attributeName, null).numRemaining() > 0; }
  }

  public void setAttribute(String id, String attributeName, Boolean value)
  {
    if (id == null) throw new IllegalArgumentException("id is null");
    if (attributeName == null)
      throw new IllegalArgumentException("attributeName is null");
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) {
      mmapDef.defineAttribute(attributeName,
                              MultiHashMapDefinition.TYPE_BOOLEAN,
                              null);
    }
    else {
      if (type != MultiHashMapDefinition.TYPE_BOOLEAN) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_BOOLEAN"); }
      final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
        (attributeName);
      if (dimTypes.length != 0) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_BOOLEAN"); } }
    mmap.setAttributeValue(id, attributeName, value, null);
  }

  public void setAttribute(String id, String attributeName, Integer value)
  {
    if (id == null) throw new IllegalArgumentException("id is null");
    if (attributeName == null)
      throw new IllegalArgumentException("attributeName is null");
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) {
      mmapDef.defineAttribute(attributeName,
                              MultiHashMapDefinition.TYPE_INTEGER,
                              null);
    }
    else {
      if (type != MultiHashMapDefinition.TYPE_INTEGER) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_INTEGER"); }
      final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
        (attributeName);
      if (dimTypes.length != 0) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_INTEGER"); } }
    mmap.setAttributeValue(id, attributeName, value, null);
  }

  public void setAttribute(String id, String attributeName, Double value)
  {
    if (id == null) throw new IllegalArgumentException("id is null");
    if (attributeName == null)
      throw new IllegalArgumentException("attributeName is null");
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) {
      mmapDef.defineAttribute(attributeName,
                              MultiHashMapDefinition.TYPE_FLOATING_POINT,
                              null);
    }
    else {
      if (type != MultiHashMapDefinition.TYPE_FLOATING_POINT) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_FLOATING"); }
      final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
        (attributeName);
      if (dimTypes.length != 0) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_FLOATING"); } }
    mmap.setAttributeValue(id, attributeName, value, null);
  }

  public void setAttribute(String id, String attributeName, String value)
  {
    if (id == null) throw new IllegalArgumentException("id is null");
    if (attributeName == null)
      throw new IllegalArgumentException("attributeName is null");
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) {
      mmapDef.defineAttribute(attributeName,
                              MultiHashMapDefinition.TYPE_STRING,
                              null);
    }
    else {
      if (type != MultiHashMapDefinition.TYPE_STRING) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_STRING"); }
      final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
        (attributeName);
      if (dimTypes.length != 0) {
        throw new IllegalArgumentException
          ("definition for attributeName '" + attributeName +
           "' already exists and it is not of TYPE_STRING"); } }
    mmap.setAttributeValue(id, attributeName, value, null);
  }

  public Boolean getBooleanAttribute(String id, String attributeName)
  {
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) { return null; }
    if (type != MultiHashMapDefinition.TYPE_BOOLEAN) {
      throw new ClassCastException
        ("definition for attributeName '" + attributeName +
         "' is not of TYPE_BOOLEAN"); }
    return (Boolean) mmap.getAttributeValue(id, attributeName, null);
  }

  public Integer getIntegerAttribute(String id, String attributeName)
  {
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) { return null; }
    if (type != MultiHashMapDefinition.TYPE_INTEGER) {
      throw new ClassCastException
        ("definition for attributeName '" + attributeName +
         "' is not of TYPE_INTEGER"); }
    return (Integer) mmap.getAttributeValue(id, attributeName, null);
  }

  public Double getDoubleAttribute(String id, String attributeName)
  {
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) { return null; }
    if (type != MultiHashMapDefinition.TYPE_FLOATING_POINT) {
      throw new ClassCastException
        ("definition for attributeName '" + attributeName +
         "' is not of TYPE_FLOATING"); }
    return (Double) mmap.getAttributeValue(id, attributeName, null);
  }

  public String getStringAttribute(String id, String attributeName)
  {
    final byte type = mmapDef.getAttributeValueType(attributeName);
    if (type < 0) { return null; }
    if (type != MultiHashMapDefinition.TYPE_STRING) {
      throw new ClassCastException
        ("definition for attributeName '" + attributeName +
         "' is not of TYPE_STRING"); }
    return (String) mmap.getAttributeValue(id, attributeName, null);
  }

  public byte getType(String attributeName)
  {
    final byte valType = mmapDef.getAttributeValueType(attributeName);
    if (valType < 0) { return TYPE_UNDEFINED; }
    final byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes
      (attributeName);
    if (dimTypes.length == 0) { return valType; }
    if (dimTypes.length > 1) { return TYPE_COMPLEX; }
    if (dimTypes[0] == MultiHashMapDefinition.TYPE_INTEGER) {
      return TYPE_SIMPLE_LIST; }
    if (dimTypes[0] == MultiHashMapDefinition.TYPE_STRING) {
      return TYPE_SIMPLE_MAP; }
    return TYPE_COMPLEX;
  }

  public boolean deleteAttribute(String id, String attributeName)
  {
    boolean b = mmap.removeAllAttributeValues(id, attributeName);
    return b;
  }

  public boolean deleteAttribute(String attributeName)
  {
    boolean b = mmapDef.undefineAttribute(attributeName);
    return b;
  }

    // deprecated
  public void setAttributeList(String id, String attributeName, List list)
    {
	setListAttribute (id, attributeName, list);
    }


  public void setListAttribute(String id, String attributeName, List list)
  {
    if (id == null) throw new IllegalArgumentException("id is null");
    if (attributeName == null)
      throw new IllegalArgumentException("attributeName is null");
    if (list == null) throw new IllegalArgumentException("list is null");
    Iterator itor = list.iterator();
    if (!itor.hasNext()) { return; }
    final byte type;
    Object obj = itor.next();
    if (obj instanceof Double) { type = TYPE_FLOATING; }
    else if (obj instanceof Integer) { type = TYPE_INTEGER; }
    else if (obj instanceof Boolean) { type = TYPE_BOOLEAN; }
    else if (obj instanceof String) { type = TYPE_STRING; }
    else throw new IllegalArgumentException
           ("objects in list are of unrecognized type");
    while (itor.hasNext()) {
      obj = itor.next();
      if ((type == TYPE_FLOATING && (!(obj instanceof Double))) ||
          (type == TYPE_INTEGER && (!(obj instanceof Integer))) ||
          (type == TYPE_BOOLEAN && (!(obj instanceof Boolean))) ||
          (type == TYPE_STRING && (!(obj instanceof String))))
        throw new IllegalArgumentException
          ("items in list are not all of the same type"); }
    final byte valType = mmapDef.getAttributeValueType(attributeName);
    if (valType < 0) {
      mmapDef.defineAttribute
        (attributeName,
         type,
         new byte[] { MultiHashMapDefinition.TYPE_INTEGER } );
    }
    else {
      if (valType != type) {
        throw new IllegalArgumentException
          ("existing definition for attributeName '" + attributeName +
           "' is a TYPE_SIMPLE_LIST that stores other value types"); }
      final byte[] keyTypes =
        mmapDef.getAttributeKeyspaceDimensionTypes(attributeName);
      if (keyTypes.length != 1 ||
          keyTypes[0] != MultiHashMapDefinition.TYPE_INTEGER) {
        throw new IllegalArgumentException
          ("existing definition for attributeName '" + attributeName +
           "' is not of TYPE_SIMPLE_LIST"); } }
    mmap.removeAllAttributeValues(id, attributeName);
    itor = list.iterator();
    int inx = 0;
    final Object[] key = new Object[1];
    while (itor.hasNext()) {
      key[0] = new Integer(inx++);
      mmap.setAttributeValue(id, attributeName, itor.next(), key); }
  }

    // deprecated
  public List getAttributeList(String id, String attributeName)
    {
	return getListAttribute (id, attributeName);
    }


  public List getListAttribute(String id, String attributeName)
  {
    if (mmapDef.getAttributeValueType(attributeName) < 0) { return null; }
    final byte[] keyTypes = mmapDef.getAttributeKeyspaceDimensionTypes
      (attributeName);
    if (keyTypes.length != 1 ||
        keyTypes[0] != MultiHashMapDefinition.TYPE_INTEGER) {
      throw new ClassCastException
        ("attributeName '" + attributeName +
         "' is not of TYPE_SIMPLE_LIST"); }
    final ArrayList returnThis = new ArrayList();
    final Object[] key = new Object[1];
    for (int i = 0;; i++) {
      key[0] = new Integer(i);
      final Object val = mmap.getAttributeValue(id, attributeName, key);
      if (val == null) break;
      returnThis.add(i, val); }
    return returnThis;
  }

    // deprecated
  public void setAttributeMap(String id, String attributeName, Map map)
    {
	setMapAttribute(id, attributeName, map);
    }


  public void setMapAttribute(String id, String attributeName, Map map)
  {
    if (id == null) throw new IllegalArgumentException("id is null");
    if (attributeName == null)
      throw new IllegalArgumentException("attributeName is null");
    final Set entrySet = map.entrySet();
    Iterator itor = entrySet.iterator();
    if (!itor.hasNext()) { return; }
    final byte type;
    Map.Entry entry = (Map.Entry) itor.next();
    if (!(entry.getKey() instanceof String)) {
      throw new IllegalArgumentException("keys in map are not all String"); }
    Object val = entry.getValue();
    if (val instanceof Double) { type = TYPE_FLOATING; }
    else if (val instanceof Integer) { type = TYPE_INTEGER; }
    else if (val instanceof Boolean) { type = TYPE_BOOLEAN; }
    else if (val instanceof String) { type = TYPE_STRING; }
    else throw new IllegalArgumentException
           ("values in map are of unrecognized type");
    while (itor.hasNext()) {
      entry = (Map.Entry) itor.next();
      if (!(entry.getKey() instanceof String)) {
        throw new IllegalArgumentException("keys in map are not all String"); }
      val = entry.getValue();
      if ((type == TYPE_FLOATING && (!(val instanceof Double))) ||
          (type == TYPE_INTEGER && (!(val instanceof Integer))) ||
          (type == TYPE_BOOLEAN && (!(val instanceof Boolean))) ||
          (type == TYPE_STRING && (!(val instanceof String))))
        throw new IllegalArgumentException
          ("values in map are not all of the same type"); }
    final byte valType = mmapDef.getAttributeValueType(attributeName);
    if (valType < 0) {
      mmapDef.defineAttribute
        (attributeName, type,
         new byte[] { MultiHashMapDefinition.TYPE_STRING } );
    }
    else {
      if (valType != type) {
        throw new IllegalArgumentException
          ("existing definition for attributeName '" + attributeName +
           "' is a TYPE_SIMPLE_MAP that stores other value types"); }
      final byte[] keyTypes =
        mmapDef.getAttributeKeyspaceDimensionTypes(attributeName);
      if (keyTypes.length != 1 ||
          keyTypes[0] != MultiHashMapDefinition.TYPE_STRING) {
        throw new IllegalArgumentException
          ("existing definition for attributeName '" + attributeName +
           "' is not of TYPE_SIMPLE_MAP"); } }
    mmap.removeAllAttributeValues(id, attributeName);
    itor = entrySet.iterator();
    final Object[] key = new Object[1];
    while (itor.hasNext()) {
      entry = (Map.Entry) itor.next();
      key[0] = entry.getKey();
      mmap.setAttributeValue(id, attributeName, entry.getValue(), key); }
   }

    // deprecated
  public Map getAttributeMap(String id, String attributeName)
    {
	return getMapAttribute(id, attributeName);
    }


  public Map getMapAttribute(String id, String attributeName)
  {
    if (mmapDef.getAttributeValueType(attributeName) < 0) { return null; }
    final byte[] keyTypes = mmapDef.getAttributeKeyspaceDimensionTypes
      (attributeName);
    if (keyTypes.length != 1 ||
        keyTypes[0] != MultiHashMapDefinition.TYPE_STRING) {
      throw new ClassCastException
        ("attributeName '" + attributeName +
         "' is not of TYPE_SIMPLE_MAP"); }
    final Map returnThis = new HashMap();
    final Iterator keyspan = mmap.getAttributeKeyspan(id, attributeName, null);
    final Object[] key = new Object[1];
    while (keyspan.hasNext()) {
      key[0] = keyspan.next();
      returnThis.put(key[0], mmap.getAttributeValue(id, attributeName, key)); }
    return returnThis;
  }

  public MultiHashMap getMultiHashMap()
  {
    return mmap;
  }

  public MultiHashMapDefinition getMultiHashMapDefinition()
  {
    return mmapDef;
  }

}


/*
  File: NetworkData.java 
  
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

// giny import
import giny.model.*;

// cytoscape import
import cytoscape.*;

// colt import
import cern.colt.map.*;
import cern.colt.list.*;

// tclib import 
import com.sosnoski.util.hashmap.*;

// java import 
import java.util.*;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * There is an event fired anytime that a value is modified, you need to be listener to the attribute name though. 
 */
public abstract class NetworkData {

  public static String NODE_ATTRIBUTE_ADDED = "NODE_ATTRIBUTE_ADDED";
  public static String NODE_ATTRIBUTE_REMOVED = "NODE_ATTRIBUTE_REMOVED";
  public static String EDGE_ATTRIBUTE_ADDED = "EDGE_ATTRIBUTE_ADDED";
  public static String EDGE_ATTRIBUTE_REMOVED = "EDGE_ATTRIBUTE_REMOVED";


  public static int DOUBLE_TYPE = 0;
  public static int STRING_TYPE = 1;
  public static int OBJECT_TYPE = 2;
  

  public static int NO_SUCH_ATTRIBUTE = -1;
  public static int VALUE_NOT_A_DOUBLE = -2;
  public static int INSANE_ATTRIBUTE_TYPE = -4;
  public static int WRONG_ATTRIBUTE_TYPE = -8;
  public static int INSANE_ATTRIBUTE_ID = -16;
  public static int ATTRIBUTE_ALREADY_ASSIGNED = -32;

  protected static Object pcsO = new Object();
  private static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);


  //////////////////////////////
  // Node Data Structures
   
  /**
   * Each Entry is also an IntObject Map, so that each attribute has itrs own entry,
   * and each node has its own entry within each attribute.
   */
  private static OpenIntObjectHashMap nodeAttributeIDToAttributeValuesMap;

  /**
   * Keeps track of the map between an Attribute ID ( which is also
   * the key for accessing the value of an attribute for a node).
   */
  private static StringIntHashMap nodeAttNameToAttIDMap;
  
  /**
   * reverse lookup of the attribute name for a given int.
   */
   private static OpenIntObjectHashMap nodeAttIDToAttNameMap;

  /**
   * Look up of attribute id to Data Type
   */
  private static OpenIntIntHashMap nodeAttIDToAttTypeMap;


  private static OpenIntDoubleHashMap nodeAttIDToMinMap;
  private static OpenIntDoubleHashMap nodeAttIDToMaxMap;
  private static OpenIntDoubleHashMap nodeAttIDToMeanMap;
  private static OpenIntDoubleHashMap nodeAttIDToMedianMap;
  private static OpenIntDoubleHashMap nodeAttIDLastUpdate;


  /**
   * The functionality of this Map is largely replaceable by a BioDataServer,
   * so this look-up will only be used when a BioDataServer is unavalible, or
   * does not contain an alias.
   */
  private static StringIntHashMap nodeAliasToIDMap;
  

  /**
   * Each attribute is an int > 1.
   */
  private static int nodeAttributeCount = 1;


  //////////////////////////////
  // node data access methods

  /**
   * Attempts to add a new Attribute name, of Object type for nodes.
   * @param attribute_name the name of the new attribute
   * @return the ID of the attribute, if assinged, or -1 if already assigned/not a good name.
   */
  public static int addNodeAttribute ( String attribute_name ) {
    return addNodeAttribute( attribute_name, OBJECT_TYPE );
  }
  
  /**
   * Attempts to add a new Attribute name, of given type for nodes.
   * Available types are: OBJECT_TYPE, DOUBLE_TYPE, and STRING_TYPE
   * @param attribute_name the name of the new attribute
   * @return the ID of the attribute, if assinged, or -1 if already assigned/not a good name.
   */
  public static int addNodeAttribute ( String attribute_name, int attribute_type ) {
    StringIntHashMap a2id = getNodeAttNameToAttIDMap();

    // return if already contained
    if ( a2id.containsKey( attribute_name ) ) {
      return ATTRIBUTE_ALREADY_ASSIGNED;
    }
    // return if type is invalid
    if ( attribute_type != OBJECT_TYPE && attribute_type != DOUBLE_TYPE && attribute_type != STRING_TYPE ) {
      return INSANE_ATTRIBUTE_TYPE;
    }

    // associate the attribute name with its id
    int id = nodeAttributeCount++;
    a2id.add( attribute_name, id );
  
    // associate the id with the name
    OpenIntObjectHashMap id2a = getNodeAttIDToAttNameMap();
    id2a.put( id, attribute_name );

    // associate the id with the type
    OpenIntIntHashMap id2t = getNodeAttIDToAttTypeMap();
    id2t.put( id, attribute_type );

    // create the attribute map 
    OpenIntObjectHashMap a2v = getNodeAttributeIDToAttributeValuesMap();
    if ( attribute_type == DOUBLE_TYPE ) {
      // for doubles, put make the double hash map
      OpenIntDoubleHashMap double_map = new OpenIntDoubleHashMap();
      a2v.put( id, double_map );
    } else if ( attribute_type == STRING_TYPE ) {
      // for strings, make a string hash map
      IntStringHashMap string_map = new IntStringHashMap();
      a2v.put( id, string_map );
    } else {
      OpenIntObjectHashMap object_map = new OpenIntObjectHashMap();
      a2v.put( id, object_map );
    }

    // everything should be normal, return the attribute id
    return id;
  }
  
  public static int getNodeAttributeType ( String attribute ) {
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      return NO_SUCH_ATTRIBUTE;

    return getNodeAttIDToAttTypeMap().get( att_id );
  }

  public static int getNodeAttributeID ( String attribute ) {
     int att_id = getNodeAttNameToAttIDMap().get( attribute );
     if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      return NO_SUCH_ATTRIBUTE;

     return att_id;
  }


  public static int setNodeAttributeValue ( Node node, String attribute, Object value ) {
    // todo: some sort of sanity check?
    int node_index = node.getRootGraphIndex();
    
    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, OBJECT_TYPE );

    return setNodeAttributeObjectValue( node_index, att_id, value );
  }

 


  /**
   * Attempts to set the value for a node, for a given attribute. However, there a few checks that happen along the way. First the Node must be part of the CytoscapeRootGraph, also the value must match the attribute type, which must match the overall attribute type. No problem if the attribute is not initialized, but if has been initialized, and the types don't match, then you have a problem.
   * @param node the node who should be part of Cytoscape
   * @param attribute the name of this attribute
   * @param value the value, which must match the attribute type
   * @param attribute_type if this attribute has been previosly initialized, then this musht match that type. Otherwise the attribute will get initialized with this type.
   * @return "1" or a positive value if all went well, otherwise a negative value that corresponds to the error. See the "throws" keeping in mind that these are not really exceptions...
   * @throws {@link VALUE_NOT_A_DOUBLE} if the attribute_type was double and the value could be put into a Double.
   * @throws {@link INSANE_ATTRIBUTE_TYPE} if the given attribute_type is not a supported one
   * @throws {@link WRONG_ATTRIBUTE_TYPE} if the given attribute_type does not match the attribute_type of the attribute
   * @throws {@link INSANE_ATTRIBUTE_ID} if the attribute does not exist
  */
  public static int setNodeAttributeValue ( Node node, String attribute, Object value, int attribute_type ) {
    // todo: some sort of sanity check?
    int node_index = node.getRootGraphIndex();
    
    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND ) {
      att_id = addNodeAttribute( attribute, attribute_type );
    }
      

    if ( attribute_type == OBJECT_TYPE ) {
      return setNodeAttributeObjectValue( node_index, att_id, value );
    } else if ( attribute_type == DOUBLE_TYPE ) {
      Double dub = null;
      if ( value instanceof Double ) {
        dub = ( Double )value;
      } else if ( value instanceof String ) {
        try {
          dub = new Double( (String)value );
        } catch ( NumberFormatException nfe ) {
          return VALUE_NOT_A_DOUBLE;
        }
      }
      if ( dub == null ) {
        return VALUE_NOT_A_DOUBLE;
      } else {
        return setNodeAttributeDoubleValue( node_index, att_id, dub.doubleValue() );
      }
    } else if ( attribute_type == STRING_TYPE ) {
      return setNodeAttributeStringValue( node_index, att_id, value.toString() );
    }
    return INSANE_ATTRIBUTE_TYPE;
  }

  // node object set methods
  
  public static int setNodeAttributeObjectValue( int node_index, String attribute, Object value ) {
    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, OBJECT_TYPE );

    return setNodeAttributeObjectValue( node_index, att_id, value );
  }

  public static int setNodeAttributeObjectValue( Node node, String attribute, Object value ) {
    // todo: some sort of sanity check?
    int node_index = node.getRootGraphIndex();
    
    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, OBJECT_TYPE );

    return setNodeAttributeObjectValue( node_index, att_id, value );
  }


  public static int setNodeAttributeObjectValue( int node_index, int att_id, Object value ) {
    // return if higher than the number of attributes
    if ( att_id > nodeAttributeCount )
      return INSANE_ATTRIBUTE_ID;
  
    // make sure that the attribute type is correct
    if ( getNodeAttIDToAttTypeMap().get( att_id ) != OBJECT_TYPE )
      return WRONG_ATTRIBUTE_TYPE;

    OpenIntObjectHashMap object_map = ( OpenIntObjectHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id );
    
    object_map.put( node_index, value );

    return 1;
    
  }


   // node double set methods
  
  public static int setNodeAttributeDoubleValue( int node_index, String attribute, double value ) {
    
    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, DOUBLE_TYPE );

    return setNodeAttributeDoubleValue( node_index, att_id, value );
  }

  public static int setNodeAttributeDoubleValue( Node node, String attribute, double value ) {
    // todo: some sort of sanity check?
    int node_index = node.getRootGraphIndex();

    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, DOUBLE_TYPE );

    return setNodeAttributeDoubleValue( node_index, att_id, value );
  }


  public static int setNodeAttributeDoubleValue( int node_index, int att_id, double value ) {
    // return if higher than the number of attributes
    if ( att_id > nodeAttributeCount )
      return INSANE_ATTRIBUTE_ID;
  
    // make sure that the attribute type is correct
    if ( getNodeAttIDToAttTypeMap().get( att_id ) != DOUBLE_TYPE )
      return WRONG_ATTRIBUTE_TYPE;

    OpenIntDoubleHashMap double_map = ( OpenIntDoubleHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id );
    
    double_map.put( node_index, value );

    return 1;
    
  }

  // node String set methods
  
  public static int setNodeAttributeStringValue( int node_index, String attribute, String value ) {
    
    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, STRING_TYPE );

    return setNodeAttributeStringValue( node_index, att_id, value );
  }

  public static int setNodeAttributeStringValue( Node node, String attribute, String value ) {
    // todo: some sort of sanity check?
    int node_index = node.getRootGraphIndex();

    // make sure the attribute exists, if not, create it.
    int att_id = getNodeAttNameToAttIDMap().get( attribute );
    if ( att_id == StringIntHashMap.DEFAULT_NOT_FOUND )
      att_id = addNodeAttribute( attribute, STRING_TYPE );

    return setNodeAttributeStringValue( node_index, att_id, value );
  }


  public static int setNodeAttributeStringValue( int node_index, int att_id, String value ) {
    // return if higher than the number of attributes
    if ( att_id > nodeAttributeCount )
      return INSANE_ATTRIBUTE_ID;
  
    // make sure that the attribute type is correct
    if ( getNodeAttIDToAttTypeMap().get( att_id ) != STRING_TYPE )
      return WRONG_ATTRIBUTE_TYPE;

    IntStringHashMap string_map = ( IntStringHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id );
    
    string_map.add( node_index, value );

    return 1;
    
  }


  public static Object getNodeAttributeValue ( Node node, String attribute ) {
    int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return null;
    int node_index = node.getRootGraphIndex();

    return( getNodeAttributeValue( node_index, att_id ) );
  }
   
  public static Object getNodeAttributeValue ( int node_index, int att_id ) {
    if ( att_id > nodeAttributeCount )
      return null;

    int att_type = getNodeAttIDToAttTypeMap().get( att_id );

    if ( att_type == OBJECT_TYPE )
      return ( ( OpenIntObjectHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );
    else if ( att_type == DOUBLE_TYPE ) {
      double result = getNodeAttributeDoubleValue( node_index, ( String )( getNodeAttIDToAttNameMap().get( att_id ) ) );
      if ( result == Double.NaN )
        return new Double( Double.NaN );
      return new Double( result );
    }
    else if ( att_type == STRING_TYPE ) {
      return ( ( IntStringHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );
    }
    return null;
  }

  public static Object getNodeAttributeObjectValue ( Node node, String attribute ) {
     int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return null;
    int node_index = node.getRootGraphIndex();
    int att_type = getNodeAttIDToAttTypeMap().get( att_id );
    if ( att_type == OBJECT_TYPE )
      return ( ( OpenIntObjectHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );
   
    return null;
  }

  public static String getNodeAttributeStringValue ( Node node, String attribute ) {
    int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return null;
    int node_index = node.getRootGraphIndex();
    int att_type = getNodeAttIDToAttTypeMap().get( att_id );
    if ( att_type == STRING_TYPE )
      return ( ( IntStringHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );

    return null;

  }

  public static double getNodeAttributeDoubleValue ( Node node, String attribute ) {
    int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return Double.NaN;
    int node_index = node.getRootGraphIndex();
    int att_type = getNodeAttIDToAttTypeMap().get( att_id );
    if ( att_type == DOUBLE_TYPE ) {
    	OpenIntObjectHashMap a2v = getNodeAttributeIDToAttributeValuesMap();
      OpenIntDoubleHashMap n2v = ( OpenIntDoubleHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id );
      if ( n2v == null ) {
        System.out.println( "n2v is null"  );
        return Double.NaN;
      }
      if ( n2v.containsKey( node_index ) )
        return n2v.get( node_index );


      //      return ( ( OpenIntDoubleHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );
    }
    return Double.NaN;
  }
  
   public static Object getNodeAttributeObjectValue ( int node_index, String attribute ) {
     int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return null;

    int att_type = getNodeAttIDToAttTypeMap().get( att_id );
    if ( att_type == OBJECT_TYPE )
      return ( ( OpenIntObjectHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );
   
    return null;
  }

  public static String getNodeAttributeStringValue ( int node_index, String attribute ) {
    int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return null;

    int att_type = getNodeAttIDToAttTypeMap().get( att_id );
    if ( att_type == STRING_TYPE )
      return ( ( IntStringHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );

    return null;

  }

  public static double getNodeAttributeDoubleValue ( int node_index, String attribute ) {
    int att_id = getNodeAttributeID( attribute );
    if ( att_id < 0 )
      return Double.NaN;
   
    int att_type = getNodeAttIDToAttTypeMap().get( att_id );
    if ( att_type == DOUBLE_TYPE ) {
    	OpenIntObjectHashMap a2v = getNodeAttributeIDToAttributeValuesMap();
      OpenIntDoubleHashMap n2v = ( OpenIntDoubleHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id );
      if ( n2v == null ) {
        System.out.println( "n2v is null"  );
        return Double.NaN;
      }
      if ( n2v.containsKey( node_index ) )
        return n2v.get( node_index );


      //      return ( ( OpenIntDoubleHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_id )).get( node_index );
    }
    return Double.NaN;
  }
  
  ////////////////////
  // Node Double Attribute Useful Methods

  /**
   * Returns all Attributes that one or more of the given nodes have values for.
   */
  public static String[] getNodeAttributes ( int[] node_indices ) {
    
    IntArrayList att_keys = new IntArrayList();
    getNodeAttributeIDToAttributeValuesMap().keys( att_keys );
    HashSet attributes = new HashSet(); 

    
    for ( int k = 0; k < att_keys.size(); ++k ) {
      for ( int i = 0; i < node_indices.length; ++i ) {
        if (  att_keys.get(k) == DOUBLE_TYPE ) {
          OpenIntDoubleHashMap n2v = ( OpenIntDoubleHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_keys.get(k) );
          if (  n2v.containsKey( node_indices[i] ) ) {
            attributes.add( getNodeAttIDToAttNameMap().get( att_keys.get(k) ) );
            // don't look at anymore nodes
            i = node_indices.length;
          }
        } else {
          OpenIntObjectHashMap n2v = ( OpenIntObjectHashMap )getNodeAttributeIDToAttributeValuesMap().get( att_keys.get(k) );
          if (  n2v.containsKey( node_indices[i] ) ) {
            attributes.add( getNodeAttIDToAttNameMap().get( att_keys.get(k) ) );
            // don't look at anymore nodes
            i = node_indices.length;
          }
          
        }
      }
    }

    return ( String[]) attributes.toArray( new String[]{} );
  }

 

    


    



  //////////////////////////////
  // node data structure access methods

  private static OpenIntObjectHashMap getNodeAttributeIDToAttributeValuesMap () {
    if ( nodeAttributeIDToAttributeValuesMap == null )
      nodeAttributeIDToAttributeValuesMap = new OpenIntObjectHashMap();
    return nodeAttributeIDToAttributeValuesMap;
  }

   private static StringIntHashMap getNodeAttNameToAttIDMap () {
     if ( nodeAttNameToAttIDMap == null )
       nodeAttNameToAttIDMap = new StringIntHashMap();
     return nodeAttNameToAttIDMap;
   }

   private static OpenIntObjectHashMap getNodeAttIDToAttNameMap () {
     if ( nodeAttIDToAttNameMap == null) 
       nodeAttIDToAttNameMap = new OpenIntObjectHashMap();
     return nodeAttIDToAttNameMap;
  }

  private static OpenIntIntHashMap getNodeAttIDToAttTypeMap () {
    if ( nodeAttIDToAttTypeMap == null )
      nodeAttIDToAttTypeMap = new OpenIntIntHashMap();
    return nodeAttIDToAttTypeMap;
  }

  private static StringIntHashMap getNodeAliasToIDMap () {
    if ( nodeAliasToIDMap == null )
      nodeAliasToIDMap = new StringIntHashMap();
    return nodeAliasToIDMap;
  }



}

  


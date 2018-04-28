/*
 File: Thesaurus.java 
 
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

// Thesaurus.java
//-----------------------------------------------------------------------------
// $Revision: 7760 $  
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//------------------------------------------------------------------------------
package cytoscape.data.synonyms;

//-----------------------------------------------------------------------------
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

//-----------------------------------------------------------------------------
/**
 * todo (pshannon, 25 oct 2002): there may be multiple canonicalNames for the
 * same common name. a good example is from the d16s3098 marker region studied
 * in the ISB's JDRF project:
 * 
 * PHEMX -> NM_005705 PHEMX -> NM_139022 PHEMX -> NM_139023 PHEMX -> NM_139024
 * 
 * these are (I believe) mRNA splice variants. what to do?
 */
public class Thesaurus implements Serializable {

	String species;
	HashMap labelToCommon; // String -> String
	HashMap commonToLabel; // String -> String
	HashMap aliasesToLabel; // String -> String
	HashMap labelToAll; // String -> Vector of Strings

	// -----------------------------------------------------------------------------
	public Thesaurus(String species) {
		this.species = species;
		labelToCommon = new HashMap();
		commonToLabel = new HashMap();
		aliasesToLabel = new HashMap();
		labelToAll = new HashMap();
	}

	// -----------------------------------------------------------------------------
	public String getSpecies() {
		return species;
	}

	// -----------------------------------------------------------------------------
	public int nodeLabelCount() {
		return labelToAll.size();
	}

	// -----------------------------------------------------------------------------
	public void add(String canonicalName, String commonName) {
		if (labelToCommon.containsKey(canonicalName)) {
			addAlternateCommonName(canonicalName, commonName);
		}

		if (commonToLabel.containsKey(commonName)) {
			// System.out.println ("commonName " + commonName + " already has
			// canonicalName " +
			// commonToCanonical.get (commonName) + " skipping new map: " +
			// commonName + " -> " + canonicalName);
		} else
			labelToCommon.put(canonicalName, commonName);

		commonToLabel.put(commonName, canonicalName);
		storeAmongAllCommonNames(commonName, canonicalName);

	} // add

	// -----------------------------------------------------------------------------

	public void remove(String canonicalName, String commonName) {
		labelToCommon.remove(canonicalName);
		commonToLabel.remove(commonName);
		labelToAll.remove(canonicalName);
	}

	// -----------------------------------------------------------------------------
	public void addAlternateCommonName(String canonicalName,
			String alternateCommonName) {
		aliasesToLabel.put(alternateCommonName, canonicalName);
		storeAmongAllCommonNames(alternateCommonName, canonicalName);
	}

	// -----------------------------------------------------------------------------
	protected void storeAmongAllCommonNames(String commonName,
			String canonicalName) {
		Vector allCommonNames;
		if (labelToAll.containsKey(canonicalName))
			allCommonNames = (Vector) labelToAll.get(canonicalName);
		else
			allCommonNames = new Vector();

		allCommonNames.add(commonName);
		labelToAll.put(canonicalName, allCommonNames);

	} // storeAmongAllCommonNames

	// -----------------------------------------------------------------------------

	public String getCommonName(String canonicalName) {
		return (String) labelToCommon.get(canonicalName);
	}

	// -----------------------------------------------------------------------------
	public String getNodeLabel(String commonName) {
		if (commonToLabel.containsKey(commonName))
			return (String) commonToLabel.get(commonName);
		else if (aliasesToLabel.containsKey(commonName))
			return (String) aliasesToLabel.get(commonName);
		else
			return null;

	} // getCanonicalName

	// -----------------------------------------------------------------------------

	public String[] getAllCommonNames(String canonicalName) {

		if (labelToAll.containsKey(canonicalName)) {
			Vector vector = (Vector) labelToAll.get(canonicalName);
			return (String[]) vector.toArray(new String[0]);
		} else
			return new String[0];

	} // getAllCommonNames

	// -----------------------------------------------------------------------------

	public String[] getAlternateCommonNames(String canonicalName) {
		if (labelToAll.containsKey(canonicalName)) {
			Vector vector = (Vector) labelToAll.get(canonicalName);
			vector.remove(getCommonName(canonicalName));
			return (String[]) vector.toArray(new String[0]);
		} else
			return new String[0];

	} // getAlternateCommonNames

	
	/*
	 * This method returns all names including aliases and key.
	 */
	public String[] getAllNamesInGroup(String key) {

		if (labelToAll.containsKey(key)) {
			Vector vector = (Vector) labelToAll.get(key);
			vector.add(key);
			return (String[]) vector.toArray(new String[0]);
		} else if(aliasesToLabel.containsKey(key)) {
			String label = (String) aliasesToLabel.get(key);
			Vector vector = (Vector) labelToAll.get(label);
			return (String[]) vector.toArray(new String[0]);
		}else
			return new String[0];

	}
	
	// -----------------------------------------------------------------------------

	public String toString() {
		int length = 0;
		if (labelToCommon != null)
			length = labelToCommon.size();

		return species + ": " + length;
	}
	
	
	/*
	 * This is for debugging.
	 * 
	 */
	public void dump() {	
		Iterator it = labelToCommon.keySet().iterator();
		while(it.hasNext()) {
			String label = (String) it.next();
			
			System.out.print("Key is " + label + ", and commons are ");
			
			String[] alias = this.getAllCommonNames(label);
			for(int i = 0; i<alias.length;i++) {
				System.out.print(alias[i] + ", " );
			}
			System.out.println("");
		}
	}
	
	// -----------------------------------------------------------------------------
} // Thesaurus


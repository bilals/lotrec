/*
 File: Annotation.java 
 
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

// Annotation.java

//------------------------------------------------------------------------------
// $Revision: 7760 $   
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $ 
// $Author: mes $
//-----------------------------------------------------------------------------------
package cytoscape.data.annotation;

//-----------------------------------------------------------------------------------
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * Store any number of classifications for named entities, each of which is from
 * a specified species, and where the classifications are all terms from an
 * ontology (a controlled vocabulary for some subject domain, expressed as a
 * directed acyclic graph). For example:
 * <p>
 * <ul>
 * <li> for the species Halobacterium
 * <li> with respect to the KEGG metabolic pathways ontology
 * <li> gene VNG0623G has been assigned into the following categories
 * <ol>
 * <li> Valine, leucine and isoleucine degradation
 * <li> Propanoate metabolism
 * </ol>
 * </ul>
 * 
 * This simple assignment of two classifications to one gene becomes richer when
 * we refer to the ontology: in the KEGG ontology, these two terms belong to a
 * tree, expressed linearly, and from the top (most general term) down as
 * <ul>
 * <li> metablolism -> amino acid metabolism -> valine, leucine and isoleucine
 * degradation
 * <li> metabolism -> carbohydrate metabolism -> propanoate metabolism
 * <ul>
 * 
 * Thus the combination of an annotation (the present class) with an ontology
 * provides a means to richly and flexibly describe an object.
 */
public class Annotation implements Serializable {
	protected Ontology ontology;
	protected String curator;
	protected String species;
	protected String type;
	protected HashMap hash; // (name, Vector) pairs, the Vector contains
							// Integers
	// ------------------------------------------------------------------------------

	public Annotation(String species, String type, Ontology ontology) {
		this.ontology = ontology;
		this.species = species;
		this.type = type;
		this.curator = ontology.getCurator();
		hash = new HashMap();

	} // ctor
	// ------------------------------------------------------------------------------

	public Annotation(String species, String type, String curator) {
		this.curator = curator;
		this.species = species;
		this.type = type;
		hash = new HashMap();

	} // ctor
	// ------------------------------------------------------------------------------

	public HashMap getMap() {
		return hash;
	}

	// ------------------------------------------------------------------------------
	public void setOntology(Ontology newValue) {
		ontology = newValue;
	}

	// ------------------------------------------------------------------------------
	public Ontology getOntology() {
		return ontology;
	}

	// ------------------------------------------------------------------------------
	public String getCurator() {
		return curator;
	}

	// ------------------------------------------------------------------------------
	public String getType() {
		return type;
	}

	// ------------------------------------------------------------------------------
	public String getOntologyType() {
		if (ontology != null)
			return ontology.getType();

		return "unknown";
	}

	// ------------------------------------------------------------------------------
	public String getSpecies() {
		return species;
	}

	// ------------------------------------------------------------------------------
	/**
	 * create a new annotation for an entity (typically a gene) of the current
	 * species and with respect to the current ontology. an entity may have more
	 * than one classification.
	 * 
	 * @param name
	 *            usually an ORF name, a unique identifier for this species
	 * @param classificationID
	 *            a pointer into the ontology hierarchy
	 */
	public void add(String name, int classificationID) {
		Vector classifications;
		if (hash.containsKey(name))
			classifications = (Vector) hash.get(name);
		else {
			classifications = new Vector();
			hash.put(name, classifications);
		}

		Integer classificationInteger = new Integer(classificationID);
		if (!classifications.contains(classificationInteger))
			classifications.add(new Integer(classificationID));

	} // add
	// ------------------------------------------------------------------------------

	/**
	 * returns an array of all the names (usually ORFs) currently annotated
	 */
	public String[] getNames() {
		return (String[]) hash.keySet().toArray(new String[0]);
	}

	// ------------------------------------------------------------------------------
	/**
	 * returns an array of all the classifications in the current annotation
	 */
	public int[] getClassifications() {
		Vector[] arrayOfIntegerVectors = (Vector[]) hash.values().toArray(
				new Vector[0]);

		Vector collector = new Vector();

		for (int v = 0; v < arrayOfIntegerVectors.length; v++) {
			Vector vec = arrayOfIntegerVectors[v];
			for (int i = 0; i < vec.size(); i++)
				collector.add(vec.get(i));
		} // for v

		int[] result = new int[collector.size()];

		for (int i = 0; i < result.length; i++)
			result[i] = ((Integer) collector.get(i)).intValue();

		return result;

	} // getClassifications
	// ------------------------------------------------------------------------------

	/**
	 * all of the ontology identifiers registered for the specified entity
	 */
	public int[] getClassifications(String name) {
		if (!hash.containsKey(name))
			return new int[0];
		Vector classifications = (Vector) hash.get(name);
		int[] result = new int[classifications.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = ((Integer) classifications.get(i)).intValue();

		return result;

	} // getClassifications
	// ------------------------------------------------------------------------------

	/**
	 * all of the ontology identifiers registered for the specified entity, as a
	 * Vector of Integers
	 */
	public Vector getClassificationsVector(String name) {
		if (!hash.containsKey(name))
			return new Vector();
		else
			return (Vector) hash.get(name);

	} // getClassificationsVector
	// ------------------------------------------------------------------------------

	public String[][] getAllHierarchyPathsAsNames(String name) {
		if (ontology == null)
			return new String[0][0];

		int[] leafClassifications = getClassifications(name);
		String[][] result;
		if (leafClassifications.length == 0) {
			result = new String[0][0];
		}

		Vector collector = new Vector();
		for (int i = 0; i < leafClassifications.length; i++) {
			String[][] paths = ontology
					.getAllHierarchyPathsAsNames(leafClassifications[i]);
			for (int p = 0; p < paths.length; p++)
				collector.add(paths[p]);
		}

		result = new String[collector.size()][];
		for (int i = 0; i < collector.size(); i++) {
			String[] path = (String[]) collector.get(i);
			result[i] = path;
		}

		return result;

	} // getAllHierarchyPathsAsNames
	// ------------------------------------------------------------------------------

	/**
	 * total number of entities, usually ORFs.
	 */
	public int count() {
		return hash.size();
	}

	// ------------------------------------------------------------------------------
	/**
	 * total number of classifications. this will usually be larget than count
	 * (), since entities are frequently given two or more classifications,
	 * reflecting the multiple roles of many biological entities.
	 */
	public int size() {
		String[] names = getNames();
		int total = 0;
		for (int i = 0; i < names.length; i++) {
			total += ((Vector) hash.get(names[i])).size();
		}

		return total;

	} // size
	// ------------------------------------------------------------------------------

	/**
	 * get all of the full paths (as ints) from the ontology for all of the
	 * currently annotated entities. then traverse this (possibly large) list
	 * and return the longest path.
	 */
	public int maxDepth() {
		if (ontology == null)
			return 0;

		int[] classifications = getClassifications();
		int max = 0;

		for (int i = 0; i < classifications.length; i++) {
			int[][] paths = ontology.getAllHierarchyPaths(classifications[i]);
			for (int p = 0; p < paths.length; p++)
				if (paths[p].length > max)
					max = paths[p].length;
		} // for i

		return max;

	} // maxDepth
	// ------------------------------------------------------------------------------

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("annotation: ");
		sb.append(getCurator());
		sb.append(", ");
		sb.append(type);
		sb.append(", ");
		sb.append(species);
		sb.append(" (" + count() + " entities)  ");
		sb.append(" (" + size() + " classifications)  ");

		return sb.toString();

	} // toString
	// ------------------------------------------------------------------------------
} // class Annotation


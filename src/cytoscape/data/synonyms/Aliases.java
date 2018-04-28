package cytoscape.data.synonyms;

import giny.model.Edge;
import giny.model.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * 
 * Simpler version of Thesaurus.<br>
 * 
 * <p>
 * Purpose of this class is providing simple aliasing without biological
 * context. No species, commonname, nor canonical name are available. Just
 * maintains keys (IDs) and their aliases in CyAttributes.<br>
 * This will be used for importing attribute data.
 * </p>
 * 
 * <p>
 * This class has no redundant data structure: everything will be stored in
 * CyAttributes, and uses immutable special attributes called Alias
 * </p>
 * 
 * @since Cytoscape 2.4
 * @version 0.6
 * 
 * @author kono
 * 
 */
public class Aliases {

	/**
	 * Name of the attribute for aliases
	 */
	public static final String ALIAS = "alias";
	private final AliasType objectType;

	CyAttributes attributes;

	/**
	 * Constructor for the aliases object.<br>
	 * 
	 * @param type
	 *            Type of alias: node, edge, or network.
	 */
	public Aliases(AliasType type) {
		this.objectType = type;

		switch (objectType) {
		case NODE:
			attributes = Cytoscape.getNodeAttributes();
			break;
		case EDGE:
			attributes = Cytoscape.getEdgeAttributes();
			break;
		case NETWORK:
			attributes = Cytoscape.getNetworkAttributes();
			break;
		default:
			// This should not happen
			attributes = null;
		}
	}

	/**
	 * Add new alias for an object.
	 * 
	 * @param key
	 *            ID of the object.
	 * @param alias
	 *            New alias to be added.
	 */
	public void add(String key, String alias) {
		List<String> aliasList = attributes.getListAttribute(key, ALIAS);

		/*
		 * If there is no alias attributes, create new one.
		 */
		if (aliasList != null) {
			aliasList.add(alias);
			Set<String> aliasSet = new TreeSet<String>(aliasList);
			attributes.setListAttribute(key, ALIAS, new ArrayList<String>(
					aliasSet));
		} else {
			aliasList = new ArrayList<String>();
			aliasList.add(alias);
			attributes.setListAttribute(key, ALIAS, aliasList);
		}
	}

	/**
	 * Add list of aliases to the existing alias lists.<br>
	 * 
	 * @param key
	 * @param aliaseList
	 */
	public void add(String key, List<String> aliasList) {
		List<String> curAliasList = attributes.getListAttribute(key, ALIAS);

		/*
		 * If there is no alias attributes, add the given list as the new one.
		 */
		if (curAliasList != null) {
			curAliasList.addAll(aliasList);
		}
		
		/*
		 * Remove duplicates 
		 */
		Set<String> aliasSet = new TreeSet<String>(aliasList);
		attributes
				.setListAttribute(key, ALIAS, new ArrayList<String>(aliasSet));
	}

	/**
	 * Remove an alias.<br>
	 * 
	 * @param key
	 *            ID of the object.
	 * @param alias
	 *            Alias to be removed.
	 */
	public void remove(String key, String alias) {
		List<String> curAliasList = attributes.getListAttribute(key, ALIAS);

		/*
		 * Need to remove the alias only when alias attributes exist.
		 */
		if (curAliasList != null) {
			curAliasList.remove(alias);
			attributes.setListAttribute(key, ALIAS, curAliasList);
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public List<String> getAliases(String key) {
		return attributes.getListAttribute(key, ALIAS);
	}

	/**
	 * 
	 * Returns true if the object with the alias wxists in the memory
	 * (rootGraph).
	 * 
	 * This is an O(n) operation, which is expensive!
	 * 
	 * @param key
	 * @param alias
	 * @return Key for this alias. If it does not exist, return null.
	 */
	public String getKey(String alias) {

		final Iterator it;
		String id = null;

		switch (objectType) {
		case NODE:
			it = Cytoscape.getRootGraph().nodesIterator();
			while (it.hasNext()) {
				id = ((Node) it.next()).getIdentifier();
				final List aliases = attributes.getListAttribute(id, ALIAS);
				if (aliases != null && aliases.contains(alias)) {
					return id;
				}
			}
			break;
		case EDGE:
			it = Cytoscape.getRootGraph().edgesIterator();
			while (it.hasNext()) {
				id = ((Edge) it.next()).getIdentifier();
				final List aliases = attributes.getListAttribute(id, ALIAS);
				if (aliases != null && aliases.contains(alias)) {
					return id;
				}
			}
			break;
		case NETWORK:
			it = Cytoscape.getNetworkSet().iterator();
			while (it.hasNext()) {
				id = ((Node) it.next()).getIdentifier();
				final List aliases = attributes.getListAttribute(id, ALIAS);
				if (aliases != null && aliases.contains(alias)) {
					return id;
				}
			}
			break;
		default:
			// This should not happen
		}
		return null;
	}

	/**
	 * Return set of all names for the key (including key itself.)
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> getIdSet(String key) {
		List<String> curAliases = attributes.getListAttribute(key, ALIAS);
		Set<String> allNames = new TreeSet<String>();
		if (curAliases != null) {
			allNames.addAll(curAliases);
		}
		allNames.add(key);
		return allNames;
	}
}

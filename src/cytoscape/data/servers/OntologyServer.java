package cytoscape.data.servers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.data.ontology.DBCrossReferences;
import cytoscape.data.ontology.Ontology;
import cytoscape.data.ontology.OntologyFactory;
import cytoscape.data.readers.BookmarkReader;
import cytoscape.data.synonyms.AliasType;
import cytoscape.data.synonyms.Aliases;

/**
 * Ontology Server which manages ontologies, aliases, and bookmarks.<br>
 * 
 * @since Cytoscape 2.4
 * @version 0.6
 * 
 * @author kono
 * 
 */
public class OntologyServer implements PropertyChangeListener {

	public static enum OntologyType {
		BASIC, GO;
	}

	/**
	 * Map of Ontologies.
	 */
	private HashMap<String, Ontology> ontologies;

	/*
	 * Aliases
	 */
	private Aliases nodeAliases;
	private Aliases edgeAliases;
	private Aliases networkAliases;

	/*
	 * Factory to create actual Ontology objects.
	 */
	private OntologyFactory factory;

	/*
	 * Bookmarks. This can be used not only for ontology stuff, but also network
	 * file locations, attributes, etc.
	 */
	//private Bookmarks bookmarks;

	/*
	 * URLs of ontology data in memory
	 */
	private Map<String, URL> ontologySources;

	/*
	 * Database cross references.
	 */
	private DBCrossReferences xref;

	/**
	 * Constructor.<br>
	 * 
	 * Create hash map for ontologies.
	 * 
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws JAXBException
	 */
	public OntologyServer() throws IOException, JAXBException {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		
		factory = new OntologyFactory();
		this.ontologies = new HashMap<String, Ontology>();
		this.ontologySources = new HashMap<String, URL>();

		xref = new DBCrossReferences();
		xref.load();

		//loadBookmarks();

		nodeAliases = new Aliases(AliasType.NODE);
		edgeAliases = new Aliases(AliasType.EDGE);
		networkAliases = new Aliases(AliasType.NETWORK);
	}

//	public Bookmarks getBookmarks() {
//		return bookmarks;
//	}
//
//	public void loadBookmarks() throws JAXBException, IOException {
//		BookmarkReader reader = new BookmarkReader();
//		reader.readBookmarks();
//		bookmarks = reader.getBookmarks();
//	}

	public DBCrossReferences getCrossReferences() {
		return xref;
	}

	/**
	 * Getter of the property <tt>ontologies</tt>
	 * 
	 * @return Returns the ontologies.
	 * @uml.property name="ontologies"
	 */
	public HashMap<String, Ontology> getOntologies() {
		return ontologies;
	}

	public void addOntology(Ontology onto) {
		ontologies.put(onto.getName(), onto);
	}

	public void addOntology(URL dataSource, OntologyType type,
			String ontologyName, String description) throws IOException,
			URISyntaxException {
		Ontology onto;

		switch (type) {
		case BASIC:
			onto = factory.createBasicOntology(dataSource, ontologyName,
					description);
			break;
		case GO:
			onto = factory.createGeneOntology(dataSource, ontologyName,
					description);
			break;
		// case KEGG:
		// onto = factory.createKEGGOntology(dataSource, ontologyName);
		// break;
		default:
			onto = null;
		}
		ontologies.put(onto.getName(), onto);
		ontologySources.put(onto.getName(), dataSource);
	}

	public int ontologyCount() {
		return ontologies.size();
	}

	public void setOntology(Ontology onto) {
		ontologies.put(onto.getName(), onto);
	}

	public Set<String> getOntologyNames() {
		return ontologies.keySet();
	}

	public Map<String, URL> getOntologySources() {
		return ontologySources;
	}
	
	public void setOntologySources(Map<String, URL> newMap) {
		ontologySources = newMap;
	}

	public Aliases getNodeAliases() {
		return nodeAliases;
	}

	public Aliases getEdgeAliases() {
		return edgeAliases;
	}

	public Aliases getNetworkAliases() {
		return networkAliases;
	}

	public void propertyChange(PropertyChangeEvent e) {
		
		if(e.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
			/*
			 * Remove network name from ontology server.
			 */
			ontologies.remove(Cytoscape.getNetwork((String) e.getNewValue()).getTitle());
		}
		
	}
}

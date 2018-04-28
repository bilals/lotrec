package cytoscape.data.ontology.readers;

import static cytoscape.data.ontology.readers.OBOTags.BROAD_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.DEF;
import static cytoscape.data.ontology.readers.OBOTags.EXACT_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.ID;
import static cytoscape.data.ontology.readers.OBOTags.IS_A;
import static cytoscape.data.ontology.readers.OBOTags.IS_OBSOLETE;
import static cytoscape.data.ontology.readers.OBOTags.NARROW_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.RELATED_SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.RELATIONSHIP;
import static cytoscape.data.ontology.readers.OBOTags.SYNONYM;
import static cytoscape.data.ontology.readers.OBOTags.XREF;
import static cytoscape.data.ontology.readers.OBOTags.XREF_ANALOG;
import giny.model.Edge;
import giny.model.Node;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.ontology.Ontology;

/**
 * OBO file reader.<br>
 * 
 * <p>
 * This is a general OBO (Open Biomedical Ontologies:
 * http://obo.sourceforge.net/) flatfile reader.<br>
 * 
 * In Cytoscape, This will be used mainly for reading gene ontology. However, it
 * is compatible with all files written in OBO format.<br>
 * </p>
 * 
 * <p>
 * OBO files are available at:<br>
 * http://obo.sourceforge.net/cgi-bin/table.cgi
 * </p>
 * 
 * @since Cytoscape 2.4
 * @version 0.7
 * @author Keiichiro Ono
 * 
 */
public class OBOFlatFileReader implements OntologyReader {

	public static final String ONTOLOGY_DAG_ROOT = "Ontology DAGs";

	public static final String OBO_PREFIX = "ontology";

	private static final String DEF_ORIGIN = "def_origin";
	protected static final String TERM_TAG = "[Term]";
	private static final String DEF_ONTOLOGY_NAME = "Ontology DAG";

	private ArrayList<String[]> interactionList;
	private CyNetwork ontologyDAG;

	/*
	 * This is for attributes of nodes.
	 */
	private CyAttributes termAttributes;

	/*
	 * Attribute for the Ontology DAG.
	 */
	private CyAttributes networkAttributes;

	private Map<String, String> header;
	private String name;

	private InputStream inputStream;

	/**
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public OBOFlatFileReader(final String fileName, String name)
			throws FileNotFoundException {
		this(new FileInputStream(fileName), name);
	}

	/**
	 * 
	 * @param dataSource
	 * @throws IOException
	 */
	public OBOFlatFileReader(URL dataSource, String name) throws IOException {
		this(dataSource.openStream(), name);
	}

	/**
	 * 
	 * @param oboStream
	 * @param name
	 */
	public OBOFlatFileReader(InputStream oboStream, String name) {
		this.inputStream = oboStream;
		this.name = name;
		initialize();
	}

	private void initialize() {
		interactionList = new ArrayList<String[]>();
		header = new HashMap<String, String>();

		networkAttributes = Cytoscape.getNetworkAttributes();
		termAttributes = Cytoscape.getNodeAttributes();

		if (name == null) {
			name = DEF_ONTOLOGY_NAME;
		}

		/*
		 * Ontology DAGs will be distinguished by this attribute.
		 */
		networkAttributes.setAttribute(name, Ontology.IS_ONTOLOGY, true);
		networkAttributes.setUserVisible(Ontology.IS_ONTOLOGY, false);
		networkAttributes.setUserEditable(Ontology.IS_ONTOLOGY, false);

		String rootID = Cytoscape.getOntologyRootID();
		if (rootID == null) {

			Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
			for (CyNetwork net : networkSet) {
				if (net.getTitle().equals(ONTOLOGY_DAG_ROOT)) {
					rootID = net.getIdentifier();
				}
			}

			if (rootID == null) {
				rootID = Cytoscape.createNetwork(ONTOLOGY_DAG_ROOT, false)
						.getIdentifier();
				Cytoscape.setOntologyRootID(rootID);
			}
		}
		ontologyDAG = Cytoscape.createNetwork(name, Cytoscape
				.getNetwork(rootID), false);
	}

	/**
	 * @throws IOException
	 */
	public void readOntology() throws IOException {

		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				inputStream));
		String line;

		while ((line = bufRd.readLine()) != null) {
			// Read header
			if (line.startsWith(TERM_TAG)) {
				readEntry(bufRd);
				break;
			} else if (line.length() != 0) {
				final int colonInx = line.indexOf(':');
				final String key = line.substring(0, colonInx).trim();
				final String val = line.substring(colonInx + 1).trim();
				header.put(key, val);
			}
		}

		while ((line = bufRd.readLine()) != null) {
			// Read header
			if (line.startsWith(TERM_TAG)) {
				readEntry(bufRd);
			}
		}

		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException ioe) {
		} finally {
			inputStream = null;
		}

		buildDag();
		setAttributeDescriptions();
	}

	/**
	 * Read one Ontology Term
	 * 
	 * @param rd
	 * @throws IOException
	 */
	private void readEntry(final BufferedReader rd) throws IOException {
		String id = "";
		String line = null;

		while (true) {

			line = rd.readLine().trim();
			if (line.length() == 0)
				break;
			final int colonInx = line.indexOf(':');
			final String key = line.substring(0, colonInx).trim();
			final String val = line.substring(colonInx + 1).trim();
			Node source = null;

			if (key.equals(ID.toString())) {
				// There's only one id.
				id = val;
			} else if (key.equals(DEF.toString())) {
				// System.out.println("DEF: " + id + " = " + val);
				String[] definitionParts = val.split("\"");
				termAttributes.setAttribute(id, OBO_PREFIX + "." + key,
						definitionParts[1]);
				List<String> originList = getReferences(val
						.substring(definitionParts[1].length() + 2));
				if (originList != null) {
					termAttributes.setListAttribute(id, OBO_PREFIX + "."
							+ DEF_ORIGIN, originList);
				}
			} else if (key.equals(EXACT_SYNONYM.toString())
					|| key.equals(RELATED_SYNONYM.toString())
					|| key.equals(BROAD_SYNONYM.toString())
					|| key.equals(NARROW_SYNONYM.toString())
					|| key.equals(SYNONYM.toString())) {

				String[] synonymParts = val.split("\"");
				Map<String, String> synoMap = termAttributes.getMapAttribute(
						id, OBO_PREFIX + "." + OBOTags.SYNONYM.toString());

				if (synoMap == null) {
					synoMap = new HashMap<String, String>();
				}

				if (key.equals(SYNONYM.toString())) {
					synoMap.put(synonymParts[1], synonymParts[2].trim());
				} else {
					synoMap.put(synonymParts[1], key);
				}

				termAttributes.setMapAttribute(id, OBO_PREFIX + "."
						+ OBOTags.SYNONYM.toString(), synoMap);

			} else if (key.equals(RELATIONSHIP.toString())) {

				if (source == null) {
					source = Cytoscape.getCyNode(id, true);
					ontologyDAG.addNode(source);
				}
				String[] entry = val.split(" ");

				final String[] itr = new String[3];
				itr[0] = id;
				itr[1] = entry[1];
				itr[2] = entry[0];
				interactionList.add(itr);
			} else if (key.equals(IS_A.toString())) {
				/*
				 * This is the keyword to create an edge. IS_A relationship
				 * means current node is the source, and target is the one
				 * written here.
				 */
				final Node target;
				if (source == null) {
					source = Cytoscape.getCyNode(id, true);
					ontologyDAG.addNode(source);
				}
				int colonidx = val.indexOf('!');
				final String targetId;
				if (colonidx == -1) {
					// GO Slim.
					targetId = val.trim();
				} else {
					targetId = val.substring(0, colonidx).trim();
				}
				target = Cytoscape.getCyNode(targetId, true);
				ontologyDAG.addNode(target);

				final String[] itr = new String[3];
				itr[0] = id;
				itr[1] = targetId;
				itr[2] = "is_a";
				interactionList.add(itr);

			} else if (key.equals(IS_OBSOLETE.toString())) {
				termAttributes.setAttribute(id, OBO_PREFIX + "." + key, Boolean
						.parseBoolean(val));
			} else if (key.equals(XREF.toString())
					|| key.equals(XREF_ANALOG.toString())) {
				List xrefAnalog = termAttributes.getListAttribute(id,
						OBO_PREFIX + "." + XREF.toString());
				if (xrefAnalog == null) {
					xrefAnalog = new ArrayList<String>();
				}
				if (val != null) {
					xrefAnalog.add(val.toString());
				}
				termAttributes.setListAttribute(id, OBO_PREFIX + "."
						+ XREF.toString(), xrefAnalog);
			} else {
				termAttributes.setAttribute(id, OBO_PREFIX + "." + key, val);
			}
		}
	}

	public Map<String, String> getHeader() {
		return header;
	}

	private void buildDag() {
		Iterator<String[]> it = interactionList.iterator();
		while (it.hasNext()) {
			String[] interaction = it.next();

			Edge isA = Cytoscape.getCyEdge(Cytoscape.getCyNode(interaction[0],
					true), Cytoscape.getCyNode(interaction[1], true),
					Semantics.INTERACTION, interaction[2], true, true);
			ontologyDAG.addEdge(isA);
		}
	}

	public CyAttributes getTermsAttributes() {
		return termAttributes;
	}

	public CyNetwork getDag() {
		return ontologyDAG;
	}

	private List<String> getReferences(String list) {
		String trimed = list.trim();
		trimed = trimed.substring(trimed.indexOf("[") + 1, trimed.indexOf("]"));
		if (trimed.length() == 0) {
			return null;
		} else {
			List<String> entries = new ArrayList<String>();
			for (String entry : trimed.split(",")) {
				entries.add(entry.trim());
			}
			return entries;
		}
	}

	private void setAttributeDescriptions() {
		String[] attrNames = termAttributes.getAttributeNames();
		Set<String> attrNameSet = new TreeSet<String>();
		for (String name : attrNames) {
			attrNameSet.add(name);
		}

		for (OBOTags tags : OBOTags.values()) {
			if (attrNameSet.contains(OBOTags.getPrefix() + "."
					+ tags.toString())) {
				termAttributes.setAttributeDescription(OBOTags.getPrefix()
						+ "." + tags.toString(), tags.getDescription());
			}
		}
	}
}

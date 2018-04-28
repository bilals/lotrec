package cytoscape.data.ontology.readers;

import static cytoscape.data.ontology.readers.DBXrefKeywords.ABBREVIATION;
import static cytoscape.data.ontology.readers.DBXrefKeywords.DATABASE;
import static cytoscape.data.ontology.readers.DBXrefKeywords.GENERIC_URL;
import static cytoscape.data.ontology.readers.DBXrefKeywords.OBJECT;
import static cytoscape.data.ontology.readers.DBXrefKeywords.SYNONYM;
import static cytoscape.data.ontology.readers.DBXrefKeywords.URL_SYNTAX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.data.ontology.DBReference;

/**
 * Reader for database cross reference file.<br>
 * 
 * <p>
 * The resource file location is hard-coded.  This will be in the cytoscape.jar.
 * </p>
 * 
 * @version 0.9
 * @since Cytoscape 2.4
 * @author kono
 *
 */
public class DBCrossReferenceReader {

	private Map<String, DBReference> xref;

	/*
	 * Resource file for DBXref.
	 * 
	 * This is in the jar file.
	 */
	private static final String DBXREF_RESOURCE_FILE = "/cytoscape/resources/GO.xrf_abbs";

	/**
	 * Constructor.<br>
	 * 
	 * Create a new map for the DB references.
	 *
	 */
	public DBCrossReferenceReader() {
		xref = new HashMap<String, DBReference>();
	}

	/**
	 * Read the resource file in the jar.
	 * 
	 * @throws IOException
	 */
	public void readResourceFile() throws IOException {
		URL resource = getClass().getResource(DBXREF_RESOURCE_FILE);
		BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				resource.openStream()));
		String line;

		while ((line = bufRd.readLine()) != null) {
			// Read header
			if (line.startsWith(ABBREVIATION.toString())) {
				int colonInx = line.indexOf(':');
				String abb = line.substring(colonInx + 1).trim();
				readEntry(abb, bufRd);
			}
		}
		try {
			if (bufRd != null) {
				bufRd.close();
			}
		} catch (IOException ioe) {
		} finally {
			bufRd = null;
		}
	}

	/**
	 * Extract an entry.
	 * 
	 * @param abbreviation
	 * @param rd
	 * @throws IOException
	 */
	private void readEntry(final String abbreviation, final BufferedReader rd) throws IOException {

		String dbName = null;
		String urlSyntax = null;
		String genericUrl = null;
		String object = null;
		List<String> synonyms = new ArrayList<String>();

		while (true) // Parse until blank line.
		{
			final String line = rd.readLine();
			if(line == null) {
				break;
			}
			
			if (line.trim().length() == 0)
				break;
			final int colonInx = line.indexOf(':');
			final String key = line.substring(0, colonInx).trim();
			final String val = line.substring(colonInx + 1).trim();
			
			if (key.equals(SYNONYM.toString())) {
				synonyms.add(val);
			} else if (key.equals(DATABASE.toString())) {
				dbName = val;
			} else if (key.equals(GENERIC_URL.toString())) {
				genericUrl = val;
			} else if (key.equals(URL_SYNTAX.toString())) {
				urlSyntax = val;
			} else if (key.equals(OBJECT.toString())) {
				object = val;
			}
		}

		/*
		 * Create a new DBReference.
		 */
		DBReference ref = new DBReference(abbreviation, dbName, urlSyntax,
				genericUrl, object);
		if (synonyms.size() != 0) {
			ref.setSynonym(synonyms);
		}
		xref.put(abbreviation, ref);
	}

	
	/**
	 * Get the references as a map.
	 * 
	 * @return
	 */
	public Map<String, DBReference> getXrefMap() {
		return xref;
	}
}

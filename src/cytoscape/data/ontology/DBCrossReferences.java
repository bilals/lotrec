package cytoscape.data.ontology;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cytoscape.data.ontology.readers.DBCrossReferenceReader;

/**
 * Database cress reference.<br>
 * <p>
 * This class manages the relationships between databases. The cross reference
 * file is available at:<br>
 * 
 * http://www.geneontology.org/doc/GO.xrf_abbs
 * 
 * </p>
 * 
 * @version 0.9
 * @since Cytoscape 2.4
 * @author kono
 * 
 */
public class DBCrossReferences {

	/*
	 * Map to store the cross reference.
	 */
	private Map<String, DBReference> crossRefMap;

	public DBCrossReferences() {
		this.crossRefMap = new HashMap<String, DBReference>();
	}

	public void load() throws IOException {
		DBCrossReferenceReader xrefReader = new DBCrossReferenceReader();
		xrefReader.readResourceFile();
		this.crossRefMap = xrefReader.getXrefMap();
	}
	/**
	 * Add a database reference object
	 * 
	 * @param db
	 */
	public void setDBReference(DBReference db) {
		crossRefMap.put(db.getAbbreviation(), db);
	}

	public Set getDBNames() {
		return crossRefMap.keySet();
	}
	
	public DBReference getDBReference(String abbreviation) {
		return crossRefMap.get(abbreviation);
	}
}

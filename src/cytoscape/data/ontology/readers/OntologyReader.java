package cytoscape.data.ontology.readers;

import java.io.IOException;
import java.util.Map;

import cytoscape.CyNetwork;

/**
 * Interface for all ontology file readers.
 * 
 * Basic function of the readers are build ontology DAG/Tree and its header.
 * 
 * Actual data steructure is always CyNetwork and CyAttributes.
 * 
 * @author kono
 * 
 */
public interface OntologyReader {

	public void readOntology() throws IOException;

	public CyNetwork getDag();

	public Map getHeader();

}

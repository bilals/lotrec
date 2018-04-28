package cytoscape.data.ontology;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import cytoscape.data.ontology.readers.OBOFlatFileReader;
import static cytoscape.data.ontology.readers.OBOHeaderTags.*;
import cytoscape.data.readers.MetadataEntries;
import cytoscape.data.readers.MetadataParser;

public class OntologyFactory {

	private MetadataParser mdp;

	public Ontology createBasicOntology(URL dataSource, String name,
			String description) throws IOException, URISyntaxException {
		OBOFlatFileReader reader = new OBOFlatFileReader(dataSource, name);
		reader.readOntology();

		Ontology onto = new Ontology(name, "General Ontology", description,
				reader.getDag());

		Map header = reader.getHeader();
		if (header != null && header.get(DATE.toString()) != null) {
			mdp = new MetadataParser(reader.getDag());
			
			mdp.setMetadata(MetadataEntries.DATE, header.get(
					DATE.toString()).toString());
		}
		return onto;
	}

	public GeneOntology createGeneOntology(URL dataSource, String name,
			String description) throws IOException, URISyntaxException {
		OBOFlatFileReader reader = new OBOFlatFileReader(dataSource, name);
		reader.readOntology();

		GeneOntology go = new GeneOntology(name, "GO", description, reader
				.getDag());
		Map header = reader.getHeader();

		mdp = new MetadataParser(reader.getDag());
		mdp.setMetadata(MetadataEntries.DATE, header.get(
				DATE.toString()).toString());
		return go;
	}

	// public KEGGOntology createKEGGOntology(URL dataSource, String name) {
	// KEGGOntology kegg = null;
	// return kegg;
	// }

}

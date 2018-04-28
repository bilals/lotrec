package cytoscape.data.ontology;

import static cytoscape.data.ontology.readers.OBOTags.DEF;
import static cytoscape.data.ontology.readers.OBOTags.NAME;
import static cytoscape.data.ontology.readers.OBOTags.NAMESPACE;
import static cytoscape.data.readers.MetadataEntries.SOURCE;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.biojava.ontology.Term;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.ontology.readers.OBOTags;

/**
 * Gene Ontology object based on general Ontology.<br>
 * 
 * 
 * @author kono
 * 
 */
public class GeneOntology extends Ontology {

	private CyAttributes goTermAttributes = Cytoscape.getNodeAttributes();

	public enum GOAspect {
		BIOLOGICAL_PROCESS("P"), CELLULAR_COMPONENT("C"), MOLECULAR_FUNCTION(
				"F");

		private String aspect;

		private GOAspect(String aspect) {
			this.aspect = aspect;
		}

		public String toString() {
			return aspect;
		}

	}

	public GeneOntology(String name, String curator, String description,
			CyNetwork dag) throws URISyntaxException, MalformedURLException {
		super(name, curator, description, dag);
		final DBReference reference = Cytoscape.getOntologyServer()
				.getCrossReferences().getDBReference("GOC");
		metaParser.setMetadata(SOURCE, reference.getGenericURL().toString());
	}

	/**
	 * 
	 * @return Curator as string
	 */
	public String getCurator() {
		return null;
	}

	public List<Term> getTermsInNamespace(String namespace) {
		return null;
	}

	public GOTerm getGOTerm(String goID) {
		return new GOTerm(goID, Cytoscape.getNodeAttributes()
				.getStringAttribute(goID, OBOTags.getPrefix() + "." + NAME.toString()), name,
				goTermAttributes.getStringAttribute(goID, OBOTags.getPrefix() + "." + DEF.toString()));
	}

	/**
	 * Returns Aspect/name space of the GO term.
	 * 
	 * @param goID
	 *            ID of the GO term (for example GO:000011)
	 * 
	 * @return GOAspect of the given ID
	 */
	public GOAspect getAspect(String goID) {
		final String nameSpace = goTermAttributes.getStringAttribute(goID, OBOTags.getPrefix() + "." + NAMESPACE.toString());

		if(nameSpace == null) {
			return null;
		}
		
		if(nameSpace.equalsIgnoreCase(GOAspect.BIOLOGICAL_PROCESS.name())) {
			return GOAspect.BIOLOGICAL_PROCESS;
		} else if(nameSpace.equalsIgnoreCase(GOAspect.CELLULAR_COMPONENT.name())) {
			return GOAspect.CELLULAR_COMPONENT;
		} else if(nameSpace.equalsIgnoreCase(GOAspect.MOLECULAR_FUNCTION.name())) {
			return GOAspect.MOLECULAR_FUNCTION;
		}

		return null;
	}
}

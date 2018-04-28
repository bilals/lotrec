package cytoscape.data.ontology;

import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.ontology.readers.OBOTags;

/**
 * A Gene Ontology term. This class is an extended version of normal ontology.
 * 
 * @author kono
 * 
 */
public class GOTerm extends OntologyTerm {

	public GOTerm(final String id, final String termName, final String ontologyName,
			final String description) {
		super(id, ontologyName, description);
		if (termName != null) {
			Cytoscape.getNodeAttributes().setAttribute(id,
					OBOTags.getPrefix() + "." + OBOTags.NAME.toString(), termName);
		}
	}

	public String getNameSpace() {
		return null;
	}

	public Map getCrossReferences() {
		return null;
	}

	public String getFullName() {
		return Cytoscape.getNodeAttributes().getStringAttribute(
				super.getName(), OBOTags.getPrefix() + "." + OBOTags.NAME.toString());
	}

	public String getDescription() {
		return Cytoscape.getNodeAttributes().getStringAttribute(
				super.getName(), OBOTags.getPrefix() + "." + OBOTags.DEF.toString());
	}

	public String getType() {
		return Cytoscape.getNodeAttributes().getStringAttribute(
				super.getName(), OBOTags.getPrefix() + "." + OBOTags.NAMESPACE.toString());
	}

}

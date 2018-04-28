package cytoscape.data.ontology.readers;

public enum OBOHeaderTags {
	FORMAT_VERSION, TYPEREF, VERSION, DATE, SAVED_BY, 
	AUTO_GENERATED_BY, DEFAULT_NAMESPACE, REMARK, SUBSETDEF;
	
	public String toString() {
		String name = name().toLowerCase();
		return name.replace('_', '-');
	}
}

package cytoscape.data.ontology.readers;

public enum DBXrefKeywords {
	ABBREVIATION("abbreviation"), DATABASE("database"), 
	GENERIC_URL("generic_url"), URL_SYNTAX("url_syntax"), 
	URL_EXAMPLE("url_example"), OBJECT("object"), 
	EXAMPLE_ID("example_id"), SYNONYM("synonym");

	private String keyword;

	private DBXrefKeywords(String keyword) {
		this.keyword = keyword;
	}

	public String toString() {
		return keyword;
	}
}
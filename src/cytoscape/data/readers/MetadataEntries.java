package cytoscape.data.readers;


/**
 * Entries in the network metadata.<br>
 * 
 * This is the list of entries in the network metadata object.
 * 
 * @author kono
 *
 */
public enum MetadataEntries {
	DATE("Date"), TITLE("Title"), IDENTIFIER("Identifier"), 
	DESCRIPTION("Description"), SOURCE("Source"), TYPE("Type"), 
	FORMAT("Format");

	private String name;
	
	private MetadataEntries(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}

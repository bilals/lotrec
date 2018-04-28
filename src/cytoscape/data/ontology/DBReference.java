package cytoscape.data.ontology;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an entry in the database cross reference.<br>
 * 
 * @author kono
 * 
 */
public class DBReference {

	private String abbreviation;
	private String name;
	private String urlSyntax;
	private String genericUrl;
	private String object;
	private List<String> synonyms;

	public DBReference(String abbreviation, String name, String genericUrl) {
		this(abbreviation, name, null, genericUrl, null);
	}

	public DBReference(String abbreviation, String name, String urlSyntax,
			String genericUrl, String object) {
		this.abbreviation = abbreviation;
		this.name = name;
		this.urlSyntax = urlSyntax;
		this.genericUrl = genericUrl;
		this.synonyms = null;
		this.object = object;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public String getFullName() {
		return name;
	}

	public URL getGenericURL() throws MalformedURLException {
		return new URL(genericUrl);
	}
	
	public String getObject() {
		return object;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public URL getQueryURL(String entry) throws MalformedURLException {
		final String queryURL = urlSyntax + entry;
		return new URL(queryURL);
	}

	public void setSynonym(List<String> synonym) {
		this.synonyms = synonym;
	}

}

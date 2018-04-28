package cytoscape.data.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMap;

/**
 * CyAttributeWriter extracted from AttributeSaverDialog.
 * 
 */
public class CyAttributesWriter2 {

	public static String newline = System.getProperty("line.separator");

	private final CyAttributes cyAttributes;
	private final String attributeName;
	private final Writer fileWriter;

	public CyAttributesWriter2(final CyAttributes attributes,
			final String attributeName, final Writer fileWriter) {
		this.cyAttributes = attributes;
		this.attributeName = attributeName;
		this.fileWriter = fileWriter;
	}

	/**
	 * Write out the state for the given attributes
	 * 
	 * @param selectedRows
	 * 
	 * @return number of files successfully saved, the better way to do this
	 *         would just be to throw the error and display a specific message
	 *         for each failure, but oh well.
	 * @throws IOException
	 * 
	 */
	public void writeAttributes() throws IOException {

		final MultiHashMap attributeMap = cyAttributes.getMultiHashMap();
		if (attributeMap != null) {
			final Iterator keys = cyAttributes.getMultiHashMap().getObjectKeys(
					attributeName);
			while (keys.hasNext()) {
				final String key = (String) keys.next();
				Object value = attributeMap.getAttributeValue(key,
						attributeName, null);

				if (value != null) {
					if (value instanceof Collection) {
						String result = key + " = ";
						Collection collection = (Collection) value;
						if (collection.size() > 0) {
							Iterator objIt = collection.iterator();
							result += "(" + objIt.next();
							while (objIt.hasNext()) {
								result += "::" + objIt.next();
							}
							result += ")" + newline;
							fileWriter.write(result);
						}
					} else {
						fileWriter.write(key + " = " + value + newline);
					}
				}
			}
			fileWriter.flush();
		}
	}

}

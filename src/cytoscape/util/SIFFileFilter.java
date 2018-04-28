package cytoscape.util;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;

/**
 * FileFilter for Reading in Cytoscape SIF Files.
 *
 * @author Cytoscape Development Team.
 */
public class SIFFileFilter extends CyFileFilter {
    /**
     * SIF Files are Graphs.
     */
    private static String fileNature = ImportHandler.GRAPH_NATURE;

    /**
     * File Extension.
     */
    private static String fileExtension = "sif";

    /**
     * Filter Description.
     */
    private static String description = "SIF files";

    /**
     * Constructor.
     */
    public SIFFileFilter() {
        super(fileExtension, description, fileNature);
    }

    /**
     * Gets Graph Reader.
     * @param fileName File name.
     * @return GraphReader Object.
     */
    public GraphReader getReader(String fileName) {
        reader = new InteractionsReader(fileName);
        return reader;
    }
}

package cytoscape.util;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;

/**
 * FileFilter for Reading in GML Files.
 *
 * @author Cytoscape Development Team.
 */
public class GMLFileFilter extends CyFileFilter {

    /**
     * XGMML Files are Graphs.
     */
    private static String fileNature = ImportHandler.GRAPH_NATURE;

    /**
     * File Extensions.
     */
    private static String fileExtension = "gml";

    /**
     * Filter Description.
     */
    private static String description = "GML files";

    /**
     * Constructor.
     */
    public GMLFileFilter() {
        super(fileExtension, description, fileNature);
    }

    /**
     * Gets GraphReader.
     * @param fileName File Name.
     * @return GraphReader Object.
     */
    public GraphReader getReader(String fileName) {
        reader = new GMLReader(fileName);
        return reader;
    }
}
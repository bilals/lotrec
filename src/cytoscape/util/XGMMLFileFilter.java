package cytoscape.util;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.XGMMLReader;

import java.io.File;
import java.io.IOException;

/**
 * FileFilter for Reading in XGMML Files.
 *
 * @author Cytoscape Development Team.
 */
public class XGMMLFileFilter extends CyFileFilter {

    /**
     * XGMML Files are Graphs.
     */
    private static String fileNature = ImportHandler.GRAPH_NATURE;

    /**
     * File Extensions.
     */
    private static String[] fileExtensions = {"xgmml", "xml"};

    /**
     * Filter Description.
     */
    private static String description = "XGMML files";

    /**
     * Constructor.
     */
    public XGMMLFileFilter() {
        super(fileExtensions, description, fileNature);
    }

    /**
     * Gets Graph Reader.
     * @param fileName File name.
     * @return GraphReader Object.
     */
    public GraphReader getReader(String fileName) {
        reader = new XGMMLReader(fileName);
        return reader;
    }

    /**
     * Indicates which files the XGMMLFileFilter accepts.
     *
     * This method will return true only if:
     * <UL>
     * <LI>File ends in .xml or .xgmml;  and
     * <LI>File headers includes the www.cs.rpi.edu/XGMML namespace declaration.
     * </UL>
     *
     * @param file File
     * @return true or false.
     */
    public boolean accept(File file) {
        String fileName = file.getName();
        boolean firstPass = false;
        //  First test:  file must end with one of the registered file extensions.
        for (int i=0; i<fileExtensions.length; i++) {
            if (fileName.endsWith(fileExtensions[i])) {
                firstPass = true;
            }
        }
        if (firstPass) {
            //  Second test:  file header must contain the xgmml declaration
            try {
                String header = getHeader(file).toLowerCase();
                if (header.indexOf("www.cs.rpi.edu/xgmml") > 0) {
                    return true;
                }
            } catch (IOException e) {
            }
        }
        return false;
    }
}

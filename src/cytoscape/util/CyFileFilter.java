/*
  File: CyFileFilter.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/


package cytoscape.util;

import cytoscape.data.readers.GraphReader;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;


/**
 * A convenience implementation of FileFilter that filters out
 * all files except for those type extensions that it knows about.
 * <p/>
 * Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macinthosh. Case is ignored.
 *
 * @author Larissa Kamenkovich
 * @author Brad Kohlenberg
 */
public class CyFileFilter
        extends
        FileFilter
        implements
        FilenameFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";

    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    protected GraphReader reader = null;
    protected String fileNature = "UNKNOWN";

    /**
     * Creates a file filter. If no filters are added, then all
     * files are accepted.
     *
     * @see #addExtension
     */
    public CyFileFilter() {
        this.filters = new Hashtable();
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new ExampleFileFilter("jpg");
     *
     * @see #addExtension
     */
    public CyFileFilter(String extension) {
        this(extension, null, null);
    }

    /**
     * Creates a file filter that accepts the given file type.
     * Example: new ExampleFileFilter("jpg", "JPEG Image Images");
     * <p/>
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public CyFileFilter(String extension, String description) {
        this(extension, description, null);
    }

    /**
     * Creates a file filter from the given string array.
     * Example: new ExampleFileFilter(String {"gif", "jpg"});
     * <p/>
     * Note that the "." before the extension is not needed adn
     * will be ignored.
     *
     * @see #addExtension
     */
    public CyFileFilter(String[] filters) {
        this(filters, null, null);
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
     * <p/>
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @see #addExtension
     */
    public CyFileFilter(String[] filters, String description) {
        this(filters, description, null);
    }

    /**
     * Creates a file filter that accepts the given file type.
     * Example: new ExampleFileFilter("jpg", "JPEG Image Images");
     * <p/>
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public CyFileFilter(String extension, String description, String nature) {
        this.filters = new Hashtable();
        if (extension != null) {
            addExtension(extension);
        }
        if (description != null) {
            setDescription(description);
        }
        if (nature != null) {
            setFileNature(nature);
        }
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
     * <p/>
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @see #addExtension
     */
    public CyFileFilter(String[] filters, String description, String nature) {
        this.filters = new Hashtable();
        for (int i = 0; i < filters.length; i++) {
            // add filters one by one
            addExtension(filters[i]);
        }
        if (description != null) {
            setDescription(description);
        }
        if (nature != null) {
            setFileNature(nature);
        }
    }


    /**
     * Returns true if this class is capable of processing the specified file.
     *
     * @param f File
     */
    public boolean accept(File f) {
        if (f != null) {
            //  If there are no filters, always accept
            if (filters.size() == 0) {
                return true;
            }
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && filters.get(extension) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this class is capable of processing the specified file.
     *
     * @param dir       Directory.
     * @param fileName  File name.
     *
     */
    public boolean accept(File dir, String fileName) {
        return accept(new File(fileName));
    }

    /**
     * Returns true if this class is capable of processing the specified file.
     *
     * @param fileName  File name.
     */
    public boolean accept(String fileName) {
        return accept(new File(fileName));
    }

    /**
     * Return the extension portion of the file's name.
     *
     * @see #getExtension
     * @see FileFilter#accept
     */
    public String getExtension(File f) {
        if (f != null) {
            return getExtension(f.getName());
        } else {
            return null;
        }
    }

    public String getExtension(String filename) {
        if (filename != null) {
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
            ;
        }
        return null;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     * <p/>
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".jpg" and ".tif":
     * <p/>
     * ExampleFileFilter filter = new ExampleFileFilter();
     * filter.addExtension("jpg");
     * filter.addExtension("tif");
     * <p/>
     * Note that the "." before the extension is not needed and will be ignored.
     */
    public void addExtension(String extension) {
        if (filters == null) {
            filters = new Hashtable(5);
        }
        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }


    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     * @see FileFilter#getDescription
     */
    public String getDescription() {
        if (fullDescription == null) {
            if (description == null || isExtensionListInDescription()) {
                fullDescription = description == null ? "(" : description + " (";
                // build the description from the extension list
                Enumeration extensions = filters.keys();
                if (extensions != null) {
                    fullDescription += "*." + (String) (extensions.hasMoreElements() ? extensions.nextElement() : "*");
                    while (extensions.hasMoreElements()) {
                        fullDescription += ", *." + (String) extensions.nextElement();
                    }
                }
                fullDescription += ")";
            } else {
                fullDescription = description;
            }
        }
        return fullDescription;
    }

    /**
     * Sets the human readable description of this filter. For
     * example: filter.setDescription("Gif and JPG Images");
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     */
    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     * <p/>
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see isExtensionListInDescription
     */
    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     * <p/>
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see getDescription
     * @see setDescription
     * @see setExtensionListInDescription
     */
    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }

    /**
     * Returns the Set of file extension names.
     */
    public Set getExtensionSet() {
        return filters.keySet();
    }

    //
    // The following code is an extension of the CyFileFilter duties.
    // It provides extra functionality that allows a reader to be
    // associated with a file type, which allows us to disassociate
    // the checking of file type and file loading.  Simply ask the
    // file filter for the reader and you'll automatically get the
    // correct one.
    // 

    /**
     * Returns the reader.  This should be overridden by file type subclasses.
     */
    public GraphReader getReader(String fileName) {
        return null;
    }

    /**
     * Returns the nature of the file.  "Nature" refers to a grouping
     * of file types.  For instance, GML, XGMML, and SIF are all file formats
     * that contain graphs, therefore they belong to the GRAPH_NATURE.  This
     * allows the ImportHandler to return all file types with the same nature.
     */
    public String getFileNature() {
        if (fileNature == null) {
            return null;
        }
        return fileNature;
    }

    /**
     * Sets the nature of the files for this filter.
     * The files can be of the nature: Node, Edge, Graph, or Vizmap;
     *
     * @see setDescription
     * @see setExtensionListInDescription
     * @see isExtensionListInDescription
     */
    public void setFileNature(String nature) {
        fileNature = nature;
    }

    /**
     * Gets header of specified file.
     */
    protected String getHeader(File file) throws IOException {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader (file);
            bufferedReader = new BufferedReader (reader);
            String line = bufferedReader.readLine();
            StringBuffer header = new StringBuffer();
            int numLines = 0;
            while (line != null && numLines < 20) {
                header.append(line + "\n");
                line = bufferedReader.readLine();
                numLines++;
            }
            return header.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}
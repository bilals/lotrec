
/*
  File: PreferenceTableModel.java 
  
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

package cytoscape.dialogs.preferences;

import cytoscape.*;

import java.net.URL;
import java.io.*;
import javax.swing.table.*;
import javax.swing.*;
import java.util.*;

public class PreferenceTableModel extends AbstractTableModel {
    static int[] columnWidth = new int[] {150, 250};
    static int[] alignment = new int[] {JLabel.LEFT, JLabel.LEFT};

    private Properties properties;
    Vector propertiesList = new Vector();
    static String[] columnHeader = new String[] {
        "Property Name", "Value"};

    public PreferenceTableModel() {
        super();
	// use clone of CytoscapeInit properties
        properties = (Properties)(CytoscapeInit.getProperties().clone());
        loadProperties();
    }
    

    public void loadProperties() {
	    clearVector();
	    for (Enumeration names = properties.propertyNames();
					names.hasMoreElements();) {
		String name = (String)names.nextElement();
		addProperty(new String[] {name,properties.getProperty(name)});
	    }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
	// update property object
        properties.setProperty(key,value);
	// update table model (propertiesList)
	for (Iterator it = propertiesList.iterator(); it.hasNext(); ) {
	    String[] prop = (String[])it.next();
	    if (prop[0].equals(key)) {
		prop[1] = value;
	    }
	}
    }

    public void deleteProperty(String key) {
	// remove property from property object
        properties.remove(key);
	// remove property from table model (propertiesList)
	for (Iterator it = propertiesList.iterator(); it.hasNext(); ) {
	    String[] prop = (String[])it.next();
	    if (prop[0].equals(key)) {
		propertiesList.remove(prop);
		return;
	    }
	}
    }

    public void addProperty(String[] val) {
        if (val.length < 0 || val.length > columnHeader.length) return;

	// add to table model (propertiesList vector) if not present,
	// otherwise replace existing entry
	boolean found = false;
	for (Iterator it = propertiesList.iterator(); it.hasNext(); ) {
	    String[] prop = (String[])it.next();
	    if (prop[0].equals(val[0])) {
		prop[1] = val[1];
		found = true;
	    }
	}
	if (!found)
	    propertiesList.add(val);

        sort();
	// also add to local properties object for saving 
        properties.setProperty(val[0],val[1]);
    }

    public String getColumnName(int col) { return columnHeader[col]; }
    
    public void clearVector() {
        propertiesList.clear();
    }

    public void save(Properties saveToProps) {
	// save local property values to passed-in Properties
	saveToProps.putAll(properties);
    }

    public void restore(Properties restoreFromProps) {
	properties.clear();
	properties.putAll(restoreFromProps);
	loadProperties();
    }


    public int getColumnCount() {
        return columnHeader.length;
    }

    public Object getValueAt(int row, int col) {
        String[] rowData = (String[])propertiesList.get(row);
        return rowData[col];
    }

    public int getRowCount() {
        return propertiesList.size();
    }

    public void sort() {
        Collections.sort(propertiesList, new StringComparator());
    }
}

class StringComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        int result = 0;

        String[] str1 = ((String[])o1);
        String[] str2 = ((String[])o2);
        
        for (int i=0;i<str1.length;i++) {
            result = str1[i].compareTo(str2[i]);

            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
    

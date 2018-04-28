/*
 File: OntologyFlatFileReader.java 
 
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

// OntologyFlatFileReader.java

// ------------------------------------------------------------------------------
// $Revision: 7760 $ $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// ------------------------------------------------------------------------------
package cytoscape.data.annotation.readers;

// ------------------------------------------------------------------------------
import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;

import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.OntologyTerm;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextHttpReader;
import cytoscape.data.readers.TextJarReader;

// -------------------------------------------------------------------------
public class OntologyFlatFileReader {
	Ontology ontology;

	String curator = "unknown";

	String ontologyType = "unknown";

	String filename;

	String fullText;

	String[] lines;

	Vector extractedLines;

	// -------------------------------------------------------------------------
	public OntologyFlatFileReader(File file) throws Exception {
		this(file.getPath());
	}

	// Constructor to accept reader as the argument.
	// This is designed for utilize Nerius's obo file reader.
	//
	// *** This is VERY inefficient...We need to rewite entire biodataserver
	// code in future!
	//
	// Kei (9/13/2005)
	//
	public OntologyFlatFileReader(final BufferedReader rd) throws Exception {
		fullText = null;
		extractedLines = new Vector();
		String curLine = null;

		while ((curLine = rd.readLine()) != null) {

			extractedLines.add(curLine);
			// System.out.println( curLine );
		}
		rd.close();

		Object[] entireFile = extractedLines.toArray();
		lines = new String[entireFile.length];
		try {
			System.arraycopy(entireFile, 0, lines, 0, lines.length);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw e;
		}

		parseHeader();
		parse();

	}

	// -------------------------------------------------------------------------
	public OntologyFlatFileReader(String filename) throws Exception {
		this.filename = filename;
		try {
			if (filename.trim().startsWith("jar://")) {
				TextJarReader reader = new TextJarReader(filename);
				reader.read();
				fullText = reader.getText();
			} else if (filename.trim().startsWith("http://")) {
				TextHttpReader reader = new TextHttpReader(filename);
				reader.read();
				fullText = reader.getText();
			} else {
				TextFileReader reader = new TextFileReader(filename);
				reader.read();
				fullText = reader.getText();
			}
		} catch (Exception e0) {
			System.err.println("-- Exception while reading ontology flat file "
					+ filename);
			System.err.println(e0.getMessage());
			return;
		}

		lines = fullText.split("\n");
		parseHeader();
		parse();

	} // ctor

	// -------------------------------------------------------------------------

	private int stringToInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	// -------------------------------------------------------------------------
	private void parseHeader() throws Exception {
		String firstLine = lines[0].trim();
		String[] tokens = firstLine.split("\\)");

		String errorMsg = "error in OntologyFlatFileReader.parseHeader ().\n";
		errorMsg += "First line of " + filename + " must have form:\n";
		errorMsg += "   (curator=GO) (type=all) \n";
		errorMsg += "instead found:\n";
		errorMsg += "   " + firstLine + "\n";

		if (tokens.length != 2)
			throw new IllegalArgumentException(errorMsg);

		String[] curatorRaw = tokens[0].split("=");
		if (curatorRaw.length != 2)
			throw new IllegalArgumentException(errorMsg);
		curator = curatorRaw[1].trim();

		String[] typeRaw = tokens[1].split("=");
		if (typeRaw.length != 2)
			throw new IllegalArgumentException(errorMsg);
		ontologyType = typeRaw[1].trim();

	} // parseHeader

	// -------------------------------------------------------------------------

	private void parse() throws Exception {
		ontology = new Ontology(curator, ontologyType);
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];

			int equals = line.indexOf("=");
			String idString = line.substring(0, equals).trim();
			int id = stringToInt(idString);
			String value = line.substring(equals + 1);

			int firstLeftBracket = value.indexOf("[");
			if (firstLeftBracket < 0)
				continue;

			String name = value.substring(0, firstLeftBracket).trim();
			OntologyTerm term = new OntologyTerm(name, id);

			int isaStart = value.indexOf("[isa: ");
			if (isaStart >= 0) {
				int isaEnd = value.indexOf("]", isaStart);
				String rawIsa = value.substring(isaStart + 6, isaEnd).trim();
				String[] allIsas = rawIsa.split(" ");
				for (int j = 0; j < allIsas.length; j++)
					term.addParent(stringToInt(allIsas[j]));
			} // found "[isa: "

			int partofStart = value.indexOf("[partof: ");
			if (partofStart >= 0) {
				int partofEnd = value.indexOf("]", partofStart);
				String rawPartof = value.substring(partofStart + 9, partofEnd)
						.trim();
				String[] allPartofs = rawPartof.split(" ");
				for (int j = 0; j < allPartofs.length; j++)
					term.addContainer(stringToInt(allPartofs[j]));
			} // if
			ontology.add(term);
		} // for i

	} // read

	// -------------------------------------------------------------------------

	public Ontology getOntology() {
		return ontology;
	}
	// -------------------------------------------------------------------------
} // class OntologyFlatFileReader

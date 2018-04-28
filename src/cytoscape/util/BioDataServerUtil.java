/*
 File: BioDataServerUtil.java 
 
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

import cytoscape.CytoscapeInit;

// Provides some utility methods for the BDS classes.
//
public class BioDataServerUtil {

	private static final String NCBI_TAXON_SERVER = "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=";
	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";
	private static final String TAXON_FILE = "tax_report.txt";

	/*
	 * Takes readers (for tax_report and gene_association) and returns species
	 * name in the GA file.
	 */
	public String getSpecies(final BufferedReader taxRd,
			final BufferedReader gaRd) throws IOException {
		String sp = null;
		String curLine = null;

		while (null != (curLine = gaRd.readLine())) {
			curLine.trim();
			// Skip comment
			if (curLine.startsWith("!")) {
				// do nothing
				// System.out.println("Comment: " + curLine);
			} else {
				StringTokenizer st = new StringTokenizer(curLine, "\t");
				while (st.hasMoreTokens()) {
					String curToken = st.nextToken();
					if (curToken.startsWith("taxon")
							|| curToken.startsWith("Taxon")) {
						st = new StringTokenizer(curToken, ":");
						st.nextToken();
						curToken = st.nextToken();
						st = new StringTokenizer(curToken, "|");
						curToken = st.nextToken();
						// System.out.println("Taxon ID found: " + curToken);
						sp = curToken;
						sp = taxIdToName(sp, taxRd);
						taxRd.close();
						gaRd.close();
						return sp;
					}
				}
			}

		}

		taxRd.close();
		gaRd.close();
		return sp;
	}

	// Convert taxonomy ID number to species name.
	// taxId is an NCBI taxon ID
	// All info about taxonomy ID is availabe at:
	// http://www.ncbi.nlm.nih.gov/Taxonomy/TaxIdentifier/tax_identifier.cgi
	//
	public String taxIdToName(String taxId, final BufferedReader taxRd)
			throws IOException {
		String name = null;
		String curLine = null;

		taxRd.readLine();

		while (null != (curLine = taxRd.readLine())) {
			curLine.trim();

			StringTokenizer st = new StringTokenizer(curLine, "|");
			String[] oneEntry = new String[st.countTokens()];
			int counter = 0;

			while (st.hasMoreTokens()) {
				String curToken = st.nextToken().trim();
				oneEntry[counter] = curToken;
				counter++;
				if (curToken.equals(taxId)) {
					name = oneEntry[1];
					return name;
				}
			}
		}
		return name;
	}

	public String checkSpecies(BufferedReader gaReader,
			BufferedReader taxonFileReader) throws IOException {

		String txName = null;
		// Get taxon name

		txName = getSpecies(taxonFileReader, gaReader);
		if (txName == null) {
			txName = CytoscapeInit.getProperties().getProperty(
					"defaultSpeciesName");
			System.out
					.println("Warning: Cannot recognize speices.  Speices field is set to defaultSpeciesName ("
							+ txName + ")");
			System.out
					.println("Warning: Please check your tax_report.txt file.");

		}

		return txName;
	}

	// Returns taxon Map
	public HashMap getTaxonMap(File taxonFile) throws IOException {
		HashMap taxonMap = null;

		String name = null;
		String curLine = null;

		if (taxonFile.canRead() == true) {
			final BufferedReader taxonFileRd = new BufferedReader(
					new FileReader(taxonFile));

			taxonFileRd.readLine();

			while (null != (curLine = taxonFileRd.readLine())) {
				curLine.trim();

				StringTokenizer st = new StringTokenizer(curLine, "|");
				String[] oneEntry = new String[st.countTokens()];
				int counter = 0;

				while (st.hasMoreTokens()) {
					String curToken = st.nextToken().trim();
					oneEntry[counter] = curToken;
					counter++;
					name = oneEntry[1];
					taxonMap.put(curToken, name);
				}
			}

		}

		return taxonMap;
	}

	public HashMap getTaxonMap(BufferedReader taxonFileReader)
			throws IOException {
		HashMap taxonMap = new HashMap();

		String curLine = null;

		taxonFileReader.readLine();

		while ((curLine = taxonFileReader.readLine()) != null) {
			// System.out.println("===========Line: " + curLine + "
			// ===============");
			String[] parts = curLine.split("\\|");

			// System.out.println("####ID = " + parts[3].trim() + ", Name = "
			// + parts[1].trim());
			taxonMap.put(parts[3].trim(), parts[1].trim());
		}
		return taxonMap;
	}

	/*
	 * For a given taxon ID, returns species name. This method connects to
	 * NCBI's Taxonomy server, so Internet connection is required.
	 * 
	 * Kei
	 */
	protected String getTaxonFromNCBI(String id) throws MalformedURLException {
		String txName = null;
		URL taxonURL = null;
		BufferedReader htmlPageReader = null;
		String curLine = null;

		String targetId = id + "&lvl=0";

		taxonURL = new URL(NCBI_TAXON_SERVER + targetId);
		try {
			htmlPageReader = new BufferedReader(new InputStreamReader(taxonURL
					.openStream()));
			while ((curLine = htmlPageReader.readLine()) != null) {
				curLine.trim();
				// System.out.println("HTML:" + curLine);
				if (curLine.startsWith("<title>Taxonomy")) {
					System.out.println("HTML:" + curLine);
					StringTokenizer st = new StringTokenizer(curLine, "(");
					st.nextToken();
					curLine = st.nextToken();
					st = new StringTokenizer(curLine, ")");
					txName = st.nextToken().trim();
					System.out.println("Fetch result: NCBI code " + id + " is "
							+ txName);
					return txName;
				}

			}
			htmlPageReader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txName;
	}

}

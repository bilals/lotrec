
/*
  File: CytoscapeVersion.java 
  
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



package cytoscape;

/** CytoscapeVersion: identify (and describe) successive versions of cytoscape. */
public class CytoscapeVersion {

	public static String version = CytoscapeInit.getProperties().getProperty("cytoscape.version.number");

	private String [] briefHistory = {
	      "0.1   (2001/12/12) initial version",
	      "0.2   (2001/12/18) sped up node selection by name",
	      "0.3   (2001/12/20) node synonyms displayed in NodeProps dialog",
	      "0.4   (2001/12/21) edge attribute files supported",
	      "0.5   (2001/12/28) edge attributes now can control edge color",
	      "0.6   (2002/01/01) popup dialog 'relocation flash' now fixed",
	      "0.7   (2002/01/04) checkEnviroment centralized, now checks for java version",
	      "0.8   (2002/01/07) active paths dialog bounds checking fixed",
	      "0.9   (2002/01/07) IPBiodataServer.getGoTermName exception fixed",
	      "0.10  (2002/01/22) selected nodes make new window; active paths bug fixed",
	      "0.11  (2002/02/04) automatic running of active paths from command line\n" +
	       "                 data passed to ActivePathsFinder via arrays",
	      "0.12  (2002/02/19) reorganized directories; gene common names supported",
	      "0.20  (2002/03/28) now uses plugin architecture; redesign of VizMapping underway",
	      "0.8   (2002/06/17) first alpha release",
	      "0.9   (2002/11/01) first beta release",
	      "0.95  (2002/11/04) added generic annotation",
	      "0.97  (2002/12/05) added LGPL to all source",
	      "1.0   (2003/03/05) added visual styles UI, attributes filter.",
	      "1.1   (2003/05/12) jar loader; visual styles.",
	      "2.0   (2004) now using GINY",
	      "2.1   (2005) many speed improvements.",
	      "2.2   (2005/11) more bug fixes.",
	      "2.3   (2006/06) new (faster) rendering engine.",
	      "2.4   (2006/12) various usability improvements."
	      };


	public String getVersion ()
	{
	  return "Cytoscape version: " + version;
	}

	public String toString ()
	{
	  return getVersion ();
	}

	/** @deprecated See cytoscape.org for history. Will be removed 10/2007 */
	public String getBriefHistory ()
	{
	  StringBuffer sb = new StringBuffer ();
	  for (int i=0; i < briefHistory.length; i++) {
	    sb.append (briefHistory [i]);
	    sb.append ("\n");
	    }

	  return sb.toString ();

	} 
} 



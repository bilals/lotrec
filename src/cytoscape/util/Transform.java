
/*
  File: Transform.java 
  
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

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Apply XSLT to an XML document
 * 
 * @author kono
 *
 */
public class Transform {
	
	
	private File input, xslt, output;
	
	public Transform( String document, String template, String output) {
		input = new File(document);
		xslt = new File(template);
		this.output = new File(output);
	}
	
	public void convert() throws TransformerException {
		StreamSource in = new StreamSource(input);
		StreamSource ss = new StreamSource(xslt);
		StreamResult out = new StreamResult(output);
		
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer(ss);
		tf.transform(in, out);
		
		System.out.println("File conversion done!: " + output.getName());
	}
	
	public static void main(String args[]) throws Exception {
		StreamSource in = new StreamSource(new File(args[0]));
		StreamSource ss = new StreamSource(new File(args[1]));
		StreamResult out = new StreamResult(new File(args[2]));

		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer(ss);
		tf.transform(in, out);
		System.out.println("Done: " + args[2]);
	}
}

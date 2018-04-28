
/*
  File: AnnotationXmlReader.java 
  
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

// AnnotationXmlReader.java


//------------------------------------------------------------------------------
// $Revision: 7760 $  $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
//------------------------------------------------------------------------------
package cytoscape.data.annotation.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

import cytoscape.data.annotation.*;
//-------------------------------------------------------------------------
public class AnnotationXmlReader { 
  File xmlFile;
  Annotation annotation;
  File directoryAbsolute;
//-------------------------------------------------------------------------
public AnnotationXmlReader (File xmlFile) throws Exception
{
  if (!xmlFile.canRead ()) {
    System.out.println ("---- data.annotation.readers.AnnotationXmlReader error, cannot read");
    System.out.println ("        " + xmlFile);
    throw new Exception ("cannot read input: " + xmlFile);
    }

  this.xmlFile = xmlFile;
  directoryAbsolute = xmlFile.getAbsoluteFile().getParentFile ();
  read ();
}
//-------------------------------------------------------------------------
private void read () throws Exception
{
  SAXBuilder builder = new SAXBuilder (); 
  Document doc = builder.build (xmlFile);
  Element root = doc.getRootElement ();

  String species = root.getAttributeValue ("species");
  String ontologyXmlFileName = root.getAttributeValue ("ontology");
  String annotationType = root.getAttributeValue ("type");

  File ontologyXmlFileAbsolutePath = new File (directoryAbsolute, 
                                               ontologyXmlFileName);

  if (!ontologyXmlFileAbsolutePath.canRead ()) {
    System.err.println ("annotation xml file must name its associated ontology xml file");
    System.err.println ("by giving its path relative to the actual location of the");
    System.err.println ("annotation xml file.\n");
    System.err.println ("could not find:");
    System.err.println ("  " + ontologyXmlFileAbsolutePath);
    throw new FileNotFoundException (ontologyXmlFileAbsolutePath.getPath ());
    }

  OntologyXmlReader oReader = new OntologyXmlReader (ontologyXmlFileAbsolutePath);
  Ontology ontology = oReader.getOntology ();

  annotation = new Annotation (species, annotationType, ontology);

  List children = root.getChildren ();
  ListIterator iterator = children.listIterator ();

  while (iterator.hasNext ()) {
    Element termElement = (Element) iterator.next ();
    String entityName = termElement.getChild("entity").getText().trim();
    String tmp  = termElement.getChild("id").getText().trim();
    int id = Integer.parseInt (tmp);
    annotation.add (entityName, id);
    }

} // read
//-------------------------------------------------------------------------
public Annotation getAnnotation ()
{
  return annotation;
}
//-------------------------------------------------------------------------
} // class AnnotationXmlReader



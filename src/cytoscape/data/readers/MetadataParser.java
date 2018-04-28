/*
 File: MetadataParser.java 
 
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

package cytoscape.data.readers;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.generated2.Date;
import cytoscape.generated2.Description;
import cytoscape.generated2.Format;
import cytoscape.generated2.Identifier;
import cytoscape.generated2.ObjectFactory;
import cytoscape.generated2.RdfDescription;
import cytoscape.generated2.RdfRDF;
import cytoscape.generated2.Source;
import cytoscape.generated2.Title;
import cytoscape.generated2.Type;

/**
 * Manipulates network metadata for loading and saving.<br>
 * 
 * @author kono
 * 
 */
public class MetadataParser {

	/*
	 * Actual CyAttribute name for the network metadata
	 */
	public static final String DEFAULT_NETWORK_METADATA_LABEL = "Network Metadata";

	/*
	 * Default values for new meta data. Maybe changed later...
	 */
	private static final String DEF_URI = "http://www.cytoscape.org/";
	private static final String DEF_TYPE = "Protein-Protein Interaction";
	private static final String DEF_FORMAT = "Cytoscape-XGMML";

	private String metadataLabel;
	private CyNetwork network;
	private RdfRDF metadata;
	private CyAttributes networkAttributes;
	private Map rdfAsMap;

	/**
	 * Constructor.
	 * 
	 * @param network
	 *            Target network for editing metadata.
	 */
	public MetadataParser(CyNetwork network) {
		this(network, DEFAULT_NETWORK_METADATA_LABEL);
	}

	/**
	 * Constructor.
	 * 
	 * @param network
	 *            Target network
	 * @param metadataLabel
	 *            Label used as a tag for this attribute.
	 */
	public MetadataParser(CyNetwork network, String metadataLabel) {
		this.metadataLabel = metadataLabel;
		this.network = network;
		networkAttributes = Cytoscape.getNetworkAttributes();

		// Extract Network Metadata from CyAttributes
		rdfAsMap = networkAttributes.getMapAttribute(network.getIdentifier(),
				metadataLabel);
	}

	/**
	 * Build new metadata RDF structure based on given network information.
	 * 
	 * Data items in "defaultLabels" will be created and inserted into RDF
	 * structure.
	 */
	public Map<String, String> makeNewMetadataMap() {

		Map<String, String> dataMap = new HashMap<String, String>();

		// Extract default values from property
		String defSource = CytoscapeInit.getProperties().getProperty(
				"defaultMetadata.source");
		String defType = CytoscapeInit.getProperties().getProperty(
				"defaultMetadata.type");
		String defFormat = CytoscapeInit.getProperties().getProperty(
				"defaultMetadata.format");

		MetadataEntries[] entries = MetadataEntries.values();
		for (int i = 0; i < entries.length; i++) {

			switch (entries[i]) {
			case DATE:
				java.util.Date now = new java.util.Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataMap.put(entries[i].toString(), df.format(now));
				break;
			case TITLE:
				dataMap.put(entries[i].toString(), network.getTitle());
				break;
			case SOURCE:
				if (defSource == null) {
					dataMap.put(entries[i].toString(), DEF_URI);
				} else {
					dataMap.put(entries[i].toString(), defSource);
				}
				break;
			case TYPE:
				if (defType == null) {
					dataMap.put(entries[i].toString(), DEF_TYPE);
				} else {
					dataMap.put(entries[i].toString(), defType);
				}
				break;
			case FORMAT:
				if (defFormat == null) {
					dataMap.put(entries[i].toString(), DEF_FORMAT);
				} else {
					dataMap.put(entries[i].toString(), defFormat);
				}
				break;
			default:
				dataMap.put(entries[i].toString(), "N/A");
				break;
			}
		}
		return dataMap;
	}

	/**
	 * Get metadata as an RDF object
	 * 
	 * @return Network metadata in RdfRDF object
	 * @throws JAXBException
	 */
	public RdfRDF getMetadata() throws JAXBException {
		final ObjectFactory objFactory = new ObjectFactory();
		metadata = objFactory.createRdfRDF();
		RdfDescription dc = objFactory.createRdfDescription();

		// Set "about" for RDF
		dc.setAbout(DEF_URI);

		if (rdfAsMap == null || rdfAsMap.keySet().size() == 0) {
			rdfAsMap = makeNewMetadataMap();
		}

		Set labels = rdfAsMap.keySet();
		Object value = null;
		String key = null;

		Iterator it = labels.iterator();
		while (it.hasNext()) {
			key = (String) it.next();
			value = rdfAsMap.get(key);
			dc.getDcmes().add(getJAXBElement(key.trim(), value));
		}

		metadata.getDescription().add(dc);

		// Put the data in CyAttributes
		networkAttributes.setMapAttribute(network.getIdentifier(),
				metadataLabel, rdfAsMap);

		return metadata;
	}

	/**
	 * Get Network Metadata as Map object
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	public Map getMetadataMap() {
		if (rdfAsMap == null || rdfAsMap.keySet().size() == 0) {
			rdfAsMap = makeNewMetadataMap();
		}
		return rdfAsMap;
	}

	/**
	 * Set data to JAXB-generated objects
	 * 
	 * @param label
	 * @param value
	 * @return
	 * @throws JAXBException
	 */
	private JAXBElement getJAXBElement(String label, Object value)
			throws JAXBException {
		final ObjectFactory objF = new ObjectFactory();
		final MetadataEntries entry = MetadataEntries.valueOf(label
				.toUpperCase());
		if (entry == null) {
			return null;
		}

		switch (entry) {
		case DATE:
			Date dt = objF.createDate();
			dt.setContent(value.toString());
			JAXBElement<Date> dtElement = objF.createDate(dt);
			return dtElement;
		case TITLE:
			Title tl = objF.createTitle();
			tl.setContent(value.toString());
			JAXBElement<Title> tlElement = objF.createTitle(tl);
			return tlElement;
		case IDENTIFIER:
			Identifier id = objF.createIdentifier();
			id.setContent(value.toString());
			JAXBElement<Identifier> idElement = objF.createIdentifier(id);
			return idElement;
		case DESCRIPTION:
			Description dsc = objF.createDescription();
			dsc.setContent(value.toString());
			JAXBElement<Description> dscElement = objF.createDescription(dsc);
			return dscElement;
		case SOURCE:
			Source src = objF.createSource();
			src.setContent(value.toString());
			JAXBElement<Source> srcElement = objF.createSource(src);
			return srcElement;
		case TYPE:
			Type type = objF.createType();
			type.setContent(value.toString());
			JAXBElement<Type> typeElement = objF.createType(type);
			return typeElement;
		case FORMAT:
			Format fmt = objF.createFormat();
			fmt.setContent(value.toString());
			JAXBElement<Format> fmtElement = objF.createFormat(fmt);
			return fmtElement;
		default:
			return null;
		}
	}

	public void setMetadata(MetadataEntries entryName, String value) {
		Map<String, String> metadata = networkAttributes.getMapAttribute(
				network.getIdentifier(), metadataLabel);
		if (metadata == null) {
			metadata = makeNewMetadataMap();
		}
		metadata.put(entryName.toString(), value);
		networkAttributes.setMapAttribute(network.getIdentifier(),
				metadataLabel, metadata);
		rdfAsMap = metadata;
	}

	/**
	 * Set Network Attribute called "Network Metadata" as a Map by using given
	 * JAXB object.<br>
	 * 
	 * @param newMetadata
	 */
	public void setMetadata(RdfRDF newMetadata) {
		RdfDescription dc = newMetadata.getDescription().get(0);

		for (JAXBElement entry : dc.getDcmes()) {
			MetadataEntries type = MetadataEntries.valueOf(entry.getName()
					.getLocalPart().toUpperCase());

			switch (type) {
			case DATE:
				setMetadata(type, ((Date) entry.getValue()).getContent());
				break;
			case TITLE:
				setMetadata(type, ((Title) entry.getValue()).getContent());
				break;
			case IDENTIFIER:
				setMetadata(type, ((Identifier) entry.getValue()).getContent());
				break;
			case DESCRIPTION:
				setMetadata(type, ((Description) entry.getValue()).getContent());
				break;
			case SOURCE:
				setMetadata(type, ((Source) entry.getValue()).getContent());
				break;
			case TYPE:
				setMetadata(type, ((Type) entry.getValue()).getContent());
				break;
			case FORMAT:
				setMetadata(type, ((Format) entry.getValue()).getContent());
				break;
			default:
				break;
			}
		}
	}
}

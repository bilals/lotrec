/*
 File: CyAttributesWriter.java 
 
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

package cytoscape.data.writers;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class CyAttributesWriter {

	public static void writeAttributes(CyAttributes cyAttrs,
			String attributeName, Writer fileOut) throws IOException {
		final BufferedWriter writer;
		if (fileOut instanceof BufferedWriter) {
			writer = (BufferedWriter) fileOut;
		} else {
			writer = new BufferedWriter(fileOut);
		}
		final byte cyType = cyAttrs.getType(attributeName);
		if (!(cyType == CyAttributes.TYPE_BOOLEAN
				|| cyType == CyAttributes.TYPE_FLOATING
				|| cyType == CyAttributes.TYPE_INTEGER
				|| cyType == CyAttributes.TYPE_STRING || cyType == CyAttributes.TYPE_SIMPLE_LIST)) {
			return;
		}
		final byte mulType = cyAttrs.getMultiHashMapDefinition()
				.getAttributeValueType(attributeName);
		writer.write(attributeName);
		writer.write(" (class=");
		{
			final String className;
			if (mulType == MultiHashMapDefinition.TYPE_BOOLEAN) {
				className = "java.lang.Boolean";
			} else if (mulType == MultiHashMapDefinition.TYPE_INTEGER) {
				className = "java.lang.Integer";
			} else if (mulType == MultiHashMapDefinition.TYPE_FLOATING_POINT) {
				className = "java.lang.Double";
			} else {
				className = "java.lang.String";
			}
			writer.write(className);
		}
		writer.write(")");
		writer.newLine();
		final Iterator keys = cyAttrs.getMultiHashMap().getObjectKeys(
				attributeName);
		while (keys.hasNext()) {
			final String key = (String) keys.next();
			writer.write(key);
			writer.write("=");
			if (cyType == CyAttributes.TYPE_BOOLEAN) {
				writer.write(cyAttrs.getBooleanAttribute(key, attributeName)
						.toString());
			} else if (cyType == CyAttributes.TYPE_INTEGER) {
				writer.write(cyAttrs.getIntegerAttribute(key, attributeName)
						.toString());
			} else if (cyType == CyAttributes.TYPE_FLOATING) {
				writer.write(cyAttrs.getDoubleAttribute(key, attributeName)
						.toString());
			} else if (cyType == CyAttributes.TYPE_STRING) {
				writer.write(cyAttrs.getStringAttribute(key, attributeName));
			} else { // TYPE_SIMPLE_LIST
				writer.write("(");
				final Iterator listElms = cyAttrs.getListAttribute(key,
						attributeName).iterator();
				while (listElms.hasNext()) {
					writer.write(listElms.next().toString());
					if (listElms.hasNext()) {
						writer.write("::");
					}
				}
				writer.write(")");
			}
			writer.newLine();
			;
		}
		writer.flush();
	}

}

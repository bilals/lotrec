
/*
  File: CyAttributesReader.java 
  
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

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

// I hate writing parsing code.  Grumble grumble grumble.
public class CyAttributesReader
{

  public static void loadAttributes(CyAttributes cyAttrs,
                                    Reader fileIn) throws IOException
  {
    int lineNum = 0;
    try {
    final BufferedReader reader;
    if (fileIn instanceof BufferedReader) { reader = (BufferedReader) fileIn; }
    else { reader = new BufferedReader(fileIn); }
    String attributeName;
    byte type = -1;
    {
      final String firstLine = reader.readLine();
      lineNum++;
      if (firstLine == null) { return; }
      final String searchStr = "class=";
      final int inx = firstLine.indexOf(searchStr);
      if (inx < 0) {
        attributeName = firstLine.trim(); }
      else {
        attributeName = firstLine.substring(0, inx - 1).trim();
        String foo = firstLine.substring(inx);
        final StringTokenizer tokens = new StringTokenizer(foo);
        foo = tokens.nextToken();
        String className = foo.substring(searchStr.length()).trim();
        if (className.endsWith(")")) {
          className = className.substring(0, className.length() - 1); }
        if (className.equalsIgnoreCase("java.lang.String")) {
          type = MultiHashMapDefinition.TYPE_STRING; }
        else if (className.equalsIgnoreCase("java.lang.Boolean")) {
          type = MultiHashMapDefinition.TYPE_BOOLEAN; }
        else if (className.equalsIgnoreCase("java.lang.Integer")) {
          type = MultiHashMapDefinition.TYPE_INTEGER; }
        else if (className.equalsIgnoreCase("java.lang.Double")) {
          type = MultiHashMapDefinition.TYPE_FLOATING_POINT; } }
    }
    if (attributeName.indexOf("(") >= 0) {
      attributeName = attributeName.substring
        (0, attributeName.indexOf("(")).trim(); }
    boolean firstLine = true;
    boolean list = false;
    while (true) {
      final String line = reader.readLine();
      lineNum++;
      if (line == null) { break; }
      if ("".equals(line.trim())) { continue; }
      int inx = line.indexOf('=');
      final String key = line.substring(0, inx).trim();
      String val = line.substring(inx + 1).trim();
      if (firstLine && val.startsWith("(")) { list = true; }
      if (list) {
        // Chop away leading '(' and trailing ')'.
        val = val.substring(1).trim();
        val = val.substring(0, val.length() - 1).trim();
        // Home-grown parsing (ughh) to handle escape sequences.
        final ArrayList elmsBuff = new ArrayList();
        while (val.length() > 0) {
          final StringBuffer elmBuff = new StringBuffer();
          int inx2;
          for (inx2 = 0; inx2 < val.length(); inx2++) {
            char ch = val.charAt(inx2);
            if (ch == ':') {
              inx2++; inx2++;
              break; }
            else if (ch == '\\') {
              if (inx2 + 1 < val.length()) {
                inx2++;
                char ch2 = val.charAt(inx2);
                if (ch2 == 'n') { elmBuff.append('\n'); }
                else if (ch2 == 't') { elmBuff.append('\t'); }
                else if (ch2 == 'b') { elmBuff.append('\b'); }
                else if (ch2 == 'r') { elmBuff.append('\r'); }
                else if (ch2 == 'f') { elmBuff.append('\f'); }
                else { elmBuff.append(ch2); } }
              else {
                /* val ends in '\' - just ignore it. */ } }
            else {
              elmBuff.append(ch); } }
          elmsBuff.add(new String(elmBuff));
          val = val.substring(inx2); }
        if (firstLine) {
          if (type < 0) {
            while (true) {
              try {
                new Integer((String) elmsBuff.get(0));
                type = MultiHashMapDefinition.TYPE_INTEGER;
                break; }
              catch (Exception e) {}
              try {
                new Double((String) elmsBuff.get(0));
                type = MultiHashMapDefinition.TYPE_FLOATING_POINT;
                break; }
              catch (Exception e) {}
//               try {
//                 new Boolean((String) elmsBuff.get(0));
//                 type = MultiHashMapDefinition.TYPE_BOOLEAN;
//                 break; }
//               catch (Exception e) {}
              type = MultiHashMapDefinition.TYPE_STRING;
              break; } }
          firstLine = false; }
        for (int i = 0; i < elmsBuff.size(); i++) {
          if (type == MultiHashMapDefinition.TYPE_INTEGER) {
            elmsBuff.set(i, new Integer((String) elmsBuff.get(i))); }
          else if (type == MultiHashMapDefinition.TYPE_BOOLEAN) {
            elmsBuff.set(i, new Boolean((String) elmsBuff.get(i))); }
          else if (type == MultiHashMapDefinition.TYPE_FLOATING_POINT) {
            elmsBuff.set(i, new Double((String) elmsBuff.get(i))); }
          else {
            // A string; do nothing.
          } }
        cyAttrs.setListAttribute(key, attributeName, elmsBuff); }
      else { // Not a list.
        // Do the escaping thing.
        final StringBuffer elmBuff = new StringBuffer();
        int inx2;
        for (inx2 = 0; inx2 < val.length(); inx2++) {
          char ch = val.charAt(inx2);
          if (ch == '\\') {
            if (inx2 + 1 < val.length()) {
              inx2++;
              char ch2 = val.charAt(inx2);
              if (ch2 == 'n') { elmBuff.append('\n'); }
              else if (ch2 == 't') { elmBuff.append('\t'); }
              else if (ch2 == 'b') { elmBuff.append('\b'); }
              else if (ch2 == 'r') { elmBuff.append('\r'); }
              else if (ch2 == 'f') { elmBuff.append('\f'); }
              else { elmBuff.append(ch2); } }
            else {
              /* val ends in '\' - just ignore it. */ } }
          else {
            elmBuff.append(ch); } }
        val = new String(elmBuff);
        if (firstLine) {
          if (type < 0) {
            while (true) {
              try {
                new Integer(val);
                type = MultiHashMapDefinition.TYPE_INTEGER;
                break; }
              catch (Exception e) {}
              try {
                new Double(val);
                type = MultiHashMapDefinition.TYPE_FLOATING_POINT;
                break; }
              catch (Exception e) {}
//               try {
//                 new Boolean(val);
//                 type = MultiHashMapDefinition.TYPE_BOOLEAN;
//                 break; }
//               catch (Exception e) {}
              type = MultiHashMapDefinition.TYPE_STRING;
              break; } }
          firstLine = false; }
        if (type == MultiHashMapDefinition.TYPE_INTEGER) {
          cyAttrs.setAttribute(key, attributeName, new Integer(val)); }
        else if (type == MultiHashMapDefinition.TYPE_BOOLEAN) {
          cyAttrs.setAttribute(key, attributeName, new Boolean(val)); }
        else if (type == MultiHashMapDefinition.TYPE_FLOATING_POINT) {
          cyAttrs.setAttribute(key, attributeName, new Double(val)); }
        else {
          cyAttrs.setAttribute(key, attributeName, val); } } }            
     } catch (Exception e) {
     	e.printStackTrace();
	String message = "failed parsing attributes file at line: " + lineNum + " with exception: ";
	throw new IOException( message + e.getMessage());
     }
  }

}

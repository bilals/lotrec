/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.parser.exceptions;

import lotrec.parser.GraphXMLParser;

/**
 *
 * @author said
 */
public class GraphXMLParserException extends Exception {
    public static String NO_GRAPH = "No graph definition found in the parsed xml file,\n" +
            "since there is no tag of the form <"+GraphXMLParser.GRAPH_TAG+"> to represent a graph definition";

    public GraphXMLParserException() {
        super();
    }

    public GraphXMLParserException(String msg) {
        super(msg);
    }
}

package lotrec.parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import lotrec.FileUtils;
import lotrec.Lotrec;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.ConstantExpression;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Edge;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauEdge;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.parser.exceptions.GraphXMLParserException;
import lotrec.parser.exceptions.ParseException;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.xerces.parsers.*;
import org.xml.sax.SAXException;

/**
 *
 * @author said
 */
public class GraphXMLParser {

    public static String GRAPH_TAG = "graph";
    public static String FORMULA_TAG = "formula";
    public static String FORMULA_CODE_TAG = "formula-code";
    public static String FORMULA_MARK_TAG = "formula-mark";
    public static String NODE_TAG = "node";
    public static String NODE_ID_TAG = "node-id";
    public static String NODE_MARK_TAG = "node-mark";
    public static String EDGE_TAG = "edge";
    public static String EDGE_SOURCE_TAG = "edge-source";
    public static String EDGE_TARGET_TAG = "edge-target";
    public static String EDGE_LABEL_TAG = "edge-label";

    public GraphXMLParser() {
    }

    public void saveGraphToXMLFile(Tableau t, String xmlFileName) throws GraphXMLParserException {
        Element e = null;
        Node n = null;
        // Document (Xerces implementation only).
        Document xmldoc = new DocumentImpl();
        // Root element.
        Element root = xmldoc.createElement(GraphXMLParser.GRAPH_TAG);

        Element ne = null;
        for (lotrec.dataStructure.graph.Node node : t.getNodes()) {
            ne = xmldoc.createElement(GraphXMLParser.NODE_TAG);

            e = xmldoc.createElement(GraphXMLParser.NODE_ID_TAG);
            n = xmldoc.createTextNode(node.getName());
            e.appendChild(n);
            ne.appendChild(e);

            Element fe = null;
            for (MarkedExpression formula : ((TableauNode) node).getMarkedExpressions()) {
                fe = xmldoc.createElement(GraphXMLParser.FORMULA_TAG);

                e = xmldoc.createElement(GraphXMLParser.FORMULA_CODE_TAG);
                n = xmldoc.createTextNode(formula.getCodeString());
                e.appendChild(n);
                fe.appendChild(e);

                Element fme = null;
                for (Object m : formula.getMarks()) {
                    fme = xmldoc.createElement(GraphXMLParser.FORMULA_MARK_TAG);
                    n = xmldoc.createTextNode(m.toString());
                    fme.appendChild(n);
                    fe.appendChild(fme);
                }

                ne.appendChild(fe);
            }

            Element nme = null;
            for (Object m : node.getMarks()) {
                nme = xmldoc.createElement(GraphXMLParser.NODE_MARK_TAG);
                n = xmldoc.createTextNode(m.toString());
                nme.appendChild(n);
                ne.appendChild(nme);
            }

            root.appendChild(ne);
        }

        Element ee = null;
        for (lotrec.dataStructure.graph.Node node : t.getNodes()) {
            for (Edge edge : node.getNextEdges()) {
                ee = xmldoc.createElement(GraphXMLParser.EDGE_TAG);

                e = xmldoc.createElement(GraphXMLParser.EDGE_SOURCE_TAG);
                n = xmldoc.createTextNode(node.getName());
                e.appendChild(n);
                ee.appendChild(e);

                e = xmldoc.createElement(GraphXMLParser.EDGE_TARGET_TAG);
                n = xmldoc.createTextNode(edge.getEndNode().getName());
                e.appendChild(n);
                ee.appendChild(e);

                e = xmldoc.createElement(GraphXMLParser.EDGE_LABEL_TAG);
                n = xmldoc.createTextNode(((TableauEdge) edge).getRelation().getCodeString());
                e.appendChild(n);
                ee.appendChild(e);

                root.appendChild(ee);
            }
        }

        xmldoc.appendChild(root);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlFileName);
            // XERCES 1 or 2 additionnal classes.
            OutputFormat of = new OutputFormat("XML", "UTF-8", true);
            of.setIndent(1);
            of.setIndenting(true);
//            of.setDoctype(null, "logic.dtd");
            XMLSerializer serializer = new XMLSerializer(fos, of);
            // As a DOM Serializer
            serializer.asDOMSerializer();
            serializer.serialize(xmldoc.getDocumentElement());
        } catch (FileNotFoundException ex) {
            System.out.println("XML Graph Parser Exception while saving the graph " +
                    t.getName() + " in the file " + xmlFileName);
            System.out.println("This exception due to FileNotFoundException. In the following some details:");
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println("XML Graph Parser Exception while saving the graph " +
                    t.getName() + " in the file " + xmlFileName);
            System.out.println("This exception due to IOException. In the following some details:");
            System.out.println(ex.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                System.out.println("XML Graph Parser Exception while saving the graph " +
                        t.getName() + " in the file " + xmlFileName);
                System.out.println("This exception due to IOException while closing the file. In the following some details:");
                System.out.println(ex.getMessage());
            }
        }
    }

    public Tableau parseGraphXMLFile(Logic logic, String xmlFileName) throws GraphXMLParserException {
        Tableau t = new Tableau();
        String fileName = (String) xmlFileName;
        Document doc;
        DOMParser parser = new DOMParser();
        try {
            parser.parse(fileName);
            doc = parser.getDocument();
        } catch (SAXException ex) {
            System.err.print(LogicXMLParser.stack2string(ex));
            throw new GraphXMLParserException("Graph xml file parsing stoped because of the following SAX Exception:\n" +
                    "The exception message is:\n" +
                    ex.getMessage() + "\n" +
                    "For more details read the command prompt (i.e. shell) output...");
        } catch (java.io.FileNotFoundException ex) {
            System.err.print(LogicXMLParser.stack2string(ex));
            throw new GraphXMLParserException("Graph xml file parsing stoped because of FileNotFoundException:\n" +
                    "The exception message is:\n" +
                    ex.getMessage() + "\n" +
                    "For more details read the command prompt (i.e. shell) output...");
        } catch (IOException ex) {
            if (ex instanceof java.net.MalformedURLException) {
                System.err.print(LogicXMLParser.stack2string(ex));
                throw new GraphXMLParserException("Graph xml file parsing stoped because of MalformedURL Exception.\n" +
                        "This exception occurs when the file name contains some special characters, such as ;,:,(,[,...\n" +
                        "The exception message is:\n" +
                        ex.getMessage() + "\n" +
                        "For more details read the command prompt (i.e. shell) output...");
            } else {
                System.err.print(LogicXMLParser.stack2string(ex));
                throw new GraphXMLParserException("Graph xml file parsing stoped because of the following IO Exception:\n" +
                        "The exception message is:\n" +
                        ex.getMessage() + "\n" +
                        "For more details read the command prompt (i.e. shell) output...");
            }
        }
        Lotrec.println("Graph xml file parsing is starting...");
        if (doc.getElementsByTagName(GraphXMLParser.GRAPH_TAG).getLength() == 0) {
            throw new GraphXMLParserException(GraphXMLParserException.NO_GRAPH);
        }
        t.setName(FileUtils.getFileNameWithoutExtension(fileName));
        parseNodes(logic, t, doc);
        if (t.getNodes().size() > 0) {
            parseEdges(logic, t, doc);
        }
        //Lotrec.println("------------The resulting parsed graph-------------");
        //Lotrec.println(t);
        //Lotrec.println("-------End of resulting parsed graph-------");
        Lotrec.println("Graph xml file parsing completed successfully...");
        return t;
    }

    private void parseNodes(Logic logic, Tableau t, Document doc) throws GraphXMLParserException {
        int nodesCount = doc.getElementsByTagName(GraphXMLParser.NODE_TAG).getLength();
        for (int i = 0; i < nodesCount; i++) {
            Element nodeElement = (Element) doc.getElementsByTagName(GraphXMLParser.NODE_TAG).item(i);
            TableauNode node = new TableauNode(getTagTextContent(nodeElement, GraphXMLParser.NODE_ID_TAG));
            parseFormulas(logic, node, nodeElement);
            parseNodeMarks(node, nodeElement);
            t.add(node);
        }
    }

    private void parseFormulas(Logic logic, TableauNode node, Element nodeElement) throws GraphXMLParserException {
        int formulasCount = nodeElement.getElementsByTagName(GraphXMLParser.FORMULA_TAG).getLength();
        for (int i = 0; i < formulasCount; i++) {
            Element formulaElement = (Element) nodeElement.getElementsByTagName(GraphXMLParser.FORMULA_TAG).item(i);
            String formulaCode = getTagTextContent(formulaElement, GraphXMLParser.FORMULA_CODE_TAG);
            OldiesTokenizer oldiesTokenizer = prepareOldiesTokenizer(logic);
            MarkedExpression formula;
            try {
                formula = new MarkedExpression(oldiesTokenizer.parseExpression(formulaCode));
            } catch (ParseException ex) {
                formula = new MarkedExpression(new ConstantExpression(formulaCode.replaceAll(" ", "_")));
            //throw new GraphXMLParserException(ex.getMessage());
            }
            parseFormulaMarks(formula, formulaElement);
            node.add(formula);
        }
    }

    private void parseFormulaMarks(MarkedExpression formula, Element formulaElement) {
        int formulaMarksCount = formulaElement.getElementsByTagName(GraphXMLParser.FORMULA_MARK_TAG).getLength();
        for (int i = 0; i < formulaMarksCount; i++) {
            Element formulaMarkElement = (Element) formulaElement.getElementsByTagName(GraphXMLParser.FORMULA_MARK_TAG).item(i);
            formula.mark(formulaMarkElement.getTextContent());
        }
    }

    private void parseNodeMarks(TableauNode node, Element nodeElement) {
        int nodeMarksCount = nodeElement.getElementsByTagName(GraphXMLParser.NODE_MARK_TAG).getLength();
        for (int i = 0; i < nodeMarksCount; i++) {
            Element nodeMarkElement = (Element) nodeElement.getElementsByTagName(GraphXMLParser.NODE_MARK_TAG).item(i);
            node.mark(nodeMarkElement.getTextContent());
        }
    }

    private void parseEdges(Logic logic, Tableau t, Document doc) throws GraphXMLParserException {
        int edgesCount = doc.getElementsByTagName(GraphXMLParser.EDGE_TAG).getLength();
        for (int i = 0; i < edgesCount; i++) {
            Element edgeElement = (Element) doc.getElementsByTagName(GraphXMLParser.EDGE_TAG).item(i);
            String sourceNodeId = getTagTextContent(edgeElement, GraphXMLParser.EDGE_SOURCE_TAG);
            String targetNodeId = getTagTextContent(edgeElement, GraphXMLParser.EDGE_TARGET_TAG);
            String label = getTagTextContent(edgeElement, GraphXMLParser.EDGE_LABEL_TAG);
            lotrec.dataStructure.graph.Node sourceNode = t.getNode(sourceNodeId);
            lotrec.dataStructure.graph.Node targetNode = t.getNode(targetNodeId);
            Expression relation;
            OldiesTokenizer oldiesTokenizer = prepareOldiesTokenizer(logic);
            try {
                relation = oldiesTokenizer.parseExpression(label);
            } catch (ParseException ex) {
                relation = new ConstantExpression(label.replaceAll(" ", "_"));
            //throw new GraphXMLParserException(ex.getMessage());
            }
            if(sourceNode != null && targetNode != null){
                lotrec.dataStructure.tableau.TableauEdge e = new TableauEdge(sourceNode, targetNode, relation);
                sourceNode.link(e);
            }
        }
    }

    private OldiesTokenizer prepareOldiesTokenizer(Logic logic) {
        OldiesTokenizer oldiesTokenizer;
        oldiesTokenizer = new OldiesTokenizer(logic);
        oldiesTokenizer.initializeTokenizerAndProps();
        return oldiesTokenizer;
    }

    public String getTagTextContent(
            Object e, String tagName) {
        if (e instanceof Document) {
            return ((Document) e).getElementsByTagName(tagName).item(0).getTextContent();
        } else {
            return ((Element) e).getElementsByTagName(tagName).item(0).getTextContent();
        }

    }
}

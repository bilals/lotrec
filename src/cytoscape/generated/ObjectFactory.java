//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.4-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.01.16 at 04:02:44 PM PST 
//


package cytoscape.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the cytoscape.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SelectedPanel_QNAME = new QName("", "selectedPanel");
    private final static QName _SessionNote_QNAME = new QName("", "sessionNote");
    private final static QName _PanelState_QNAME = new QName("", "panelState");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cytoscape.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Ontology }
     * 
     */
    public Ontology createOntology() {
        return new Ontology();
    }

    /**
     * Create an instance of {@link SelectedEdges }
     * 
     */
    public SelectedEdges createSelectedEdges() {
        return new SelectedEdges();
    }

    /**
     * Create an instance of {@link Server }
     * 
     */
    public Server createServer() {
        return new Server();
    }

    /**
     * Create an instance of {@link Cysession }
     * 
     */
    public Cysession createCysession() {
        return new Cysession();
    }

    /**
     * Create an instance of {@link Cytopanels }
     * 
     */
    public Cytopanels createCytopanels() {
        return new Cytopanels();
    }

    /**
     * Create an instance of {@link HiddenNodes }
     * 
     */
    public HiddenNodes createHiddenNodes() {
        return new HiddenNodes();
    }

    /**
     * Create an instance of {@link NetworkFrames }
     * 
     */
    public NetworkFrames createNetworkFrames() {
        return new NetworkFrames();
    }

    /**
     * Create an instance of {@link SelectedNodes }
     * 
     */
    public SelectedNodes createSelectedNodes() {
        return new SelectedNodes();
    }

    /**
     * Create an instance of {@link Panels }
     * 
     */
    public Panels createPanels() {
        return new Panels();
    }

    /**
     * Create an instance of {@link Plugins }
     * 
     */
    public Plugins createPlugins() {
        return new Plugins();
    }

    /**
     * Create an instance of {@link OntologyServer }
     * 
     */
    public OntologyServer createOntologyServer() {
        return new OntologyServer();
    }

    /**
     * Create an instance of {@link NetworkTree }
     * 
     */
    public NetworkTree createNetworkTree() {
        return new NetworkTree();
    }

    /**
     * Create an instance of {@link Parent }
     * 
     */
    public Parent createParent() {
        return new Parent();
    }

    /**
     * Create an instance of {@link HiddenEdges }
     * 
     */
    public HiddenEdges createHiddenEdges() {
        return new HiddenEdges();
    }

    /**
     * Create an instance of {@link Edge }
     * 
     */
    public Edge createEdge() {
        return new Edge();
    }

    /**
     * Create an instance of {@link Desktop }
     * 
     */
    public Desktop createDesktop() {
        return new Desktop();
    }

    /**
     * Create an instance of {@link Panel }
     * 
     */
    public Panel createPanel() {
        return new Panel();
    }

    /**
     * Create an instance of {@link Child }
     * 
     */
    public Child createChild() {
        return new Child();
    }

    /**
     * Create an instance of {@link ViewableNodes }
     * 
     */
    public ViewableNodes createViewableNodes() {
        return new ViewableNodes();
    }

    /**
     * Create an instance of {@link NetworkFrame }
     * 
     */
    public NetworkFrame createNetworkFrame() {
        return new NetworkFrame();
    }

    /**
     * Create an instance of {@link Node }
     * 
     */
    public Node createNode() {
        return new Node();
    }

    /**
     * Create an instance of {@link SessionState }
     * 
     */
    public SessionState createSessionState() {
        return new SessionState();
    }

    /**
     * Create an instance of {@link DesktopSize }
     * 
     */
    public DesktopSize createDesktopSize() {
        return new DesktopSize();
    }

    /**
     * Create an instance of {@link Cytopanel }
     * 
     */
    public Cytopanel createCytopanel() {
        return new Cytopanel();
    }

    /**
     * Create an instance of {@link Network }
     * 
     */
    public Network createNetwork() {
        return new Network();
    }

    /**
     * Create an instance of {@link Plugin }
     * 
     */
    public Plugin createPlugin() {
        return new Plugin();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "selectedPanel")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createSelectedPanel(String value) {
        return new JAXBElement<String>(_SelectedPanel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "sessionNote")
    public JAXBElement<String> createSessionNote(String value) {
        return new JAXBElement<String>(_SessionNote_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "panelState")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPanelState(String value) {
        return new JAXBElement<String>(_PanelState_QNAME, String.class, null, value);
    }

}

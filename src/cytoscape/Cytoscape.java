/*
File: Cytoscape.java 

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
 *///---------------------------------------------------------------------------
package cytoscape;

import giny.model.Edge;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.net.URL;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.InetSocketAddress;

import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.bind.JAXBException;

import cytoscape.actions.SaveSessionAction;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;
import cytoscape.data.ExpressionData;
import cytoscape.data.ImportHandler;
import cytoscape.data.Semantics;
import cytoscape.data.readers.BookmarkReader;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.servers.BioDataServer;
import cytoscape.data.servers.OntologyServer;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.giny.CytoscapeFingRootGraph;
import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.init.CyInitParams;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.VisualMappingManager;
import cytoscape.bookmarks.Bookmarks;

/**
 * This class, Cytoscape is <i>the</i> primary class in the API.
 * 
 * All Nodes and Edges must be created using the methods getCyNode and
 * getCyEdge, available only in this class. Once A node or edge is created using
 * these methods it can then be added to a CyNetwork, where it can be used
 * algorithmically.<BR>
 * <BR>
 * The methods get/setNode/EdgeAttributeValue allow you to assocate data with
 * nodes or edges. That data is then carried into all CyNetworks where that
 * Node/Edge is present.
 */
public abstract class Cytoscape {
    //
    // Signals
    //
    /**
     * Please consult CyAttributes documentation for event listening
     * 
     * @deprecated this event should not be used, it is not fired. Will be
     *             removed June 2007.
     * @see CyAttributes
     */
    public static String ATTRIBUTES_CHANGED = "ATTRIBUTES_CHANGED";
    public static String NETWORK_CREATED = "NETWORK_CREATED";
    public static String DATASERVER_CHANGED = "DATASERVER_CHANGED";
    public static String EXPRESSION_DATA_LOADED = "EXPRESSION_DATA_LOADED";
    public static String NETWORK_DESTROYED = "NETWORK_DESTROYED";
    public static String CYTOSCAPE_INITIALIZED = "CYTOSCAPE_INITIALIZED";
    public static String CYTOSCAPE_EXIT = "CYTOSCAPE_EXIT";    // KONO: 03/10/2006 For vizmap saving and loading
    public static String SESSION_SAVED = "SESSION_SAVED";
    public static String SESSION_LOADED = "SESSION_LOADED";
    public static String VIZMAP_RESTORED = "VIZMAP_RESTORED";
    public static String SAVE_VIZMAP_PROPS = "SAVE_VIZMAP_PROPS";
    public static String VIZMAP_LOADED = "VIZMAP_LOADED";    // WANG: 11/14/2006 For plugin to save state
    public static final String SAVE_PLUGIN_STATE = "SAVE_PLUGIN_STATE";
    public static final String RESTORE_PLUGIN_STATE = "RESTORE_PLUGIN_STATE";    // events for network modification
    public static final String NETWORK_MODIFIED = "NETWORK_MODIFIED";
    public static final String NETWORK_SAVED = "NETWORK_SAVED";
    public static final String NETWORK_LOADED = "NETWORK_LOADED";    // Root ontology network in the network panel
    public static final String ONTOLOGY_ROOT = "ONTOLOGY_ROOT";    // Events for Preference Dialog (properties).
    // Signals that the preference has change interally to the
    // prefs dialog.
    public static final String PREFERENCE_MODIFIED = "PREFERENCE_MODIFIED";    // Signals that CytoscapeInit properties have been updated.
    public static final String PREFERENCES_UPDATED = "PREFERENCES_UPDATED";
    /**
     * When creating a network, use one of the standard suffixes to have it
     * parsed correctly<BR>
     * <ul>
     * <li> sif -- Simple Interaction File</li>
     * <li> gml -- Graph Markup Languange</li>
     * <li> sbml -- SBML</li>
     * <li> xgmml -- XGMML</li>
     * </ul>
     */
    public static int FILE_BY_SUFFIX = 0;
    public static int FILE_GML = 1;
    public static int FILE_SIF = 2;
    public static int FILE_SBML = 3;
    public static int FILE_XGMML = 4;
    public static int FILE_BIOPAX = 5;
    public static int FILE_PSI_MI = 6;    // constants for tracking selection mode globally
    public static final int SELECT_NODES_ONLY = 1;
    public static final int SELECT_EDGES_ONLY = 2;
    public static final int SELECT_NODES_AND_EDGES = 3;    // global to represent which selection mode is active
    private static int currentSelectionMode = SELECT_NODES_ONLY;    // Value to manage session state
    public static final int SESSION_NEW = 0;
    public static final int SESSION_OPENED = 1;
    public static final int SESSION_CHANGED = 2;
    public static final int SESSION_CLOSED = 3;
    private static int sessionState = SESSION_NEW;
    private static BioDataServer bioDataServer;
    /**
     * New ontology server. This will replace BioDataServer.
     */
    private static OntologyServer ontologyServer;
    private static String species;
    public static final String READER_CLIENT_KEY = "reader_client_key";    // global flag to indicate if Squiggle is turned on
    private static boolean squiggleEnabled = false;
    /**
     * The shared RootGraph between all Networks
     */
    protected static CytoscapeRootGraph cytoscapeRootGraph;
    /**
     * Node CyAttributes.
     */
    private static CyAttributes nodeAttributes = new CyAttributesImpl();
    /**
     * Edge CyAttributes.
     */
    private static CyAttributes edgeAttributes = new CyAttributesImpl();
    /**
     * Network CyAttributes.
     */
    private static CyAttributes networkAttributes = new CyAttributesImpl();
    /**
     * Ontology Attributes
     * 
     * Will be used to store annotations for ontology
     * 
     */
    private static CyAttributes ontologyAttributes = new CyAttributesImpl();
    protected static ExpressionData expressionData;
    protected static Object pcsO = new Object();
    protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
            pcsO);    // Test
    protected static Object pcs2 = new Object();
    protected static PropertyChangeSupport newPcs = new PropertyChangeSupport(
            pcs2);
    protected static Map networkViewMap;
    protected static Map networkMap;
    protected static CytoscapeDesktop defaultDesktop;
    protected static String currentNetworkID;
    protected static String currentNetworkViewID;
    protected static String ontologyRootID;
    /**
     * Used by session writer. If this is null, session writer opens the file
     * chooser. Otherwise, overwrite the file.
     * 
     * KONO: 02/23/2006
     */
    private static String currentSessionFileName;
    private static Bookmarks bookmarks;
    /**
     * A null CyNetwork to give when there is no Current Network
     */
    protected static CyNetwork nullNetwork = getRootGraph().createNetwork(
            new int[]{}, new int[]{});
    private static ImportHandler importHandler = new ImportHandler();

    public static ImportHandler getImportHandler() {
        return importHandler;
    }
    /**
     * A null CyNetworkView to give when there is no Current NetworkView
     */
    protected static CyNetworkView nullNetworkView = new DingNetworkView(
            nullNetwork, "null");

    /*
     * VMM should be tied to Cytoscape, not to Desktop. Developers should call
     * this from here.
     */
    protected static VisualMappingManager VMM = new VisualMappingManager(
            nullNetworkView);

    /**
     * @return a nullNetworkView object. This is NOT simply a null object.
     */
    public static CyNetworkView getNullNetworkView() {
        return nullNetworkView;
    }

    /**
     * @return the nullNetwork CyNetwork. This is NOT simply a null object.
     */
    public static CyNetwork getNullNetwork() {
        return nullNetwork;
    }

    /**
     * Shuts down Cytoscape, after giving plugins time to react.
     * 
     * @param returnVal
     *            The return value. Zero indicates success, non-zero otherwise.
     */
    public static void exit(int returnVal) {

        int mode = CytoscapeInit.getCyInitParams().getMode();

        if (mode == CyInitParams.EMBEDDED_WINDOW || mode == CyInitParams.GUI) {
            // prompt the user about saving modified files before quitting
            if (confirmQuit()) {
                try {
                    firePropertyChange(CYTOSCAPE_EXIT, null, "now");
                } catch (Exception e) {
                    System.out.println("Errors on close, closed anyways.");
                }

                System.out.println("Cytoscape Exiting....");
                if (mode == CyInitParams.EMBEDDED_WINDOW) {
                    // don't system exit since we are running as part
                    // of a bigger application. Instead, dispose of the
                    // desktop.
                    getDesktop().dispose();
                } else {
                    System.exit(returnVal);
                }
            } else {
                return;
            }

        } else {
            System.out.println("Cytoscape Exiting....");
            System.exit(returnVal);
        }
    }

    /**
     * Prompt the user about saving modified files before quitting.
     */
    private static boolean confirmQuit() {
        final String msg = "Do you want to save your session?";
        int networkCount = Cytoscape.getNetworkSet().size();

        /*
         * If there is no network, just quit.
         */
        if (networkCount == 0) {
            return true;
        }

        //
        // Confirm user to save current session or not.
        //

        Object[] options = {"Yes, save and quit", "No, just quit", "Cancel"};
        int n = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), msg,
                "Save Networks Before Quitting?", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.NO_OPTION) {
            return true;
        } else if (n == JOptionPane.YES_OPTION) {
            SaveSessionAction saveAction = new SaveSessionAction();
            saveAction.actionPerformed(null);
            if (Cytoscape.getCurrentSessionFileName() == null) {
                return confirmQuit();
            } else {
                return true;
            }
        } else {
            return false; // default if dialog box is closed
        }
    }

    // --------------------//
    // Root Graph Methods
    // --------------------//
    /**
     * Bound events are:
     * <ol>
     * <li>NETWORK_CREATED
     * <li>NETWORK_DESTROYED
     * <li>CYTOSCAPE_EXIT
     * </ol>
     */
    public static SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
        return pcs;
    }

    public static PropertyChangeSupport getPropertyChangeSupport() {
        return newPcs;
    }

    public static VisualMappingManager getVisualMappingManager() {
        return VMM;
    }

    /**
     * Return the CytoscapeRootGraph
     */
    public static CytoscapeRootGraph getRootGraph() {
        if (cytoscapeRootGraph == null) {
            cytoscapeRootGraph = new CytoscapeFingRootGraph();
        }
        return cytoscapeRootGraph;
    }

    /**
     * Ensure the capacity of Cytoscapce. This is to prevent the inefficiency of
     * adding nodes one at a time.
     */
    public static void ensureCapacity(int nodes, int edges) {
        // getRootGraph().ensureCapacity( nodes, edges );
    }

    /**
     * @return all CyNodes that are present in Cytoscape
     */
    public static List getCyNodesList() {
        return getRootGraph().nodesList();
    }

    /**
     * @return all CyEdges that are present in Cytoscape
     */
    public static List getCyEdgesList() {
        return getRootGraph().edgesList();
    }

    /**
     * @param alias
     *            an alias of a node
     * @return will return a node, if one exists for the given alias
     */
    public static CyNode getCyNode(String alias) {
        return getCyNode(alias, false);
    }

    /**
     * @param nodeID
     *            an alias of a node
     * @param create
     *            will create a node if one does not exist
     * @return will always return a node, if <code>create</code> is true
     * 
     * KONO: 5/4/2006 Since we removed the canonicalName, no "canonicalization"
     * is necessary. This method uses given nodeID as the identifier.
     * 
     */
    public static CyNode getCyNode(String nodeID, boolean create) {

        CyNode node = Cytoscape.getRootGraph().getNode(nodeID);

        // If the node is already exists,return it.
        if (node != null) {
            return node;
        }

        // And if we do not have to create new one, just return null
        if (!create) {
            return null;
        }

        // Now, create a new node.
        node = (CyNode) getRootGraph().getNode(
                Cytoscape.getRootGraph().createNode());
        node.setIdentifier(nodeID);

        /*
         * We do not need canonicalName anymore. If necessary, user should
         * create one from Attribute Browser.
         * 
         * NOTE:
         * 
         * The following statement referencing CANONICAL_NAME should not be
         * deleted until its removed from the core (Semantics.java) on April,
         * 2007 _DO NOT DELETE UNTIL THEN_
         */

        if (getNodeAttributes().getStringAttribute(nodeID,
                Semantics.CANONICAL_NAME) == null) {
            getNodeAttributes().setAttribute(nodeID, Semantics.CANONICAL_NAME,
                    nodeID);
        }
        return node;
    }

    /**
     * Gets the first CyEdge found between the two nodes (direction does not
     * matter) that has the given value for the given attribute. If the edge
     * doesn't exist, then it creates an undirected edge.
     * 
     * This method MIGHT be deprecated, or even removed, because Cytoscape
     * shouldn't really be using undirected edges.
     * 
     * @param node_1
     *            one end of the edge
     * @param node_2
     *            the other end of the edge
     * @param attribute
     *            the attribute of the edge to be searched, a common one is
     *            {@link Semantics#INTERACTION }
     * @param attribute_value
     *            a value for the attribute, like "pp"
     * @param create
     *            will create an edge if one does not exist and if attribute is
     *            {@link Semantics#INTERACTION}
     * @return returns an existing CyEdge if present, or creates one if
     *         <code>create</code> is true and attribute is
     *         Semantics.INTERACTION, otherwise returns null.
     */
    public static CyEdge getCyEdge(Node node_1, Node node_2, String attribute,
            Object attribute_value, boolean create) {
        return getCyEdge(node_1, node_2, attribute, attribute_value, create,
                false);
    }

    /**
     * Gets the first CyEdge found between the two nodes that has the given
     * value for the given attribute. If direction flag is set, then direction
     * is taken into account, A->B is NOT equivalent to B->A
     * 
     * @param source
     *            one end of the edge
     * @param target
     *            the other end of the edge
     * @param attribute
     *            the attribute of the edge to be searched, a common one is
     *            {@link Semantics#INTERACTION }
     * @param attribute_value
     *            a value for the attribute, like "pp"
     * @param create
     *            will create an edge if one does not exist and if attribute is
     *            {@link Semantics#INTERACTION}
     * @param directed
     *            take direction into account, source->target is NOT
     *            target->source
     * @return returns an existing CyEdge if present, or creates one if
     *         <code>create</code> is true and attribute is
     *         Semantics.INTERACTION, otherwise returns null.
     */
    public static CyEdge getCyEdge(Node source, Node target, String attribute,
            Object attribute_value, boolean create, boolean directed) {

        if (Cytoscape.getRootGraph().getEdgeCount() != 0) {
            int[] n1Edges = Cytoscape.getRootGraph().getAdjacentEdgeIndicesArray(source.getRootGraphIndex(),
                    true, true, true);

            for (int i = 0; i < n1Edges.length; i++) {
                CyEdge edge = (CyEdge) Cytoscape.getRootGraph().getEdge(
                        n1Edges[i]);
                Object attValue = private_getEdgeAttributeValue(edge, attribute);

                if (attValue != null && attValue.equals(attribute_value)) {
                    // Despite the fact that we know the source node
                    // matches, the case of self edges dictates that
                    // we must check the source as well.
                    CyNode edgeTarget = (CyNode) edge.getTarget();
                    CyNode edgeSource = (CyNode) edge.getSource();
                    if ((edgeTarget.getRootGraphIndex() == target.getRootGraphIndex()) && (edgeSource.getRootGraphIndex() == source.getRootGraphIndex())) {
                        return edge;
                    }

                    if (!directed) {
                        // note that source and target are switched
                        if ((edgeTarget.getRootGraphIndex() == source.getRootGraphIndex()) && (edgeSource.getRootGraphIndex() == target.getRootGraphIndex())) {
                            return edge;
                        }
                    }
                }
            }// for i
        }

        if (create && attribute instanceof String && attribute.equals(Semantics.INTERACTION)) {
            // create the edge
            CyEdge edge = (CyEdge) Cytoscape.getRootGraph().getEdge(
                    Cytoscape.getRootGraph().createEdge(source, target));

            // create the edge id
            String edge_name = CyEdge.createIdentifier(source.getIdentifier(),
                    (String) attribute_value, target.getIdentifier());
            edge.setIdentifier(edge_name);

            // store edge id as INTERACTION / CANONICAL_NAME Attributes
            edgeAttributes.setAttribute(edge_name, Semantics.INTERACTION,
                    (String) attribute_value);
            return edge;
        }
        return null;
    }

    /**
     * Returns and edge if it exists, otherwise creates a directed edge.
     * 
     * @param source_alias
     *            an alias of a node
     * @param edge_name
     *            the name of the node
     * @param target_alias
     *            an alias of a node
     * @return will always return an edge
     */
    public static CyEdge getCyEdge(String source_alias, String edge_name,
            String target_alias, String interaction_type) {

        edge_name = canonicalizeName(edge_name);
        CyEdge edge = Cytoscape.getRootGraph().getEdge(edge_name);
        if (edge != null) {
            return edge;
        }

        // edge does not exist, create one
        CyNode source = getCyNode(source_alias);
        CyNode target = getCyNode(target_alias);

        return getCyEdge(source, target, Semantics.INTERACTION,
                interaction_type, true, true);
    }

    private static Object private_getEdgeAttributeValue(Edge edge,
            String attribute) {
        final CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
        final String canonName = edge.getIdentifier();
        final byte cyType = edgeAttrs.getType(attribute);
        if (cyType == CyAttributes.TYPE_BOOLEAN) {
            return edgeAttrs.getBooleanAttribute(canonName, attribute);
        } else if (cyType == CyAttributes.TYPE_FLOATING) {
            return edgeAttrs.getDoubleAttribute(canonName, attribute);
        } else if (cyType == CyAttributes.TYPE_INTEGER) {
            return edgeAttrs.getIntegerAttribute(canonName, attribute);
        } else if (cyType == CyAttributes.TYPE_STRING) {
            return edgeAttrs.getStringAttribute(canonName, attribute);
        } else if (cyType == CyAttributes.TYPE_SIMPLE_LIST) {
            return edgeAttrs.getListAttribute(canonName, attribute);
        } else if (cyType == CyAttributes.TYPE_SIMPLE_MAP) {
            return edgeAttrs.getMapAttribute(canonName, attribute);
        } else {
            return null;
        }
    }

    /**
     * @deprecated This will be removed Feb 2007.
     */
    private static String canonicalizeName(String name) {
        String canonicalName = name;

        if (bioDataServer != null) {
            canonicalName = bioDataServer.getCanonicalName(species, name);
            if (canonicalName == null) {
                canonicalName = name;
            }
        }
        return canonicalName;
    }

    /**
     * @deprecated This will be removed Feb 2007.
     */
    public static void setSpecies() {
        species = CytoscapeInit.getProperties().getProperty(
                "defaultSpeciesName");
    }

    // --------------------//
    // Network Methods
    // --------------------//
    /**
     * Return the Network that currently has the Focus. Can be different from
     * getCurrentNetworkView
     */
    public static CyNetwork getCurrentNetwork() {
        if (currentNetworkID == null || !(getNetworkMap().containsKey(currentNetworkID))) {
            return nullNetwork;
        }
        CyNetwork network = (CyNetwork) getNetworkMap().get(currentNetworkID);
        return network;
    }

    /**
     * Return a List of all available CyNetworks
     */
    public static Set getNetworkSet() {
        return new java.util.LinkedHashSet(((HashMap) getNetworkMap()).values());
    }

    /**
     * @return the CyNetwork that has the given identifier or the nullNetwork
     *         (see {@link #getNullNetwork()}) if there is no such network.
     */
    public static CyNetwork getNetwork(String id) {
        if (id != null && getNetworkMap().containsKey(id)) {
            return (CyNetwork) getNetworkMap().get(id);
        }
        return nullNetwork;
    }

    /**
     * @return a CyNetworkView for the given ID, if one exists, otherwise
     *         returns null
     */
    public static CyNetworkView getNetworkView(String network_id) {
        if (network_id == null || !(getNetworkViewMap().containsKey(network_id))) {
            return nullNetworkView;
        }
        CyNetworkView nview = (CyNetworkView) getNetworkViewMap().get(
                network_id);
        return nview;
    }

    /**
     * @return if a view exists for a given network id
     */
    public static boolean viewExists(String network_id) {
        return getNetworkViewMap().containsKey(network_id);
    }

    /**
     * Return the CyNetworkView that currently has the focus. Can be different
     * from getCurrentNetwork
     */
    public static CyNetworkView getCurrentNetworkView() {
        if (currentNetworkViewID == null || !(getNetworkViewMap().containsKey(currentNetworkViewID))) {
            return nullNetworkView;        // System.out.println( "Cytoscape returning current network view:
        // "+currentNetworkViewID );
        }
        CyNetworkView nview = (CyNetworkView) getNetworkViewMap().get(
                currentNetworkViewID);
        return nview;
    }

    /**
     * @return the reference to the One CytoscapeDesktop
     */
    public static CytoscapeDesktop getDesktop() {
        if (defaultDesktop == null) {
            // System.out.println( " Defaultdesktop created: "+defaultDesktop );
            defaultDesktop = new CytoscapeDesktop(CytoscapeDesktop.parseViewType(CytoscapeInit.getProperties().getProperty(
                    "viewType")));
        }
        return defaultDesktop;
    }

    /**
     * @deprecated This will be removed Feb 2007.
     */
    public static void setCurrentNetwork(String id) {
        if (getNetworkMap().containsKey(id)) {
            currentNetworkID = id;        // System.out.println( "Currentnetworkid is: "+currentNetworkID+ " set
        // from : "+id );
        }
    }

    /**
     * @deprecated This will be removed Feb 2007.
     * @return true if there is network view, false if not
     */
    public static boolean setCurrentNetworkView(String id) {
        if (getNetworkViewMap().containsKey(id)) {
            currentNetworkViewID = id;
            return true;
        }
        return false;
    }

    /**
     * This Map has keys that are Strings ( network_ids ) and values that are
     * networks.
     */
    protected static Map getNetworkMap() {
        if (networkMap == null) {
            networkMap = new HashMap();
        }
        return networkMap;
    }

    /**
     * This Map has keys that are Strings ( network_ids ) and values that are
     * networkviews.
     */
    public static Map getNetworkViewMap() {
        if (networkViewMap == null) {
            networkViewMap = new HashMap();
        }
        return networkViewMap;
    }

    /**
     * destroys the given network
     */
    public static void destroyNetwork(String network_id) {
        destroyNetwork((CyNetwork) getNetworkMap().get(network_id));
    }

    /**
     * destroys the given network
     */
    public static void destroyNetwork(CyNetwork network) {
        destroyNetwork(network, false);
    }

    /**
     * destroys the given network
     * 
     * @param network
     *            the network to be destroyed
     * @param destroy_unique
     *            if this is true, then all Nodes and Edges that are in this
     *            network, but no other are also destroyed.
     */
    public static void destroyNetwork(CyNetwork network, boolean destroy_unique) {

        String networkId = network.getIdentifier();

        firePropertyChange(NETWORK_DESTROYED, null, networkId);

        Map nmap = getNetworkMap();
        nmap.remove(networkId);
        if (networkId.equals(currentNetworkID)) {
            if (nmap.size() <= 0) {
                currentNetworkID = null;
            } else {
                // randomly pick a network to become the current network
                for (Iterator it = nmap.keySet().iterator(); it.hasNext();) {
                    currentNetworkID = (String) it.next();
                    break;
                }
            }
        }

        if (viewExists(networkId)) {
            destroyNetworkView(network);
        }
        if (destroy_unique) {

            ArrayList nodes = new ArrayList();
            ArrayList edges = new ArrayList();

            Collection networks = networkMap.values();

            Iterator nodes_i = network.nodesIterator();
            Iterator edges_i = network.edgesIterator();

            while (nodes_i.hasNext()) {
                Node node = (Node) nodes_i.next();
                boolean add = true;
                for (Iterator n_i = networks.iterator(); n_i.hasNext();) {
                    CyNetwork net = (CyNetwork) n_i.next();
                    if (net.containsNode(node)) {
                        add = false;
                        continue;
                    }
                }
                if (add) {
                    nodes.add(node);
                }
            }

            while (edges_i.hasNext()) {
                Edge edge = (Edge) edges_i.next();
                boolean add = true;
                for (Iterator n_i = networks.iterator(); n_i.hasNext();) {
                    CyNetwork net = (CyNetwork) n_i.next();
                    if (net.containsEdge(edge)) {
                        add = false;
                        continue;
                    }
                }
                if (add) {
                    edges.add(edge);
                }
            }

            /*
             * Bill Change
             */
//            for (Object n : nodes) {
//                getRootGraph().removeNode((Node) n);
//            }
//            for (Object e : edges) {
//                getRootGraph().removeEdge((Edge) e);
//            }
            /*
             * End Bill Change
             */
            getRootGraph().removeNodes(nodes);
            getRootGraph().removeEdges(edges);

        }

        // theoretically this should not be set to null till after the events
        // firing is done
        network = null;

        // updates the desktop - but only if the view is null
        // if a view exists, then the focus will have already been updated
        // in destroyNetworkView
        if (currentNetworkID != null && currentNetworkViewID == null) {
            getDesktop().setFocus(currentNetworkID);
        }
    }

    /**
     * destroys the networkview, including any layout information
     */
    public static void destroyNetworkView(CyNetworkView view) {

        // System.out.println( "destroying: "+view.getIdentifier()+" :
        // "+getNetworkViewMap().get( view.getIdentifier() ) );

        String viewID = view.getIdentifier();

        getNetworkViewMap().remove(viewID);

        if (viewID.equals(currentNetworkViewID)) {
            if (getNetworkViewMap().size() <= 0) {
                currentNetworkViewID = null;
            } else {
                // depending on which randomly chosen currentNetworkID we get, 
                // we may or may not have a view for it.
                CyNetworkView newCurr = (CyNetworkView) (getNetworkViewMap().get(currentNetworkID));
                if (newCurr != null) {
                    currentNetworkViewID = newCurr.getIdentifier();
                } else {
                    currentNetworkViewID = null;
                }
            }
        }

        // System.out.println( "gone from hash: "+view.getIdentifier()+" :
        // "+getNetworkViewMap().get( view.getIdentifier() ) );

        firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_DESTROYED, null, view);
        // theoretically this should not be set to null till after the events
        // firing is done
        view = null;

        // so that a network will be selected.
        if (currentNetworkID != null) {
            getDesktop().setFocus(currentNetworkID);
        }
    }

    /**
     * destroys the networkview, including any layout information
     */
    public static void destroyNetworkView(String network_view_id) {
        destroyNetworkView((CyNetworkView) getNetworkViewMap().get(
                network_view_id));
    }

    /**
     * destroys the networkview, including any layout information
     */
    public static void destroyNetworkView(CyNetwork network) {
        destroyNetworkView((CyNetworkView) getNetworkViewMap().get(
                network.getIdentifier()));
    }

    protected static void addNetwork(CyNetwork network, String title,
            CyNetwork parent, boolean create_view) {

        getNetworkMap().put(network.getIdentifier(), network);
        network.setTitle(title);
        String parentID = null;
        if (parent != null) {
            parentID = parent.getIdentifier();
        }
        firePropertyChange(NETWORK_CREATED, parentID, network.getIdentifier());
        if (network.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties().getProperty("viewThreshold")) && create_view) {
            createNetworkView(network);
        }
    }

    /**
     * Creates a new, empty Network.
     * 
     * @param title
     *            the title of the new network.
     */
    public static CyNetwork createNetwork(String title) {
        return createNetwork(new int[]{}, new int[]{}, title, null, true);
    }

    /**
     * Creates a new, empty Network.
     * 
     * @param title
     *            the title of the new network.
     * @param create_view
     *            if the size of the network is under the node limit, create a
     *            view
     */
    public static CyNetwork createNetwork(String title, boolean create_view) {
        return createNetwork(new int[]{}, new int[]{}, title, null,
                create_view);
    }

    /**
     * Creates a new, empty Network.
     * 
     * @param title
     *            the title of the new network.
     * @param create_view
     *            if the size of the network is under the node limit, create a
     *            view
     */
    public static CyNetwork createNetwork(String title, CyNetwork parent,
            boolean create_view) {
        return createNetwork(new int[]{}, new int[]{}, title, parent,
                create_view);
    }

    /**
     * Creates a new Network. A view will be created automatically.
     * 
     * @param nodes
     *            the indeces of nodes
     * @param edges
     *            the indeces of edges
     * @param title
     *            the title of the new network.
     */
    public static CyNetwork createNetwork(int[] nodes, int[] edges, String title) {
        return createNetwork(nodes, edges, title, null, true);
    }

    /**
     * Creates a new Network. A view will be created automatically.
     * 
     * @param nodes
     *            a collection of nodes
     * @param edges
     *            a collection of edges
     * @param title
     *            the title of the new network.
     */
    public static CyNetwork createNetwork(Collection nodes, Collection edges,
            String title) {
        return createNetwork(nodes, edges, title, null, true);
    }

    /**
     * Creates a new Network, that inherits from the given ParentNetwork. A view
     * will be created automatically.
     * 
     * @param nodes
     *            the indeces of nodes
     * @param edges
     *            the indeces of edges
     * @param child_title
     *            the title of the new network.
     * @param parent
     *            the parent of the this Network
     */
    public static CyNetwork createNetwork(int[] nodes, int[] edges,
            String child_title, CyNetwork parent) {
        return createNetwork(nodes, edges, child_title, parent, true);
    }

    /**
     * Creates a new Network, that inherits from the given ParentNetwork
     * 
     * @param nodes
     *            the indeces of nodes
     * @param edges
     *            the indeces of edges
     * @param child_title
     *            the title of the new network.
     * @param parent
     *            the parent of the this Network
     * @param create_view
     *            whether or not a view will be created
     */
    public static CyNetwork createNetwork(int[] nodes, int[] edges,
            String child_title, CyNetwork parent, boolean create_view) {
        CyNetwork network = getRootGraph().createNetwork(nodes, edges);
        addNetwork(network, child_title, parent, create_view);
        return network;
    }

    /**
     * Creates a new Network, that inherits from the given ParentNetwork. A view
     * will be created automatically.
     * 
     * @param nodes
     *            the indeces of nodes
     * @param edges
     *            the indeces of edges
     * @param parent
     *            the parent of the this Network
     */
    public static CyNetwork createNetwork(Collection nodes, Collection edges,
            String child_title, CyNetwork parent) {
        return createNetwork(nodes, edges, child_title, parent, true);
    }

    /**
     * Creates a new Network, that inherits from the given ParentNetwork.
     * 
     * @param nodes
     *            the indeces of nodes
     * @param edges
     *            the indeces of edges
     * @param parent
     *            the parent of the this Network
     * @param create_view
     *            whether or not a view will be created
     */
    public static CyNetwork createNetwork(Collection nodes, Collection edges,
            String child_title, CyNetwork parent, boolean create_view) {
        CyNetwork network = getRootGraph().createNetwork(nodes, edges);
        addNetwork(network, child_title, parent, create_view);
        return network;
    }

    /**
     * Creates a CyNetwork from a file. The file type is determined by the
     * suffix of the file.* Uses the new ImportHandler and thus the passed in
     * location should be a file of a recognized "Graph Nature". The "Nature" of
     * a file is a new way to tell what a file is beyond it's filetype e.g.
     * galFiltered.sif is, in addition to being a .sif file, the file is also of
     * Graph "Nature". Other files of Graph Nature include GML and XGMML. Beyond
     * Graph Nature there are Node, Edge, and Properties Nature.
     * 
     * A view will be created automatically.
     * 
     * @param location
     *            the location of the file
     */
    public static CyNetwork createNetworkFromFile(String location) {
        return createNetworkFromFile(location, true);
    }

    /**
     * Creates a CyNetwork from a file. The file type is determined by the
     * suffix of the file.* Uses the new ImportHandler and thus the passed in
     * location should be a file of a recognized "Graph Nature". The "Nature" of
     * a file is a new way to tell what a file is beyond it's filetype e.g.
     * galFiltered.sif is, in addition to being a .sif file, the file is also of
     * Graph "Nature". Other files of Graph Nature include GML and XGMML. Beyond
     * Graph Nature there are Node, Edge, and Properties Nature.
     * 
     * @param loc
     *            location of importable file
     * @param create_view
     *            whether or not a view will be created
     * @return a network based on the specified file or null if the file type is
     *         supported but the file is not of Graph Nature.
     */
    public static CyNetwork createNetworkFromFile(String loc,
            boolean create_view) {
        return createNetwork(importHandler.getReader(loc), create_view, null);
    }

    /**
     * Creates a CyNetwork from a URL. The file type is determined by the
     * suffix of the file or, if one does't exist, the contentType of the data.
     * Uses the new ImportHandler and thus the passed in
     * location should be a file of a recognized "Graph Nature". The "Nature" of
     * a file is a new way to tell what a file is beyond it's filetype e.g.
     * galFiltered.sif is, in addition to being a .sif file, the file is also of
     * Graph "Nature". Other files of Graph Nature include GML and XGMML. Beyond
     * Graph Nature there are Node, Edge, and Properties Nature.
     * 
     * @param url
     *            url of importable file
     * @param create_view
     *            whether or not a view will be created
     * @return a network based on the specified file or null if the file type is
     *         supported but the file is not of Graph Nature.
     */
    public static CyNetwork createNetworkFromURL(URL url, boolean create_view) {
        return createNetwork(importHandler.getReader(url), create_view, null);
    }

    /**
     * Creates a cytoscape.data.CyNetwork from a file. The passed variable
     * determines the type of file, i.e. GML, SIF, SBML, etc.
     * <p>
     * This operation may take a long time to complete. It is a good idea NOT to
     * call this method from the AWT event handling thread.
     * 
     * A view will be created automatically.
     * 
     * @param location
     *            the location of the file
     * @param file_type
     *            the type of file GML, SIF, SBML, etc.
     * @param canonicalize
     *            this will set the preferred display name to what is on the
     *            server.
     * @param biodataserver
     *            provides the name conversion service
     * @param species
     *            the species used by the BioDataServer
     * 
     * @deprecated It will be removed in April 2007 Use CyNetwork
     *             createNetworkFromFile(String location, boolean create_view)
     *             instead. File type is no longer needed as ImportHandler now
     *             manages all file types.
     */
    public static CyNetwork createNetwork(String location, int file_type,
            boolean canonicalize, BioDataServer biodataserver, String species) {
        return createNetworkFromFile(location, true);
    }

    /**
     * Creates a cytoscape.data.CyNetwork from a reader. Neccesary with
     * cesssions.
     * <p>
     * This operation may take a long time to complete. It is a good idea NOT to
     * call this method from the AWT event handling thread. This operation
     * assumes the reader is of type .xgmml since this should only be called by
     * the cessions reader which opens .xgmml files from the zipped cytoscape
     * session.
     * 
     * @param reader
     *            the graphreader that will read in the network
     * @param create_view
     *            whether or not a view will be created
     */
    public static CyNetwork createNetwork(final GraphReader reader,
            final boolean create_view, final CyNetwork parent) {

        if (reader == null) {
            throw new RuntimeException("Couldn't read specified file.");
        }

        // have the GraphReader read the given file

        // Explanation for code below: the code below recasts an IOException
        // into a RuntimeException, so that the exception can still be thrown
        // without having to change the method signature. This is less than
        // ideal, but the only sure way to ensure API stability for plugins.
        try {
            reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final String title = reader.getNetworkName();

        // get the RootGraph indices of the nodes and
        // edges that were just created
        final int[] nodes = reader.getNodeIndicesArray();
        final int[] edges = reader.getEdgeIndicesArray();

        if (nodes == null) {
            System.err.println("reader returned null nodes");
        }
        if (edges == null) {
            System.err.println("reader returned null edges");
        }

        final CyNetwork network = getRootGraph().createNetwork(nodes, edges);
        network.putClientData(READER_CLIENT_KEY, reader);
        addNetwork(network, title, parent, create_view);

        // Execute any necessary post-processing.
        reader.doPostProcessing(network);
        return network;
    }

    // --------------------//
    // Network Data Methods
    // --------------------//
    /**
     * Gets Global Node Attributes.
     * 
     * @return CyAttributes Object.
     */
    public static CyAttributes getNodeAttributes() {
        return nodeAttributes;
    }

    /**
     * Gets Global Edge Attributes
     * 
     * @return CyAttributes Object.
     */
    public static CyAttributes getEdgeAttributes() {
        return edgeAttributes;
    }

    /*
     * Bill Change
     */
    public static void resetNodeAttributes() {
        nodeAttributes = new CyAttributesImpl();
    }

    public static void resetEdgeAttributes() {
        edgeAttributes =  new CyAttributesImpl();
    }

    /*
     * End Bill Change
     */
    /**
     * Gets Global Network Attributes.
     * 
     * @return CyAttributes Object.
     */
    public static CyAttributes getNetworkAttributes() {
        return networkAttributes;
    }

    /**
     * Gets Global Network Attributes.
     * 
     * @return CyAttributes Object.
     */
    public static CyAttributes getOntologyAttributes() {
        return ontologyAttributes;
    }

    public static ExpressionData getExpressionData() {
        return expressionData;
    }

    public static void setExpressionData(ExpressionData expData) {
        expressionData = expData;
    }

    /**
     * Load Expression Data
     */
    // TODO: remove the JOption Pane stuff
    public static boolean loadExpressionData(String filename, boolean copy_atts) {
        try {
            expressionData = new ExpressionData(filename);
        } catch (Exception e) {
            System.err.println("Unable to Load Expression Data");
            String errString = "Unable to load expression data from " + filename;
            String title = "Load Expression Data";

        }

        if (copy_atts) {
            expressionData.copyToAttribs(getNodeAttributes(), null);
            firePropertyChange(ATTRIBUTES_CHANGED, null, null);
        }

        // Fire off an EXPRESSION_DATA_LOADED event.
        Cytoscape.firePropertyChange(Cytoscape.EXPRESSION_DATA_LOADED, null,
                expressionData);

        return true;
    }

    /**
     * Loads Node and Edge attribute data into Cytoscape from the given file
     * locations. Currently, the only supported attribute types are of the type
     * "name = value".
     * 
     * @param nodeAttrLocations
     *            an array of node attribute file locations. May be null.
     * @param edgeAttrLocations
     *            an array of edge attribute file locations. May be null.
     * @param canonicalize
     *            convert to the preffered name on the biodataserver
     * @param bioDataServer
     *            provides the name conversion service
     * @param species
     *            the species to use with the bioDataServer's
     * @deprecated Use loadAttributes(nodeAttrLocations,edgeAttrLocations)
     *             instead. BioDataServer, canonicalize, and species are no
     *             longer used. Will be removed 10/2007.
     */
    public static void loadAttributes(String[] nodeAttrLocations,
            String[] edgeAttrLocations, boolean canonicalize,
            BioDataServer bioDataServer, String species) {
        loadAttributes(nodeAttrLocations, edgeAttrLocations);
    }

    /**
     * Loads Node and Edge attribute data into Cytoscape from the given file
     * locations. Currently, the only supported attribute types are of the type
     * "name = value".
     * 
     * @param nodeAttrLocations
     *            an array of node attribute file locations. May be null.
     * @param edgeAttrLocations
     *            an array of edge attribute file locations. May be null.
     */
    public static void loadAttributes(String[] nodeAttrLocations,
            String[] edgeAttrLocations) {
        // check to see if there are Node Attributes passed
        if (nodeAttrLocations != null) {
            for (int i = 0; i < nodeAttrLocations.length; ++i) {
                try {
                    InputStreamReader reader = new InputStreamReader(FileUtil.getInputStream(nodeAttrLocations[i]));
                    CyAttributesReader.loadAttributes(nodeAttributes, reader);
                    firePropertyChange(ATTRIBUTES_CHANGED, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException(
                            "Failure loading node attribute data: " + nodeAttrLocations[i] + "  because of:" + e.getMessage());
                }
            }
        }

        // Check to see if there are Edge Attributes Passed
        if (edgeAttrLocations != null) {
            for (int j = 0; j < edgeAttrLocations.length; ++j) {
                try {
                    InputStreamReader reader = new InputStreamReader(FileUtil.getInputStream(edgeAttrLocations[j]));
                    CyAttributesReader.loadAttributes(edgeAttributes, reader);
                    firePropertyChange(ATTRIBUTES_CHANGED, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException(
                            "Failure loading edge attribute data: " + edgeAttrLocations[j] + "  because of:" + e.getMessage());
                }
            }
        }

    }

    /**
     * A BioDataServer should be loadable from a file systems file or from a
     * URL.
     */
    public static BioDataServer loadBioDataServer(String location) {
        try {
            bioDataServer = new BioDataServer(location);
        } catch (Exception e) {
            System.err.println("Could not Load BioDataServer from: " + location);
            return null;
        }
        return bioDataServer;
    }

    /**
     * @return the BioDataServer that was loaded, should not be null, but not
     *         contain any data.
     */
    public static BioDataServer getBioDataServer() {
        return bioDataServer;
    }

    /**
     * This will replace the bioDataServer.
     */
    public static OntologyServer buildOntologyServer() {
        try {
            ontologyServer = new OntologyServer();
        } catch (Exception e) {
            System.err.println("Could not build OntologyServer.");
            e.printStackTrace();
            return null;
        }
        return ontologyServer;
    }

    public static OntologyServer getOntologyServer() {
        return ontologyServer;
    }

    // ------------------------------//
    // CyNetworkView Creation Methods
    // ------------------------------//
    /**
     * Creates a CyNetworkView, but doesn't do anything with it. Ifnn's you want
     * to use it
     * 
     * @link {CytoscapeDesktop}
     * @param network
     *            the network to create a view of
     */
    public static CyNetworkView createNetworkView(CyNetwork network) {
        return createNetworkView(network, network.getTitle());
    }

    /**
     * Creates a CyNetworkView, but doesn't do anything with it. Ifnn's you want
     * to use it
     * 
     * @link {CytoscapeDesktop}
     * @param network
     *            the network to create a view of
     */
    public static CyNetworkView createNetworkView(CyNetwork network,
            String title) {

        if (network == nullNetwork) {
            return nullNetworkView;
        }
        if (viewExists(network.getIdentifier())) {
            return getNetworkView(network.getIdentifier());
        }
        final DingNetworkView view = new DingNetworkView(network, title);
        view.setGraphLOD(new CyGraphLOD());

        view.setIdentifier(network.getIdentifier());
        getNetworkViewMap().put(network.getIdentifier(), view);
        view.setTitle(network.getTitle());

        setSelectionMode(currentSelectionMode, view);

        firePropertyChange(
                cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, null,
                view);

        if (network.getClientData(READER_CLIENT_KEY) != null) {
            ((GraphReader) network.getClientData(READER_CLIENT_KEY)).layout(Cytoscape.getNetworkView(network.getIdentifier()));
        } else {
            // TODO
            // This creates the default square layout.
            // This code should not be here, rather it should
            // be somewhere in the cytoscape.layout package.
            // This change should be made when we add the
            // layoutbroker code in 2.5.
            // When you move this code, don't forget to update
            // cytoscape.data.readers.AbstractGraphReader.doLayout()
            // as well.
            double distanceBetweenNodes = 80.0d;
            int columns = (int) Math.sqrt(view.nodeCount());
            Iterator nodeViews = view.getNodeViewsIterator();
            double currX = 0.0d;
            double currY = 0.0d;
            int count = 0;
            while (nodeViews.hasNext()) {
                NodeView nView = (NodeView) nodeViews.next();
                nView.setOffset(currX, currY);
                count++;
                if (count == columns) {
                    count = 0;
                    currX = 0.0d;
                    currY += distanceBetweenNodes;
                } else {
                    currX += distanceBetweenNodes;
                }
            }
        }

        getCurrentNetworkView().fitContent();

        return view;
    }

    public static void firePropertyChange(String property_type,
            Object old_value, Object new_value) {

        PropertyChangeEvent e = new PropertyChangeEvent(pcsO, property_type,
                old_value, new_value);
        // System.out.println("Cytoscape FIRING : " + property_type);

        getSwingPropertyChangeSupport().firePropertyChange(e);
        getPropertyChangeSupport().firePropertyChange(e);
    }

    /**
     * Gets the selection mode value.
     */
    public static int getSelectionMode() {
        return currentSelectionMode;
    }

    /**
     * Sets the specified selection mode on all views.
     * 
     * @param selectionMode
     *            SELECT_NODES_ONLY, SELECT_EDGES_ONLY, or
     *            SELECT_NODES_AND_EDGES.
     */
    public static void setSelectionMode(int selectionMode) {

        // set the selection mode on all the views
        GraphView view;
        String network_id;
        Map networkViewMap = getNetworkViewMap();
        for (Iterator iter = networkViewMap.keySet().iterator(); iter.hasNext();) {
            network_id = (String) iter.next();
            view = (GraphView) networkViewMap.get(network_id);
            setSelectionMode(selectionMode, view);
        }

        // update the global indicating the selection mode
        currentSelectionMode = selectionMode;

    }

    /**
     * Utility method to set the selection mode on the specified GraphView.
     * 
     * @param selectionMode
     *            SELECT_NODES_ONLY, SELECT_EDGES_ONLY, or
     *            SELECT_NODES_AND_EDGES.
     * @param view
     *            the GraphView to set the selection mode on.
     */
    public static void setSelectionMode(int selectionMode, GraphView view) {

        // first, disable node and edge selection on the view
        view.disableNodeSelection();
        view.disableEdgeSelection();

        // then, based on selection mode, enable node and/or edge selection
        switch (selectionMode) {

            case SELECT_NODES_ONLY:
                view.enableNodeSelection();
                break;

            case SELECT_EDGES_ONLY:
                view.enableEdgeSelection();
                break;

            case SELECT_NODES_AND_EDGES:
                view.enableNodeSelection();
                view.enableEdgeSelection();
                break;

        }

    }

    /**
     * Get name of the current session file.
     * 
     * @return current session file name
     */
    public static String getCurrentSessionFileName() {
        return currentSessionFileName;
    }

    /**
     * Set the current session name.
     * 
     * @param newName
     */
    public static void setCurrentSessionFileName(String newName) {
        currentSessionFileName = newName;
    }

    public static void setSessionState(int state) {
        sessionState = state;
    }

    public static int getSessionstate() {
        return sessionState;
    }

    /**
     * Clear all networks and attributes and start a new session.
     */
    public static void createNewSession() {
        Set netSet = getNetworkSet();
        Iterator it = netSet.iterator();

        while (it.hasNext()) {
            CyNetwork net = (CyNetwork) it.next();
            destroyNetwork(net);
        }

        // Clear node attributes
        CyAttributes nodeAttributes = getNodeAttributes();
        String[] nodeAttrNames = nodeAttributes.getAttributeNames();
        for (int i = 0; i < nodeAttrNames.length; i++) {
            nodeAttributes.deleteAttribute(nodeAttrNames[i]);
        }

        // Clear edge attributes
        CyAttributes edgeAttributes = getEdgeAttributes();
        String[] edgeAttrNames = edgeAttributes.getAttributeNames();
        for (int i = 0; i < edgeAttrNames.length; i++) {
            edgeAttributes.deleteAttribute(edgeAttrNames[i]);
        }

        // Clear network attributes
        CyAttributes networkAttributes = getNetworkAttributes();
        String[] networkAttrNames = networkAttributes.getAttributeNames();
        for (int i = 0; i < networkAttrNames.length; i++) {
            networkAttributes.deleteAttribute(networkAttrNames[i]);
        }

        setCurrentSessionFileName(null);
    }

    public static String getOntologyRootID() {
        return ontologyRootID;
    }

    public static void setOntologyRootID(String id) {
        ontologyRootID = id;
    }

    public static Bookmarks getBookmarks() throws JAXBException, IOException {
        if (bookmarks == null) {
            BookmarkReader reader = new BookmarkReader();
            reader.readBookmarks();
            bookmarks = reader.getBookmarks();
        }
        return bookmarks;
    }

    public static void setBookmarks(Bookmarks pBookmarks) {
        bookmarks = pBookmarks;
    }
}

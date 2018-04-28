package lotrec.dataStructure.graph;

import java.util.Vector;
import java.util.Enumeration;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.process.Dispatcher;
import lotrec.process.ProcessListener;
import lotrec.process.ProcessEvent;
import lotrec.util.Duplicator;
import lotrec.util.CommonDuplicator;

/**
Contains graphes, that's all !
@see Graph
@author David Fauthoux
 */
public class Wallet {

    /**
    name given to wallets created with empty constructor
     */
    public static String defaultName = "wallet";
//  private static int forName = 0;
    private String name;
    private Vector<Graph> graphes;
    private Dispatcher dispatcher;

    /**
    Creates an empty wallet with a default name. The default name is computed as follow : <code>defaultName</code> + an incremented int to distinguish wallets.
     */
    public Wallet() {
        this(defaultName);
//    this(defaultName + forName);
//    forName++;
    }

    /**
    Creates an empty wallet
    @param name the name of this wallet
     */
    public Wallet(String name) {
        this.name = name;
        graphes = new Vector();
        dispatcher = new Dispatcher();
    }

    /**
    Sets the name of this wallet
    @param name the name of this wallet
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
    Returns the name of this wallet
    @return the name of this wallet
     */
    public String getName() {
        return name;
    }

    public Graph getGraph(String name) {
        for (Graph g : getGraphes()) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }

    /**
    Adds the specified listener to receive process event from this wallet
    @param listener the listener to add
     */
    public void addProcessListener(ProcessListener listener) {
        dispatcher.addProcessListener(listener);
    }

    /**
    Removes the specified listener so it no longer receives process events from this wallet
    @param listener the listener to remove
     */
    public void removeProcessListener(ProcessListener listener) {
        dispatcher.removeProcessListener(listener);
    }

    /**
    Adds the graph to this wallet.
    Sends a <code>GraphEvent</code>
    @param graph the graph to add
     */
    public void add(Graph graph) {
        graph.setWallet(this);
        graphes.add(graph);
        dispatcher.process(new GraphEvent(this, GraphEvent.ADDED, graph));
        graph.addProcessListener(dispatcher);
    }

    /**
    Removes the graph from this wallet.
    Sends a <code>GraphEvent</code>
    @param graph the graph to remove
    @return true if this wallet contains the graph, false otherwise
     */
    public boolean remove(Graph graph) {
        if (!graphes.contains(graph)) {
            return false;
        }
        graphes.remove(graph);
        dispatcher.process(new GraphEvent(this, GraphEvent.REMOVED, graph));
        return true;
    }

    /**
    Returns the contained graphes
    @return the contained graphes
     */
    public Enumeration getGraphesEnum() {
        return graphes.elements();
    }

    public Vector<Graph> getGraphes() {
        return graphes;
    }

    public Vector<Tableau> getGraphesAsTableaux() {
        Vector<Tableau> tableauList = new Vector<Tableau>();
        for(Graph g : graphes){
            tableauList.add((Tableau)g);
        }
        return tableauList;
    }

//    public Graph getTableau(String name) {
//        Tableau t;
//        for (Graph g : graphes) {
//            if (g instanceof Tableau) {
//                t = (Tableau) g;
//                if (t.getName().equals(name)) {
//                    return t;
//                }
//            }
//        }
//        return null;
//    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * 
     * @return true if the Wallet has at least one open Tableau.
     */
    public boolean hasOpenTableau() {
        for (int i = 0; i < graphes.size(); i++) {
            if (!((Tableau) graphes.get(i)).isClosed()) {
                return true;
            }
        }
        return false;
    }

    // main
    /**
    Tests the package.
     */
    public static void main(String[] args) {
        Wallet wallet = new Wallet();
        wallet.addProcessListener(new ProcessListener() {

            @Override
            public void process(ProcessEvent event) {
                System.out.println("event : " + event);
            }
        });

        Graph graph = new Graph();
        wallet.add(graph);

        Node n0 = new Node();
        Node n1 = new Node();
        graph.add(n0);
        graph.add(n1);
        Edge edge = new Edge(n0, n1);

        n0.link(edge);

        Duplicator duplicator = new CommonDuplicator();
        Graph duplicatedGraph = (Graph) graph.duplicate(duplicator);
        try {
            duplicatedGraph.completeDuplication(duplicator);
            duplicatedGraph.translateDuplication(duplicator);
        } catch (Exception exception) {
            System.out.println(exception);
            return;
        }
        wallet.add(duplicatedGraph);

        System.out.println("Wallet : " + wallet);
        for (Enumeration enumr = wallet.getGraphesEnum(); enumr.hasMoreElements();) {
            Graph g = (Graph) enumr.nextElement();
            System.out.println(" . Graph : " + g);
            for (Enumeration enum_ = g.getNodesEnumeration(); enum_.hasMoreElements();) {
                Node n = (Node) enum_.nextElement();
                System.out.println("   . Node : " + n);
                for (Enumeration enum__ = n.getNextEdgesEnum(); enum__.hasMoreElements();) {
                    Edge e = (Edge) enum__.nextElement();
                    System.out.println("     . Edge next : " + e);
                }
                for (Enumeration enum__ = n.getLastEdgesEnum(); enum__.hasMoreElements();) {
                    Edge e = (Edge) enum__.nextElement();
                    System.out.println("     . Edge last : " + e);
                }
            }
        }
    }
}

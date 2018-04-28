package lotrec.dataStructure.tableau.action;

import java.io.Serializable;
import lotrec.process.AbstractAction;
import lotrec.dataStructure.expression.*;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import java.util.Set;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.tableau.*;
import lotrec.engine.Engine;
import lotrec.process.EventMachine;

/**
 * When applied, duplicates a tableau containing a specified node.
 * This class can reference duplicated concrete objects (nodes, edges...) in the returned instance set.
 * @author David Fauthoux
 */
public class DuplicateAction extends AbstractAction implements Serializable {

    public static Tableau initialRelatedTableau;
//    private SchemeVariable sourceNodeScheme;
    private String duplictataMark;
//    private Vector<Couple> schemes;
    public static int stepCount,  count = 1; //these two variables are for controlled wide duplication

    /**
    Creates an action which will duplicate the tableau containing the specified node.
    User only specifies the scheme representing this node and this class will find it in the instance set (modifier), when <i>apply</i> method is called.
    @param sourceNodeScheme the scheme representing the node to get the tableau to duplicate
    @param strategy the global strategy where the duplicated tableau strategy will be put, in order to be executed
     */
//    public DuplicateAction(SchemeVariable sourceNodeScheme) {
//        super();
//        this.sourceNodeScheme = sourceNodeScheme;
//        schemes = new Vector();
//        stepCount = -1;
//    }
    /*
     * The old constructor is above
     * New one defined below, reflects the new way defined into the XML logic files
     * At the time the definition was:  duplicate node0 begin 
     *                                                     node0 node1
     *                                                     node0 node2
     *                                                     ...
     *                                                   end
     * Now, it's simply: duplicate node0 node1
     * cause this is always the case!! (for the best of my knowledge) 
     */
    @ParametersTypes(types = {"mark"})
    @ParametersDescriptions(descriptions = {"A mark to designate the duplicata premodel." +
    "It is used as prefix to nodes identifiers to distinguish the (equivalent) nodes in the current premodel and its duplicata." +
            "For example, if w and u are instantiated with nodes from the current premodel in the conditions," +
            "then the action \"duplicate premodel_copy\" is performed, thus premodel_copy.w and premodel_copy.u" +
            "designate respectively the nodes w and u in premodel_copy."})
    public DuplicateAction(String duplictataScheme) {
        super();
//        this.sourceNodeScheme = sourceNodeScheme;
        this.duplictataMark = duplictataScheme;
//        schemes = new Vector<Couple>();
        //add(sourceNodeScheme, duplictataScheme);
        stepCount = -1;
    }

//    @ParametersTypes(types = {"node", "mark"})
//    @ParametersDescriptions(descriptions = {"The node whose tableau will be duplicated. It should be already instanciated by other conditions or created by other actions",
//        "The mark that is used as prefix to nodes name to distinguish the equivalent nodes in the duplicata tableau."})
//    public DuplicateAction(SchemeVariable sourceNodeScheme, String duplictataScheme) {
//        super();
//        this.sourceNodeScheme = sourceNodeScheme;
//        this.duplictataMark = duplictataScheme;
////        schemes = new Vector<Couple>();
//        //add(sourceNodeScheme, duplictataScheme);
//        stepCount = -1;
//    }

    public static void setStepCount(int sCount) {
        stepCount = sCount;
    }

    /**
    Adds the link between a scheme and its destination scheme, so, when the action is applied, the returned instance set will be able to reference the duplicated objects with the destination schemes
    @param sourceScheme the scheme referencing the object in the source tableau
    @param destinationScheme the scheme referencing the object in the duplicated tableau
     */
//    public void add(SchemeVariable sourceScheme, SchemeVariable destinationScheme) {
//        schemes.add(new Couple(sourceScheme, destinationScheme));
//    }
    /**
    Finds the concrete node in the modifier, represented by sourceNodeScheme in the constructor, to get its tableau; and duplicates the tableau.
    Finally, adds the representation of the duplicated objects to the instance set (modifier) and returns it.
    <p>To have a reference in the returned instance set to the duplicated object, use the <i>add</i> method.
    @param modifier the instance set used in the restriction process
    @return the instance set completed with the destination schemes
     */
    @Override
    public Object apply(EventMachine em, Object modifier) {
//        Engine engine = currentTableau.getStrategy().getEngine();
//        synchronized (engine) {
//            if (engine.shouldPause()) {
//                engine.makePause();
//                while (engine.shouldPause()) {
//                    try {
//                        engine.wait();
//                    } catch (InterruptedException ex) {
//                        System.err.println(ex.getMessage());
//                    }
//                }
//                engine.makeResume();
//            }
//            if (engine.shouldStop()) {
//                return modifier;
//            }
//
//        //Discativated when benchmarking
//        //------------------------------
////            if (engine.getEngineTimer().getCurrentElapsedTime() > 100000){
//////                    && engine.getEngineTimer().getCurrentElapsedTime() < 100100) { //this can be activated during benchmarking to allow to continue after 100 seconds
////                    engine.getMainFrame().getTableauxPanel().makePause();
////            }
//        //------------------------------
//        }

        count++;
        InstanceSet instanceSet = (InstanceSet) modifier;
//        TableauNode n = (TableauNode) instanceSet.get(sourceNodeScheme);
//        if (n == null) {
//            throw new ProcessException(toString() + " : cannot apply action without instance for node");
//        }


//        Tableau tableau = (Tableau) n.getGraph();
        Duplicator duplicator = em.getRelatedTableau().duplicateAsItMustBeDone();

        try {
//            System.out.println("Before the duplication: " + tableau.getStrategy().getEngine().getStrategiesListInfo());
            if (stepCount > -1) {
                if (count <= stepCount) {
                    em.getRelatedTableau().getStrategy().getEngine().add(((Tableau) duplicator.getImage(em.getRelatedTableau())).getStrategy());
//                    System.out.println("A duplictaion has been done: ");
                }
            } else {
                em.getRelatedTableau().getStrategy().getEngine().add(((Tableau) duplicator.getImage(em.getRelatedTableau())).getStrategy());
//                System.out.println("_____________________________________________________________________________________");
//                System.out.println("A duplictaion has been done.. Tableau \"" + tableau.getName() + "\" was duplicated in tableau \"" + ((Tableau) duplicator.getImage(tableau)).getName() + "\"");
                //((Tableau) duplicator.getImage(tableau)).setDuplicationParent(tableau);
                ((Tableau) duplicator.getImage(em.getRelatedTableau())).setDuplicationInitialParent(initialRelatedTableau);
//                System.out.println("Initial duplicated tableau is \"" + initialRelatedTableau.getName() + "\"");
//                System.out.println("_____________________________________________________________________________________");
//                System.out.println("After the duplication: " + tableau.getStrategy().getEngine().getStrategiesListInfo());
            }
        } catch (DuplicateException exception) {
            System.err.println("Duplicate action cannot find the duplicated strategy !");
            System.err.println(exception);
        }

        Set<SchemeVariable> schemesOfTab = instanceSet.getTable().keySet();
        for (SchemeVariable scheme : schemesOfTab) {
            if (scheme instanceof StringSchemeVariable) {
                Object schemeInstanceInTab = instanceSet.get(scheme);
                //if (schemeInstanceInTab instanceof TableauNode) {
                try {
                    Object schemeDuplicataInstanceInTabDuplicata = duplicator.getImage(schemeInstanceInTab);
                    instanceSet = instanceSet.plus(new StringSchemeVariable(duplictataMark + "." + scheme.toString()), schemeDuplicataInstanceInTabDuplicata);
//                    System.out.println("Scheme [" + scheme + "] should have as duplicata [" + duplictataMark + "." + scheme.toString() + "]");
                } catch (DuplicateException exception) {
                    // It is normal to have an error in case of two duplications
                    // Debug with the LogicTest file "Non-deterministic-choices" with the rule "Or_V3"
                    System.err.println("Duplicate action cannot reference the required object (" + schemeInstanceInTab + "):");
                    System.err.println(exception);
                }
            }
        }

//        for (Couple c : schemes) {
//            Object nodeInstanceInTab = instanceSet.get(c.nodeInTab);
//            try {
//                Object nodeDuplicataInstanceInTabDuplicata = duplicator.getImage(nodeInstanceInTab);
//                instanceSet = instanceSet.plus(c.nodeDuplicataInTabDuplicata, nodeDuplicataInstanceInTabDuplicata);
//            } catch (DuplicateException exception) {
//                System.err.println("Duplicate action cannot reference the required object (" + nodeInstanceInTab + ") : " + exception);
//            }
//        }
        return instanceSet;
    }

//    private class Couple implements Serializable {
//
//        public SchemeVariable nodeInTab;
//        public SchemeVariable nodeDuplicataInTabDuplicata;
//
//        public Couple(SchemeVariable source, SchemeVariable destination) {
//            this.nodeInTab = source;
//            this.nodeDuplicataInTabDuplicata = destination;
//        }
//    }
}

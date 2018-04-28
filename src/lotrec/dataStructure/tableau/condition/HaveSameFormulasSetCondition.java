package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
 * Tests wether two nodes have the same set of formulas, 
 * without taking into account their marks or their formulas marks 
 * or any other properties not metioned here..
 * @author Bilal Said
 */
public class HaveSameFormulasSetCondition extends AbstractCondition {

    private SchemeVariable node1;
    private SchemeVariable node2;

    /**
    Creates a contains constraint condition, ready to deliver knowledge about the corresponding restriction
    This conditions aims to test the double inclusion of the set of formulas of two nodes N0 and N1.
    @param node1 the scheme representing the node N0
    @param node2 the scheme representing the node N1
     */
    @ParametersTypes(types = {"node", "node"})
    @ParametersDescriptions(descriptions = {"The (bigger) node that should be verified if it contains the second \"node\"",
        "The (smaller) node that should be verified as contained in the first \"node\""
    })
    public HaveSameFormulasSetCondition(SchemeVariable node1, SchemeVariable node2) {
        super();
        this.node1 = node1;
        this.node2 = node2;
    }
    
    /**
     * this condition cannot establish the rule instantiation, i.e. it does not respond to events, thus it has no activator
     * Other Conditions should start before it to instantiate its node Schemes 
     * @return null
     */
    public BasicActivator createActivator() { 
        return null;
    }

    /**
     * Since no activator, there is no activation Scheme
     * @return null
     */
    public Vector getActivationSchemes() {
        return null;
    }

    public Restriction createRestriction() {
        return new HaveSameFormulasSetMatch(node1, node2);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(node1) && entry.contains(node2)) {
            return entry;
        } else {
            return null;
        }
    }
}




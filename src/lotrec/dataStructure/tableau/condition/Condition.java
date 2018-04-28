package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.process.*;

/**
Delivers knowledge about the corresponding activator and restriction.
Implementing class are used with the <code>Rule</code> class.
The implementing classes are only useful class in order to minimize the user work.
@see Rule
@author David Fauthoux
 */
public interface Condition extends java.io.Serializable {

    /**
     *  Creates a corresponding activator.
     *  Usually, the activator class has the same name of the condition, not followed "Condition", but followed by "Activator".
     * 
     * @return a new instance of the corresponding class, null if this class cannot describe an activator
     */
    public BasicActivator createActivator();

    /**
    Returns a set of schemes : these schemes are those created when an event is received (in the corresponding activator class) , using the <i>createModifier</i> method.
    Useful to correctly order the chain of restrictions.
     * Useful to correctly establish the chain of restrictions ordering.
    @return the set of known schemes when an event is received, null if this class cannot describe an activator
     */
    public Vector getActivationSchemes();

    /**
     *  Creates a corresponding restriction.
     *  Usually, the restriction class has the same name of the condition, not followed "Condition", but followed by "Restriction", "Constraint" or "Match".
     * 
     * @return a new instance of the corresponding class, null if this class cannot describe a restriction
     */
    public Restriction createRestriction();

    /**
    Completes the set of schemes with the new known schemes :
    This method simulates this corresponding restriction class behaviour (if this class will complete the modifier with any schemes when it receive a modifier with such schemes (entry), it puts the completion in the collection)
    Useful to correctly order the restriction chain.
    @param entry the set of schemes on which the method must simulates this class behaviour
    @return the completed collection (with the added schemes in a simulate behaviour) if the behaviour can be simulated, null if this class forbids the call with such set of schemes. 
     */
    public Vector updateSchemes(Vector entry);
}

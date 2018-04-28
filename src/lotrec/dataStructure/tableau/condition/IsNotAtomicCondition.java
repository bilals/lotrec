package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
 * Tests wether a given formula represents an atomic proposition
 * It should be a VariableExpression or a ConstantExpression 
 * (i.e. not a ExpressionWithSubExpression)
 * 
 * Note: this condition is diffrent than HasProposition(node,formula) 
 * which tests if the "formula" is bieng an atomic proposition 
 * BUT tests also if it exists in "node"
 * @author Bilal Said
 */

public class IsNotAtomicCondition extends AbstractCondition {

   // private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an expression condition, ready to deliver knowledge about the corresponding activator and restriction
    @param nodeScheme the scheme representing the node where the expression is added
    @param expressionScheme the scheme representing the added expression
     */
    // node should be omitted!! it is not necessary
    @ParametersTypes(types = {"formula"})//"node",
    @ParametersDescriptions(descriptions = {//"The node where \"formula\" exists (will be omitted in late versions)",
        "The (variable) formula that is to be tested if it is a proposition"
    })
    public IsNotAtomicCondition(Expression expressionScheme) {//SchemeVariable nodeScheme,
        super();
        //this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }
    /**
     * this condition cannot establish the rule instantiation, i.e. it does not respond to events, thus it has no activator
     * Other Conditions should start before it to instantiate its nodeScheme and expressionScheme
     * @return null
     */
    @Override
    public BasicActivator createActivator() {
        return null;
    }

    @Override
    public Vector getActivationSchemes() {
        // 
        return null;
    }

    @Override
    public Restriction createRestriction() {
        return new IsNotAtomicMatch(expressionScheme); // where the tests is really done
    }

    @Override
    public Vector updateSchemes(Vector entry) { // it does not instantiate new variables
        // it is true that I do not instantiate new variables here,
        // but it should return null when it does not find its expressionScheme
        // in order to tell the rule compilation that it should be put later in the restriction chain,
        // this will guarantee that it is ordered after the restriction instantiating its expressionSceme
        // Bilal: 3 July 2009,  bug reported by Marcio Moretto Ribeiro
        if (entry.contains(expressionScheme)) {
            return entry;
        } else {
            return null;
        }
//    return entry;
    }
}

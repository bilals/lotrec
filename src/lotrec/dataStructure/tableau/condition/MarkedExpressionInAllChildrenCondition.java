package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.Rule;
import lotrec.process.*;

/**
Delivers knowledge about the contains constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see MarkConstraint
@author David Fauthoux
 */
public class MarkedExpressionInAllChildrenCondition extends AbstractCondition {

    private SchemeVariable parentNodeScheme;
    private Expression markedExpression,  relation;
    private Object marker;

    /**
    Creates a contains constraint condition, ready to deliver knowledge about the corresponding restriction
    The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
    @param bigNodeScheme the scheme representing the node N0
    @param smallNodeScheme the scheme representing the node N1
     */
    @ParametersTypes(types = {"node", "formula", "relation", "mark"})
    @ParametersDescriptions(descriptions = {"The node which all its children linked to it by \"relation\" have \"formula\" marked by \"mark\"",
        "The formula that should be in all children of \"node\" linked to it by \"relation\" and should be marked by \"mark\"",
        "The relation label of the links between the \"node\" and its children",
        "The mark annotating the \"formula\" in all children of \"node\" linked to it by \"relation\" "
    })
    public MarkedExpressionInAllChildrenCondition(SchemeVariable nodeScheme, Expression markedExpression, Expression relation, Object marker) {
        this.parentNodeScheme = nodeScheme;
        this.markedExpression = markedExpression;
        this.relation = relation;
        this.marker = marker;
    }

    /**
    Returns null
    @return null
     */
    public BasicActivator createActivator() {
        //ici je ne passe pas la relation comme
//parametre et ça a comme effet que si une
//expression a été marquée dans un noeud
//successeur sans prend en compte la relation elle active
//la règle.
        return new MarkedExpressionInAllChildrenActivator(parentNodeScheme, markedExpression, relation, marker);
    }

    /**
    Returns null
    @return null
     */
    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(parentNodeScheme);
        return v;
    }

    public Restriction createRestriction() {
        return new MarkedExpressionInAllChildrenMatch(parentNodeScheme, markedExpression, relation, marker);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(parentNodeScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}




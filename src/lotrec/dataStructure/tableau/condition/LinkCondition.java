package lotrec.dataStructure.tableau.condition;

import java.util.*;
import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the link construction : the activator, the restriction and how they works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see LinkActivator
@see LinkMatch
@author David Fauthoux
 */
public class LinkCondition extends AbstractCondition {

    private SchemeVariable nodeFromScheme;
    private SchemeVariable nodeToScheme;
    private Expression relationScheme;

    /**
    Creates a link condition, ready to deliver knowledge about the corresponding activator and restriction
    @param nodeFromScheme the scheme representing the source node of the link
    @param nodeToScheme the scheme representing the destination node of the link
    @param relationScheme the scheme representing the relation of the edge between the two nodes
     */
    @ParametersTypes(types = {"node", "node", "relation"})
    @ParametersDescriptions(descriptions = {"The (source) node to be tested if it is linked to the other \"node\" parameter by the \"relation\" parameter",
        "The (destination) node to be tested if it is linked to the first \"node\" parameter by the \"relation\" parameter",
        "Relation's expression to be teseted as labeling the relation's link between the two \"node\" parameters"
    })
    public LinkCondition(SchemeVariable nodeFromScheme, SchemeVariable nodeToScheme, Expression relationScheme) {
        super();
        this.nodeFromScheme = nodeFromScheme;
        this.nodeToScheme = nodeToScheme;
        this.relationScheme = relationScheme;
    }

    @Override
    public BasicActivator createActivator() {
        return new LinkActivator(nodeFromScheme, nodeToScheme, relationScheme);
    }

    @Override
    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeFromScheme);
        v.add(nodeToScheme);
        //relation osf
        return v;
    }

    @Override
    public Restriction createRestriction() {
        return new LinkMatch(nodeFromScheme, nodeToScheme, relationScheme);
    }

    @Override
    public Vector updateSchemes(Vector entry) {
        if (entry.contains(nodeToScheme)) {
            if (!entry.contains(nodeFromScheme)) {
                Vector v = (Vector) entry.clone();
                v.add(nodeFromScheme);
                for (Expression variable : relationScheme.getVariableExpressions()) {
                    v.add(variable);
                }
                return v;
            }
            for (Expression variable : relationScheme.getVariableExpressions()) {
                entry.add(variable);
            }
            return entry;
        }
        if (entry.contains(nodeFromScheme)) {
            if (!entry.contains(nodeToScheme)) {
                Vector v = (Vector) entry.clone();
                v.add(nodeToScheme);
                for (Expression variable : relationScheme.getVariableExpressions()) {
                    v.add(variable);
                }
                return v;
            }
            for (Expression variable : relationScheme.getVariableExpressions()) {
                entry.add(variable);
            }
            return entry;
        }
        return null;
    //%%	//si contient nodeTo et nodeFrom, rajout relation
    //si contient nodeTo, rajout nodeFrom //%%+relation
    //si contient nodeFrom, rajout nodeTo //%%+relation
    //si contient ni nodeTo ni nodeFrom, ERREUR
    }
}

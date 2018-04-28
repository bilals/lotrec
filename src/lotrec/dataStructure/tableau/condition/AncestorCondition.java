package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the ancestor construction : the activator, the restriction and how they works.
This class is used with the <code>Rule</code> class, it is a useful class.
@author David Fauthoux
 */
public class AncestorCondition extends AbstractCondition {

    private SchemeVariable childScheme;
    private SchemeVariable ancestorScheme;
    //private Expression relationScheme;

    /**
    Creates an ancestor condition, ready to deliver knowledge about the corresponding activator and restriction
    @param childScheme the scheme representing the child in the links chain
    @param ancestorScheme the scheme representing the ancestor in the links chain
     */
    @ParametersTypes(types = {"node", "node"})
    @ParametersDescriptions(descriptions = {"The parent node that should be verified as ansector-linked to the second \"node\"",
        "The child node that should be verified as linked-late-successor to the first \"node\""
    })
    public AncestorCondition(SchemeVariable ancestorScheme, SchemeVariable childScheme) {
        super();
        this.childScheme = childScheme;
        this.ancestorScheme = ancestorScheme;
// this.relationScheme=relationScheme;
    }

    @Override
    public BasicActivator createActivator() {
        //REMOVED 10 16 2000
        //return new AncestorActivator(childScheme, ancestorScheme);
        return null;
    }

    @Override
    public Vector getActivationSchemes() {
        /* REMOVED 10 16 2000
        Vector v = new Vector();
        v.add(childScheme);
        // NON NON !! v.add(ancestorScheme); ON N'EN A PAS BESOIN
        // OK 2000 10 09
        //relation osf
        return v;
         */
        return null;
    }

    @Override
    public Restriction createRestriction() {
        return new AncestorMatch(childScheme, ancestorScheme);
    }

    @Override
    public Vector updateSchemes(Vector entry) {
        /* ADDED 2000 10 09 */
        if (entry.contains(ancestorScheme)) {
            if (!entry.contains(childScheme)) {
                Vector v = (Vector) entry.clone();
                v.add(childScheme);
                return v;
            }
            return entry;
        }
        if (entry.contains(childScheme)) {
            if (!entry.contains(ancestorScheme)) {
                Vector v = (Vector) entry.clone();
                v.add(ancestorScheme);
                return v;
            }
            return entry;
        }
        return null;
    /* REMOVED 2000 10 09
    if(!entry.contains(childScheme)) return null;
    if(!entry.contains(ancestorScheme)) {
    Vector v = (Vector)entry.clone();
    v.add(ancestorScheme);
    return v;
    }
    return entry;
    //il me faut child
    //je creerai ancestor si pas present
     */
    }
}

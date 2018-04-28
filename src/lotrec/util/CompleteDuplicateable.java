package lotrec.util;

//be careful to the law for duplication, specific for each class
//see the constructor (Duplicator duplicator)

//!!!!!!!!!!! CA SERAIT SUPER MIEUX DE NE DUPLIQUER QUE SI CE N'EST PAS DEJA FAIT (voir queueListener, dispatcher, restrictedDispatcher...)
//in order to __ensure__ qu'a la sortie du duplication process, the duplication has been done and is __minimal__

//mettre dans la doc: c'est pas super cool qu'a la duplication, les events soient dupliques tous chacun plein de fois dans chaque regle qui recoit des events.

///
public interface CompleteDuplicateable {

    /*
     * Usually called for a duplicatable object o after calling o.duplicate()
     * And usually, it consists on completing the duplication of the fields of o
     * Example, for a Node n we need to complete the duplication of its edges correctly.
     */
    public void completeDuplication(Duplicator duplicator) throws ClassCastException;

    /*
     * Usually called for a duplicatable object o after calling o.completeDuplication()
     * And usually, it consists on correcting the refrences of the fields of o pointing to objects in the base duplication structure (tableau)
     * For example, let t' is the duplicata of t. Let Node n be a node in t. During the call of n.duplicate() this would have created an image node, let it be Node n'.
     * And usually, during the call of n.duplicate(), we assign t as the container of n'.
     * Thus we should correct that by assigning t' instead.
     * This can be done by saying in n'.translateDuplication(duplicator):
     * container = (Graph) duplicator.getImage(container);
     *
     * NOTE: an exception may occur when the filed could be null!!
     * For example, currentTableau in ActionStocking, currentQueue in EventMachine etc.
     */
    public void translateDuplication(Duplicator duplicator) throws DuplicateException;
}

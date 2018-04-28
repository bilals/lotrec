package lotrec.util;

//should be final

///
public interface Duplicateable extends CompleteDuplicateable {
    ///
    public abstract Duplicateable duplicate(Duplicator duplicator);
}

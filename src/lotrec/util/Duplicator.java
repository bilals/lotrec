package lotrec.util;

///
public interface Duplicator {
    ///
    public void setImage(Object o, Object image);
    ///
    public Object getImage(Object o) throws DuplicateException;
    
    public boolean hasImage(Object o);
}

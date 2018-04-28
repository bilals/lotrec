package lotrec.util;

import java.util.Vector;
import java.util.Enumeration;

///TRES LENT
public class CommonDuplicator implements Duplicator {

    //Will be set when creating a new CommonDuplicator
    private Object duplicatedSource;
    private Object duplicatedImage;
    ///
    protected Vector hashers;

    ///
    public CommonDuplicator() {
        hashers = new Vector();
    }
    ///
    @Override
    public void setImage(Object o, Object image) {
        /*
        for(Enumeration enumr = hashers.elements(); enumr.hasMoreElements();) {
        Hasher h = (Hasher)enumr.nextElement();
        if(h.object.equals(o)) throw new DuplicateException("Object: "+o+" already has an image: "+h.image+", cannot set image: "+image);
        }
         */
        hashers.add(new Hasher(o, image));
    }
    ///
    @Override
    public Object getImage(Object o) throws DuplicateException {
        for (Enumeration enumr = hashers.elements(); enumr.hasMoreElements();) {
            Hasher h = (Hasher) enumr.nextElement();
            if (h.object.equals(o)) {
                return h.image;
            }
        }
        throw new DuplicateException("Cannot find image for object: " + o);
    }

    public boolean hasImage(Object o) {
        for (Enumeration enumr = hashers.elements(); enumr.hasMoreElements();) {
            Hasher h = (Hasher) enumr.nextElement();
            if (h.object.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public Object getDuplicatedSource() {
        return duplicatedSource;
    }

    public void setDuplicatedSource(Object duplicatedSource) {
        this.duplicatedSource = duplicatedSource;
    }

    public Object getDuplicatedImage() {
        return duplicatedImage;
    }

    public void setDuplicatedImage(Object duplicatedImage) {
        this.duplicatedImage = duplicatedImage;
    }
}

class Hasher {

    public Object object;
    public Object image;

    public Hasher(Object object, Object image) {
        this.object = object;
        this.image = image;
    }
}

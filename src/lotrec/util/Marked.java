package lotrec.util;

import java.util.Vector;

///
public class Marked implements CompleteDuplicateable {

  ///
  protected Vector marks;

  ///
  public Marked() {
    marks = new Vector();
  }

  ///
  public void mark(Object markWith) {
    marks.add(markWith);
  }

  ///
  public void unmark(Object markWith) {
    marks.remove(markWith);
  }

  ///
  public boolean isMarked(Object marker) {
    return marks.contains(marker);
  }

  ///
    @Override
  public String toString() {
    if(marks.isEmpty()) return "";
    else return marks.toString();
  }


  //////////////////////////////////////////////////////////
  ///Creates a marked object, marked with the same marks as toDuplicate
  public Marked(Marked toDuplicate) {
    marks = (Vector)toDuplicate.marks.clone();
  }
  ///
  public void completeDuplication(Duplicator duplicator) throws ClassCastException {
    //nothing
  }
  ///
  public void translateDuplication(Duplicator duplicator) throws DuplicateException {
    for(int j = 0; j < marks.size(); j++) {
      if(duplicator.hasImage(marks.get(j))) marks.set(j, duplicator.getImage(marks.get(j)));
    }
  }

    /**
     * @return the marks
     */
    public Vector getMarks() {
        return marks;
    }
  //////////////////////////////////////////////////////////

}

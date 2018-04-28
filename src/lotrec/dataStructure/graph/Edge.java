package lotrec.dataStructure.graph;

import lotrec.util.Marked;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;

/**
Instead of adding the linked nodes references, the nodes are linked to others with this class, so the link concept can be handled and improved
@see Node#link(Edge e)
@author David Fauthoux
 */
public class Edge extends Marked implements Duplicateable {

  private Node begin;
  private Node end;

  /**
  Class constructor specifying the source and destination nodes
  @param begin source node
  @param end destination node
   */
  public Edge(Node begin, Node end) {
    this.begin = begin;
    this.end = end;
  }

  /**
  Returns the source node of this edge
  @return the source node of this edge
   */
  public Node getBeginNode() {
    return begin;
  }

  /**
  Returns the destination node of this edge
  @return the destination node of this edge
   */
  public Node getEndNode() {
    return end;
  }


    @Override
  public String toString() {
    return begin + " -> " + end + super.toString();
  }


  //duplication

  /**
  Creates an edge with the toDuplicate's fields.<b> The source and destination nodes will not be duplicated with this duplication methods, but they must be duplicated when you call translateDuplication.</b>
  @param toDuplicate the egde to duplicate
   */
  public Edge(Edge toDuplicate) {
    super(toDuplicate);
    begin = toDuplicate.begin;
    end = toDuplicate.end;
  }

    @Override
  public void completeDuplication(Duplicator duplicator) throws ClassCastException {
    super.completeDuplication(duplicator);
    //nothing
  }

    @Override
  public void translateDuplication(Duplicator duplicator) throws DuplicateException {
    super.translateDuplication(duplicator);
    begin = (Node)duplicator.getImage(begin);
    end = (Node)duplicator.getImage(end);
  }
  public Duplicateable duplicate(Duplicator duplicator) {
    Duplicateable d = new Edge(this);
    duplicator.setImage(this, d);
    return d;
  }

}



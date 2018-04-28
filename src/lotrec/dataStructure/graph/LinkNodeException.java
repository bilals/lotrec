package lotrec.dataStructure.graph;

/**
   Signals that a link exception has occured. Thrown when a code try to link a node with an invalid edge.
   @see Node#link(Edge edge)
 */
public class LinkNodeException extends RuntimeException {

    /**
      Constructs an instance of LinkNodeException with an empty detail message.
    */
    public LinkNodeException() {
	super();
    }

    /**
      Constructs an instance of LinkNodeException with the specified detail message.
    */
    public LinkNodeException(String s) {
	super(s);
    }

}

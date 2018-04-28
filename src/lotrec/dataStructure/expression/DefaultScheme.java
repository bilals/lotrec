package lotrec.dataStructure.expression;

/**
Useful class to easily create scheme variables without being aware of a name.
All the created schemes are unequal.
*/
public class DefaultScheme implements SchemeVariable {
  private static int forCount = 0;
  private int count;
    /**
    Creates a default scheme
    */
    public DefaultScheme() {
      count = forCount;
      forCount++;
    }

    /**
    Tests the equality between two schemes.
    @param o the scheme to test
    @return true if the specified object is the same object of this (testing the pointers equality)
    */
    public boolean equals(Object o) {
	return o == this;
    }

    public String toString() {
	return "Scheme"+count;
    }

    public int hashCode() {
	return super.hashCode();
    }
}

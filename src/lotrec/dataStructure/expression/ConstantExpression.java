package lotrec.dataStructure.expression;

import java.util.ArrayList;

/**
Defines an expression which will match only with an equal constant expression
@author David Fauthoux
 */
public class ConstantExpression implements Expression {
    
    public static final ConstantExpression FALSUM = new ConstantExpression("False");
    public static final String DEFAULT_NAME = "Const";
    private static int forSoleName = 0;

    private static synchronized int getForSoleName() {
        int i = forSoleName;
        forSoleName++;
        return i;
    }
    private String name;

    /** ADDED 1 octobre 2000    */
    /**
    Creates a constant expression with an arbitrary sole name
     */
    public ConstantExpression() {
        this.name = DEFAULT_NAME + getForSoleName();
    }

    /**                         */
    /**
    Creates a constant expression with the specified name
    @param name the name of the created expression
     */
    public ConstantExpression(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
    Tests whether the specified expression equals this or not.
    @param e the expression to test
    @return true if the specified expression is a constant expression and its name equals this expression name, false otherwise
     */
    @Override
    public boolean equals(Object e) {
        if (e instanceof ConstantExpression) {
            return ((ConstantExpression) e).name.equalsIgnoreCase(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
    Returns the instance set if the specified expression equals this, null otherwise.
    Because the matching process between a constant expression and another expression only succeeds when the specified expression is a equal constant.
    @param e the expression on which the instanciation will run
    @param current the instance set to use and complete
    @return the instance set if the matching process succeeds, null if it fails
     */
    @Override
    public InstanceSet matchWith(Expression e, InstanceSet current) {
        if (equals(e)) {
            return current;
        }
        return null;
    }

    /**
    A constant expression can not be instanciated, so this method always return this.
    @param set the instance set used in the instanciation
    @return this
     */
    @Override
    public Expression getInstance(InstanceSet set) {
        return this;
    }

    /**
    Creates a string representation of this expression for MSPASS.
    @return a string representaion of this expression
     */
    public String toMSPASS() {
        return name.toLowerCase();

    }

    @Override
    public String getCodeString() {
        return this.name;
    }

    @Override
    public boolean isUsed(Connector c) {
        return false;
    }

    @Override
    public ArrayList<Expression> getVariableExpressions() {
        return new ArrayList<Expression>();
    }

    @Override
    public ArrayList<Connector> getUsedConnectors() {
        return new ArrayList<Connector>();
    }
}

package lotrec.dataStructure.expression;

import java.util.ArrayList;

/**
Defines an expression which can match with any expression
@author David Fauthoux
 */
public class VariableExpression implements Expression, SchemeVariable {

    /**
    The default string used to name a by default built expression
     */
    public static String defaultName = "A";
    protected String name;

    /**
    Creates a variable expression with the specified name
    @param name the name of the created expression
     */
    public VariableExpression(String name) {
        this.name = name;
    }

    /**
    Creates a variable expression with the default name
     */
    public VariableExpression() {
        name = defaultName;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
    Tests whether the specified expression equals this or not.
    @param e the expression to test
    @return true if the specified expression is a variable expression and its name equals this expression name, false otherwise
     */
    @Override
    public boolean equals(Object e) {
        if (e instanceof VariableExpression) {
            return ((VariableExpression) e).name.equals(name);
        }
        return false;
    }

    /**
    Completes the instance set with the link between this expression and the specified expression.
    But if the specified instance set is yet referencing another expression with this expression as scheme, then the method will return null.
    In all the other cases, the matching process between a variable expression and another expression succeeds.
    @param e the expression on which the instanciation will run
    @param current the instance set to use and complete
    @return the completed instance set if the matching process succeeds, null if it fails
     */
    @Override
    public InstanceSet matchWith(Expression e, InstanceSet current) {
        //return current.plus(this, e);
        Expression f = (Expression) current.get(this);
        if (f == null) {
            return current.plus(this, e);
        }
        if (f.equals(e)) {
            return current;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
    Creates a string representation of this expression for MSPASS.
    @return a string representaion of this expression
     */
    @Override
    public String toMSPASS() {
        return name.toLowerCase();

    }

    /** MODIFIED 1 octobre 2000          */
    /*
    Finds the instance of this expression in the specified set and returns it.
    @param set the instance set used in the instanciation
    @return the found instance of this expression, or this expression if the specified set does not contains the instance for this.
    public Expression getInstance(InstanceSet set) {
    Expression f = (Expression)set.get(this);
    if(f == null) return this;//variable libre qui traine
    else return f;
    }
     */
    /**                                  */
    /**
    Finds the instance of this expression in the specified set and returns it.
    @param set the instance set used in the instanciation
    @return the found instance of this expression, or A NEW CONSTANT if the specified set does not contains the instance for this.
     */
    /* REMOVED 00/12/10 
    public Expression getInstance(InstanceSet set) {
    Expression f = (Expression)set.get(this);
    if(f == null) return new ConstantExpression();
    else return f;
    }
     */
    /* ADDED 00/12/10 */
    @Override
    public Expression getInstance(InstanceSet set) {
        Expression f = (Expression) set.get(this);
        return f;
    }

    @Override
    public String getCodeString() {
        return "variable " + this.name;
    }

    @Override
    public boolean isUsed(Connector c) {
        return false;
    }

    @Override
    public ArrayList<Expression> getVariableExpressions() {
        ArrayList<Expression> result = new ArrayList<Expression>();
        result.add(this);
        return result;
    }

    @Override
    public ArrayList<Connector> getUsedConnectors() {
        return new ArrayList<Connector>();
    }
    /* */
}

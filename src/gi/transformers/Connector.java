package gi.transformers;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
public class Connector {
    
    public static char SPECIAL_CHARACTER = '_';

    private String name;  //the name used in the prefix form
    private int arity;    //the number of it arguments
    private String output;//the way the arguments and the connector are displayed in Infix form
    private int priority; //c1 priority less than c2 priority => c1 rule is treated before c2 rule

    public Connector(String name, int arity, String output,int priority) {
        this.name = name;
        this.arity = arity;
        this.output = output;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArity() {
        return arity;
    }

    public void setArity(int arity) {
        this.arity = arity;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public String toString(){
        return this.getName() + ": " + this.getOutput();
    }
}

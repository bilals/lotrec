/*
 * TestingFormula.java
 *
 * Created on 7 mars 2007, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lotrec.dataStructure;

import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.MarkedExpression;

/**
 *
 * @author said
 */
public class TestingFormula implements java.io.Serializable{
    
    private MarkedExpression formula;
    private String name;
    private String comment;
    
    /** Creates a new instance of TestingFormula */
    public TestingFormula() {
    }

    public MarkedExpression getFormula() {
        return formula;
    }

    public void setFormula(MarkedExpression formula) {
        this.formula = formula;
    }

    public String getDisplayName() {
        if(name==null || name.equals("")){
            //the output format designed in the connectors definition
            return formula.expression.toString();
        }else{
            return name;
        }        
    }
    
    public String getName() {
        return name;
    }    

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString(){
        //return this.codeAppearence;
        return this.formula.toString();
    }

    public String getCode() {
        return formula.getCodeString();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public boolean isUsed(Connector c){            
        return formula.isUsed(c);
    }    
    
}

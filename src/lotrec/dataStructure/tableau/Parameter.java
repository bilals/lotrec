/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.dataStructure.tableau;

import java.io.Serializable;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.StringSchemeVariable;

/**
 *
 * @author said
 */
public class Parameter implements Serializable {
    
    private ParameterType type;
    private Object value;

    public Parameter(ParameterType type, Object objectValue) {
        this.type = type;
        this.value = objectValue;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public String getValueCode() {
        switch(type){
            case NODE     : return ((StringSchemeVariable) value).toString();
            case FORMULA  : ;
            case RELATION : return ((Expression) value).getCodeString();
            case MARK     : return ((String) value);
            default       : return null;
        }
    }

    public void setValue(Object objectValue) {
        this.value = objectValue;
    }
}

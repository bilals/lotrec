/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.dataStructure.tableau;

/**
 *
 * @author said
 */
public enum ParameterType {
        NODE("node"), FORMULA("formula"), RELATION("relation"), MARK("mark");
        private String typeName;
        ParameterType(String typeName){
            this.typeName = typeName;
        }
                
        @Override
        public String toString(){
            return typeName;
        }
        
        public static ParameterType getParameterType(String typeName){
            for(ParameterType at: ParameterType.values()){
                if(at.typeName.equals(typeName))
                    return at;
            }                   
            return null;
        }       
}

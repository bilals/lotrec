/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.dataStructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author said
 */
@Target(value= {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionDescription {
    public String description();
}

/*
 * XMLLogicFileFilter.java
 *
 * Created on 26 octobre 2007, 14:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lotrec.gui.dialogs;

import java.io.File;
import javax.swing.filechooser.*;
/**
 *
 * @author said
 */
public class PsFileFilter extends FileFilter{
    
    
    //Accept all xml files and all directories  
    //(to view a directory, not to open it as the chosen file) 
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.PS_EXTENSION)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    //The description of this filter
    @Override
    public String getDescription() {
        return Utils.PS_DESC;
    }
}

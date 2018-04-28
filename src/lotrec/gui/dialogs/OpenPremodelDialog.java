/*
 * OpenLogicDialog.java
 *
 * Created on 26 octobre 2007, 11:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lotrec.gui.dialogs;

import java.io.File;
import javax.swing.JFileChooser;
import lotrec.Lotrec;

/**
 *
 * @author said
 */
public class OpenPremodelDialog extends JFileChooser{
    
    
    /** Creates a new instance of OpenPremodelDialog */
    public OpenPremodelDialog() {        
        this.setDialogTitle("Load a premodel");
        this.setDialogType(JFileChooser.OPEN_DIALOG);  
        this.setFileView(new XMLFileView());
        this.setFileFilter(new XMLFileFilter());
        this.setAcceptAllFileFilterUsed(false);
        this.setCurrentDirectory(new File(Lotrec.getPredefLogicsPath()));
    }
    
    public String getCompleteFileName(){
        return this.getSelectedFile().getAbsolutePath();
    }    
    
    public String getFileName(){
        return this.getSelectedFile().getName();
    }      
}

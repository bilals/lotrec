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
import lotrec.resources.ResourcesProvider;

/**
 *
 * @author said
 */
public class OpenLogicDialog extends JFileChooser{
    
    private java.util.ResourceBundle resource;
    
    /** Creates a new instance of OpenLogicDialog */
    public OpenLogicDialog() {        
        this.resource = java.util.ResourceBundle.getBundle("lotrec.resources.dialogs.OpenLogicDialog", ResourcesProvider.getCurrentLocale());        
        this.setDialogTitle(resource.getString("OpenLogicDialog.title"));
        //this.setApproveButtonText(resource.getString("OpenLogicDialog.OpenButton"));
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

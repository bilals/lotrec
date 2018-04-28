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
public class SaveAsPDFDialog extends JFileChooser {

    /** Creates a new instance of OpenLogicDialog */
    public SaveAsPDFDialog() {        
        this.setDialogTitle("Export Premodel As PDF Image File");
        this.setDialogType(JFileChooser.SAVE_DIALOG);
        this.setFileView(new PdfFileView());
        this.setFileFilter(new PdfFileFilter());
        this.setAcceptAllFileFilterUsed(false);
        this.setCurrentDirectory(new File(Lotrec.getPredefLogicsPath()));
    }

    public void setFileName(String fileName) {
        ((javax.swing.plaf.basic.BasicFileChooserUI) this.getUI()).setFileName(fileName);
    }

    public String getCompleteFileName() {
        if (Utils.getExtension(this.getSelectedFile()) != null &&
                Utils.getExtension(this.getSelectedFile()).equals(Utils.PDF_EXTENSION)) {
            return this.getSelectedFile().getAbsolutePath();
        } else {
            return this.getSelectedFile().getAbsolutePath() + "." + Utils.PDF_EXTENSION;
        }
    }

    public String getFileName() {
        if (Utils.getExtension(this.getSelectedFile()) != null &&
                Utils.getExtension(this.getSelectedFile()).equals(Utils.PDF_EXTENSION)) {
            return this.getSelectedFile().getName();
        } else {
            return this.getSelectedFile().getName() + "." + Utils.PDF_EXTENSION;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.gui.dialogs;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import lotrec.Lotrec;

/**
 *
 * @author said
 */
public class Utils {

    public final static String XML_EXTENSION = "xml";
    public final static String PDF_EXTENSION = "pdf";
    public final static String PS_EXTENSION = "ps";
    public final static String PNG_EXTENSION = "png";
    public final static String XML_DESC = "*.xml (XML Files)";
    public final static String PDF_DESC = "*.pdf (PDF Files)";
    public final static String PS_DESC = "*.ps (Post Script Files)";
    public final static String PNG_DESC = "*.png (PNG Files)";
    public final static String XML_TYPE_DESC = "XML File";
    public final static String PDF_TYPE_DESC = "PDF File";
    public final static String PS_TYPE_DESC = "PS File";
    public final static String PNG_TYPE_DESC = "PNG File";
    public final static ImageIcon PDF_ICON = createImageIcon("/lotrec/images/pdfIcon.PNG");
    public final static ImageIcon PS_ICON = createImageIcon("/lotrec/images/psIcon.PNG");
    public final static ImageIcon PNG_ICON = createImageIcon("/lotrec/images/pngIcon.PNG");
    public final static ImageIcon XML_ICON = createImageIcon("/lotrec/images/xmlIcon.gif");

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Lotrec.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static String getExtension(FileFilter currentFileFilter) {
        if (currentFileFilter instanceof PdfFileFilter) {
            return Utils.PDF_EXTENSION;
        } else if (currentFileFilter instanceof PngFileFilter) {
            return Utils.PNG_EXTENSION;
        } else if (currentFileFilter instanceof XMLFileFilter) {
            return Utils.XML_EXTENSION;
        } else if (currentFileFilter instanceof PsFileFilter) {
            return Utils.PS_EXTENSION;
        } else {
            return null;
        }
    }
}

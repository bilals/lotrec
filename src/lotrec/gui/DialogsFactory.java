/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.gui;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author said
 */
public class DialogsFactory {

    public static int cancelDialog(Component parent) {
        Object[] options = {"Yes", "No"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                "All eventual changes made will be lost..\n" + "Still want to cancel?",
                "Discard Changes",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[1]);
    }

    public static int deleteDialog(Component parent, String deleted) {
        Object[] options = {"Yes", "No"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                "You asked to delete " + deleted + "\n" + "Are you sure?",
                "Delete Confirmation",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[1]);
    }
    
    public static int notSavedLogicWarning(Component parent, String logicName) {
        Object[] options = {"Yes", "No"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                "Do you want to save changes of '" + logicName + "'?",
                "Warning: Changes will be lost",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[1]);
    }       
    
    public static int fileExistsWarning(Component parent, String fileName) {
        Object[] options = {"Yes", "No"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                "You want to save in file '" + fileName + "'\n"+
                "An old file with the same name already exists.\n" +
                "Overwrite old file?",
                "Overwrite File",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[1]);
    }    
    
    public static int openLogicFileAlreadyOpenWarning(Component parent, String logicFileName) {
        Object[] options = {"Ok"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                "The logic '" + logicFileName + "' is already open.\n" +
                "If you would like to open another copy of this file,\n" +
                "you may save it as a new file and open it.",
                "File already open",
                javax.swing.JOptionPane.OK_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[0]);
    }   
    
    public static int saveLogicFileAlreadyOpenWarning(Component parent, String logicFileName) {
        Object[] options = {"Ok"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                "The logic '" + logicFileName + "' is already open.\n" +
                " -Choose another file name, or\n" +
                " -Close the open file first, before overwriting it.",
                "File already open",
                javax.swing.JOptionPane.OK_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[0]);
    }     
    
    public static int syntaxErrorDialog(Component parent, String msg) {
        Object[] options = {"Ok", "Help"};
        return javax.swing.JOptionPane.showOptionDialog(parent,
                msg,
                "Syntax Error",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.ERROR_MESSAGE,
                new javax.swing.ImageIcon(DialogsFactory.class.getResource("/lotrec/images/warn.png")),
//                new javax.swing.ImageIcon(parent.getClass().getResource("/lotrec/images/warn.png")),
                options,
                options[1]);
    }    

    public static void syntaxErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Syntax Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void ruleDefinitionErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Rule definition error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void runTimeErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Run-time Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void syntaxWarningMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Syntax Problem",
                JOptionPane.WARNING_MESSAGE);
    }
    
    public static void parseLogicErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Logic XML Parser Error",
                JOptionPane.ERROR_MESSAGE);
    }
    
    public static void semanticErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Semantic Error",
                JOptionPane.ERROR_MESSAGE);
    }   
    
    public static void PremodelEditingActionWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Premodels Editor - warning",
                JOptionPane.WARNING_MESSAGE);
    }      
}

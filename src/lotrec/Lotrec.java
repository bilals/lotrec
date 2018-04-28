/*
 * Lotrec.java
 *
 * Created on 21 février 2007, 14:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec;

import java.io.File;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.gui.GraphvizDisplayer;
import lotrec.parser.exceptions.ParseException;
import lotrec.parser.LogicXMLParser;
//import lotrec.parser.OldLogicXMLParser;
import lotrec.parser.OldiesTokenizer;

/**
 *
 * @author said
 */
public class Lotrec {

    private static String predefLogicsPath;
    private static String userdefLogicsPath;
    private static String workingPath;
//    private static boolean alreadyRun = false;
    private static String runMode;
    public static String GUI_RUN_MODE = "GUI";
    public static String WEB_RUN_MODE = "WEB";

    /** Creates a new instance of Lotrec */
    public Lotrec() {
    }

    public static void initialize(String runMode) {
        Lotrec.setRunMode(runMode);
        if (runMode.equals(Lotrec.GUI_RUN_MODE)) {
            workingPath = System.getProperty("user.dir") + System.getProperty("file.separator");
        }
        if (runMode.equals(Lotrec.WEB_RUN_MODE)) {
            //workingPath will be set from the WebInterface class
        }
        setPredefLogicsPath(getWorkingPath() + "PredefinedLogics" + System.getProperty("file.separator"));
        setUserdefLogicsPath(getWorkingPath() + "UserdefinedLogics" + System.getProperty("file.separator"));
    }

//    public static Logic openJarLogicFile(String jarResourceFile) {
//        Logic resultLogic = null;
//        LogicXMLParser lxmlparser = new LogicXMLParser();
//        try {
//            resultLogic = lxmlparser.parseLogic(jarResourceFile);
//        } catch (ParseException ex) {
//            Lotrec.println("Execption while reading the logic file " + jarResourceFile);
//            Lotrec.println(ex.getMessage());
//        }
//        return resultLogic;
//    }    
    public static Logic openLogicFile(String completeFileName) {
        Logic resultLogic = null;
        LogicXMLParser lxmlparser = new LogicXMLParser();
        try {
            resultLogic = lxmlparser.parseLogic(completeFileName);
        } catch (ParseException ex) {
            Lotrec.println("Execption while reading the logic file " + completeFileName);
            Lotrec.println(ex.getMessage());
        }
        return resultLogic;
    }

    public static void saveLogicFile(Logic logic, String completeFileName) {
        LogicXMLParser lxmlparser = new LogicXMLParser();
        try {
            lxmlparser.saveLogic(logic, completeFileName);
        } catch (ParseException ex) {
            Lotrec.println("Execption while reading the logic file " + completeFileName);
            Lotrec.println(ex.getMessage());
        }
    }

//    public static Logic openOldLogicFile(String completeFileName) {
//        Logic resultLogic = null;
//        OldLogicXMLParser lxmlparser = new OldLogicXMLParser(completeFileName);
//        try {
//            resultLogic = lxmlparser.parseLogic();
//        } catch (LogicParserException ex) {
//            Lotrec.println("Execption while reading the logic file " + completeFileName);
//            Lotrec.println(ex.getMessage());
//        }
//        return resultLogic;
//    }
    public static Logic parsePredefinedXMLLogicFile(String logicXMLFileName) {
        Logic resultLogic = null;
        String fileName = workingPath + "PredefinedLogics" + System.getProperty("file.separator") + logicXMLFileName;
        LogicXMLParser lxmlparser = new LogicXMLParser();
        try {
            resultLogic = lxmlparser.parseLogic(fileName);
        } catch (ParseException ex) {
            Lotrec.println("Execption while reading the logic file " + fileName);
            Lotrec.println(ex.getMessage());
        }
        return resultLogic;
    }

    public static Logic parseUserdefinedXMLLogicFile(String logicXMLFileName) {
        Logic resultLogic = null;
        String fileName = workingPath + "UserdefinedLogics" + System.getProperty("file.separator") + logicXMLFileName;
        LogicXMLParser lxmlparser = new LogicXMLParser();
        try {
            resultLogic = lxmlparser.parseLogic(fileName);
        } catch (ParseException ex) {
            Lotrec.println("Execption while reading the logic file " + fileName);
            Lotrec.println(ex.getMessage());
        }
        return resultLogic;
    }

//    public static Wallet buildTableauxFor(Logic givenLogic, String chosenStrategyName, String givenFormula) {
//        MarkedExpression formula = Lotrec.parseFormula(givenLogic, givenFormula);
//        Engine engine = new Engine(givenLogic, chosenStrategyName, formula);
//        return engine.buildTableaux();
//    }
//
//    public static Wallet buildTableauxFor(Logic givenLogic, String chosenStrategyName, MarkedExpression givenFormula) {
//        Engine engine = new Engine(givenLogic, chosenStrategyName, givenFormula);
//        return engine.buildTableaux();
//    }
    public static MarkedExpression parseFormula(Logic givenLogic, String formulaCode) {
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(givenLogic);
        oldiesTokenizer.initializeTokenizerAndProps();
        MarkedExpression formula = null;
        try {
            formula = new MarkedExpression(oldiesTokenizer.parseExpression(formulaCode));
        } catch (ParseException ex) {
            Lotrec.println("Execption while reading a formula...");
            Lotrec.println(ex.getMessage());
        }
        return formula;
    }

    //To be used in WEB mode, after buildTableauxFor
    //it deals with the new parser and the new version
    public static String[] createTableauxImages(Wallet resultsWallet, MarkedExpression givenFormula) {
        String[] result;
        result = GraphvizDisplayer.createTableauImages(resultsWallet, givenFormula);
        for (int i = 0; i < result.length; i++) {
            System.out.println("Tableau image with name: " + result[i] + " has been created.");
        }
        return result;
    }

    //To be used in WEB mode, after buildTableauxFor & createTableauxImages
    //delete all tableaux images files created by Web Lotrec
    public static void deleteTableauxImages() {
        File imagesDir = new File(Lotrec.getWorkingPath() + "tableauxImages");
        if (imagesDir.isDirectory()) {
            String[] fileList = imagesDir.list();
            for (int i = 0; i < fileList.length; i++) {
                File file = new File(imagesDir, fileList[i]);
                file.delete();
            }
        }
    }

    public static void exitLotrec() {
        if (runMode.equals(Lotrec.GUI_RUN_MODE)) {
//            Cytoscape.exit();
        }
        if (runMode.equals(Lotrec.WEB_RUN_MODE)) {
        }
    }
//    
//    public static void main(String[] args){
//        
//    }

    public static void print(Object o) {
        System.out.print(o);
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void setWorkingPath(String workPath) {
        workingPath = workPath;
    }

    public static String getWorkingPath() {
        return workingPath;
    }

    public static String getRunMode() {
        return runMode;
    }

    public static void setRunMode(String aRunMode) {
        runMode = aRunMode;
    }

    public static String getPredefLogicsPath() {
        return predefLogicsPath;
    }

    public static void setPredefLogicsPath(String aPredefLogicsPath) {
        predefLogicsPath = aPredefLogicsPath;
    }

    public static String getUserdefLogicsPath() {
        return userdefLogicsPath;
    }

    public static void setUserdefLogicsPath(String aUserdefLogicsPath) {
        userdefLogicsPath = aUserdefLogicsPath;
    }
}

/*
 * PLLoader.java
 *
 * Created on 22 juin 2007, 16:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.process.Strategy;

/**
 *Predefined Logics Loader
 *
 * @author said
 */
public class PLLoader {

    public static ArrayList logicsNames;
    public static ArrayList PLList;
    public static ArrayList PLFilesNamesList;

    /** Creates a new instance of PLLoader */
    public PLLoader() {
    }

    public static void loadPLs() {
        PLList = new ArrayList();
        PLFilesNamesList = new ArrayList();
        System.out.println("The path is: " + Lotrec.getWorkingPath() + "PredefinedLogics");
        File imagesDir = new File(Lotrec.getWorkingPath() + "PredefinedLogics");
        System.out.println("imagesDir is: " + imagesDir);
        if (imagesDir.isDirectory()) {
            String[] PLFilesNames = imagesDir.list();
            for (int i = 0; i < PLFilesNames.length; i++) {
                System.out.println("Found file name: " + PLFilesNames[i]);
                if (PLFilesNames[i].endsWith(".xml")) {
                    PLFilesNamesList.add(PLFilesNames[i]);
                    PLList.add(Lotrec.parsePredefinedXMLLogicFile(PLFilesNames[i]));
                }
            }
        }
    }

    public static void loadLogicsNames() {
        logicsNames = null;
        if ((PLList != null) && (PLList.size() > 0)) {
            logicsNames = new ArrayList();
            for (int i = 0; i < PLList.size(); i++) {
                logicsNames.add(((Logic) PLList.get(i)).getName());
            }
        }
    }

    public static ArrayList getTFNames(int logicIndex) {
        ArrayList TFNames = null;
        Vector tfs = getLogic(logicIndex).getTestingFormulae();
        if ((tfs != null) && (tfs.size() > 0)) {
            TFNames = new ArrayList();
            for (int i = 0; i < tfs.size(); i++) {
                //getName replaced getCodeAppearance while updating to the new XML version
                TFNames.add(((TestingFormula) tfs.get(i)).getDisplayName());
            }
        }
        return TFNames;
    }

    public static ArrayList getTFCodes(int logicIndex) {
        ArrayList TFCodes = null;
        Vector tfs = getLogic(logicIndex).getTestingFormulae();
        if ((tfs != null) && (tfs.size() > 0)) {
            TFCodes = new ArrayList();
            for (int i = 0; i < tfs.size(); i++) {
                TFCodes.add(((TestingFormula) tfs.get(i)).getCode());
            }
        }
        return TFCodes;
    }

    public static ArrayList getCompleteStartegiesNames(int logicIndex) {
        ArrayList strNames = null;
        Vector strs = getLogic(logicIndex).getStrategies();
        if ((strs != null) && (strs.size() > 0)) {
            strNames = new ArrayList();
            for (int i = 0; i < strs.size(); i++) {
                Strategy str = (Strategy) strs.get(i);
                strNames.add(str.getWorkerName());
//                if (str.getUsability().equals("complete")) {
//                    strNames.add(str.getWorkerName());
//                }
            }
        }
        return strNames;
    }

    public static Logic getLogic(int logicIndex) {
        return (Logic) PLList.get(logicIndex);
    }

    public static Vector getTestingFormulae(int logicIndex) {
        return getLogic(logicIndex).getTestingFormulae();
    }

    public static TestingFormula getTesingFormula(int logicIndex, int formulaIndex) {
        return (TestingFormula) getTestingFormulae(logicIndex).get(formulaIndex);
    }

    public static MarkedExpression getTFFormula(int logicIndex, int formulaIndex) {
        return ((TestingFormula) getTestingFormulae(logicIndex).get(formulaIndex)).getFormula();
    }

    public static Vector getStrategies(int logicIndex) {
        return getLogic(logicIndex).getStrategies();
    }

    public static Strategy getStrategy(int logicIndex, int strategyIndex) {
        return (Strategy) getStrategies(logicIndex).get(strategyIndex);
    }
    //will not be used :s

    public static String getStrategyName(int logicIndex, int strategyIndex) {
        return ((Strategy) getStrategies(logicIndex).get(strategyIndex)).getWorkerName();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec;

import java.io.*;
import java.util.*;

/**
 *
 * @author said
 */
public class FileUtils {
    
    public static String LOTREC_HOME = 
            System.getProperty("user.home")+
            System.getProperty("file.separator") + ".LoTREC";
    public static String PREDEFINED_HOME = 
            LOTREC_HOME + 
            System.getProperty("file.separator") + 
            "PredefinedLogics";

    public static File createLoTRECDir() {
        try {
            String dirName = System.getProperty("user.home");
            File parent_dir = new File(dirName, ".LoTREC");
            if (parent_dir.mkdir()) {
                System.out.println("Parent dir: " + parent_dir + " created.");
            }
            return parent_dir;
        } catch (Exception e) {
            System.out.println("Error getting/creating .LoTREC directory in your home: "+System.getProperty("user.home"));
        }
        return null;
    }

    public static File createPredefinedLogicsDir() {
        try {
            File parent_dir = createLoTRECDir();
            File logics_dir = new File(parent_dir, "PredefinedLogics");
            if (logics_dir.mkdir()) {
                System.out.println("PredefinedLogics dir: " + logics_dir + " created.");
            }
            return logics_dir;
        } catch (Exception e) {
            System.out.println("Error getting/creating PredefinedLogics directory in "+createLoTRECDir());
        }
        return null;
    }

    public static File createPredefinedLogicFile(String fileName) {
        try {
            File logicsDir = createPredefinedLogicsDir();
            File file = new File(logicsDir, fileName);
            if (file.createNewFile()) {
                System.out.println("Logic file: " + file + " created.");
            }
            return file;
        } catch (Exception e) {
            System.out.println("Error getting/creating logic file: " + fileName);
        }
        return null;
    }

    public static void extractPredefinedLogicFile(String jarPath, String fileName) {
        InputStream is = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        String line;
        try {
            File logicFile = createPredefinedLogicFile(fileName);
//            System.out.println("logic file: "+logicFile);
            bw = new BufferedWriter(new FileWriter(logicFile));
            is = FileUtils.class.getResourceAsStream(jarPath + fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine())) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error occured while copiying logic file: " + fileName);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void copyLogicDtdTo(String dir){
        extractPredefinedLogicFile(
                PredefinedLogicsLoader.JAR_PATH, 
                PredefinedLogicsLoader.DTD_FILE_NAME);
        BufferedReader dtdBR = null;
        BufferedWriter dtdBW = null;
        try{
        dtdBR = new BufferedReader(
                new FileReader(new File(PREDEFINED_HOME,
                PredefinedLogicsLoader.DTD_FILE_NAME)));
        dtdBW = new BufferedWriter(
                new FileWriter(new File(dir, 
                PredefinedLogicsLoader.DTD_FILE_NAME)));
        String line;
        while (null != (line = dtdBR.readLine())) {
                dtdBW.write(line);
                dtdBW.newLine();
            }
        }catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error occured while copiying logic.dtd to the following path:\n" + dir);
        }finally {
            try {
                if (dtdBW != null) {
                    dtdBW.close();
                }
                if (dtdBR != null) {
                    dtdBR.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> readTextFromJar(String s) {
        InputStream is = null;
        BufferedReader br = null;
        String line;
        ArrayList<String> list = new ArrayList<String>();

        try {
            is = FileUtils.class.getResourceAsStream(s);
            br = new BufferedReader(new InputStreamReader(is));
            while (null != (line = br.readLine())) {
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static String getFileNameWithoutExtension(String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
            return tmpFile.getName().substring(0, whereDot);
        //extension = filename.substring(whereDot+1);
        }
        return "";
    }

    public static void main(String args[]) throws IOException {
//        FileUtils.extractPredefinedLogicFile(
//                PredefinedLogicsLoader.JAR_PATH,
//                PredefinedLogicsLoader.DTD_FILE_NAME);
//        for(int i=0; i<PredefinedLogicsLoader.LOGICS_FILES.length; i++){
//            FileUtils.extractPredefinedLogicFile(
//                    PredefinedLogicsLoader.JAR_PATH,
//                    PredefinedLogicsLoader.LOGICS_FILES[i]+".xml");
//        }     
                
//        FileUtils.extractPredefinedLogicFile("/lotrec/logics/", "CPLminimal.xml");

//        List<String> list = FileUtils.readTextFromJar("/lotrec/logics/CPLminimal.xml");
//        Iterator<String> it = list.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next());
//        }
//
//    list = FileUtils.readTextFromJar("/lotrec/logics/CPLminimal.xml");
//    it = list.iterator();
//    while(it.hasNext()) {
//      System.out.println(it.next());
//    }
    }
}

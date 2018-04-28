/*
 * GraphvizDisplayer.java
 *
 * Created on 6 juin 2007, 16:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lotrec.gui;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lotrec.Lotrec;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauEdge;
import lotrec.dataStructure.tableau.TableauNode;

/**
 *This class is used to create Tableau images in a specific folder "tableauxImages" to the web display
 * @author said
 */
public class GraphvizDisplayer {
    
    /** Creates a new instance of GraphvizDisplayer */
    public GraphvizDisplayer() {
    }
    
    public static String[] createTableauImages(Wallet wallet, MarkedExpression formula) {
        int tableauxNum = 0;
        int imgNum = 0;
        for(Enumeration enumr1 = wallet.getGraphesEnum(); enumr1.hasMoreElements();){
            tableauxNum++;
            enumr1.nextElement();
        }
        String[] imagesNames = new String[tableauxNum];
        String imageTableauName= new String();
        imgNum = 0;
        String imageType = new String("png");
        String tableauTimeName;
        for(Enumeration enumr = wallet.getGraphesEnum(); enumr.hasMoreElements();) {
            Tableau t = (Tableau)enumr.nextElement();
            tableauTimeName = t.getName() + getCurrentTime();            
            if(t.isClosed()){
                imageTableauName = "Closed ";
            }else{
                imageTableauName = "Open ";                
            }
            imageTableauName = imageTableauName + "Tableau " + (imgNum+1) + " of " + tableauxNum;
            putTableauInDotFile(t,tableauTimeName,imageTableauName,formula);
            giveTableauDotGraphic(t,tableauTimeName,imageType);
            imagesNames[imgNum] = tableauTimeName + "." + imageType;
            imgNum++;
        }
        return imagesNames;
    }
    
    public static void putTableauInDotFile(Tableau t, String tableauTimeName, String imageTableauName, MarkedExpression formula) {
        String dotFileName = new String();
        if(System.getProperty("file.separator").equals("/")){
            dotFileName = Lotrec.getWorkingPath() + "tableauxImages/" + tableauTimeName + ".dot";
        }else{
            dotFileName = Lotrec.getWorkingPath() + "tableauxImages\\" + tableauTimeName + ".dot";
        }
        try{
            FileWriter fw = new FileWriter(dotFileName);
            fw.write("digraph tableau {\n");
            fw.write("\tfontsize = 12;\n");
            fw.write("\tlabel = \"\\n"+imageTableauName+" for the formula: "+formula+"\";\n");
            fw.write("\tnode [shape=box,fontsize = 12];\n");
            fw.write("\tedge [fontsize = 12];\n");
            for(Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
                TableauNode n = (TableauNode)enum_.nextElement();               
                if(n.isClosed()){                    
                    fw.write("\t\t\"" + n.getName() + "\" [fillcolor=\"#B7D3FC\", style=filled,label = \"");
                    for(Enumeration subEnumerator = n.getMarkedExpressionsEnum(); subEnumerator.hasMoreElements();) {
                        MarkedExpression me = (MarkedExpression)subEnumerator.nextElement();
                        fw.write(me +"\\n");
                    }
                    fw.write("\"];\n");
                }else{                    
                    fw.write("\t\t\"" + n.getName() + "\" [label = \"");
                    for(Enumeration subEnumerator = n.getMarkedExpressionsEnum(); subEnumerator.hasMoreElements();) {
                        MarkedExpression me = (MarkedExpression)subEnumerator.nextElement();
                        fw.write(me +"\\n");
                    }
                    fw.write("\"];\n");
                }
                for(Enumeration subEnumerator = n.getNextEdgesEnum(); subEnumerator.hasMoreElements();) {
                    TableauEdge te = (TableauEdge)subEnumerator.nextElement();
                    fw.write("\t\t\""+ te.getBeginNode().getName() + "\" -> \"" + te.getEndNode().getName() + "\" [label =\"" + te.getRelation()+"\"];\n");
                }
            }
            fw.write("}\n");
            fw.close();
        } catch(IOException e){
            System.out.println("Exception during creation of .dot Tableau file with name: " + dotFileName);
        }
    }
    
    public static void giveTableauDotGraphic(Tableau t, String tableauTimeName, String imageType) {
        String dotCommand;
        String dotFileName = new String();
        String imageFileName = new String();
        if(System.getProperty("file.separator").equals("/")){
            dotFileName = Lotrec.getWorkingPath() + "tableauxImages/" + tableauTimeName + ".dot";
            imageFileName = Lotrec.getWorkingPath() + "tableauxImages/" + tableauTimeName + "." + imageType;
            dotCommand = new String("/usr/bin/dot -T");
        }else{
            dotFileName = "\"" + Lotrec.getWorkingPath() + "tableauxImages\\" + tableauTimeName + ".dot\"";
            imageFileName = "\"" + Lotrec.getWorkingPath() + "tableauxImages\\" + tableauTimeName + "." + imageType + "\"";
            dotCommand = new String("dot -T");
        }
        String command = dotCommand + imageType + " " + dotFileName + " -o " + imageFileName;
        System.out.println("The executed command is: \n" + command);
        try{
            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Runtime execution of dot command line for creating tableaux images fails...(IOException)");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.out.println("Runtime execution of dot command line for creating tableaux images fails...(InterruptException)");
        }
    }
    
    public static String getCurrentTime(){
    /*
     ** on some JDK, the default TimeZone is wrong
     ** we must set the TimeZone manually!!!
     **   Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"));
     */
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        
        String DATE_FORMAT = "HH:mm:ss:SSS";
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat(DATE_FORMAT);
    /*
     ** on some JDK, the default TimeZone is wrong
     ** we must set the TimeZone manually!!!
     **     sdf.setTimeZone(TimeZone.getTimeZone("EST"));
     */
        sdf.setTimeZone(TimeZone.getDefault());
        String REGEX = ":";
        String INPUT = sdf.format(cal.getTime());
        String REPLACE = ".";
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object
        StringBuffer sb = new StringBuffer();
        while(m.find()){
            m.appendReplacement(sb,REPLACE);
        }
        m.appendTail(sb);
        String currentTime = sb.toString();
        return currentTime;
    }
    
}

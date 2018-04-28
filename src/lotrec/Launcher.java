package lotrec;

import cytoscape.CyMain;
import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanelState;
import gi.transformers.TransformerGUI;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.engine.Engine;
import lotrec.gui.MainFrame;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.Strategy;
import lotrec.resources.ResourcesProvider;
/*
 * Luncher.java
 *
 * Created on 24 octobre 2007, 19:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author said
 */
public class Launcher {

    public static MainFrame getTheMainFrame() {
        return theMainFrame;
    }
    private JLabel splashLabel;
    private JPanel splashPanel;
    private JWindow splashScreen = null;
    private JFrame frame = null;
    private static MainFrame theMainFrame;

    /** Creates a new instance of Luncher */
    public Launcher() {
        ResourcesProvider.setCurrentLocale(new Locale("en", "US"));
        frame = new JFrame();
        createSplashScreen();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                showSplashScreen();
            }
        });

        initialize();
//        initialize_Benchmark();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                theMainFrame.setVisible(true);
                theMainFrame.showTaskPane();
                hideSplash();
            }
        });
    }

    public void initialize() {
//        FileUtils.extractPredefinedLogicFile("/lotrec/logics/", "CPLminimal.xml");
//        FileUtils.extractPredefinedLogicFile("/lotrec/logics/", "logic.dtd");
//        VerifierKawa.displayConditionsClasses();
//        VerifierKawa.displayActionsClasses();
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error:\n");
            System.out.println(e.getMessage());
        }
        theMainFrame = new MainFrame();
//        theMainFrame.getTableauxPanel().initializeCyFrame(); // this is achieved few lines later below...
//        theMainFrame.repaint();
        URL imgURL = Lotrec.class.getResource("/lotrec/images/lotrecIcon.GIF");
//        System.out.println(imgURL);
        ImageIcon icon = new ImageIcon(imgURL);
        theMainFrame.setIconImage(icon.getImage());
        Lotrec.initialize(Lotrec.GUI_RUN_MODE);
        //Just to test!!
        String[] cyArgs = new String[]{"-p", "csplugins.quickfind.plugin.QuickFindPlugIn",
            "-p", "browser.AttributeBrowserPlugin",
            "-p", "GraphMerge.GraphMerge",
            "-p", "cytoscape.editor.CytoscapeEditorPlugin",
            "-p", "org.cytoscape.coreplugin.cpath.plugin.CPathPlugIn",
            "-p", "filter.cytoscape.CsFilter",
//            "-p", "linkout.LinkOutPlugin",
            "-p", "org.cytoscape.coreplugin.psi_mi.plugin.PsiMiPlugIn",
            "-p", "org.mskcc.biopax_plugin.plugin.BioPaxPlugIn",
            "-p", "csplugins.contextmenu.yeast.YeastPlugin",
            "-p", "edu.ucsd.bioeng.coreplugin.tableImport.TableImportPlugin",
            "-p", "sbmlreader.SBMLReaderPlugin",
            "-p", "csplugins.layout.LayoutPlugin",
            "-p", "ManualLayout.ManualLayoutPlugin",
            "-p", "yfiles.YFilesLayoutPlugin"
        };
        //old method to load the plugins was:
//        cyArgs[0] = "-p";
//        cyArgs[1] = "plugins";
        try {
            new CyMain(cyArgs);
            // Some adjusments...
            Cytoscape.getDesktop().getCyMenus().getToolBar().setVisible(false);
            Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
            Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
            Cytoscape.getDesktop().clearStatusBar();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createSplashScreen() {
        splashLabel = new JLabel();
        JLabel splashLabel2 = new JLabel();
        splashLabel.setOpaque(false);
        splashLabel.setText(" ");
        splashLabel2.setText("Tableaux Theorem Prover");
        splashLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
        splashLabel2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));//"Bradley Hand ITC"
        splashLabel.setHorizontalTextPosition(JLabel.CENTER);
        URL imgURL = Lotrec.class.getResource("/lotrec/images/lotrec.png");
//        System.out.println("imgURL of splash screen is: " + imgURL);
        splashLabel.setIcon(new ImageIcon(imgURL));
        splashLabel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK, 4));
        splashPanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
//                URL imgURL = getClass().getResource("/lotrec/images/lotrec.jpg");
//                System.out.println(imgURL);
//                ImageIcon icon = new ImageIcon(imgURL);
//                g.drawImage(icon.getImage(),0,0,this);
            }
        };
        splashPanel.add(splashLabel);
        splashScreen = new JWindow(frame);
        splashScreen.getContentPane().add(splashPanel);
        splashScreen.pack();
        Rectangle screenRect = frame.getGraphicsConfiguration().getBounds();
        splashScreen.setLocation(
                screenRect.x + screenRect.width / 2 - splashScreen.getSize().width / 2,
                screenRect.y + screenRect.height / 2 - splashScreen.getSize().height / 2);
    }

    public void showSplashScreen() {
        splashScreen.setVisible(true);
    }

    public void hideSplash() {
        splashScreen.setVisible(false);
        splashScreen.dispose();
        splashScreen = null;
        splashLabel = null;
    }

    private static void sleepSec(int sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {        
        //When benchmarking
        //------------------------
//        treatArgsForBenchmark(args);
        //------------------------
        new Launcher();
    }
    
    public void initialize_Benchmark() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error:\n");
            System.out.println(e.getMessage());
        }
        theMainFrame = new MainFrame();
        URL imgURL = Lotrec.class.getResource("/lotrec/images/lotrecIcon.GIF");
        ImageIcon icon = new ImageIcon(imgURL);
        theMainFrame.setIconImage(icon.getImage());
        Lotrec.initialize(Lotrec.GUI_RUN_MODE);
        String[] cyArgs = new String[]{};
        try {
            new CyMain(cyArgs);
            // Some adjusments...
            Cytoscape.getDesktop().getCyMenus().getToolBar().setVisible(false);
            Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
            Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
            Cytoscape.getDesktop().clearStatusBar();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void treatArgsForBenchmark(String[] args) {
        for (String arg : args) {
            System.out.println("Argumant: " + arg);
        }
        if (args != null && args.length == 4) {
            String logicName = args[0];
            String formulaInfixCode = args[1];
            boolean SAT = Boolean.valueOf(args[2]);
//            System.out.println("args[2] is: "+args[2]);
//            System.out.println("SAT is: "+SAT);
            boolean NEG = Boolean.valueOf(args[3]);
//            System.out.println("args[3] is: "+args[3]);
//            System.out.println("NEG is: "+NEG);
            Launcher launcher;
            launcher = new Launcher();
            launcher.benchmark(logicName, formulaInfixCode, SAT, NEG);

        }
        System.exit(0);
    }

    private void benchmark(String logicName, String formulaInfixCode, boolean SAT, boolean NEG) {
        String fileName = logicName + ".xml";
        String formulaCode = null;
        Logic chosenLogic = null;
        //------------------Parsing the logic--------------------
        String completeFileName = FileUtils.PREDEFINED_HOME +
                System.getProperty("file.separator") + fileName;
        FileUtils.extractPredefinedLogicFile(
                PredefinedLogicsLoader.JAR_PATH, fileName);
        LogicXMLParser lxmlparser = new LogicXMLParser();
        try {
            chosenLogic = lxmlparser.parseLogic(completeFileName);
        } catch (ParseException ex) {
            System.err.println("Execption while reading the logic file " + completeFileName);
            System.err.println(ex.getMessage());
            return;
        }
        //-------transforming the infix formula to prefix---------
        TransformerGUI transformer = new TransformerGUI();
        formulaCode = transformer.toPrefix(formulaInfixCode);
        if (formulaCode == null) {
            return;
        }
        if (NEG) {
            formulaCode = "not " + formulaCode;
            System.out.println("Not Phi is being considered..");
        } else {
            System.out.println("Phi is being considered..");
        }
//        System.out.println(formulaCode);
        //------------------Parsing the prefix formula--------------------
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(chosenLogic);
        oldiesTokenizer.initializeTokenizerAndProps();
        MarkedExpression formula = null;
        try {
            formula = new MarkedExpression(oldiesTokenizer.parseExpression(formulaCode));
            oldiesTokenizer.verifyCodeEnd();
        } catch (ParseException ex) {
            System.err.println("The given formula code raised the following parser exception:\n\n" +
                    ex.getMessage());
            return;
        }
        //------------------Parsing the strategy--------------------
        String defaultStrategyName = chosenLogic.getMainStrategyName();
        Strategy newStr;
        try {
            newStr = oldiesTokenizer.parseStrategy(chosenLogic.getStrategy(defaultStrategyName).getCode());
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            System.err.println("The given strategy raised the following parser exception:\n" +
                    ex.getMessage());
            return;
        }
        //------------------Building the tableaux--------------------
        if (formula != null) {
            Engine engine = new Engine(chosenLogic, newStr, formula, getTheMainFrame());
            if (SAT) {
                engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
                System.out.println("SAT is being considered..");
            } else {
                System.out.println("ALL is being considered..");
            }
            getTheMainFrame().setEngine(engine);
            getTheMainFrame().getEngine().buildTableaux();
            System.out.println("Relaxing for 10 secs... :)");
            sleepSec(10);
            System.out.println("LoTREC engine starting...");
            getTheMainFrame().getEngine().start();
            try {
                getTheMainFrame().getEngine().join();
            } catch (InterruptedException ex) {
                System.err.println("Wait failed in Launcher...\n" +
                        ex.getMessage());
            }
            System.out.println("LoTREC engine has finished...");
            System.out.println("Was done in: " + //Launcher.getTheMainFrame().getEngine().getEngineTimer().getElapsedTime()/1000 + " | s | " +
                    Launcher.getTheMainFrame().getEngine().getEngineTimer().getElapsedTime() + " | ms");
        } else {
            System.out.println("Parsed formula is null!!!");
            System.out.println("Was done in: ERROR PARSING FORMULA");
        }
    }
}

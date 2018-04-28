/*
 * LogicDefPanel.java
 *
 * Created on 6 fÃ©vrier 2008, 17:53
 */
package lotrec.gui.logicspane;

import java.awt.Point;
import lotrec.FileUtils;
import lotrec.Lotrec;
import lotrec.PredefinedLogicsLoader;
import lotrec.dataStructure.Logic;
import lotrec.gui.DialogsFactory;
import lotrec.gui.MainFrame;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.exceptions.ParseException;

/**
 *
 * @author  said
 */
public class LogicDefTab extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private Logic logic;
    private boolean defautConstruct = true;
    private boolean modifiedAndNotSaved = false;
    private String fileName;
    private String fileDir;
    //fileName == null -> New Empty Logic (No File) (fileDir will be null too)
    //fileName != null BUT fileDir == null -> Predefined Logic
    //fileName != null && fileDir != null -> Existing Logic opened form a File
    /** Creates new form LogicDefPanel */
    public LogicDefTab() {
        initComponents();
    }

    public LogicDefTab(String logicFileName, String logicFileDir, MainFrame mainFrame) {
        //There will be an eventual Logic object..
        defautConstruct = false;
        this.fileName = logicFileName;
        this.fileDir = logicFileDir;
        this.mainFrame = mainFrame;
        if (fileName == null) {
            //Case of New Empty Logic
            logic = Logic.getNewEmptyLogic();
        //Better to make it not modified when open
        //it will follow the rule of modification like the others..            
//            modifiedAndNotSaved = true;
        } else if (fileDir == null) {
            //Case of predefined logic:
            if (mainFrame.getLoadedLogicsPanel().
                    predefinedLogicFileAlreadyOpen(fileName)) {
                DialogsFactory.openLogicFileAlreadyOpenWarning(mainFrame, fileName);
                return;
            }
            // Adding a version to the name is not needed, cause the files are re-created
            // No problem between the files versions...
//            fileName = LogicXMLParser.PREDEFINED_XML_VERSION+fileName;
            String completeFileName = FileUtils.PREDEFINED_HOME +
                    System.getProperty("file.separator") + fileName;
            //Changed April 2008
//            FileUtils.extractPredefinedLogicFile(
//                    PredefinedLogicsLoader.JAR_PATH,
//                    PredefinedLogicsLoader.DTD_FILE_NAME);
            FileUtils.extractPredefinedLogicFile(
                    PredefinedLogicsLoader.JAR_PATH, fileName);
            LogicXMLParser lxmlparser = new LogicXMLParser();
            try {
                logic = lxmlparser.parseLogic(completeFileName);
            } catch (ParseException ex) {
                Lotrec.println("Execption while reading the logic file " + completeFileName);
                Lotrec.println(ex.getMessage());
                //No Logic For Sub-Components
                defautConstruct = true;
                DialogsFactory.parseLogicErrorMessage(mainFrame,
                        "Execption while reading the logic file " +
                        completeFileName + "\n\n" + ex.getMessage());
                return;
            }
        } else {
            if (mainFrame.getLoadedLogicsPanel().
                    logicFileAlreadyOpen(fileName, fileDir)) {
                DialogsFactory.openLogicFileAlreadyOpenWarning(mainFrame, fileName);
                return;
            }
            //Case of Existing logic file opened:
            String completeFileName = getCompleteFileName();//fileName & fileDir must be set before calling this method..
            LogicXMLParser lxmlparser = new LogicXMLParser();
            try {
                logic = lxmlparser.parseLogic(completeFileName);
            } catch (ParseException ex) {
                Lotrec.println("Execption while reading the logic file " + completeFileName);
                Lotrec.println(ex.getMessage());
                //No Logic For Sub-Components
                defautConstruct = true;
                DialogsFactory.parseLogicErrorMessage(mainFrame,
                        "Execption while reading the logic file " +
                        completeFileName + "\n\n" + ex.getMessage());
                return;
            }
        }
        //Change logic name before it will be displayed in initComponents();
        mainFrame.getLoadedLogicsPanel().changeLogicNameIfAlreadyUsed(this);
        initComponents();
        //exitLabel must be created before!!
        //fileName must be set before!
        mainFrame.getLoadedLogicsPanel().addLogicTab(this);
        mainFrame.enableLogicMenuItems();
        mainFrame.adjustHorizontalSplitPane();
//        if (logic.getTestingFormulae().size() > 0) {
//            mainFrame.getNewControlsPanel().buildSatPremodel(logic.getTestingFormulae().get(0).getFormula());
//        } else {
//            mainFrame.getNewControlsPanel().buildSatPremodel(new MarkedExpression(new ConstantExpression("Compose your own formula")));
//        }
    }

//    public javax.swing.JLabel getExitLabel() {
//        return this.lblExit;
//    }
    public String getTitle() {
        if (fileName == null) {
            return "New Logic (No File)";
        } else {
            return fileName;
        }
    }

    public String getToolTip() {
        if (fileDir == null) {
            if (fileName == null) {
                return "No path";
            } else {
                return "Predefined Logic (Hidden File)";
            }
        } else {
            return fileDir;
        }
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getCompleteFileName() {
        return this.fileDir + System.getProperty("file.separator") + this.fileName;
    }

    public boolean isNewEmptyLogic() {
        return this.fileName == null;
    }

    public boolean isPredefinedLogic() {
        return this.fileDir == null;
    }

    public boolean isModifiedAndNotSaved() {
        return modifiedAndNotSaved;
    }

    public void setModifiedAndNotSaved(boolean modifiedAndNotSaved) {
        this.modifiedAndNotSaved = modifiedAndNotSaved;
        if (modifiedAndNotSaved) {
            mainFrame.getLoadedLogicsPanel().setTabTitle(this, getTitle() + "(*)");
        } else {
            mainFrame.getLoadedLogicsPanel().setTabTitle(this, getTitle());
        }
//        System.out.println("Logic " + logic.getName() + " has been modified..");
    }

    public void showLogicInfo(Point location) {
        dlgLogicInfo.pack();
        int x = (getWidth() - dlgLogicInfo.getWidth()) / 2;
        int y = (getHeight() - dlgLogicInfo.getHeight()) / 2;
        Point p = location;
        p.translate(x, y);
        dlgLogicInfo.setLocation(p);
        dlgLogicInfo.setVisible(true);
    }

    public void hideLogicInfo() {
        dlgLogicInfo.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dlgLogicInfo = new javax.swing.JDialog();
        logicInfoPanel = defautConstruct?          
        new LogicInfoPanel()          
        :
        new LogicInfoPanel(this);
        conRulesTabPane = new javax.swing.JTabbedPane();
        connTabPanel = defautConstruct? 
        new ConnTabPanel() 
        :
        new ConnTabPanel(this);
        rulesTabPanel = defautConstruct? 
        new RulesTabPanel() 
        :
        new RulesTabPanel(this);
        stratTabPanel = defautConstruct? 
        new StratTabPanel() 
        :
        new StratTabPanel(this);
        testingFormulaePanel = defautConstruct? 
        new TestingFormulaePanel() 
        :
        new TestingFormulaePanel(this);

        dlgLogicInfo.setTitle("Logic Description");
        dlgLogicInfo.setResizable(false);

        org.jdesktop.layout.GroupLayout dlgLogicInfoLayout = new org.jdesktop.layout.GroupLayout(dlgLogicInfo.getContentPane());
        dlgLogicInfo.getContentPane().setLayout(dlgLogicInfoLayout);
        dlgLogicInfoLayout.setHorizontalGroup(
            dlgLogicInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logicInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );
        dlgLogicInfoLayout.setVerticalGroup(
            dlgLogicInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logicInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        setOpaque(false);

        conRulesTabPane.addTab("Connectors", connTabPanel);
        conRulesTabPane.addTab("Rules", rulesTabPanel);
        conRulesTabPane.addTab("Strategies", stratTabPanel);
        conRulesTabPane.addTab("Predefined Formulas", testingFormulaePanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(conRulesTabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, conRulesTabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    //The close button
//        private void lblExitMouseEntered(java.awt.event.MouseEvent evt) {
//        lblExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/exitEnteredS.JPG"))); // NOI18N
//}
//
//    private void lblExitMousePressed(java.awt.event.MouseEvent evt) {
//        lblExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/exitPressedS.JPG"))); // NOI18N
//}
//
//    private void lblExitMouseExited(java.awt.event.MouseEvent evt) {
//        lblExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/exitNormalS.JPG"))); // NOI18N
//}
//
//    private void lblExitMouseReleased(java.awt.event.MouseEvent evt) {
//        lblExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/exitNormalS.JPG"))); // NOI18N
//}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane conRulesTabPane;
    private lotrec.gui.logicspane.ConnTabPanel connTabPanel;
    private javax.swing.JDialog dlgLogicInfo;
    private lotrec.gui.logicspane.LogicInfoPanel logicInfoPanel;
    private lotrec.gui.logicspane.RulesTabPanel rulesTabPanel;
    private lotrec.gui.logicspane.StratTabPanel stratTabPanel;
    private lotrec.gui.logicspane.TestingFormulaePanel testingFormulaePanel;
    // End of variables declaration//GEN-END:variables
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public lotrec.gui.logicspane.StratTabPanel getStratTabPanel() {
        return stratTabPanel;
    }

    public lotrec.gui.logicspane.RulesTabPanel getRulesTabPanel() {
        return rulesTabPanel;
    }

    public lotrec.gui.logicspane.TestingFormulaePanel getTestingFormulaePanel() {
        return testingFormulaePanel;
    }
}

/*
 * MainFrame.java
 *
 * Created on 6 fÃ©vrier 2008, 10:47
 */
package lotrec.gui;

import cytoscape.view.CyNetworkView;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import org.freehep.util.UserProperties;
import java.awt.Point;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import lotrec.Lotrec;
import lotrec.PredefinedLogicsLoader;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;
import lotrec.gui.logicspane.LogicDefTab;
import lotrec.parser.exceptions.GraphXMLParserException;
import lotrec.resources.ResourcesProvider;
import lotrec.gui.dialogs.*;
import lotrec.parser.GraphXMLParser;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;

/**
 *
 * @author  said
 */
public class MainFrame extends javax.swing.JFrame {

    private int vSplitLocation = 0;//center, -1 most left, 1 most right
    private int lastHSplitLocation = 0;
    private int lastVSplitLocation = 0;
    private java.io.File currentDirectory = new java.io.File(System.getProperty("user.dir"));
    private ControlsPanel controlsPanel;
    private String visiteOurWebStie = "Visit our web site...";
    private String newLogicFile = "New logic file...";
    private String openLogicFile = "Open logic file...";
    private String modelCheckingFileName = "Model Checking (monomodal)...";
    private String kLogicName = "Modal logic K...";
    private String otherLogics = "Others...";

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        controlsPanel = new ControlsPanel(this);
        this.spltHSplit.setRightComponent(controlsPanel);

        rdbtnHirerachic.setActionCommand(CyTableauDisplayer.Hierarchic);
        rdbtnCircular.setActionCommand(CyTableauDisplayer.Circular);
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setBounds(0, 0, screenSize.width, screenSize.height);
        this.setExtendedState(MainFrame.MAXIMIZED_BOTH);
        this.jMenuBar1.remove(this.mnuControl);
        fillPredefinedLogicsList();
        disableLogicMenuItems();

//        openNewEmptyLogicFile();
//        int loc = (int) ((spltVSplit.getBounds().getWidth() - spltVSplit.getDividerSize()) / 2);
//        int loc = getLogicsPanel().getWidth();
//        spltVSplit.setDividerLocation(loc);
////        loc = (int) ((spltHSplit.getBounds().getHeight() - spltHSplit.getDividerSize()) / 2);
//        loc =  ((int) (spltHSplit.getBounds().getHeight())) - getControlsPanel().getHeight();
////        loc = getLogicsPanel().getHeight();
//        spltHSplit.setDividerLocation(loc);
//        closeActiveLogicTab();

//        getVSplitToLeftButton().addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (vSplitLocation == 1) {
//                    //to the center
//                    int loc = (int) ((spltVSplit.getBounds().getWidth() - spltVSplit.getDividerSize()) / 2);
//                    spltVSplit.setDividerLocation(loc);
//                    vSplitLocation = 0;
//                } else if (vSplitLocation == 0) {
//                    //to the most left
//                    int loc = 0;
//                    spltVSplit.setDividerLocation(loc);
//                    vSplitLocation = -1;
//                } else {
//                    //nothing to do
//                }
//            }
//        });
//        getVSplitToRighttButton().addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (vSplitLocation == -1) {
//                    //to the center
//                    int loc = (int) ((spltVSplit.getBounds().getWidth() - spltVSplit.getDividerSize()) / 2);
//                    spltVSplit.setDividerLocation(loc);
//                    vSplitLocation = 0;
//                } else if (vSplitLocation == 0) {
//                    //to the most right
//                    int loc = (int) (spltVSplit.getBounds().getWidth() - spltVSplit.getDividerSize());
//                    spltVSplit.setDividerLocation(loc);
//                    vSplitLocation = 1;
//                } else {
//                    //nothing to do
//                }
//            }
//        });
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                terminate();
            }
        });
//        dlgOldControlPanel.pack();
//        Point p = getLocation();
//        p.translate(
//                (int) (getWidth() / 2 - dlgOldControlPanel.getWidth() / 2),
//                (int) (getHeight() / 2 - dlgOldControlPanel.getHeight() / 2));
//        dlgOldControlPanel.setLocation(p);
//        dlgOldControlPanel.setVisible(true);

        lblModalLogicK.setText(kLogicName);
        lblModelChecking.setText(modelCheckingFileName);
        lblOthers.setText(otherLogics);
        lblOpenFile.setText(openLogicFile);
        lblNewFile.setText(newLogicFile);
    }

    public NewControlsPanel getNewControlsPanel() {
        return this.newControlsPanel;
    }

    public void terminate() {
        //Eventual safety closing...
        System.exit(0);
    }

    //The following two functions are disactivated since they should be completed
    //by a solution for enabling/disabling correctly the save and saveAs buttons
    public void enableLogicMenuItems() {
//        mnuitLogicFileInfo.setEnabled(true);
//        mnuitClose.setEnabled(true);
    }

    public void disableLogicMenuItems() {
//        mnuitLogicFileInfo.setEnabled(false);
//        mnuitClose.setEnabled(false);
    }

    void setSaveEnabled(boolean b) {
        mnuitSave.setEnabled(b);
    }

    public void showWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getTableauxPanel().getCytoscapeFrame().setCursor(
                Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void hideWaitCursor() {
        getTableauxPanel().getCytoscapeFrame().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void makeTableauxPanelFullScreen(boolean fullscreen) {
        if (fullscreen) {
//            Double location = new Double(getWidth()/2.3);
//            spltVSplit.setDividerLocation(location.intValue());
            spltVSplit.setDividerLocation(lastVSplitLocation);
        } else {
            lastVSplitLocation = spltVSplit.getDividerLocation();
            spltVSplit.setDividerLocation(0);
        }
    }

    public void showLogicsPanel(boolean show) {
        if (show) {
//            Double location = new Double(getWidth()/2.3);
//            spltVSplit.setDividerLocation(location.intValue());
            spltVSplit.setDividerLocation(lastVSplitLocation);
        } else {
            lastVSplitLocation = spltVSplit.getDividerLocation();
            spltVSplit.setDividerLocation(0);
        }
    }

    public void showControlsPanel(boolean show) {
        if (show) {
//            Double location = new Double(getHeight()/2.3);
//            spltHSplit.setDividerLocation(location.intValue());
            spltHSplit.setDividerLocation(lastHSplitLocation);
        } else {
            lastHSplitLocation = spltHSplit.getDividerLocation();
            spltHSplit.setDividerLocation(0);
        }
    }

    public void showTableauxPanel(boolean show) {
        if (show) {
        } else {
        }
    }

    private void exportToPDF(String completeFileName, CyNetworkView currentNetworkView) {
        try {
            UserProperties p = new UserProperties();
            p.setProperty(PDFGraphics2D.PAGE_MARGINS, new Insets(0, 0, 0, 0));
//            p.setProperty(PDFGraphics2D.ORIENTATION, PageConstants.BEST_FIT); //Doesn't work properly!!
            if ((currentNetworkView.getComponent().getSize().getHeight() / currentNetworkView.getComponent().getSize().getWidth()) > 1) {
                p.setProperty(PDFGraphics2D.ORIENTATION, PageConstants.PORTRAIT);
            } else {
                p.setProperty(PDFGraphics2D.ORIENTATION, PageConstants.LANDSCAPE);
            }
            p.setProperty(PDFGraphics2D.PAGE_SIZE, PageConstants.A4);
//            p.setProperty(PDFGraphics2D.PAGE_SIZE, PageConstants.CUSTOM_PAGE_SIZE);
            p.setProperty(PDFGraphics2D.FIT_TO_PAGE, true);
            File imgFile = new File(completeFileName);
            VectorGraphics g = new PDFGraphics2D(imgFile, currentNetworkView.getComponent().getSize());
            g.setProperties(p);
            g.startExport();
            currentNetworkView.getComponent().print(g);
            g.endExport();
//            exportPDFBB(completeFileName, currentNetworkView, p);
        } catch (Exception e) {
            System.out.println("Error occured during exporting the premodel as an image.\n" + e.getMessage());
        }
    }

//    private void exportPDFBB(String completeFileName, CyNetworkView currentNetworkView, UserProperties p) {
//        try {
//            File imgFile = new File(completeFileName+".bb");
//            PSGraphics2D g = new PSGraphics2D(imgFile, currentNetworkView.getComponent().getSize());
//            g.setProperties(p);
//            g.startExport();
//            currentNetworkView.getComponent().print(g);
//            g.endExport();
//        } catch (Exception e) {
//            System.out.println("Error occured during exporting the premodel as an image.\n" + e.getMessage());
//        }
//    }
    private void exportToPNG(String completeFileName, CyNetworkView currentNetworkView) {
        try {
            UserProperties p = new UserProperties();
//            p.setProperty("PageSize", "A5");
            File imgFile = new File(completeFileName);
            VectorGraphics g = new ImageGraphics2D(imgFile, currentNetworkView.getComponent().getSize(), "png");
            g.setProperties(p);
            g.startExport();
            currentNetworkView.getComponent().print(g);
            g.endExport();
        } catch (Exception e) {
            System.out.println("Error occured during exporting the premodel as an image.\n" + e.getMessage());
        }
    }

    private void exportToPS(String completeFileName, CyNetworkView currentNetworkView) {
        try {
            UserProperties p = new UserProperties();
            p.setProperty(PSGraphics2D.PAGE_MARGINS, new Insets(0, 0, 0, 0));
//            p.setProperty(PSGraphics2D.ORIENTATION, PageConstants.BEST_FIT); // Doesn't work properly!! and makes rotated images that remain rotated in LaTeX
            if ((currentNetworkView.getComponent().getSize().getHeight() / currentNetworkView.getComponent().getSize().getWidth()) > 1) {
                p.setProperty(PDFGraphics2D.ORIENTATION, PageConstants.PORTRAIT);
            } else {
                p.setProperty(PDFGraphics2D.ORIENTATION, PageConstants.LANDSCAPE);
            }
            p.setProperty(PSGraphics2D.PAGE_SIZE, PageConstants.A4);
//            p.setProperty(PSGraphics2D.PAGE_SIZE, PageConstants.CUSTOM_PAGE_SIZE);
            p.setProperty(PSGraphics2D.FIT_TO_PAGE, true);
            File imgFile = new File(completeFileName);
            VectorGraphics g = new PSGraphics2D(imgFile, currentNetworkView.getComponent().getSize());
            g.setProperties(p);
            g.startExport();
            currentNetworkView.getComponent().print(g);
            g.endExport();
        } catch (Exception e) {
            System.out.println("Error occured during exporting the premodel as an image.\n" + e.getMessage());
        }
    }

    private void fillPredefinedLogicsList() {
        String[] logicsNames = PredefinedLogicsLoader.LOGICS_FILES;
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < logicsNames.length; i++) {
            listModel.addElement(logicsNames[i]);
        }
        lstPredefinedLogics.setModel(listModel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btngrpLanguage = new javax.swing.ButtonGroup();
        dlgPredefinedLogics = new javax.swing.JDialog();
        pnlList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstPredefinedLogics = new javax.swing.JList();
        pnlBtns = new javax.swing.JPanel();
        btnOpen = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        dlgTaskPane = new javax.swing.JDialog();
        pnlHead = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlTasks = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblModelChecking = new javax.swing.JLabel();
        lblModalLogicK = new javax.swing.JLabel();
        lblOthers = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblVisitWebSite = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblNewFile = new javax.swing.JLabel();
        lblOpenFile = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btngrpLayout = new javax.swing.ButtonGroup();
        btngrpDisplayMode = new javax.swing.ButtonGroup();
        dlgResults = new javax.swing.JDialog();
        dlgOldControlPanel = new javax.swing.JDialog();
        dlgOldHsplit = new javax.swing.JDialog();
        jDialog1 = new javax.swing.JDialog();
        newControlsPanel = new NewControlsPanel(this);
        spltVSplit = new javax.swing.JSplitPane();
        spltHSplit = new javax.swing.JSplitPane();
        loadedLogicsPanel = new LoadedLogicsPanel(this);
        tableauxPanel = new TableauxPanel(this);
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuControl = new javax.swing.JMenu();
        mnuShowHide = new javax.swing.JMenu();
        chkbxmnuitSHLogics = new javax.swing.JCheckBoxMenuItem();
        chkbxmnuitSHControls = new javax.swing.JCheckBoxMenuItem();
        chkbxmnuitSHTableaux = new javax.swing.JCheckBoxMenuItem();
        mnuLanguage = new javax.swing.JMenu();
        rdbtnEnglish = new javax.swing.JRadioButtonMenuItem();
        rdbtnFrench = new javax.swing.JRadioButtonMenuItem();
        mnusepControl = new javax.swing.JSeparator();
        mnuitExit = new javax.swing.JMenuItem();
        mnuLogic = new javax.swing.JMenu();
        mnuitNew = new javax.swing.JMenuItem();
        mnuitOpen = new javax.swing.JMenuItem();
        mnuitPredefinedLogics = new javax.swing.JMenuItem();
        mnusepLogic = new javax.swing.JSeparator();
        mnuitSave = new javax.swing.JMenuItem();
        mnuitSaveAs = new javax.swing.JMenuItem();
        mnuitSaveAll = new javax.swing.JMenuItem();
        mnusepLogic2 = new javax.swing.JSeparator();
        mnuitClose = new javax.swing.JMenuItem();
        mnuitCloseAll = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuitLogicDescription = new javax.swing.JMenuItem();
        mnuView = new javax.swing.JMenu();
        mnuLayout = new javax.swing.JMenu();
        rdbtnHirerachic = new javax.swing.JRadioButtonMenuItem();
        rdbtnCircular = new javax.swing.JRadioButtonMenuItem();
        rdbtnTileCascade = new javax.swing.JRadioButtonMenuItem();
        mnuPremodelsDisplayMode = new javax.swing.JMenu();
        rdbtnOnlyOne = new javax.swing.JRadioButtonMenuItem();
        rdbtnMany = new javax.swing.JRadioButtonMenuItem();
        rdbtnAllPremodels = new javax.swing.JRadioButtonMenuItem();
        mnuitPremodelsFilters = new javax.swing.JMenuItem();
        mnuPremodels = new javax.swing.JMenu();
        mnuitLoadPremodel = new javax.swing.JMenuItem();
        savePremodel = new javax.swing.JMenuItem();
        mnuitExportPremodel = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuitPremodelEditor = new javax.swing.JMenuItem();
        mnuitRunInfo = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuitQuickHelp = new javax.swing.JMenuItem();
        mnuitWebTutorial = new javax.swing.JMenuItem();
        mnuitHomePage = new javax.swing.JMenuItem();
        mnusepHelp = new javax.swing.JSeparator();
        mnuitAbout = new javax.swing.JMenuItem();

        dlgPredefinedLogics.setTitle("Predefined Logics");
        dlgPredefinedLogics.setAlwaysOnTop(true);
        dlgPredefinedLogics.setResizable(false);
        dlgPredefinedLogics.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgPredefinedLogicsWindowClosing(evt);
            }
        });

        lstPredefinedLogics.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "<Predefined Logics List>" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstPredefinedLogics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstPredefinedLogicsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstPredefinedLogics);

        org.jdesktop.layout.GroupLayout pnlListLayout = new org.jdesktop.layout.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlBtns.setLayout(new java.awt.GridBagLayout());

        btnOpen.setText("Open");
        btnOpen.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.insets = new java.awt.Insets(3, 4, 3, 4);
        pnlBtns.add(btnOpen, gridBagConstraints);

        btnCancel.setText("Cancel");
        btnCancel.setIconTextGap(0);
        btnCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new java.awt.Insets(3, 4, 3, 4);
        pnlBtns.add(btnCancel, gridBagConstraints);

        org.jdesktop.layout.GroupLayout dlgPredefinedLogicsLayout = new org.jdesktop.layout.GroupLayout(dlgPredefinedLogics.getContentPane());
        dlgPredefinedLogics.getContentPane().setLayout(dlgPredefinedLogicsLayout);
        dlgPredefinedLogicsLayout.setHorizontalGroup(
            dlgPredefinedLogicsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlBtns, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
            .add(pnlList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgPredefinedLogicsLayout.setVerticalGroup(
            dlgPredefinedLogicsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dlgPredefinedLogicsLayout.createSequentialGroup()
                .add(pnlList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlBtns, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        dlgTaskPane.setTitle("Task Pane");
        dlgTaskPane.setAlwaysOnTop(true);
        dlgTaskPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dlgTaskPane.setResizable(false);
        dlgTaskPane.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                dlgTaskPaneWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgTaskPaneWindowClosing(evt);
            }
        });

        pnlHead.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("How do you prefer to start?");

        org.jdesktop.layout.GroupLayout pnlHeadLayout = new org.jdesktop.layout.GroupLayout(pnlHead);
        pnlHead.setLayout(pnlHeadLayout);
        pnlHeadLayout.setHorizontalGroup(
            pnlHeadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeadLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addContainerGap(140, Short.MAX_VALUE))
        );
        pnlHeadLayout.setVerticalGroup(
            pnlHeadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHeadLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTasks.setOpaque(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Open Predefined Logic:");

        lblModelChecking.setText("Model Checking...");
        lblModelChecking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblModelCheckingMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblModelCheckingMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblModelCheckingMouseExited(evt);
            }
        });

        lblModalLogicK.setText("Modal Logic K...");
        lblModalLogicK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblModalLogicKMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblModalLogicKMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblModalLogicKMouseExited(evt);
            }
        });

        lblOthers.setText("Others...");
        lblOthers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblOthersMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblOthersMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblOthersMouseExited(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Create Your Own:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Open Existing File:");

        lblVisitWebSite.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblVisitWebSite.setText("Visit our web site");
        lblVisitWebSite.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblVisitWebSiteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblVisitWebSiteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblVisitWebSiteMouseExited(evt);
            }
        });

        jLabel10.setText("Tutorial... (up coming)");
        jLabel10.setEnabled(false);

        lblNewFile.setText("New Logic File...");
        lblNewFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNewFileMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblNewFileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblNewFileMouseExited(evt);
            }
        });

        lblOpenFile.setText("Open Logic File...");
        lblOpenFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblOpenFileMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblOpenFileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblOpenFileMouseExited(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlTasksLayout = new org.jdesktop.layout.GroupLayout(pnlTasks);
        pnlTasks.setLayout(pnlTasksLayout);
        pnlTasksLayout.setHorizontalGroup(
            pnlTasksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTasksLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlTasksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlTasksLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(pnlTasksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblModelChecking)
                            .add(lblOthers)
                            .add(lblModalLogicK)))
                    .add(jLabel2)
                    .add(pnlTasksLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lblOpenFile))
                    .add(jLabel7)
                    .add(jLabel6)
                    .add(pnlTasksLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(pnlTasksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel10)
                            .add(lblNewFile))
                        .add(18, 18, 18))
                    .add(lblVisitWebSite))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        pnlTasksLayout.setVerticalGroup(
            pnlTasksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTasksLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblModalLogicK)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblModelChecking)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblOthers)
                .add(18, 18, 18)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblOpenFile)
                .add(18, 18, 18)
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblNewFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel10)
                .add(18, 18, 18)
                .add(lblVisitWebSite)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setOpaque(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("LoTREC 2.0");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(254, Short.MAX_VALUE)
                .add(jLabel8)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel8)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout dlgTaskPaneLayout = new org.jdesktop.layout.GroupLayout(dlgTaskPane.getContentPane());
        dlgTaskPane.getContentPane().setLayout(dlgTaskPaneLayout);
        dlgTaskPaneLayout.setHorizontalGroup(
            dlgTaskPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlHead, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTasks, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgTaskPaneLayout.setVerticalGroup(
            dlgTaskPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dlgTaskPaneLayout.createSequentialGroup()
                .add(pnlHead, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTasks, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 42, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        dlgResults.setTitle("Results");
        dlgResults.setAlwaysOnTop(true);

        org.jdesktop.layout.GroupLayout dlgResultsLayout = new org.jdesktop.layout.GroupLayout(dlgResults.getContentPane());
        dlgResults.getContentPane().setLayout(dlgResultsLayout);
        dlgResultsLayout.setHorizontalGroup(
            dlgResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1019, Short.MAX_VALUE)
        );
        dlgResultsLayout.setVerticalGroup(
            dlgResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 971, Short.MAX_VALUE)
        );

        dlgOldControlPanel.setTitle("What to do:");
        dlgOldControlPanel.setAlwaysOnTop(true);
        dlgOldControlPanel.setResizable(false);

        org.jdesktop.layout.GroupLayout dlgOldControlPanelLayout = new org.jdesktop.layout.GroupLayout(dlgOldControlPanel.getContentPane());
        dlgOldControlPanel.getContentPane().setLayout(dlgOldControlPanelLayout);
        dlgOldControlPanelLayout.setHorizontalGroup(
            dlgOldControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 456, Short.MAX_VALUE)
        );
        dlgOldControlPanelLayout.setVerticalGroup(
            dlgOldControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 211, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout dlgOldHsplitLayout = new org.jdesktop.layout.GroupLayout(dlgOldHsplit.getContentPane());
        dlgOldHsplit.getContentPane().setLayout(dlgOldHsplitLayout);
        dlgOldHsplitLayout.setHorizontalGroup(
            dlgOldHsplitLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 207, Short.MAX_VALUE)
        );
        dlgOldHsplitLayout.setVerticalGroup(
            dlgOldHsplitLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 155, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jDialog1Layout = new org.jdesktop.layout.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 589, Short.MAX_VALUE)
            .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jDialog1Layout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(newControlsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 439, Short.MAX_VALUE)
            .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jDialog1Layout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(newControlsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LoTREC 2.0 - Tableaux Theorem Prover");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        spltVSplit.setDividerLocation(500);
        spltVSplit.setDividerSize(8);
        spltVSplit.setOneTouchExpandable(true);
        spltVSplit.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                spltVSplitComponentResized(evt);
            }
        });
        spltVSplit.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                spltVSplitPropertyChange(evt);
            }
        });

        spltHSplit.setDividerLocation(550);
        spltHSplit.setDividerSize(8);
        spltHSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spltHSplit.setOneTouchExpandable(true);
        spltHSplit.setTopComponent(loadedLogicsPanel);

        spltVSplit.setLeftComponent(spltHSplit);
        spltVSplit.setRightComponent(tableauxPanel);

        mnuControl.setText("Control");

        mnuShowHide.setText("Show/Hide Panels");

        chkbxmnuitSHLogics.setSelected(true);
        chkbxmnuitSHLogics.setText("Logics");
        chkbxmnuitSHLogics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkbxmnuitSHLogicsActionPerformed(evt);
            }
        });
        mnuShowHide.add(chkbxmnuitSHLogics);

        chkbxmnuitSHControls.setSelected(true);
        chkbxmnuitSHControls.setText("Controls");
        mnuShowHide.add(chkbxmnuitSHControls);

        chkbxmnuitSHTableaux.setSelected(true);
        chkbxmnuitSHTableaux.setText("Tableaux");
        chkbxmnuitSHTableaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkbxmnuitSHTableauxActionPerformed(evt);
            }
        });
        mnuShowHide.add(chkbxmnuitSHTableaux);

        mnuControl.add(mnuShowHide);

        mnuLanguage.setText("Language");

        btngrpLanguage.add(rdbtnEnglish);
        rdbtnEnglish.setSelected(true);
        rdbtnEnglish.setText("English");
        rdbtnEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnEnglishActionPerformed(evt);
            }
        });
        mnuLanguage.add(rdbtnEnglish);

        btngrpLanguage.add(rdbtnFrench);
        rdbtnFrench.setText("French");
        rdbtnFrench.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnFrenchActionPerformed(evt);
            }
        });
        mnuLanguage.add(rdbtnFrench);

        mnuControl.add(mnuLanguage);
        mnuControl.add(mnusepControl);

        mnuitExit.setText("Exit");
        mnuitExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitExitActionPerformed(evt);
            }
        });
        mnuControl.add(mnuitExit);

        jMenuBar1.add(mnuControl);

        mnuLogic.setText("Logic");
        mnuLogic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLogicActionPerformed(evt);
            }
        });
        mnuLogic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuLogicMouseClicked(evt);
            }
        });

        mnuitNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mnuitNew.setText("New...");
        mnuitNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitNewActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitNew);

        mnuitOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuitOpen.setText("Open...");
        mnuitOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitOpenActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitOpen);

        mnuitPredefinedLogics.setText("Predefined Logics...");
        mnuitPredefinedLogics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitPredefinedLogicsActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitPredefinedLogics);
        mnuLogic.add(mnusepLogic);

        mnuitSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuitSave.setText("Save...");
        mnuitSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitSaveActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitSave);

        mnuitSaveAs.setText("Save As...");
        mnuitSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitSaveAsActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitSaveAs);

        mnuitSaveAll.setText("Save All...");
        mnuitSaveAll.setEnabled(false);
        mnuLogic.add(mnuitSaveAll);
        mnuLogic.add(mnusepLogic2);

        mnuitClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        mnuitClose.setText("Close");
        mnuitClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitCloseActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitClose);

        mnuitCloseAll.setText("Close All");
        mnuitCloseAll.setEnabled(false);
        mnuLogic.add(mnuitCloseAll);
        mnuLogic.add(jSeparator1);

        mnuitLogicDescription.setText("Logic Description...");
        mnuitLogicDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitLogicDescriptionActionPerformed(evt);
            }
        });
        mnuLogic.add(mnuitLogicDescription);

        jMenuBar1.add(mnuLogic);

        mnuView.setText("View");

        mnuLayout.setText("Premodels Layout");

        btngrpLayout.add(rdbtnHirerachic);
        rdbtnHirerachic.setSelected(true);
        rdbtnHirerachic.setText("Hirerachic");
        rdbtnHirerachic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnHirerachicActionPerformed(evt);
            }
        });
        mnuLayout.add(rdbtnHirerachic);

        btngrpLayout.add(rdbtnCircular);
        rdbtnCircular.setText("Circular");
        rdbtnCircular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnCircularActionPerformed(evt);
            }
        });
        mnuLayout.add(rdbtnCircular);

        mnuView.add(mnuLayout);

        rdbtnTileCascade.setText("Tile premodels window");
        rdbtnTileCascade.setToolTipText("Toggle the Tile/Cascade display premodels windows");
        rdbtnTileCascade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnTileCascadeActionPerformed(evt);
            }
        });
        mnuView.add(rdbtnTileCascade);

        mnuPremodelsDisplayMode.setText("Premodels Display Mode");

        btngrpDisplayMode.add(rdbtnOnlyOne);
        rdbtnOnlyOne.setSelected(true);
        rdbtnOnlyOne.setText("Only selected one");
        rdbtnOnlyOne.setActionCommand(TableauxPanel.SINGLE_DISPLAY_MODE);
        rdbtnOnlyOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDisplayMode(evt);
            }
        });
        mnuPremodelsDisplayMode.add(rdbtnOnlyOne);

        btngrpDisplayMode.add(rdbtnMany);
        rdbtnMany.setText("Many selected ones");
        rdbtnMany.setActionCommand(TableauxPanel.MULTIPLE_DISPLAY_MODE);
        rdbtnMany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDisplayMode(evt);
            }
        });
        mnuPremodelsDisplayMode.add(rdbtnMany);

        btngrpDisplayMode.add(rdbtnAllPremodels);
        rdbtnAllPremodels.setText("All premodels");
        rdbtnAllPremodels.setActionCommand(TableauxPanel.ALL_DISPLAY_MODE);
        rdbtnAllPremodels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDisplayMode(evt);
            }
        });
        mnuPremodelsDisplayMode.add(rdbtnAllPremodels);

        mnuView.add(mnuPremodelsDisplayMode);

        mnuitPremodelsFilters.setText("Premodels Filters...");
        mnuitPremodelsFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitPremodelsFiltersActionPerformed(evt);
            }
        });
        mnuView.add(mnuitPremodelsFilters);

        jMenuBar1.add(mnuView);

        mnuPremodels.setText("Premodels");

        mnuitLoadPremodel.setText("Load premodel...");
        mnuitLoadPremodel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitLoadPremodelActionPerformed(evt);
            }
        });
        mnuPremodels.add(mnuitLoadPremodel);

        savePremodel.setText("Save selected premodel...");
        savePremodel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePremodelActionPerformed(evt);
            }
        });
        mnuPremodels.add(savePremodel);

        mnuitExportPremodel.setText("Export Premodel...");
        mnuitExportPremodel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitExportPremodelActionPerformed(evt);
            }
        });
        mnuPremodels.add(mnuitExportPremodel);
        mnuPremodels.add(jSeparator2);

        mnuitPremodelEditor.setText("Premodels Editor...");
        mnuitPremodelEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitPremodelEditorActionPerformed(evt);
            }
        });
        mnuPremodels.add(mnuitPremodelEditor);

        mnuitRunInfo.setText("Run Info Window");
        mnuitRunInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitRunInfoActionPerformed(evt);
            }
        });
        mnuPremodels.add(mnuitRunInfo);

        jMenuBar1.add(mnuPremodels);

        mnuHelp.setText("Help");

        mnuitQuickHelp.setText("Quick Help...");
        mnuitQuickHelp.setEnabled(false);
        mnuHelp.add(mnuitQuickHelp);

        mnuitWebTutorial.setText("Tutorial...");
        mnuitWebTutorial.setEnabled(false);
        mnuHelp.add(mnuitWebTutorial);

        mnuitHomePage.setText("Home Page...");
        mnuitHomePage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitHomePageActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuitHomePage);
        mnuHelp.add(mnusepHelp);

        mnuitAbout.setText("About");
        mnuitAbout.setEnabled(false);
        mnuHelp.add(mnuitAbout);

        jMenuBar1.add(mnuHelp);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spltVSplit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spltVSplit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void chkbxmnuitSHLogicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkbxmnuitSHLogicsActionPerformed
    }//GEN-LAST:event_chkbxmnuitSHLogicsActionPerformed

    private void changeLanguageToEnglish() {
        ResourcesProvider.setCurrentLocale(new java.util.Locale("en", "US"));
        refresh();
    }

    private void changeLanguageToFrensh() {
        ResourcesProvider.setCurrentLocale(new java.util.Locale("fr", "FR"));
        refresh();
    }

    private void rdbtnEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnEnglishActionPerformed
    }//GEN-LAST:event_rdbtnEnglishActionPerformed

    private void rdbtnFrenchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnFrenchActionPerformed
    }//GEN-LAST:event_rdbtnFrenchActionPerformed

    private void mnuitOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitOpenActionPerformed
        openExistingLogicFile();
}//GEN-LAST:event_mnuitOpenActionPerformed

    private boolean openExistingLogicFile() {
        boolean takPaneWasShowing = false;
        if (dlgTaskPane.isShowing()) {
            takPaneWasShowing = true;
            hideTaskPane();
        }
        OpenLogicDialog opLgcDlg = new OpenLogicDialog();
        opLgcDlg.setCurrentDirectory(currentDirectory);
        int returnVal = opLgcDlg.showOpenDialog(this);
        if (returnVal == OpenLogicDialog.APPROVE_OPTION) {
            currentDirectory = opLgcDlg.getCurrentDirectory();
            showWaitCursor();
            new LogicDefTab(opLgcDlg.getFileName(),
                    currentDirectory.getAbsolutePath(), this);
            hideWaitCursor();
            return true;
        } else {
            if (takPaneWasShowing) {
                showTaskPane();
            }
//            System.out.println("Open command cancelled by user.");
            return false;
        }
    }

    private void mnuitSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitSaveActionPerformed
        if (getLoadedLogicsPanel().getSelectedLogicDefTab() == null) {
//            System.out.println("There is no loaded logic..");
//            System.out.println("The action Save could not precise a selected logic in the Logics panel to be saved..");
        } else {
            LogicDefTab logicDefTab = getLoadedLogicsPanel().getSelectedLogicDefTab();
            //IT SHOULD BE THE:
            //Case it is Opened Existing XML File,
            //Save it in the same file   
            if (logicDefTab.isModifiedAndNotSaved()) {
                String completeFileName = logicDefTab.getCompleteFileName();
                //Save it
//                System.out.println("(Opened Existing) Logic File will be saved in its original file: " + completeFileName);
                Lotrec.saveLogicFile(logicDefTab.getLogic(), completeFileName);
                logicDefTab.setModifiedAndNotSaved(false);
            } else {
//                System.out.println("(Opened Existing) Logic File had not been modified.. No need to save it");
            }
        }
}//GEN-LAST:event_mnuitSaveActionPerformed

    public void saveLogicDefTab(LogicDefTab logicDefTab) {
//        System.out.println("Logic to be Saved is: " + logicDefTab.getLogic().getName());
        if (logicDefTab.isNewEmptyLogic()) {
            //Case of "Empty New Logic"
            //Show the Save As dialog
            // - file name "New Logic"    
//            System.out.println("New Empty Logic should be Saved As");
            saveLogicDefTabAs(logicDefTab);
        //After Save As the Empty New Logic
        //turns into an Existing Opened Logic File
        //related to the fileName and directory chosen by the user
//            System.out.println("logic " + logicDefTab.getLogic().getName() +
//                    " Saved As in file: " + logicDefTab.getCompleteFileName());
        } else if (logicDefTab.isPredefinedLogic()) {
            //Case it's a predefined logic
            //Dialog message: Predefined logics could not be saved in the same file, 
            //Do you want to "Save it As"?                
//            System.out.println("Predefined Logic: ask for Save As");
//            int choice = 0; //Dialog Yes, No...
//            if (choice == 0) {
            //Save As
            saveLogicDefTabAs(logicDefTab);
        //After Save As the Empty New Logic
        //turns into an Existing Opened Logic File
        //related to the fileName and directory chosen by the user
//            System.out.println("logic " + logicDefTab.getLogic().getName() +
//                    " Saved As in file: " + logicDefTab.getCompleteFileName());
//            } else {
//            //Nothing to do..
//            }
        } else {
            //Case it is Opened Existing XML File,
            //Save it in the same file                
            String completeFileName = logicDefTab.getCompleteFileName();
            //Save it
//            System.out.println("(Opened Existing) Logic File will be saved in its original file: " + completeFileName);
            Lotrec.saveLogicFile(logicDefTab.getLogic(), completeFileName);
            logicDefTab.setModifiedAndNotSaved(false);
        }
    }

    private void saveLogicDefTabAs(LogicDefTab logicDefTab) {
        Logic logicToSaveAs = logicDefTab.getLogic();
        String logicFileToSaveAs = logicDefTab.getFileName();
        if (logicDefTab.isNewEmptyLogic()) {
            logicFileToSaveAs = "New Logic";
        } else {
//            logicFileToSaveAs = "Copy Of " + logicFileToSaveAs;
        }
//        System.out.println("Logic to be Saved As is: " + logicToSaveAs.getName());
        SaveAsLogicDialog svAsLgcDlg = new SaveAsLogicDialog();
        svAsLgcDlg.setCurrentDirectory(currentDirectory);
        svAsLgcDlg.setFileName(logicFileToSaveAs);
        int returnVal = svAsLgcDlg.showSaveDialog(this);
        if (returnVal == SaveAsLogicDialog.APPROVE_OPTION) {
            currentDirectory = svAsLgcDlg.getCurrentDirectory();
            String fileDirName = currentDirectory.getAbsolutePath();
            String fileName = svAsLgcDlg.getFileName();
            String completeFileName = svAsLgcDlg.getCompleteFileName();
//            System.out.println("completeFileName is : " + completeFileName);
            File file = new File(completeFileName);
            if (file.exists() && (DialogsFactory.fileExistsWarning(this, completeFileName) == 1)) {
//                System.out.println("Logic file: " + file + " is already created.");
                saveLogicDefTabAs(logicDefTab);
            } else {
                if (this.getLoadedLogicsPanel().logicFileAlreadyOpen(fileName, fileDirName)) {
                    DialogsFactory.saveLogicFileAlreadyOpenWarning(this, fileName);
                    saveLogicDefTabAs(logicDefTab);
                    return;
                }
                if (logicDefTab.isNewEmptyLogic() || logicDefTab.isPredefinedLogic()) {
                    //Case of New Empty Logic
                    //OR Case of Predefined Logic
                    logicDefTab.setFileName(fileName);
                    logicDefTab.setFileDir(fileDirName);
                    logicDefTab.setModifiedAndNotSaved(false);
                //Cause we want them to turn into Existing Opened Files
                } else {
                    //Case Opened Existing File
                    // - Don't change its ModifiedAndNotSaved status
                    // - Don't change its fileName & fileDir
                }
                performSaveAs(logicToSaveAs,
                        fileDirName,
                        completeFileName);
                this.setSaveEnabled(true);
            //Cause else, it will not be activated until going to another LogicDefTab,
            //then coming back to this saved-as LogicDefTab...
            //In fact: setSaveEnabled is called only when LoadedLogicsPanel state changes
            //which only happens by clicking on the tabs...
            }
        } else {
//            System.out.println("Save command cancelled by user.");
        }
    }

    private void performSaveAs(Logic logicToSaveAs, String fileDirName, String completeFileName) {
//        System.out.println(
//                "Logic: " + logicToSaveAs.getName() +
//                " is to be Saved As in the file: " + completeFileName + ".");
//        FileUtils.copyLogicDtdTo(fileDirName);
//        String originalName = logicToSaveAs.getName();
//        logicToSaveAs.setName("Copy Of " + originalName);
        showWaitCursor();
        Lotrec.saveLogicFile(logicToSaveAs, completeFileName);
//        logicToSaveAs.setName(originalName);
        hideWaitCursor();
    }

    private void mnuitSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitSaveAsActionPerformed
        if (getLoadedLogicsPanel().getSelectedLogicDefTab() == null) {
//            System.out.println("There is no loaded logic..");
//            System.out.println("The action Save As could not precise a selected logic in the Logics panel to be saved..");
        } else {
            this.saveLogicDefTabAs(getLoadedLogicsPanel().getSelectedLogicDefTab());
        }
}//GEN-LAST:event_mnuitSaveAsActionPerformed

    private void mnuitPredefinedLogicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitPredefinedLogicsActionPerformed
        showPredefinedLogisDialog();
    }//GEN-LAST:event_mnuitPredefinedLogicsActionPerformed

    private void mnuLogicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLogicActionPerformed
        if (getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            getLoadedLogicsPanel().getSelectedLogicDefTab().showLogicInfo(getLocation());
        }
}//GEN-LAST:event_mnuLogicActionPerformed

    private void mnuitNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitNewActionPerformed
        openNewEmptyLogicFile();
    }//GEN-LAST:event_mnuitNewActionPerformed

    private void openNewEmptyLogicFile() {
//        System.out.println("New logic created...");
        showWaitCursor();
        new LogicDefTab(null, null, this);
        hideWaitCursor();
        requestFocus();
    }

    public void adjustHorizontalSplitPane() {
        this.spltHSplit.setDividerLocation((int) this.getLoadedLogicsPanel().getMinimumSize().getHeight());
    }

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        openPredefinedLogic((String) lstPredefinedLogics.getSelectedValue());
        closePredefinedLogisDialog();
    }//GEN-LAST:event_btnOpenActionPerformed

    private void openPredefinedLogic(String predefinedLogicName) {
        String hiddenFileName = predefinedLogicName;
        hiddenFileName = hiddenFileName + ".xml";
        showWaitCursor();
        new LogicDefTab(hiddenFileName, null, this);
        hideWaitCursor();
        requestFocus();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        closePredefinedLogisDialog();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void dlgPredefinedLogicsWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgPredefinedLogicsWindowClosing
        closePredefinedLogisDialog();
    }//GEN-LAST:event_dlgPredefinedLogicsWindowClosing

    private void lblModalLogicKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModalLogicKMouseClicked
        openPredefinedLogic("Monomodal-K");

        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblModalLogicK.setText(kLogicName);
        lblModalLogicK.setForeground(new java.awt.Color(0, 0, 0));
}//GEN-LAST:event_lblModalLogicKMouseClicked

    private void lblOthersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOthersMouseClicked
        showPredefinedLogisDialog(dlgTaskPane);

        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblOthers.setText(otherLogics);
        lblOthers.setForeground(new java.awt.Color(0, 0, 0));
}//GEN-LAST:event_lblOthersMouseClicked

    private void lblModelCheckingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModelCheckingMouseClicked
        openPredefinedLogic("Model-Checking-Monomodal");

        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblModelChecking.setText(modelCheckingFileName);
        lblModelChecking.setForeground(new java.awt.Color(0, 0, 0));
}//GEN-LAST:event_lblModelCheckingMouseClicked

    private void lblOpenFileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenFileMouseClicked
        openExistingLogicFile();

        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblOpenFile.setText(openLogicFile);
        lblOpenFile.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_lblOpenFileMouseClicked

    private void lblNewFileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNewFileMouseClicked
        openNewEmptyLogicFile();

        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblNewFile.setText(newLogicFile);
        lblNewFile.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_lblNewFileMouseClicked

    private void lblModalLogicKMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModalLogicKMouseEntered
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblModalLogicK.setText("<html><u>" + kLogicName + "</u></html>");//<font color=blue></font>
        lblModalLogicK.setForeground(new java.awt.Color(51, 153, 255));
    }//GEN-LAST:event_lblModalLogicKMouseEntered

    private void lblModalLogicKMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModalLogicKMouseExited
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblModalLogicK.setText(kLogicName);
        lblModalLogicK.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_lblModalLogicKMouseExited

    private void lblModelCheckingMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModelCheckingMouseEntered
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblModelChecking.setText("<html><u>" + modelCheckingFileName + "</u></html>");//<font color=blue></font>
        lblModelChecking.setForeground(new java.awt.Color(51, 153, 255));
}//GEN-LAST:event_lblModelCheckingMouseEntered

    private void lblModelCheckingMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModelCheckingMouseExited
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblModelChecking.setText(modelCheckingFileName);
        lblModelChecking.setForeground(new java.awt.Color(0, 0, 0));
}//GEN-LAST:event_lblModelCheckingMouseExited

    private void lblOthersMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOthersMouseEntered
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblOthers.setText("<html><u>" + otherLogics + "</u></html>");//<font color=blue></font>
        lblOthers.setForeground(new java.awt.Color(51, 153, 255));
}//GEN-LAST:event_lblOthersMouseEntered

    private void lblOthersMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOthersMouseExited
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblOthers.setText(otherLogics);
        lblOthers.setForeground(new java.awt.Color(0, 0, 0));
}//GEN-LAST:event_lblOthersMouseExited

    private void lblOpenFileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenFileMouseEntered
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblOpenFile.setText("<html><u>" + openLogicFile + "</u></html>");//<font color=blue></font>
        lblOpenFile.setForeground(new java.awt.Color(51, 153, 255));
    }//GEN-LAST:event_lblOpenFileMouseEntered

    private void lblOpenFileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenFileMouseExited
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblOpenFile.setText(openLogicFile);
        lblOpenFile.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_lblOpenFileMouseExited

    private void lblNewFileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNewFileMouseEntered
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblNewFile.setText("<html><u>" + newLogicFile + "</u></html>");//<font color=blue></font>
        lblNewFile.setForeground(new java.awt.Color(51, 153, 255));
    }//GEN-LAST:event_lblNewFileMouseEntered

    private void lblNewFileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNewFileMouseExited
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblNewFile.setText(newLogicFile);
        lblNewFile.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_lblNewFileMouseExited

    private void mnuLogicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuLogicMouseClicked
}//GEN-LAST:event_mnuLogicMouseClicked

    private void mnuitCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitCloseActionPerformed
        closeActiveLogicTab();
    }//GEN-LAST:event_mnuitCloseActionPerformed

    private void closeActiveLogicTab() {
        LogicDefTab logicDefTab = getLoadedLogicsPanel().getSelectedLogicDefTab();
        getLoadedLogicsPanel().closeLogicDefTab(logicDefTab);
    }

    private void chkbxmnuitSHTableauxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkbxmnuitSHTableauxActionPerformed
    }//GEN-LAST:event_chkbxmnuitSHTableauxActionPerformed

    private void showHideTableauxPanel() {
        if (chkbxmnuitSHTableaux.isSelected()) {
            showTableauxPanel(false);
        } else {
            showTableauxPanel(true);
        }
    }

    private void spltVSplitComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_spltVSplitComponentResized
//            int loc = (int) ((spltVSplit.getBounds().getWidth() - spltVSplit.getDividerSize()) / 2);
//            spltVSplit.setDividerLocation(loc);
    }//GEN-LAST:event_spltVSplitComponentResized

    private void spltVSplitPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_spltVSplitPropertyChange
//        if (evt.getPropertyName().equals(javax.swing.JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
//            System.out.println("divider location changed");
//            int loc = (int) ((spltVSplit.getBounds().getWidth() - spltVSplit.getDividerSize()) / 2.3);
//            spltVSplit.setDividerLocation(loc);
//        }            
    }//GEN-LAST:event_spltVSplitPropertyChange

    private void lstPredefinedLogicsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstPredefinedLogicsMouseClicked
        if (evt.getClickCount() == 2) {
            openPredefinedLogic((String) lstPredefinedLogics.getSelectedValue());
            closePredefinedLogisDialog();
        }
    }//GEN-LAST:event_lstPredefinedLogicsMouseClicked

    private void mnuitExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitExitActionPerformed
}//GEN-LAST:event_mnuitExitActionPerformed

    private void mnuitHomePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitHomePageActionPerformed
        lotrec.util.BareBonesBrowserLaunch.openURL("http://www.irit.fr/Lotrec");
    }//GEN-LAST:event_mnuitHomePageActionPerformed

    private void lblVisitWebSiteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblVisitWebSiteMouseClicked
        lotrec.util.BareBonesBrowserLaunch.openURL("http://www.irit.fr/Lotrec");
}//GEN-LAST:event_lblVisitWebSiteMouseClicked

    private void lblVisitWebSiteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblVisitWebSiteMouseEntered
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblVisitWebSite.setText("<html><u>" + visiteOurWebStie + "</u></html>");//<font color=blue></font>
        lblVisitWebSite.setForeground(new java.awt.Color(51, 153, 255));
}//GEN-LAST:event_lblVisitWebSiteMouseEntered

    private void lblVisitWebSiteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblVisitWebSiteMouseExited
        dlgTaskPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        lblVisitWebSite.setText(visiteOurWebStie);
        lblVisitWebSite.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_lblVisitWebSiteMouseExited

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        //Math.max(getLogicsPanel().getWidth(),(int)(spltVSplit.getBounds().getWidth()/2.5))
        //spltHSplit.setDividerLocation(controlsPanel.getInternalPanelHeight()/spltVSplit.getBounds().getHeight());
//        spltVSplit.setDividerLocation(0.4);
//        spltHSplit.setDividerLocation(0.7);
    }//GEN-LAST:event_formComponentResized

    private void dlgTaskPaneWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgTaskPaneWindowClosed
    }//GEN-LAST:event_dlgTaskPaneWindowClosed

    private void dlgTaskPaneWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgTaskPaneWindowClosing
        requestFocus();
    }//GEN-LAST:event_dlgTaskPaneWindowClosing

    private void mnuitLogicDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitLogicDescriptionActionPerformed
        if (getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            getLoadedLogicsPanel().getSelectedLogicDefTab().showLogicInfo(getLocation());
        }
}//GEN-LAST:event_mnuitLogicDescriptionActionPerformed

    private void mnuitPremodelEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitPremodelEditorActionPerformed
        getTableauxPanel().showPremodelEditor();
}//GEN-LAST:event_mnuitPremodelEditorActionPerformed

    private void mnuitRunInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitRunInfoActionPerformed
        getTableauxPanel().showRunInfo();
    }//GEN-LAST:event_mnuitRunInfoActionPerformed

    private void rdbtnCircularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnCircularActionPerformed
        layoutActionPerformed(evt);
}//GEN-LAST:event_rdbtnCircularActionPerformed

    private void rdbtnHirerachicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnHirerachicActionPerformed
        layoutActionPerformed(evt);
}//GEN-LAST:event_rdbtnHirerachicActionPerformed

    private void rdbtnTileCascadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnTileCascadeActionPerformed
        if (rdbtnTileCascade.isSelected()) {
            rdbtnTileCascade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cascade.png")));
            rdbtnTileCascade.setText("Cascade premodels window");
            getTableauxPanel().tileWindows();
        } else {
            rdbtnTileCascade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/tile-vertical.png")));
            rdbtnTileCascade.setText("Tile premodels window");
            getTableauxPanel().cascadeTableauxFrames();
        }
    }//GEN-LAST:event_rdbtnTileCascadeActionPerformed

    private void changeDisplayMode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDisplayMode
        getTableauxPanel().displayModeChanged(evt.getActionCommand());
    }//GEN-LAST:event_changeDisplayMode

    private void mnuitPremodelsFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitPremodelsFiltersActionPerformed
        getTableauxPanel().showPremodelFilters();
    }//GEN-LAST:event_mnuitPremodelsFiltersActionPerformed

    private void savePremodelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePremodelActionPerformed
        saveSelectedPremodel();
    }//GEN-LAST:event_savePremodelActionPerformed

    private void mnuitLoadPremodelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitLoadPremodelActionPerformed
        loadPremodel();
}//GEN-LAST:event_mnuitLoadPremodelActionPerformed

    private void mnuitExportPremodelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitExportPremodelActionPerformed
        exportSelectedPremodel();
    }//GEN-LAST:event_mnuitExportPremodelActionPerformed

    private void loadPremodel() {
        String xmlFileName = "";
        OpenPremodelDialog opPremodelDlg = new OpenPremodelDialog();
        opPremodelDlg.setCurrentDirectory(currentDirectory);
        int returnVal = opPremodelDlg.showOpenDialog(this);
        if (returnVal == OpenPremodelDialog.APPROVE_OPTION) {
            currentDirectory = opPremodelDlg.getCurrentDirectory();
            xmlFileName = opPremodelDlg.getCompleteFileName();
        } else {
//            System.out.println("Open command cancelled by the user.");
            return;
        }
        GraphXMLParser graphParser = new GraphXMLParser();
        Logic logic;
        if (getLoadedLogicsPanel().getSelectedLogicDefTab() == null) {
            logic = Logic.getNewEmptyLogic();
        } else {
            logic = getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
        }
        try {
            Tableau t = graphParser.parseGraphXMLFile(logic, xmlFileName);
            CyTableauDisplayer.displayTableauInCy(t);
        } catch (GraphXMLParserException ex) {
            System.out.println("Error occured during opening the premodel file.\n" + ex.getMessage());
        }
    }

    private void saveSelectedPremodel() {
        if (getEngine() != null &&
                getEngine().getCurrentWallet() != null) {
            Tableau t = (Tableau) getEngine().getCurrentWallet().getGraph((String) getTableauxPanel().getTableauxList().getSelectedValue());
            if (t != null) {
                SaveAsPremodelDialog svAsPremodelDlg = new SaveAsPremodelDialog();
                svAsPremodelDlg.setCurrentDirectory(currentDirectory);
                svAsPremodelDlg.setFileName(t.getName());
                int returnVal = svAsPremodelDlg.showSaveDialog(this);
                if (returnVal == SaveAsPremodelDialog.APPROVE_OPTION) {
                    currentDirectory = svAsPremodelDlg.getCurrentDirectory();
                    String completeFileName = svAsPremodelDlg.getCompleteFileName();
                    File file = new File(completeFileName);
                    if (file.exists() && (DialogsFactory.fileExistsWarning(this, completeFileName) == 1)) {
//                    System.out.println("The file: " + file + ", where to save the premodel, already exists");
                        saveSelectedPremodel();
                    } else {
                        GraphXMLParser graphParser = new GraphXMLParser();
                        try {
                            graphParser.saveGraphToXMLFile(t, completeFileName);
                        } catch (GraphXMLParserException ex) {
                            System.out.println("Error occured during saving the premodel.\n" + ex.getMessage());
                        }
                    }
                } else {
//                System.out.println("Save as premodel canceled by the user...");
                }
            }
        }
    }

//    private void exportSelectedPremodelAsPNG() {
//        if (cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getAllFrames().length > 0) {
//            CyNetworkView currentNetworkView = cytoscape.Cytoscape.getCurrentNetworkView();
//            SaveAsPNGDialog svAsImgDlg = new SaveAsPNGDialog();
//            svAsImgDlg.setCurrentDirectory(currentDirectory);
//            svAsImgDlg.setFileName(currentNetworkView.getTitle());
//            int returnVal = svAsImgDlg.showSaveDialog(this);
//            if (returnVal == SaveAsPNGDialog.APPROVE_OPTION) {
//                currentDirectory = svAsImgDlg.getCurrentDirectory();
////                String fileDirName = currentDirectory.getAbsolutePath();
////                String fileName = svAsImgDlg.getFileName();
//                String completeFileName = svAsImgDlg.getCompleteFileName();
////                System.out.println("Export complete file name is : " + completeFileName);
//                File file = new File(completeFileName);
//                if (file.exists() && (DialogsFactory.fileExistsWarning(this, completeFileName) == 1)) {
////                    System.out.println("Export file: " + file + " already exists");
//                    exportSelectedPremodelAsPNG();
//                } else {
////                    System.out.println("Should be exported to the file: " + completeFileName);
//                    exportToPNG(completeFileName, currentNetworkView);
//                }
//            } else {
////                System.out.println("Export canceled by the user...");
//            }
//        }
//    }
    private void exportSelectedPremodel() {
        if (cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getAllFrames().length > 0) {
            CyNetworkView currentNetworkView = cytoscape.Cytoscape.getCurrentNetworkView();
            ExportPremodelDialog exportDialog = new ExportPremodelDialog();
            exportDialog.setCurrentDirectory(currentDirectory);
            exportDialog.setFileName(currentNetworkView.getTitle());
            int returnVal = exportDialog.showSaveDialog(this);
            if (returnVal == ExportPremodelDialog.APPROVE_OPTION) {
                currentDirectory = exportDialog.getCurrentDirectory();
//                String fileDirName = currentDirectory.getAbsolutePath();
//                String fileName = svAsImgDlg.getFileName();
                String completeFileName = exportDialog.getCompleteFileName();
//                System.out.println("Export complete file name is : " + completeFileName);
                File file = new File(completeFileName);
                if (file.exists() && (DialogsFactory.fileExistsWarning(this, completeFileName) == 1)) {
//                    System.out.println("Export file: " + file + " already exists");
                    exportSelectedPremodel();
                } else {
                    FileFilter currentFileFilter = exportDialog.getFileFilter();
                    if (currentFileFilter instanceof PdfFileFilter) {
                        exportToPDF(completeFileName, currentNetworkView);
                    } else if (currentFileFilter instanceof PngFileFilter) {
                        exportToPNG(completeFileName, currentNetworkView);
                    } else if (currentFileFilter instanceof PsFileFilter) {
                        exportToPS(completeFileName, currentNetworkView);
                    } else {
                        System.out.println("Premodel cannot be exported..\n" +
                                "Unsupported exportation type: " + exportDialog.getFileFilter().getDescription());
                        return;
                    }
                }
            } else {
//                System.out.println("Export canceled by the user...");
            }
        }
    }

//    private void exportSelectedPremodelAsPDF() {
//        if (cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getAllFrames().length > 0) {
//            CyNetworkView currentNetworkView = cytoscape.Cytoscape.getCurrentNetworkView();
//            SaveAsPDFDialog svAsImgDlg = new SaveAsPDFDialog();
//            svAsImgDlg.setCurrentDirectory(currentDirectory);
//            svAsImgDlg.setFileName(currentNetworkView.getTitle());
//            int returnVal = svAsImgDlg.showSaveDialog(this);
//            if (returnVal == SaveAsPDFDialog.APPROVE_OPTION) {
//                currentDirectory = svAsImgDlg.getCurrentDirectory();
////                String fileDirName = currentDirectory.getAbsolutePath();
////                String fileName = svAsImgDlg.getFileName();
//                String completeFileName = svAsImgDlg.getCompleteFileName();
////                System.out.println("Export complete file name is : " + completeFileName);
//                File file = new File(completeFileName);
//                if (file.exists() && (DialogsFactory.fileExistsWarning(this, completeFileName) == 1)) {
////                    System.out.println("Export file: " + file + " already exists");
//                    exportSelectedPremodelAsPDF();
//                } else {
////                    System.out.println("Should be exported to the file: " + completeFileName);
//                    exportToPDF(completeFileName, currentNetworkView);
//                }
//            } else {
////                System.out.println("Export canceled by the user...");
//            }
//        }
//    }
    private void layoutActionPerformed(java.awt.event.ActionEvent evt) {
        if (cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getAllFrames().length > 0) {
            CyTableauDisplayer.doYLayout(evt.getActionCommand());
        }
    }

    public static String getSelectedLayout() {
        return rdbtnHirerachic.isSelected() ? rdbtnHirerachic.getActionCommand() : rdbtnCircular.getActionCommand();
    }

    private Component getVSplitToLeftButton() {
        BasicSplitPaneDivider divider = ((BasicSplitPaneUI) spltVSplit.getUI()).getDivider();
        return divider.getComponent(0);
    }

    private Component getVSplitToRighttButton() {
        BasicSplitPaneDivider divider = ((BasicSplitPaneUI) spltVSplit.getUI()).getDivider();
        return divider.getComponent(1);
    }

    public void showTaskPane() {
        dlgTaskPane.pack();
        int x = (getWidth() - dlgTaskPane.getWidth()) / 2;
        int y = (getHeight() - dlgTaskPane.getHeight()) / 2;
        Point p = getLocation();
        p.translate(x, y);
        dlgTaskPane.setLocation(p);
        dlgTaskPane.setVisible(true);
    }

    public void hideTaskPane() {
        dlgTaskPane.dispose();
    }

    private void showPredefinedLogisDialog() {
        lstPredefinedLogics.setSelectedIndex(0);
        dlgPredefinedLogics.pack();
        dlgPredefinedLogics.setModal(true);
        int x = (getWidth() - dlgPredefinedLogics.getWidth()) / 2;
        int y = (getHeight() - dlgPredefinedLogics.getHeight()) / 2;
        Point p = getLocation();
        p.translate(x, y);
        dlgPredefinedLogics.setLocation(p);
        dlgPredefinedLogics.setVisible(true);
    }

    private void showPredefinedLogisDialog(Component parent) {
        lstPredefinedLogics.setSelectedIndex(0);
        dlgPredefinedLogics.pack();
        dlgPredefinedLogics.setModal(true);
        int x = (parent.getWidth() - dlgPredefinedLogics.getWidth()) / 2;
        int y = (parent.getHeight() - dlgPredefinedLogics.getHeight()) / 2;
        Point p = parent.getLocation();
        p.translate(x, y);
        dlgPredefinedLogics.setLocation(p);
        dlgPredefinedLogics.setVisible(true);
    }

    private void closePredefinedLogisDialog() {
        dlgPredefinedLogics.dispose();
        requestFocus();
    }

    public String getBaseTitle() {
        return "LoTREC 2.0 - Tableaux Theorem Prover";
    }

    public void refresh() {
        java.util.ResourceBundle resource = java.util.ResourceBundle.getBundle("lotrec.resources.MainFrame", lotrec.resources.ResourcesProvider.getCurrentLocale());
        this.setTitle(resource.getString("MainFrame.title"));
//        this.mainMenuBar.refresh();
//        this.loadedLogicsPanel.refresh();
//        this.tableauxEnginePanel.refresh();
//        refreshSplitPosition();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

//    public LogicsPanel getLogicsPanel() {
//        return new LogicsPanel(this);//logicsPanel;
//    }
    public LoadedLogicsPanel getLoadedLogicsPanel() {
        return this.loadedLogicsPanel;
    }

    public ControlsPanel getControlsPanel() {
        return controlsPanel;//new ControlsPanel(this);//
    }

    public TableauxPanel getTableauxPanel() {
        return tableauxPanel;//new TableauxPanel(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOpen;
    private javax.swing.ButtonGroup btngrpDisplayMode;
    private javax.swing.ButtonGroup btngrpLanguage;
    private javax.swing.ButtonGroup btngrpLayout;
    private javax.swing.JCheckBoxMenuItem chkbxmnuitSHControls;
    private javax.swing.JCheckBoxMenuItem chkbxmnuitSHLogics;
    private javax.swing.JCheckBoxMenuItem chkbxmnuitSHTableaux;
    private javax.swing.JDialog dlgOldControlPanel;
    private javax.swing.JDialog dlgOldHsplit;
    private javax.swing.JDialog dlgPredefinedLogics;
    private javax.swing.JDialog dlgResults;
    private javax.swing.JDialog dlgTaskPane;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblModalLogicK;
    private javax.swing.JLabel lblModelChecking;
    private javax.swing.JLabel lblNewFile;
    private javax.swing.JLabel lblOpenFile;
    private javax.swing.JLabel lblOthers;
    private javax.swing.JLabel lblVisitWebSite;
    private lotrec.gui.LoadedLogicsPanel loadedLogicsPanel;
    private javax.swing.JList lstPredefinedLogics;
    private javax.swing.JMenu mnuControl;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuLanguage;
    private javax.swing.JMenu mnuLayout;
    private javax.swing.JMenu mnuLogic;
    private javax.swing.JMenu mnuPremodels;
    private javax.swing.JMenu mnuPremodelsDisplayMode;
    private javax.swing.JMenu mnuShowHide;
    private javax.swing.JMenu mnuView;
    private javax.swing.JMenuItem mnuitAbout;
    private javax.swing.JMenuItem mnuitClose;
    private javax.swing.JMenuItem mnuitCloseAll;
    private javax.swing.JMenuItem mnuitExit;
    private javax.swing.JMenuItem mnuitExportPremodel;
    private javax.swing.JMenuItem mnuitHomePage;
    private javax.swing.JMenuItem mnuitLoadPremodel;
    private javax.swing.JMenuItem mnuitLogicDescription;
    private javax.swing.JMenuItem mnuitNew;
    private javax.swing.JMenuItem mnuitOpen;
    private javax.swing.JMenuItem mnuitPredefinedLogics;
    private javax.swing.JMenuItem mnuitPremodelEditor;
    private javax.swing.JMenuItem mnuitPremodelsFilters;
    private javax.swing.JMenuItem mnuitQuickHelp;
    private javax.swing.JMenuItem mnuitRunInfo;
    private javax.swing.JMenuItem mnuitSave;
    private javax.swing.JMenuItem mnuitSaveAll;
    private javax.swing.JMenuItem mnuitSaveAs;
    private javax.swing.JMenuItem mnuitWebTutorial;
    private javax.swing.JSeparator mnusepControl;
    private javax.swing.JSeparator mnusepHelp;
    private javax.swing.JSeparator mnusepLogic;
    private javax.swing.JSeparator mnusepLogic2;
    private lotrec.gui.NewControlsPanel newControlsPanel;
    private javax.swing.JPanel pnlBtns;
    private javax.swing.JPanel pnlHead;
    private javax.swing.JPanel pnlList;
    private javax.swing.JPanel pnlTasks;
    private javax.swing.JRadioButtonMenuItem rdbtnAllPremodels;
    private static javax.swing.JRadioButtonMenuItem rdbtnCircular;
    private javax.swing.JRadioButtonMenuItem rdbtnEnglish;
    private javax.swing.JRadioButtonMenuItem rdbtnFrench;
    private static javax.swing.JRadioButtonMenuItem rdbtnHirerachic;
    private javax.swing.JRadioButtonMenuItem rdbtnMany;
    private javax.swing.JRadioButtonMenuItem rdbtnOnlyOne;
    public static javax.swing.JRadioButtonMenuItem rdbtnTileCascade;
    private javax.swing.JMenuItem savePremodel;
    private javax.swing.JSplitPane spltHSplit;
    private javax.swing.JSplitPane spltVSplit;
    private lotrec.gui.TableauxPanel tableauxPanel;
    // End of variables declaration//GEN-END:variables
    private Engine engine;
}

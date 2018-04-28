/*
 * LoadedLogicsPanel.java
 *
 * Created on 6 février 2008, 16:29
 */
package lotrec.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import lotrec.dataStructure.Logic;
import lotrec.gui.logicspane.LogicDefTab;

/**
 *
 * @author  said
 */
public class LoadedLogicsPanel extends javax.swing.JPanel {

    private ArrayList<LogicDefTab> logicDefTabs;
    private final MainFrame mainFrame;

    /** Creates new form LoadedLogicsPanel */
    public LoadedLogicsPanel(MainFrame mainFrame) {
        logicDefTabs = new ArrayList();
        this.mainFrame = mainFrame;
        initComponents();
    }

    public LoadedLogicsPanel() {
        this(null);
    }

    public ArrayList<Logic> getLoadedLogics() {
        ArrayList<Logic> loadedLogics = new ArrayList();
        for (int i = 0; i < this.logicDefTabs.size(); i++) {
            loadedLogics.add(logicDefTabs.get(i).getLogic());
        }
        return loadedLogics;
    }

    public void addLogicTab(LogicDefTab logicDefTab) {
        //First logic added
        if (logicDefTabs.size() == 0) {
            this.mainFrame.hideTaskPane();
        }
//        logicDefTabsPane.addTab(logicTab.getLogic().getDisplayName()+" ("+logicTab.getLogicFileName()+")",logicTab);
        String title;
        if (logicDefTab.isModifiedAndNotSaved()) {
            title = logicDefTab.getTitle() + "(*)";
        } else {
            title = logicDefTab.getTitle();
        }
//        logicDefTabsPane.addTab(title, null, logicDefTab, logicDefTab.getToolTip());
        logicDefTabsPane.add(title, logicDefTab);
        logicDefTabs.add(logicDefTab);
        LogicTabComponent logicTabComponent = new LogicTabComponent(logicDefTabsPane);
        logicDefTabsPane.setSelectedComponent(logicDefTab);
        logicDefTabsPane.setTabComponentAt(
                logicDefTabsPane.getSelectedIndex(), logicTabComponent);
        assignExit(logicTabComponent);
//        refresh();
    }

    private void assignExit(final LogicTabComponent tabPanel) {
        JButton closeButton = tabPanel.getCloseButton();
        //Close the proper tab by clicking the button
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = logicDefTabsPane.indexOfTabComponent(tabPanel);
                if (i != -1) {
                    closeLogicDefTab(logicDefTabs.get(i));
                }
            }
        });
    }

    public boolean logicFileAlreadyOpen(String fileName, String fileDir) {
        for (int i = 0; i < logicDefTabs.size(); i++) {
            LogicDefTab oldTab = (LogicDefTab) logicDefTabs.get(i);
            if (oldTab.getFileName() != null && oldTab.getFileName().equals(fileName) && oldTab.getFileDir() != null && oldTab.getFileDir().equals(fileDir)) {
                return true;
            }
        }
        return false;
    }

    public boolean predefinedLogicFileAlreadyOpen(String fileName) {
        for (int i = 0; i < logicDefTabs.size(); i++) {
            LogicDefTab oldTab = (LogicDefTab) logicDefTabs.get(i);
            if (oldTab.getFileName() != null && oldTab.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void changeLogicNameIfAlreadyUsed(LogicDefTab logicDefTab) {
        //Look up for other tabs which logic name is the same
        int similarNum = 0;
        for (int i = 0; i < logicDefTabs.size(); i++) {
            LogicDefTab oldTab = (LogicDefTab) logicDefTabs.get(i);
            if (!oldTab.equals(logicDefTab) &&
                    oldTab.getLogic().getName().equals(logicDefTab.getLogic().getName())) {
                similarNum = 1;
                break;
            }
        }
        if (similarNum == 1) {
            String newName = logicDefTab.getLogic().getName();
            boolean noSimilar = false;
            while (!noSimilar) {
                noSimilar = true;
                for (int i = 0; i < logicDefTabs.size(); i++) {
                    LogicDefTab oldTab = (LogicDefTab) logicDefTabs.get(i);
                    if (!oldTab.equals(logicDefTab) &&
                            oldTab.getLogic().getName().equals(newName + "(" + similarNum + ")")) {
                        similarNum++;
                        noSimilar = false;
                        break;
                    }
                }
            }
            logicDefTab.getLogic().setName(newName + "(" + similarNum + ")");
        }
    }

    public void removeLogicTab(LogicDefTab logicDefTab) {
        logicDefTabs.remove(logicDefTab);
        logicDefTabsPane.remove(logicDefTab);
    }

    public void removeLogicTab(LogicDefTab logicDefTab, int[] deletedSelectedLogicIndexes) {
        deletedSelectedLogicIndexes[0] = logicDefTabs.indexOf(logicDefTab);
        logicDefTabs.remove(logicDefTab);
        logicDefTabsPane.remove(logicDefTab);
        deletedSelectedLogicIndexes[1] = logicDefTabsPane.getSelectedIndex();

    }

//    public void assignExit(LogicDefTab logicDefTab) {
//        final LogicDefTab copyOfTab = logicDefTab;
//        logicDefTab.getExitLabel().addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                closeLogicDefTab(copyOfTab);
//            }
//        });
//    }
    public void closeLogicDefTab(LogicDefTab logicDefTab) {
        if (logicDefTab.isModifiedAndNotSaved()) {
            if (DialogsFactory.notSavedLogicWarning(mainFrame, logicDefTab.getTitle()) == 0) {
                //When other save dialogs are closed, 
                //the user are not alerted again to save or discard
                //return value should be introduced in the save and save as methods
                //and simply then a loop here while not saved...
                mainFrame.saveLogicDefTab(logicDefTab);
            }

        }
        int deletedSelectedLogicIndexes[] = new int[2];
        removeLogicTab(logicDefTab, deletedSelectedLogicIndexes);
//        int deletedLogicIndex = 0;
//        int selectedLogicIndex = 0;
//        deletedLogicIndex =
//                deletedSelectedLogicIndexes[0];
//        selectedLogicIndex =
//                deletedSelectedLogicIndexes[1];
//        mainFrame.getControlsPanel().refreshLogicsList(deletedLogicIndex, selectedLogicIndex);
//        mainFrame.getControlsPanel().refreshFormulaeList();
        //Last logic removed
        if (logicDefTabs.size() == 0) {
            mainFrame.disableLogicMenuItems();
            mainFrame.showTaskPane();
        }

    }

    public void setTabTitle(LogicDefTab logicDefTab, String newTitle) {
        logicDefTabsPane.setTitleAt(logicDefTabsPane.indexOfComponent(logicDefTab), newTitle);
        LogicTabComponent logicTabComponent = (LogicTabComponent) logicDefTabsPane.getTabComponentAt(logicDefTabsPane.indexOfComponent(logicDefTab));
        logicTabComponent.getLabel().setText(newTitle);
        mainFrame.setTitle(mainFrame.getBaseTitle()+" - "+logicDefTab.getTitle());
    }

    public LogicDefTab getSelectedLogicDefTab() {
        return (LogicDefTab) logicDefTabsPane.getSelectedComponent();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logicDefTabsPane = new javax.swing.JTabbedPane();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Loaded Logics"));

        logicDefTabsPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                logicDefTabsPaneStateChanged(evt);
            }
        });
        logicDefTabsPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logicDefTabsPaneMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logicDefTabsPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logicDefTabsPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    private void logicDefTabsPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logicDefTabsPaneStateChanged
        mainFrame.getControlsPanel().refreshFormulaeList();
        LogicDefTab logicDefTab = (LogicDefTab) logicDefTabsPane.getSelectedComponent();
        if (logicDefTab != null) {
            mainFrame.setTitle(mainFrame.getBaseTitle()+" - "+logicDefTab.getTitle());
//            System.out.println("Selected tab is: " + logicDefTab.getTitle());
            if (logicDefTab.isNewEmptyLogic() || logicDefTab.isPredefinedLogic()) {
                mainFrame.setSaveEnabled(false);
            } else {
                mainFrame.setSaveEnabled(true);
            }
        }else{
            mainFrame.setTitle(mainFrame.getBaseTitle());
        }
    }//GEN-LAST:event_logicDefTabsPaneStateChanged

    private void logicDefTabsPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logicDefTabsPaneMouseClicked
//        System.out.println("Selected tab bounds: " + logicDefTabsPane.getBoundsAt(logicDefTabsPane.getSelectedIndex()));
//        System.out.println("Click at: " + evt.getX() + evt.getY());
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON2) {
            if (logicDefTabsPane.getBoundsAt(logicDefTabsPane.getSelectedIndex()).contains(evt.getX(), evt.getY())) {
                closeLogicDefTab((LogicDefTab) logicDefTabsPane.getSelectedComponent());
            }
        }
    }//GEN-LAST:event_logicDefTabsPaneMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane logicDefTabsPane;
    // End of variables declaration//GEN-END:variables
}

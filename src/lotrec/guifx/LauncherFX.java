package lotrec.guifx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lotrec.Lotrec;
import lotrec.guifx.dialogs.TaskPaneDialog;
import lotrec.guifx.dialogs.PredefinedLogicsDialog;
import lotrec.guifx.dialogs.FileDialogs;

import cytoscape.CyMain;
import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanelState;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class LauncherFX extends Application {

    private Stage splashStage;
    private MainFrameFX mainFrame;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Show splash screen
        showSplash();

        // Initialize in background
        Thread initThread = new Thread(() -> {
            Lotrec.initialize(Lotrec.GUI_RUN_MODE);
            initializeCytoscape();

            Platform.runLater(() -> {
                mainFrame = new MainFrameFX(primaryStage);
                // Embed Cytoscape desktop pane into the bridge
                mainFrame.getTableauxPane().getCytoscapeBridge().initCytoscape();
                primaryStage.show();
                if (splashStage != null) {
                    splashStage.close();
                }
                showTaskPaneDialog(primaryStage);
            });
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    private void showTaskPaneDialog(Stage primaryStage) {
        TaskPaneDialog dlg = new TaskPaneDialog(primaryStage);
        Optional<String> result = dlg.showAndWait();
        result.ifPresent(choice -> {
            switch (choice) {
                case "Open Predefined Logic":
                    PredefinedLogicsDialog predDlg = new PredefinedLogicsDialog(primaryStage);
                    Optional<String> logicName = predDlg.showAndWait();
                    logicName.ifPresent(name -> {
                        String fileName = name + ".xml";
                        lotrec.FileUtils.extractPredefinedLogicFile(
                            lotrec.PredefinedLogicsLoader.JAR_PATH, fileName);
                        String completeFileName = lotrec.FileUtils.PREDEFINED_HOME +
                            System.getProperty("file.separator") + fileName;
                        lotrec.dataStructure.Logic logic =
                            Lotrec.openLogicFile(completeFileName);
                        if (logic != null) {
                            mainFrame.getLoadedLogicsPane().addLogic(logic);
                        }
                    });
                    break;
                case "Open Existing File":
                    FileDialogs fileDialogs = new FileDialogs(primaryStage);
                    File file = fileDialogs.showOpenLogicDialog();
                    if (file != null) {
                        lotrec.dataStructure.Logic logic =
                            Lotrec.openLogicFile(file.getAbsolutePath());
                        if (logic != null) {
                            mainFrame.getLoadedLogicsPane().addLogic(logic);
                        }
                    }
                    break;
                case "Create New Logic":
                    mainFrame.getLoadedLogicsPane().addLogic(
                        lotrec.dataStructure.Logic.getNewEmptyLogic());
                    break;
            }
        });
    }

    private void initializeCytoscape() {
        try {
            String[] cyArgs = new String[]{
                "-p", "csplugins.quickfind.plugin.QuickFindPlugIn",
                "-p", "browser.AttributeBrowserPlugin",
                "-p", "GraphMerge.GraphMerge",
                "-p", "cytoscape.editor.CytoscapeEditorPlugin",
                "-p", "org.cytoscape.coreplugin.cpath.plugin.CPathPlugIn",
                "-p", "filter.cytoscape.CsFilter",
                "-p", "org.cytoscape.coreplugin.psi_mi.plugin.PsiMiPlugIn",
                "-p", "org.mskcc.biopax_plugin.plugin.BioPaxPlugIn",
                "-p", "csplugins.contextmenu.yeast.YeastPlugin",
                "-p", "edu.ucsd.bioeng.coreplugin.tableImport.TableImportPlugin",
                "-p", "sbmlreader.SBMLReaderPlugin",
                "-p", "csplugins.layout.LayoutPlugin",
                "-p", "ManualLayout.ManualLayoutPlugin",
                "-p", "yfiles.YFilesLayoutPlugin"
            };
            SwingUtilities.invokeAndWait(() -> {
                try {
                    new CyMain(cyArgs);
                    Cytoscape.getDesktop().getCyMenus().getToolBar().setVisible(false);
                    Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
                    Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
                    Cytoscape.getDesktop().clearStatusBar();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showSplash() {
        splashStage = new Stage(StageStyle.UNDECORATED);

        VBox splashLayout = new VBox(10);
        splashLayout.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #336699; -fx-border-width: 2;");

        // Try to load splash image
        try {
            InputStream is = getClass().getResourceAsStream("/lotrec/resources/lotrec.PNG");
            if (is != null) {
                ImageView imageView = new ImageView(new Image(is));
                imageView.setFitWidth(400);
                imageView.setPreserveRatio(true);
                splashLayout.getChildren().add(imageView);
            }
        } catch (Exception e) {
            // Image not found - continue without it
        }

        Label titleLabel = new Label("LoTREC — Tableaux Theorem Prover");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #336699;");
        splashLayout.getChildren().add(titleLabel);

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
        splashLayout.getChildren().add(loadingLabel);

        Scene splashScene = new Scene(splashLayout);
        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.show();
    }
}

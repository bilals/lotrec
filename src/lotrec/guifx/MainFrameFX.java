package lotrec.guifx;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lotrec.Lotrec;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.engine.Engine;
import lotrec.engine.JavaFXEngineListener;
import lotrec.guifx.dialogs.*;
import lotrec.parser.OldiesTokenizer;
import lotrec.process.AbstractWorker;
import lotrec.process.EventMachine;
import lotrec.process.Routine;
import lotrec.process.Strategy;

import java.util.ArrayList;

import java.io.File;
import java.util.Optional;

public class MainFrameFX {
    private final Stage stage;
    private final MenuBar menuBar;
    private final SplitPane mainSplitPane;
    private final SplitPane leftSplitPane;
    private final LoadedLogicsPane loadedLogicsPane;
    private final PremodelSettingsPane premodelSettingsPane;
    private final ControlsPane controlsPane;
    private final TableauxPane tableauxPane;
    private final VBox rightPanel;
    private Engine engine;
    private ToggleGroup layoutToggleGroup;

    public MainFrameFX(Stage stage) {
        this.stage = stage;

        // Create panels
        loadedLogicsPane = new LoadedLogicsPane();
        premodelSettingsPane = new PremodelSettingsPane();
        controlsPane = new ControlsPane();
        tableauxPane = new TableauxPane();

        // Wire logic tab selection to update PremodelSettingsPane
        loadedLogicsPane.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldTab, newTab) -> {
                Logic selected = loadedLogicsPane.getSelectedLogic();
                premodelSettingsPane.setLogic(selected);
            }
        );

        // Wire engine control buttons
        wireEngineControls();

        // Create menu bar (after panels exist so handlers can reference them)
        menuBar = createMenuBar();

        // Left side: just the LoadedLogicsPane
        leftSplitPane = new SplitPane();
        leftSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        leftSplitPane.getItems().add(loadedLogicsPane);

        // Right side: top row (PremodelSettings + controls | PremodelsList) + graph + status
        // Append runtime controls to PremodelSettingsPane
        premodelSettingsPane.getChildren().add(controlsPane);

        HBox rightTopRow = new HBox(5, premodelSettingsPane, tableauxPane.getPremodelsListBox());
        HBox.setHgrow(premodelSettingsPane, Priority.ALWAYS);
        HBox.setHgrow(tableauxPane.getPremodelsListBox(), Priority.ALWAYS);

        rightPanel = new VBox(5);
        rightPanel.getChildren().addAll(
            rightTopRow,
            tableauxPane.getGraphDisplayArea(),
            tableauxPane.getStatusBar()
        );
        VBox.setVgrow(tableauxPane.getGraphDisplayArea(), Priority.ALWAYS);

        // Main horizontal split: left (logics+controls) | right (settings+tableaux)
        mainSplitPane = new SplitPane();
        mainSplitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        mainSplitPane.getItems().addAll(leftSplitPane, rightPanel);
        mainSplitPane.setDividerPositions(0.35);

        // Root layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(mainSplitPane);
        root.getStyleClass().add("main-frame");

        // Configure scene and stage
        Scene scene = new Scene(root, 1200, 800);
        try {
            String cssPath = getClass().getResource("styles/default.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            // CSS not yet available - continue without styling
        }

        stage.setScene(scene);
        stage.setTitle("LoTREC \u2014 Tableaux Theorem Prover");
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/lotrec/images/lotrecIcon.GIF")));
        } catch (Exception e) {
            // Icon not found - continue without it
        }
        stage.setMaximized(true);
    }

    // --- Engine wiring ---

    private void wireEngineControls() {
        premodelSettingsPane.getBuildButton().setOnAction(e -> startEngine());
        premodelSettingsPane.getStepButton().setOnAction(e -> showStepByStepDialog());
        premodelSettingsPane.getSatCheckButton().setOnAction(e -> showSatCheckDialog());
        controlsPane.getStopButton().setOnAction(e -> {
            if (engine != null) {
                engine.stopWork();
            }
        });
        controlsPane.getNextStepButton().setOnAction(e -> {
            if (engine != null) {
                engine.resumeWorkToNextStep();
            }
        });
    }

    /**
     * Validates formula and strategy, returns a prepared (but not started) Engine,
     * or null if validation failed (error dialogs shown to user).
     */
    private Engine validateAndCreateEngine() {
        Logic logic = loadedLogicsPane.getSelectedLogic();
        if (logic == null) {
            DialogsFactory.errorDialog(stage, "No Logic", "Please load a logic first.");
            return null;
        }

        MarkedExpression formula;
        try {
            formula = premodelSettingsPane.parseFormula();
        } catch (Exception ex) {
            DialogsFactory.errorDialog(stage, "Parse Error",
                "Formula parse error:\n" + ex.getMessage());
            return null;
        }

        Strategy strategy;
        try {
            OldiesTokenizer tokenizer = new OldiesTokenizer(logic);
            tokenizer.initializeTokenizerAndProps();
            lotrec.process.Strategy mainStrat = logic.getStrategy(logic.getMainStrategyName());
            if (mainStrat == null) {
                DialogsFactory.errorDialog(stage, "No Strategy",
                    "No main strategy defined for this logic.");
                return null;
            }
            strategy = tokenizer.parseStrategy(mainStrat.getCode());
            tokenizer.verifyCodeEnd();
        } catch (Exception ex) {
            DialogsFactory.errorDialog(stage, "Strategy Error",
                "Strategy parse error:\n" + ex.getMessage());
            return null;
        }

        return new Engine(logic, strategy, formula, new JavaFXEngineListener(this));
    }

    private void startEngine() {
        engine = validateAndCreateEngine();
        if (engine == null) return;
        engine.buildTableaux();
        engine.start();
    }

    private void showStepByStepDialog() {
        engine = validateAndCreateEngine();
        if (engine == null) return;

        BreakPointsDialog dlg = new BreakPointsDialog(stage);
        dlg.populateFromStrategy(engine.getStrategy());
        Optional<ArrayList<Integer>> result = dlg.showAndWait();
        result.ifPresent(breakPoints -> {
            engine.setRunningBySteps(true);
            engine.setRulesBreakPoints(breakPoints);
            engine.buildTableaux();
            engine.start();
        });
    }

    private void showSatCheckDialog() {
        engine = validateAndCreateEngine();
        if (engine == null) return;

        SatisfiabilityDialog dlg = new SatisfiabilityDialog(stage);
        Optional<Integer> result = dlg.showAndWait();
        result.ifPresent(action -> {
            engine.setOpenTableauAction(action);
            engine.buildTableaux();
            engine.start();
        });
    }

    // --- Logic loading helpers ---

    private void loadPredefinedLogic(String logicName) {
        String fileName = logicName + ".xml";
        // Extract from JAR resources to ~/.LoTREC/PredefinedLogics/ (same as Swing version)
        lotrec.FileUtils.extractPredefinedLogicFile(
            lotrec.PredefinedLogicsLoader.JAR_PATH, fileName);
        String completeFileName = lotrec.FileUtils.PREDEFINED_HOME +
            System.getProperty("file.separator") + fileName;
        Logic logic = Lotrec.openLogicFile(completeFileName);
        if (logic != null) {
            loadedLogicsPane.addLogic(logic);
        } else {
            DialogsFactory.errorDialog(stage, "Load Error",
                "Failed to load predefined logic: " + logicName);
        }
    }

    private void openLogicFromFile(File file) {
        Logic logic = Lotrec.openLogicFile(file.getAbsolutePath());
        if (logic != null) {
            loadedLogicsPane.addLogic(logic);
        } else {
            DialogsFactory.errorDialog(stage, "Load Error",
                "Failed to load logic file: " + file.getName());
        }
    }

    // --- Accessor methods ---

    public Stage getStage() { return stage; }
    public MenuBar getMenuBar() { return menuBar; }
    public SplitPane getMainSplitPane() { return mainSplitPane; }
    public LoadedLogicsPane getLoadedLogicsPane() { return loadedLogicsPane; }
    public PremodelSettingsPane getPremodelSettingsPane() { return premodelSettingsPane; }
    public ControlsPane getControlsPane() { return controlsPane; }
    public TableauxPane getTableauxPane() { return tableauxPane; }
    public Engine getEngine() { return engine; }
    public String getSelectedLayout() {
        if (layoutToggleGroup != null && layoutToggleGroup.getSelectedToggle() != null) {
            return ((RadioMenuItem) layoutToggleGroup.getSelectedToggle()).getText();
        }
        return "Hierarchic";
    }

    // --- Menu bar ---

    private MenuBar createMenuBar() {
        // Control menu
        Menu controlMenu = new Menu("Control");
        CheckMenuItem showLogics = new CheckMenuItem("Logics");
        showLogics.setSelected(true);
        showLogics.setOnAction(e -> {
            if (showLogics.isSelected()) {
                if (!leftSplitPane.getItems().contains(loadedLogicsPane)) {
                    leftSplitPane.getItems().add(0, loadedLogicsPane);
                }
            } else {
                leftSplitPane.getItems().remove(loadedLogicsPane);
            }
        });
        CheckMenuItem showControls = new CheckMenuItem("Controls");
        showControls.setSelected(true);
        showControls.setOnAction(e -> {
            controlsPane.setVisible(showControls.isSelected());
            controlsPane.setManaged(showControls.isSelected());
        });
        CheckMenuItem showTableaux = new CheckMenuItem("Tableaux");
        showTableaux.setSelected(true);
        showTableaux.setOnAction(e -> {
            if (showTableaux.isSelected()) {
                if (!mainSplitPane.getItems().contains(rightPanel)) {
                    mainSplitPane.getItems().add(rightPanel);
                }
            } else {
                mainSplitPane.getItems().remove(rightPanel);
            }
        });
        Menu showHideMenu = new Menu("Show/Hide Panels");
        showHideMenu.getItems().addAll(showLogics, showControls, showTableaux);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        controlMenu.getItems().addAll(showHideMenu, new SeparatorMenuItem(), exitItem);

        // Logic menu
        Menu logicMenu = new Menu("Logic");
        MenuItem newLogic = new MenuItem("New...");
        newLogic.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newLogic.setOnAction(e -> {
            loadedLogicsPane.addLogic(Logic.getNewEmptyLogic());
        });

        MenuItem openLogic = new MenuItem("Open...");
        openLogic.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openLogic.setOnAction(e -> {
            FileDialogs fileDialogs = new FileDialogs(stage);
            File file = fileDialogs.showOpenLogicDialog();
            if (file != null) {
                openLogicFromFile(file);
            }
        });

        MenuItem predefinedLogics = new MenuItem("Predefined Logics...");
        predefinedLogics.setOnAction(e -> {
            PredefinedLogicsDialog dlg = new PredefinedLogicsDialog(stage);
            Optional<String> result = dlg.showAndWait();
            result.ifPresent(this::loadPredefinedLogic);
        });

        MenuItem saveLogic = new MenuItem("Save...");
        saveLogic.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveLogic.setOnAction(e -> {
            Logic logic = loadedLogicsPane.getSelectedLogic();
            if (logic != null) {
                FileDialogs fileDialogs = new FileDialogs(stage);
                File file = fileDialogs.showSaveLogicDialog();
                if (file != null) {
                    Lotrec.saveLogicFile(logic, file.getAbsolutePath());
                }
            }
        });

        MenuItem saveAs = new MenuItem("Save As...");
        saveAs.setOnAction(e -> {
            Logic logic = loadedLogicsPane.getSelectedLogic();
            if (logic != null) {
                FileDialogs fileDialogs = new FileDialogs(stage);
                File file = fileDialogs.showSaveLogicDialog();
                if (file != null) {
                    Lotrec.saveLogicFile(logic, file.getAbsolutePath());
                }
            }
        });

        MenuItem closeLogic = new MenuItem("Close");
        closeLogic.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
        closeLogic.setOnAction(e -> {
            Logic logic = loadedLogicsPane.getSelectedLogic();
            if (logic != null) {
                loadedLogicsPane.removeLogic(logic);
            }
        });

        MenuItem logicDescription = new MenuItem("Logic Description...");
        logicDescription.setOnAction(e -> {
            Logic logic = loadedLogicsPane.getSelectedLogic();
            if (logic != null) {
                LogicDescriptionDialog dlg = new LogicDescriptionDialog(stage);
                dlg.setDescription(logic.getName(),
                    logic.getDescription() != null ? logic.getDescription() : "");
                dlg.showAndWait();
            }
        });

        logicMenu.getItems().addAll(
            newLogic, openLogic, predefinedLogics,
            new SeparatorMenuItem(),
            saveLogic, saveAs,
            new SeparatorMenuItem(),
            closeLogic,
            new SeparatorMenuItem(),
            logicDescription
        );

        // View menu
        Menu viewMenu = new Menu("View");
        RadioMenuItem hierarchicLayout = new RadioMenuItem("Hierarchic");
        hierarchicLayout.setSelected(true);
        RadioMenuItem circularLayout = new RadioMenuItem("Circular");
        layoutToggleGroup = new ToggleGroup();
        hierarchicLayout.setToggleGroup(layoutToggleGroup);
        circularLayout.setToggleGroup(layoutToggleGroup);
        Menu layoutMenu = new Menu("Premodels Layout");
        layoutMenu.getItems().addAll(hierarchicLayout, circularLayout);

        RadioMenuItem singleDisplay = new RadioMenuItem("Only selected one");
        singleDisplay.setSelected(true);
        RadioMenuItem multiDisplay = new RadioMenuItem("Many selected ones");
        RadioMenuItem allDisplay = new RadioMenuItem("All premodels");
        ToggleGroup displayGroup = new ToggleGroup();
        singleDisplay.setToggleGroup(displayGroup);
        multiDisplay.setToggleGroup(displayGroup);
        allDisplay.setToggleGroup(displayGroup);
        Menu displayMenu = new Menu("Premodels Display Mode");
        displayMenu.getItems().addAll(singleDisplay, multiDisplay, allDisplay);

        MenuItem filters = new MenuItem("Premodels Filters...");
        filters.setOnAction(e -> {
            FilterDialog dlg = new FilterDialog(stage);
            dlg.showAndWait();
        });
        viewMenu.getItems().addAll(layoutMenu, displayMenu, new SeparatorMenuItem(), filters);

        // Premodels menu
        Menu premodelsMenu = new Menu("Premodels");
        MenuItem loadPremodel = new MenuItem("Load premodel...");
        MenuItem savePremodel = new MenuItem("Save selected premodel...");
        MenuItem exportPremodel = new MenuItem("Export Premodel...");
        exportPremodel.setOnAction(e -> {
            FileDialogs fileDialogs = new FileDialogs(stage);
            fileDialogs.showExportDialog();
        });
        MenuItem premodelEditor = new MenuItem("Premodels Editor...");
        premodelEditor.setOnAction(e -> {
            PremodelEditorDialog dlg = new PremodelEditorDialog(stage);
            dlg.showAndWait();
        });
        MenuItem runInfo = new MenuItem("Run Info Window");
        runInfo.setOnAction(e -> {
            RunInfoDialog dlg = new RunInfoDialog(stage);
            dlg.showAndWait();
        });
        premodelsMenu.getItems().addAll(
            loadPremodel, savePremodel, exportPremodel,
            new SeparatorMenuItem(),
            premodelEditor, runInfo
        );

        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem homePage = new MenuItem("Home Page...");
        homePage.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("http://www.irit.fr/Lotrec/"));
            } catch (Exception ex) {
                // Ignore if browser cannot open
            }
        });
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            DialogsFactory.infoDialog(stage, "About LoTREC",
                "LoTREC \u2014 Tableaux Theorem Prover\n" +
                "An automated theorem prover for modal and description logics.");
        });
        helpMenu.getItems().addAll(homePage, new SeparatorMenuItem(), about);

        return new MenuBar(controlMenu, logicMenu, viewMenu, premodelsMenu, helpMenu);
    }
}

package lotrec.guifx;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFrameFX {
    private final Stage stage;
    private final MenuBar menuBar;
    private final SplitPane mainSplitPane;
    private final SplitPane leftSplitPane;
    private final StackPane leftPane;  // placeholder for LoadedLogicsPane + ControlsPane
    private final StackPane rightPane; // placeholder for TableauxPane
    private ControlsPane controlsPane;
    private TableauxPane tableauxPane;

    public MainFrameFX(Stage stage) {
        this.stage = stage;

        // Create menu bar with 5 menus
        menuBar = createMenuBar();

        // Create left side: vertically split LoadedLogics (top) + Controls (bottom)
        leftPane = new StackPane();
        leftPane.getStyleClass().add("left-pane");
        leftPane.getChildren().add(new Label("Logic panels pending migration"));

        StackPane controlsPlaceholder = new StackPane();
        controlsPlaceholder.getStyleClass().add("controls-pane");
        controlsPlaceholder.getChildren().add(new Label("Controls pending migration"));

        leftSplitPane = new SplitPane();
        leftSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        leftSplitPane.getItems().addAll(leftPane, controlsPlaceholder);
        leftSplitPane.setDividerPositions(0.7);

        // Create right side: TableauxPane placeholder
        rightPane = new StackPane();
        rightPane.getStyleClass().add("right-pane");
        rightPane.getChildren().add(new Label("Tableaux panel pending migration"));

        // Main horizontal split: left (logics+controls) | right (tableaux)
        mainSplitPane = new SplitPane();
        mainSplitPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        mainSplitPane.getItems().addAll(leftSplitPane, rightPane);
        mainSplitPane.setDividerPositions(0.4);

        // Root layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(mainSplitPane);
        root.getStyleClass().add("main-frame");

        // Configure scene and stage
        Scene scene = new Scene(root, 1200, 800);
        // Load CSS if available
        try {
            String cssPath = getClass().getResource("styles/default.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            // CSS not yet available - continue without styling
        }

        stage.setScene(scene);
        stage.setTitle("LoTREC \u2014 Tableaux Theorem Prover");
        stage.setMaximized(true);
    }

    // Accessor methods
    public Stage getStage() { return stage; }
    public MenuBar getMenuBar() { return menuBar; }
    public SplitPane getMainSplitPane() { return mainSplitPane; }
    public StackPane getLeftPane() { return leftPane; }
    public StackPane getRightPane() { return rightPane; }
    public ControlsPane getControlsPane() { return controlsPane; }
    public void setControlsPane(ControlsPane controlsPane) { this.controlsPane = controlsPane; }
    public TableauxPane getTableauxPane() { return tableauxPane; }
    public void setTableauxPane(TableauxPane tableauxPane) { this.tableauxPane = tableauxPane; }

    private MenuBar createMenuBar() {
        // Control menu
        Menu controlMenu = new Menu("Control");
        // Show/Hide panels submenu
        CheckMenuItem showLogics = new CheckMenuItem("Logics");
        showLogics.setSelected(true);
        CheckMenuItem showControls = new CheckMenuItem("Controls");
        showControls.setSelected(true);
        CheckMenuItem showTableaux = new CheckMenuItem("Tableaux");
        showTableaux.setSelected(true);
        Menu showHideMenu = new Menu("Show/Hide Panels");
        showHideMenu.getItems().addAll(showLogics, showControls, showTableaux);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        controlMenu.getItems().addAll(showHideMenu, new SeparatorMenuItem(), exitItem);

        // Logic menu
        Menu logicMenu = new Menu("Logic");
        MenuItem newLogic = new MenuItem("New...");
        newLogic.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MenuItem openLogic = new MenuItem("Open...");
        openLogic.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        MenuItem predefinedLogics = new MenuItem("Predefined Logics...");
        MenuItem saveLogic = new MenuItem("Save...");
        saveLogic.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        MenuItem saveAs = new MenuItem("Save As...");
        MenuItem closeLogic = new MenuItem("Close");
        closeLogic.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
        MenuItem logicDescription = new MenuItem("Logic Description...");
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
        ToggleGroup layoutGroup = new ToggleGroup();
        hierarchicLayout.setToggleGroup(layoutGroup);
        circularLayout.setToggleGroup(layoutGroup);
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
        viewMenu.getItems().addAll(layoutMenu, displayMenu, new SeparatorMenuItem(), filters);

        // Premodels menu
        Menu premodelsMenu = new Menu("Premodels");
        MenuItem loadPremodel = new MenuItem("Load premodel...");
        MenuItem savePremodel = new MenuItem("Save selected premodel...");
        MenuItem exportPremodel = new MenuItem("Export Premodel...");
        MenuItem premodelEditor = new MenuItem("Premodels Editor...");
        MenuItem runInfo = new MenuItem("Run Info Window");
        premodelsMenu.getItems().addAll(
            loadPremodel, savePremodel, exportPremodel,
            new SeparatorMenuItem(),
            premodelEditor, runInfo
        );

        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem homePage = new MenuItem("Home Page...");
        MenuItem about = new MenuItem("About");
        about.setDisable(true);
        helpMenu.getItems().addAll(homePage, new SeparatorMenuItem(), about);

        return new MenuBar(controlMenu, logicMenu, viewMenu, premodelsMenu, helpMenu);
    }
}

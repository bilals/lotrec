package lotrec.guifx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lotrec.dataStructure.graph.Graph;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;
import lotrec.gui.CyTableauDisplayer;
import lotrec.guifx.graph.CytoscapeSwingBridge;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Vector;

public class TableauxPane extends VBox {

    public static final String TABLEAU_TREE = "Tableaux Tree";

    private final ListView<String> premodelsList;
    private final ObservableList<String> premodelsItems;
    private final StackPane graphDisplayArea;
    private final CytoscapeSwingBridge cytoscapeBridge;
    private final Label tableauxCountLabel;
    private final Label elapsedTimeLabel;
    private final Label engineStatusLabel;
    private final CheckBox filterClosedCheckBox;

    private int[] lastSelectedIndices;
    private ArrayList<String> lastSelectedTabs;

    public TableauxPane() {
        setSpacing(8);
        setPadding(new Insets(10));

        // Premodels list
        premodelsItems = FXCollections.observableArrayList();
        premodelsList = new ListView<>(premodelsItems);
        premodelsList.setPrefHeight(150);

        // Track selection changes for lastSelectedIndices/lastSelectedTabs
        premodelsList.getSelectionModel().selectedIndexProperty().addListener(
            (obs, oldVal, newVal) -> {
                int idx = newVal.intValue();
                if (idx >= 0) {
                    lastSelectedIndices = new int[]{ idx };
                    lastSelectedTabs = new ArrayList<>();
                    lastSelectedTabs.add(premodelsList.getItems().get(idx));
                }
            }
        );

        // Filter
        filterClosedCheckBox = new CheckBox("Filter closed tableaux");

        // Graph display area with CytoscapeSwingBridge
        cytoscapeBridge = new CytoscapeSwingBridge();
        graphDisplayArea = new StackPane();
        graphDisplayArea.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        graphDisplayArea.setPrefHeight(300);
        graphDisplayArea.getChildren().add(cytoscapeBridge);
        VBox.setVgrow(graphDisplayArea, Priority.ALWAYS);

        // Status bar
        tableauxCountLabel = new Label("Tableaux: 0");
        elapsedTimeLabel = new Label("Time: --");
        engineStatusLabel = new Label("Status: Idle");
        HBox statusBar = new HBox(15, engineStatusLabel, tableauxCountLabel, elapsedTimeLabel);

        getChildren().addAll(
            new Label("Premodels:"), premodelsList, filterClosedCheckBox,
            graphDisplayArea, statusBar
        );
    }

    // --- Wallet display methods (mirroring Swing TableauxPanel) ---

    /**
     * Reads the engine's current wallet and populates the premodels list.
     * Must be called on the FX Application Thread.
     */
    public void fillTableauxList(Engine engine) {
        Wallet wallet = engine.getCurrentWallet();
        premodelsItems.clear();
        premodelsItems.add(TABLEAU_TREE);
        for (Graph g : wallet.getGraphes()) {
            if (filterClosedCheckBox.isSelected() && g.isClosed()) {
                continue;
            }
            premodelsItems.add(g.getName());
        }
        setTableauxCount(wallet.getGraphes().size());
    }

    /**
     * Fills the list and displays the first tableau (or the tree if only one entry).
     * Must be called on the FX Application Thread.
     */
    public void fillTabListAndDisplayFirst(Engine engine) {
        fillTableauxList(engine);
        if (premodelsItems.size() > 0) {
            if (premodelsItems.size() == 1) {
                // Only "Tableaux Tree" — select it
                premodelsList.getSelectionModel().select(0);
            } else {
                // Select first actual tableau
                premodelsList.getSelectionModel().select(1);
            }
            displaySelectedTableau(engine);
        }
    }

    /**
     * Fills the list and restores previous selection, then displays.
     * Must be called on the FX Application Thread.
     */
    public void fillTabListAndDisplayLastChosenOnes(Engine engine) {
        int[] savedIndices = lastSelectedIndices;
        fillTableauxList(engine);
        if (savedIndices != null && savedIndices.length > 0) {
            // Restore the first saved index (single-selection mode)
            int idx = savedIndices[0];
            if (idx >= 0 && idx < premodelsItems.size()) {
                premodelsList.getSelectionModel().select(idx);
            } else if (premodelsItems.size() > 0) {
                premodelsList.getSelectionModel().select(0);
            }
        } else if (premodelsItems.size() > 0) {
            premodelsList.getSelectionModel().select(0);
        }
        displaySelectedTableau(engine);
    }

    /**
     * Displays the currently selected tableau in Cytoscape.
     * Flushes previous networks, then renders the selected one.
     * The Cytoscape calls are dispatched to the Swing EDT.
     */
    public void displaySelectedTableau(Engine engine) {
        int tabIndex = premodelsList.getSelectionModel().getSelectedIndex();
        if (tabIndex < 0) {
            if (premodelsItems.size() > 0) {
                premodelsList.getSelectionModel().select(0);
                tabIndex = 0;
            } else {
                return;
            }
        }

        final int selectedIndex = tabIndex;
        final String selectedName = premodelsItems.get(selectedIndex);
        final String layoutName = getLayoutName();

        SwingUtilities.invokeLater(() -> {
            CyTableauDisplayer.flush();
            if (selectedIndex == 0) {
                // Display tableaux tree
                Vector<Tableau> tableauxList = new Vector<>();
                for (Graph g : engine.getCurrentWallet().getGraphes()) {
                    tableauxList.add((Tableau) g);
                }
                CyTableauDisplayer.displayTableauxTreeInCy(tableauxList);
            } else {
                // Display single tableau
                Graph g = engine.getCurrentWallet().getGraph(selectedName);
                if (g != null) {
                    CyTableauDisplayer.displayTableauInCy((Tableau) g);
                }
            }
        });
    }

    /**
     * Wires a click listener on the premodels list to display the selected tableau.
     * Must pass the engine reference so the listener can access the wallet.
     */
    public void wireSelectionListener(Engine engine) {
        premodelsList.setOnMouseClicked(event -> {
            displaySelectedTableau(engine);
        });
    }

    /**
     * Gets the layout name. Tries to read from MainFrameFX if available,
     * defaults to "Hierarchic".
     */
    private String getLayoutName() {
        // Default to Hierarchic; the CyTableauDisplayer.displayTableau()
        // calls doYLayout internally using MainFrame.getSelectedLayout()
        return "Hierarchic";
    }

    // --- Status methods ---

    public void setTableauxCount(int count) {
        tableauxCountLabel.setText("Tableaux: " + count);
    }

    public void setElapsedTime(String time) {
        elapsedTimeLabel.setText("Time: " + time);
    }

    public void setEngineStatus(String status) {
        engineStatusLabel.setText("Status: " + status);
    }

    public void addPremodel(String name) {
        premodelsItems.add(name);
    }

    public void clearPremodels() {
        premodelsItems.clear();
    }

    // --- Getters ---

    public ListView<String> getPremodelsList() { return premodelsList; }
    public StackPane getGraphDisplayArea() { return graphDisplayArea; }
    public Label getTableauxCountLabel() { return tableauxCountLabel; }
    public Label getElapsedTimeLabel() { return elapsedTimeLabel; }
    public Label getEngineStatusLabel() { return engineStatusLabel; }
    public CheckBox getFilterClosedCheckBox() { return filterClosedCheckBox; }
    public CytoscapeSwingBridge getCytoscapeBridge() { return cytoscapeBridge; }
}

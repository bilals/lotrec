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
    private final VBox premodelsListBox;
    private final StackPane graphDisplayArea;
    private final CytoscapeSwingBridge cytoscapeBridge;
    private final Label tableauxCountLabel;
    private final Label elapsedTimeLabel;
    private final Label engineStatusLabel;
    private final Label appliedRulesLabel;
    private final CheckBox filterClosedCheckBox;
    private final HBox statusBar;

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

        // Premodels list box (exposed for layout composition)
        premodelsListBox = new VBox(5, new Label("Premodels:"), premodelsList, filterClosedCheckBox);
        VBox.setVgrow(premodelsList, Priority.ALWAYS);

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
        appliedRulesLabel = new Label("Rules: --");
        statusBar = new HBox(15, engineStatusLabel, tableauxCountLabel, elapsedTimeLabel, appliedRulesLabel);

        getChildren().addAll(graphDisplayArea, statusBar);
    }

    // --- Wallet display methods (mirroring Swing TableauxPanel) ---

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

    public void fillTabListAndDisplayFirst(Engine engine) {
        fillTableauxList(engine);
        if (premodelsItems.size() > 0) {
            if (premodelsItems.size() == 1) {
                premodelsList.getSelectionModel().select(0);
            } else {
                premodelsList.getSelectionModel().select(1);
            }
            displaySelectedTableau(engine);
        }
    }

    public void fillTabListAndDisplayLastChosenOnes(Engine engine) {
        int[] savedIndices = lastSelectedIndices;
        fillTableauxList(engine);
        if (savedIndices != null && savedIndices.length > 0) {
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
                Vector<Tableau> tableauxList = new Vector<>();
                for (Graph g : engine.getCurrentWallet().getGraphes()) {
                    tableauxList.add((Tableau) g);
                }
                CyTableauDisplayer.displayTableauxTreeInCy(tableauxList);
            } else {
                Graph g = engine.getCurrentWallet().getGraph(selectedName);
                if (g != null) {
                    CyTableauDisplayer.displayTableauInCy((Tableau) g);
                }
            }
        });
    }

    public void wireSelectionListener(Engine engine) {
        premodelsList.setOnMouseClicked(event -> {
            displaySelectedTableau(engine);
        });
    }

    private String getLayoutName() {
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

    public void setAppliedRules(String count) {
        appliedRulesLabel.setText("Rules: " + count);
    }

    public void addPremodel(String name) {
        premodelsItems.add(name);
    }

    public void clearPremodels() {
        premodelsItems.clear();
    }

    // --- Getters ---

    public ListView<String> getPremodelsList() { return premodelsList; }
    public VBox getPremodelsListBox() { return premodelsListBox; }
    public StackPane getGraphDisplayArea() { return graphDisplayArea; }
    public Label getTableauxCountLabel() { return tableauxCountLabel; }
    public Label getElapsedTimeLabel() { return elapsedTimeLabel; }
    public Label getEngineStatusLabel() { return engineStatusLabel; }
    public CheckBox getFilterClosedCheckBox() { return filterClosedCheckBox; }
    public CytoscapeSwingBridge getCytoscapeBridge() { return cytoscapeBridge; }
    public HBox getStatusBar() { return statusBar; }
    public Label getAppliedRulesLabel() { return appliedRulesLabel; }
}

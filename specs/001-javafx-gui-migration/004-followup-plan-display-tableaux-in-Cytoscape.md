# Display Tableaux in Cytoscape — FX GUI Integration

## Problem

When clicking "Build Premodels" in the JavaFX GUI, the engine runs correctly (tableaux rules fire, console output shows rule applications), but **no Cytoscape graphs appear** in the tableau panel. The Swing GUI displays them correctly.

There are 3 missing pieces:

### 1. Cytoscape Is Never Initialized

The Swing `Launcher.java` (lines 99-127) bootstraps the entire Cytoscape framework:

```java
String[] cyArgs = new String[]{
    "-p", "csplugins.quickfind.plugin.QuickFindPlugIn",
    "-p", "browser.AttributeBrowserPlugin",
    "-p", "yfiles.YFilesLayoutPlugin"
};
new CyMain(cyArgs);
Cytoscape.getDesktop().getCyMenus().getToolBar().setVisible(false);
Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
```

`LauncherFX.java` only calls `Lotrec.initialize(Lotrec.GUI_RUN_MODE)` — it **never calls `new CyMain()`**, so `Cytoscape.getDesktop()`, `Cytoscape.getCyNode()`, `Cytoscape.createNetwork()`, etc. are all unavailable.

### 2. Empty Listener Stubs in JavaFXEngineListener

`JavaFXEngineListener.java` lines 160-168:

```java
@Override
public void refreshTableauxDisplay() {
    // Will be connected to TableauxPane.fillList() in full integration
}

@Override
public void refreshLastChosenTableaux() {
    // Will be connected to TableauxPane display refresh in full integration
}
```

These are empty TODO stubs. Additionally, `onBuildStart()` and `onBuildEnd()` **don't call the tableau display methods** that their Swing counterparts do:

**Swing (`SwingEngineListener`):**
- `onBuildStart()` → calls `tableauxPanel.fillTabListAndDisplayFirst()`
- `onBuildEnd()` → calls `tableauxPanel.fillTabListAndDisplayLastChosenOnes()`
- `onPause()` → calls `tableauxPanel.fillTabListAndDisplayLastChosenOnes()`
- `onStepPause()` → calls `tableauxPanel.fillTabListAndDisplayLastChosenOnes()`

**FX (`JavaFXEngineListener`):**
- `onBuildStart()` → calls `tableauxPane.clearPremodels()` only
- `onBuildEnd()` → updates status label only
- `onPause()` → enables controls only
- `onStepPause()` → enables step controls only

### 3. No Wallet-to-Display Pipeline in TableauxPane

`TableauxPane.java` has `addPremodel(String)` and `clearPremodels()` but:
- No method to read the Engine's `Wallet` and populate the premodels list
- No method to trigger `CyTableauDisplayer` rendering
- No selection listener to display a chosen tableau
- No connection between `CytoscapeSwingBridge` and actual Cytoscape network views

---

## Swing Display Flow (Reference)

```
Engine.run()
├─ startBuild()
│  └─ listener.onBuildStart()
│     └─ TableauxPanel.fillTabListAndDisplayFirst()
│        ├─ fillTableauxList()                    // reads engine.getCurrentWallet()
│        │  ├─ wallet.getGraphes()                // iterates all tableaux
│        │  └─ populates JList with names
│        ├─ selects first item
│        └─ displaySelectedTableau()
│           └─ CyTableauDisplayer.displayTableauInCy(tableau)
│              ├─ creates CyNode for each TableauNode
│              ├─ creates CyEdge for each TableauEdge
│              ├─ Cytoscape.createNetwork(nodes, edges)
│              └─ doYLayout(layoutName)
│
├─ applyStrategies()                              // rules fire, wallet grows
│
└─ endBuild()
   └─ listener.onBuildEnd()
      └─ TableauxPanel.fillTabListAndDisplayLastChosenOnes()
         ├─ fillTableauxList()                    // refresh from wallet
         ├─ restores previous selection
         └─ displayLastChosenOnes()
            └─ CyTableauDisplayer.displayTableauxInCy(selected)
```

### Key Swing Methods in TableauxPanel

- **`fillTableauxList()`** — Gets `engine.getCurrentWallet()`, iterates `wallet.getGraphes()`, populates list model with "Tableaux Tree" + tableau names, optionally filtering closed ones
- **`fillTabListAndDisplayFirst()`** — Calls `fillTableauxList()`, selects index 1 (first actual tableau), calls `displaySelectedTableau()`
- **`fillTabListAndDisplayLastChosenOnes()`** — Calls `fillTableauxList()`, restores `lastSelectedIndices`, calls `displayLastChosenOnes()`
- **`displaySelectedTableau()`** — Index 0 → tree view via `CyTableauDisplayer.displayTableauxTreeInCy()`, else single tableau via `CyTableauDisplayer.displayTableauInCy()`
- **`displayLastChosenOnes()`** — Renders multiple selected tableaux, handles tile/cascade layout

### CyTableauDisplayer Core Rendering

`CyTableauDisplayer.displayTableau(Tableau t)`:
1. Iterates `t.getNodesEnumeration()` → creates `CyNode` per `TableauNode`
2. Sets node attributes: name, formulas list, marks list, closed flag
3. Iterates edges → creates `CyEdge` per `TableauEdge`
4. Sets edge attributes: relations list, isTableauTreeEdge flag
5. Calls `Cytoscape.createNetwork(nodes, edges, name)` → creates CyNetwork
6. Applies yFiles layout via `doYLayout()`

### CyTableauDisplayer.flush()

Destroys all previously created networks, resets node/edge attributes.

---

## Key Data Structures

| Class | Access | Returns |
|-------|--------|---------|
| `Engine.getCurrentWallet()` | Wallet containing all tableaux | `Wallet` |
| `Wallet.getGraphes()` | All tableaux as graphs | `Vector<Graph>` |
| `Wallet.getGraph(String name)` | Specific tableau by name | `Graph` |
| `Tableau.getNodesEnumeration()` | All nodes in tableau | `Enumeration<TableauNode>` |
| `TableauNode.getMarkedExpressionsEnum()` | Formulas in node | `Enumeration<MarkedExpression>` |
| `TableauNode.getNextEdgesEnum()` | Outgoing edges | `Enumeration<TableauEdge>` |
| `Tableau.isClosed()` | Whether branch is closed | `boolean` |
| `Tableau.getDuplicationInitialParent()` | Parent in tree | `Tableau` |

---

## Implementation Plan

### Step 1: Initialize Cytoscape in LauncherFX

**File:** `src/lotrec/guifx/LauncherFX.java`

- Call `new CyMain(cyArgs)` with the same plugin arguments as Swing `Launcher.java`
- Must run on the AWT EDT since Cytoscape is Swing-based: `SwingUtilities.invokeAndWait(() -> new CyMain(cyArgs))`
- Hide Cytoscape's own toolbar, west panel, south panel (same as Swing)
- This must happen **before** the JavaFX stage is shown, so Cytoscape's singleton is ready

### Step 2: Embed Cytoscape Desktop in CytoscapeSwingBridge

**File:** `src/lotrec/guifx/graph/CytoscapeSwingBridge.java`

- After Cytoscape is initialized, get the desktop pane: `Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane()`
- Call `displayComponent(desktopPane)` to embed it in the SwingNode
- This gives us the live Cytoscape canvas where networks appear when created

### Step 3: Add Wallet Display Methods to TableauxPane

**File:** `src/lotrec/guifx/TableauxPane.java`

Add methods mirroring Swing's `TableauxPanel`:

- **`fillTableauxList(Engine engine)`** — Read `engine.getCurrentWallet().getGraphes()`, populate `premodelsItems` with "Tableaux Tree" + tableau names, apply closed-filter if checkbox selected
- **`fillTabListAndDisplayFirst(Engine engine)`** — Call `fillTableauxList()`, select index 1, call `displaySelectedTableau()`
- **`fillTabListAndDisplayLastChosenOnes(Engine engine)`** — Save/restore selection indices, refresh list, display
- **`displaySelectedTableau(Engine engine)`** — If index 0: `CyTableauDisplayer.displayTableauxTreeInCy()`; else: `CyTableauDisplayer.displayTableauInCy(tableau)`
- Track `lastSelectedIndices` for selection restoration across refreshes

Add a selection listener on `premodelsList` to call `displaySelectedTableau()` when user clicks.

### Step 4: Wire JavaFXEngineListener to TableauxPane

**File:** `src/lotrec/engine/JavaFXEngineListener.java`

Implement all the missing display callbacks. The listener needs access to the Engine to read the wallet. Options:
- Pass engine reference when creating the listener
- Access via `mainFrameFX.getEngine()`

Update these methods:

```java
@Override
public void onBuildStart() {
    Platform.runLater(() -> {
        // existing controls code...
        mainFrameFX.getTableauxPane().fillTabListAndDisplayFirst(mainFrameFX.getEngine());
    });
}

@Override
public void onBuildEnd(boolean wasStopped) {
    Platform.runLater(() -> {
        // existing controls code...
        mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
    });
}

@Override
public void onPause() {
    Platform.runLater(() -> {
        // existing controls code...
        mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
    });
}

@Override
public void onStepPause(EventMachine ruleEM) {
    Platform.runLater(() -> {
        // existing controls code...
        mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
    });
}

@Override
public void refreshTableauxDisplay() {
    Platform.runLater(() -> {
        mainFrameFX.getTableauxPane().fillTabListAndDisplayFirst(mainFrameFX.getEngine());
    });
}

@Override
public void refreshLastChosenTableaux() {
    Platform.runLater(() -> {
        mainFrameFX.getTableauxPane().fillTabListAndDisplayLastChosenOnes(mainFrameFX.getEngine());
    });
}
```

### Step 5: Thread Safety — CyTableauDisplayer Calls Must Use Swing EDT

`CyTableauDisplayer` creates Cytoscape networks (Swing components). When called from `Platform.runLater()` (JavaFX thread), the Cytoscape calls must be wrapped:

```java
SwingUtilities.invokeLater(() -> {
    CyTableauDisplayer.flush();
    CyTableauDisplayer.displayTableauInCy(tableau);
});
```

The list population (JavaFX ListView) stays on the FX thread; only Cytoscape rendering dispatches to Swing EDT.

---

## Files to Modify

| File | Changes |
|------|---------|
| `src/lotrec/guifx/LauncherFX.java` | Add Cytoscape initialization via `new CyMain(cyArgs)` |
| `src/lotrec/guifx/graph/CytoscapeSwingBridge.java` | Embed `Cytoscape.getDesktop()` network pane after init |
| `src/lotrec/guifx/TableauxPane.java` | Add wallet display methods, selection listener, lastSelectedIndices tracking |
| `src/lotrec/engine/JavaFXEngineListener.java` | Implement all display callbacks, wire to TableauxPane |

## Risks & Considerations

- **Swing/FX thread interop**: Cytoscape is Swing-based. All Cytoscape calls must be on the Swing EDT (`SwingUtilities.invokeLater`), while JavaFX UI updates must be on the FX Application Thread (`Platform.runLater`). Mixing these causes deadlocks or crashes.
- **Cytoscape Desktop visibility**: Cytoscape normally creates its own JFrame. We need to suppress that and only embed the internal desktop pane. Check if `CyMain` can be configured headless or if the JFrame needs to be hidden.
- **yFiles layout plugin**: The layout algorithm (`doYLayout`) navigates Cytoscape's menu structure programmatically. This may not work if the Cytoscape desktop frame is hidden.
- **Memory**: Each CyNetwork stays in memory until `flush()` is called. Ensure flush is called before each new display.

## Verification

1. `$GW build` — compiles without errors
2. `$GW run` — load predefined logic K, select a testing formula, click "Build Premodels"
3. Premodels list should populate with "Tableaux Tree" + tableau names
4. Selecting a tableau should render its graph in the Cytoscape area
5. Selecting "Tableaux Tree" should show the tree of all tableaux
6. Step-by-step mode should update display at each pause

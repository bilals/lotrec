package lotrec.guifx;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lotrec.dataStructure.Logic;
import lotrec.guifx.logicspane.LogicDefTab;

import java.util.HashMap;
import java.util.Map;

public class LoadedLogicsPane extends TabPane {

    private final Map<Logic, Tab> logicTabs = new HashMap<>();

    public LoadedLogicsPane() {
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        getStyleClass().add("loaded-logics-pane");
    }

    public void addLogic(Logic logic) {
        if (logicTabs.containsKey(logic)) {
            getSelectionModel().select(logicTabs.get(logic));
            return;
        }
        LogicDefTab content = new LogicDefTab(logic);
        Tab tab = new Tab(logic.getName(), content);
        tab.setOnClosed(e -> logicTabs.remove(logic));
        logicTabs.put(logic, tab);
        getTabs().add(tab);
        getSelectionModel().select(tab);
    }

    public void removeLogic(Logic logic) {
        Tab tab = logicTabs.remove(logic);
        if (tab != null) {
            getTabs().remove(tab);
        }
    }

    public Logic getSelectedLogic() {
        Tab selected = getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (Map.Entry<Logic, Tab> entry : logicTabs.entrySet()) {
                if (entry.getValue() == selected) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}

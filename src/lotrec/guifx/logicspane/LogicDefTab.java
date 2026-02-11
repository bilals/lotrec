package lotrec.guifx.logicspane;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lotrec.dataStructure.Logic;

public class LogicDefTab extends TabPane {

    private final Logic logic;
    private final ConnTabPane connTabPane;
    private final StratTabPane stratTabPane;
    private final TestingFormulaePane testingFormulaePane;
    private final RulesTabPane rulesTabPane;

    public LogicDefTab(Logic logic) {
        this.logic = logic;

        connTabPane = new ConnTabPane(logic);
        stratTabPane = new StratTabPane(logic);
        testingFormulaePane = new TestingFormulaePane(logic);
        rulesTabPane = new RulesTabPane(logic);

        Tab connTab = new Tab("Connectors", connTabPane);
        connTab.setClosable(false);
        Tab rulesTab = new Tab("Rules", rulesTabPane);
        rulesTab.setClosable(false);
        Tab stratTab = new Tab("Strategies", stratTabPane);
        stratTab.setClosable(false);
        Tab formulasTab = new Tab("Predefined Formulas", testingFormulaePane);
        formulasTab.setClosable(false);

        getTabs().addAll(connTab, rulesTab, stratTab, formulasTab);
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    public Logic getLogic() { return logic; }
    public ConnTabPane getConnTabPane() { return connTabPane; }
    public StratTabPane getStratTabPane() { return stratTabPane; }
    public TestingFormulaePane getTestingFormulaePane() { return testingFormulaePane; }
    public RulesTabPane getRulesTabPane() { return rulesTabPane; }
}

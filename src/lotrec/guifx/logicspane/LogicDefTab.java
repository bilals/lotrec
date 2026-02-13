package lotrec.guifx.logicspane;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lotrec.dataStructure.Logic;

public class LogicDefTab extends VBox {

    private final Logic logic;
    private final TabPane subTabs;
    private final ConnTabPane connTabPane;
    private final StratTabPane stratTabPane;
    private final TestingFormulaePane testingFormulaePane;
    private final RulesTabPane rulesTabPane;

    public LogicDefTab(Logic logic) {
        this.logic = logic;

        // Collapsible description pane
        TextArea descriptionArea = new TextArea(
            logic.getDescription() != null ? logic.getDescription() : "");
        descriptionArea.setEditable(false);
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        TitledPane descriptionPane = new TitledPane("Description", descriptionArea);
        descriptionPane.setExpanded(false);

        connTabPane = new ConnTabPane(logic);
        stratTabPane = new StratTabPane(logic);
        testingFormulaePane = new TestingFormulaePane(logic);
        rulesTabPane = new RulesTabPane(logic);

        subTabs = new TabPane();
        Tab connTab = new Tab("Connectors", connTabPane);
        connTab.setClosable(false);
        Tab rulesTab = new Tab("Rules", rulesTabPane);
        rulesTab.setClosable(false);
        Tab stratTab = new Tab("Strategies", stratTabPane);
        stratTab.setClosable(false);
        Tab formulasTab = new Tab("Predefined Formulas", testingFormulaePane);
        formulasTab.setClosable(false);

        subTabs.getTabs().addAll(connTab, rulesTab, stratTab, formulasTab);
        subTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(subTabs, Priority.ALWAYS);

        getChildren().addAll(descriptionPane, subTabs);
    }

    public Logic getLogic() { return logic; }
    public SingleSelectionModel<Tab> getSelectionModel() { return subTabs.getSelectionModel(); }
    public ConnTabPane getConnTabPane() { return connTabPane; }
    public StratTabPane getStratTabPane() { return stratTabPane; }
    public TestingFormulaePane getTestingFormulaePane() { return testingFormulaePane; }
    public RulesTabPane getRulesTabPane() { return rulesTabPane; }
}

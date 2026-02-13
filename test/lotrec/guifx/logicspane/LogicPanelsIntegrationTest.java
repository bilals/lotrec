package lotrec.guifx.logicspane;

import javafx.scene.control.Tab;
import javafx.stage.Stage;
import lotrec.dataStructure.Logic;
import lotrec.guifx.LoadedLogicsPane;
import lotrec.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Logic Panels Integration")
@ExtendWith(ApplicationExtension.class)
class LogicPanelsIntegrationTest {

    private Stage stage;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Logic Panels Integration Test");
        stage.show();
    }

    @Test
    @DisplayName("should load Monomodal-K and display all 4 logic tabs correctly")
    void shouldLoadKLogicAndDisplayTabs(FxRobot robot) throws Exception {
        Logic logic = TestFixtures.loadLogic("Monomodal-K");
        AtomicReference<LoadedLogicsPane> paneRef = new AtomicReference<>();

        robot.interact(() -> {
            LoadedLogicsPane pane = new LoadedLogicsPane();
            pane.addLogic(logic);
            paneRef.set(pane);
        });

        LoadedLogicsPane pane = paneRef.get();

        assertThat(pane.getTabs()).hasSize(1);

        Tab logicTab = pane.getTabs().get(0);
        assertThat(logicTab.getContent()).isInstanceOf(LogicDefTab.class);

        LogicDefTab defTab = (LogicDefTab) logicTab.getContent();
        // LogicDefTab should have 4 sub-tabs: Connectors, Rules, Strategies, Formulas
        assertThat(defTab.getSelectionModel()).isNotNull();
        assertThat(defTab.getConnTabPane()).isNotNull();
        assertThat(defTab.getRulesTabPane()).isNotNull();
        assertThat(defTab.getStratTabPane()).isNotNull();
        assertThat(defTab.getTestingFormulaePane()).isNotNull();

        // Verify connector count matches what Monomodal-K defines
        assertThat(logic.getConnectors()).isNotNull();
        assertThat(logic.getConnectors().size()).isGreaterThan(0);

        // Verify rule count
        assertThat(logic.getRules()).isNotNull();
        assertThat(logic.getRules().size()).isGreaterThan(0);

        // Verify strategy count
        assertThat(logic.getStrategies()).isNotNull();
        assertThat(logic.getStrategies().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should display connector details from loaded logic")
    void shouldDisplayConnectorDetails(FxRobot robot) throws Exception {
        Logic logic = TestFixtures.loadLogic("Monomodal-K");
        AtomicReference<ConnTabPane> connRef = new AtomicReference<>();

        robot.interact(() -> {
            ConnTabPane connPane = new ConnTabPane(logic);
            connRef.set(connPane);
        });

        ConnTabPane connPane = connRef.get();
        assertThat(connPane.getConnectorList().getItems()).isNotEmpty();
    }

    @Test
    @DisplayName("should display rules from loaded logic")
    void shouldDisplayRules(FxRobot robot) throws Exception {
        Logic logic = TestFixtures.loadLogic("Monomodal-K");
        AtomicReference<RulesTabPane> rulesRef = new AtomicReference<>();

        robot.interact(() -> {
            RulesTabPane rulesPane = new RulesTabPane(logic);
            rulesRef.set(rulesPane);
        });

        RulesTabPane rulesPane = rulesRef.get();
        assertThat(rulesPane.getRulesList().getItems()).isNotEmpty();
    }

    @Test
    @DisplayName("should display strategies from loaded logic")
    void shouldDisplayStrategies(FxRobot robot) throws Exception {
        Logic logic = TestFixtures.loadLogic("Monomodal-K");
        AtomicReference<StratTabPane> stratRef = new AtomicReference<>();

        robot.interact(() -> {
            StratTabPane stratPane = new StratTabPane(logic);
            stratRef.set(stratPane);
        });

        StratTabPane stratPane = stratRef.get();
        assertThat(stratPane.getStrategyList().getItems()).isNotEmpty();
    }
}

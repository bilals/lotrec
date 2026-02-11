package lotrec.guifx;

import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lotrec.dataStructure.Logic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.assertj.core.api.Assertions.*;

@DisplayName("LoadedLogicsPane")
@ExtendWith(ApplicationExtension.class)
class LoadedLogicsPaneTest {

    private LoadedLogicsPane pane;

    @Start
    void start(Stage stage) {
        pane = new LoadedLogicsPane();
        javafx.scene.Scene scene = new javafx.scene.Scene(pane, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    @DisplayName("should be a TabPane")
    void shouldBeTabPane(FxRobot robot) {
        assertThat(pane).isInstanceOf(TabPane.class);
    }

    @Test
    @DisplayName("should start with no tabs")
    void shouldStartEmpty(FxRobot robot) {
        assertThat(pane.getTabs()).isEmpty();
    }

    @Test
    @DisplayName("should add a tab when logic is loaded")
    void shouldAddTabForLogic(FxRobot robot) {
        Logic logic = new Logic();
        logic.setName("TestLogic");
        robot.interact(() -> pane.addLogic(logic));
        assertThat(pane.getTabs()).hasSize(1);
        assertThat(pane.getTabs().get(0).getText()).isEqualTo("TestLogic");
    }

    @Test
    @DisplayName("should remove a tab when logic is closed")
    void shouldRemoveTabForLogic(FxRobot robot) {
        Logic logic = new Logic();
        logic.setName("TestLogic");
        robot.interact(() -> {
            pane.addLogic(logic);
            pane.removeLogic(logic);
        });
        assertThat(pane.getTabs()).isEmpty();
    }
}

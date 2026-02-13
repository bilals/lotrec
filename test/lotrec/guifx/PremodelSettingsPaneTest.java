package lotrec.guifx;

import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PremodelSettingsPane")
@ExtendWith(ApplicationExtension.class)
class PremodelSettingsPaneTest {

    private Stage stage;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        stage.show();
    }

    @Test
    @DisplayName("should display formula input field")
    void shouldDisplayFormulaInput(FxRobot robot) {
        AtomicReference<PremodelSettingsPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new PremodelSettingsPane()));
        assertThat(ref.get().getFormulaField()).isNotNull();
    }

    @Test
    @DisplayName("should accept formula text")
    void shouldAcceptFormulaText(FxRobot robot) {
        AtomicReference<PremodelSettingsPane> ref = new AtomicReference<>();
        robot.interact(() -> {
            PremodelSettingsPane pane = new PremodelSettingsPane();
            pane.getFormulaField().setText("and p q");
            ref.set(pane);
        });
        assertThat(ref.get().getFormulaField().getText()).isEqualTo("and p q");
    }

    @Test
    @DisplayName("should have build button")
    void shouldHaveBuildButton(FxRobot robot) {
        AtomicReference<PremodelSettingsPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new PremodelSettingsPane()));
        assertThat(ref.get().getBuildButton()).isNotNull();
        assertThat(ref.get().getBuildButton().getText()).contains("Build");
    }

    @Test
    @DisplayName("should have step-by-step and sat check buttons")
    void shouldHaveEngineButtons(FxRobot robot) {
        AtomicReference<PremodelSettingsPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new PremodelSettingsPane()));
        assertThat(ref.get().getStepButton()).isNotNull();
        assertThat(ref.get().getSatCheckButton()).isNotNull();
    }
}

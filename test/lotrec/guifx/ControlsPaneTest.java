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

@DisplayName("ControlsPane")
@ExtendWith(ApplicationExtension.class)
class ControlsPaneTest {

    private Stage stage;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        stage.show();
    }

    @Test
    @DisplayName("should display control buttons")
    void shouldDisplayControlButtons(FxRobot robot) {
        AtomicReference<ControlsPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new ControlsPane()));
        assertThat(ref.get().getNextStepButton()).isNotNull();
        assertThat(ref.get().getPauseResumeButton()).isNotNull();
        assertThat(ref.get().getStopButton()).isNotNull();
    }

    @Test
    @DisplayName("should disable controls during idle")
    void shouldDisableControlsDuringIdle(FxRobot robot) {
        AtomicReference<ControlsPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new ControlsPane()));
        // Controls should be disabled when no engine is running
        assertThat(ref.get().getNextStepButton().isDisabled()).isTrue();
        assertThat(ref.get().getPauseResumeButton().isDisabled()).isTrue();
        assertThat(ref.get().getStopButton().isDisabled()).isTrue();
    }

    @Test
    @DisplayName("should enable controls when activated")
    void shouldEnableControlsWhenActivated(FxRobot robot) {
        AtomicReference<ControlsPane> ref = new AtomicReference<>();
        robot.interact(() -> {
            ControlsPane pane = new ControlsPane();
            pane.enableControls();
            ref.set(pane);
        });
        assertThat(ref.get().getPauseResumeButton().isDisabled()).isFalse();
        assertThat(ref.get().getStopButton().isDisabled()).isFalse();
    }

    @Test
    @DisplayName("should disable controls after disableControls()")
    void shouldDisableControlsAfterDisable(FxRobot robot) {
        AtomicReference<ControlsPane> ref = new AtomicReference<>();
        robot.interact(() -> {
            ControlsPane pane = new ControlsPane();
            pane.enableControls();
            pane.disableControls();
            ref.set(pane);
        });
        assertThat(ref.get().getNextStepButton().isDisabled()).isTrue();
        assertThat(ref.get().getPauseResumeButton().isDisabled()).isTrue();
        assertThat(ref.get().getStopButton().isDisabled()).isTrue();
    }
}

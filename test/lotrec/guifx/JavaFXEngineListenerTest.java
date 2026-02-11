package lotrec.guifx;

import javafx.stage.Stage;
import lotrec.engine.EngineListener;
import lotrec.engine.EngineStatus;
import lotrec.engine.JavaFXEngineListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JavaFXEngineListener")
@ExtendWith(ApplicationExtension.class)
class JavaFXEngineListenerTest {

    private MainFrameFX mainFrame;

    @Start
    void start(Stage stage) {
        mainFrame = new MainFrameFX(stage);
        stage.show();
    }

    @Test
    @DisplayName("should implement EngineListener interface")
    void shouldImplementEngineListener(FxRobot robot) {
        AtomicReference<JavaFXEngineListener> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new JavaFXEngineListener(mainFrame)));
        assertThat(ref.get()).isInstanceOf(EngineListener.class);
    }

    @Test
    @DisplayName("should accept MainFrameFX in constructor")
    void shouldAcceptMainFrameFX(FxRobot robot) {
        AtomicReference<JavaFXEngineListener> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new JavaFXEngineListener(mainFrame)));
        assertThat(ref.get()).isNotNull();
        assertThat(ref.get().getMainFrameFX()).isSameAs(mainFrame);
    }

    @Test
    @DisplayName("should reject null MainFrameFX")
    void shouldRejectNull(FxRobot robot) {
        assertThatThrownBy(() -> new JavaFXEngineListener(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should handle onBuildStart without errors")
    void shouldHandleBuildStart(FxRobot robot) {
        AtomicReference<JavaFXEngineListener> ref = new AtomicReference<>();
        robot.interact(() -> {
            JavaFXEngineListener listener = new JavaFXEngineListener(mainFrame);
            listener.onBuildStart();
            ref.set(listener);
        });
        assertThat(ref.get()).isNotNull();
    }

    @Test
    @DisplayName("should handle onBuildEnd without errors")
    void shouldHandleBuildEnd(FxRobot robot) {
        AtomicReference<JavaFXEngineListener> ref = new AtomicReference<>();
        robot.interact(() -> {
            JavaFXEngineListener listener = new JavaFXEngineListener(mainFrame);
            listener.onBuildEnd(false);
            ref.set(listener);
        });
        assertThat(ref.get()).isNotNull();
    }

    @Test
    @DisplayName("should handle onStatusChanged without errors")
    void shouldHandleStatusChanged(FxRobot robot) {
        AtomicReference<JavaFXEngineListener> ref = new AtomicReference<>();
        robot.interact(() -> {
            JavaFXEngineListener listener = new JavaFXEngineListener(mainFrame);
            listener.onStatusChanged(EngineStatus.NORMAL);
            ref.set(listener);
        });
        assertThat(ref.get()).isNotNull();
    }

    @Test
    @DisplayName("should handle onRuntimeError without errors")
    void shouldHandleRuntimeError(FxRobot robot) {
        AtomicReference<JavaFXEngineListener> ref = new AtomicReference<>();
        robot.interact(() -> {
            JavaFXEngineListener listener = new JavaFXEngineListener(mainFrame);
            listener.onRuntimeError("test error");
            ref.set(listener);
        });
        assertThat(ref.get()).isNotNull();
    }
}

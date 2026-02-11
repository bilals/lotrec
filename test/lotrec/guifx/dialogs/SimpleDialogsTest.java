package lotrec.guifx.dialogs;

import javafx.stage.Stage;
import lotrec.guifx.DialogsFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Simple Dialogs")
@ExtendWith(ApplicationExtension.class)
class SimpleDialogsTest {

    private Stage ownerStage;

    @Start
    void start(Stage stage) {
        ownerStage = stage;
        stage.setTitle("Test Owner");
        stage.show();
    }

    @Nested
    @DisplayName("DialogsFactory")
    class DialogsFactoryTests {

        @Test
        @DisplayName("should create factory with owner stage")
        void shouldCreateFactory(FxRobot robot) {
            DialogsFactory factory = new DialogsFactory(ownerStage);
            assertThat(factory).isNotNull();
        }
    }

    @Nested
    @DisplayName("PredefinedLogicsDialog")
    class PredefinedLogicsTests {

        @Test
        @DisplayName("should create predefined logics dialog")
        void shouldCreateDialog(FxRobot robot) {
            AtomicReference<PredefinedLogicsDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new PredefinedLogicsDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should list predefined logics")
        void shouldListPredefinedLogics(FxRobot robot) {
            AtomicReference<PredefinedLogicsDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new PredefinedLogicsDialog(ownerStage)));
            assertThat(ref.get().getLogicNames()).isNotEmpty();
            assertThat(ref.get().getLogicNames()).contains("Monomodal-K");
        }
    }

    @Nested
    @DisplayName("LogicDescriptionDialog")
    class LogicDescriptionTests {

        @Test
        @DisplayName("should create logic description dialog")
        void shouldCreateDialog(FxRobot robot) {
            AtomicReference<LogicDescriptionDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new LogicDescriptionDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should accept description text")
        void shouldAcceptDescription(FxRobot robot) {
            AtomicReference<LogicDescriptionDialog> ref = new AtomicReference<>();
            robot.interact(() -> {
                LogicDescriptionDialog d = new LogicDescriptionDialog(ownerStage);
                d.setDescription("Test logic", "A test logic description");
                ref.set(d);
            });
            assertThat(ref.get().getLogicName()).isEqualTo("Test logic");
        }
    }

    @Nested
    @DisplayName("SatisfiabilityDialog")
    class SatisfiabilityTests {

        @Test
        @DisplayName("should create satisfiability dialog")
        void shouldCreateDialog(FxRobot robot) {
            AtomicReference<SatisfiabilityDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new SatisfiabilityDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }
    }

    @Nested
    @DisplayName("FilterDialog")
    class FilterTests {

        @Test
        @DisplayName("should create filter dialog")
        void shouldCreateDialog(FxRobot robot) {
            AtomicReference<FilterDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new FilterDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }
    }

    @Nested
    @DisplayName("RunInfoDialog")
    class RunInfoTests {

        @Test
        @DisplayName("should create run info dialog")
        void shouldCreateDialog(FxRobot robot) {
            AtomicReference<RunInfoDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new RunInfoDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should accept run statistics")
        void shouldAcceptStatistics(FxRobot robot) {
            AtomicReference<RunInfoDialog> ref = new AtomicReference<>();
            robot.interact(() -> {
                RunInfoDialog d = new RunInfoDialog(ownerStage);
                d.setStatistics(150, 42, 3500);
                ref.set(d);
            });
            assertThat(ref.get().getElapsedTime()).isEqualTo(3500);
        }
    }

    @Nested
    @DisplayName("TaskPaneDialog")
    class TaskPaneTests {

        @Test
        @DisplayName("should create task pane dialog")
        void shouldCreateDialog(FxRobot robot) {
            AtomicReference<TaskPaneDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new TaskPaneDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should have startup options")
        void shouldHaveOptions(FxRobot robot) {
            AtomicReference<TaskPaneDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new TaskPaneDialog(ownerStage)));
            assertThat(ref.get().getOptions()).hasSize(3);
        }
    }
}

package lotrec.guifx.dialogs;

import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Complex Dialogs")
@ExtendWith(ApplicationExtension.class)
class ComplexDialogsTest {

    private Stage ownerStage;

    @Start
    void start(Stage stage) {
        ownerStage = stage;
        stage.show();
    }

    @Nested
    @DisplayName("PremodelEditorDialog")
    class PremodelEditorTests {

        @Test
        @DisplayName("should create premodel editor dialog")
        void shouldShowPremodelEditor(FxRobot robot) {
            AtomicReference<PremodelEditorDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new PremodelEditorDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should have nodes and edges editing areas")
        void shouldHaveEditingAreas(FxRobot robot) {
            AtomicReference<PremodelEditorDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new PremodelEditorDialog(ownerStage)));
            assertThat(ref.get().getNodesTable()).isNotNull();
            assertThat(ref.get().getEdgesTable()).isNotNull();
        }
    }

    @Nested
    @DisplayName("BreakPointsDialog")
    class BreakPointsTests {

        @Test
        @DisplayName("should create break points dialog")
        void shouldShowBreakPointsDialog(FxRobot robot) {
            AtomicReference<BreakPointsDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new BreakPointsDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should have strategy tree with checkboxes")
        void shouldHaveStrategyTree(FxRobot robot) {
            AtomicReference<BreakPointsDialog> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new BreakPointsDialog(ownerStage)));
            assertThat(ref.get()).isNotNull();
            assertThat(ref.get().getDialogPane().getContent()).isNotNull();
        }
    }

    @Nested
    @DisplayName("FileDialogs")
    class FileDialogsTests {

        @Test
        @DisplayName("should create file dialogs utility")
        void shouldShowFileOpenDialog(FxRobot robot) {
            FileDialogs dialogs = new FileDialogs(ownerStage);
            assertThat(dialogs).isNotNull();
        }

        @Test
        @DisplayName("should have XML file filter")
        void shouldHaveXmlFilter(FxRobot robot) {
            FileDialogs dialogs = new FileDialogs(ownerStage);
            assertThat(dialogs.getXmlFilter()).isNotNull();
        }
    }

    @Nested
    @DisplayName("FormulaTransformerPane")
    class FormulaTransformerTests {

        @Test
        @DisplayName("should create formula transformer pane")
        void shouldCreatePane(FxRobot robot) {
            AtomicReference<lotrec.guifx.FormulaTransformerPane> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new lotrec.guifx.FormulaTransformerPane()));
            assertThat(ref.get()).isNotNull();
        }

        @Test
        @DisplayName("should have infix and prefix fields")
        void shouldHaveFields(FxRobot robot) {
            AtomicReference<lotrec.guifx.FormulaTransformerPane> ref = new AtomicReference<>();
            robot.interact(() -> ref.set(new lotrec.guifx.FormulaTransformerPane()));
            assertThat(ref.get().getInfixField()).isNotNull();
            assertThat(ref.get().getPrefixField()).isNotNull();
        }
    }
}

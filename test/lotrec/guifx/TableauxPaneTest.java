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

@DisplayName("TableauxPane")
@ExtendWith(ApplicationExtension.class)
class TableauxPaneTest {

    private Stage stage;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        stage.show();
    }

    @Test
    @DisplayName("should display premodels list")
    void shouldDisplayPremodelsList(FxRobot robot) {
        AtomicReference<TableauxPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new TableauxPane()));
        assertThat(ref.get().getPremodelsList()).isNotNull();
    }

    @Test
    @DisplayName("should start with empty premodels list")
    void shouldStartEmpty(FxRobot robot) {
        AtomicReference<TableauxPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new TableauxPane()));
        assertThat(ref.get().getPremodelsList().getItems()).isEmpty();
    }

    @Test
    @DisplayName("should have graph display area")
    void shouldHaveGraphDisplayArea(FxRobot robot) {
        AtomicReference<TableauxPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new TableauxPane()));
        assertThat(ref.get().getGraphDisplayArea()).isNotNull();
    }

    @Test
    @DisplayName("should have status display labels")
    void shouldHaveStatusLabels(FxRobot robot) {
        AtomicReference<TableauxPane> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new TableauxPane()));
        assertThat(ref.get().getTableauxCountLabel()).isNotNull();
        assertThat(ref.get().getElapsedTimeLabel()).isNotNull();
    }
}

package lotrec.guifx.graph;

import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CytoscapeSwingBridge")
@ExtendWith(ApplicationExtension.class)
class CytoscapeSwingBridgeTest {

    private Stage stage;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        stage.show();
    }

    @Test
    @DisplayName("should create bridge with graph display area")
    void shouldDisplayGraphArea(FxRobot robot) {
        AtomicReference<CytoscapeSwingBridge> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new CytoscapeSwingBridge()));
        assertThat(ref.get()).isNotNull();
        assertThat(ref.get().getChildren()).isNotEmpty();
    }

    @Test
    @DisplayName("should show error placeholder on bridge failure")
    void shouldShowErrorOnBridgeFailure(FxRobot robot) {
        AtomicReference<CytoscapeSwingBridge> ref = new AtomicReference<>();
        robot.interact(() -> {
            CytoscapeSwingBridge bridge = new CytoscapeSwingBridge();
            bridge.showError("Test error message");
            ref.set(bridge);
        });
        assertThat(ref.get().isShowingError()).isTrue();
    }

    @Test
    @DisplayName("should have SwingNode for Cytoscape embedding")
    void shouldEmbedCytoscapeViaSwingNode(FxRobot robot) {
        AtomicReference<CytoscapeSwingBridge> ref = new AtomicReference<>();
        robot.interact(() -> ref.set(new CytoscapeSwingBridge()));
        assertThat(ref.get().getSwingNode()).isNotNull();
    }

    @Test
    @DisplayName("should clear display")
    void shouldClearDisplay(FxRobot robot) {
        AtomicReference<CytoscapeSwingBridge> ref = new AtomicReference<>();
        robot.interact(() -> {
            CytoscapeSwingBridge bridge = new CytoscapeSwingBridge();
            bridge.clear();
            ref.set(bridge);
        });
        assertThat(ref.get().isShowingError()).isFalse();
    }
}

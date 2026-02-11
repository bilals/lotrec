package lotrec.guifx;

import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MainFrameFX")
@ExtendWith(ApplicationExtension.class)
class MainFrameFXTest {

    private MainFrameFX mainFrame;

    @Start
    void start(Stage stage) {
        mainFrame = new MainFrameFX(stage);
        stage.show();
    }

    @Test
    @DisplayName("should display main window with correct title")
    void shouldDisplayMainWindow(FxRobot robot) {
        Stage stage = mainFrame.getStage();
        assertThat(stage.isShowing()).isTrue();
        assertThat(stage.getTitle()).contains("LoTREC");
    }

    @Test
    @DisplayName("should have a menu bar")
    void shouldHaveMenuBar(FxRobot robot) {
        MenuBar menuBar = mainFrame.getMenuBar();
        assertThat(menuBar).isNotNull();
        assertThat(menuBar.getMenus()).isNotEmpty();
    }

    @Test
    @DisplayName("should have File, Logic, View, Premodels, and Help menus")
    void shouldHaveExpectedMenus(FxRobot robot) {
        MenuBar menuBar = mainFrame.getMenuBar();
        assertThat(menuBar.getMenus()).hasSizeGreaterThanOrEqualTo(5);
        assertThat(menuBar.getMenus().get(0).getText()).isEqualTo("Control");
        assertThat(menuBar.getMenus().get(1).getText()).isEqualTo("Logic");
        assertThat(menuBar.getMenus().get(2).getText()).isEqualTo("View");
        assertThat(menuBar.getMenus().get(3).getText()).isEqualTo("Premodels");
        assertThat(menuBar.getMenus().get(4).getText()).isEqualTo("Help");
    }

    @Test
    @DisplayName("should have split pane layout")
    void shouldHaveSplitPaneLayout(FxRobot robot) {
        SplitPane mainSplit = mainFrame.getMainSplitPane();
        assertThat(mainSplit).isNotNull();
        assertThat(mainSplit.getItems()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("should provide left pane accessor")
    void shouldProvideLeftPane(FxRobot robot) {
        assertThat(mainFrame.getLeftPane()).isNotNull();
    }

    @Test
    @DisplayName("should provide right pane accessor")
    void shouldProvideRightPane(FxRobot robot) {
        assertThat(mainFrame.getRightPane()).isNotNull();
    }

    @Test
    @DisplayName("should have keyboard accelerators on Logic menu items")
    void shouldHaveKeyboardAccelerators(FxRobot robot) {
        MenuBar menuBar = mainFrame.getMenuBar();
        // Logic menu is index 1
        var logicMenu = menuBar.getMenus().get(1);
        // First item should be "New..." with Ctrl+N
        var newItem = logicMenu.getItems().get(0);
        assertThat(newItem.getAccelerator()).isNotNull();
    }
}

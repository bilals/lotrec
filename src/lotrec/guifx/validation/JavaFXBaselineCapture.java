package lotrec.guifx.validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lotrec.Lotrec;
import lotrec.dataStructure.Logic;
import lotrec.guifx.MainFrameFX;
import lotrec.guifx.LoadedLogicsPane;
import lotrec.guifx.logicspane.LogicDefTab;
import javafx.scene.control.Tab;

/**
 * Programmatic JavaFX baseline screenshot capture utility.
 * <p>
 * Launches the full JavaFX GUI, loads Monomodal-K logic, and captures
 * screenshots of key application states for visual migration validation.
 * <p>
 * States requiring interactive user input (menus, file dialogs, proof search
 * results) are covered by the manual reference screenshots in
 * {@code .specify/memory/GUI-V2/}.
 * <p>
 * Usage: {@code gradlew.bat captureJavaFXBaseline}
 */
public class JavaFXBaselineCapture extends Application {

    private static Path outputDir;
    private MainFrameFX mainFrame;
    private JavaFXScreenshotCapture capture;
    private int capturedCount = 0;

    public static void main(String[] args) {
        outputDir = Paths.get("build/screenshots/javafx-current");
        if (args.length > 0) {
            outputDir = Paths.get(args[0]);
        }
        outputDir.toFile().mkdirs();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        capture = new JavaFXScreenshotCapture(outputDir);

        System.out.println("=== JavaFX Baseline Screenshot Capture ===");
        System.out.println("Output: " + outputDir.toAbsolutePath());

        // Initialize Lotrec on a background thread (same as LauncherFX)
        Thread initThread = new Thread(() -> {
            Lotrec.initialize(Lotrec.GUI_RUN_MODE);

            // Create MainFrameFX on the FX Application Thread
            CountDownLatch frameLatch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    mainFrame = new MainFrameFX(primaryStage);

                    // Override maximized and set fixed size for reproducible screenshots
                    primaryStage.setMaximized(false);
                    primaryStage.setWidth(1280);
                    primaryStage.setHeight(900);
                    primaryStage.centerOnScreen();
                    primaryStage.show();
                } finally {
                    frameLatch.countDown();
                }
            });

            try {
                frameLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted during frame creation");
                Platform.exit();
                return;
            }

            // Wait for rendering to settle
            sleep(2000);

            try {
                captureAllStates();
            } catch (Exception e) {
                System.err.println("Capture failed: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n=== Capture Complete ===");
            System.out.println("Total screenshots captured: " + capturedCount);
            Platform.exit();
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    private void captureAllStates() throws Exception {
        // --- State 02: Empty Main Frame ---
        captureRootOnFXThread("02-main-frame");

        // --- Load Monomodal-K logic ---
        System.out.println("\nLoading Monomodal-K logic...");
        runOnFXThread(() -> {
            String fileName = "Monomodal-K.xml";
            lotrec.FileUtils.extractPredefinedLogicFile(
                lotrec.PredefinedLogicsLoader.JAR_PATH, fileName);
            String completeFileName = lotrec.FileUtils.PREDEFINED_HOME +
                System.getProperty("file.separator") + fileName;
            Logic logic = Lotrec.openLogicFile(completeFileName);
            if (logic != null) {
                mainFrame.getLoadedLogicsPane().addLogic(logic);
            } else {
                System.err.println("WARNING: Failed to load Monomodal-K logic");
            }
        });
        sleep(1000);

        // --- State 04: Main Frame with Loaded Logic - Connectors Tab (default) ---
        captureRootOnFXThread("04-main-frame-connectors-tab");

        // Get LogicDefTab for tab navigation
        AtomicReference<LogicDefTab> logicDefTabRef = new AtomicReference<>();
        runOnFXThread(() -> {
            LoadedLogicsPane loadedLogicsPane = mainFrame.getLoadedLogicsPane();
            Tab selectedTab = loadedLogicsPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null && selectedTab.getContent() instanceof LogicDefTab) {
                logicDefTabRef.set((LogicDefTab) selectedTab.getContent());
            }
        });

        LogicDefTab logicDefTab = logicDefTabRef.get();
        if (logicDefTab != null) {
            // --- State 05: Rules Tab ---
            switchTabAndCapture(logicDefTab, 1, "05-rules-tab");

            // --- State 06: Strategy Tab ---
            switchTabAndCapture(logicDefTab, 2, "06-strategy-tab");

            // --- State 07: Predefined Formulas Tab ---
            switchTabAndCapture(logicDefTab, 3, "07-predefined-formulas-tab");

            // Switch back to Connectors for consistency
            runOnFXThread(() -> logicDefTab.getSelectionModel().select(0));
        } else {
            System.err.println("WARNING: Could not find LogicDefTab");
        }

        // --- State 32: Controls Panel (sub-component) ---
        captureNodeOnFXThread(mainFrame.getControlsPane(), "32-controls-panel");

        // --- State 33: Loaded Logics Panel (sub-component) ---
        captureNodeOnFXThread(mainFrame.getLoadedLogicsPane(), "33-loaded-logics-panel");
    }

    private void captureRootOnFXThread(String label) throws Exception {
        runOnFXThread(() -> {
            try {
                Scene scene = mainFrame.getStage().getScene();
                if (scene != null && scene.getRoot() != null) {
                    File file = capture.captureAndSave(scene.getRoot(), label);
                    capturedCount++;
                    System.out.println("  Captured: " + label + " -> " + file.getName());
                } else {
                    System.err.println("  SKIP: " + label + " (no scene/root)");
                }
            } catch (IOException e) {
                System.err.println("  Failed to capture " + label + ": " + e.getMessage());
            }
        });
        sleep(500);
    }

    private void captureNodeOnFXThread(javafx.scene.Node node, String label) throws Exception {
        runOnFXThread(() -> {
            try {
                if (node != null && node.getScene() != null) {
                    File file = capture.captureAndSave(node, label);
                    capturedCount++;
                    System.out.println("  Captured: " + label + " -> " + file.getName());
                } else {
                    System.err.println("  SKIP: " + label + " (node not in scene)");
                }
            } catch (IOException e) {
                System.err.println("  Failed to capture " + label + ": " + e.getMessage());
            }
        });
        sleep(500);
    }

    private void switchTabAndCapture(LogicDefTab logicDefTab, int index, String label)
            throws Exception {
        runOnFXThread(() -> logicDefTab.getSelectionModel().select(index));
        sleep(500);
        captureRootOnFXThread(label);
    }

    private void runOnFXThread(Runnable task) throws Exception {
        if (Platform.isFxApplicationThread()) {
            task.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Exception> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                task.run();
            } catch (Exception e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (error.get() != null) {
            throw error.get();
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

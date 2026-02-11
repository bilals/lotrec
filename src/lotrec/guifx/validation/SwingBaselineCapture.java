package lotrec.guifx.validation;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import lotrec.Lotrec;
import lotrec.gui.MainFrame;
import lotrec.gui.logicspane.LogicDefTab;
import lotrec.resources.ResourcesProvider;

/**
 * Programmatic Swing baseline screenshot capture utility.
 * <p>
 * Launches the full Swing GUI, loads Monomodal-K logic, and captures
 * screenshots of key application states for visual migration validation.
 * <p>
 * States requiring interactive user input (menus, file dialogs, proof search
 * results) are covered by the manual reference screenshots in
 * {@code .specify/memory/GUI-V2/}.
 * <p>
 * Usage: {@code gradlew.bat captureSwingBaseline}
 */
public class SwingBaselineCapture {

    private static final Dimension CAPTURE_SIZE = new Dimension(1280, 900);
    private static MainFrame mainFrame;
    private static SwingScreenshotCapture capture;
    private static int capturedCount = 0;

    public static void main(String[] args) throws Exception {
        Path outputDir = Paths.get("specs/001-javafx-gui-migration/screenshots/swing-baseline/programmatic");
        if (args.length > 0) {
            outputDir = Paths.get(args[0]);
        }
        outputDir.toFile().mkdirs();
        capture = new SwingScreenshotCapture(outputDir);

        System.out.println("=== Swing Baseline Screenshot Capture ===");
        System.out.println("Output: " + outputDir.toAbsolutePath());

        // Initialize on EDT
        CountDownLatch initLatch = new CountDownLatch(1);
        AtomicReference<Exception> initError = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            try {
                initialize();
            } catch (Exception e) {
                initError.set(e);
            } finally {
                initLatch.countDown();
            }
        });
        initLatch.await();

        if (initError.get() != null) {
            System.err.println("Initialization failed: " + initError.get().getMessage());
            initError.get().printStackTrace();
            System.exit(1);
        }

        // Wait for rendering to settle
        Thread.sleep(2000);

        // Capture states
        captureAllStates();

        System.out.println("\n=== Capture Complete ===");
        System.out.println("Total screenshots captured: " + capturedCount);
        System.exit(0);
    }

    private static void initialize() {
        ResourcesProvider.setCurrentLocale(new Locale("en", "US"));
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("L&F error (non-critical): " + e.getMessage());
        }

        mainFrame = new MainFrame();

        URL imgURL = Lotrec.class.getResource("/lotrec/images/lotrecIcon.GIF");
        if (imgURL != null) {
            mainFrame.setIconImage(new ImageIcon(imgURL).getImage());
        }

        Lotrec.initialize(Lotrec.GUI_RUN_MODE);

        // Set fixed size for reproducible screenshots (not maximized)
        mainFrame.setPreferredSize(CAPTURE_SIZE);
        mainFrame.setSize(CAPTURE_SIZE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private static void captureAllStates() throws Exception {
        // --- State 02: Empty Main Frame ---
        captureFrameOnEDT("02-main-frame");

        // --- Load Monomodal-K logic ---
        System.out.println("\nLoading Monomodal-K logic...");
        runOnEDT(() -> new LogicDefTab("Monomodal-K.xml", null, mainFrame));
        Thread.sleep(1000);

        // --- State 04: Main Frame with Loaded Logic - Connectors Tab (default) ---
        captureFrameOnEDT("04-main-frame-connectors-tab");

        // Get the LogicDefTab and its internal tabbed pane
        AtomicReference<JTabbedPane> tabPaneRef = new AtomicReference<>();
        runOnEDT(() -> {
            LogicDefTab tab = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab();
            if (tab != null) {
                JTabbedPane pane = findTabbedPane(tab);
                tabPaneRef.set(pane);
            }
        });

        JTabbedPane logicTabPane = tabPaneRef.get();
        if (logicTabPane != null) {
            // --- State 05: Rules Tab ---
            switchTabAndCapture(logicTabPane, 1, "05-rules-tab");

            // --- State 06: Strategy Tab ---
            switchTabAndCapture(logicTabPane, 2, "06-strategy-tab");

            // --- State 07: Predefined Formulas Tab ---
            switchTabAndCapture(logicTabPane, 3, "07-predefined-formulas-tab");

            // Switch back to Connectors for consistency
            runOnEDT(() -> logicTabPane.setSelectedIndex(0));
        } else {
            System.err.println("WARNING: Could not find logic tab pane");
        }

        // --- State 32: Controls Panel (sub-component) ---
        captureComponentOnEDT(mainFrame.getControlsPanel(), "32-controls-panel");

        // --- LoadedLogicsPanel (sub-component) ---
        captureComponentOnEDT(
                mainFrame.getLoadedLogicsPanel(), "33-loaded-logics-panel");
    }

    private static void captureFrameOnEDT(String label) throws Exception {
        runOnEDT(() -> {
            try {
                captureFrame(label);
            } catch (IOException e) {
                System.err.println("Failed to capture " + label + ": " + e.getMessage());
            }
        });
        Thread.sleep(500);
    }

    private static void captureComponentOnEDT(JComponent component, String label) throws Exception {
        runOnEDT(() -> {
            try {
                if (component.getWidth() > 0 && component.getHeight() > 0) {
                    File file = capture.captureAndSave(component, label);
                    capturedCount++;
                    System.out.println("  Captured: " + label + " -> " + file.getName());
                } else {
                    System.err.println("  SKIP: " + label + " (zero size)");
                }
            } catch (IOException e) {
                System.err.println("Failed to capture " + label + ": " + e.getMessage());
            }
        });
        Thread.sleep(500);
    }

    private static void switchTabAndCapture(JTabbedPane tabPane, int index, String label)
            throws Exception {
        runOnEDT(() -> tabPane.setSelectedIndex(index));
        Thread.sleep(500);
        captureFrameOnEDT(label);
    }

    private static void captureFrame(String label) throws IOException {
        JComponent contentPane = (JComponent) mainFrame.getContentPane();
        if (contentPane.getWidth() <= 0 || contentPane.getHeight() <= 0) {
            System.err.println("  SKIP: " + label + " (frame not yet sized)");
            return;
        }
        File outputFile = capture.captureAndSave(contentPane, label);
        capturedCount++;
        System.out.println("  Captured: " + label + " -> " + outputFile.getName());
    }

    private static JTabbedPane findTabbedPane(JComponent parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JTabbedPane) {
                return (JTabbedPane) c;
            }
            if (c instanceof JComponent) {
                JTabbedPane found = findTabbedPane((JComponent) c);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static void runOnEDT(Runnable task) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Exception> error = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
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
}

package lotrec.guifx.validation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;

/**
 * Batch visual comparison runner.
 * <p>
 * Compares Swing baseline screenshots against JavaFX current screenshots
 * using {@link VisualComparator} and generates a text report.
 * <p>
 * Usage: {@code gradlew.bat compareVisuals}
 */
public class ValidationRunner {

    private static final Path SWING_DIR = Paths.get("build/screenshots/swing-baseline");
    private static final Path JAVAFX_DIR = Paths.get("build/screenshots/javafx-current");
    private static final Path REPORT_FILE = Paths.get("build/screenshots/comparison-report.txt");

    public static void main(String[] args) {
        Path swingDir = SWING_DIR;
        Path javafxDir = JAVAFX_DIR;
        Path reportFile = REPORT_FILE;

        // Allow override via args: [swingDir] [javafxDir] [reportFile]
        if (args.length >= 1) swingDir = Paths.get(args[0]);
        if (args.length >= 2) javafxDir = Paths.get(args[1]);
        if (args.length >= 3) reportFile = Paths.get(args[2]);

        System.out.println("=== Visual Comparison Runner ===");
        System.out.println("Swing baseline:  " + swingDir.toAbsolutePath());
        System.out.println("JavaFX current:  " + javafxDir.toAbsolutePath());
        System.out.println("Report output:   " + reportFile.toAbsolutePath());
        System.out.println();

        if (!Files.isDirectory(swingDir)) {
            System.err.println("ERROR: Swing baseline directory not found: " + swingDir);
            System.err.println("Run 'gradlew captureSwingBaseline' first.");
            System.exit(1);
        }
        if (!Files.isDirectory(javafxDir)) {
            System.err.println("ERROR: JavaFX current directory not found: " + javafxDir);
            System.err.println("Run 'gradlew captureJavaFXBaseline' first.");
            System.exit(1);
        }

        // Collect all PNG files
        File[] swingFiles = swingDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));
        File[] javafxFiles = javafxDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));

        if (swingFiles == null) swingFiles = new File[0];
        if (javafxFiles == null) javafxFiles = new File[0];

        Arrays.sort(swingFiles);
        Arrays.sort(javafxFiles);

        Set<String> swingNames = new TreeSet<>();
        for (File f : swingFiles) swingNames.add(f.getName());

        Set<String> javafxNames = new TreeSet<>();
        for (File f : javafxFiles) javafxNames.add(f.getName());

        VisualComparator comparator = new VisualComparator();
        List<String> reportLines = new ArrayList<>();
        int passed = 0;
        int failed = 0;
        int skipped = 0;

        reportLines.add("=== Visual Comparison Report ===");
        reportLines.add("Swing baseline:  " + swingDir.toAbsolutePath());
        reportLines.add("JavaFX current:  " + javafxDir.toAbsolutePath());
        reportLines.add("");

        // Compare matching pairs
        reportLines.add("--- Per-State Results ---");
        for (File swingFile : swingFiles) {
            String name = swingFile.getName();
            String stateLabel = name.replace(".png", "");
            File javafxFile = javafxDir.resolve(name).toFile();

            if (!javafxFile.exists()) {
                reportLines.add(String.format("  %-35s  SKIPPED (no JavaFX counterpart)", stateLabel));
                skipped++;
                continue;
            }

            try {
                BufferedImage swingImg = ImageIO.read(swingFile);
                BufferedImage javafxImg = ImageIO.read(javafxFile);
                VisualComparator.ComparisonResult result = comparator.compare(swingImg, javafxImg);

                String dimInfo = String.format("%dx%d vs %dx%d",
                    swingImg.getWidth(), swingImg.getHeight(),
                    javafxImg.getWidth(), javafxImg.getHeight());
                String dimStatus = result.dimensionsMatch() ? "dims-match" : "DIMS-MISMATCH";
                String verdict = result.hasMajorDifferences() ? "FAIL" : "PASS";
                String diffPct = String.format("%.1f%%", result.getDifferencePercentage());

                reportLines.add(String.format("  %-35s  %s  diff=%s  %s  [%s]",
                    stateLabel, verdict, diffPct, dimStatus, dimInfo));

                if (result.hasMajorDifferences()) {
                    failed++;
                } else {
                    passed++;
                }
            } catch (IOException e) {
                reportLines.add(String.format("  %-35s  ERROR: %s", stateLabel, e.getMessage()));
                failed++;
            }
        }

        // Report JavaFX-only files
        Set<String> javafxOnly = new TreeSet<>(javafxNames);
        javafxOnly.removeAll(swingNames);
        if (!javafxOnly.isEmpty()) {
            reportLines.add("");
            reportLines.add("--- JavaFX-Only Files (no Swing baseline) ---");
            for (String name : javafxOnly) {
                reportLines.add("  " + name);
            }
        }

        // Summary
        reportLines.add("");
        reportLines.add("--- Summary ---");
        reportLines.add(String.format("  Passed:  %d", passed));
        reportLines.add(String.format("  Failed:  %d", failed));
        reportLines.add(String.format("  Skipped: %d", skipped));
        reportLines.add(String.format("  Total:   %d", passed + failed + skipped));

        // Write report
        try {
            reportFile.toFile().getParentFile().mkdirs();
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(reportFile))) {
                for (String line : reportLines) {
                    writer.println(line);
                    System.out.println(line);
                }
            }
            System.out.println("\nReport written to: " + reportFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERROR: Failed to write report: " + e.getMessage());
            // Still print to stdout
            for (String line : reportLines) {
                System.out.println(line);
            }
        }

        // Exit with code 1 if any failures (for CI)
        if (failed > 0) {
            System.exit(1);
        }
    }
}

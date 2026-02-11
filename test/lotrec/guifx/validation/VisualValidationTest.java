package lotrec.guifx.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Visual Validation")
class VisualValidationTest {

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("Swing Screenshot Capture")
    class SwingCapture {

        private SwingScreenshotCapture capture;

        @BeforeEach
        void setUp() {
            capture = new SwingScreenshotCapture(tempDir);
        }

        @Test
        @DisplayName("should capture a Swing component as a BufferedImage")
        void shouldCaptureSwingScreenshot() {
            // Create a simple Swing component to capture
            javax.swing.JPanel panel = new javax.swing.JPanel();
            panel.setSize(200, 100);
            panel.setBackground(java.awt.Color.WHITE);
            // Force layout for off-screen rendering
            panel.doLayout();
            panel.addNotify();

            BufferedImage image = capture.captureComponent(panel, "test-panel");

            assertThat(image).isNotNull();
            assertThat(image.getWidth()).isGreaterThan(0);
            assertThat(image.getHeight()).isGreaterThan(0);
        }

        @Test
        @DisplayName("should save captured screenshot as PNG file")
        void shouldSaveScreenshotAsPng() throws IOException {
            javax.swing.JPanel panel = new javax.swing.JPanel();
            panel.setSize(200, 100);
            panel.setBackground(java.awt.Color.BLUE);
            panel.addNotify();

            File saved = capture.captureAndSave(panel, "test-save");

            assertThat(saved).exists();
            assertThat(saved.getName()).endsWith(".png");
            BufferedImage loaded = ImageIO.read(saved);
            assertThat(loaded).isNotNull();
        }
    }

    @Nested
    @DisplayName("JavaFX Screenshot Capture")
    class JavaFXCapture {

        private JavaFXScreenshotCapture capture;

        @BeforeEach
        void setUp() {
            capture = new JavaFXScreenshotCapture(tempDir);
        }

        @Test
        @DisplayName("should create JavaFX capture utility")
        void shouldCreateJavaFXCaptureUtility() {
            assertThat(capture).isNotNull();
            assertThat(capture.getOutputDir()).isEqualTo(tempDir);
        }
    }

    @Nested
    @DisplayName("Visual Comparator")
    class Comparison {

        private VisualComparator comparator;

        @BeforeEach
        void setUp() {
            comparator = new VisualComparator();
        }

        @Test
        @DisplayName("should compare two identical images as matching")
        void shouldCompareIdenticalImages() {
            BufferedImage img1 = createTestImage(100, 100, java.awt.Color.RED);
            BufferedImage img2 = createTestImage(100, 100, java.awt.Color.RED);

            VisualComparator.ComparisonResult result = comparator.compare(img1, img2);

            assertThat(result.dimensionsMatch()).isTrue();
            assertThat(result.hasMajorDifferences()).isFalse();
        }

        @Test
        @DisplayName("should detect dimension mismatch between images")
        void shouldDetectDimensionMismatch() {
            BufferedImage img1 = createTestImage(100, 100, java.awt.Color.RED);
            BufferedImage img2 = createTestImage(200, 100, java.awt.Color.RED);

            VisualComparator.ComparisonResult result = comparator.compare(img1, img2);

            assertThat(result.dimensionsMatch()).isFalse();
        }

        @Test
        @DisplayName("should detect major color differences between images")
        void shouldDetectMajorDifferences() {
            BufferedImage img1 = createTestImage(100, 100, java.awt.Color.RED);
            BufferedImage img2 = createTestImage(100, 100, java.awt.Color.BLUE);

            VisualComparator.ComparisonResult result = comparator.compare(img1, img2);

            assertThat(result.hasMajorDifferences()).isTrue();
            assertThat(result.getDifferencePercentage()).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("should generate a text comparison report")
        void shouldGenerateComparisonReport() {
            BufferedImage img1 = createTestImage(100, 100, java.awt.Color.RED);
            BufferedImage img2 = createTestImage(100, 100, java.awt.Color.RED);

            VisualComparator.ComparisonResult result = comparator.compare(img1, img2);
            String report = result.toReport("test-state");

            assertThat(report).contains("test-state");
            assertThat(report).containsIgnoringCase("dimensions");
        }

        private BufferedImage createTestImage(int width, int height, java.awt.Color color) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = image.createGraphics();
            g.setColor(color);
            g.fillRect(0, 0, width, height);
            g.dispose();
            return image;
        }
    }
}

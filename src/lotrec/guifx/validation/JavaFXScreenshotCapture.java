package lotrec.guifx.validation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

/**
 * Utility for capturing screenshots of JavaFX nodes/scenes.
 * Captures snapshots of JavaFX {@link Node} instances and optionally
 * saves them as PNG files to a configured output directory.
 */
public class JavaFXScreenshotCapture {

    private final Path outputDir;

    /**
     * Creates a new screenshot capture utility.
     *
     * @param outputDir the directory where captured screenshots will be saved
     */
    public JavaFXScreenshotCapture(Path outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Returns the output directory where screenshots are saved.
     *
     * @return the output directory path
     */
    public Path getOutputDir() {
        return outputDir;
    }

    /**
     * Captures a snapshot of the given JavaFX node and returns it as a {@link WritableImage}.
     *
     * @param node       the JavaFX node to capture
     * @param stateLabel a label describing the state being captured (for documentation purposes)
     * @return the captured image
     */
    public WritableImage captureNode(Node node, String stateLabel) {
        return node.snapshot(null, null);
    }

    /**
     * Captures a snapshot of the given JavaFX node, converts it to a {@link BufferedImage},
     * and saves it as a PNG file in the output directory.
     * <p>
     * The file is named {@code {stateLabel}.png} within the configured output directory.
     *
     * @param node       the JavaFX node to capture
     * @param stateLabel a label used as the filename (without extension)
     * @return the saved PNG file
     * @throws IOException if the image cannot be written to disk
     */
    public File captureAndSave(Node node, String stateLabel) throws IOException {
        WritableImage snapshot = node.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        File outputFile = outputDir.resolve(stateLabel + ".png").toFile();
        outputFile.getParentFile().mkdirs();
        ImageIO.write(bufferedImage, "png", outputFile);

        return outputFile;
    }
}

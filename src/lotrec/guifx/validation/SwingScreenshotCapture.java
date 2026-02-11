package lotrec.guifx.validation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Utility for capturing screenshots of Swing components.
 * Used during migration validation to compare Swing rendering
 * with JavaFX counterparts.
 */
public class SwingScreenshotCapture {

    private final Path outputDir;

    public SwingScreenshotCapture(Path outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Captures a screenshot of the given Swing component as a BufferedImage.
     *
     * @param component  the Swing component to capture
     * @param stateLabel a label describing the current state (used for identification)
     * @return a BufferedImage containing the rendered component
     */
    public BufferedImage captureComponent(JComponent component, String stateLabel) {
        int width = component.getWidth();
        int height = component.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        try {
            component.paint(g2d);
        } finally {
            g2d.dispose();
        }
        return image;
    }

    /**
     * Captures a screenshot of the given Swing component and saves it as a PNG file.
     *
     * @param component  the Swing component to capture
     * @param stateLabel a label used as the filename (without extension)
     * @return the File where the screenshot was saved
     * @throws IOException if the image cannot be written to disk
     */
    public File captureAndSave(JComponent component, String stateLabel) throws IOException {
        BufferedImage image = captureComponent(component, stateLabel);
        File outputFile = outputDir.resolve(stateLabel + ".png").toFile();
        outputFile.getParentFile().mkdirs();
        ImageIO.write(image, "png", outputFile);
        return outputFile;
    }
}

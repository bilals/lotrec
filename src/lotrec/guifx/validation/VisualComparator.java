package lotrec.guifx.validation;

import java.awt.image.BufferedImage;

/**
 * Structural visual comparison tool for comparing BufferedImages.
 * Compares images pixel by pixel with a tolerance threshold to
 * accommodate anti-aliasing and minor rendering differences.
 */
public class VisualComparator {

    /** RGB distance threshold below which two pixels are considered equivalent. */
    private static final int PIXEL_TOLERANCE = 30;

    /** Percentage threshold above which differences are considered major. */
    private static final double MAJOR_DIFFERENCE_THRESHOLD = 10.0;

    /**
     * Compares two images and produces a {@link ComparisonResult}.
     *
     * @param baseline the reference image
     * @param current  the image to compare against the baseline
     * @return a ComparisonResult describing how the two images differ
     */
    public ComparisonResult compare(BufferedImage baseline, BufferedImage current) {
        int w1 = baseline.getWidth();
        int h1 = baseline.getHeight();
        int w2 = current.getWidth();
        int h2 = current.getHeight();

        boolean dimensionsMatch = (w1 == w2) && (h1 == h2);

        double differencePercentage;

        if (!dimensionsMatch) {
            // When dimensions differ, we cannot do a meaningful pixel comparison.
            // Report 100% difference to signal a complete mismatch.
            differencePercentage = 100.0;
        } else {
            int totalPixels = w1 * h1;
            if (totalPixels == 0) {
                differencePercentage = 0.0;
            } else {
                int differingPixels = 0;
                for (int y = 0; y < h1; y++) {
                    for (int x = 0; x < w1; x++) {
                        int rgb1 = baseline.getRGB(x, y);
                        int rgb2 = current.getRGB(x, y);
                        if (pixelsDiffer(rgb1, rgb2)) {
                            differingPixels++;
                        }
                    }
                }
                differencePercentage = (differingPixels / (double) totalPixels) * 100.0;
            }
        }

        boolean majorDifferences = differencePercentage > MAJOR_DIFFERENCE_THRESHOLD;

        return new ComparisonResult(dimensionsMatch, majorDifferences, differencePercentage,
                w1, h1, w2, h2);
    }

    /**
     * Determines whether two pixels differ significantly by computing the
     * Euclidean distance of their RGB components and comparing it against
     * {@link #PIXEL_TOLERANCE}.
     */
    private boolean pixelsDiffer(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;

        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;

        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;

        double distance = Math.sqrt(dr * dr + dg * dg + db * db);
        return distance > PIXEL_TOLERANCE;
    }

    /**
     * Encapsulates the result of a visual comparison between two images.
     */
    public static class ComparisonResult {

        private final boolean dimensionsMatch;
        private final boolean majorDifferences;
        private final double differencePercentage;
        private final int baselineWidth;
        private final int baselineHeight;
        private final int currentWidth;
        private final int currentHeight;

        ComparisonResult(boolean dimensionsMatch, boolean majorDifferences,
                         double differencePercentage,
                         int baselineWidth, int baselineHeight,
                         int currentWidth, int currentHeight) {
            this.dimensionsMatch = dimensionsMatch;
            this.majorDifferences = majorDifferences;
            this.differencePercentage = differencePercentage;
            this.baselineWidth = baselineWidth;
            this.baselineHeight = baselineHeight;
            this.currentWidth = currentWidth;
            this.currentHeight = currentHeight;
        }

        /**
         * Returns {@code true} if both images have the same width and height.
         */
        public boolean dimensionsMatch() {
            return dimensionsMatch;
        }

        /**
         * Returns {@code true} if more than 10% of pixels differ significantly.
         */
        public boolean hasMajorDifferences() {
            return majorDifferences;
        }

        /**
         * Returns the percentage of pixels that differ (0.0 to 100.0).
         */
        public double getDifferencePercentage() {
            return differencePercentage;
        }

        /**
         * Generates a human-readable text report of the comparison.
         *
         * @param stateLabel a label identifying the state or context of the comparison
         * @return a formatted multi-line report string
         */
        public String toReport(String stateLabel) {
            String dimStatus = dimensionsMatch ? "MATCH" : "MISMATCH";
            String majorStatus = majorDifferences ? "YES" : "NO";

            return String.format(
                    "Visual Comparison Report: %s%n" +
                    "  Dimensions: %dx%d vs %dx%d - %s%n" +
                    "  Pixel Difference: %.1f%%%n" +
                    "  Major Differences: %s",
                    stateLabel,
                    baselineWidth, baselineHeight, currentWidth, currentHeight, dimStatus,
                    differencePercentage,
                    majorStatus
            );
        }
    }
}

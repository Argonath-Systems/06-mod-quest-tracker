package com.argonathsystems.mods.questtrackerui.accessibility;

/**
 * Text scaling utility for accessibility.
 *
 * <p>Allows users to adjust text size for improved readability.
 * Supports scaling from 0.5x (50%) to 2.0x (200%) with 0.1 increments.
 *
 * @since 1.0.0
 */
public final class TextScaling {
    
    /** Minimum text scale factor. */
    public static final double MIN_SCALE = 0.5;
    
    /** Maximum text scale factor. */
    public static final double MAX_SCALE = 2.0;
    
    /** Default text scale factor. */
    public static final double DEFAULT_SCALE = 1.0;
    
    /** Scale increment for adjustments. */
    public static final double SCALE_INCREMENT = 0.1;
    
    private TextScaling() {
        // Utility class
    }
    
    /**
     * Apply text scale to a font size.
     *
     * @param baseFontSize Base font size in pixels
     * @param scale Scale factor (0.5 to 2.0)
     * @return Scaled font size, minimum 8 pixels
     */
    public static int applyScale(int baseFontSize, double scale) {
        double clampedScale = clampScale(scale);
        int scaled = (int) Math.round(baseFontSize * clampedScale);
        return Math.max(8, scaled); // Minimum readable size
    }
    
    /**
     * Apply text scale to line spacing.
     *
     * @param baseSpacing Base line spacing in pixels
     * @param scale Scale factor
     * @return Scaled line spacing
     */
    public static int applySpacingScale(int baseSpacing, double scale) {
        double clampedScale = clampScale(scale);
        return Math.max(2, (int) Math.round(baseSpacing * clampedScale));
    }
    
    /**
     * Apply text scale to UI element dimensions.
     *
     * <p>Elements that contain text should scale proportionally.
     *
     * @param baseDimension Base dimension in pixels
     * @param scale Scale factor
     * @return Scaled dimension
     */
    public static int applyDimensionScale(int baseDimension, double scale) {
        double clampedScale = clampScale(scale);
        // Use square root scaling for dimensions to prevent UI from becoming too large
        double dimensionScale = 1.0 + (clampedScale - 1.0) * 0.5;
        return Math.max(1, (int) Math.round(baseDimension * dimensionScale));
    }
    
    /**
     * Clamp scale factor to valid range.
     *
     * @param scale Scale factor
     * @return Clamped scale factor
     */
    public static double clampScale(double scale) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }
    
    /**
     * Round scale to nearest increment.
     *
     * @param scale Scale factor
     * @return Rounded scale factor
     */
    public static double roundToIncrement(double scale) {
        double clamped = clampScale(scale);
        return Math.round(clamped / SCALE_INCREMENT) * SCALE_INCREMENT;
    }
    
    /**
     * Increase scale by one increment.
     *
     * @param currentScale Current scale factor
     * @return Increased scale factor
     */
    public static double increaseScale(double currentScale) {
        return clampScale(currentScale + SCALE_INCREMENT);
    }
    
    /**
     * Decrease scale by one increment.
     *
     * @param currentScale Current scale factor
     * @return Decreased scale factor
     */
    public static double decreaseScale(double currentScale) {
        return clampScale(currentScale - SCALE_INCREMENT);
    }
    
    /**
     * Format scale factor as percentage string.
     *
     * @param scale Scale factor
     * @return Formatted percentage (e.g., "100%")
     */
    public static String formatAsPercentage(double scale) {
        return String.format("%.0f%%", scale * 100);
    }
    
    /**
     * Check if scale is at minimum.
     *
     * @param scale Scale factor
     * @return true if at minimum
     */
    public static boolean isAtMinimum(double scale) {
        return scale <= MIN_SCALE;
    }
    
    /**
     * Check if scale is at maximum.
     *
     * @param scale Scale factor
     * @return true if at maximum
     */
    public static boolean isAtMaximum(double scale) {
        return scale >= MAX_SCALE;
    }
}

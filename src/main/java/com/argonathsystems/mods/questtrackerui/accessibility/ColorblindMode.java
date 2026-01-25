package com.argonathsystems.mods.questtrackerui.accessibility;

/**
 * Colorblind mode types for color adjustment.
 *
 * <p>Each mode adjusts colors to improve distinguishability for users
 * with different types of color vision deficiency.
 *
 * @since 1.0.0
 */
public enum ColorblindMode {
    /**
     * No color adjustment - default mode.
     */
    NONE("None", "No color adjustments"),
    
    /**
     * Deuteranopia - red-green color blindness (most common).
     * Difficulty distinguishing green from red.
     */
    DEUTERANOPIA("Deuteranopia", "Red-green (green-weak)"),
    
    /**
     * Protanopia - red-green color blindness.
     * Difficulty distinguishing red from green.
     */
    PROTANOPIA("Protanopia", "Red-green (red-weak)"),
    
    /**
     * Tritanopia - blue-yellow color blindness (rare).
     * Difficulty distinguishing blue from yellow.
     */
    TRITANOPIA("Tritanopia", "Blue-yellow");
    
    private final String displayName;
    private final String description;
    
    ColorblindMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Get the display name for UI.
     *
     * @return Display name
     */
    public String displayName() {
        return displayName;
    }
    
    /**
     * Get the description.
     *
     * @return Description
     */
    public String description() {
        return description;
    }
    
    /**
     * Transform a color for this colorblind mode.
     *
     * <p>Uses Daltonization algorithm to shift colors into
     * distinguishable ranges for each type of color blindness.
     *
     * @param argb ARGB color value
     * @return Transformed ARGB color
     */
    public int transformColor(int argb) {
        if (this == NONE) {
            return argb;
        }
        
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        
        // Convert to linear RGB
        double lr = toLinear(r / 255.0);
        double lg = toLinear(g / 255.0);
        double lb = toLinear(b / 255.0);
        
        // Apply colorblind simulation matrix then correction
        double[] result = applyTransform(lr, lg, lb);
        
        // Convert back to sRGB
        int newR = (int) Math.round(fromLinear(result[0]) * 255);
        int newG = (int) Math.round(fromLinear(result[1]) * 255);
        int newB = (int) Math.round(fromLinear(result[2]) * 255);
        
        // Clamp values
        newR = Math.max(0, Math.min(255, newR));
        newG = Math.max(0, Math.min(255, newG));
        newB = Math.max(0, Math.min(255, newB));
        
        return (a << 24) | (newR << 16) | (newG << 8) | newB;
    }
    
    private double[] applyTransform(double r, double g, double b) {
        // Daltonization matrices to shift confusing colors
        return switch (this) {
            case DEUTERANOPIA -> {
                // Shift greens toward blues/yellows
                double newR = 0.625 * r + 0.375 * g;
                double newG = 0.7 * g + 0.3 * b;
                double newB = 0.3 * g + 0.7 * b;
                yield new double[] { newR, newG, newB };
            }
            case PROTANOPIA -> {
                // Shift reds toward blues/yellows
                double newR = 0.567 * r + 0.433 * g;
                double newG = 0.558 * r + 0.442 * g;
                double newB = 0.242 * g + 0.758 * b;
                yield new double[] { newR, newG, newB };
            }
            case TRITANOPIA -> {
                // Shift blues toward reds/greens
                double newR = 0.95 * r + 0.05 * g;
                double newG = 0.433 * g + 0.567 * b;
                double newB = 0.475 * g + 0.525 * b;
                yield new double[] { newR, newG, newB };
            }
            default -> new double[] { r, g, b };
        };
    }
    
    private static double toLinear(double srgb) {
        if (srgb <= 0.04045) {
            return srgb / 12.92;
        }
        return Math.pow((srgb + 0.055) / 1.055, 2.4);
    }
    
    private static double fromLinear(double linear) {
        if (linear <= 0.0031308) {
            return linear * 12.92;
        }
        return 1.055 * Math.pow(linear, 1.0 / 2.4) - 0.055;
    }
    
    /**
     * Parse colorblind mode from string.
     *
     * @param value String value (case-insensitive)
     * @return ColorblindMode, defaults to NONE
     */
    public static ColorblindMode fromString(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}

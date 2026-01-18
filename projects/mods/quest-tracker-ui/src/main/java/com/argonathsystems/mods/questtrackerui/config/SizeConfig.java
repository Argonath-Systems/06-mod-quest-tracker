package com.argonathsystems.mods.questtrackerui.config;

/**
 * Size configuration for the tracker.
 *
 * @param width Width in pixels
 * @param maxHeight Maximum height in pixels
 * @param scale UI scale multiplier
 */
public record SizeConfig(
    int width,
    int maxHeight,
    double scale
) {
    
    /**
     * Default size configuration.
     */
    public static final SizeConfig DEFAULT = new SizeConfig(280, 400, 1.0);
    
    /**
     * Get scaled width.
     *
     * @return Width multiplied by scale
     */
    public int scaledWidth() {
        return (int) (width * scale);
    }
    
    /**
     * Get scaled max height.
     *
     * @return Max height multiplied by scale
     */
    public int scaledMaxHeight() {
        return (int) (maxHeight * scale);
    }
}

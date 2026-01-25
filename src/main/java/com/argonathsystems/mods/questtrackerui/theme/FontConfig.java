package com.argonathsystems.mods.questtrackerui.theme;

/**
 * Font configuration for a theme.
 *
 * @param titleFont Font identifier for titles
 * @param bodyFont Font identifier for body text
 * @param titleSize Title font size in pixels
 * @param bodySize Body font size in pixels
 * @param smallSize Small text font size in pixels
 */
public record FontConfig(
    String titleFont,
    String bodyFont,
    int titleSize,
    int bodySize,
    int smallSize
) {
    
    /**
     * Default Minecraft-style font configuration.
     */
    public static final FontConfig DEFAULT = new FontConfig(
        "minecraft", "minecraft", 14, 12, 10
    );
    
    /**
     * Create a configuration with custom sizes but default fonts.
     *
     * @param titleSize Title size
     * @param bodySize Body size
     * @param smallSize Small size
     * @return FontConfig with default fonts
     */
    public static FontConfig withSizes(int titleSize, int bodySize, int smallSize) {
        return new FontConfig("minecraft", "minecraft", titleSize, bodySize, smallSize);
    }
    
    /**
     * Scale all font sizes by a multiplier.
     *
     * @param scale Scale multiplier (1.0 = no change)
     * @return Scaled FontConfig
     */
    public FontConfig scaled(double scale) {
        return new FontConfig(
            titleFont, bodyFont,
            (int) Math.round(titleSize * scale),
            (int) Math.round(bodySize * scale),
            (int) Math.round(smallSize * scale)
        );
    }
}

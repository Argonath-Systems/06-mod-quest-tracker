package com.argonathsystems.mods.questtrackerui.theme;

/**
 * Border style configuration for a theme.
 *
 * @param style Border style type
 * @param radius Corner radius in pixels (for rounded style)
 * @param width Border width in pixels
 */
public record BorderConfig(
    BorderStyle style,
    int radius,
    int width
) {
    
    /**
     * No border configuration.
     */
    public static final BorderConfig NONE = new BorderConfig(BorderStyle.NONE, 0, 0);
    
    /**
     * Default solid border configuration.
     */
    public static final BorderConfig SOLID = new BorderConfig(BorderStyle.SOLID, 0, 1);
    
    /**
     * Default rounded border configuration.
     */
    public static final BorderConfig ROUNDED = new BorderConfig(BorderStyle.ROUNDED, 4, 1);
    
    /**
     * Create a rounded border with custom radius.
     *
     * @param radius Corner radius
     * @return BorderConfig with rounded style
     */
    public static BorderConfig rounded(int radius) {
        return new BorderConfig(BorderStyle.ROUNDED, radius, 1);
    }
    
    /**
     * Create a solid border with custom width.
     *
     * @param width Border width
     * @return BorderConfig with solid style
     */
    public static BorderConfig solid(int width) {
        return new BorderConfig(BorderStyle.SOLID, 0, width);
    }
    
    /**
     * Border style types.
     */
    public enum BorderStyle {
        /**
         * No border.
         */
        NONE,
        
        /**
         * Solid line border.
         */
        SOLID,
        
        /**
         * Rounded corner border.
         */
        ROUNDED,
        
        /**
         * Fancy/decorative border.
         */
        FANCY
    }
}

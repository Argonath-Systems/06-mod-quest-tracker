package com.argonathsystems.mods.questtrackerui.hud;

/**
 * Screen anchor position for UI elements.
 */
public enum AnchorPosition {
    
    /**
     * Top-left corner of the screen.
     */
    TOP_LEFT(0, 0),
    
    /**
     * Top-center of the screen.
     */
    TOP_CENTER(0.5, 0),
    
    /**
     * Top-right corner of the screen.
     */
    TOP_RIGHT(1.0, 0),
    
    /**
     * Middle-left of the screen.
     */
    MIDDLE_LEFT(0, 0.5),
    
    /**
     * Center of the screen.
     */
    CENTER(0.5, 0.5),
    
    /**
     * Middle-right of the screen.
     */
    MIDDLE_RIGHT(1.0, 0.5),
    
    /**
     * Bottom-left corner of the screen.
     */
    BOTTOM_LEFT(0, 1.0),
    
    /**
     * Bottom-center of the screen.
     */
    BOTTOM_CENTER(0.5, 1.0),
    
    /**
     * Bottom-right corner of the screen.
     */
    BOTTOM_RIGHT(1.0, 1.0);
    
    private final double xFactor;
    private final double yFactor;
    
    AnchorPosition(double xFactor, double yFactor) {
        this.xFactor = xFactor;
        this.yFactor = yFactor;
    }
    
    /**
     * Calculate the X position for an element with the given width.
     *
     * @param screenWidth Screen width
     * @param elementWidth Element width
     * @param offsetX Additional X offset
     * @return Calculated X position
     */
    public int calculateX(int screenWidth, int elementWidth, int offsetX) {
        int baseX = (int) (screenWidth * xFactor);
        int adjustment = (int) (elementWidth * xFactor);
        return baseX - adjustment + offsetX;
    }
    
    /**
     * Calculate the Y position for an element with the given height.
     *
     * @param screenHeight Screen height
     * @param elementHeight Element height
     * @param offsetY Additional Y offset
     * @return Calculated Y position
     */
    public int calculateY(int screenHeight, int elementHeight, int offsetY) {
        int baseY = (int) (screenHeight * yFactor);
        int adjustment = (int) (elementHeight * yFactor);
        return baseY - adjustment + offsetY;
    }
    
    /**
     * Parse an anchor position from a string.
     *
     * @param value String value (e.g., "TOP_RIGHT", "top-right")
     * @return Parsed anchor position, or TOP_RIGHT if invalid
     */
    public static AnchorPosition parse(String value) {
        if (value == null) {
            return TOP_RIGHT;
        }
        
        String normalized = value.toUpperCase().replace("-", "_").replace(" ", "_");
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return TOP_RIGHT;
        }
    }
}

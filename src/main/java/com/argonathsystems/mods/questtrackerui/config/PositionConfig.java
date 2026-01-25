package com.argonathsystems.mods.questtrackerui.config;

import com.argonathsystems.mods.questtrackerui.hud.AnchorPosition;

/**
 * Position configuration for the tracker.
 *
 * @param anchor Screen anchor position
 * @param offsetX Horizontal offset from anchor
 * @param offsetY Vertical offset from anchor
 */
public record PositionConfig(
    AnchorPosition anchor,
    int offsetX,
    int offsetY
) {
    
    /**
     * Default position configuration (top-right corner).
     */
    public static final PositionConfig DEFAULT = new PositionConfig(
        AnchorPosition.TOP_RIGHT, -20, 50
    );
    
    /**
     * Create a position config from anchor name.
     *
     * @param anchorName Anchor name (e.g., "TOP_RIGHT")
     * @param offsetX X offset
     * @param offsetY Y offset
     * @return PositionConfig
     */
    public static PositionConfig of(String anchorName, int offsetX, int offsetY) {
        return new PositionConfig(AnchorPosition.parse(anchorName), offsetX, offsetY);
    }
}

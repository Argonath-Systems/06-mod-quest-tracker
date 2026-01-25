package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.hud.RenderContext;

/**
 * Extended render context for full-screen UI rendering.
 * 
 * <p>Provides additional capabilities for screen-based rendering
 * including mouse interaction, scrolling, and button rendering.
 */
public interface ScreenContext extends RenderContext {
    
    /**
     * Get current mouse X position.
     *
     * @return Mouse X in screen coordinates
     */
    int mouseX();
    
    /**
     * Get current mouse Y position.
     *
     * @return Mouse Y in screen coordinates
     */
    int mouseY();
    
    /**
     * Check if a point is within a rectangle.
     *
     * @param x Rectangle left edge
     * @param y Rectangle top edge
     * @param width Rectangle width
     * @param height Rectangle height
     * @return true if mouse is within the rectangle
     */
    default boolean isMouseOver(int x, int y, int width, int height) {
        int mx = mouseX();
        int my = mouseY();
        return mx >= x && mx < x + width && my >= y && my < y + height;
    }
    
    /**
     * Draw a button.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Button width
     * @param height Button height
     * @param text Button text
     * @param enabled Whether the button is enabled
     * @param hovered Whether the button is hovered
     */
    void drawButton(int x, int y, int width, int height, String text, boolean enabled, boolean hovered);
    
    /**
     * Draw a checkbox.
     *
     * @param x Left edge
     * @param y Top edge
     * @param checked Whether the checkbox is checked
     * @param enabled Whether the checkbox is enabled
     * @param hovered Whether the checkbox is hovered
     */
    void drawCheckbox(int x, int y, boolean checked, boolean enabled, boolean hovered);
    
    /**
     * Draw a scrollbar.
     *
     * @param x Left edge
     * @param y Top edge
     * @param height Scrollbar height
     * @param scrollPercent Current scroll position (0.0 to 1.0)
     * @param visiblePercent Visible portion percentage (0.0 to 1.0)
     */
    void drawScrollbar(int x, int y, int height, double scrollPercent, double visiblePercent);
    
    /**
     * Start a scissor/clipping region.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Region width
     * @param height Region height
     */
    void pushScissor(int x, int y, int width, int height);
    
    /**
     * End the current scissor/clipping region.
     */
    void popScissor();
    
    /**
     * Play a UI click sound.
     */
    void playClickSound();
    
    /**
     * Close the current screen.
     */
    void closeScreen();
    
    /**
     * Set the system clipboard contents.
     *
     * @param text Text to copy to clipboard
     */
    void setClipboard(String text);
}

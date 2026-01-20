package com.argonathsystems.mods.questtrackerui.hud;

/**
 * Render context provided during HUD rendering.
 * 
 * <p>Abstracts the platform-specific rendering API so HUD components
 * can render without direct platform dependencies.
 */
public interface RenderContext {
    
    /**
     * Get the current screen width.
     *
     * @return Screen width in pixels
     */
    int screenWidth();
    
    /**
     * Get the current screen height.
     *
     * @return Screen height in pixels
     */
    int screenHeight();
    
    /**
     * Get the partial tick time for smooth animations.
     *
     * @return Partial tick (0.0 to 1.0)
     */
    float partialTicks();
    
    /**
     * Get the current UI scale factor.
     *
     * @return Scale factor
     */
    double scaleFactor();
    
    /**
     * Draw a filled rectangle.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Width
     * @param height Height
     * @param color ARGB color
     */
    void fillRect(int x, int y, int width, int height, int color);

    /**
     * Draw styled text component.
     * 
     * @param text The component to draw
     * @param x X Position
     * @param y Y Position
     */
    void drawText(com.argonathsystems.framework.text.Component text, int x, int y);
    
    /**
     * Draw a rectangle outline.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Width
     * @param height Height
     * @param color ARGB color
     * @param thickness Line thickness
     */
    void drawRect(int x, int y, int width, int height, int color, int thickness);
    
    /**
     * Draw a rounded rectangle.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Width
     * @param height Height
     * @param radius Corner radius
     * @param color ARGB color
     */
    void fillRoundedRect(int x, int y, int width, int height, int radius, int color);
    
    /**
     * Draw text.
     *
     * @param text Text to draw
     * @param x X position
     * @param y Y position
     * @param color ARGB color
     */
    default void drawText(String text, int x, int y, int color) {
        drawText(com.argonathsystems.framework.text.Component.text(text, com.argonathsystems.framework.text.Style.of(com.argonathsystems.framework.text.TextColor.of(color))), x, y);
    }
    
    /**
     * Draw text with shadow.
     *
     * @param text Text to draw
     * @param x X position
     * @param y Y position
     * @param color ARGB color
     */
    default void drawTextWithShadow(String text, int x, int y, int color) {
         // Shadow is momentarily ignored in the component migration
        drawText(com.argonathsystems.framework.text.Component.text(text, com.argonathsystems.framework.text.Style.of(com.argonathsystems.framework.text.TextColor.of(color))), x, y);
    }
    
    /**
     * Get the width of text when rendered.
     *
     * @param text Text to measure
     * @return Width in pixels
     */
    int textWidth(String text);
    
    /**
     * Get the height of text when rendered.
     *
     * @return Height in pixels
     */
    int textHeight();
    
    /**
     * Draw a texture/icon.
     *
     * @param texturePath Path to the texture
     * @param x X position
     * @param y Y position
     * @param width Render width
     * @param height Render height
     */
    void drawTexture(String texturePath, int x, int y, int width, int height);
    
    /**
     * Draw an icon with a tint color.
     *
     * @param iconPath Path to the icon texture
     * @param x X position
     * @param y Y position
     * @param width Render width
     * @param height Render height
     * @param color Tint color (ARGB)
     */
    default void drawIcon(String iconPath, int x, int y, int width, int height, int color) {
        drawTexture(iconPath, x, y, width, height);
    }
    
    /**
     * Draw a texture region.
     *
     * @param texturePath Path to the texture
     * @param x X position
     * @param y Y position
     * @param width Render width
     * @param height Render height
     * @param u Texture U coordinate
     * @param v Texture V coordinate
     * @param uWidth Texture region width
     * @param vHeight Texture region height
     * @param textureWidth Full texture width
     * @param textureHeight Full texture height
     */
    void drawTextureRegion(String texturePath, int x, int y, int width, int height,
                           int u, int v, int uWidth, int vHeight,
                           int textureWidth, int textureHeight);
    
    /**
     * Draw a horizontal gradient.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Width
     * @param height Height
     * @param colorLeft Left color (ARGB)
     * @param colorRight Right color (ARGB)
     */
    void fillGradientHorizontal(int x, int y, int width, int height, int colorLeft, int colorRight);
    
    /**
     * Push a scissor/clip region.
     *
     * @param x Left edge
     * @param y Top edge
     * @param width Width
     * @param height Height
     */
    void pushScissor(int x, int y, int width, int height);
    
    /**
     * Pop the current scissor/clip region.
     */
    void popScissor();
    
    /**
     * Push a translation transform.
     *
     * @param x X offset
     * @param y Y offset
     */
    void pushTranslate(int x, int y);
    
    /**
     * Pop the current translation.
     */
    void popTranslate();
}

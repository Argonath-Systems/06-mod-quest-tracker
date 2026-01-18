package com.argonathsystems.mods.questtrackerui.accessibility;

import java.util.Objects;

/**
 * Centralized accessibility settings configuration.
 *
 * <p>Aggregates all accessibility options and provides methods for
 * applying transformations to colors and sizes.
 *
 * <p>Example usage:
 * <pre>{@code
 * AccessibilitySettings settings = AccessibilitySettings.builder()
 *     .highContrastMode(true)
 *     .colorblindMode(ColorblindMode.DEUTERANOPIA)
 *     .textScale(1.2)
 *     .build();
 *     
 * int adjustedColor = settings.adjustColor(originalColor);
 * int scaledSize = settings.scaleFontSize(12);
 * }</pre>
 *
 * @since 1.0.0
 */
public final class AccessibilitySettings {
    
    private final boolean highContrastMode;
    private final ColorblindMode colorblindMode;
    private final boolean screenReaderSupport;
    private final double textScale;
    
    private AccessibilitySettings(Builder builder) {
        this.highContrastMode = builder.highContrastMode;
        this.colorblindMode = Objects.requireNonNullElse(builder.colorblindMode, ColorblindMode.NONE);
        this.screenReaderSupport = builder.screenReaderSupport;
        this.textScale = TextScaling.clampScale(builder.textScale);
    }
    
    /**
     * Check if high contrast mode is enabled.
     *
     * @return true if enabled
     */
    public boolean isHighContrastMode() {
        return highContrastMode;
    }
    
    /**
     * Get the colorblind mode.
     *
     * @return Colorblind mode, never null
     */
    public ColorblindMode colorblindMode() {
        return colorblindMode;
    }
    
    /**
     * Check if screen reader support is enabled.
     *
     * @return true if enabled
     */
    public boolean isScreenReaderSupport() {
        return screenReaderSupport;
    }
    
    /**
     * Get the text scale factor.
     *
     * @return Text scale (0.5 to 2.0)
     */
    public double textScale() {
        return textScale;
    }
    
    /**
     * Check if any accessibility features are enabled.
     *
     * @return true if any feature is active
     */
    public boolean hasActiveFeatures() {
        return highContrastMode 
            || colorblindMode != ColorblindMode.NONE 
            || screenReaderSupport 
            || textScale != TextScaling.DEFAULT_SCALE;
    }
    
    /**
     * Apply all color adjustments (colorblind + high contrast).
     *
     * @param argb Original ARGB color
     * @return Adjusted color
     */
    public int adjustColor(int argb) {
        int adjusted = colorblindMode.transformColor(argb);
        return HighContrastMode.applyHighContrast(adjusted, highContrastMode);
    }
    
    /**
     * Adjust a foreground color to ensure contrast against background.
     *
     * @param foreground Foreground ARGB color
     * @param background Background ARGB color
     * @return Adjusted foreground color
     */
    public int adjustColorWithContrast(int foreground, int background) {
        int adjusted = adjustColor(foreground);
        int adjustedBg = adjustColor(background);
        
        if (highContrastMode) {
            return HighContrastMode.ensureContrast(adjusted, adjustedBg, 
                HighContrastMode.WCAG_AAA_NORMAL);
        }
        return adjusted;
    }
    
    /**
     * Scale a font size.
     *
     * @param baseFontSize Base font size in pixels
     * @return Scaled font size
     */
    public int scaleFontSize(int baseFontSize) {
        return TextScaling.applyScale(baseFontSize, textScale);
    }
    
    /**
     * Scale line spacing.
     *
     * @param baseSpacing Base spacing in pixels
     * @return Scaled spacing
     */
    public int scaleSpacing(int baseSpacing) {
        return TextScaling.applySpacingScale(baseSpacing, textScale);
    }
    
    /**
     * Scale a UI dimension.
     *
     * @param baseDimension Base dimension in pixels
     * @return Scaled dimension
     */
    public int scaleDimension(int baseDimension) {
        return TextScaling.applyDimensionScale(baseDimension, textScale);
    }
    
    /**
     * Create a copy with high contrast mode toggled.
     *
     * @return New settings with toggled high contrast
     */
    public AccessibilitySettings withHighContrastToggled() {
        return toBuilder()
            .highContrastMode(!highContrastMode)
            .build();
    }
    
    /**
     * Create a copy with a different colorblind mode.
     *
     * @param mode New colorblind mode
     * @return New settings
     */
    public AccessibilitySettings withColorblindMode(ColorblindMode mode) {
        return toBuilder()
            .colorblindMode(mode)
            .build();
    }
    
    /**
     * Create a copy with increased text scale.
     *
     * @return New settings with increased scale
     */
    public AccessibilitySettings withIncreasedTextScale() {
        return toBuilder()
            .textScale(TextScaling.increaseScale(textScale))
            .build();
    }
    
    /**
     * Create a copy with decreased text scale.
     *
     * @return New settings with decreased scale
     */
    public AccessibilitySettings withDecreasedTextScale() {
        return toBuilder()
            .textScale(TextScaling.decreaseScale(textScale))
            .build();
    }
    
    /**
     * Create default settings (no accessibility features).
     *
     * @return Default settings
     */
    public static AccessibilitySettings defaults() {
        return builder().build();
    }
    
    /**
     * Create a new builder.
     *
     * @return Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Create a builder pre-populated with this settings' values.
     *
     * @return Builder instance
     */
    public Builder toBuilder() {
        return new Builder()
            .highContrastMode(highContrastMode)
            .colorblindMode(colorblindMode)
            .screenReaderSupport(screenReaderSupport)
            .textScale(textScale);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AccessibilitySettings other)) return false;
        return highContrastMode == other.highContrastMode
            && colorblindMode == other.colorblindMode
            && screenReaderSupport == other.screenReaderSupport
            && Double.compare(textScale, other.textScale) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(highContrastMode, colorblindMode, screenReaderSupport, textScale);
    }
    
    @Override
    public String toString() {
        return String.format(
            "AccessibilitySettings[highContrast=%s, colorblind=%s, screenReader=%s, textScale=%.1f]",
            highContrastMode, colorblindMode, screenReaderSupport, textScale);
    }
    
    /**
     * Builder for AccessibilitySettings.
     */
    public static final class Builder {
        private boolean highContrastMode = false;
        private ColorblindMode colorblindMode = ColorblindMode.NONE;
        private boolean screenReaderSupport = false;
        private double textScale = TextScaling.DEFAULT_SCALE;
        
        private Builder() {}
        
        /**
         * Set high contrast mode.
         *
         * @param enabled true to enable
         * @return this builder
         */
        public Builder highContrastMode(boolean enabled) {
            this.highContrastMode = enabled;
            return this;
        }
        
        /**
         * Set colorblind mode.
         *
         * @param mode Colorblind mode
         * @return this builder
         */
        public Builder colorblindMode(ColorblindMode mode) {
            this.colorblindMode = mode;
            return this;
        }
        
        /**
         * Set screen reader support.
         *
         * @param enabled true to enable
         * @return this builder
         */
        public Builder screenReaderSupport(boolean enabled) {
            this.screenReaderSupport = enabled;
            return this;
        }
        
        /**
         * Set text scale factor.
         *
         * @param scale Scale factor (0.5 to 2.0)
         * @return this builder
         */
        public Builder textScale(double scale) {
            this.textScale = scale;
            return this;
        }
        
        /**
         * Build the settings.
         *
         * @return AccessibilitySettings instance
         */
        public AccessibilitySettings build() {
            return new AccessibilitySettings(this);
        }
    }
}

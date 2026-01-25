package com.argonathsystems.mods.questtrackerui.accessibility;

/**
 * High contrast mode for improved visibility.
 *
 * <p>Increases color contrast ratios to meet WCAG accessibility standards
 * and improves readability for users with low vision.
 *
 * @since 1.0.0
 */
public final class HighContrastMode {
    
    /** Minimum contrast ratio for normal text (WCAG AA). */
    public static final double WCAG_AA_NORMAL = 4.5;
    
    /** Minimum contrast ratio for large text (WCAG AA). */
    public static final double WCAG_AA_LARGE = 3.0;
    
    /** Minimum contrast ratio for normal text (WCAG AAA). */
    public static final double WCAG_AAA_NORMAL = 7.0;
    
    /** Contrast boost factor for high contrast mode. */
    private static final double CONTRAST_BOOST = 1.5;
    
    private HighContrastMode() {
        // Utility class
    }
    
    /**
     * Apply high contrast transformation to a color.
     *
     * <p>Increases the contrast by pushing colors toward black or white
     * based on their luminance.
     *
     * @param argb Original ARGB color
     * @param enabled Whether high contrast is enabled
     * @return Transformed color if enabled, original otherwise
     */
    public static int applyHighContrast(int argb, boolean enabled) {
        if (!enabled) {
            return argb;
        }
        
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        
        // Calculate relative luminance
        double luminance = calculateLuminance(r, g, b);
        
        // Push colors toward black or white based on luminance
        double factor;
        if (luminance > 0.5) {
            // Light color - make lighter
            factor = 1.0 + (1.0 - luminance) * (CONTRAST_BOOST - 1.0);
        } else {
            // Dark color - make darker  
            factor = 1.0 - luminance * (CONTRAST_BOOST - 1.0);
        }
        
        // Also increase saturation for better distinction
        int[] hsb = rgbToHsb(r, g, b);
        hsb[1] = Math.min(100, (int) (hsb[1] * 1.3)); // Boost saturation
        int[] rgb = hsbToRgb(hsb[0], hsb[1], hsb[2]);
        
        // Apply luminance boost
        int newR = clamp((int) (rgb[0] * factor));
        int newG = clamp((int) (rgb[1] * factor));
        int newB = clamp((int) (rgb[2] * factor));
        
        return (a << 24) | (newR << 16) | (newG << 8) | newB;
    }
    
    /**
     * Ensure foreground color has sufficient contrast against background.
     *
     * <p>If contrast is insufficient, the foreground color is adjusted
     * to meet the minimum contrast ratio.
     *
     * @param foreground Foreground ARGB color
     * @param background Background ARGB color
     * @param minContrastRatio Minimum required contrast ratio
     * @return Adjusted foreground color with sufficient contrast
     */
    public static int ensureContrast(int foreground, int background, double minContrastRatio) {
        double currentRatio = calculateContrastRatio(foreground, background);
        
        if (currentRatio >= minContrastRatio) {
            return foreground;
        }
        
        // Determine direction to adjust
        double bgLuminance = getLuminance(background);
        boolean makeLighter = bgLuminance < 0.5;
        
        // Binary search for correct luminance
        int a = (foreground >> 24) & 0xFF;
        int r = (foreground >> 16) & 0xFF;
        int g = (foreground >> 8) & 0xFF;
        int b = foreground & 0xFF;
        
        for (int i = 0; i < 10; i++) { // Max 10 iterations
            if (makeLighter) {
                r = Math.min(255, r + 20);
                g = Math.min(255, g + 20);
                b = Math.min(255, b + 20);
            } else {
                r = Math.max(0, r - 20);
                g = Math.max(0, g - 20);
                b = Math.max(0, b - 20);
            }
            
            int adjusted = (a << 24) | (r << 16) | (g << 8) | b;
            if (calculateContrastRatio(adjusted, background) >= minContrastRatio) {
                return adjusted;
            }
        }
        
        // Fallback to black or white
        return makeLighter ? (a << 24) | 0xFFFFFF : (a << 24);
    }
    
    /**
     * Calculate contrast ratio between two colors.
     *
     * @param color1 First ARGB color
     * @param color2 Second ARGB color
     * @return Contrast ratio (1.0 to 21.0)
     */
    public static double calculateContrastRatio(int color1, int color2) {
        double l1 = getLuminance(color1);
        double l2 = getLuminance(color2);
        
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        
        return (lighter + 0.05) / (darker + 0.05);
    }
    
    /**
     * Get the relative luminance of a color.
     *
     * @param argb ARGB color
     * @return Relative luminance (0.0 to 1.0)
     */
    public static double getLuminance(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return calculateLuminance(r, g, b);
    }
    
    private static double calculateLuminance(int r, int g, int b) {
        double rLinear = toLinear(r / 255.0);
        double gLinear = toLinear(g / 255.0);
        double bLinear = toLinear(b / 255.0);
        
        // Standard luminance formula
        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear;
    }
    
    private static double toLinear(double srgb) {
        if (srgb <= 0.04045) {
            return srgb / 12.92;
        }
        return Math.pow((srgb + 0.055) / 1.055, 2.4);
    }
    
    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
    
    private static int[] rgbToHsb(int r, int g, int b) {
        float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
        return new int[] { 
            (int) (hsb[0] * 360), 
            (int) (hsb[1] * 100), 
            (int) (hsb[2] * 100) 
        };
    }
    
    private static int[] hsbToRgb(int h, int s, int brightness) {
        int rgb = java.awt.Color.HSBtoRGB(h / 360f, s / 100f, brightness / 100f);
        return new int[] { 
            (rgb >> 16) & 0xFF, 
            (rgb >> 8) & 0xFF, 
            rgb & 0xFF 
        };
    }
}

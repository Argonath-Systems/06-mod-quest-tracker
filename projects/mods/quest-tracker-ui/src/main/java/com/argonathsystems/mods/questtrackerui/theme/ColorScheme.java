package com.argonathsystems.mods.questtrackerui.theme;

/**
 * Color scheme configuration for a theme.
 *
 * @param background Background color (hex)
 * @param backgroundOpacity Background opacity (0.0 to 1.0)
 * @param border Border color (hex)
 * @param textPrimary Primary text color (hex)
 * @param textSecondary Secondary text color (hex)
 * @param textCompleted Completed text color (hex)
 * @param questMain Main quest color (hex)
 * @param questSide Side quest color (hex)
 * @param questTimed Timed quest color (hex)
 * @param questGuild Guild quest color (hex)
 * @param questDaily Daily quest color (hex)
 * @param questWeekly Weekly quest color (hex)
 * @param progressFill Progress bar fill color (hex)
 * @param progressBackground Progress bar background color (hex)
 * @param progressComplete Progress bar complete color (hex)
 * @param timerNormal Timer normal color (hex)
 * @param timerWarning Timer warning color (< 5 min, hex)
 * @param timerCritical Timer critical color (< 1 min, hex)
 */
public record ColorScheme(
    String background,
    double backgroundOpacity,
    String border,
    String textPrimary,
    String textSecondary,
    String textCompleted,
    String questMain,
    String questSide,
    String questTimed,
    String questGuild,
    String questDaily,
    String questWeekly,
    String progressFill,
    String progressBackground,
    String progressComplete,
    String timerNormal,
    String timerWarning,
    String timerCritical
) {
    
    /**
     * Default dark fantasy color scheme.
     */
    public static final ColorScheme DARK_FANTASY = new ColorScheme(
        "#1a1a2e", 0.85, "#4a4a6a",
        "#ffffff", "#aaaaaa", "#666666",
        "#ffd700", "#87ceeb", "#ff6b6b", "#90ee90", "#ffa500", "#9370db",
        "#4caf50", "#333333", "#8bc34a",
        "#ffffff", "#ffeb3b", "#f44336"
    );
    
    /**
     * Light minimal color scheme.
     */
    public static final ColorScheme LIGHT_MINIMAL = new ColorScheme(
        "#f5f5f5", 0.95, "#cccccc",
        "#333333", "#666666", "#999999",
        "#b8860b", "#4682b4", "#dc143c", "#228b22", "#ff8c00", "#6a5acd",
        "#4caf50", "#e0e0e0", "#8bc34a",
        "#333333", "#ff9800", "#f44336"
    );
    
    /**
     * Parse a hex color to ARGB integer.
     *
     * @param hex Hex color string (e.g., "#ff0000" or "ff0000")
     * @return ARGB integer
     */
    public static int parseHex(String hex) {
        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
        if (clean.length() == 6) {
            return 0xFF000000 | Integer.parseInt(clean, 16);
        } else if (clean.length() == 8) {
            return Integer.parseUnsignedInt(clean, 16);
        }
        return 0xFFFFFFFF;
    }
    
    /**
     * Get background color as ARGB with opacity applied.
     *
     * @return ARGB integer with opacity
     */
    public int backgroundArgb() {
        int rgb = parseHex(background) & 0x00FFFFFF;
        int alpha = (int) (backgroundOpacity * 255) << 24;
        return alpha | rgb;
    }
    
    /**
     * Get border color as ARGB.
     *
     * @return ARGB integer
     */
    public int borderArgb() {
        return parseHex(border);
    }
    
    /**
     * Get primary text color as ARGB.
     *
     * @return ARGB integer
     */
    public int textPrimaryArgb() {
        return parseHex(textPrimary);
    }
    
    /**
     * Get secondary text color as ARGB.
     *
     * @return ARGB integer
     */
    public int textSecondaryArgb() {
        return parseHex(textSecondary);
    }
    
    /**
     * Get completed text color as ARGB.
     *
     * @return ARGB integer
     */
    public int textCompletedArgb() {
        return parseHex(textCompleted);
    }
    
    /**
     * Get quest main color as ARGB.
     *
     * @return ARGB integer
     */
    public int questMainArgb() {
        return parseHex(questMain);
    }
    
    /**
     * Get quest side color as ARGB.
     *
     * @return ARGB integer
     */
    public int questSideArgb() {
        return parseHex(questSide);
    }
    
    /**
     * Get quest timed color as ARGB.
     *
     * @return ARGB integer
     */
    public int questTimedArgb() {
        return parseHex(questTimed);
    }
    
    /**
     * Get quest guild color as ARGB.
     *
     * @return ARGB integer
     */
    public int questGuildArgb() {
        return parseHex(questGuild);
    }
    
    /**
     * Get quest daily color as ARGB.
     *
     * @return ARGB integer
     */
    public int questDailyArgb() {
        return parseHex(questDaily);
    }
    
    /**
     * Get quest weekly color as ARGB.
     *
     * @return ARGB integer
     */
    public int questWeeklyArgb() {
        return parseHex(questWeekly);
    }
    
    /**
     * Get quest event color as ARGB (uses main quest color).
     *
     * @return ARGB integer
     */
    public int questEventArgb() {
        return parseHex(questMain); // Reuse main color for events
    }
    
    /**
     * Get progress fill color as ARGB.
     *
     * @return ARGB integer
     */
    public int progressFillArgb() {
        return parseHex(progressFill);
    }
    
    /**
     * Get progress background color as ARGB.
     *
     * @return ARGB integer
     */
    public int progressBackgroundArgb() {
        return parseHex(progressBackground);
    }
    
    /**
     * Get progress complete color as ARGB.
     *
     * @return ARGB integer
     */
    public int progressCompleteArgb() {
        return parseHex(progressComplete);
    }
    
    /**
     * Get timer normal color as ARGB.
     *
     * @return ARGB integer
     */
    public int timerNormalArgb() {
        return parseHex(timerNormal);
    }
    
    /**
     * Get timer warning color as ARGB.
     *
     * @return ARGB integer
     */
    public int timerWarningArgb() {
        return parseHex(timerWarning);
    }
    
    /**
     * Get timer critical color as ARGB.
     *
     * @return ARGB integer
     */
    public int timerCriticalArgb() {
        return parseHex(timerCritical);
    }
}

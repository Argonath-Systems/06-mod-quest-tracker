package com.argonathsystems.mods.questtrackerui.theme;

/**
 * Helper class providing quick access to theme colors as ARGB integers.
 * 
 * <p>Wraps a {@link ColorScheme} and provides integer-returning methods
 * for convenient use in rendering code.
 */
public final class ThemeColors {
    
    private final ColorScheme scheme;
    
    /**
     * Create a new theme colors helper.
     *
     * @param scheme The color scheme to wrap
     */
    public ThemeColors(ColorScheme scheme) {
        this.scheme = scheme;
    }
    
    /** Background with opacity applied */
    public int background() { return scheme.backgroundArgb(); }
    
    /** Border color */
    public int border() { return scheme.borderArgb(); }
    
    /** Primary text color */
    public int textPrimary() { return scheme.textPrimaryArgb(); }
    
    /** Secondary text color */
    public int textSecondary() { return scheme.textSecondaryArgb(); }
    
    /** Completed text color */
    public int textCompleted() { return scheme.textCompletedArgb(); }
    
    /** Main quest color */
    public int questMain() { return scheme.questMainArgb(); }
    
    /** Side quest color */
    public int questSide() { return scheme.questSideArgb(); }
    
    /** Timed quest color */
    public int questTimed() { return scheme.questTimedArgb(); }
    
    /** Guild quest color */
    public int questGuild() { return scheme.questGuildArgb(); }
    
    /** Daily quest color */
    public int questDaily() { return scheme.questDailyArgb(); }
    
    /** Weekly quest color */
    public int questWeekly() { return scheme.questWeeklyArgb(); }
    
    /** Event quest color */
    public int questEvent() { return scheme.questEventArgb(); }
    
    /** Progress bar fill color */
    public int progressFill() { return scheme.progressFillArgb(); }
    
    /** Progress bar background color */
    public int progressBackground() { return scheme.progressBackgroundArgb(); }
    
    /** Progress bar complete color */
    public int progressComplete() { return scheme.progressCompleteArgb(); }
    
    /** Timer normal color */
    public int timerNormal() { return scheme.timerNormalArgb(); }
    
    /** Timer warning color */
    public int timerWarning() { return scheme.timerWarningArgb(); }
    
    /** Timer critical color */
    public int timerCritical() { return scheme.timerCriticalArgb(); }
    
    /**
     * Get the underlying color scheme.
     *
     * @return The wrapped color scheme
     */
    public ColorScheme scheme() {
        return scheme;
    }
}

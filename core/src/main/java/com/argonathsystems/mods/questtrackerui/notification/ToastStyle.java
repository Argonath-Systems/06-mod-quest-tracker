package com.argonathsystems.mods.questtrackerui.notification;

/**
 * Style for toast notifications.
 */
public enum ToastStyle {
    
    /**
     * Informational toast.
     */
    INFO(0xFF87CEEB, "info"),
    
    /**
     * Success toast (quest/objective complete).
     */
    SUCCESS(0xFF90EE90, "complete"),
    
    /**
     * Warning toast (timer warning).
     */
    WARNING(0xFFFFA500, "warning"),
    
    /**
     * Error toast (quest failed).
     */
    ERROR(0xFFFF6B6B, "error"),
    
    /**
     * Epic toast with special effects (quest complete with rewards).
     */
    EPIC(0xFFFFD700, "legendary");
    
    private final int color;
    private final String soundSuffix;
    
    ToastStyle(int color, String soundSuffix) {
        this.color = color;
        this.soundSuffix = soundSuffix;
    }
    
    /**
     * Get the accent color for this style.
     *
     * @return ARGB color value
     */
    public int primaryColor() {
        return color;
    }
    
    /**
     * Get the sound identifier for this style.
     *
     * @return Sound identifier
     */
    public String soundId() {
        return "quest." + soundSuffix;
    }
}

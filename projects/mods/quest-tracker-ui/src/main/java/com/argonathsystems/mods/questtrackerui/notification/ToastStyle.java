package com.argonathsystems.mods.questtrackerui.notification;

/**
 * Style for toast notifications.
 */
public enum ToastStyle {
    
    /**
     * Informational toast.
     */
    INFO("#3498db", "info"),
    
    /**
     * Success toast (quest/objective complete).
     */
    SUCCESS("#27ae60", "success"),
    
    /**
     * Warning toast (timer warning).
     */
    WARNING("#f39c12", "warning"),
    
    /**
     * Error toast (quest failed).
     */
    ERROR("#e74c3c", "error"),
    
    /**
     * Epic toast with special effects (quest complete with rewards).
     */
    EPIC("#9b59b6", "epic");
    
    private final String color;
    private final String soundPrefix;
    
    ToastStyle(String color, String soundPrefix) {
        this.color = color;
        this.soundPrefix = soundPrefix;
    }
    
    /**
     * Get the accent color for this style.
     *
     * @return Hex color string
     */
    public String color() {
        return color;
    }
    
    /**
     * Get the sound prefix for this style.
     *
     * @return Sound identifier prefix
     */
    public String soundPrefix() {
        return soundPrefix;
    }
    
    /**
     * Parse color to ARGB.
     *
     * @return ARGB color value
     */
    public int colorArgb() {
        String clean = color.startsWith("#") ? color.substring(1) : color;
        return 0xFF000000 | Integer.parseInt(clean, 16);
    }
}

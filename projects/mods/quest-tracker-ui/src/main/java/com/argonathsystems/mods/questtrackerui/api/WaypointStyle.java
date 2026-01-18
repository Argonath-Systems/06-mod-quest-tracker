package com.argonathsystems.mods.questtrackerui.api;

/**
 * Visual style for waypoint markers.
 */
public enum WaypointStyle {
    
    /**
     * Standard waypoint marker.
     */
    DEFAULT("default", "Default", "waypoint_default", 0xFFFFFFFF),
    
    /**
     * Objective-specific icon.
     */
    OBJECTIVE("objective", "Objective", "waypoint_objective", 0xFFFFA500),
    
    /**
     * NPC head/icon style.
     */
    NPC("npc", "NPC", "waypoint_npc", 0xFF00FF00),
    
    /**
     * Location flag style.
     */
    LOCATION("location", "Location", "waypoint_location", 0xFF0000FF),
    
    /**
     * Danger/warning style (skull icon).
     */
    DANGER("danger", "Danger", "waypoint_danger", 0xFFFF4444),
    
    /**
     * Quest giver style.
     */
    QUEST_GIVER("quest_giver", "Quest Giver", "waypoint_quest_giver", 0xFFFFD700),
    
    /**
     * Quest turn-in style.
     */
    QUEST_TURNIN("quest_turnin", "Turn-In", "waypoint_quest_turnin", 0xFF90EE90);
    
    private final String id;
    private final String displayName;
    private final String iconName;
    private final int color;
    
    WaypointStyle(String id, String displayName, String iconName, int color) {
        this.id = id;
        this.displayName = displayName;
        this.iconName = iconName;
        this.color = color;
    }
    
    /**
     * Get the identifier for this style.
     *
     * @return Style identifier
     */
    public String id() {
        return id;
    }
    
    /**
     * Get the human-readable display name.
     *
     * @return Display name
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Get the icon asset name.
     *
     * @return Icon name
     */
    public String iconName() {
        return iconName;
    }

    /**
     * Get the color hex code.
     *
     * @return Color int (ARGB)
     */
    public int color() {
        return color;
    }
}

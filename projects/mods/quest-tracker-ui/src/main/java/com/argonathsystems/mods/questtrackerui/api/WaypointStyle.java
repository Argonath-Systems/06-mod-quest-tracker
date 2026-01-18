package com.argonathsystems.mods.questtrackerui.api;

/**
 * Visual style for waypoint markers.
 */
public enum WaypointStyle {
    
    /**
     * Standard waypoint marker.
     */
    DEFAULT("default", "Default"),
    
    /**
     * Objective-specific icon.
     */
    OBJECTIVE("objective", "Objective"),
    
    /**
     * NPC head/icon style.
     */
    NPC("npc", "NPC"),
    
    /**
     * Location flag style.
     */
    LOCATION("location", "Location"),
    
    /**
     * Danger/warning style (skull icon).
     */
    DANGER("danger", "Danger"),
    
    /**
     * Quest giver style.
     */
    QUEST_GIVER("quest_giver", "Quest Giver"),
    
    /**
     * Quest turn-in style.
     */
    QUEST_TURNIN("quest_turnin", "Turn-In");
    
    private final String id;
    private final String displayName;
    
    WaypointStyle(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
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
}

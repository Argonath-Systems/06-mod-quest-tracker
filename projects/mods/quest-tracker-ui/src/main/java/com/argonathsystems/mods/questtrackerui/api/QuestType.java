package com.argonathsystems.mods.questtrackerui.api;

/**
 * Represents the type of a quest for display purposes.
 */
public enum QuestType {
    
    /**
     * Main story/primary quest.
     * Displayed with ★ icon and gold color.
     */
    MAIN("★", "Main Quest"),
    
    /**
     * Side quest.
     * Displayed with ◆ icon and light blue color.
     */
    SIDE("◆", "Side Quest"),
    
    /**
     * Timed quest with a deadline.
     * Displayed with ○ icon and red color.
     */
    TIMED("○", "Timed Quest"),
    
    /**
     * Guild-related quest.
     * Displayed with ⚔ icon and green color.
     */
    GUILD("⚔", "Guild Quest"),
    
    /**
     * Daily repeatable quest.
     * Displayed with ☼ icon and orange color.
     */
    DAILY("☼", "Daily Quest"),
    
    /**
     * Weekly repeatable quest.
     * Displayed with ▣ icon and purple color.
     */
    WEEKLY("▣", "Weekly Quest"),
    
    /**
     * Special event quest.
     * Displayed with ✦ icon and magenta color.
     */
    EVENT("✦", "Event Quest");
    
    private final String icon;
    private final String displayName;
    
    QuestType(String icon, String displayName) {
        this.icon = icon;
        this.displayName = displayName;
    }
    
    /**
     * Get the icon character for this quest type.
     *
     * @return Unicode icon character
     */
    public String icon() {
        return icon;
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

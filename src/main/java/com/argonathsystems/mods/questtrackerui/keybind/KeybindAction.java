package com.argonathsystems.mods.questtrackerui.keybind;

/**
 * Represents a keybind action that can be triggered.
 * 
 * <p>Enum of all keybind actions defined in QT-006 specification.
 * 
 * @author Argonath Systems
 * @version 1.1.0
 * @since 1.1.0
 */
public enum KeybindAction {
    
    /**
     * Toggle tracker visibility on/off.
     */
    TOGGLE_TRACKER("Toggle Tracker", "quest_tracker.keybind.toggle", false),
    
    /**
     * Expand or collapse the tracker widget.
     */
    EXPAND_TRACKER("Expand/Collapse Tracker", "quest_tracker.keybind.expand", false),
    
    /**
     * Cycle through pinned quests.
     */
    CYCLE_PINNED("Cycle Pinned Quests", "quest_tracker.keybind.cycle", false),
    
    /**
     * Open the full quest list menu.
     */
    OPEN_QUEST_MENU("Open Quest Menu", "quest_tracker.keybind.menu", false),
    
    /**
     * Track the nearest quest objective.
     */
    TRACK_NEAREST("Track Nearest", "quest_tracker.keybind.nearest", false),
    
    /**
     * Pin or unpin a quest (in menu context).
     */
    PIN_QUEST("Pin/Unpin Quest", "quest_tracker.keybind.pin", true),
    
    /**
     * Abandon a quest (in menu context).
     */
    ABANDON_QUEST("Abandon Quest", "quest_tracker.keybind.abandon", true),
    
    /**
     * Share a quest with party members (in menu context).
     */
    SHARE_QUEST("Share Quest", "quest_tracker.keybind.share", true);
    
    private final String displayName;
    private final String translationKey;
    private final boolean menuContextOnly;
    
    KeybindAction(String displayName, String translationKey, boolean menuContextOnly) {
        this.displayName = displayName;
        this.translationKey = translationKey;
        this.menuContextOnly = menuContextOnly;
    }
    
    /**
     * Get the human-readable display name.
     *
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get the translation key for localization.
     *
     * @return Translation key
     */
    public String getTranslationKey() {
        return translationKey;
    }
    
    /**
     * Check if this action only applies within menu context.
     *
     * @return true if menu-only action
     */
    public boolean isMenuContextOnly() {
        return menuContextOnly;
    }
    
    /**
     * Get the keybind for this action from a config.
     *
     * @param config Keybind configuration
     * @return The configured keybind string
     */
    public String getKeybind(KeybindConfig config) {
        return switch (this) {
            case TOGGLE_TRACKER -> config.toggleTracker();
            case EXPAND_TRACKER -> config.expandTracker();
            case CYCLE_PINNED -> config.cyclePinned();
            case OPEN_QUEST_MENU -> config.openQuestMenu();
            case TRACK_NEAREST -> config.trackNearest();
            case PIN_QUEST -> config.pinQuest();
            case ABANDON_QUEST -> config.abandonQuest();
            case SHARE_QUEST -> config.shareQuest();
        };
    }
}

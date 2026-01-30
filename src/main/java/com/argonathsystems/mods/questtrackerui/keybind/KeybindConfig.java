package com.argonathsystems.mods.questtrackerui.keybind;

import java.util.Objects;

/**
 * Configuration for Quest Tracker keybindings per QT-006 specification.
 * 
 * <p>Defines the default keybindings and allows customization through config files.
 * The actual keybind registration is handled by the platform adapter layer since
 * keybind APIs are platform-specific.
 * 
 * <h2>Default Keybindings (QT-006)</h2>
 * <ul>
 *   <li>K - Toggle tracker visibility</li>
 *   <li>SHIFT+K - Expand/collapse tracker details</li>
 *   <li>TAB - Cycle through pinned quests</li>
 *   <li>J - Open full quest list menu</li>
 *   <li>N - Track nearest quest objective</li>
 * </ul>
 * 
 * @author Argonath Systems
 * @version 1.1.0
 * @since 1.1.0
 */
public record KeybindConfig(
    String toggleTracker,
    String expandTracker,
    String cyclePinned,
    String openQuestMenu,
    String trackNearest,
    String pinQuest,
    String abandonQuest,
    String shareQuest
) {
    
    /**
     * Default keybinding configuration per QT-006 specification.
     */
    public static final KeybindConfig DEFAULT = new KeybindConfig(
        "K",            // Toggle tracker
        "SHIFT+K",      // Expand tracker
        "TAB",          // Cycle pinned
        "J",            // Open quest menu
        "N",            // Track nearest
        "MOUSE_MIDDLE", // Pin quest (in menu)
        "DELETE",       // Abandon quest (in menu)
        "S"             // Share quest (in menu)
    );
    
    /**
     * Validate keybind config.
     */
    public KeybindConfig {
        Objects.requireNonNull(toggleTracker, "toggleTracker keybind must not be null");
        Objects.requireNonNull(expandTracker, "expandTracker keybind must not be null");
        Objects.requireNonNull(cyclePinned, "cyclePinned keybind must not be null");
        Objects.requireNonNull(openQuestMenu, "openQuestMenu keybind must not be null");
        Objects.requireNonNull(trackNearest, "trackNearest keybind must not be null");
        Objects.requireNonNull(pinQuest, "pinQuest keybind must not be null");
        Objects.requireNonNull(abandonQuest, "abandonQuest keybind must not be null");
        Objects.requireNonNull(shareQuest, "shareQuest keybind must not be null");
    }
    
    /**
     * Builder for creating custom keybind configurations.
     */
    public static class Builder {
        private String toggleTracker = DEFAULT.toggleTracker();
        private String expandTracker = DEFAULT.expandTracker();
        private String cyclePinned = DEFAULT.cyclePinned();
        private String openQuestMenu = DEFAULT.openQuestMenu();
        private String trackNearest = DEFAULT.trackNearest();
        private String pinQuest = DEFAULT.pinQuest();
        private String abandonQuest = DEFAULT.abandonQuest();
        private String shareQuest = DEFAULT.shareQuest();
        
        public Builder toggleTracker(String key) {
            this.toggleTracker = key;
            return this;
        }
        
        public Builder expandTracker(String key) {
            this.expandTracker = key;
            return this;
        }
        
        public Builder cyclePinned(String key) {
            this.cyclePinned = key;
            return this;
        }
        
        public Builder openQuestMenu(String key) {
            this.openQuestMenu = key;
            return this;
        }
        
        public Builder trackNearest(String key) {
            this.trackNearest = key;
            return this;
        }
        
        public Builder pinQuest(String key) {
            this.pinQuest = key;
            return this;
        }
        
        public Builder abandonQuest(String key) {
            this.abandonQuest = key;
            return this;
        }
        
        public Builder shareQuest(String key) {
            this.shareQuest = key;
            return this;
        }
        
        public KeybindConfig build() {
            return new KeybindConfig(
                toggleTracker, expandTracker, cyclePinned, openQuestMenu,
                trackNearest, pinQuest, abandonQuest, shareQuest
            );
        }
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
     * Check if a keybind includes a modifier key.
     *
     * @param keybind Keybind string
     * @return true if contains SHIFT, CTRL, or ALT
     */
    public static boolean hasModifier(String keybind) {
        if (keybind == null) return false;
        String upper = keybind.toUpperCase();
        return upper.contains("SHIFT") || upper.contains("CTRL") || upper.contains("ALT");
    }
    
    /**
     * Get the base key without modifiers.
     *
     * @param keybind Keybind string (e.g., "SHIFT+K")
     * @return Base key (e.g., "K")
     */
    public static String getBaseKey(String keybind) {
        if (keybind == null) return "";
        int plusIndex = keybind.lastIndexOf('+');
        if (plusIndex >= 0 && plusIndex < keybind.length() - 1) {
            return keybind.substring(plusIndex + 1);
        }
        return keybind;
    }
    
    /**
     * Check if keybind requires SHIFT modifier.
     *
     * @param keybind Keybind string
     * @return true if SHIFT is required
     */
    public static boolean requiresShift(String keybind) {
        return keybind != null && keybind.toUpperCase().contains("SHIFT");
    }
    
    /**
     * Check if keybind requires CTRL modifier.
     *
     * @param keybind Keybind string
     * @return true if CTRL is required
     */
    public static boolean requiresCtrl(String keybind) {
        return keybind != null && keybind.toUpperCase().contains("CTRL");
    }
    
    /**
     * Check if keybind requires ALT modifier.
     *
     * @param keybind Keybind string
     * @return true if ALT is required
     */
    public static boolean requiresAlt(String keybind) {
        return keybind != null && keybind.toUpperCase().contains("ALT");
    }
}

package com.argonathsystems.mods.questtrackerui.config;

/**
 * Collapse behavior configuration.
 *
 * @param enabled Whether collapse is enabled
 * @param defaultCollapsed Whether to start collapsed
 * @param collapseCompleted Whether to auto-collapse completed quests
 * @param collapseDuringCombat Whether to collapse during combat
 */
public record CollapseConfig(
    boolean enabled,
    boolean defaultCollapsed,
    boolean collapseCompleted,
    boolean collapseDuringCombat
) {
    
    /**
     * Default collapse configuration.
     */
    public static final CollapseConfig DEFAULT = new CollapseConfig(
        true, false, true, false
    );
    
    /**
     * Configuration with collapse disabled.
     */
    public static final CollapseConfig DISABLED = new CollapseConfig(
        false, false, false, false
    );
}

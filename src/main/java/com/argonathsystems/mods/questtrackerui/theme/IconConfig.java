package com.argonathsystems.mods.questtrackerui.theme;

/**
 * Icon configuration for a theme.
 *
 * @param questMain Main quest icon path
 * @param questSide Side quest icon path
 * @param questTimed Timed quest icon path
 * @param questGuild Guild quest icon path
 * @param objectiveComplete Completed objective icon path
 * @param objectiveIncomplete Incomplete objective icon path
 * @param waypoint Waypoint arrow icon path
 * @param timer Timer icon path
 * @param pinned Pinned quest icon path
 * @param settings Settings gear icon path
 * @param collapse Collapse/expand icon path
 */
public record IconConfig(
    String questMain,
    String questSide,
    String questTimed,
    String questGuild,
    String objectiveComplete,
    String objectiveIncomplete,
    String waypoint,
    String timer,
    String pinned,
    String settings,
    String collapse
) {
    
    /**
     * Default icon configuration using built-in textures.
     */
    public static final IconConfig DEFAULT = new IconConfig(
        "textures/icons/star.png",
        "textures/icons/diamond.png",
        "textures/icons/clock.png",
        "textures/icons/sword.png",
        "textures/icons/check.png",
        "textures/icons/square.png",
        "textures/icons/arrow.png",
        "textures/icons/timer.png",
        "textures/icons/pin.png",
        "textures/icons/gear.png",
        "textures/icons/chevron.png"
    );
    
    /**
     * Get icon path for a quest type.
     *
     * @param type Quest type
     * @return Icon path
     */
    public String forQuestType(com.argonathsystems.mods.questtrackerui.api.QuestType type) {
        return switch (type) {
            case MAIN -> questMain;
            case SIDE -> questSide;
            case TIMED -> questTimed;
            case GUILD -> questGuild;
            case DAILY -> questTimed; // Reuse timed icon
            case WEEKLY -> questGuild; // Reuse guild icon
            case EVENT -> questMain; // Reuse main icon
        };
    }
    
    /**
     * Get daily quest icon path.
     *
     * @return Daily quest icon path
     */
    public String questDaily() {
        return questTimed; // Reuse timed icon for daily
    }
    
    /**
     * Get weekly quest icon path.
     *
     * @return Weekly quest icon path
     */
    public String questWeekly() {
        return questGuild; // Reuse guild icon for weekly
    }
    
    /**
     * Get event quest icon path.
     *
     * @return Event quest icon path
     */
    public String questEvent() {
        return questMain; // Reuse main icon for event
    }
}

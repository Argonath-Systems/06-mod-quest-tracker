package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.api.QuestType;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Filter options for the quest list.
 */
public enum QuestFilterOption {
    
    /**
     * Show all quests.
     */
    ALL("All", q -> true),
    
    /**
     * Show only main story quests.
     */
    MAIN_STORY("Main Story", q -> q.type() == QuestType.MAIN),
    
    /**
     * Show only side quests.
     */
    SIDE_QUESTS("Side Quests", q -> q.type() == QuestType.SIDE),
    
    /**
     * Show only timed quests.
     */
    TIMED("Timed", q -> q.type() == QuestType.TIMED || q.isTimed()),
    
    /**
     * Show only daily quests.
     */
    DAILY("Daily", q -> q.type() == QuestType.DAILY),
    
    /**
     * Show only guild quests.
     */
    GUILD("Guild", q -> q.type() == QuestType.GUILD),
    
    /**
     * Show only pinned quests.
     */
    PINNED("Pinned", TrackedQuest::isPinned),
    
    /**
     * Show only incomplete quests.
     */
    INCOMPLETE("Incomplete", q -> !q.isComplete()),
    
    /**
     * Show only complete quests.
     */
    COMPLETE("Complete", TrackedQuest::isComplete);
    
    private final String displayName;
    private final Predicate<TrackedQuest> predicate;
    
    QuestFilterOption(String displayName, Predicate<TrackedQuest> predicate) {
        this.displayName = displayName;
        this.predicate = predicate;
    }
    
    /**
     * Get the display name for UI.
     *
     * @return Human-readable name
     */
    public String displayName() {
        return displayName;
    }
    
    /**
     * Get the filter predicate.
     *
     * @return Predicate for filtering quests
     */
    public Predicate<TrackedQuest> predicate() {
        return predicate;
    }
    
    /**
     * Get the next filter option (cycles through all options).
     *
     * @return Next filter option
     */
    public QuestFilterOption next() {
        QuestFilterOption[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
    
    /**
     * Get all type-based filters.
     *
     * @return Set of type-based filter options
     */
    public static Set<QuestFilterOption> typeFilters() {
        return EnumSet.of(MAIN_STORY, SIDE_QUESTS, TIMED, DAILY, GUILD);
    }
    
    /**
     * Get all status-based filters.
     *
     * @return Set of status-based filter options
     */
    public static Set<QuestFilterOption> statusFilters() {
        return EnumSet.of(PINNED, INCOMPLETE, COMPLETE);
    }
}

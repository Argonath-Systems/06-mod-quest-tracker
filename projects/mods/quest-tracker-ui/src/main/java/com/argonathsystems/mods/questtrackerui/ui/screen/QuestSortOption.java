package com.argonathsystems.mods.questtrackerui.ui.screen;

import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;

import java.util.Comparator;

/**
 * Sort options for the quest list.
 */
public enum QuestSortOption {
    
    /**
     * Sort by quest name alphabetically.
     */
    NAME("Name", Comparator.comparing(TrackedQuest::name)),
    
    /**
     * Sort by quest type (main first, then side, etc.).
     */
    TYPE("Type", Comparator.comparing(q -> q.type().ordinal())),
    
    /**
     * Sort by priority (highest first).
     */
    PRIORITY("Priority", Comparator.comparingInt(TrackedQuest::displayPriority).reversed()),
    
    /**
     * Sort by progress percentage (most complete first).
     */
    PROGRESS("Progress", Comparator.comparingDouble(TrackedQuest::progressPercent).reversed()),
    
    /**
     * Sort by time remaining (least time first).
     */
    TIME_REMAINING("Time", (a, b) -> {
        if (a.timeRemaining() == null && b.timeRemaining() == null) return 0;
        if (a.timeRemaining() == null) return 1;
        if (b.timeRemaining() == null) return -1;
        return a.timeRemaining().compareTo(b.timeRemaining());
    }),
    
    /**
     * Sort by pinned status (pinned first).
     */
    PINNED("Pinned", Comparator.comparing(TrackedQuest::isPinned).reversed());
    
    private final String displayName;
    private final Comparator<TrackedQuest> comparator;
    
    QuestSortOption(String displayName, Comparator<TrackedQuest> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
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
     * Get the comparator for this sort option.
     *
     * @return Comparator for sorting quests
     */
    public Comparator<TrackedQuest> comparator() {
        return comparator;
    }
    
    /**
     * Get the next sort option (cycles through all options).
     *
     * @return Next sort option
     */
    public QuestSortOption next() {
        QuestSortOption[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}

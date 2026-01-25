package com.argonathsystems.mods.questtrackerui.api;

import java.time.Duration;
import java.util.List;

/**
 * Represents a quest being tracked in the UI.
 *
 * @param id Unique quest identifier
 * @param name Quest name/title
 * @param description Quest description
 * @param type Type of quest (main, side, timed, etc.)
 * @param objectives List of objectives for this quest
 * @param timeRemaining Time remaining for timed quests (null if no time limit)
 * @param isPinned Whether this quest is pinned to the HUD
 * @param displayPriority Priority for display ordering (higher = more important)
 */
public record TrackedQuest(
    String id,
    String name,
    String description,
    QuestType type,
    List<TrackedObjective> objectives,
    Duration timeRemaining,
    boolean isPinned,
    int displayPriority
) {
    
    /**
     * Create a simple tracked quest.
     *
     * @param id Quest identifier
     * @param name Quest name
     * @param type Quest type
     * @param objectives List of objectives
     * @return TrackedQuest with default settings
     */
    public static TrackedQuest of(String id, String name, QuestType type, List<TrackedObjective> objectives) {
        return new TrackedQuest(id, name, "", type, List.copyOf(objectives), null, false, 0);
    }
    
    /**
     * Check if this is a timed quest with remaining time.
     *
     * @return true if timeRemaining is not null
     */
    public boolean isTimed() {
        return timeRemaining != null;
    }
    
    /**
     * Get the number of completed objectives.
     *
     * @return Count of completed objectives
     */
    public int completedObjectiveCount() {
        return (int) objectives.stream().filter(TrackedObjective::isComplete).count();
    }
    
    /**
     * Get overall progress as a percentage (0.0 to 1.0).
     *
     * @return Progress percentage based on objective completion
     */
    public double progressPercent() {
        if (objectives.isEmpty()) return 0.0;
        return (double) completedObjectiveCount() / objectives.size();
    }
    
    /**
     * Check if all objectives are complete.
     *
     * @return true if all objectives are complete
     */
    public boolean isComplete() {
        return !objectives.isEmpty() && objectives.stream().allMatch(TrackedObjective::isComplete);
    }
    
    /**
     * Get the first incomplete objective.
     *
     * @return First incomplete objective, or null if all complete
     */
    public TrackedObjective nextObjective() {
        return objectives.stream()
            .filter(o -> !o.isComplete())
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Create a copy with pinned status toggled.
     *
     * @param pinned Whether to pin
     * @return Updated quest
     */
    public TrackedQuest withPinned(boolean pinned) {
        return new TrackedQuest(id, name, description, type, objectives, timeRemaining, pinned, displayPriority);
    }
    
    /**
     * Create a copy with updated time remaining.
     *
     * @param remaining New time remaining
     * @return Updated quest
     */
    public TrackedQuest withTimeRemaining(Duration remaining) {
        return new TrackedQuest(id, name, description, type, objectives, remaining, isPinned, displayPriority);
    }
    
    /**
     * Create a copy with updated objectives.
     *
     * @param newObjectives New objectives list
     * @return Updated quest
     */
    public TrackedQuest withObjectives(List<TrackedObjective> newObjectives) {
        return new TrackedQuest(id, name, description, type, List.copyOf(newObjectives), timeRemaining, isPinned, displayPriority);
    }
    
    /**
     * Format the time remaining as a string (e.g., "12:34" or "1:23:45").
     *
     * @return Formatted time string, or empty if not timed
     */
    public String formattedTimeRemaining() {
        if (timeRemaining == null) return "";
        
        long totalSeconds = timeRemaining.toSeconds();
        if (totalSeconds < 0) return "0:00";
        
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }
}

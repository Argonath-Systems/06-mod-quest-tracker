package com.argonathsystems.mods.questtrackerui.api;

/**
 * Represents a single objective within a tracked quest.
 *
 * @param id Unique identifier within the quest
 * @param description Human-readable objective description
 * @param currentProgress Current progress value
 * @param requiredProgress Target progress value
 * @param isComplete Whether this objective is complete
 * @param waypoint Optional waypoint for this objective (null if none)
 */
public record TrackedObjective(
    String id,
    String description,
    int currentProgress,
    int requiredProgress,
    boolean isComplete,
    QuestWaypoint waypoint
) {
    
    /**
     * Create a simple objective without progress tracking.
     *
     * @param id Objective identifier
     * @param description Description
     * @param isComplete Whether complete
     * @return TrackedObjective with 0/1 or 1/1 progress
     */
    public static TrackedObjective simple(String id, String description, boolean isComplete) {
        return new TrackedObjective(id, description, isComplete ? 1 : 0, 1, isComplete, null);
    }
    
    /**
     * Create a progress-based objective.
     *
     * @param id Objective identifier
     * @param description Description
     * @param current Current progress
     * @param required Required progress
     * @return TrackedObjective with auto-calculated completion
     */
    public static TrackedObjective progress(String id, String description, int current, int required) {
        return new TrackedObjective(id, description, current, required, current >= required, null);
    }
    
    /**
     * Get progress as a percentage (0.0 to 1.0).
     *
     * @return Progress percentage
     */
    public double progressPercent() {
        if (requiredProgress <= 0) return isComplete ? 1.0 : 0.0;
        return Math.min(1.0, (double) currentProgress / requiredProgress);
    }
    
    /**
     * Check if this objective has a waypoint.
     *
     * @return true if waypoint is not null
     */
    public boolean hasWaypoint() {
        return waypoint != null;
    }
    
    /**
     * Get formatted progress text (e.g., "7/10").
     *
     * @return Progress text
     */
    public String progressText() {
        return currentProgress + "/" + requiredProgress;
    }
    
    /**
     * Create a copy with updated progress.
     *
     * @param current New current progress
     * @return Updated objective
     */
    public TrackedObjective withProgress(int current) {
        return new TrackedObjective(id, description, current, requiredProgress, current >= requiredProgress, waypoint);
    }
    
    /**
     * Create a copy with a waypoint.
     *
     * @param newWaypoint Waypoint to attach
     * @return Updated objective
     */
    public TrackedObjective withWaypoint(QuestWaypoint newWaypoint) {
        return new TrackedObjective(id, description, currentProgress, requiredProgress, isComplete, newWaypoint);
    }
}

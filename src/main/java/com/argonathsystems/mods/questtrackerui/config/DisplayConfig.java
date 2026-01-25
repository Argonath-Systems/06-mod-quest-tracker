package com.argonathsystems.mods.questtrackerui.config;

/**
 * Display settings for the tracker.
 *
 * @param maxPinnedQuests Maximum number of pinned quests to show
 * @param maxVisibleObjectives Maximum objectives to show per quest
 * @param showCompletedObjectives Whether to show completed objectives
 * @param completedFadeSeconds Seconds before completed objectives fade
 * @param showProgressBars Whether to show progress bars
 * @param showProgressText Whether to show progress as text
 * @param showDistance Whether to show distance to waypoints
 * @param showTimer Whether to show timers for timed quests
 */
public record DisplayConfig(
    int maxPinnedQuests,
    int maxVisibleObjectives,
    boolean showCompletedObjectives,
    int completedFadeSeconds,
    boolean showProgressBars,
    boolean showProgressText,
    boolean showDistance,
    boolean showTimer
) {
    
    /**
     * Default display configuration.
     */
    public static final DisplayConfig DEFAULT = new DisplayConfig(
        3, 8, true, 3, true, true, true, true
    );
    
    /**
     * Builder for display configuration.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Fluent builder for DisplayConfig.
     */
    public static class Builder {
        private int maxPinnedQuests = 3;
        private int maxVisibleObjectives = 8;
        private boolean showCompletedObjectives = true;
        private int completedFadeSeconds = 3;
        private boolean showProgressBars = true;
        private boolean showProgressText = true;
        private boolean showDistance = true;
        private boolean showTimer = true;
        
        public Builder maxPinnedQuests(int max) {
            this.maxPinnedQuests = max;
            return this;
        }
        
        public Builder maxVisibleObjectives(int max) {
            this.maxVisibleObjectives = max;
            return this;
        }
        
        public Builder showCompletedObjectives(boolean show) {
            this.showCompletedObjectives = show;
            return this;
        }
        
        public Builder completedFadeSeconds(int seconds) {
            this.completedFadeSeconds = seconds;
            return this;
        }
        
        public Builder showProgressBars(boolean show) {
            this.showProgressBars = show;
            return this;
        }
        
        public Builder showProgressText(boolean show) {
            this.showProgressText = show;
            return this;
        }
        
        public Builder showDistance(boolean show) {
            this.showDistance = show;
            return this;
        }
        
        public Builder showTimer(boolean show) {
            this.showTimer = show;
            return this;
        }
        
        public DisplayConfig build() {
            return new DisplayConfig(
                maxPinnedQuests, maxVisibleObjectives, showCompletedObjectives,
                completedFadeSeconds, showProgressBars, showProgressText,
                showDistance, showTimer
            );
        }
    }
}

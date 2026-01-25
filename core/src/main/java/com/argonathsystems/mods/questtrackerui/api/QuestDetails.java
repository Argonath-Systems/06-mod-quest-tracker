package com.argonathsystems.mods.questtrackerui.api;

import java.util.List;

/**
 * Detailed quest information for the quest detail view.
 *
 * @param quest The tracked quest data
 * @param questGiver Name of the NPC or source that gave this quest
 * @param recommendedLevel Recommended player level
 * @param category Quest category (e.g., "Main Story - Chapter 3")
 * @param rewards List of rewards for completing this quest
 * @param canAbandon Whether this quest can be abandoned
 * @param canShare Whether this quest can be shared
 */
public record QuestDetails(
    TrackedQuest quest,
    String questGiver,
    int recommendedLevel,
    String category,
    List<Reward> rewards,
    boolean canAbandon,
    boolean canShare
) {
    
    /**
     * Create quest details from a tracked quest with defaults.
     *
     * @param quest The tracked quest
     * @return QuestDetails with default values
     */
    public static QuestDetails fromQuest(TrackedQuest quest) {
        return new QuestDetails(quest, "", 1, "", List.of(), false, false);
    }
    
    /**
     * Builder for creating quest details.
     */
    public static Builder builder(TrackedQuest quest) {
        return new Builder(quest);
    }
    
    /**
     * Fluent builder for QuestDetails.
     */
    public static class Builder {
        private final TrackedQuest quest;
        private String questGiver = "";
        private int recommendedLevel = 1;
        private String category = "";
        private List<Reward> rewards = List.of();
        private boolean canAbandon = false;
        private boolean canShare = false;
        
        private Builder(TrackedQuest quest) {
            this.quest = quest;
        }
        
        public Builder questGiver(String giver) {
            this.questGiver = giver;
            return this;
        }
        
        public Builder recommendedLevel(int level) {
            this.recommendedLevel = level;
            return this;
        }
        
        public Builder category(String cat) {
            this.category = cat;
            return this;
        }
        
        public Builder rewards(List<Reward> rewardList) {
            this.rewards = List.copyOf(rewardList);
            return this;
        }
        
        public Builder canAbandon(boolean abandon) {
            this.canAbandon = abandon;
            return this;
        }
        
        public Builder canShare(boolean share) {
            this.canShare = share;
            return this;
        }
        
        public QuestDetails build() {
            return new QuestDetails(quest, questGiver, recommendedLevel, category, rewards, canAbandon, canShare);
        }
    }
}

package com.argonathsystems.mods.questtrackerui.api;

import java.util.UUID;

/**
 * Listener for real-time quest update events.
 * 
 * <p>Register with a {@link QuestDataProvider} to receive notifications
 * when quest state changes occur.
 */
public interface QuestUpdateListener {
    
    /**
     * Called when a new quest is added/accepted.
     *
     * @param playerId Player who accepted the quest
     * @param quest The newly added quest
     */
    void onQuestAdded(UUID playerId, TrackedQuest quest);
    
    /**
     * Called when a quest is removed (abandoned or failed).
     *
     * @param playerId Player who lost the quest
     * @param questId Identifier of the removed quest
     */
    void onQuestRemoved(UUID playerId, String questId);
    
    /**
     * Called when a quest is completed.
     *
     * @param playerId Player who completed the quest
     * @param questId Identifier of the completed quest
     */
    void onQuestCompleted(UUID playerId, String questId);
    
    /**
     * Called when an objective's progress is updated.
     *
     * @param playerId Player whose progress changed
     * @param questId Identifier of the quest
     * @param objective Updated objective data
     */
    void onObjectiveUpdated(UUID playerId, String questId, TrackedObjective objective);
    
    /**
     * Called when an objective is completed.
     *
     * @param playerId Player who completed the objective
     * @param questId Identifier of the quest
     * @param objectiveId Identifier of the completed objective
     */
    void onObjectiveCompleted(UUID playerId, String questId, String objectiveId);
    
    /**
     * Called when a quest's pinned status changes.
     *
     * @param playerId Player whose pin status changed
     * @param questId Identifier of the quest
     * @param pinned New pinned status
     */
    default void onQuestPinChanged(UUID playerId, String questId, boolean pinned) {
        // Default empty implementation
    }
    
    /**
     * Called when a timed quest's timer updates significantly.
     * 
     * <p>Not called every tick - only on significant changes
     * (e.g., warning thresholds).
     *
     * @param playerId Player with the timed quest
     * @param quest Updated quest with new time remaining
     */
    default void onTimerUpdate(UUID playerId, TrackedQuest quest) {
        // Default empty implementation
    }
}

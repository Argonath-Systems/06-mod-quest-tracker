package com.argonathsystems.mods.questtrackerui.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * API for quest mods to integrate with the Quest Tracker UI.
 * 
 * <p>Quest mods implement this interface to provide quest data
 * to the tracker. Multiple providers can be registered simultaneously
 * to support modpacks with multiple quest systems.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * public class MyQuestProvider implements QuestDataProvider {
 *     @Override
 *     public List<TrackedQuest> getTrackedQuests(UUID playerId) {
 *         return myQuestSystem.getQuestsForPlayer(playerId)
 *             .stream()
 *             .map(this::convertToTracked)
 *             .toList();
 *     }
 *     // ... other methods
 * }
 * }</pre>
 */
public interface QuestDataProvider {
    
    /**
     * Get the unique identifier for this provider.
     * 
     * <p>This is used to distinguish quests from different systems
     * in modpacks with multiple quest mods.
     *
     * @return Provider identifier (e.g., "quest-book-lite", "ftb-quests")
     */
    String getProviderId();
    
    /**
     * Get the display name for this provider.
     *
     * @return Human-readable name (e.g., "Quest Book Lite")
     */
    String getProviderName();
    
    /**
     * Get all trackable quests for a player.
     * 
     * <p>Returns quests that can be displayed in the quest list UI,
     * regardless of pinned status.
     *
     * @param playerId Target player's unique identifier
     * @return List of all trackable quests, may be empty
     */
    List<TrackedQuest> getTrackedQuests(UUID playerId);
    
    /**
     * Get pinned quests for a player.
     * 
     * <p>Returns only quests that should be shown on the HUD overlay.
     * The tracker will respect the max_pinned_quests configuration.
     *
     * @param playerId Target player's unique identifier
     * @return List of pinned quests, ordered by display priority
     */
    List<TrackedQuest> getPinnedQuests(UUID playerId);
    
    /**
     * Pin or unpin a quest for a player.
     *
     * @param playerId Target player's unique identifier
     * @param questId Quest identifier to pin/unpin
     * @param pinned true to pin, false to unpin
     * @return true if the operation was successful
     */
    boolean setPinned(UUID playerId, String questId, boolean pinned);
    
    /**
     * Get detailed information about a specific quest.
     * 
     * <p>Used for the quest detail view screen.
     *
     * @param questId Quest identifier
     * @return Quest details, or empty if quest not found
     */
    Optional<QuestDetails> getQuestDetails(String questId);
    
    /**
     * Register a listener for real-time quest updates.
     * 
     * <p>The tracker will call this to receive live updates
     * for progress changes, completions, etc.
     *
     * @param listener Listener to register
     */
    void addUpdateListener(QuestUpdateListener listener);
    
    /**
     * Remove a previously registered update listener.
     *
     * @param listener Listener to remove
     */
    void removeUpdateListener(QuestUpdateListener listener);
    
    /**
     * Check if this provider supports abandoning quests.
     *
     * @return true if abandon is supported
     */
    default boolean supportsAbandon() {
        return false;
    }
    
    /**
     * Abandon a quest for a player.
     * 
     * <p>Only called if {@link #supportsAbandon()} returns true.
     *
     * @param playerId Target player's unique identifier
     * @param questId Quest to abandon
     * @return true if abandoned successfully
     */
    default boolean abandonQuest(UUID playerId, String questId) {
        return false;
    }
    
    /**
     * Check if this provider supports sharing quests.
     *
     * @return true if sharing is supported
     */
    default boolean supportsSharing() {
        return false;
    }
    
    /**
     * Share a quest with another player.
     * 
     * <p>Only called if {@link #supportsSharing()} returns true.
     *
     * @param fromPlayerId Player sharing the quest
     * @param toPlayerId Player receiving the shared quest
     * @param questId Quest to share
     * @return true if shared successfully
     */
    default boolean shareQuest(UUID fromPlayerId, UUID toPlayerId, String questId) {
        return false;
    }
}

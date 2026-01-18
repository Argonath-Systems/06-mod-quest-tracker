package com.argonathsystems.mods.questtrackerui;

import com.argonathsystems.mods.questtrackerui.api.QuestDataProvider;
import com.argonathsystems.mods.questtrackerui.api.TrackedQuest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for quest data providers.
 * 
 * <p>Quest mods register their providers here to integrate with the tracker UI.
 * Multiple providers can be registered to support modpacks.
 */
public class ProviderRegistry {
    
    private static final ProviderRegistry INSTANCE = new ProviderRegistry();
    
    private final Map<String, QuestDataProvider> providers;
    private final List<ProviderChangeListener> listeners;
    
    private ProviderRegistry() {
        this.providers = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Get the singleton instance.
     *
     * @return Provider registry instance
     */
    public static ProviderRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Register a quest data provider.
     *
     * @param provider Provider to register
     * @throws IllegalArgumentException if a provider with the same ID is already registered
     */
    public void register(QuestDataProvider provider) {
        Objects.requireNonNull(provider, "Provider cannot be null");
        
        String id = provider.getProviderId();
        if (providers.containsKey(id)) {
            throw new IllegalArgumentException("Provider already registered: " + id);
        }
        
        providers.put(id, provider);
        notifyListeners(provider, true);
    }
    
    /**
     * Unregister a quest data provider.
     *
     * @param providerId ID of the provider to remove
     * @return true if the provider was removed
     */
    public boolean unregister(String providerId) {
        QuestDataProvider removed = providers.remove(providerId);
        if (removed != null) {
            notifyListeners(removed, false);
            return true;
        }
        return false;
    }
    
    /**
     * Get a provider by ID.
     *
     * @param providerId Provider ID
     * @return Optional containing the provider if found
     */
    public Optional<QuestDataProvider> getProvider(String providerId) {
        return Optional.ofNullable(providers.get(providerId));
    }
    
    /**
     * Get all registered providers.
     *
     * @return Unmodifiable collection of providers
     */
    public Collection<QuestDataProvider> getAllProviders() {
        return Collections.unmodifiableCollection(providers.values());
    }
    
    /**
     * Check if any providers are registered.
     *
     * @return true if at least one provider is registered
     */
    public boolean hasProviders() {
        return !providers.isEmpty();
    }
    
    /**
     * Get all pinned quests from all providers for a player.
     *
     * @param playerId Player ID
     * @return Combined list of pinned quests from all providers
     */
    public List<TrackedQuest> getAllPinnedQuests(UUID playerId) {
        List<TrackedQuest> allPinned = new ArrayList<>();
        
        for (QuestDataProvider provider : providers.values()) {
            try {
                allPinned.addAll(provider.getPinnedQuests(playerId));
            } catch (Exception e) {
                // Log and continue with other providers
                System.err.println("Error getting pinned quests from " + provider.getProviderId() + ": " + e.getMessage());
            }
        }
        
        // Sort by display priority (descending)
        allPinned.sort((a, b) -> Integer.compare(b.displayPriority(), a.displayPriority()));
        
        return allPinned;
    }
    
    /**
     * Get all tracked quests from all providers for a player.
     *
     * @param playerId Player ID
     * @return Combined list of all tracked quests
     */
    public List<TrackedQuest> getAllTrackedQuests(UUID playerId) {
        List<TrackedQuest> allQuests = new ArrayList<>();
        
        for (QuestDataProvider provider : providers.values()) {
            try {
                allQuests.addAll(provider.getTrackedQuests(playerId));
            } catch (Exception e) {
                System.err.println("Error getting tracked quests from " + provider.getProviderId() + ": " + e.getMessage());
            }
        }
        
        return allQuests;
    }
    
    /**
     * Add a listener for provider changes.
     *
     * @param listener Listener to add
     */
    public void addChangeListener(ProviderChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a provider change listener.
     *
     * @param listener Listener to remove
     */
    public void removeChangeListener(ProviderChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(QuestDataProvider provider, boolean added) {
        for (ProviderChangeListener listener : listeners) {
            try {
                if (added) {
                    listener.onProviderAdded(provider);
                } else {
                    listener.onProviderRemoved(provider);
                }
            } catch (Exception e) {
                System.err.println("Provider change listener error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Listener for provider registry changes.
     */
    public interface ProviderChangeListener {
        /**
         * Called when a provider is added.
         *
         * @param provider The added provider
         */
        void onProviderAdded(QuestDataProvider provider);
        
        /**
         * Called when a provider is removed.
         *
         * @param provider The removed provider
         */
        void onProviderRemoved(QuestDataProvider provider);
    }
}

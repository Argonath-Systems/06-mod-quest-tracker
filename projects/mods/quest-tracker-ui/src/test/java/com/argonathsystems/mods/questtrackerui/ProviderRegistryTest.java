package com.argonathsystems.mods.questtrackerui;

import com.argonathsystems.mods.questtrackerui.api.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProviderRegistry Tests")
class ProviderRegistryTest {
    
    private ProviderRegistry registry;
    
    @BeforeEach
    void setUp() {
        // Create a fresh registry for each test
        // Note: In production this is a singleton, but we test the behavior
        registry = new ProviderRegistry() {};
    }
    
    @Nested
    @DisplayName("Provider registration")
    class ProviderRegistration {
        
        @Test
        @DisplayName("register() adds provider")
        void registerAddsProvider() {
            MockQuestProvider provider = new MockQuestProvider("test-provider");
            
            registry.register(provider);
            
            assertThat(registry.getProvider("test-provider")).isPresent();
            assertThat(registry.hasProviders()).isTrue();
        }
        
        @Test
        @DisplayName("register() throws for duplicate ID")
        void registerThrowsForDuplicate() {
            MockQuestProvider provider1 = new MockQuestProvider("test");
            MockQuestProvider provider2 = new MockQuestProvider("test");
            
            registry.register(provider1);
            
            assertThatThrownBy(() -> registry.register(provider2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
        }
        
        @Test
        @DisplayName("unregister() removes provider")
        void unregisterRemovesProvider() {
            MockQuestProvider provider = new MockQuestProvider("test");
            registry.register(provider);
            
            boolean removed = registry.unregister("test");
            
            assertThat(removed).isTrue();
            assertThat(registry.getProvider("test")).isEmpty();
        }
        
        @Test
        @DisplayName("unregister() returns false for unknown ID")
        void unregisterReturnsFalseForUnknown() {
            boolean removed = registry.unregister("nonexistent");
            
            assertThat(removed).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Quest aggregation")
    class QuestAggregation {
        
        @Test
        @DisplayName("getAllPinnedQuests() combines from all providers")
        void getAllPinnedQuestsCombines() {
            MockQuestProvider provider1 = new MockQuestProvider("provider1");
            provider1.addQuest(createQuest("q1", "Quest 1", true, 1));
            
            MockQuestProvider provider2 = new MockQuestProvider("provider2");
            provider2.addQuest(createQuest("q2", "Quest 2", true, 2));
            
            registry.register(provider1);
            registry.register(provider2);
            
            UUID playerId = UUID.randomUUID();
            List<TrackedQuest> pinned = registry.getAllPinnedQuests(playerId);
            
            assertThat(pinned).hasSize(2);
        }
        
        @Test
        @DisplayName("getAllPinnedQuests() sorts by priority descending")
        void getAllPinnedQuestsSortsByPriority() {
            MockQuestProvider provider = new MockQuestProvider("test");
            provider.addQuest(createQuest("low", "Low Priority", true, 1));
            provider.addQuest(createQuest("high", "High Priority", true, 10));
            provider.addQuest(createQuest("mid", "Mid Priority", true, 5));
            
            registry.register(provider);
            
            UUID playerId = UUID.randomUUID();
            List<TrackedQuest> pinned = registry.getAllPinnedQuests(playerId);
            
            assertThat(pinned.get(0).id()).isEqualTo("high");
            assertThat(pinned.get(1).id()).isEqualTo("mid");
            assertThat(pinned.get(2).id()).isEqualTo("low");
        }
    }
    
    @Nested
    @DisplayName("Change listeners")
    class ChangeListeners {
        
        @Test
        @DisplayName("listener notified on provider added")
        void listenerNotifiedOnAdd() {
            QuestDataProvider[] captured = new QuestDataProvider[1];
            
            registry.addChangeListener(new ProviderRegistry.ProviderChangeListener() {
                @Override
                public void onProviderAdded(QuestDataProvider provider) {
                    captured[0] = provider;
                }
                
                @Override
                public void onProviderRemoved(QuestDataProvider provider) {}
            });
            
            MockQuestProvider provider = new MockQuestProvider("test");
            registry.register(provider);
            
            assertThat(captured[0]).isEqualTo(provider);
        }
        
        @Test
        @DisplayName("listener notified on provider removed")
        void listenerNotifiedOnRemove() {
            QuestDataProvider[] captured = new QuestDataProvider[1];
            
            MockQuestProvider provider = new MockQuestProvider("test");
            registry.register(provider);
            
            registry.addChangeListener(new ProviderRegistry.ProviderChangeListener() {
                @Override
                public void onProviderAdded(QuestDataProvider provider) {}
                
                @Override
                public void onProviderRemoved(QuestDataProvider provider) {
                    captured[0] = provider;
                }
            });
            
            registry.unregister("test");
            
            assertThat(captured[0]).isEqualTo(provider);
        }
    }
    
    // Helper methods
    
    private TrackedQuest createQuest(String id, String name, boolean pinned, int priority) {
        return new TrackedQuest(id, name, "", QuestType.MAIN, List.of(), null, pinned, priority);
    }
    
    /**
     * Simple mock provider for testing.
     */
    private static class MockQuestProvider implements QuestDataProvider {
        private final String id;
        private final List<TrackedQuest> quests = new java.util.ArrayList<>();
        
        MockQuestProvider(String id) {
            this.id = id;
        }
        
        void addQuest(TrackedQuest quest) {
            quests.add(quest);
        }
        
        @Override
        public String getProviderId() {
            return id;
        }
        
        @Override
        public String getProviderName() {
            return "Mock Provider: " + id;
        }
        
        @Override
        public List<TrackedQuest> getTrackedQuests(UUID playerId) {
            return List.copyOf(quests);
        }
        
        @Override
        public List<TrackedQuest> getPinnedQuests(UUID playerId) {
            return quests.stream().filter(TrackedQuest::isPinned).toList();
        }
        
        @Override
        public boolean setPinned(UUID playerId, String questId, boolean pinned) {
            return true;
        }
        
        @Override
        public Optional<QuestDetails> getQuestDetails(String questId) {
            return quests.stream()
                .filter(q -> q.id().equals(questId))
                .findFirst()
                .map(QuestDetails::fromQuest);
        }
        
        @Override
        public void addUpdateListener(QuestUpdateListener listener) {}
        
        @Override
        public void removeUpdateListener(QuestUpdateListener listener) {}
    }
}

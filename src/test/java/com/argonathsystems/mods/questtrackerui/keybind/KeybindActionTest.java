package com.argonathsystems.mods.questtrackerui.keybind;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link KeybindAction}.
 */
@DisplayName("KeybindAction Tests")
class KeybindActionTest {

    @Nested
    @DisplayName("Action properties")
    class ActionProperties {
        
        @Test
        @DisplayName("All actions have non-null display names")
        void allActionsHaveDisplayNames() {
            for (KeybindAction action : KeybindAction.values()) {
                assertThat(action.getDisplayName())
                    .as("DisplayName for %s", action.name())
                    .isNotNull()
                    .isNotBlank();
            }
        }
        
        @Test
        @DisplayName("All actions have translation keys")
        void allActionsHaveTranslationKeys() {
            for (KeybindAction action : KeybindAction.values()) {
                assertThat(action.getTranslationKey())
                    .as("TranslationKey for %s", action.name())
                    .isNotNull()
                    .startsWith("quest_tracker.keybind.");
            }
        }
    }
    
    @Nested
    @DisplayName("Menu context actions")
    class MenuContextActions {
        
        @Test
        @DisplayName("PIN_QUEST is menu-context only")
        void pinQuestIsMenuOnly() {
            assertThat(KeybindAction.PIN_QUEST.isMenuContextOnly()).isTrue();
        }
        
        @Test
        @DisplayName("ABANDON_QUEST is menu-context only")
        void abandonQuestIsMenuOnly() {
            assertThat(KeybindAction.ABANDON_QUEST.isMenuContextOnly()).isTrue();
        }
        
        @Test
        @DisplayName("SHARE_QUEST is menu-context only")
        void shareQuestIsMenuOnly() {
            assertThat(KeybindAction.SHARE_QUEST.isMenuContextOnly()).isTrue();
        }
        
        @Test
        @DisplayName("TOGGLE_TRACKER is not menu-context only")
        void toggleTrackerIsNotMenuOnly() {
            assertThat(KeybindAction.TOGGLE_TRACKER.isMenuContextOnly()).isFalse();
        }
        
        @Test
        @DisplayName("OPEN_QUEST_MENU is not menu-context only")
        void openQuestMenuIsNotMenuOnly() {
            assertThat(KeybindAction.OPEN_QUEST_MENU.isMenuContextOnly()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Keybind resolution")
    class KeybindResolution {
        
        @Test
        @DisplayName("getKeybind() returns correct values from config")
        void getKeybindReturnsFromConfig() {
            KeybindConfig config = KeybindConfig.DEFAULT;
            
            assertThat(KeybindAction.TOGGLE_TRACKER.getKeybind(config)).isEqualTo("K");
            assertThat(KeybindAction.EXPAND_TRACKER.getKeybind(config)).isEqualTo("SHIFT+K");
            assertThat(KeybindAction.CYCLE_PINNED.getKeybind(config)).isEqualTo("TAB");
            assertThat(KeybindAction.OPEN_QUEST_MENU.getKeybind(config)).isEqualTo("J");
            assertThat(KeybindAction.TRACK_NEAREST.getKeybind(config)).isEqualTo("N");
        }
        
        @Test
        @DisplayName("getKeybind() uses custom config values")
        void getKeybindUsesCustomConfig() {
            KeybindConfig config = new KeybindConfig.Builder()
                .toggleTracker("CUSTOM")
                .build();
            
            assertThat(KeybindAction.TOGGLE_TRACKER.getKeybind(config)).isEqualTo("CUSTOM");
        }
    }
    
    @Nested
    @DisplayName("Expected action count")
    class ExpectedActionCount {
        
        @Test
        @DisplayName("There are exactly 8 keybind actions")
        void correctActionCount() {
            assertThat(KeybindAction.values()).hasSize(8);
        }
        
        @Test
        @DisplayName("Expected actions exist")
        void expectedActionsExist() {
            assertThat(KeybindAction.valueOf("TOGGLE_TRACKER")).isNotNull();
            assertThat(KeybindAction.valueOf("EXPAND_TRACKER")).isNotNull();
            assertThat(KeybindAction.valueOf("CYCLE_PINNED")).isNotNull();
            assertThat(KeybindAction.valueOf("OPEN_QUEST_MENU")).isNotNull();
            assertThat(KeybindAction.valueOf("TRACK_NEAREST")).isNotNull();
            assertThat(KeybindAction.valueOf("PIN_QUEST")).isNotNull();
            assertThat(KeybindAction.valueOf("ABANDON_QUEST")).isNotNull();
            assertThat(KeybindAction.valueOf("SHARE_QUEST")).isNotNull();
        }
    }
}

package com.argonathsystems.mods.questtrackerui.keybind;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link KeybindConfig}.
 */
@DisplayName("KeybindConfig Tests")
class KeybindConfigTest {

    @Nested
    @DisplayName("Default configuration")
    class DefaultConfiguration {
        
        @Test
        @DisplayName("DEFAULT has expected keybinds per QT-006")
        void defaultHasExpectedKeybinds() {
            KeybindConfig config = KeybindConfig.DEFAULT;
            
            assertThat(config.toggleTracker()).isEqualTo("K");
            assertThat(config.expandTracker()).isEqualTo("SHIFT+K");
            assertThat(config.cyclePinned()).isEqualTo("TAB");
            assertThat(config.openQuestMenu()).isEqualTo("J");
            assertThat(config.trackNearest()).isEqualTo("N");
            assertThat(config.pinQuest()).isEqualTo("MOUSE_MIDDLE");
            assertThat(config.abandonQuest()).isEqualTo("DELETE");
            assertThat(config.shareQuest()).isEqualTo("S");
        }
    }
    
    @Nested
    @DisplayName("Record validation")
    class RecordValidation {
        
        @Test
        @DisplayName("Null toggleTracker throws NullPointerException")
        void nullToggleTrackerThrows() {
            assertThatThrownBy(() -> new KeybindConfig(
                null, "SHIFT+K", "TAB", "J", "N", "MOUSE_MIDDLE", "DELETE", "S"
            )).isInstanceOf(NullPointerException.class)
              .hasMessageContaining("toggleTracker");
        }
        
        @Test
        @DisplayName("Null expandTracker throws NullPointerException")
        void nullExpandTrackerThrows() {
            assertThatThrownBy(() -> new KeybindConfig(
                "K", null, "TAB", "J", "N", "MOUSE_MIDDLE", "DELETE", "S"
            )).isInstanceOf(NullPointerException.class)
              .hasMessageContaining("expandTracker");
        }
        
        @Test
        @DisplayName("Null cyclePinned throws NullPointerException")
        void nullCyclePinnedThrows() {
            assertThatThrownBy(() -> new KeybindConfig(
                "K", "SHIFT+K", null, "J", "N", "MOUSE_MIDDLE", "DELETE", "S"
            )).isInstanceOf(NullPointerException.class)
              .hasMessageContaining("cyclePinned");
        }
        
        @Test
        @DisplayName("All null values throw NullPointerException")
        void allNullThrows() {
            assertThatThrownBy(() -> new KeybindConfig(
                null, null, null, null, null, null, null, null
            )).isInstanceOf(NullPointerException.class);
        }
    }
    
    @Nested
    @DisplayName("Modifier key parsing")
    class ModifierKeyParsing {
        
        @Test
        @DisplayName("requiresShift() detects SHIFT modifier")
        void requiresShiftDetectsModifier() {
            assertThat(KeybindConfig.requiresShift("SHIFT+K")).isTrue();
            assertThat(KeybindConfig.requiresShift("SHIFT+CTRL+A")).isTrue();
            assertThat(KeybindConfig.requiresShift("K")).isFalse();
            assertThat(KeybindConfig.requiresShift("CTRL+K")).isFalse();
        }
        
        @Test
        @DisplayName("requiresCtrl() detects CTRL modifier")
        void requiresCtrlDetectsModifier() {
            assertThat(KeybindConfig.requiresCtrl("CTRL+K")).isTrue();
            assertThat(KeybindConfig.requiresCtrl("SHIFT+CTRL+A")).isTrue();
            assertThat(KeybindConfig.requiresCtrl("K")).isFalse();
            assertThat(KeybindConfig.requiresCtrl("SHIFT+K")).isFalse();
        }
        
        @Test
        @DisplayName("requiresAlt() detects ALT modifier")
        void requiresAltDetectsModifier() {
            assertThat(KeybindConfig.requiresAlt("ALT+K")).isTrue();
            assertThat(KeybindConfig.requiresAlt("SHIFT+ALT+A")).isTrue();
            assertThat(KeybindConfig.requiresAlt("K")).isFalse();
            assertThat(KeybindConfig.requiresAlt("SHIFT+K")).isFalse();
        }
        
        @Test
        @DisplayName("getBaseKey() extracts base key from modifier combinations")
        void getBaseKeyExtractsBase() {
            assertThat(KeybindConfig.getBaseKey("K")).isEqualTo("K");
            assertThat(KeybindConfig.getBaseKey("SHIFT+K")).isEqualTo("K");
            assertThat(KeybindConfig.getBaseKey("CTRL+SHIFT+A")).isEqualTo("A");
            assertThat(KeybindConfig.getBaseKey("ALT+CTRL+SHIFT+X")).isEqualTo("X");
        }
    }
    
    @Nested
    @DisplayName("Builder pattern")
    class BuilderPattern {
        
        @Test
        @DisplayName("Builder creates custom config")
        void builderCreatesCustomConfig() {
            KeybindConfig config = new KeybindConfig.Builder()
                .toggleTracker("T")
                .expandTracker("E")
                .build();
            
            assertThat(config.toggleTracker()).isEqualTo("T");
            assertThat(config.expandTracker()).isEqualTo("E");
            // Other values should be defaults
            assertThat(config.cyclePinned()).isEqualTo("TAB");
            assertThat(config.openQuestMenu()).isEqualTo("J");
        }
        
        @Test
        @DisplayName("Builder defaults to DEFAULT values")
        void builderDefaultsToDefaults() {
            KeybindConfig config = new KeybindConfig.Builder().build();
            
            assertThat(config).isEqualTo(KeybindConfig.DEFAULT);
        }
        
        @Test
        @DisplayName("Builder can override all values")
        void builderCanOverrideAll() {
            KeybindConfig config = new KeybindConfig.Builder()
                .toggleTracker("1")
                .expandTracker("2")
                .cyclePinned("3")
                .openQuestMenu("4")
                .trackNearest("5")
                .pinQuest("6")
                .abandonQuest("7")
                .shareQuest("8")
                .build();
            
            assertThat(config.toggleTracker()).isEqualTo("1");
            assertThat(config.expandTracker()).isEqualTo("2");
            assertThat(config.cyclePinned()).isEqualTo("3");
            assertThat(config.openQuestMenu()).isEqualTo("4");
            assertThat(config.trackNearest()).isEqualTo("5");
            assertThat(config.pinQuest()).isEqualTo("6");
            assertThat(config.abandonQuest()).isEqualTo("7");
            assertThat(config.shareQuest()).isEqualTo("8");
        }
    }
}

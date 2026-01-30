package com.argonathsystems.mods.questtrackerui.config;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ImmersiveConfig}.
 */
@DisplayName("ImmersiveConfig Tests")
class ImmersiveConfigTest {

    @Nested
    @DisplayName("Default configuration")
    class DefaultConfiguration {
        
        @Test
        @DisplayName("DEFAULT is enabled")
        void defaultIsEnabled() {
            assertThat(ImmersiveConfig.DEFAULT.enabled()).isTrue();
        }
        
        @Test
        @DisplayName("DEFAULT does not hide in combat by default")
        void defaultDoesNotHideInCombat() {
            assertThat(ImmersiveConfig.DEFAULT.hideInCombat()).isFalse();
        }
        
        @Test
        @DisplayName("DEFAULT hides in dialogue")
        void defaultHidesInDialogue() {
            assertThat(ImmersiveConfig.DEFAULT.hideInDialogue()).isTrue();
        }
        
        @Test
        @DisplayName("DEFAULT hides in cutscene")
        void defaultHidesInCutscene() {
            assertThat(ImmersiveConfig.DEFAULT.hideInCutscene()).isTrue();
        }
        
        @Test
        @DisplayName("DEFAULT has 0.8 idle opacity")
        void defaultIdleOpacity() {
            assertThat(ImmersiveConfig.DEFAULT.opacityIdle()).isEqualTo(0.8);
        }
        
        @Test
        @DisplayName("DEFAULT has 1.0 active opacity")
        void defaultActiveOpacity() {
            assertThat(ImmersiveConfig.DEFAULT.opacityActive()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("DEFAULT has 5000ms idle timeout")
        void defaultIdleTimeout() {
            assertThat(ImmersiveConfig.DEFAULT.idleTimeoutMs()).isEqualTo(5000);
        }
    }

    @Nested
    @DisplayName("DISABLED configuration")
    class DisabledConfiguration {
        
        @Test
        @DisplayName("DISABLED is not enabled")
        void disabledIsNotEnabled() {
            assertThat(ImmersiveConfig.DISABLED.enabled()).isFalse();
        }
        
        @Test
        @DisplayName("DISABLED has no hide rules")
        void disabledHasNoHideRules() {
            assertThat(ImmersiveConfig.DISABLED.hideInCombat()).isFalse();
            assertThat(ImmersiveConfig.DISABLED.hideInDialogue()).isFalse();
            assertThat(ImmersiveConfig.DISABLED.hideInCutscene()).isFalse();
        }
        
        @Test
        @DisplayName("DISABLED has full opacity")
        void disabledHasFullOpacity() {
            assertThat(ImmersiveConfig.DISABLED.opacityIdle()).isEqualTo(1.0);
            assertThat(ImmersiveConfig.DISABLED.opacityActive()).isEqualTo(1.0);
        }
    }

    @Nested
    @DisplayName("Value clamping")
    class ValueClamping {
        
        @Test
        @DisplayName("Opacity values above 1.0 are clamped")
        void opacityAboveOneClamped() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 
                1.5, // Above 1.0
                2.0, // Above 1.0
                5000
            );
            
            assertThat(config.opacityIdle()).isEqualTo(1.0);
            assertThat(config.opacityActive()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("Opacity values below 0.0 are clamped")
        void opacityBelowZeroClamped() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 
                -0.5, // Below 0.0
                -1.0, // Below 0.0
                5000
            );
            
            assertThat(config.opacityIdle()).isEqualTo(0.0);
            assertThat(config.opacityActive()).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Negative idle timeout is clamped to 0")
        void negativeTimeoutClamped() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 
                0.8, 1.0, 
                -1000 // Negative
            );
            
            assertThat(config.idleTimeoutMs()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("shouldHide()")
    class ShouldHide {
        
        @Test
        @DisplayName("Returns false when disabled")
        void returnsFalseWhenDisabled() {
            ImmersiveConfig config = new ImmersiveConfig(
                false, true, true, true, 0.8, 1.0, 5000
            );
            
            assertThat(config.shouldHide(true, true, true)).isFalse();
        }
        
        @Test
        @DisplayName("Returns true when in combat and hideInCombat is true")
        void returnsTrueWhenInCombatAndConfigured() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, true, false, false, 0.8, 1.0, 5000
            );
            
            assertThat(config.shouldHide(true, false, false)).isTrue();
        }
        
        @Test
        @DisplayName("Returns false when in combat but hideInCombat is false")
        void returnsFalseWhenInCombatButNotConfigured() {
            ImmersiveConfig config = ImmersiveConfig.DEFAULT; // hideInCombat = false
            
            assertThat(config.shouldHide(true, false, false)).isFalse();
        }
        
        @Test
        @DisplayName("Returns true when in dialogue and hideInDialogue is true")
        void returnsTrueWhenInDialogueAndConfigured() {
            ImmersiveConfig config = ImmersiveConfig.DEFAULT; // hideInDialogue = true
            
            assertThat(config.shouldHide(false, true, false)).isTrue();
        }
        
        @Test
        @DisplayName("Returns true when in cutscene and hideInCutscene is true")
        void returnsTrueWhenInCutsceneAndConfigured() {
            ImmersiveConfig config = ImmersiveConfig.DEFAULT; // hideInCutscene = true
            
            assertThat(config.shouldHide(false, false, true)).isTrue();
        }
        
        @Test
        @DisplayName("Returns true when any configured context is active")
        void returnsTrueWhenAnyContextActive() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, true, true, true, 0.8, 1.0, 5000
            );
            
            // Only combat
            assertThat(config.shouldHide(true, false, false)).isTrue();
            // Only dialogue
            assertThat(config.shouldHide(false, true, false)).isTrue();
            // Only cutscene
            assertThat(config.shouldHide(false, false, true)).isTrue();
            // All contexts
            assertThat(config.shouldHide(true, true, true)).isTrue();
        }
        
        @Test
        @DisplayName("Returns false when no context is active")
        void returnsFalseWhenNoContextActive() {
            ImmersiveConfig config = ImmersiveConfig.DEFAULT;
            
            assertThat(config.shouldHide(false, false, false)).isFalse();
        }
    }

    @Nested
    @DisplayName("getOpacity()")
    class GetOpacity {
        
        @Test
        @DisplayName("Returns idle opacity when idle")
        void returnsIdleOpacityWhenIdle() {
            ImmersiveConfig config = ImmersiveConfig.DEFAULT;
            
            assertThat(config.getOpacity(true)).isEqualTo(0.8);
        }
        
        @Test
        @DisplayName("Returns active opacity when not idle")
        void returnsActiveOpacityWhenNotIdle() {
            ImmersiveConfig config = ImmersiveConfig.DEFAULT;
            
            assertThat(config.getOpacity(false)).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("Custom opacity values are returned correctly")
        void customOpacityValuesReturned() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 
                0.5, // idle
                0.9, // active
                5000
            );
            
            assertThat(config.getOpacity(true)).isEqualTo(0.5);
            assertThat(config.getOpacity(false)).isEqualTo(0.9);
        }
    }
}

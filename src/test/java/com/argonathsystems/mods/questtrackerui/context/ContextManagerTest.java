package com.argonathsystems.mods.questtrackerui.context;

import com.argonathsystems.mods.questtrackerui.config.ImmersiveConfig;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ContextManager}.
 */
@DisplayName("ContextManager Tests")
class ContextManagerTest {

    private ContextManager manager;

    @BeforeEach
    void setUp() {
        manager = new ContextManager(ImmersiveConfig.DEFAULT);
    }

    @Nested
    @DisplayName("Initial state")
    class InitialState {
        
        @Test
        @DisplayName("Starts with all contexts false")
        void startsWithAllContextsFalse() {
            assertThat(manager.isInCombat()).isFalse();
            assertThat(manager.isInDialogue()).isFalse();
            assertThat(manager.isInCutscene()).isFalse();
            assertThat(manager.isManuallyHidden()).isFalse();
        }
        
        @Test
        @DisplayName("Starts not idle")
        void startsNotIdle() {
            assertThat(manager.isIdle()).isFalse();
        }
        
        @Test
        @DisplayName("Initially visible")
        void initiallyVisible() {
            assertThat(manager.shouldBeVisible()).isTrue();
        }
        
        @Test
        @DisplayName("Has default config")
        void hasDefaultConfig() {
            assertThat(manager.getConfig()).isEqualTo(ImmersiveConfig.DEFAULT);
        }
    }

    @Nested
    @DisplayName("Combat state")
    class CombatState {
        
        @Test
        @DisplayName("setCombatState() updates state")
        void setCombatStateUpdates() {
            manager.setCombatState(true);
            
            assertThat(manager.isInCombat()).isTrue();
        }
        
        @Test
        @DisplayName("Combat hides tracker when configured")
        void combatHidesWhenConfigured() {
            // Create config that hides in combat
            ImmersiveConfig config = new ImmersiveConfig(
                true, true, false, false, 0.8, 1.0, 5000
            );
            manager.setConfig(config);
            
            manager.setCombatState(true);
            
            assertThat(manager.shouldBeVisible()).isFalse();
        }
        
        @Test
        @DisplayName("Combat does not hide when not configured (DEFAULT)")
        void combatDoesNotHideWhenNotConfigured() {
            // DEFAULT has hideInCombat = false
            manager.setCombatState(true);
            
            assertThat(manager.shouldBeVisible()).isTrue();
        }
    }

    @Nested
    @DisplayName("Dialogue state")
    class DialogueState {
        
        @Test
        @DisplayName("setDialogueState() updates state")
        void setDialogueStateUpdates() {
            manager.setDialogueState(true);
            
            assertThat(manager.isInDialogue()).isTrue();
        }
        
        @Test
        @DisplayName("Dialogue hides tracker (DEFAULT config)")
        void dialogueHidesTracker() {
            // DEFAULT has hideInDialogue = true
            manager.setDialogueState(true);
            
            assertThat(manager.shouldBeVisible()).isFalse();
        }
    }

    @Nested
    @DisplayName("Cutscene state")
    class CutsceneState {
        
        @Test
        @DisplayName("setCutsceneState() updates state")
        void setCutsceneStateUpdates() {
            manager.setCutsceneState(true);
            
            assertThat(manager.isInCutscene()).isTrue();
        }
        
        @Test
        @DisplayName("Cutscene hides tracker (DEFAULT config)")
        void cutsceneHidesTracker() {
            // DEFAULT has hideInCutscene = true
            manager.setCutsceneState(true);
            
            assertThat(manager.shouldBeVisible()).isFalse();
        }
    }

    @Nested
    @DisplayName("Manual hide")
    class ManualHide {
        
        @Test
        @DisplayName("setManuallyHidden() updates state")
        void setManuallyHiddenUpdates() {
            manager.setManuallyHidden(true);
            
            assertThat(manager.isManuallyHidden()).isTrue();
        }
        
        @Test
        @DisplayName("Manually hidden overrides all other states")
        void manuallyHiddenOverrides() {
            manager.setManuallyHidden(true);
            
            assertThat(manager.shouldBeVisible()).isFalse();
        }
        
        @Test
        @DisplayName("Unhiding shows tracker again")
        void unhidingShowsTracker() {
            manager.setManuallyHidden(true);
            manager.setManuallyHidden(false);
            
            assertThat(manager.shouldBeVisible()).isTrue();
        }
    }

    @Nested
    @DisplayName("Visibility listeners")
    class VisibilityListeners {
        
        @Test
        @DisplayName("Listener receives visibility changes")
        void listenerReceivesChanges() {
            AtomicBoolean lastState = new AtomicBoolean(true);
            manager.addVisibilityListener(lastState::set);
            
            // Trigger hide (dialogue is configured to hide)
            manager.setDialogueState(true);
            
            assertThat(lastState.get()).isFalse();
        }
        
        @Test
        @DisplayName("Listener receives visibility restored")
        void listenerReceivesRestored() {
            AtomicBoolean lastState = new AtomicBoolean(true);
            manager.addVisibilityListener(lastState::set);
            
            manager.setDialogueState(true);
            assertThat(lastState.get()).isFalse();
            
            manager.setDialogueState(false);
            assertThat(lastState.get()).isTrue();
        }
        
        @Test
        @DisplayName("Removed listener not called")
        void removedListenerNotCalled() {
            AtomicInteger callCount = new AtomicInteger(0);
            var listener = (java.util.function.Consumer<Boolean>) b -> callCount.incrementAndGet();
            
            manager.addVisibilityListener(listener);
            manager.removeVisibilityListener(listener);
            
            manager.setDialogueState(true);
            
            assertThat(callCount.get()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Opacity")
    class Opacity {
        
        @Test
        @DisplayName("Active opacity returned when not idle")
        void activeOpacityWhenNotIdle() {
            assertThat(manager.getCurrentOpacity()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("Idle opacity returned when idle")
        void idleOpacityWhenIdle() {
            // Use config with very short timeout for testing
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 0.8, 1.0, 1 // 1ms timeout
            );
            manager.setConfig(config);
            
            // Wait for idle timeout
            try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }
            
            manager.checkIdleState();
            
            assertThat(manager.isIdle()).isTrue();
            assertThat(manager.getCurrentOpacity()).isEqualTo(0.8);
        }
        
        @Test
        @DisplayName("markActive() resets idle state")
        void markActiveResetsIdle() {
            // Use config with very short timeout for testing
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 0.8, 1.0, 1 // 1ms timeout
            );
            manager.setConfig(config);
            
            try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }
            manager.checkIdleState();
            assertThat(manager.isIdle()).isTrue();
            
            manager.markActive();
            
            assertThat(manager.isIdle()).isFalse();
            assertThat(manager.getCurrentOpacity()).isEqualTo(1.0);
        }
    }

    @Nested
    @DisplayName("Opacity listeners")
    class OpacityListeners {
        
        @Test
        @DisplayName("Listener receives opacity changes")
        void listenerReceivesOpacityChanges() {
            AtomicReference<Double> lastOpacity = new AtomicReference<>(1.0);
            manager.addOpacityListener(lastOpacity::set);
            
            // Use very short timeout
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 0.5, 1.0, 1
            );
            manager.setConfig(config);
            
            try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }
            manager.checkIdleState();
            
            assertThat(lastOpacity.get()).isEqualTo(0.5);
        }
    }

    @Nested
    @DisplayName("Context change listeners")
    class ContextChangeListeners {
        
        @Test
        @DisplayName("Listener called on any context change")
        void listenerCalledOnContextChange() {
            AtomicInteger callCount = new AtomicInteger(0);
            manager.addContextChangeListener(callCount::incrementAndGet);
            
            manager.setCombatState(true);
            manager.setDialogueState(true);
            manager.setCutsceneState(true);
            
            assertThat(callCount.get()).isEqualTo(3);
        }
        
        @Test
        @DisplayName("Listener not called on duplicate state")
        void listenerNotCalledOnDuplicate() {
            AtomicInteger callCount = new AtomicInteger(0);
            manager.addContextChangeListener(callCount::incrementAndGet);
            
            manager.setCombatState(true);
            manager.setCombatState(true); // Duplicate
            manager.setCombatState(true); // Duplicate
            
            assertThat(callCount.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Configuration updates")
    class ConfigurationUpdates {
        
        @Test
        @DisplayName("setConfig() updates configuration")
        void setConfigUpdates() {
            ImmersiveConfig custom = ImmersiveConfig.DISABLED;
            
            manager.setConfig(custom);
            
            assertThat(manager.getConfig()).isEqualTo(custom);
        }
        
        @Test
        @DisplayName("Config change triggers visibility update")
        void configChangeTriggers() {
            AtomicBoolean lastVisible = new AtomicBoolean(true);
            manager.addVisibilityListener(lastVisible::set);
            
            // Set in dialogue (should hide with DEFAULT)
            manager.setDialogueState(true);
            assertThat(lastVisible.get()).isFalse();
            
            // Switch to DISABLED config (should show)
            manager.setConfig(ImmersiveConfig.DISABLED);
            
            assertThat(lastVisible.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("Idle timeout")
    class IdleTimeout {
        
        @Test
        @DisplayName("Disabled when idleTimeoutMs is 0")
        void disabledWhenTimeoutZero() {
            ImmersiveConfig config = new ImmersiveConfig(
                true, false, false, false, 0.8, 1.0, 0 // 0 = disabled
            );
            manager.setConfig(config);
            
            try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }
            manager.checkIdleState();
            
            // Should not become idle when disabled
            assertThat(manager.isIdle()).isFalse();
        }
        
        @Test
        @DisplayName("Disabled when immersive is disabled")
        void disabledWhenImmersiveDisabled() {
            manager.setConfig(ImmersiveConfig.DISABLED);
            
            try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }
            manager.checkIdleState();
            
            assertThat(manager.isIdle()).isFalse();
        }
    }
}

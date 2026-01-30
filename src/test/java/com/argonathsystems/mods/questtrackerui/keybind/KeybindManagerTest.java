package com.argonathsystems.mods.questtrackerui.keybind;

import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link KeybindManager}.
 */
@DisplayName("KeybindManager Tests")
class KeybindManagerTest {

    private KeybindManager manager;

    @BeforeEach
    void setUp() {
        manager = new KeybindManager(KeybindConfig.DEFAULT);
    }

    @Nested
    @DisplayName("Construction")
    class Construction {
        
        @Test
        @DisplayName("Default constructor creates enabled manager")
        void defaultConstructorCreatesEnabled() {
            KeybindManager m = new KeybindManager();
            
            assertThat(m.isEnabled()).isTrue();
            assertThat(m.getConfig()).isEqualTo(KeybindConfig.DEFAULT);
        }
        
        @Test
        @DisplayName("Constructor with config stores configuration")
        void constructorWithConfigStoresConfig() {
            KeybindConfig custom = new KeybindConfig.Builder()
                .toggleTracker("X")
                .build();
            
            KeybindManager m = new KeybindManager(custom);
            
            assertThat(m.getConfig()).isEqualTo(custom);
        }
        
        @Test
        @DisplayName("Null config throws NullPointerException")
        void nullConfigThrows() {
            assertThatThrownBy(() -> new KeybindManager(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Handler registration")
    class HandlerRegistration {
        
        @Test
        @DisplayName("registerHandler() adds handler")
        void registerHandlerAdds() {
            Runnable handler = () -> {};
            
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, handler);
            
            assertThat(manager.hasHandler(KeybindAction.TOGGLE_TRACKER)).isTrue();
        }
        
        @Test
        @DisplayName("unregisterHandler() removes handler")
        void unregisterHandlerRemoves() {
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> {});
            
            manager.unregisterHandler(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(manager.hasHandler(KeybindAction.TOGGLE_TRACKER)).isFalse();
        }
        
        @Test
        @DisplayName("hasHandler() returns false for unregistered action")
        void hasHandlerReturnsFalseForUnregistered() {
            assertThat(manager.hasHandler(KeybindAction.TOGGLE_TRACKER)).isFalse();
        }
        
        @Test
        @DisplayName("Null action throws NullPointerException")
        void nullActionThrows() {
            assertThatThrownBy(() -> manager.registerHandler(null, () -> {}))
                .isInstanceOf(NullPointerException.class);
        }
        
        @Test
        @DisplayName("Null handler throws NullPointerException")
        void nullHandlerThrows() {
            assertThatThrownBy(() -> manager.registerHandler(KeybindAction.TOGGLE_TRACKER, null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Action triggering")
    class ActionTriggering {
        
        @Test
        @DisplayName("triggerAction() executes handler")
        void triggerActionExecutesHandler() {
            AtomicBoolean called = new AtomicBoolean(false);
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> called.set(true));
            
            boolean result = manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(result).isTrue();
            assertThat(called.get()).isTrue();
        }
        
        @Test
        @DisplayName("triggerAction() returns false for unregistered action")
        void triggerActionReturnsFalseForUnregistered() {
            boolean result = manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(result).isFalse();
        }
        
        @Test
        @DisplayName("triggerAction() does nothing when disabled")
        void triggerActionDoesNothingWhenDisabled() {
            AtomicBoolean called = new AtomicBoolean(false);
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> called.set(true));
            manager.setEnabled(false);
            
            boolean result = manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(result).isFalse();
            assertThat(called.get()).isFalse();
        }
    }

    @Nested
    @DisplayName("Menu context")
    class MenuContext {
        
        @Test
        @DisplayName("Menu-context-only actions fail outside menu")
        void menuContextActionsFailOutsideMenu() {
            AtomicBoolean called = new AtomicBoolean(false);
            manager.registerHandler(KeybindAction.PIN_QUEST, () -> called.set(true));
            manager.setInMenuContext(false);
            
            boolean result = manager.triggerAction(KeybindAction.PIN_QUEST);
            
            assertThat(result).isFalse();
            assertThat(called.get()).isFalse();
        }
        
        @Test
        @DisplayName("Menu-context-only actions succeed in menu")
        void menuContextActionsSucceedInMenu() {
            AtomicBoolean called = new AtomicBoolean(false);
            manager.registerHandler(KeybindAction.PIN_QUEST, () -> called.set(true));
            manager.setInMenuContext(true);
            
            boolean result = manager.triggerAction(KeybindAction.PIN_QUEST);
            
            assertThat(result).isTrue();
            assertThat(called.get()).isTrue();
        }
        
        @Test
        @DisplayName("Non-menu actions work regardless of context")
        void nonMenuActionsWorkRegardlessOfContext() {
            AtomicBoolean called = new AtomicBoolean(false);
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> called.set(true));
            manager.setInMenuContext(false);
            
            boolean result = manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(result).isTrue();
            assertThat(called.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("Global listener")
    class GlobalListener {
        
        @Test
        @DisplayName("Global listener receives action before handler")
        void globalListenerReceivesAction() {
            AtomicReference<KeybindAction> received = new AtomicReference<>();
            manager.setGlobalListener(received::set);
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> {});
            
            manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(received.get()).isEqualTo(KeybindAction.TOGGLE_TRACKER);
        }
        
        @Test
        @DisplayName("Global listener not called when disabled")
        void globalListenerNotCalledWhenDisabled() {
            AtomicReference<KeybindAction> received = new AtomicReference<>();
            manager.setGlobalListener(received::set);
            manager.registerHandler(KeybindAction.TOGGLE_TRACKER, () -> {});
            manager.setEnabled(false);
            
            manager.triggerAction(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(received.get()).isNull();
        }
    }

    @Nested
    @DisplayName("Enable/disable")
    class EnableDisable {
        
        @Test
        @DisplayName("Manager starts enabled")
        void managerStartsEnabled() {
            assertThat(manager.isEnabled()).isTrue();
        }
        
        @Test
        @DisplayName("setEnabled() changes state")
        void setEnabledChangesState() {
            manager.setEnabled(false);
            assertThat(manager.isEnabled()).isFalse();
            
            manager.setEnabled(true);
            assertThat(manager.isEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("Configuration")
    class Configuration {
        
        @Test
        @DisplayName("setConfig() updates configuration")
        void setConfigUpdatesConfiguration() {
            KeybindConfig custom = new KeybindConfig.Builder()
                .toggleTracker("Z")
                .build();
            
            manager.setConfig(custom);
            
            assertThat(manager.getConfig()).isEqualTo(custom);
        }
        
        @Test
        @DisplayName("getKeybindFor() returns configured keybind")
        void getKeybindForReturnsConfigured() {
            String keybind = manager.getKeybindFor(KeybindAction.TOGGLE_TRACKER);
            
            assertThat(keybind).isEqualTo("K");
        }
        
        @Test
        @DisplayName("Null config in setter throws")
        void nullConfigInSetterThrows() {
            assertThatThrownBy(() -> manager.setConfig(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Keybind descriptor")
    class KeybindDescriptorTests {
        
        @Test
        @DisplayName("getDescriptor() returns valid descriptor")
        void getDescriptorReturnsValid() {
            var descriptor = manager.getDescriptor(KeybindAction.EXPAND_TRACKER);
            
            assertThat(descriptor.action()).isEqualTo(KeybindAction.EXPAND_TRACKER);
            assertThat(descriptor.keybindString()).isEqualTo("SHIFT+K");
            assertThat(descriptor.baseKey()).isEqualTo("K");
            assertThat(descriptor.shiftRequired()).isTrue();
            assertThat(descriptor.ctrlRequired()).isFalse();
            assertThat(descriptor.altRequired()).isFalse();
        }
        
        @Test
        @DisplayName("Descriptor for menu-only action has menuContextOnly true")
        void descriptorForMenuOnlyHasFlag() {
            var descriptor = manager.getDescriptor(KeybindAction.PIN_QUEST);
            
            assertThat(descriptor.menuContextOnly()).isTrue();
        }
    }
}

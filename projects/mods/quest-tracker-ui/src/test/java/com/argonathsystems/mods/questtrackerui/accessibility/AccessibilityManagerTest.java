package com.argonathsystems.mods.questtrackerui.accessibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for AccessibilityManager.
 */
@DisplayName("AccessibilityManager")
class AccessibilityManagerTest {
    
    private AccessibilityManager manager;
    
    @BeforeEach
    void setUp() {
        manager = AccessibilityManager.getInstance();
        manager.resetToDefaults();
    }
    
    @Nested
    @DisplayName("singleton")
    class Singleton {
        
        @Test
        @DisplayName("getInstance returns same instance")
        void returnsSameInstance() {
            assertThat(AccessibilityManager.getInstance())
                .isSameAs(AccessibilityManager.getInstance());
        }
    }
    
    @Nested
    @DisplayName("setSettings")
    class SetSettings {
        
        @Test
        @DisplayName("updates settings")
        void updatesSettings() {
            AccessibilitySettings newSettings = AccessibilitySettings.builder()
                .highContrastMode(true)
                .build();
            
            manager.setSettings(newSettings);
            
            assertThat(manager.getSettings().isHighContrastMode()).isTrue();
        }
        
        @Test
        @DisplayName("null sets defaults")
        void nullSetsDefaults() {
            manager.setHighContrastMode(true);
            manager.setSettings(null);
            
            assertThat(manager.getSettings().isHighContrastMode()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("individual setters")
    class IndividualSetters {
        
        @Test
        @DisplayName("setHighContrastMode")
        void setHighContrastMode() {
            manager.setHighContrastMode(true);
            assertThat(manager.getSettings().isHighContrastMode()).isTrue();
            
            manager.setHighContrastMode(false);
            assertThat(manager.getSettings().isHighContrastMode()).isFalse();
        }
        
        @Test
        @DisplayName("toggleHighContrastMode")
        void toggleHighContrastMode() {
            assertThat(manager.getSettings().isHighContrastMode()).isFalse();
            
            manager.toggleHighContrastMode();
            assertThat(manager.getSettings().isHighContrastMode()).isTrue();
            
            manager.toggleHighContrastMode();
            assertThat(manager.getSettings().isHighContrastMode()).isFalse();
        }
        
        @Test
        @DisplayName("setColorblindMode")
        void setColorblindMode() {
            manager.setColorblindMode(ColorblindMode.DEUTERANOPIA);
            assertThat(manager.getSettings().colorblindMode()).isEqualTo(ColorblindMode.DEUTERANOPIA);
        }
        
        @Test
        @DisplayName("setColorblindMode null sets NONE")
        void setColorblindModeNull() {
            manager.setColorblindMode(ColorblindMode.DEUTERANOPIA);
            manager.setColorblindMode(null);
            assertThat(manager.getSettings().colorblindMode()).isEqualTo(ColorblindMode.NONE);
        }
        
        @Test
        @DisplayName("cycleColorblindMode cycles through all modes")
        void cycleColorblindMode() {
            for (ColorblindMode mode : ColorblindMode.values()) {
                assertThat(manager.getSettings().colorblindMode()).isEqualTo(mode);
                manager.cycleColorblindMode();
            }
            // Should wrap back to NONE
            assertThat(manager.getSettings().colorblindMode()).isEqualTo(ColorblindMode.NONE);
        }
        
        @Test
        @DisplayName("setScreenReaderSupport")
        void setScreenReaderSupport() {
            manager.setScreenReaderSupport(true);
            assertThat(manager.getSettings().isScreenReaderSupport()).isTrue();
        }
        
        @Test
        @DisplayName("setTextScale")
        void setTextScale() {
            manager.setTextScale(1.5);
            assertThat(manager.getSettings().textScale()).isEqualTo(1.5);
        }
        
        @Test
        @DisplayName("setTextScale clamps value")
        void setTextScaleClamps() {
            manager.setTextScale(5.0);
            assertThat(manager.getSettings().textScale()).isEqualTo(2.0);
        }
        
        @Test
        @DisplayName("increaseTextScale")
        void increaseTextScale() {
            manager.increaseTextScale();
            assertThat(manager.getSettings().textScale()).isCloseTo(1.1, within(0.001));
        }
        
        @Test
        @DisplayName("decreaseTextScale")
        void decreaseTextScale() {
            manager.decreaseTextScale();
            assertThat(manager.getSettings().textScale()).isCloseTo(0.9, within(0.001));
        }
        
        @Test
        @DisplayName("resetTextScale")
        void resetTextScale() {
            manager.setTextScale(1.5);
            manager.resetTextScale();
            assertThat(manager.getSettings().textScale()).isEqualTo(1.0);
        }
    }
    
    @Nested
    @DisplayName("change listeners")
    class ChangeListeners {
        
        @Test
        @DisplayName("listener receives updates")
        void listenerReceivesUpdates() {
            AtomicReference<AccessibilitySettings> received = new AtomicReference<>();
            manager.addChangeListener(received::set);
            
            manager.setHighContrastMode(true);
            
            assertThat(received.get()).isNotNull();
            assertThat(received.get().isHighContrastMode()).isTrue();
        }
        
        @Test
        @DisplayName("listener not called for same value")
        void listenerNotCalledForSameValue() {
            AtomicInteger callCount = new AtomicInteger();
            manager.addChangeListener(s -> callCount.incrementAndGet());
            
            manager.setHighContrastMode(false); // Already false
            assertThat(callCount.get()).isZero();
            
            manager.setHighContrastMode(true);
            assertThat(callCount.get()).isEqualTo(1);
            
            manager.setHighContrastMode(true); // Same value
            assertThat(callCount.get()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("removed listener not called")
        void removedListenerNotCalled() {
            AtomicInteger callCount = new AtomicInteger();
            var listener = (java.util.function.Consumer<AccessibilitySettings>) s -> callCount.incrementAndGet();
            
            manager.addChangeListener(listener);
            manager.setHighContrastMode(true);
            assertThat(callCount.get()).isEqualTo(1);
            
            manager.removeChangeListener(listener);
            manager.setHighContrastMode(false);
            assertThat(callCount.get()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("null listener ignored")
        void nullListenerIgnored() {
            assertThatCode(() -> manager.addChangeListener(null))
                .doesNotThrowAnyException();
        }
    }
    
    @Nested
    @DisplayName("configuration loading")
    class ConfigurationLoading {
        
        @Test
        @DisplayName("loadFromConfig applies settings")
        void loadFromConfigApplies() {
            Map<String, Object> config = Map.of(
                "high_contrast_mode", true,
                "colorblind_mode", "deuteranopia",
                "screen_reader_support", true,
                "text_scale", 1.5
            );
            
            manager.loadFromConfig(config);
            
            AccessibilitySettings settings = manager.getSettings();
            assertThat(settings.isHighContrastMode()).isTrue();
            assertThat(settings.colorblindMode()).isEqualTo(ColorblindMode.DEUTERANOPIA);
            assertThat(settings.isScreenReaderSupport()).isTrue();
            assertThat(settings.textScale()).isEqualTo(1.5);
        }
        
        @Test
        @DisplayName("loadFromConfig handles null")
        void loadFromConfigHandlesNull() {
            assertThatCode(() -> manager.loadFromConfig(null))
                .doesNotThrowAnyException();
        }
        
        @Test
        @DisplayName("loadFromConfig handles partial config")
        void loadFromConfigHandlesPartial() {
            Map<String, Object> config = Map.of("high_contrast_mode", true);
            
            manager.loadFromConfig(config);
            
            assertThat(manager.getSettings().isHighContrastMode()).isTrue();
            assertThat(manager.getSettings().colorblindMode()).isEqualTo(ColorblindMode.NONE);
        }
    }
    
    @Nested
    @DisplayName("configuration export")
    class ConfigurationExport {
        
        @Test
        @DisplayName("toConfigMap exports all settings")
        void toConfigMapExports() {
            manager.setHighContrastMode(true);
            manager.setColorblindMode(ColorblindMode.PROTANOPIA);
            manager.setScreenReaderSupport(true);
            manager.setTextScale(1.5);
            
            Map<String, Object> config = manager.toConfigMap();
            
            assertThat(config.get("high_contrast_mode")).isEqualTo(true);
            assertThat(config.get("colorblind_mode")).isEqualTo("protanopia");
            assertThat(config.get("screen_reader_support")).isEqualTo(true);
            assertThat(config.get("text_scale")).isEqualTo(1.5);
        }
    }
    
    @Nested
    @DisplayName("getColorblindModeOptions")
    class GetColorblindModeOptions {
        
        @Test
        @DisplayName("returns all modes")
        void returnsAllModes() {
            List<AccessibilityManager.ColorblindModeInfo> options = manager.getColorblindModeOptions();
            
            assertThat(options).hasSize(ColorblindMode.values().length);
        }
        
        @Test
        @DisplayName("marks current mode as selected")
        void marksCurrentAsSelected() {
            manager.setColorblindMode(ColorblindMode.DEUTERANOPIA);
            
            List<AccessibilityManager.ColorblindModeInfo> options = manager.getColorblindModeOptions();
            
            assertThat(options)
                .filteredOn(AccessibilityManager.ColorblindModeInfo::selected)
                .hasSize(1)
                .first()
                .extracting(AccessibilityManager.ColorblindModeInfo::mode)
                .isEqualTo(ColorblindMode.DEUTERANOPIA);
        }
    }
    
    @Nested
    @DisplayName("resetToDefaults")
    class ResetToDefaults {
        
        @Test
        @DisplayName("resets all settings")
        void resetsAllSettings() {
            manager.setHighContrastMode(true);
            manager.setColorblindMode(ColorblindMode.DEUTERANOPIA);
            manager.setTextScale(1.5);
            
            manager.resetToDefaults();
            
            assertThat(manager.getSettings()).isEqualTo(AccessibilitySettings.defaults());
        }
    }
}

package com.argonathsystems.mods.questtrackerui.accessibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for AccessibilitySettings.
 */
@DisplayName("AccessibilitySettings")
class AccessibilitySettingsTest {
    
    @Nested
    @DisplayName("defaults")
    class Defaults {
        
        @Test
        @DisplayName("default settings have all features disabled")
        void defaultsHaveAllDisabled() {
            AccessibilitySettings settings = AccessibilitySettings.defaults();
            
            assertThat(settings.isHighContrastMode()).isFalse();
            assertThat(settings.colorblindMode()).isEqualTo(ColorblindMode.NONE);
            assertThat(settings.isScreenReaderSupport()).isFalse();
            assertThat(settings.textScale()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("defaults have no active features")
        void defaultsNoActiveFeatures() {
            assertThat(AccessibilitySettings.defaults().hasActiveFeatures()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("builder")
    class Builder {
        
        @Test
        @DisplayName("builds with all settings")
        void buildsWithAllSettings() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .highContrastMode(true)
                .colorblindMode(ColorblindMode.DEUTERANOPIA)
                .screenReaderSupport(true)
                .textScale(1.5)
                .build();
            
            assertThat(settings.isHighContrastMode()).isTrue();
            assertThat(settings.colorblindMode()).isEqualTo(ColorblindMode.DEUTERANOPIA);
            assertThat(settings.isScreenReaderSupport()).isTrue();
            assertThat(settings.textScale()).isEqualTo(1.5);
        }
        
        @Test
        @DisplayName("clamps text scale")
        void clampsTextScale() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .textScale(5.0)
                .build();
            
            assertThat(settings.textScale()).isEqualTo(2.0);
        }
        
        @Test
        @DisplayName("null colorblind mode defaults to NONE")
        void nullColorblindDefaults() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .colorblindMode(null)
                .build();
            
            assertThat(settings.colorblindMode()).isEqualTo(ColorblindMode.NONE);
        }
    }
    
    @Nested
    @DisplayName("hasActiveFeatures")
    class HasActiveFeatures {
        
        @Test
        @DisplayName("true when high contrast enabled")
        void trueWithHighContrast() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .highContrastMode(true)
                .build();
            assertThat(settings.hasActiveFeatures()).isTrue();
        }
        
        @Test
        @DisplayName("true when colorblind mode set")
        void trueWithColorblind() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .colorblindMode(ColorblindMode.PROTANOPIA)
                .build();
            assertThat(settings.hasActiveFeatures()).isTrue();
        }
        
        @Test
        @DisplayName("true when screen reader enabled")
        void trueWithScreenReader() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .screenReaderSupport(true)
                .build();
            assertThat(settings.hasActiveFeatures()).isTrue();
        }
        
        @Test
        @DisplayName("true when text scale changed")
        void trueWithTextScale() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .textScale(1.5)
                .build();
            assertThat(settings.hasActiveFeatures()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("adjustColor")
    class AdjustColor {
        
        @Test
        @DisplayName("applies both colorblind and high contrast")
        void appliesBoth() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .highContrastMode(true)
                .colorblindMode(ColorblindMode.DEUTERANOPIA)
                .build();
            
            int original = 0xFFFF0000;
            int adjusted = settings.adjustColor(original);
            
            // Should be different from both original and single-mode transform
            assertThat(adjusted).isNotEqualTo(original);
        }
        
        @Test
        @DisplayName("no change with defaults")
        void noChangeWithDefaults() {
            int original = 0xFFABCDEF;
            int adjusted = AccessibilitySettings.defaults().adjustColor(original);
            assertThat(adjusted).isEqualTo(original);
        }
    }
    
    @Nested
    @DisplayName("scaling methods")
    class ScalingMethods {
        
        @Test
        @DisplayName("scaleFontSize applies text scale")
        void scaleFontSize() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .textScale(1.5)
                .build();
            
            assertThat(settings.scaleFontSize(12)).isEqualTo(18);
        }
        
        @Test
        @DisplayName("scaleSpacing applies text scale")
        void scaleSpacing() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .textScale(1.5)
                .build();
            
            assertThat(settings.scaleSpacing(10)).isEqualTo(15);
        }
        
        @Test
        @DisplayName("scaleDimension applies reduced scale")
        void scaleDimension() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .textScale(2.0)
                .build();
            
            // Dimension scale is 1 + (2-1)*0.5 = 1.5
            assertThat(settings.scaleDimension(100)).isEqualTo(150);
        }
    }
    
    @Nested
    @DisplayName("immutable copy methods")
    class ImmutableCopyMethods {
        
        @Test
        @DisplayName("withHighContrastToggled creates new instance")
        void toggleHighContrast() {
            AccessibilitySettings original = AccessibilitySettings.defaults();
            AccessibilitySettings toggled = original.withHighContrastToggled();
            
            assertThat(original.isHighContrastMode()).isFalse();
            assertThat(toggled.isHighContrastMode()).isTrue();
        }
        
        @Test
        @DisplayName("withColorblindMode creates new instance")
        void withColorblindMode() {
            AccessibilitySettings original = AccessibilitySettings.defaults();
            AccessibilitySettings modified = original.withColorblindMode(ColorblindMode.TRITANOPIA);
            
            assertThat(original.colorblindMode()).isEqualTo(ColorblindMode.NONE);
            assertThat(modified.colorblindMode()).isEqualTo(ColorblindMode.TRITANOPIA);
        }
        
        @Test
        @DisplayName("withIncreasedTextScale increases by 0.1")
        void increaseTextScale() {
            AccessibilitySettings original = AccessibilitySettings.defaults();
            AccessibilitySettings increased = original.withIncreasedTextScale();
            
            assertThat(original.textScale()).isEqualTo(1.0);
            assertThat(increased.textScale()).isCloseTo(1.1, within(0.001));
        }
        
        @Test
        @DisplayName("withDecreasedTextScale decreases by 0.1")
        void decreaseTextScale() {
            AccessibilitySettings original = AccessibilitySettings.defaults();
            AccessibilitySettings decreased = original.withDecreasedTextScale();
            
            assertThat(original.textScale()).isEqualTo(1.0);
            assertThat(decreased.textScale()).isCloseTo(0.9, within(0.001));
        }
    }
    
    @Nested
    @DisplayName("toBuilder")
    class ToBuilder {
        
        @Test
        @DisplayName("preserves all settings")
        void preservesSettings() {
            AccessibilitySettings original = AccessibilitySettings.builder()
                .highContrastMode(true)
                .colorblindMode(ColorblindMode.DEUTERANOPIA)
                .screenReaderSupport(true)
                .textScale(1.5)
                .build();
            
            AccessibilitySettings rebuilt = original.toBuilder().build();
            
            assertThat(rebuilt).isEqualTo(original);
        }
        
        @Test
        @DisplayName("allows modification")
        void allowsModification() {
            AccessibilitySettings original = AccessibilitySettings.builder()
                .highContrastMode(true)
                .build();
            
            AccessibilitySettings modified = original.toBuilder()
                .colorblindMode(ColorblindMode.PROTANOPIA)
                .build();
            
            assertThat(modified.isHighContrastMode()).isTrue();
            assertThat(modified.colorblindMode()).isEqualTo(ColorblindMode.PROTANOPIA);
        }
    }
    
    @Nested
    @DisplayName("equals and hashCode")
    class EqualsAndHashCode {
        
        @Test
        @DisplayName("equal instances are equal")
        void equalInstancesEqual() {
            AccessibilitySettings a = AccessibilitySettings.builder()
                .highContrastMode(true)
                .textScale(1.5)
                .build();
            AccessibilitySettings b = AccessibilitySettings.builder()
                .highContrastMode(true)
                .textScale(1.5)
                .build();
            
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }
        
        @Test
        @DisplayName("different instances are not equal")
        void differentInstancesNotEqual() {
            AccessibilitySettings a = AccessibilitySettings.builder()
                .highContrastMode(true)
                .build();
            AccessibilitySettings b = AccessibilitySettings.builder()
                .highContrastMode(false)
                .build();
            
            assertThat(a).isNotEqualTo(b);
        }
    }
    
    @Nested
    @DisplayName("toString")
    class ToString {
        
        @Test
        @DisplayName("contains all settings")
        void containsAllSettings() {
            AccessibilitySettings settings = AccessibilitySettings.builder()
                .highContrastMode(true)
                .colorblindMode(ColorblindMode.DEUTERANOPIA)
                .screenReaderSupport(false)
                .textScale(1.5)
                .build();
            
            String str = settings.toString();
            assertThat(str).contains("highContrast=true");
            assertThat(str).contains("DEUTERANOPIA");
            assertThat(str).contains("screenReader=false");
            assertThat(str).contains("textScale=1.5");
        }
    }
}

package com.argonathsystems.mods.questtrackerui.accessibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for high contrast mode transformations.
 */
@DisplayName("HighContrastMode")
class HighContrastModeTest {
    
    @Nested
    @DisplayName("applyHighContrast")
    class ApplyHighContrast {
        
        @Test
        @DisplayName("returns original when disabled")
        void returnsOriginalWhenDisabled() {
            int original = 0xFFABCDEF;
            assertThat(HighContrastMode.applyHighContrast(original, false)).isEqualTo(original);
        }
        
        @Test
        @DisplayName("preserves alpha channel")
        void preservesAlpha() {
            int original = 0x80808080; // 50% transparent gray
            int transformed = HighContrastMode.applyHighContrast(original, true);
            
            int originalAlpha = (original >> 24) & 0xFF;
            int transformedAlpha = (transformed >> 24) & 0xFF;
            
            assertThat(transformedAlpha).isEqualTo(originalAlpha);
        }
        
        @Test
        @DisplayName("makes dark colors darker")
        void makesDarkColorsDarker() {
            int darkGray = 0xFF303030;
            int transformed = HighContrastMode.applyHighContrast(darkGray, true);
            
            int originalBrightness = ((darkGray >> 16) & 0xFF) + ((darkGray >> 8) & 0xFF) + (darkGray & 0xFF);
            int transformedBrightness = ((transformed >> 16) & 0xFF) + ((transformed >> 8) & 0xFF) + (transformed & 0xFF);
            
            assertThat(transformedBrightness).isLessThanOrEqualTo(originalBrightness);
        }
        
        @Test
        @DisplayName("makes light colors lighter")
        void makesLightColorsLighter() {
            int lightGray = 0xFFD0D0D0;
            int transformed = HighContrastMode.applyHighContrast(lightGray, true);
            
            int originalBrightness = ((lightGray >> 16) & 0xFF) + ((lightGray >> 8) & 0xFF) + (lightGray & 0xFF);
            int transformedBrightness = ((transformed >> 16) & 0xFF) + ((transformed >> 8) & 0xFF) + (transformed & 0xFF);
            
            assertThat(transformedBrightness).isGreaterThanOrEqualTo(originalBrightness);
        }
    }
    
    @Nested
    @DisplayName("calculateContrastRatio")
    class CalculateContrastRatio {
        
        @Test
        @DisplayName("black and white have maximum contrast")
        void blackWhiteMaxContrast() {
            int black = 0xFF000000;
            int white = 0xFFFFFFFF;
            
            double ratio = HighContrastMode.calculateContrastRatio(black, white);
            assertThat(ratio).isCloseTo(21.0, within(0.1));
        }
        
        @Test
        @DisplayName("identical colors have ratio of 1")
        void identicalColorsRatioOne() {
            int gray = 0xFF808080;
            double ratio = HighContrastMode.calculateContrastRatio(gray, gray);
            assertThat(ratio).isCloseTo(1.0, within(0.01));
        }
        
        @Test
        @DisplayName("order of colors doesn't matter")
        void orderDoesntMatter() {
            int dark = 0xFF202020;
            int light = 0xFFE0E0E0;
            
            double ratio1 = HighContrastMode.calculateContrastRatio(dark, light);
            double ratio2 = HighContrastMode.calculateContrastRatio(light, dark);
            
            assertThat(ratio1).isEqualTo(ratio2);
        }
    }
    
    @Nested
    @DisplayName("ensureContrast")
    class EnsureContrast {
        
        @Test
        @DisplayName("returns original if already sufficient")
        void returnsOriginalIfSufficient() {
            int white = 0xFFFFFFFF;
            int black = 0xFF000000;
            
            int result = HighContrastMode.ensureContrast(white, black, 4.5);
            double ratio = HighContrastMode.calculateContrastRatio(result, black);
            
            assertThat(ratio).isGreaterThanOrEqualTo(4.5);
        }
        
        @Test
        @DisplayName("adjusts color to meet ratio")
        void adjustsToMeetRatio() {
            int gray = 0xFF606060;
            int darkGray = 0xFF404040;
            
            int result = HighContrastMode.ensureContrast(gray, darkGray, 4.5);
            double ratio = HighContrastMode.calculateContrastRatio(result, darkGray);
            
            assertThat(ratio).isGreaterThanOrEqualTo(4.5);
        }
        
        @Test
        @DisplayName("lightens foreground on dark background")
        void lightensOnDarkBg() {
            int gray = 0xFF808080;
            int darkBg = 0xFF101010;
            
            int result = HighContrastMode.ensureContrast(gray, darkBg, 7.0);
            int resultBrightness = ((result >> 16) & 0xFF) + ((result >> 8) & 0xFF) + (result & 0xFF);
            int originalBrightness = ((gray >> 16) & 0xFF) + ((gray >> 8) & 0xFF) + (gray & 0xFF);
            
            assertThat(resultBrightness).isGreaterThanOrEqualTo(originalBrightness);
        }
        
        @Test
        @DisplayName("darkens foreground on light background")
        void darkensOnLightBg() {
            int gray = 0xFF808080;
            int lightBg = 0xFFF0F0F0;
            
            int result = HighContrastMode.ensureContrast(gray, lightBg, 7.0);
            int resultBrightness = ((result >> 16) & 0xFF) + ((result >> 8) & 0xFF) + (result & 0xFF);
            int originalBrightness = ((gray >> 16) & 0xFF) + ((gray >> 8) & 0xFF) + (gray & 0xFF);
            
            assertThat(resultBrightness).isLessThanOrEqualTo(originalBrightness);
        }
    }
    
    @Nested
    @DisplayName("getLuminance")
    class GetLuminance {
        
        @Test
        @DisplayName("black has luminance 0")
        void blackHasZeroLuminance() {
            assertThat(HighContrastMode.getLuminance(0xFF000000)).isCloseTo(0.0, within(0.001));
        }
        
        @Test
        @DisplayName("white has luminance 1")
        void whiteHasOneLuminance() {
            assertThat(HighContrastMode.getLuminance(0xFFFFFFFF)).isCloseTo(1.0, within(0.001));
        }
        
        @Test
        @DisplayName("gray has medium luminance")
        void grayHasMediumLuminance() {
            double luminance = HighContrastMode.getLuminance(0xFF808080);
            assertThat(luminance).isBetween(0.2, 0.3);
        }
    }
    
    @Nested
    @DisplayName("WCAG constants")
    class WcagConstants {
        
        @Test
        @DisplayName("AA normal is 4.5")
        void aaNormal() {
            assertThat(HighContrastMode.WCAG_AA_NORMAL).isEqualTo(4.5);
        }
        
        @Test
        @DisplayName("AA large is 3.0")
        void aaLarge() {
            assertThat(HighContrastMode.WCAG_AA_LARGE).isEqualTo(3.0);
        }
        
        @Test
        @DisplayName("AAA normal is 7.0")
        void aaaNormal() {
            assertThat(HighContrastMode.WCAG_AAA_NORMAL).isEqualTo(7.0);
        }
    }
}

package com.argonathsystems.mods.questtrackerui.accessibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for colorblind mode color transformations.
 */
@DisplayName("ColorblindMode")
class ColorblindModeTest {
    
    @Nested
    @DisplayName("transformColor")
    class TransformColor {
        
        @Test
        @DisplayName("NONE mode returns original color unchanged")
        void noneReturnsOriginal() {
            int original = 0xFFFF0000; // Red
            assertThat(ColorblindMode.NONE.transformColor(original)).isEqualTo(original);
        }
        
        @Test
        @DisplayName("preserves alpha channel")
        void preservesAlpha() {
            int original = 0x80FF0000; // 50% transparent red
            int transformed = ColorblindMode.DEUTERANOPIA.transformColor(original);
            
            int originalAlpha = (original >> 24) & 0xFF;
            int transformedAlpha = (transformed >> 24) & 0xFF;
            
            assertThat(transformedAlpha).isEqualTo(originalAlpha);
        }
        
        @ParameterizedTest
        @EnumSource(value = ColorblindMode.class, names = {"DEUTERANOPIA", "PROTANOPIA", "TRITANOPIA"})
        @DisplayName("transforms colors for active modes")
        void transformsColors(ColorblindMode mode) {
            int red = 0xFFFF0000;
            int green = 0xFF00FF00;
            
            int transformedRed = mode.transformColor(red);
            int transformedGreen = mode.transformColor(green);
            
            // Should be different from original (or at least not both identical)
            assertThat(transformedRed != red || transformedGreen != green).isTrue();
        }
        
        @Test
        @DisplayName("DEUTERANOPIA shifts red-green to distinguishable colors")
        void deuteranopiaShiftsRedGreen() {
            int red = 0xFFFF0000;
            int green = 0xFF00FF00;
            
            int transformedRed = ColorblindMode.DEUTERANOPIA.transformColor(red);
            int transformedGreen = ColorblindMode.DEUTERANOPIA.transformColor(green);
            
            // Colors should be distinguishable (different R or B channels)
            int redR = (transformedRed >> 16) & 0xFF;
            int greenR = (transformedGreen >> 16) & 0xFF;
            int redB = transformedRed & 0xFF;
            int greenB = transformedGreen & 0xFF;
            
            // At least one channel should differ significantly
            assertThat(Math.abs(redR - greenR) > 20 || Math.abs(redB - greenB) > 20).isTrue();
        }
        
        @Test
        @DisplayName("handles black and white correctly")
        void handlesBlackAndWhite() {
            int black = 0xFF000000;
            int white = 0xFFFFFFFF;
            
            for (ColorblindMode mode : ColorblindMode.values()) {
                int transformedBlack = mode.transformColor(black);
                int transformedWhite = mode.transformColor(white);
                
                // Black should stay very dark
                int blackLuminance = ((transformedBlack >> 16) & 0xFF) + 
                    ((transformedBlack >> 8) & 0xFF) + (transformedBlack & 0xFF);
                assertThat(blackLuminance).isLessThan(100);
                
                // White should stay very light
                int whiteLuminance = ((transformedWhite >> 16) & 0xFF) + 
                    ((transformedWhite >> 8) & 0xFF) + (transformedWhite & 0xFF);
                assertThat(whiteLuminance).isGreaterThan(600);
            }
        }
    }
    
    @Nested
    @DisplayName("fromString")
    class FromString {
        
        @ParameterizedTest
        @CsvSource({
            "none, NONE",
            "NONE, NONE",
            "deuteranopia, DEUTERANOPIA",
            "DEUTERANOPIA, DEUTERANOPIA",
            "protanopia, PROTANOPIA",
            "tritanopia, TRITANOPIA"
        })
        @DisplayName("parses valid values case-insensitively")
        void parsesValidValues(String input, ColorblindMode expected) {
            assertThat(ColorblindMode.fromString(input)).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "''",
            "invalid",
            "foo"
        })
        @DisplayName("returns NONE for invalid values")
        void returnsNoneForInvalid(String input) {
            assertThat(ColorblindMode.fromString(input)).isEqualTo(ColorblindMode.NONE);
        }
        
        @Test
        @DisplayName("returns NONE for null")
        void returnsNoneForNull() {
            assertThat(ColorblindMode.fromString(null)).isEqualTo(ColorblindMode.NONE);
        }
    }
    
    @Nested
    @DisplayName("properties")
    class Properties {
        
        @Test
        @DisplayName("all modes have display names")
        void allHaveDisplayNames() {
            for (ColorblindMode mode : ColorblindMode.values()) {
                assertThat(mode.displayName()).isNotBlank();
            }
        }
        
        @Test
        @DisplayName("all modes have descriptions")
        void allHaveDescriptions() {
            for (ColorblindMode mode : ColorblindMode.values()) {
                assertThat(mode.description()).isNotBlank();
            }
        }
    }
}

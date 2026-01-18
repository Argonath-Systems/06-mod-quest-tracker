package com.argonathsystems.mods.questtrackerui.accessibility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for text scaling utilities.
 */
@DisplayName("TextScaling")
class TextScalingTest {
    
    @Nested
    @DisplayName("applyScale")
    class ApplyScale {
        
        @Test
        @DisplayName("scale 1.0 returns original size")
        void scaleOneReturnsOriginal() {
            assertThat(TextScaling.applyScale(12, 1.0)).isEqualTo(12);
        }
        
        @Test
        @DisplayName("scale 2.0 doubles size")
        void scaleTwoDoubles() {
            assertThat(TextScaling.applyScale(12, 2.0)).isEqualTo(24);
        }
        
        @Test
        @DisplayName("scale 0.5 halves size")
        void scaleHalfHalves() {
            assertThat(TextScaling.applyScale(12, 0.5)).isEqualTo(6);
        }
        
        @Test
        @DisplayName("minimum size is 8 pixels")
        void minimumSizeIs8() {
            assertThat(TextScaling.applyScale(4, 0.5)).isEqualTo(8);
            assertThat(TextScaling.applyScale(10, 0.5)).isEqualTo(8);
        }
        
        @Test
        @DisplayName("rounds to nearest integer")
        void roundsToNearest() {
            assertThat(TextScaling.applyScale(11, 1.5)).isEqualTo(17); // 16.5 rounds to 17
        }
    }
    
    @Nested
    @DisplayName("applySpacingScale")
    class ApplySpacingScale {
        
        @Test
        @DisplayName("scales spacing proportionally")
        void scalesProportionally() {
            assertThat(TextScaling.applySpacingScale(10, 1.5)).isEqualTo(15);
        }
        
        @Test
        @DisplayName("minimum spacing is 2 pixels")
        void minimumSpacingIs2() {
            assertThat(TextScaling.applySpacingScale(2, 0.5)).isEqualTo(2);
        }
    }
    
    @Nested
    @DisplayName("applyDimensionScale")
    class ApplyDimensionScale {
        
        @Test
        @DisplayName("scales at reduced rate")
        void scalesAtReducedRate() {
            int scaled = TextScaling.applyDimensionScale(100, 2.0);
            // At scale 2.0, dimension scale is 1 + (2-1)*0.5 = 1.5
            assertThat(scaled).isEqualTo(150);
        }
        
        @Test
        @DisplayName("no change at scale 1.0")
        void noChangeAtScaleOne() {
            assertThat(TextScaling.applyDimensionScale(100, 1.0)).isEqualTo(100);
        }
    }
    
    @Nested
    @DisplayName("clampScale")
    class ClampScale {
        
        @ParameterizedTest
        @CsvSource({
            "0.5, 0.5",
            "1.0, 1.0",
            "2.0, 2.0",
            "0.3, 0.5",
            "2.5, 2.0",
            "-1.0, 0.5"
        })
        @DisplayName("clamps to valid range")
        void clampsToValidRange(double input, double expected) {
            assertThat(TextScaling.clampScale(input)).isEqualTo(expected);
        }
    }
    
    @Nested
    @DisplayName("roundToIncrement")
    class RoundToIncrement {
        
        @ParameterizedTest
        @CsvSource({
            "1.0, 1.0",
            "1.05, 1.0",
            "1.15, 1.1",
            "1.25, 1.3",
            "0.95, 0.9"
        })
        @DisplayName("rounds to 0.1 increments")
        void roundsToIncrements(double input, double expected) {
            assertThat(TextScaling.roundToIncrement(input)).isCloseTo(expected, within(0.001));
        }
    }
    
    @Nested
    @DisplayName("increaseScale and decreaseScale")
    class IncreaseDecreaseScale {
        
        @Test
        @DisplayName("increaseScale adds 0.1")
        void increaseAdds01() {
            assertThat(TextScaling.increaseScale(1.0)).isCloseTo(1.1, within(0.001));
        }
        
        @Test
        @DisplayName("decreaseScale subtracts 0.1")
        void decreaseSubtracts01() {
            assertThat(TextScaling.decreaseScale(1.0)).isCloseTo(0.9, within(0.001));
        }
        
        @Test
        @DisplayName("increaseScale clamps at max")
        void increaseClamps() {
            assertThat(TextScaling.increaseScale(2.0)).isEqualTo(2.0);
        }
        
        @Test
        @DisplayName("decreaseScale clamps at min")
        void decreaseClamps() {
            assertThat(TextScaling.decreaseScale(0.5)).isEqualTo(0.5);
        }
    }
    
    @Nested
    @DisplayName("formatAsPercentage")
    class FormatAsPercentage {
        
        @ParameterizedTest
        @CsvSource({
            "1.0, '100%'",
            "0.5, '50%'",
            "1.5, '150%'",
            "2.0, '200%'"
        })
        @DisplayName("formats as percentage string")
        void formatsCorrectly(double scale, String expected) {
            assertThat(TextScaling.formatAsPercentage(scale)).isEqualTo(expected);
        }
    }
    
    @Nested
    @DisplayName("boundary checks")
    class BoundaryChecks {
        
        @Test
        @DisplayName("isAtMinimum true at 0.5")
        void isAtMinimumTrue() {
            assertThat(TextScaling.isAtMinimum(0.5)).isTrue();
            assertThat(TextScaling.isAtMinimum(0.3)).isTrue();
        }
        
        @Test
        @DisplayName("isAtMinimum false above 0.5")
        void isAtMinimumFalse() {
            assertThat(TextScaling.isAtMinimum(0.6)).isFalse();
        }
        
        @Test
        @DisplayName("isAtMaximum true at 2.0")
        void isAtMaximumTrue() {
            assertThat(TextScaling.isAtMaximum(2.0)).isTrue();
            assertThat(TextScaling.isAtMaximum(2.5)).isTrue();
        }
        
        @Test
        @DisplayName("isAtMaximum false below 2.0")
        void isAtMaximumFalse() {
            assertThat(TextScaling.isAtMaximum(1.9)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("constants")
    class Constants {
        
        @Test
        @DisplayName("MIN_SCALE is 0.5")
        void minScale() {
            assertThat(TextScaling.MIN_SCALE).isEqualTo(0.5);
        }
        
        @Test
        @DisplayName("MAX_SCALE is 2.0")
        void maxScale() {
            assertThat(TextScaling.MAX_SCALE).isEqualTo(2.0);
        }
        
        @Test
        @DisplayName("DEFAULT_SCALE is 1.0")
        void defaultScale() {
            assertThat(TextScaling.DEFAULT_SCALE).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("SCALE_INCREMENT is 0.1")
        void scaleIncrement() {
            assertThat(TextScaling.SCALE_INCREMENT).isEqualTo(0.1);
        }
    }
}

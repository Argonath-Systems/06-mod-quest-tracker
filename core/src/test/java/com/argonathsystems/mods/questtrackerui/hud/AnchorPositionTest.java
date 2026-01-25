package com.argonathsystems.mods.questtrackerui.hud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AnchorPosition enum.
 */
class AnchorPositionTest {
    
    @Test
    @DisplayName("Should calculate top-left position")
    void shouldCalculateTopLeftPosition() {
        AnchorPosition anchor = AnchorPosition.TOP_LEFT;
        
        int x = anchor.calculateX(1920, 280, 20);
        int y = anchor.calculateY(1080, 400, 50);
        
        assertThat(x).isEqualTo(20);
        assertThat(y).isEqualTo(50);
    }
    
    @Test
    @DisplayName("Should calculate top-right position")
    void shouldCalculateTopRightPosition() {
        AnchorPosition anchor = AnchorPosition.TOP_RIGHT;
        
        int x = anchor.calculateX(1920, 280, -20);
        int y = anchor.calculateY(1080, 400, 50);
        
        // 1920 - 280 - 20 = 1620
        assertThat(x).isEqualTo(1620);
        assertThat(y).isEqualTo(50);
    }
    
    @Test
    @DisplayName("Should calculate center position")
    void shouldCalculateCenterPosition() {
        AnchorPosition anchor = AnchorPosition.CENTER;
        
        int x = anchor.calculateX(1920, 280, 0);
        int y = anchor.calculateY(1080, 400, 0);
        
        // (1920 / 2) - (280 / 2) = 820
        assertThat(x).isEqualTo(820);
        // (1080 / 2) - (400 / 2) = 340
        assertThat(y).isEqualTo(340);
    }
    
    @Test
    @DisplayName("Should calculate bottom-right position")
    void shouldCalculateBottomRightPosition() {
        AnchorPosition anchor = AnchorPosition.BOTTOM_RIGHT;
        
        int x = anchor.calculateX(1920, 280, -20);
        int y = anchor.calculateY(1080, 400, -50);
        
        // 1920 - 280 - 20 = 1620
        assertThat(x).isEqualTo(1620);
        // 1080 - 400 - 50 = 630
        assertThat(y).isEqualTo(630);
    }
    
    @Test
    @DisplayName("Should parse anchor from string")
    void shouldParseAnchorFromString() {
        assertThat(AnchorPosition.parse("TOP_RIGHT")).isEqualTo(AnchorPosition.TOP_RIGHT);
        assertThat(AnchorPosition.parse("top_left")).isEqualTo(AnchorPosition.TOP_LEFT);
        assertThat(AnchorPosition.parse("top-center")).isEqualTo(AnchorPosition.TOP_CENTER);
        assertThat(AnchorPosition.parse("BOTTOM LEFT")).isEqualTo(AnchorPosition.BOTTOM_LEFT);
    }
    
    @Test
    @DisplayName("Should default to TOP_RIGHT for invalid input")
    void shouldDefaultToTopRightForInvalidInput() {
        assertThat(AnchorPosition.parse(null)).isEqualTo(AnchorPosition.TOP_RIGHT);
        assertThat(AnchorPosition.parse("invalid")).isEqualTo(AnchorPosition.TOP_RIGHT);
        assertThat(AnchorPosition.parse("")).isEqualTo(AnchorPosition.TOP_RIGHT);
    }
}

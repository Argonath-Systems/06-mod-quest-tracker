package com.argonathsystems.mods.questtrackerui.theme;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ColorScheme record.
 */
class ColorSchemeTest {
    
    @Test
    @DisplayName("Should parse hex colors correctly")
    void shouldParseHexColorsCorrectly() {
        assertThat(ColorScheme.parseHex("#ff0000")).isEqualTo(0xFFFF0000);
        assertThat(ColorScheme.parseHex("00ff00")).isEqualTo(0xFF00FF00);
        assertThat(ColorScheme.parseHex("#0000ff")).isEqualTo(0xFF0000FF);
        assertThat(ColorScheme.parseHex("#1a1a2e")).isEqualTo(0xFF1A1A2E);
    }
    
    @Test
    @DisplayName("Should parse hex colors with alpha")
    void shouldParseHexColorsWithAlpha() {
        assertThat(ColorScheme.parseHex("80ff0000")).isEqualTo(0x80FF0000);
    }
    
    @Test
    @DisplayName("Should calculate background ARGB with opacity")
    void shouldCalculateBackgroundArgbWithOpacity() {
        ColorScheme scheme = new ColorScheme(
            "#ffffff", 0.5, "#000000",
            "#ffffff", "#aaaaaa", "#666666",
            "#ffd700", "#87ceeb", "#ff6b6b", "#90ee90", "#ffa500", "#9370db",
            "#4caf50", "#333333", "#8bc34a",
            "#ffffff", "#ffeb3b", "#f44336"
        );
        
        int argb = scheme.backgroundArgb();
        int alpha = (argb >> 24) & 0xFF;
        
        assertThat(alpha).isEqualTo(127); // 0.5 * 255 = 127
        assertThat(argb & 0x00FFFFFF).isEqualTo(0x00FFFFFF);
    }
    
    @Test
    @DisplayName("Should provide default dark fantasy scheme")
    void shouldProvideDefaultDarkFantasyScheme() {
        ColorScheme scheme = ColorScheme.DARK_FANTASY;
        
        assertThat(scheme.background()).isEqualTo("#1a1a2e");
        assertThat(scheme.backgroundOpacity()).isEqualTo(0.85);
        assertThat(scheme.questMain()).isEqualTo("#ffd700");
    }
    
    @Test
    @DisplayName("Should provide light minimal scheme")
    void shouldProvideLightMinimalScheme() {
        ColorScheme scheme = ColorScheme.LIGHT_MINIMAL;
        
        assertThat(scheme.background()).isEqualTo("#f5f5f5");
        assertThat(scheme.backgroundOpacity()).isEqualTo(0.95);
    }
}

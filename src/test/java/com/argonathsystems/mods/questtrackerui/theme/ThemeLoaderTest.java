package com.argonathsystems.mods.questtrackerui.theme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ThemeLoader Tests")
class ThemeLoaderTest {
    
    private ThemeLoader loader;
    
    @BeforeEach
    void setUp() {
        loader = new ThemeLoader();
    }
    
    @Nested
    @DisplayName("YAML parsing")
    class YamlParsing {
        
        @Test
        @DisplayName("loads minimal theme from YAML")
        void loadsMinimalTheme() throws Exception {
            String yaml = """
                theme:
                  id: test-theme
                  name: "Test Theme"
                  author: "Tester"
                """;
            
            Theme theme = loader.loadFromString(yaml, "test.yml");
            
            assertThat(theme.id()).isEqualTo("test-theme");
            assertThat(theme.name()).isEqualTo("Test Theme");
            assertThat(theme.author()).isEqualTo("Tester");
            assertThat(theme.isPremium()).isFalse();
        }
        
        @Test
        @DisplayName("loads theme with custom colors")
        void loadsThemeWithColors() throws Exception {
            String yaml = """
                theme:
                  id: custom
                  name: "Custom"
                  colors:
                    background: "#ff0000"
                    background_opacity: 0.5
                    text_primary: "#00ff00"
                """;
            
            Theme theme = loader.loadFromString(yaml, "test.yml");
            
            assertThat(theme.colors().background()).isEqualTo("#ff0000");
            assertThat(theme.colors().backgroundOpacity()).isEqualTo(0.5);
            assertThat(theme.colors().textPrimary()).isEqualTo("#00ff00");
        }
        
        @Test
        @DisplayName("uses defaults for missing color values")
        void usesDefaultsForMissingColors() throws Exception {
            String yaml = """
                theme:
                  id: partial
                  colors:
                    background: "#123456"
                """;
            
            Theme theme = loader.loadFromString(yaml, "test.yml");
            
            assertThat(theme.colors().background()).isEqualTo("#123456");
            // Other colors should use defaults
            assertThat(theme.colors().textPrimary()).isEqualTo("#ffffff");
        }
        
        @Test
        @DisplayName("loads theme with border configuration")
        void loadsThemeWithBorders() throws Exception {
            String yaml = """
                theme:
                  id: bordered
                  borders:
                    style: fancy
                    radius: 8
                    width: 2
                """;
            
            Theme theme = loader.loadFromString(yaml, "test.yml");
            
            assertThat(theme.borders().style()).isEqualTo(BorderConfig.BorderStyle.FANCY);
            assertThat(theme.borders().radius()).isEqualTo(8);
            assertThat(theme.borders().width()).isEqualTo(2);
        }
        
        @Test
        @DisplayName("handles premium flag")
        void handlesPremiumFlag() throws Exception {
            String yaml = """
                theme:
                  id: premium-theme
                  premium: true
                """;
            
            Theme theme = loader.loadFromString(yaml, "test.yml");
            
            assertThat(theme.isPremium()).isTrue();
        }
        
        @Test
        @DisplayName("throws exception for empty file")
        void throwsForEmptyFile() {
            assertThatThrownBy(() -> loader.loadFromString("", "empty.yml"))
                .isInstanceOf(ThemeLoader.ThemeParseException.class)
                .hasMessageContaining("Empty theme file");
        }
    }
    
    @Nested
    @DisplayName("Color parsing")
    class ColorParsing {
        
        @Test
        @DisplayName("parseHex() handles # prefix")
        void parseHexHandlesPrefix() {
            int color = ColorScheme.parseHex("#ff0000");
            
            assertThat(color).isEqualTo(0xFFFF0000);
        }
        
        @Test
        @DisplayName("parseHex() handles no prefix")
        void parseHexHandlesNoPrefix() {
            int color = ColorScheme.parseHex("00ff00");
            
            assertThat(color).isEqualTo(0xFF00FF00);
        }
        
        @Test
        @DisplayName("parseHex() handles 8-digit ARGB")
        void parseHexHandles8Digit() {
            int color = ColorScheme.parseHex("80ff0000");
            
            assertThat(color).isEqualTo(0x80FF0000);
        }
    }
}
